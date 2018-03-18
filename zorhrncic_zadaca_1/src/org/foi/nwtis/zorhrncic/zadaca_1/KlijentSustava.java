/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;

/**
 *
 * @author grupa_1
 */
public class KlijentSustava extends KorisnikSustava {

    Konfiguracija konfig;
    private String fromServer;

    public KlijentSustava(Konfiguracija konfig, Properties upisaniAurumenti) {
        super();
        this.konfig = konfig;
        this.upisaniArgumenti = upisaniAurumenti;
    }

    public void preuzmiKontrolu() {
        try {
            Socket socket = new Socket(upisaniArgumenti.getProperty("adresa"), Integer.parseInt(upisaniArgumenti.getProperty("port")));
            try (
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));) {

                for (String command : getCommand()) {
                       out.println(command);
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
                    }*/
                }

            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(KlijentSustava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String[] getCommand() {
        if (upisaniArgumenti.containsKey("spavanje") && upisaniArgumenti.containsKey("datotekaIotClient")) {
            String[] retValue = {"IOT " + upisaniArgumenti.getProperty("datotekaIotClient"), "CEKAJ " + upisaniArgumenti.getProperty("spavanje")};
            return retValue;
        } else if (upisaniArgumenti.containsKey("datotekaIotClient")) {
            String[] retValue = {"IOT " + upisaniArgumenti.getProperty("datotekaIotClient")};
            return retValue;
        } else {
            return null;
        }
    }
}