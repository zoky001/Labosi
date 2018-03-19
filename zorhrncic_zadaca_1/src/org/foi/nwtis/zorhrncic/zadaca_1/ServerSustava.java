/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author grupa_1
 */
public class ServerSustava {

    private static boolean pause_state = false;
    private static boolean stop_request = false;

    public static boolean beginStoppingServer() {
        if (stop_request == false) {
            stop_request = true;
            return true;
        } else {
            return false;
        }
    }

    public static boolean isStopRequest() {
        return stop_request;
    }

    ;
    public static boolean isPause() {
        return pause_state;
    }

    ;
    public static boolean setServerPause() {
        if (pause_state == true) {
            return false;
        } else {
            pause_state = true;
            return true;
        }
    }

    public static boolean setServerStart() {
        if (pause_state == true) {
            pause_state = false;
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        String datotekaKonfig;
        if (args.length != 1) {
            System.out.println("Premalo ili previše argumenata");
            return;
        }
        datotekaKonfig = args[0];
        try {
            Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfig);
            ServerSustava ss = new ServerSustava();
            ss.pokreniPosluzitelj(konfig);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    private void pokreniPosluzitelj(Konfiguracija konfig) {
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        int maksCekanje = Integer.parseInt(konfig.dajPostavku("maks.broj.zahtjeva.cekanje"));
        String datotekaEvidencije = konfig.dajPostavku("datoteka.evidencije.rada");
        int maksRadnihDretvi = Integer.parseInt(konfig.dajPostavku("maks.broj.radnih.dretvi"));
        boolean krajRada = false;
        int brojRadnihDretvi = 0;
//TODO Provjeri i kao postoju učitaj evidenciju rada

        IOT iot = new IOT();
        Evidencija evidencija = new Evidencija();
      

        SerijalizatorEvidencije se = new SerijalizatorEvidencije("zorhrncic - serijalizator", konfig, evidencija);
        se.start();
        try {
            ServerSocket serverSocket = new ServerSocket(port, maksCekanje);
            while (!krajRada) {
                Socket socket = serverSocket.accept();
                System.out.println("Korisnik se spojio");

                if (brojRadnihDretvi == maksRadnihDretvi) {
                    vratiOdgovorDaNemaSlobodnihRadnihDretvi(socket);
                } else {
                    RadnaDretva radnaDretva = new RadnaDretva(socket, "zorhrncic - " + brojRadnihDretvi, konfig,evidencija);
                    brojRadnihDretvi++;
                    radnaDretva.start();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void vratiOdgovorDaNemaSlobodnihRadnihDretvi(Socket socket) {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            String inputLine, outputLine;

            out.println("ERROR 01; nema više slobodnih radnih dretvi");

        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
