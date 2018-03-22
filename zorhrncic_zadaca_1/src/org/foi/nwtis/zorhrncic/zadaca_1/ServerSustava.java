/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaBin;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author grupa_1
 */
public class ServerSustava {

    private boolean pause_state = false;
    private boolean stop_request = false;
    private Evidencija evidencija;
    private int port;
    private int maksCekanje;
    private String datotekaEvidencije;
    private int maksRadnihDretvi;
    private int brojRadnihDretvi;
    private int redniBrojDrete;
    private boolean krajRada;
    private boolean upis = false;

    public synchronized boolean beginStoppingServer()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        if (stop_request == false) {
            stop_request = true;
            upis = false;
            notify();
            return true;
        } else {
            upis = false;
            notify();
            return false;
        }

    }

    public synchronized boolean isStopRequest()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        boolean ret = stop_request;
        upis = false;
        notify();
        return ret;

    }

    public synchronized boolean isPause()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        boolean ret = pause_state;
        upis = false;
        notify();
        return ret;

    }

    public synchronized boolean setServerPause()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        if (pause_state == true) {
            upis = false;
            notify();
            return false;
        } else {
            pause_state = true;
            upis = false;
            notify();
            return true;
        }

    }

    public synchronized boolean setServerStart()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        if (pause_state == true) {
            pause_state = false;
            upis = false;
            notify();
            return true;
        } else {
            upis = false;
            notify();
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
            System.out.println("Ne postoji datoteka konfiguracije!!");
            return;
        } catch (NeispravnaKonfiguracija ex) {
            System.out.println("Greška u datoteci konfiguracije!!");
            return;
        }
    }

    private void pokreniPosluzitelj(Konfiguracija konfig) {
        port = Integer.parseInt(konfig.dajPostavku("port"));
        maksCekanje = Integer.parseInt(konfig.dajPostavku("maks.broj.zahtjeva.cekanje"));
        datotekaEvidencije = konfig.dajPostavku("datoteka.evidencije.rada");
        maksRadnihDretvi = Integer.parseInt(konfig.dajPostavku("maks.broj.radnih.dretvi"));
        krajRada = false;
        brojRadnihDretvi = 0;
        redniBrojDrete = 0;
//TODO Provjeri i kao postoju učitaj evidenciju rada
        postaviEvidencijuRada(datotekaEvidencije);

        Gson g = new Gson();
        System.out.println(g.toJson(evidencija));

        IOT iot = new IOT();
        SerijalizatorEvidencije se = new SerijalizatorEvidencije("zorhrncic - serijalizator", konfig, evidencija);
        se.start();
        try {
            ServerSocket serverSocket = new ServerSocket(port, maksCekanje);
            while (!krajRada) {
                Socket socket = serverSocket.accept();
                System.out.println("Korisnik se spojio");
                evidencija.dodajNoviZahtjev();//test monitor

                if (brojRadnihDretvi >= maksRadnihDretvi) {
                    evidencija.dodajOdbijenZahtjevJerNemaDretvi();
                    vratiOdgovorDaNemaSlobodnihRadnihDretvi(socket);
                } else {
                    povecajBrojRadnihDretvi();
                    RadnaDretva radnaDretva = new RadnaDretva(socket, "zorhrncic - " + Integer.toBinaryString(redniBrojDrete), konfig, evidencija, this);
                    radnaDretva.start();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
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

    public void ucitajEvidenciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
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
            InputStream is = Files.newInputStream(datKonf.toPath(), StandardOpenOption.READ);
            ObjectInputStream ois = new ObjectInputStream(is);
            evidencija = (Evidencija) ois.readObject();
            ois.close();
        } catch (IOException ex) {
            throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke " + datKonf.getAbsolutePath());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KonfiguracijaBin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void postaviEvidencijuRada(String datotekaEvidencije) {

        try {
            FileInputStream in = new FileInputStream(datotekaEvidencije);
            ObjectInputStream s = new ObjectInputStream(in);
            evidencija = (Evidencija) s.readObject();
            s.close();
        } catch (Exception e) {
            System.out.println("Problem kod učitavanja podataka evidencije: " + e.getMessage());
        } finally {
            if (evidencija == null) {
                evidencija = new Evidencija();
                System.out.println("Problem kod učitavanja podataka evidencije: ");
            }
        }

    }

    private synchronized void povecajBrojRadnihDretvi()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        if (this.redniBrojDrete >= 63) {
            redniBrojDrete = 0;
        } else {
            redniBrojDrete++;
        }
        this.brojRadnihDretvi++;
        System.out.println("Povećan broj radnih dretvi: " + brojRadnihDretvi);
        upis = false;
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized void smanjiBrojRadnihDretvi()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        this.brojRadnihDretvi--;
        evidencija.dodajUspjesnoObavljenZahtjev();
        System.out.println("Smanjen broj radnih dretvi: " + brojRadnihDretvi);
        upis = false;
        System.out.println("Posao obavljen");
        notify();
    }

}
