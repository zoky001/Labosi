/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;

/**
 *
 * @author grupa_1
 */
public class Evidencija implements Serializable {

    private long ukupanbrojZahtjeva = 0; // broj svih tahteva na serveru
    private long brojPrkinutihZahtjeva = 0; // broj zahtjeva koji su odbijeni jer nema slobodnih radnih dretvi
    private long brojUspjesnihZahtjeva = 0;//broj uspješno izvršenih zahteva

    private long brojNeispravnihZahtjeva = 0; // pogrešna komanda
    private long brojNedozvoljenihZahtjeva = 0; //pogrešna lozinka

    private long ukupnoVrijemeRadaRadnihDretvi = 0;
    private long brojObavljanjaSerijalizacije = 0;

    private transient  boolean upis = false;

    public boolean isUpis() {
        return upis;
    }

    public void setUpis(boolean upis) {
        this.upis = upis;
    }
    
    

    public synchronized void dodajUspjesnoObavljenZahtjev()
            throws InterruptedException {
        while (isUpis()) {
            System.out.println("Netko upisuje");
            wait();
        }
        setUpis(true);

        //radi
        this.brojUspjesnihZahtjeva++;

        setUpis(false);
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized void dodajOdbijenZahtjevJerNemaDretvi()
            throws InterruptedException {
        while (isUpis()) {
            System.out.println("Netko upisuje");
            wait();
        }


 setUpis(true);
        //radi
        this.brojPrkinutihZahtjeva++;

 setUpis(false);
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized void dodajNoviZahtjev()
            throws InterruptedException {
        while (isUpis()) {
            System.out.println("Netko upisuje");
            wait();
        }

 setUpis(true);
        //radi
        this.ukupanbrojZahtjeva++;

 setUpis(false);
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized void dodajNeispravanZahtjev()
            throws InterruptedException {
       // upis = false;
        while (isUpis()) {
            System.out.println("Netko upisuje");
            wait();
        }

 setUpis(true);

        //radi
        this.brojNeispravnihZahtjeva++;

 setUpis(false);
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized void obaviSerijalizaciju(String nazivDatotekeZaSerijalizaciju)
            throws InterruptedException {
        //upis = false;
        while (isUpis()) {
            System.out.println("Netko upisuje");
            wait();
        }
 setUpis(true);
 obaviSerijizacijuPoaso(nazivDatotekeZaSerijalizaciju);
        this.brojObavljanjaSerijalizacije++;
 setUpis(false);
        System.out.println("Posao obavljen");
        notify();
    }

    private void obaviSerijizacijuPoaso(String nazivDatotekeZaSerijalizaciju) {
        ObjectOutputStream s = null;
        try {
            File datKonf = new File(nazivDatotekeZaSerijalizaciju);
            if (datKonf.exists() && datKonf.isDirectory()) {
                throw new NeispravnaKonfiguracija(nazivDatotekeZaSerijalizaciju + " nije datoteka već direktorij");
            }
            try {
                FileOutputStream out = new FileOutputStream(nazivDatotekeZaSerijalizaciju);
                s = new ObjectOutputStream(out);
                s.writeObject(this);
                // os = Files.newOutputStream(datKonf.toPath(), StandardOpenOption.CREATE);
                // Gson gsonObj = new Gson();
                //  String strJson = gsonObj.toJson(evidencija);
                //System.out.println(strJson);
                //os.write(strJson.getBytes());
                //this.postavke.storeToXML(os, "Konfiguracija NWTIS grupa 2");
            } catch (IOException ex) {
                throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke " + datKonf.getAbsolutePath());
            }
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SerijalizatorEvidencije.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // os.close();
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(SerijalizatorEvidencije.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //radi

    }

    public Evidencija() {
         setUpis(false);
    }

}
