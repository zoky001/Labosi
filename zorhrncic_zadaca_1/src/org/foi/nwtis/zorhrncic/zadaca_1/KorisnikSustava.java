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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;

/**
 *
 * @author grupa_1
 */
public class KorisnikSustava {

    String sintaksaIP_URL = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5]))|((?:http(?:s)?\\:\\/\\/)?[a-zA-Z0-9_-]+(?:.[a-zA-Z0-9_-]+)*.[a-zA-Z]{2,4}(?:\\/[a-zA-Z0-9_]+)*(?:\\/[a-zA-Z0-9_]+.[a-zA-Z]{2,4}(?:\\?[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)?)?(?:\\&[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)*)$";

    String sintaksaClientSaSpavanjem = "^-s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) (?:--spavanje (0?[1-9]|[1-9][0-9]|[1-5][0-9][0-9]|[6-6][0-0][0-0]))? ([^\\s]+\\.(?i)(txt|xml|json|bin))";// (--spavanje ([^\\s]+) | --ne )";// ([^\\s]+\\.(?i)txt|xml|json)";
    String sintaksaClientBezSpavanja = "^-s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) ([^\\s]+\\.(?i)(txt|xml|json|bin))";
    String sintaksaAdmin = "^-k ([^[a-zA-Z0-9_-]]{3,10}) -l ([^[a-zA-Z0-9[#!]_-]]{3,10}) -s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) (--pauza|--kreni|--zaustavi|--stanje|--evidencija ([^\\s]+\\.(?i)(txt|xml|json|bin))|--iot ([^\\s]+\\.(?i)(txt|xml|json|bin)))";

    String korisnik;
    String lozinka;
    String adresa;
    int port;
    private boolean administrator = false;
    private boolean client = false;
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




    protected Properties upisaniArgumenti = new Properties();

    public KorisnikSustava() {
 

    }

    public static void main(String[] args) {

        KorisnikSustava ks = new KorisnikSustava();
        if (ks.preuzmiPostavke(args)) {
            ks.args = args;
            if (ks.administrator) {
                AdministratorSustava anAdministratorSustava = new AdministratorSustava( ks.upisaniArgumenti);
                anAdministratorSustava.preuzmiKontrolu();

            } else {
                KlijentSustava klijentSustava = new KlijentSustava(ks.upisaniArgumenti);
                klijentSustava.preuzmiKontrolu();

            }
        }

    }

    private boolean preuzmiPostavke(String[] args) {
        administrator = ucitajUlazneParametreAdmina(args);
        client = ucitajUlazneParametreKlijenta(args);
        for (Map.Entry<Object, Object> entry : upisaniArgumenti.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + " - " + value + "");
        }
        if (!administrator && !client) {
            System.out.println("ERROR 02; greška u komandi \n\n Dozvoljene komande:\n\n  ADMINISTRATOR: \n\n -k korisnik -l lozinka -s [ipadresa | adresa] -p port [--pauza | --kreni | --zaustavi | --stanje | --evidencija datoteka1 | --iot datoteka2] \n\n - korisnik (min 3, maks 10 znakova) može sadržavati mala i velika slova, brojeve i znakove: _, -  \n - lozinka (min 3, maks 10 znakova) može sadržavati mala i velika slova, brojeve i znakove: _, -, #, ! \n - ipadresa je adresa IPv4 (npr. 127.0.0.1, 192.168.15.1) \n - adresa je opisni naziv poslužitelja (npr. localhost, dkermek.nwtis.foi.hr) \n - port može biti u intervalu između 8000 i 9999. \n - datoteka1 - datoteka u koju se provodi deserijalizacija evidencije rada. Datoteka je lokalna, s apsolutnim ili relativnim nazivom (npr. evidencija.txt, d:\\NWTiS\\evidencija.txt,...) \n - datoteka2 - datoteka u koju se sprema trenutno stanje iot uređaja. Datoteka je lokalna, s apsolutnim ili relativnim nazivom (npr. iot.txt, d:\\NWTiS\\iot.txt,...) \n\n KLIJENT: \n\n -s [ipadresa | adresa] -p port [--spavanje nnn] datoteka \n\n - nnn je broj sekundi koje čeka radna dretva prije nego što završi rad, može biti u intervalu 1 do 600 \n - datoteka - datoteka s podacima jednog iot uređaja u json formatu. Datoteka je lokalna, s apsolutnim ili relativnim nazivom (npr. iot.txt, d:\\NWTiS\\iot.txt,...).");
            return false;
        }
        return true;
    }

    private boolean ucitajUlazneParametreAdmina(String[] args) {
        if (testInputArgs(sintaksaAdmin, args)) {
            if (testInputString(sintaksaIP_URL, args[5])) {
                upisaniArgumenti.setProperty("korisnik", args[1]);
                upisaniArgumenti.setProperty("lozinka", args[3]);
                upisaniArgumenti.setProperty("adresa", args[5]);
                upisaniArgumenti.setProperty("port", args[7]);
                return setOtherArguments(args);

            }
        }
        return false;
    }

    private boolean setOtherArguments(String[] args) {
        switch (args[8]) {
            case parametarPauza:
                upisaniArgumenti.setProperty("pauza", "1");
                return true;
            case parametarStanje:
                upisaniArgumenti.setProperty("stanje", "1");
                return true;
            case parametarKreni:
                upisaniArgumenti.setProperty("kreni", "1");
                return true;
            case parametarZaustavi:
                upisaniArgumenti.setProperty("zaustavi", "1");
                return true;
            case parametarEvidencija:
                upisaniArgumenti.setProperty("datotekaEvidencija", args[9]);
                return true;
            case parametarIot:
                upisaniArgumenti.setProperty("datotekaIot", args[9]);
                return true;
        }
        return false;
    }

    private boolean ucitajUlazneParametreKlijenta(String[] args) {
        if (testInputArgs(sintaksaClientBezSpavanja, args)) {
            if (testInputString(sintaksaIP_URL, args[1])) {
                upisaniArgumenti.setProperty("adresa", args[1]);
                upisaniArgumenti.setProperty("port", args[3]);
                upisaniArgumenti.setProperty("datotekaIotClient", args[4]);
                return true;
            }
        } else if (testInputArgs(sintaksaClientSaSpavanjem, args)) {
            if (testInputString(sintaksaIP_URL, args[1])) {
                upisaniArgumenti.setProperty("adresa", args[1]);
                upisaniArgumenti.setProperty("port", args[3]);
                upisaniArgumenti.setProperty("datotekaIotClient", args[6]);
                upisaniArgumenti.setProperty("spavanje", args[5]);
                return true;
            }
        }
        return false;
    }

    public boolean testInputArgs(String sintaksa, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return testInputString(sintaksa, sb.toString());
    }

    public boolean testInputString(String sintaksa, String string) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        return m.matches();
    }
}
