/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
 *
 * @author grupa_1
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
    private Uredjaj_A iotUredjaj;
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
            serverSustava.smanjiBrojRadnihDretvi();
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
                    OutputStream outputStream = socket.getOutputStream();) {//                System.out.println("Pokrenuta dreetva naziva: " + nazivDretve);
                this.out = outputStream;
                int znak;
                StringBuffer buffer = new StringBuffer();
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
        evidencija.dodajVrijemeRadaDretve(System.currentTimeMillis() - pocetak);
    }

    public String getStringJSON() {
        return StringJSON;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

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
            } else if (!obradaZahtjevaAdmina(inputLine, commandWords, serverSustava.isStopRequest()) && !obradaZahtjevaKlijenata(inputLine, commandWords)) {
                vratiOdgovorKlijentuString(ERROR_02 + " komanda nije ispravna");
                evidencija.dodajNeispravanZahtjev();
            }
        }
    }

    private boolean obradaZahtjevaAdmina(String inputLine, List<String> commandWords, boolean stopped) {
        if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaPauza)) {
            obradaAdminPauza(inputLine, commandWords);
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaKreni)) {
            obradaAdminKreni(inputLine, commandWords);
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaZaustavi)) {
            obradaAdminZaustavi(inputLine, commandWords);
            return true;
        } else if (commandWords.size() == 5 && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaStanje)) {//without stopped
            obradaAdminStanje(inputLine, commandWords);
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaEvidencija)) {
            obradaAdminEvidencija(inputLine, commandWords);
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaIot)) {
            obradaAdminIot(inputLine, commandWords);
            return true;
        } else {
            return false;
        }
    }

    private boolean obradaZahtjevaKlijenata(String inputLine, List<String> commandWords) {
        if (testInputStringAndExtractSleepTimeAndJSON(inputLine, sintaksaSadrzajDatotekeCekaj, true)) {
            obradaKlijenataCekaj_ObradaIot();
            return true;
        } else if (testInputStringAndExtractSleepTimeAndJSON(inputLine, sintaksaSadrzajDatoteke, false)) {
            obradaKlijentaIotDatoteka();
            return true;
        } else {
            return false;
        }
    }

    private void obradaAdminPauza(String inputLine, List<String> commandWords) {
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

    private void obradaAdminKreni(String inputLine, List<String> commandWords) {
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

    private void obradaAdminZaustavi(String inputLine, List<String> commandWords) {
        if (!authenticateUser()) {
            vratiOdgovorKlijentuString(ERROR_10 + " korisnik nije administrator ili su pogrešni podatc u za prijavu!");
            evidencija.dodajNedozvoljeniZahtjev();
        } else {
            zaustaviServer();
        }
    }

    private void obradaAdminStanje(String inputLine, List<String> commandWords) {
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

    private void obradaAdminEvidencija(String inputLine, List<String> commandWords) {
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

    private void obradaAdminIot(String inputLine, List<String> commandWords) {
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
            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean authenticateUser() {
        for (int i = 0; i < 10; i++) {
            if (konfig.dajPostavku("admin." + i + "." + username) != null && konfig.dajPostavku("admin." + i + "." + username).equals(password)) {
                postojiAdmin = true;
                return true;
            }
        }
        return false;
    }

    //client
    private void obradaKlijenataCekaj_ObradaIot() {
        try {
            serverSustava.addDretvaCekaj(this);
            Thread.sleep(vrijemeCekanja * 1000);
            vratiOdgovorKlijentuString(OK);
            evidencija.dodajUspjesnoObavljenZahtjev();
        } catch (InterruptedException ex) {
            vratiOdgovorKlijentuString(ERROR_22 + " Dretva je prekinuta ");
            evidencija.dodajNedozvoljeniZahtjev();
        } finally {
            serverSustava.removeDretvaCekaj(this);
        }

    }

    private void obradaKlijentaIotDatoteka() {
        Uredjaj_A a = obradiJsonZapisUredjaja(StringJSON);
        if (a != null) {
            addOrUpdateDEvice(a);
        }
    }

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

    private Uredjaj_A obradiJsonZapisUredjaja(String StringJSON) {
        try {
            return gson.fromJson(StringJSON, Uredjaj_A.class);
        } catch (JsonSyntaxException e) {
            vratiOdgovorKlijentuString(ERROR_20 + " Neispravan format JSON zapisa");
        }
        return null;
    }

    private void vratiOdgovorKlijentuString(String odgovor) {
        vratiOdgovorKlijentuByte(odgovor.getBytes());
    }

    private void vratiOdgovorKlijentuByte(byte[] odgovor) {
        try {
            out.write(odgovor);
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addOrUpdateDEvice(Uredjaj_A iotUredjaj) {
        try {
            String odgovor = iot.addOrUpdateDevice(iotUredjaj);
            if (odgovor.equals(OK_21)) {
                vratiOdgovorKlijentuString(OK_21);
            } else if (odgovor.equals(OK_20)) {
                vratiOdgovorKlijentuString(OK_20);
            } else {
                vratiOdgovorKlijentuString(ERROR_21 + "Došlo je do greške tjekom dodavanja novog IOT uređaja!!");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
