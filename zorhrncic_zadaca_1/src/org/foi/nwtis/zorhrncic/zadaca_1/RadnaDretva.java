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
import jdk.nashorn.internal.parser.TokenType;
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
    OutputStream out;
    private static boolean pause_state = false;

    private Evidencija evidencija;

    public RadnaDretva(Socket socket, String nazivDretve, Konfiguracija konfig, Evidencija evidencija) {
        super(nazivDretve);
        this.socket = socket;
        this.nazivDretve = nazivDretve;
        this.konfig = konfig;
        this.evidencija = evidencija;
    }

    @Override
    public void interrupt() {
        super.interrupt();

    }

    /*  @Override
    public void run() {
        try (
                
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
           System.out.println("Pokrenuta dreetva naziva: " + nazivDretve); 
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

    }*/
    @Override
    public void run() {
        try (
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();) {
            System.out.println("Pokrenuta dreetva naziva: " + nazivDretve);
            this.out = outputStream;
            int znak;
            StringBuffer buffer = new StringBuffer();
            String inputLine, outputLine;

            while (true) {
                znak = inputStream.read();
                if (znak == -1) {
                    break;
                }
                buffer.append((char) znak);

            }
            System.out.println("Klijent je napisao KOMDANDU: " + buffer.toString());
            obradaZahtjeva(buffer.toString());
            //TODO Provjeri ispravnost primljene komande

        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

//TODO smanji broj aktivnih radnih dretvi kod ServerSustava
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

            if (ServerSustava.isStopRequest()) {
                //server is stopped
                if (!obradaZahtjevaAdmina(commandWords, ServerSustava.isStopRequest())) {
                    try {
                        //prima samo zahtjeve admina, treba li vratiti odgovor ako je zahtjev od klijenta
                        out.write("ERROR 02; komanda nije ispravna".getBytes());
                    } catch (IOException ex) {
                        Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        evidencija.dodajNeispravanZahtjev();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (ServerSustava.isPause()) {
                if (!obradaZahtjevaAdmina(commandWords, ServerSustava.isStopRequest())) {
                    try {
                        //prima samo zahtjeve admina, treba li vratiti odgovor ako je zahtjev od klijenta
                        out.write("ERROR 02; komanda nije ispravna".getBytes());
                    } catch (IOException ex) {
                        Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        evidencija.dodajNeispravanZahtjev();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (!obradaZahtjevaAdmina(commandWords, ServerSustava.isStopRequest()) && !obradaZahtjevaKlijenata(commandWords)) {

                try {
                    out.write("ERROR 02; komanda nije ispravna".getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    evidencija.dodajNeispravanZahtjev();
                } catch (InterruptedException ex) {
                    Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }

    private boolean obradaZahtjevaAdmina(List<String> commandWords, boolean stopped) {
        System.out.println("obrada zahtjeva admina");
        if (!stopped && commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "PAUZA;".equals(commandWords.get(4))) {
            obradaAdminPauza(commandWords);
            return true;
        } else if (!stopped && commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "KRENI;".equals(commandWords.get(4))) {
            obradaAdminKreni(commandWords);
            return true;
        } else if (!stopped && commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "ZAUSTAVI;".equals(commandWords.get(4))) {
            obradaAdminZaustavi(commandWords);
            return true;
        } else if (commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "STANJE;".equals(commandWords.get(4))) {
            //without stopped
            obradaAdminStanje(commandWords);
            return true;
        } else if (!stopped && commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "EVIDENCIJA;".equals(commandWords.get(4))) {
            obradaAdminEvidencija(commandWords);
            return true;
        } else if (!stopped && commandWords.size() == 5 && "KORISNIK".equals(commandWords.get(0)) && commandWords.get(1) != null && "LOZINKA".equals(commandWords.get(2)) && commandWords.get(3) != null && "IOT;".equals(commandWords.get(4))) {
            obradaAdminIot(commandWords);
            return true;
        } else {
            return false;
        }

    }

    private boolean obradaZahtjevaKlijenata(List<String> commandWords) {
        System.out.println("obrada zahtjeva klijnata");
        if (commandWords.size() == 4 && "CEKAJ".equals(commandWords.get(0)) && commandWords.get(1) != null && "IOT".equals(commandWords.get(2)) && commandWords.get(3) != null) {
            obradaKlijenataCekaj_ObradaIot(commandWords);
            return true;
        } else if (commandWords.size() == 2 && "IOT".equals(commandWords.get(0)) && commandWords.get(1) != null) {
            obradaKlijentaIotDatoteka(commandWords.get(1));
            System.out.println("upisan parametar datoteka. Dodaje ili ažurira podatke o IOT uređaju na temelju primljenih podataka u json formatu. Od atributa jedino je obavezan id. Ako je neispravni json format vraća odgovor ERROR 20; tekst (tekst objašnjava razlog pogreške). Ako je došlo do problema tijekom rada vraća mu se odgovor ERROR 21; tekst (tekst objašnjava razlog pogreške). Ako je sve u redu i dodan je novi IOT, vraća mu se odgovor OK 20; Ako je sve u redu i ažuriran je postojeći IOT , vraća mu se odgovor OK 21;");
            return true;
        } else {
            return false;
        }
    }

    private void obradaAdminPauza(List<String> commandWords) {
        System.out.println("upisan parametar --pauza pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu i server nije u stanju pauze, privremeno prekida prijem svih komandi osim administratorskih. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako je u stanju pauze vraća se odgovor ERROR 11; tekst (tekst objašnjava razlog pogreške).");
        extractUsernameAndPasswordFromCommand(commandWords);
        try {
            if (authenticateUser() && ServerSustava.setServerPause()) {

                out.write("OK".getBytes());

            } else if (authenticateUser() && !ServerSustava.setServerPause()) {
                out.write("ERROR 11; server se već nalazi u stanju pauze!".getBytes());
            } else if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else {
                out.write("ERROR;".getBytes());
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminKreni(List<String> commandWords) {
        System.out.println("upisan parametar --kreni pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu i server je u stanju pauze, nastavlja prijem svih komandi. Korisnku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nije u stanju pauze vraća se odgovor ERROR 12; tekst (tekst objašnjava razlog pogreške).");
        extractUsernameAndPasswordFromCommand(commandWords);
        try {
            if (authenticateUser() && ServerSustava.setServerStart()) {
                out.write("OK".getBytes());
            } else if (authenticateUser() && !ServerSustava.setServerStart()) {
                out.write("ERROR 12; server nije u stanju pauze!".getBytes());
            } else if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else {
                out.write("ERROR;".getBytes());
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminZaustavi(List<String> commandWords) {
        System.out.println("upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške). ");
        extractUsernameAndPasswordFromCommand(commandWords);
        try {
            if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else if (true) {
                ServerSustava.beginStoppingServer(); // TODO dovršti
                out.write("OK".getBytes());
            } else {
                out.write("ERROR;".getBytes());
//TODO upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške).            
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminStanje(List<String> commandWords) {
        System.out.println("upisan parametar --stanje pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako je u redu i ako server nije u stanju pauze, korisniku se vraća odgovor OK; 0. Ako je u redu i ako server je u stanju pauze, korisniku se vraća odgovor OK; 1. Ako je u redu i ako je server ranije dobio komandu za zaustavljenje a još nije zatvorio prijem zahtjeva, korisniku se vraća odgovor OK; 2");
        extractUsernameAndPasswordFromCommand(commandWords);
        try {
            if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else if (authenticateUser() && ServerSustava.isStopRequest()) {
                out.write("OK; 2".getBytes()); // JE u stanju pauze
            } else if (authenticateUser() && !ServerSustava.isPause()) {
                out.write("OK; 0".getBytes()); // nije u stanju pauze
            } else if (authenticateUser() && ServerSustava.isPause()) {
                out.write("OK; 1".getBytes()); // JE u stanju pauze
            } else {
                out.write("ERROR;".getBytes());
//TODO upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške).            
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminEvidencija(List<String> commandWords) {
        System.out.println("upisan parametar --evidencija datoteka1 pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu korisniku se vraća odgovor OK; ZN-KODOVI kod; DUZINA n<CRLF> i zatim vraća deserijalizirane podatke o evidenciji rada u formatiranom obliku u zadanom skupu kodova znakova iz postavki. n predstavlja broj byte-ova koje zauzima deserijalizirana evidencija rada. Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s evidencijom rada vraća se odgovor ERROR 15; tekst (tekst objašnjava razlog pogreške). Ako je evidencija rada u redu admninistrator sprema u datoteku pod nazivom iz opcije.");
        extractUsernameAndPasswordFromCommand(commandWords);
        try {
            if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else {
                out.write("ERROR; TODO".getBytes());
//TODO
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminIot(List<String> commandWords) {
        System.out.println("upisan parametar --iot datoteka2 pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu korisniku se vraća odgovor OK; ZN-KODOVI kod; DUZINA n<CRLF> i zatim vraća podatke o svim IOT uređajima za koje je primio podatke u formatiranom obliku u zadanom skupu kodova znakova iz postavki. n predstavlja broj byte-ova koje zauzima datoteka. Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s evidencijom rada vraća se odgovor ERROR 16; tekst (tekst objašnjava razlog pogreške). Ako je evidencija rada u redu admninistrator sprema u datoteku pod nazivom iz opcije");
        extractUsernameAndPasswordFromCommand(commandWords);
        try {
            if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else {
                out.write("ERROR; TODO".getBytes());
//TODO
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
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
            //System.out.println("Lozink: \" konfig.dajPostavku(\"admin." + i + "." + username + " : " + password + " )   :  _" + konfig.dajPostavku("admin." + i + "." + username) + "_");
            if (konfig.dajPostavku("admin." + i + "." + username) != null && konfig.dajPostavku("admin." + i + "." + username).equals(password)) {
                System.out.println("postoji korinsnik");
                postojiAdmin = true;
                return true;
            }
        }
        return false;
    }

    //client
    private void obradaKlijenataCekaj_ObradaIot(List<String> commandWords) {
        System.out.println("cekaj + upisan parametar datoteka. Dodaje ili ažurira podatke o IOT uređaju na temelju primljenih podataka u json formatu. Od atributa jedino je obavezan id. Ako je neispravni json format vraća odgovor ERROR 20; tekst (tekst objašnjava razlog pogreške). Ako je došlo do problema tijekom rada vraća mu se odgovor ERROR 21; tekst (tekst objašnjava razlog pogreške). Ako je sve u redu i dodan je novi IOT, vraća mu se odgovor OK 20; Ako je sve u redu i ažuriran je postojeći IOT , vraća mu se odgovor OK 21;");

        System.out.println("upisan parametar --spavanje n . Radna dretva treba čekati zadani broj sekundi pretvorenih u milisekunde. Ako je uspješno odradila čekanje vraća mu se odgovor OK; Ako nije uspjela odraditi čekanje vraća mu se odgovor ERROR 22; tekst (tekst objašnjava razlog pogreške). ");
        int vrijemeCekanja;
        try {
            if (commandWords.size() == 4 && "CEKAJ".equals(commandWords.get(0)) && "IOT".equals(commandWords.get(2)) && commandWords.get(3) != null) {
                
            
                System.out.println(vrijemeCekanja = Integer.parseInt(commandWords.get(1).substring(0, commandWords.get(1).length() - 1)));
                Thread.sleep(vrijemeCekanja * 1000);
                out.write("OK;".getBytes());
                obradaKlijentaIotDatoteka(commandWords.get(3));

            }
        } catch (InterruptedException ex) {
            String mess = "ERROR 22; " + ex.getMessage();
            try {
                out.write(mess.getBytes());
            } catch (IOException ex1) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void obradaKlijentaIotDatoteka(String commandWords) {
        System.out.println("SAMO upisan parametar datoteka. Dodaje ili ažurira podatke o IOT uređaju na temelju primljenih podataka u json formatu. Od atributa jedino je obavezan id. Ako je neispravni json format vraća odgovor ERROR 20; tekst (tekst objašnjava razlog pogreške). Ako je došlo do problema tijekom rada vraća mu se odgovor ERROR 21; tekst (tekst objašnjava razlog pogreške). Ako je sve u redu i dodan je novi IOT, vraća mu se odgovor OK 20; Ako je sve u redu i ažuriran je postojeći IOT , vraća mu se odgovor OK 21;");
        try {
            out.write("OK; TODO REzultati iod datoteka".getBytes());
        } catch (IOException ex) {
            String mess = "ERROR 22; " + ex.getMessage();
            try {
                out.write(mess.getBytes());
            } catch (IOException ex1) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
