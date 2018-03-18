/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;

/**
 *
 * @author grupa_1
 */
public class RadnaDretva extends Thread {

    Socket socket;
    String nazivDretve;
    Konfiguracija konfig;
    String username, password;
    private boolean postojiAdmin = false;
    PrintWriter out;
    private static boolean pause_state = false;

    public RadnaDretva(Socket socket, String nazivDretve, Konfiguracija konfig) {
        super(nazivDretve);
        this.socket = socket;
        this.nazivDretve = nazivDretve;
        this.konfig = konfig;
    }

    @Override
    public void interrupt() {
        super.interrupt();

    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            this.out = out;
            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Klijent je napisao: " + inputLine);
                obradaZahtjeva(inputLine);
                out.println("Dobar dan ponovno - Server");
            }

//TODO smanji broj aktivnih radnih dretvi kod ServerSustava
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

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
            obradaZahtjevaAdmina(commandWords);
            obradaZahtjevaKlijenata(commandWords);
        }

    }

    private void obradaZahtjevaAdmina(List<String> commandWords) {
        System.out.println("obrada zahtjeva admina");
        if (commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "PAUZA;".equals(commandWords.get(4))) {
            obradaAdminPauza(commandWords);
        } else if (commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "KRENI;".equals(commandWords.get(4))) {
            obradaAdminKreni(commandWords);
        } else if (commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "ZAUSTAVI;".equals(commandWords.get(4))) {
            obradaAdminZaustavi(commandWords);
        } else if (commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "STANJE;".equals(commandWords.get(4))) {
            obradaAdminStanje(commandWords);
        } else if (commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "EVIDENCIJA;".equals(commandWords.get(4))) {
            obradaAdminEvidencija(commandWords);
        } else if (commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "IOT;".equals(commandWords.get(4))) {
            obradaAdminIot(commandWords);
        }

    }

    private void obradaZahtjevaKlijenata(List<String> commandWords) {
        System.out.println("obrada zahtjeva klijnata");
        if (commandWords.size() == 2 && "CEKAJ".equals(commandWords.get(0)) && commandWords.get(1) != null) {
            obradaKlijenataCekaj(commandWords);
        } else if (commandWords.size() == 2 && "IOT".equals(commandWords.get(0)) && commandWords.get(1) != null) {
            System.out.println("upisan parametar datoteka. Dodaje ili ažurira podatke o IOT uređaju na temelju primljenih podataka u json formatu. Od atributa jedino je obavezan id. Ako je neispravni json format vraća odgovor ERROR 20; tekst (tekst objašnjava razlog pogreške). Ako je došlo do problema tijekom rada vraća mu se odgovor ERROR 21; tekst (tekst objašnjava razlog pogreške). Ako je sve u redu i dodan je novi IOT, vraća mu se odgovor OK 20; Ako je sve u redu i ažuriran je postojeći IOT , vraća mu se odgovor OK 21;");
        }

    }

    private void obradaAdminPauza(List<String> commandWords) {
        System.out.println("upisan parametar --pauza pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu i server nije u stanju pauze, privremeno prekida prijem svih komandi osim administratorskih. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako je u stanju pauze vraća se odgovor ERROR 11; tekst (tekst objašnjava razlog pogreške).");
        extractUsernameAndPasswordFromCommand(commandWords);
        if (authenticateUser() && ServerSustava.setServerPause()) {
            out.println("OK");
        } else if (authenticateUser() && !ServerSustava.setServerPause()) {
            out.println("ERROR 11; server se već nalazi u stanju pauze!");
        } else if (!authenticateUser()) {
            out.println("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!");
        } else {
            out.println("ERROR;");
        }
    }

    private void obradaAdminKreni(List<String> commandWords) {
        System.out.println("upisan parametar --kreni pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu i server je u stanju pauze, nastavlja prijem svih komandi. Korisnku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nije u stanju pauze vraća se odgovor ERROR 12; tekst (tekst objašnjava razlog pogreške).");
        extractUsernameAndPasswordFromCommand(commandWords);
        if (authenticateUser() && ServerSustava.setServerStart()) {
            out.println("OK");
        } else if (authenticateUser() && !ServerSustava.setServerStart()) {
            out.println("ERROR 12; server nije u stanju pauze!");
        } else if (!authenticateUser()) {
            out.println("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!");
        } else {
            out.println("ERROR;");
        }
    }

    private void obradaAdminZaustavi(List<String> commandWords) {
        System.out.println("upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške). ");
        extractUsernameAndPasswordFromCommand(commandWords);
        if (!authenticateUser()) {
            out.println("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!");
        } else if (true) {
            ServerSustava.beginStoppingServer(); // TODO dovršti
            out.println("OK");
        } else {
            out.println("ERROR;");
//TODO upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške).            
        }

    }

    private void obradaAdminStanje(List<String> commandWords) {
        System.out.println("upisan parametar --stanje pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako je u redu i ako server nije u stanju pauze, korisniku se vraća odgovor OK; 0. Ako je u redu i ako server je u stanju pauze, korisniku se vraća odgovor OK; 1. Ako je u redu i ako je server ranije dobio komandu za zaustavljenje a još nije zatvorio prijem zahtjeva, korisniku se vraća odgovor OK; 2");
        extractUsernameAndPasswordFromCommand(commandWords);
        if (!authenticateUser()) {
            out.println("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!");
        } else if (authenticateUser() && ServerSustava.isStopRequest()) {
            out.println("OK; 2"); // JE u stanju pauze
        } else if (authenticateUser() && !ServerSustava.isPause()) {
            out.println("OK; 0"); // nije u stanju pauze
        } else if (authenticateUser() && ServerSustava.isPause()) {
            out.println("OK; 1"); // JE u stanju pauze
        } else {
            out.println("ERROR;");
//TODO upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške).            
        }
    }

    private void obradaAdminEvidencija(List<String> commandWords) {
        System.out.println("upisan parametar --evidencija datoteka1 pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu korisniku se vraća odgovor OK; ZN-KODOVI kod; DUZINA n<CRLF> i zatim vraća deserijalizirane podatke o evidenciji rada u formatiranom obliku u zadanom skupu kodova znakova iz postavki. n predstavlja broj byte-ova koje zauzima deserijalizirana evidencija rada. Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s evidencijom rada vraća se odgovor ERROR 15; tekst (tekst objašnjava razlog pogreške). Ako je evidencija rada u redu admninistrator sprema u datoteku pod nazivom iz opcije.");
        extractUsernameAndPasswordFromCommand(commandWords);
        if (!authenticateUser()) {
            out.println("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!");
        } else {
            out.println("ERROR; TODO");
//TODO
        }
    }

    private void obradaAdminIot(List<String> commandWords) {
        System.out.println("upisan parametar --iot datoteka2 pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu korisniku se vraća odgovor OK; ZN-KODOVI kod; DUZINA n<CRLF> i zatim vraća podatke o svim IOT uređajima za koje je primio podatke u formatiranom obliku u zadanom skupu kodova znakova iz postavki. n predstavlja broj byte-ova koje zauzima datoteka. Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s evidencijom rada vraća se odgovor ERROR 16; tekst (tekst objašnjava razlog pogreške). Ako je evidencija rada u redu admninistrator sprema u datoteku pod nazivom iz opcije");
        extractUsernameAndPasswordFromCommand(commandWords);
        if (!authenticateUser()) {
            out.println("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!");
        } else {
            out.println("ERROR; TODO");
//TODO
        }
    }

    private void extractUsernameAndPasswordFromCommand(List<String> commandWords) {
        if (commandWords.get(1) != null && commandWords.get(3) != null) {
            this.username = commandWords.get(1).substring(0, commandWords.get(1).length() - 1);

            this.password = commandWords.get(3).substring(0, commandWords.get(3).length() - 1);
        }
    }

    private boolean authenticateUser() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Lozink: \" konfig.dajPostavku(\"admin." + i + "." + username + " : " + password + " )   :  _" + konfig.dajPostavku("admin." + i + "." + username) + "_");
            if (konfig.dajPostavku("admin." + i + "." + username) != null && konfig.dajPostavku("admin." + i + "." + username).equals(password)) {
                System.out.println("postoji korinsnik");
                postojiAdmin = true;
                return true;
            }
        }
        return false;
    }

    //client
    private void obradaKlijenataCekaj(List<String> commandWords) {
        System.out.println("upisan parametar --spavanje n . Radna dretva treba čekati zadani broj sekundi pretvorenih u milisekunde. Ako je uspješno odradila čekanje vraća mu se odgovor OK; Ako nije uspjela odraditi čekanje vraća mu se odgovor ERROR 22; tekst (tekst objašnjava razlog pogreške). ");
        int vrijemeCekanja;

        if (commandWords.size() == 2 && "CEKAJ".equals(commandWords.get(0))) {

            System.out.println(vrijemeCekanja = Integer.parseInt(commandWords.get(1)));
            try {
                Thread.sleep(vrijemeCekanja * 1000);
                out.println("OK; 1");
            } catch (InterruptedException ex) {
                out.println("ERROR 22; " + ex.getMessage());
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}
