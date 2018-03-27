/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaJSON;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author grupa_1
 */
public class KlijentSustava extends KorisnikSustava {

    private Gson gson = new Gson();
    private Socket socket;

    public KlijentSustava(Properties upisaniAurumenti) {
        super();
        this.uA = upisaniAurumenti;
    }

    /**
     * Kreira socket i povezuje se na server, te otvara ulazni stream za odgovor
     * na server.
     */
    public void preuzmiKontrolu() {
        try {
            socket = new Socket(uA.getProperty("adresa"), Integer.parseInt(uA.getProperty("port")));
            handle(socket);
        } catch (IOException ex) {
            Logger.getLogger(KlijentSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * Na temelju upisanih argumentata, koji su prosljedjeni u konstruktoru,
     * kreira komandu koja se salje na server.
     *
     * @return Lista komandi koje su prepoznate na temelju ulaznih argumenata.
     */
    private String[] getCommand() {
        if (uA.containsKey("spavanje")) {
            String[] retValue = {"CEKAJ " + uA.getProperty("spavanje") + ";"};
            return retValue;
        } else if (uA.containsKey("datotekaIotClient")) {
            //           System.out.println("JSON:  "+getJsonFile(upisaniArgumenti.getProperty("datotekaIotClient")));
            String[] retValue = {"IOT " + getJsonFile(uA.getProperty("datotekaIotClient")) + ";"};
            return retValue;
        } else {
            return null;
        }
    }

    /**
     * Cita json zapis iz datoteke.
     *
     * @param datoteka naziv datoteke koja sadrzi json zapis
     * @return string koji sadrzi json zapis
     */
    private String getJsonFile(String datoteka) {
        try {
            if (datoteka == null || datoteka.length() == 0) {
                throw new NemaKonfiguracije("naziv datoteke nedostaje");
            }
            File datKonf = new File(datoteka);
            if (!datKonf.exists()) {
                throw new NemaKonfiguracije("Datoteka: " + datoteka + " ne postoji!");
            } else if (datKonf.isDirectory()) {
                throw new NeispravnaKonfiguracija(datoteka + " nije datoteka već direktorij");
            }
            return readFromFile(datoteka);
        } catch (NemaKonfiguracije ex) {
            System.out.println("Nema datoteke sa IOT podatcima");
        } catch (NeispravnaKonfiguracija ex) {
            System.out.println("Nema datoteke sa IOT podatcima");
        }
        return null;
    }

    /**
     * Nakon kreiranja socketa, salje komandu serveru i ceka odgovor. Po
     * primitku odgovora, salje na daljnju obradu.
     *
     * @param socket socket pomocu kojeg se spaja na server
     */
    private void handle(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();) {
            if (getCommand() != null) {
                for (String command : getCommand()) {
                    outputStream.write(command.getBytes());
                    outputStream.flush();
                }
            } else {
                System.out.println("ERROR 02; komanda nije ispravna");
            }
            socket.shutdownOutput();
            int znak;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (true) {
                znak = inputStream.read();
                if (znak == -1) {
                    break;
                }
                baos.write(znak);
            }
            System.out.println(new String(baos.toByteArray()));
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * cita zapis iz datoteke
     * @param datoteka nazv datoteke
     * @return zapis iz datoteke
     */
    private String readFromFile(String datoteka) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(datoteka));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KonfiguracijaJSON.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KlijentSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
