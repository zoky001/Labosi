/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdk.nashorn.internal.parser.TokenType;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaBin;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaJSON;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author grupa_1
 */
public class RadnaDretva extends Thread {
    //comanda 1. Admin
    //KORISNIK korisnik; LOZINKA lozinka; PAUZA;

    private String sintaksaPauza = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); PAUZA;";
    //KORISNIK korisnik; LOZINKA lozinka; KRENI;
    private String sintaksaKreni = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); KRENI;";
    //KORISNIK korisnik; LOZINKA lozinka; ZAUSTAVI;
    private String sintaksaZaustavi = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); ZAUSTAVI;";
    //KORISNIK korisnik; LOZINKA lozinka; STANJE;
    private String sintaksaStanje = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); STANJE;";

    //KORISNIK korisnik; LOZINKA lozinka; EVIDENCIJA;
    private String sintaksaEvidencija = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); EVIDENCIJA;";
    //KORISNIK korisnik; LOZINKA lozinka; IOT;
    private String sintaksaIot = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); IOT;";
    private String sintaksaSadrzajDatoteke = "IOT (.*);";
    private String sintaksaSadrzajDatotekeCekaj = "CEKAJ ([0-9]+); IOT (.*);";

    Socket socket;
    String nazivDretve;
    Konfiguracija konfig;
    String username, password;
    private boolean postojiAdmin = false;
    OutputStream out;
    private static boolean pause_state = false;

    private Evidencija evidencija;
    private ServerSustava serverSustava;
    private String StringJSON;

    private int vrijemeCekanja;

    private Gson gson = new Gson();
    private Uredjaj_A iotUredjaj;
    private IOT iot;

    public static final String ERROR_20 = "ERROR 20;";
    public static final String ERROR_21 = "ERROR 21;";
    public static final String OK_20 = "OK 20;";
    public static final String OK_21 = "OK 21;";

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
        } finally {
            try {
                serverSustava.smanjiBrojRadnihDretvi();
            } catch (InterruptedException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            try {
                if (serverSustava.isStopRequest()) {
                    //server is stopped
                    if (!obradaZahtjevaAdmina(inputLine, commandWords, serverSustava.isStopRequest())) {
                        //prima samo zahtjeve admina, treba li vratiti odgovor ako je zahtjev od klijenta
                        out.write("ERROR 02; komanda nije ispravna".getBytes());
                        evidencija.dodajNeispravanZahtjev();
                    }
                } else if (serverSustava.isPause()) {
                    if (!obradaZahtjevaAdmina(inputLine, commandWords, serverSustava.isStopRequest())) {
                        //prima samo zahtjeve admina, treba li vratiti odgovor ako je zahtjev od klijenta
                        out.write("ERROR 02; komanda nije ispravna".getBytes());
                        evidencija.dodajNeispravanZahtjev();
                    }
                } else if (!obradaZahtjevaAdmina(inputLine, commandWords, serverSustava.isStopRequest()) && !obradaZahtjevaKlijenata(inputLine, commandWords)) {
                    out.write("ERROR 02; komanda nije ispravna".getBytes());
                    evidencija.dodajNeispravanZahtjev();
                }
            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean obradaZahtjevaAdmina(String inputLine, List<String> commandWords, boolean stopped) {
        System.out.println("obrada zahtjeva admina");
        if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaPauza)) {
            obradaAdminPauza(inputLine, commandWords);
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaKreni)) {
            obradaAdminKreni(inputLine, commandWords);
            return true;
        } else if (!stopped && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaZaustavi)) {
            obradaAdminZaustavi(inputLine, commandWords);
            return true;
        } else if (commandWords.size() == 5 && testInputStringAndExtractUsernameAdnPassword(inputLine, sintaksaStanje)) {
            //without stopped
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
        System.out.println("obrada zahtjeva klijnata");
        if (testInputStringAndExtractSleepTimeAndJSON(inputLine, sintaksaSadrzajDatotekeCekaj)) {
            obradaKlijenataCekaj_ObradaIot();
            return true;
        } else if (testInputStringAndExtractSleepTimeAndJSON(inputLine, sintaksaSadrzajDatoteke)) {
            obradaKlijentaIotDatoteka();
            System.out.println("upisan parametar datoteka. Dodaje ili ažurira podatke o IOT uređaju na temelju primljenih podataka u json formatu. Od atributa jedino je obavezan id. Ako je neispravni json format vraća odgovor ERROR 20; tekst (tekst objašnjava razlog pogreške). Ako je došlo do problema tijekom rada vraća mu se odgovor ERROR 21; tekst (tekst objašnjava razlog pogreške). Ako je sve u redu i dodan je novi IOT, vraća mu se odgovor OK 20; Ako je sve u redu i ažuriran je postojeći IOT , vraća mu se odgovor OK 21;");
            return true;
        } else {
            return false;
        }
    }

    private void obradaAdminPauza(String inputLine, List<String> commandWords) {
        System.out.println("upisan parametar --pauza pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu i server nije u stanju pauze, privremeno prekida prijem svih komandi osim administratorskih. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako je u stanju pauze vraća se odgovor ERROR 11; tekst (tekst objašnjava razlog pogreške).");
        // extractUsernameAndPasswordFromCommand(inputLine, commandWords);
        try {
            if (authenticateUser() && serverSustava.setServerPause()) {
                out.write("OK".getBytes());

            } else if (authenticateUser() && !serverSustava.setServerPause()) {
                out.write("ERROR 11; server se već nalazi u stanju pauze!".getBytes());
            } else if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else {
                out.write("ERROR;".getBytes());
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminKreni(String inputLine, List<String> commandWords) {
        System.out.println("upisan parametar --kreni pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu i server je u stanju pauze, nastavlja prijem svih komandi. Korisnku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nije u stanju pauze vraća se odgovor ERROR 12; tekst (tekst objašnjava razlog pogreške).");
        //  extractUsernameAndPasswordFromCommand(inputLine, commandWords);
        try {
            if (authenticateUser() && serverSustava.setServerStart()) {
                out.write("OK".getBytes());
            } else if (authenticateUser() && !serverSustava.setServerStart()) {
                out.write("ERROR 12; server nije u stanju pauze!".getBytes());
            } else if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else {
                out.write("ERROR;".getBytes());
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminZaustavi(String inputLine, List<String> commandWords) {
        System.out.println("upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške). ");
        // extractUsernameAndPasswordFromCommand(inputLine, commandWords);
        try {
            if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else if (true) {
                serverSustava.beginStoppingServer(); // TODO dovršti
                out.write("OK".getBytes());
            } else {
                out.write("ERROR;".getBytes());
//TODO upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške).            
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminStanje(String inputLine, List<String> commandWords) {
        System.out.println("upisan parametar --stanje pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako je u redu i ako server nije u stanju pauze, korisniku se vraća odgovor OK; 0. Ako je u redu i ako server je u stanju pauze, korisniku se vraća odgovor OK; 1. Ako je u redu i ako je server ranije dobio komandu za zaustavljenje a još nije zatvorio prijem zahtjeva, korisniku se vraća odgovor OK; 2");
        //  extractUsernameAndPasswordFromCommand(inputLine, commandWords);
        try {
            if (!authenticateUser()) {
                out.write("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!".getBytes());
            } else if (authenticateUser() && serverSustava.isStopRequest()) {
                out.write("OK; 2".getBytes()); // JE u stanju pauze
            } else if (authenticateUser() && !serverSustava.isPause()) {
                out.write("OK; 0".getBytes()); // nije u stanju pauze
            } else if (authenticateUser() && serverSustava.isPause()) {
                out.write("OK; 1".getBytes()); // JE u stanju pauze
            } else {
                out.write("ERROR;".getBytes());
//TODO upisan parametar --zaustavi pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu prekida prijem komandi, serijalizira evidenciju rada i završava rad. Korisniku se vraća odgovor OK.  Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s prekidom rada ili serijalizacijom vraća se odgovor ERROR 13; tekst (tekst objašnjava razlog pogreške).            
            }
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradaAdminEvidencija(String inputLine, List<String> commandWords) {
        System.out.println("upisan parametar --evidencija datoteka1 pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu korisniku se vraća odgovor OK; ZN-KODOVI kod; DUZINA n<CRLF> i zatim vraća deserijalizirane podatke o evidenciji rada u formatiranom obliku u zadanom skupu kodova znakova iz postavki. n predstavlja broj byte-ova koje zauzima deserijalizirana evidencija rada. Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s evidencijom rada vraća se odgovor ERROR 15; tekst (tekst objašnjava razlog pogreške). Ako je evidencija rada u redu admninistrator sprema u datoteku pod nazivom iz opcije.");
        // extractUsernameAndPasswordFromCommand(inputLine, commandWords);
        if (!authenticateUser()) {
            vratiOdgovorKlijentuString("ERROR 10; korisnik nije administrator ili su pogrešni podatc u za prijavu!");
        } else {

            try {

                String str = new String(evidencija.toStringser(Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))), StandardCharsets.UTF_8);

                System.out.println("SER: \n" + str);

                //   ByteBuffer byteBuffer = StandardCharsets.ISO_8859_1.encode(d);
                String s = "OK; ZN-KODOVI " + Charset.forName(konfig.dajPostavku("skup.kodova.znakova")) + "; DUZINA " + evidencija.toStringser(Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))).length + "\r\n";//+evidencija.toStringser();

                byte[] b = s.getBytes();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(b);
                outputStream.write(evidencija.toStringser(Charset.forName(konfig.dajPostavku("skup.kodova.znakova"))));

                byte c[] = outputStream.toByteArray();

                vratiOdgovorKlijentuByte(c);
//TODO
            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void obradaAdminIot(String inputLine, List<String> commandWords) {
        System.out.println("upisan parametar --iot datoteka2 pa provjerava postoji li korisnik i njemu pridružena lozinka u datoteci s postavkama. Ako je u redu korisniku se vraća odgovor OK; ZN-KODOVI kod; DUZINA n<CRLF> i zatim vraća podatke o svim IOT uređajima za koje je primio podatke u formatiranom obliku u zadanom skupu kodova znakova iz postavki. n predstavlja broj byte-ova koje zauzima datoteka. Kada nije u redu, korisnik nije administrator ili lozinka ne odgovara, vraća se odgovor ERROR 10; tekst (tekst objašnjava razlog pogreške). Ako nešto nije u redu s evidencijom rada vraća se odgovor ERROR 16; tekst (tekst objašnjava razlog pogreške). Ako je evidencija rada u redu admninistrator sprema u datoteku pod nazivom iz opcije");
        //  extractUsernameAndPasswordFromCommand(inputLine, commandWords);
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

    private void extractUsernameAndPasswordFromCommand(String inputLine, List<String> commandWords) {
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
    private void obradaKlijenataCekaj_ObradaIot() {
        System.out.println("cekaj + upisan parametar datoteka. Dodaje ili ažurira podatke o IOT uređaju na temelju primljenih podataka u json formatu. Od atributa jedino je obavezan id. Ako je neispravni json format vraća odgovor ERROR 20; tekst (tekst objašnjava razlog pogreške). Ako je došlo do problema tijekom rada vraća mu se odgovor ERROR 21; tekst (tekst objašnjava razlog pogreške). Ako je sve u redu i dodan je novi IOT, vraća mu se odgovor OK 20; Ako je sve u redu i ažuriran je postojeći IOT , vraća mu se odgovor OK 21;");
        System.out.println("upisan parametar --spavanje n . Radna dretva treba čekati zadani broj sekundi pretvorenih u milisekunde. Ako je uspješno odradila čekanje vraća mu se odgovor OK; Ako nije uspjela odraditi čekanje vraća mu se odgovor ERROR 22; tekst (tekst objašnjava razlog pogreške). ");
        try {
            Thread.sleep(vrijemeCekanja * 1000);
            out.write("OK;".getBytes());
            obradaKlijentaIotDatoteka();
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

    private void obradaKlijentaIotDatoteka() {
        System.out.println("SAMO upisan parametar datoteka. Dodaje ili ažurira podatke o IOT uređaju na temelju primljenih podataka u json formatu. Od atributa jedino je obavezan id. Ako je neispravni json format vraća odgovor ERROR 20; tekst (tekst objašnjava razlog pogreške). Ako je došlo do problema tijekom rada vraća mu se odgovor ERROR 21; tekst (tekst objašnjava razlog pogreške). Ako je sve u redu i dodan je novi IOT, vraća mu se odgovor OK 20; Ako je sve u redu i ažuriran je postojeći IOT , vraća mu se odgovor OK 21;");
        Uredjaj_A a = obradiJsonZapisUredjaja(StringJSON);
        if (a != null) {
            addOrUpdateDEvice(a);
        }

    }

    public boolean testInputArgs(String sintaksa, List<String> args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i)).append(" ");
        }
        return testInputStringAndExtractUsernameAdnPassword(sintaksa, sb.toString());
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
            /*for (int i = poc; i <= kraj; i++) {
                System.out.println(i + ". " + m.group(i));
            }*/
        } else {
            System.out.println("Ne odgovara!");
        }
        return status;
    }

    public boolean testInputStringAndExtractSleepTimeAndJSON(String string, String sintaksa) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = false;
        status = m.matches();
        if (status) {
            if (m.groupCount() == 1) {
                StringJSON = m.group(1);
            } else if (m.groupCount() == 2) {
                StringJSON = m.group(2);
                System.out.println(vrijemeCekanja = Integer.parseInt(m.group(1)));
            }

        } else {
            System.out.println("Ne odgovara!");
        }
        return status;
    }

    private Uredjaj_A obradiJsonZapisUredjaja(String StringJSON) {
        try {
            return gson.fromJson(StringJSON, Uredjaj_A.class);
        } catch (JsonSyntaxException e) {
            System.out.println("JSON: " + e.getMessage());
            vratiOdgovorKlijentuString("ERROR 20; Neispravan format JSON zapisa");
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

}
