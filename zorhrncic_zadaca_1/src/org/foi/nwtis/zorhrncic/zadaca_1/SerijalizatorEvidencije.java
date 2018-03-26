/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;

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
    private boolean upis = false;

        public synchronized boolean setKrajRada(boolean b) {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        if (krajRada != b) {
            krajRada = b;
            upis = false;
            notify();
            return true;
        } else {
            upis = false;
            notify();
            return false;
        }
    }

    public SerijalizatorEvidencije() {
    }

    public synchronized boolean isKrajRada() {      
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        boolean ret = krajRada;
        upis = false;
        notify();
        return ret;
    }

    
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
        while (!isKrajRada()) {
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
