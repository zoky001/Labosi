/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;

/**
 *
 * @author grupa_1
 */
public class SerijalizatorEvidencije extends Thread {

    String nazivDretve;
    Konfiguracija konfig;
    boolean krajRada = false;
    String nazivDatotekeZaSerijalizaciju;
    private Evidencija evidencija;

    public SerijalizatorEvidencije(String nazivDretve, Konfiguracija konfig, Evidencija e) {
        super(nazivDretve);

        this.nazivDretve = nazivDretve;
        this.konfig = konfig;
        this.evidencija = e;

    }

    @Override
    public void interrupt() {
        super.interrupt();

    }

    @Override
    public void run() {
        nazivDatotekeZaSerijalizaciju = konfig.dajPostavku("datoteka.evidencije.rada");
        int intervalZaSerijalizuaciju = Integer.parseInt(konfig.dajPostavku("interval.za.serijalizaciju"));

        while (!krajRada) {
            long pocetak = System.currentTimeMillis();

            System.out.println("Dretva: " + nazivDretve + "Početak: " + pocetak);
            try {
                evidencija.obaviSerijalizaciju(nazivDatotekeZaSerijalizaciju);  
            } catch (InterruptedException ex) {
                Logger.getLogger(SerijalizatorEvidencije.class.getName()).log(Level.SEVERE, null, ex);
            }

            long kraj = System.currentTimeMillis();

            long razlika = kraj - pocetak;

            try {
                Thread.sleep(intervalZaSerijalizuaciju * 1000 - razlika);
            } catch (InterruptedException ex) {
                Logger.getLogger(SerijalizatorEvidencije.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public synchronized void start() {
        super.start();
    }

}
