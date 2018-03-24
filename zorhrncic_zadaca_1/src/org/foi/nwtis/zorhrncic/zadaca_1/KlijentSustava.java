/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaJSON;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author grupa_1
 */
public class KlijentSustava extends KorisnikSustava {

    private Gson gson = new Gson();

    public KlijentSustava(Properties upisaniAurumenti) {
        super();
        this.upisaniArgumenti = upisaniAurumenti;
    }

    public void preuzmiKontrolu() {
        try {
            // IOT io = new IOT();
            //io.createMOckJsonFile();

            Socket socket = new Socket(upisaniArgumenti.getProperty("adresa"), Integer.parseInt(upisaniArgumenti.getProperty("port")));
            try (
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();) {
                if (getCommand() != null) {
                    for (String command : getCommand()) {
                        outputStream.write(command.getBytes());
                        outputStream.flush();
                    }

                } else {
                    System.out.println("ERROR 02; komanda nije ispravna");
                }

                // String komanda = "KORISNIK  " + korisnik + "; LOZINKA " + lozinka + "; PAUZA;"; // korisnik iz korisnik sustava 
                // outputStream.write(komanda.getBytes());
                socket.shutdownOutput();

                int znak;
                StringBuffer buffer = new StringBuffer();
                while (true) {
                    znak = inputStream.read();
                    if (znak == -1) {
                        break;
                    }
                    buffer.append((char) znak);

                }
                System.out.println("buffer: " + buffer.toString());
            }/*try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));) {

                if (getCommand() != null) {
                    for (String command : getCommand()) {
                        out.println(command);
                    }
                    
                }else{
                    System.out.println("ERROR 02; komanda nije ispravna");
                }

                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server je napisao: " + fromServer);
                    if (fromServer.equals("Bye.")) {
                        break;
                    }

                    /* fromUser = System.
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        out.println(fromUser);
                    }*//*
                }

            } */ catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(KlijentSustava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String[] getCommand() {
        if (upisaniArgumenti.containsKey("spavanje") && upisaniArgumenti.containsKey("datotekaIotClient")) {
            String[] retValue = {"CEKAJ " + upisaniArgumenti.getProperty("spavanje") + "; " + "IOT " + getJsonFile(upisaniArgumenti.getProperty("datotekaIotClient")) + ";"};
            return retValue;
        } else if (upisaniArgumenti.containsKey("datotekaIotClient")) {
            System.out.println("JSON:  "+getJsonFile(upisaniArgumenti.getProperty("datotekaIotClient")));
            String[] retValue = {"IOT " + getJsonFile(upisaniArgumenti.getProperty("datotekaIotClient")) + ";"};
            return retValue;
        } else {
            return null;
        }
    }

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
            try {
                byte[] encoded = Files.readAllBytes(Paths.get(datoteka));
                return new String(encoded, StandardCharsets.UTF_8);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(KonfiguracijaJSON.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KlijentSustava.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NemaKonfiguracije ex) {
            System.out.println("Nema datoteke sa IOT podatcima");
        } catch (NeispravnaKonfiguracija ex) {
            System.out.println("Nema datoteke sa IOT podatcima");
        }
        return "Kraj";
    }
}
