/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;

/**
 * Klasa koja izvrsava ulogu radne dretve. svakom korisniku se po spajanju na
 * server dodjeljuje jedna radna dretva koja onda izvrsava njegov zahtjev.
 *
 * @author Zoran Hrncic
 */
public class RadnaDretva extends Thread {

    //KORISNIK korisnik; LOZINKA lozinka; PAUZA;
    private final String sintaksaPauza = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); PAUZA;";
    //KORISNIK korisnik; LOZINKA lozinka; KRENI;
    private final String sintaksaKreni = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); KRENI;";
    //KORISNIK korisnik; LOZINKA lozinka; ZAUSTAVI;
    private final String sintaksaZaustavi = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); ZAUSTAVI;";
    //KORISNIK korisnik; LOZINKA lozinka; STANJE;
    private final String sintaksaStanje = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); STANJE;";
    //KORISNIK korisnik; LOZINKA lozinka; EVIDENCIJA;
    private final String sintaksaEvidencija = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); EVIDENCIJA;";
    //KORISNIK korisnik; LOZINKA lozinka; IOT;
    private final String sintaksaIot = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); IOT;";
    private final String sintaksaSadrzajDatoteke = "IOT (.*);";
    private final String sintaksaSadrzajDatotekeCekaj = "CEKAJ ([0-9]+);";

    private Socket socket;
    private String nazivDretve;
    private Konfiguracija konfig;
    private String username, password;
    private boolean postojiAdmin = false;
    private OutputStream out;
    private static boolean pause_state = false;
    private Evidencija evidencija;
    private ServerSustava serverSustava;
    private String StringJSON;
    private int vrijemeCekanja;
    private Gson gson = new Gson();
    private IOT iot;

    public static final String ERROR_12 = "ERROR 12;";
    public static final String ERROR_10 = "ERROR 10;";
    public static final String ERROR_11 = "ERROR 11;";
    public static final String ERROR_02 = "ERROR 02;";
    public static final String ERROR_20 = "ERROR 20;";
    public static final String ERROR_21 = "ERROR 21;";
    public static final String ERROR_22 = "ERROR 22;";

    public static final String ERROR_13 = "ERROR 13;";
    public static final String OK_20 = "OK 20;";
    public static final String OK_21 = "OK 21;";
    public static final String OK = "OK";
    public static final String OK2 = "OK; 2";
    public static final String OK0 = "OK; 0";
    public static final String OK1 = "OK; 1";
    private boolean stop = false;
    private boolean upis = false;

    /**
     * Medjusobno iskljucivo postavlja varijablju, zastavicu za rad dretve na
     * false za gasenje dretve, odnosno na true kada dretva radi.
     *
     * @param b
     * @return
     */
    public synchronized boolean setKrajRada(boolean b) {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        if (stop != b) {
            stop = b;
            upis = false;
            notify();
            return true;
        } else {
            upis = false;
            notify();
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public int getVrijemeCekanja() {
        return vrijemeCekanja;
    }

    public String getPassword() {
        return password;
    }

    public RadnaDretva() {
    }

    /**
     * Testiranje varijable stop. vraca njezinu vrijednost.
     *
     * @return
     */
    public synchronized boolean isKrajRada() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        boolean ret = stop;
        upis = false;
        notify();
        return ret;
    }

    public RadnaDretva(Socket socket, String nazivDretve, Konfiguracija konfig, Evidencija evidencija, ServerSustava serverSustava, IOT iot) {
        super(nazivDretve);
        this.socket = socket;
        this.nazivDretve = nazivDretve;
        this.konfig = konfig;
        this.evidencija = evidencija;
        this.serverSustava = serverSustava;
        this.iot = iot;
    }

    @Override
    public void interrupt() {
        super.interrupt();

    }

    @Override
    public void run() {
        long pocetak = System.currentTimeMillis();
        while (!isKrajRada()) {
            try (
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();) {
                this.out = outputStream;
                int znak;
                StringBuilder buffer = new StringBuilder();
                while (true) {
                    znak = inputStream.read();
                    if (znak == -1) {
                        break;
                    }
                    buffer.append((char) znak);
                }
                obradaZahtjeva(buffer.toString());
            } catch (IOException ex) {
                setKrajRada(true);
            } finally {
                setKrajRada(true);
            }
        }
        System.out.println("kraj rada dretve : " + nazivDretve);
        evidencija.dodajVrijemeRadaDretve(System.currentTimeMillis() - pocetak);
        serverSustava.smanjiBrojRadnihDretvi();
    }

    public String getStringJSON() {
        return StringJSON;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * Na temlju komande primljenen od korisnika i trenutnog stanja servera
     * odredjuje koja ce se operacija izvrsavati.
     *
     * @param inputLine primljena komanda od korinika
     */
    private void obradaZahtjeva(String inputLine) {
        List<String> commandWords = new ArrayList<>();
        for (String retval : inputLine.split(" ")) {
            commandWords.add(retval);
        }
        if (commandWords.size() > 1) {
            if (serverSustava.isStopRequest()) { //server is stopped              
                if (!obradaZahtjevaAdmina(inputLine, commandWords, serverSustava.isStopRequest())) { //prima samo zahtjeve admina, treba li vratiti odgovor ako je zahtjev od klijenta
                    vratiOdgovorKlijentuString(ERROR_02 + " komanda nije ispravna");
                    evidencija.dodajNeispravanZahtjev();
                }
            } else if (serverSustava.isPause()) {
                if (!obradaZahtjevaAdmina(inputLine, commandWords, serverSustava.isStopRequest())) { //prima samo zahtjeve admina, treba li vratiti odgovor ako je zahtjev od klijenta
                    vratiOdgovorKlijentuString(ERROR_02 + " komanda nije ispravna");
                    evidencija.dodajNeispravanZahtjev();
                }
            } else if (!obradaZahtjevaAdmina(inputLine, commandWords, serverSustava.isStopRequest()) && !obradaZahtjevaKlijenata(inputLine)) {
                vratiOdgovorKlijentuString(ERROR_02 + " komanda nije ispravna");
                evidencija.dodajNeispravanZahtjev();
            }
        }
    }

    /**
     * Prepoznavanje komande, pripada li adminu i koju radnju treba izvrsiti.
     *
     * @param inputLine komanda
     * @param commandWords komanda kao list odvojenih stringova
     * @param stopped zastavica koja nam govori jeli kod servera postoji zahtjev
     * za zaustavljanjem
     * @return rezulat o uspehu true - ako je usojesno inace false
     */
    private boolean obradaZahtjevaAdmina(String inputLine, List<String> commandWords, boolean stopped) {
        if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaPauza)) {
            obradaAdminPauza();
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaKreni)) {
            obradaAdminKreni();
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaZaustavi)) {
            obradaAdminZaustavi();
            return true;
        } else if (commandWords.size() == 5 && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaStanje)) {//without stopped
            obradaAdminStanje();
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaEvidencija)) {
            obradaAdminEvidencija();
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaIot)) {
            obradaAdminIot();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prepoznavanje komande, pripada li klijentu i koju radnju treba izvrsiti.
     *
     * @param inputLine komanda
     * @return rezulat o uspehu true - ako je usojesno inace false
     */
    private boolean obradaZahtjevaKlijenata(String inputLine) {
        if (testInputStringAndExtractSleepTimeAndJSON(inputLine, sintaksaSadrzajDatotekeCekaj, true)) {
            obradaKlijenataCekaj_ObradaIot();
            return true;
        } else if (testInputStringAndExtractSleepTimeAndJSON(inputLine, sintaksaSadrzajDatoteke, false)) {
            addOrUpdateDEvice();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Izvrsava adminov zahtev za pauzom te stavlja server u stanje pauze.
     * server vise ne prima korisnikove komande.
     *
     */
    private void obradaAdminPauza() {
        if (authenticateUser() && serverSustava.setServerPause()) {
            vratiOdgovorKlijentuString(OK);
            evidencija.dodajUspjesnoObavljenZahtjev();
        } else if (authenticateUser() && !serverSustava.setServerPause()) {
            vratiOdgovorKlijentuString(ERROR_11 + " server se već nalazi u stanju pauze!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else if (!authenticateUser()) {
            vratiOdgovorKlijentuString(ERROR_10 + " korisnik nije administrator ili su pogrešni podatc u za prijavu!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else {
            vratiOdgovorKlijentuString("ERROR;");
        }
    }

    /**
     * Izvrsava adminovu komandu za pokretanje servera iz stanja pauze.
     *
     */
    private void obradaAdminKreni() {
        if (authenticateUser() && serverSustava.setServerStart()) {
            vratiOdgovorKlijentuString(OK);
            evidencija.dodajUspjesnoObavljenZahtjev();
        } else if (authenticateUser() && !serverSustava.setServerStart()) {
            vratiOdgovorKlijentuString(ERROR_12 + "server nije u stanju pauze!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else if (!authenticateUser()) {
            vratiOdgovorKlijentuString(ERROR_10 + "korisnik nije administrator ili su pogrešni podatc u za prijavu!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else {
            vratiOdgovorKlijentuString("ERROR;");
        }
    }

    /**
     * Izvrsava adminovu komandu za potpunim zaustavljanjem servera.
     *
     * Ugasi server.
     *
     */
    private void obradaAdminZaustavi() {
        if (!authenticateUser()) {
            vratiOdgovorKlijentuString(ERROR_10 + " korisnik nije administrator ili su pogrešni podatc u za prijavu!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else {
            zaustaviServer();
        }
    }

    /**
     * Izvrsava adminovu komandu za dohvat informacije o trenutnom stanju
     * servera
     *
     */
    private void obradaAdminStanje() {
        if (!authenticateUser()) {
            vratiOdgovorKlijentuString(ERROR_10 + " korisnik nije administrator ili su pogrešni podatc u za prijavu!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else if (authenticateUser() && serverSustava.isStopRequest()) {
            vratiOdgovorKlijentuString(OK2);
            evidencija.dodajUspjesnoObavljenZahtjev();// JE u stanju pauze
        } else if (authenticateUser() && !serverSustava.isPause()) {
            vratiOdgovorKlijentuString(OK0);
            evidencija.dodajUspjesnoObavljenZahtjev(); // nije u stanju pauze
        } else if (authenticateUser() && serverSustava.isPause()) {
            vratiOdgovorKlijentuString(OK1);
            evidencija.dodajUspjesnoObavljenZahtjev();; // JE u stanju pauze
        } else {
            vratiOdgovorKlijentuString("ERROR;");
        }
    }

    /**
     * Izvrsava adminovu komandu za dohvacanje podataka iz evidencije rada.
     *
     */
    private void obradaAdminEvidencija() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (!authenticateUser()) {
            vratiOdgovorKlijentuString(ERROR_10 + " korisnik nije administrator ili su pogrešni podatc u za prijavu!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else {
            try {
                String s = "OK; ZN-KODOVI " + Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))
                        + "; DUZINA " + evidencija.toStringser(Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))).length + "\r\n";
                byte[] b = s.getBytes();
                outputStream.write(b);
                outputStream.write(evidencija.toStringser(Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))));
                byte c[] = outputStream.toByteArray();
                vratiOdgovorKlijentuByte(c);
                evidencija.dodajUspjesnoObavljenZahtjev();
            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                evidencija.dodajNeispravanZahtjev();
            }
        }
    }

    /**
     * Izvrsava obradu adminove naredbe za dohvat podataka o iot uredjajima
     */
    private void obradaAdminIot() {
        if (!authenticateUser()) {
            vratiOdgovorKlijentuString(ERROR_10 + " korisnik nije administrator ili su pogrešni podatc u za prijavu!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else {
            try {
                String s = "OK; ZN-KODOVI " + Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))
                        + "; DUZINA " + iot.toStringser(Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))).length + "\r\n";
                byte[] b = s.getBytes();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(b);
                outputStream.write(iot.toStringser(Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))));
                byte c[] = outputStream.toByteArray();
                vratiOdgovorKlijentuByte(c);
                evidencija.dodajUspjesnoObavljenZahtjev();
            } catch (IOException ex) {
                evidencija.dodajNedozvoljeniZahtjev();
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Autentificira korisika na temelju prosljedjenih podataka.
     *
     * @return true - ako postoje podatci u konfiguraciji, false - ako ne
     * postoje podatci
     */
    private boolean authenticateUser() {
        for (int i = 0; i < 10; i++) {
            if (konfig.dajPostavku("admin." + i + "." + username) != null && konfig.dajPostavku("admin." + i + "." + username).equals(password)) {
                postojiAdmin = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Vrsi obradu clijentovog zahtjeva za spavanjem odredjeni broj sekundi.
     * Nakon uspjesno odradjenog zadatka vraca odgovor, ednosno gresku ako je
     * doslo do prekida.
     */
    private void obradaKlijenataCekaj_ObradaIot() {
        try {
            serverSustava.addDretvaCekaj(this);
            Thread.sleep(vrijemeCekanja * 1000);
            vratiOdgovorKlijentuString(OK);
            evidencija.dodajUspjesnoObavljenZahtjev();
        } catch (InterruptedException ex) {
            vratiOdgovorKlijentuString(ERROR_22 + " Dretva je prekinuta ");
            evidencija.dodajOdbijenZahtjevJerNemaDretvi();
        } finally {
            serverSustava.removeDretvaCekaj(this);
        }

    }

    /**
     * Usporedjuje ulazni string sa sintaksom REGex izraza. ako je
     * zadovoljavajuce, onda pohranjuje u varijable username ipasseord.
     *
     * @param string ulazni text/string
     * @param sintaksa regex izraz
     * @return true - ako zadovoljava, false - ako ne zadovoljava
     */
    public boolean testInputStringAndExtractUsernameAdnPassword(String string, String sintaksa) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = false;
        status = m.matches();
        if (status) {
            int poc = 0;
            int kraj = m.groupCount();
            username = m.group(1);
            password = m.group(2);
        }
        return status;
    }

    /**
     * Usporedjuje ulazni string sa sintaksom REGex izraza. ako je
     * zadovoljavajuce, onda pohranjuje u varijable vrijeme spavanja dretve,
     * odnosno JSON zapis iot uredjaja
     *
     * @param string ulazni text/string
     * @param sintaksa regex izraz
     * @param cekaj vrijeme spavanje dretve
     * @return true - ako zadovoljava, false - ako ne zadovoljava
     */
    public boolean testInputStringAndExtractSleepTimeAndJSON(String string, String sintaksa, boolean cekaj) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = false;
        status = m.matches();
        if (status) {
            if (m.groupCount() == 1 && cekaj) {
                System.out.println(vrijemeCekanja = Integer.parseInt(m.group(1)));
            } else if (m.groupCount() == 1 && !cekaj) {
                StringJSON = m.group(1);
            }
        }
        return status;
    }

    /**
     * Vraca korisniku odgovor.
     *
     * @param odgovor string odgovora
     */
    private void vratiOdgovorKlijentuString(String odgovor) {
        vratiOdgovorKlijentuByte(odgovor.getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Vraca klijentu odgovor.
     *
     * @param odgovor bytecode odgovora
     */
    private void vratiOdgovorKlijentuByte(byte[] odgovor) {
        try {
            out.write(odgovor);
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Izvrsava klijentovu radnju za dodavanje, odnosno azuriranjem IOT
     * uredjaja.
     *
     */
    private void addOrUpdateDEvice() {
        try {
            String string = StringJSON;
            String odgovor = iot.addOrUpdateDevice(string);
            if (odgovor.equals(OK_21)) {
                vratiOdgovorKlijentuString(OK_21);
                evidencija.dodajUspjesnoObavljenZahtjev();
            } else if (odgovor.equals(OK_20)) {
                vratiOdgovorKlijentuString(OK_20);
                evidencija.dodajUspjesnoObavljenZahtjev();
            } else if (odgovor.equals(ERROR_20)) {
                vratiOdgovorKlijentuString(ERROR_20 + " Pogrešan JSON format!");
                evidencija.dodajNeispravanZahtjev();
            } else {
                vratiOdgovorKlijentuString(ERROR_21 + "Došlo je do greške tjekom dodavanja novog IOT uređaja!!"
                        + "\n Mora sadržavati ID oblika pozitivne cjelobrojna vrijednost");
                evidencija.dodajNeispravanZahtjev();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Izvrsava adminov zahtjev za zaustavljanjem servera. Pokrece zaustavljanje
     * servera i gasenje aktivnoh dretvi (cekaj)
     */
    private void zaustaviServer() {
        boolean b = serverSustava.zaustaviServer(konfig);
        if (b) {
            vratiOdgovorKlijentuString("OK");
            evidencija.dodajUspjesnoObavljenZahtjev();
        } else {
            vratiOdgovorKlijentuString(ERROR_13 + " greška kod zaustavljanja servera.");
            evidencija.dodajNedozvoljeniZahtjev();
        }
    }

}
