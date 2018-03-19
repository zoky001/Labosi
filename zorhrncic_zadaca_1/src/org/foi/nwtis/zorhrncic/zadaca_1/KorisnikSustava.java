/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;

/**
 *
 * @author grupa_1
 */
public class KorisnikSustava {

    String korisnik;
    String lozinka;
    String adresa;
    int port;
    boolean administrator = false;
    String[] args;
    Konfiguracija konfig;
    private final String parametarKorisnik = "-k";
    private final String parametarLozinka = "-l";
    private final String parametarAdresa = "-s";
    private final String parametarPort = "-p";
    private final String parametarPauza = "--pauza";
    private final String parametarStanje = "--stanje";
    private final String parametarKreni = "--kreni";
    private final String parametarZaustavi = "--zaustavi";
    private final String parametarSpavanje = "--spavanje";
    private final String parametarEvidencija = "--evidencija";
    private final String parametarIot = "--iot";

    List<String> dozvoljeniParametriList = new ArrayList<>();
    private boolean pauza;
    private boolean stanje;
    private boolean kreni;
    private boolean zaustavi;
    private int spavanje;
    private String datotekaEvidencija;
    private String datotekaIot;
    private String datotekaIotClient;

    protected Properties upisaniArgumenti = new Properties();

    public KorisnikSustava() {
        dozvoljeniParametriList.add(parametarAdresa);
        dozvoljeniParametriList.add(parametarKorisnik);
        dozvoljeniParametriList.add(parametarLozinka);
        dozvoljeniParametriList.add(parametarPort);
        dozvoljeniParametriList.add(parametarPauza);
        dozvoljeniParametriList.add(parametarStanje);
        dozvoljeniParametriList.add(parametarKreni);
        dozvoljeniParametriList.add(parametarZaustavi);
        dozvoljeniParametriList.add(parametarSpavanje);
        dozvoljeniParametriList.add(parametarEvidencija);
        dozvoljeniParametriList.add(parametarIot);

    }

    public static void main(String[] args) {

        KorisnikSustava ks = new KorisnikSustava();
        ks.preuzmiPostavke(args);
        ks.args = args;

        if (ks.administrator) {
//TODO kreiraj objekt administrator sustava i predaj mu kontrolu
            AdministratorSustava anAdministratorSustava = new AdministratorSustava(ks.konfig,ks.upisaniArgumenti);
            anAdministratorSustava.preuzmiKontrolu();

        } else {
            //TODO kreiraj objekt korisnika sustava i predaj mu kontrolu
            KlijentSustava klijentSustava = new KlijentSustava(ks.konfig,ks.upisaniArgumenti);
            klijentSustava.preuzmiKontrolu();

        }

    }

    private void preuzmiPostavke(String[] args) {

//TODO Provjeri upisane argumente
        ucitajUlazneParametreAdmina(args);
        ucitajUlazneParametreKlijenta(args);
        
        for (Map.Entry<Object, Object> entry : upisaniArgumenti.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
                System.out.println(key + " - "+ value + "\n");
            
        }
        
        
    

        if (korisnik != null) {
            korisnik = korisnik.trim();
            if (!korisnik.isEmpty()) {
                administrator = true;
            }
        }

        if (lozinka != null) {
            lozinka = lozinka.trim();
            if (!lozinka.isEmpty()) {
                administrator = true;
            } else {
                administrator = false;
            }
        } else {
            administrator = false;
        }
//TODO Provjeri da li je korisnik koa admin u postavkama

    }

    private void ucitajUlazneParametreAdmina(String[] args) {
        int index = 0;
        for (String arg : args) {
            if (dozvoljeniParametriList.contains(arg)) {
                switch (arg) {
                    case parametarKorisnik:
                        upisaniArgumenti.setProperty("korisnik", args[index + 1]);
                        korisnik = args[index + 1];
                        break;
                    case parametarAdresa:
                        upisaniArgumenti.setProperty("adresa", args[index + 1]);
                        adresa = args[index + 1];
                        break;
                    case parametarLozinka:
                        upisaniArgumenti.setProperty("lozinka", args[index + 1]);
                        lozinka = args[index + 1];
                        break;
                    case parametarPort:
                        upisaniArgumenti.setProperty("port", args[index + 1]);
                        port = Integer.parseInt(args[index + 1]);
                        break;
                    case parametarPauza:
                        upisaniArgumenti.setProperty("pauza", "1");
                        pauza = true;
                        break;
                    case parametarStanje:
                        upisaniArgumenti.setProperty("stanje", "1");
                        stanje = true;
                        break;
                    case parametarKreni:
                        upisaniArgumenti.setProperty("kreni", "1");
                        kreni = true;
                        break;
                    case parametarZaustavi:
                        upisaniArgumenti.setProperty("zaustavi", "1");
                        zaustavi = true;
                        break;
                    case parametarSpavanje:
                        upisaniArgumenti.setProperty("spavanje", args[index + 1]);
                        spavanje = Integer.parseInt(args[index + 1]);
                        break;
                    case parametarEvidencija:
                        upisaniArgumenti.setProperty("datotekaEvidencija", args[index + 1]);
                        datotekaEvidencija = args[index + 1];
                        break;
                    case parametarIot:
                        upisaniArgumenti.setProperty("datotekaIot", args[index + 1]);
                        datotekaIot = args[index + 1];
                        break;
                }
                System.out.println(arg + "\n");

            }
            index++;
        }

    }

    private void ucitajUlazneParametreKlijenta(String[] args) {
        if (korisnik == null && lozinka == null && args.length > 4) {
            upisaniArgumenti.setProperty("datotekaIotClient", args[args.length - 1]);
            this.datotekaIotClient = args[args.length - 1];
        }

    }

}
