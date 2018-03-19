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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;

/**
 *
 * @author grupa_1
 */
public class AdministratorSustava extends KorisnikSustava {

    Konfiguracija konfig;
    private String fromServer;

    public AdministratorSustava(Konfiguracija konfig, Properties upisaniAurumenti) {
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

                if (getCommand().size() > 0) {
                    for (String command : getCommand()) {
                        out.println(command);
                    }
                } else {
                    System.out.println("ERROR 02; komanda nije ispravna");
                }

              

                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server je napisao: " + fromServer);
                    if (fromServer.equals("Bye.")) {
                        break;
                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private List<String> getCommand() {
        List<String> commands = new ArrayList();

        if (upisaniArgumenti.containsKey("pauza") && "1".equals(upisaniArgumenti.getProperty("pauza")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; PAUZA;");
            System.out.println("pauza");
        } else if (upisaniArgumenti.containsKey("kreni") && "1".equals(upisaniArgumenti.getProperty("kreni")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; KRENI;");
            System.out.println("kreni");

        } else if (upisaniArgumenti.containsKey("zaustavi") && "1".equals(upisaniArgumenti.getProperty("zaustavi")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; ZAUSTAVI;");
            System.out.println("zaustavi");

        } else if (upisaniArgumenti.containsKey("stanje") && "1".equals(upisaniArgumenti.getProperty("stanje")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; STANJE;");
            System.out.println("stanje");

        } else if (upisaniArgumenti.containsKey("datotekaEvidencija") && upisaniArgumenti.getProperty("datotekaEvidencija") != null && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; EVIDENCIJA;");
            System.out.println("evidencija");

        } else if (upisaniArgumenti.containsKey("datotekaIot") && upisaniArgumenti.getProperty("datotekaIot") != null && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; IOT;");
            System.out.println("iot");

        }
        return commands;
    }

}
