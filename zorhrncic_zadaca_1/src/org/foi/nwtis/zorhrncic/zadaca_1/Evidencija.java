/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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

    private transient boolean upis = false;
    private Charset charset;

    public boolean isUpis() {
        return upis;
    }

    public void setUpis(boolean upis) {
        this.upis = upis;
    }

    public synchronized void dodajUspjesnoObavljenZahtjev()
           {
        while (isUpis()) {
            try {
                System.out.println("Netko upisuje");
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Evidencija.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    public synchronized byte[] toStringser(Charset charset) throws InterruptedException, IOException {
        // upis = false;
        while (isUpis()) {
            System.out.println("Netko upisuje");
            wait();
        }
        setUpis(true);
        byte[] b = toStringserPrivate(charset);
        setUpis(false);
        System.out.println("Posao obavljen");
        notify();
        return b;
    }

    private byte[] toStringserPrivate(Charset charset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileWriter out = new FileWriter("tmp");
        out.write(String.format("%-40s%-5d\n", "Ukupan broj zahtjeva:", ukupanbrojZahtjeva));
        out.write(String.format("%-40s%-5d\n", "Broj uspješnih zahtjeva:", brojUspjesnihZahtjeva));
        out.write(String.format("%-40s%-5d\n", "Broj prekunutih zahtjeva:", brojPrkinutihZahtjeva));
        out.write(String.format("%-40s%-5d\n", "Broj neispravnih zahtjeva:", brojNeispravnihZahtjeva));
        out.write(String.format("%-40s%-5d\n", "Broj nedozvoljenih zahtjeva:", brojNedozvoljenihZahtjeva));
        out.write(String.format("%-40s%-5d\n", "Ukupno vrijeme rada radih dretvi:", ukupnoVrijemeRadaRadnihDretvi));
        out.write(String.format("%-40s%-5d\n", "Broj obavljanja serijalizacije:", brojObavljanjaSerijalizacije));
        out.close();
        File inputFile = new File("tmp");
        BufferedReader bf = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(inputFile), charset));
        String linija = null;
        while ((linija = bf.readLine()) != null) {
            String s = linija + "\n";
            baos.write(s.getBytes(charset));
        }
        bf.close();

        //charset = StandardCharsets.ISO_8859_1;
        /* 
        String s = String.format("%-40s%-5d\n", "Ukupan broj zahtjeva:", ukupanbrojZahtjeva);
        baos.write(charset.encode(s).array());
        s = String.format("%-40s%-5d\n", "Broj uspješnih zahtjeva:", brojUspjesnihZahtjeva);
        baos.write(charset.encode(s).array());
        s = String.format("%-40s%-5d\n", "Broj prekunutih zahtjeva:", brojPrkinutihZahtjeva);
        baos.write(charset.encode(s).array());
        s = String.format("%-40s%-5d\n", "Broj neispravnih zahtjeva:", brojNeispravnihZahtjeva);
        baos.write(charset.encode(s).array());
        s = String.format("%-40s%-5d\n", "Broj nedozvoljenih zahtjeva:", brojNedozvoljenihZahtjeva);
        baos.write(charset.encode(s).array());
        s = String.format("%-40s%-5d\n", "Ukupno vrijeme rada radih dretvi:", ukupnoVrijemeRadaRadnihDretvi);
        baos.write(charset.encode(s).array());
        s = String.format("%-40s%-5d\n", "Broj obavljanja serijalizacije:", brojObavljanjaSerijalizacije);
        baos.write(charset.encode(s).array());*/
        return baos.toByteArray();
    }

}
