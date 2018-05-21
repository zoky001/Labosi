/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa obavlja sve funkcije potrebne za komuikaciju Administratora sa serverom, te upravlja odgovorom.
 * @author Zoran Hrncic
 */
public class AdministratorSustava extends KorisnikSustava {

    private final String sintaksaAdminEvidencijaIot = "^OK; ZN-KODOVI ([^\\s]+); DUZINA ([0-9]+)\r\n([\\s\\S]+)";

    public AdministratorSustava() {
    }

    public String getCharset() {
        return charset;
    }
    private String charset;
    private int numberOfBytes;
    private int znak;
    public final static char CR = (char) 0x0D;
    public final static char LF = (char) 0x0A;

    public AdministratorSustava(Properties upisaniAurumenti) {
        super();

        this.uA = upisaniAurumenti;
    }

    /**
     * Kreira socket kojim se spaja na server na temelju adrese i porta prosljedjenog u konstruktoru.
     */
    public void preuzmiKontrolu() {
        try {
            Socket socket = new Socket(uA.getProperty("adresa"), Integer.parseInt(uA.getProperty("port")));
            handle(socket);
            
        } catch(java.net.ConnectException e){
            System.out.println("Server ne postoji ili nije aktivan!!");
        }catch (IOException ex) {
            Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Na temelju upisanih argumentata, koji su prosljedjeni u konstruktoru, kreira komandu koja se salje na server.
     * 
     * @return Lista komandi koje su prepoznate na temelju ulaznih argumenata.
     */
    private List<String> getCommand() {
        commands = new ArrayList<>();          
        if (uA.containsKey("pauza") && "1".equals(uA.getProperty("pauza")) && uA.containsKey("korisnik") && uA.containsKey("lozinka")) {
            commands.add("KORISNIK " + uA.getProperty("korisnik") + "; LOZINKA " + uA.getProperty("lozinka") + "; PAUZA;");
        } else if (uA.containsKey("kreni") && "1".equals(uA.getProperty("kreni")) && uA.containsKey("korisnik") 
                && uA.containsKey("lozinka")) {
            commands.add("KORISNIK " + uA.getProperty("korisnik") + "; LOZINKA " + uA.getProperty("lozinka") + "; KRENI;");
        } else if (uA.containsKey("zaustavi") && "1".equals(uA.getProperty("zaustavi")) && uA.containsKey("korisnik") 
                && uA.containsKey("lozinka")) {
            commands.add("KORISNIK " + uA.getProperty("korisnik") + "; LOZINKA " + uA.getProperty("lozinka") + "; ZAUSTAVI;");
        } else if (uA.containsKey("stanje") && "1".equals(uA.getProperty("stanje")) && uA.containsKey("korisnik") 
                && uA.containsKey("lozinka")) {
            commands.add("KORISNIK " + uA.getProperty("korisnik") + "; LOZINKA " + uA.getProperty("lozinka") + "; STANJE;");
        } else if (uA.containsKey("datotekaEvidencija") && uA.getProperty("datotekaEvidencija") != null 
                && uA.containsKey("korisnik") && uA.containsKey("lozinka")) {
            commands.add("KORISNIK " + uA.getProperty("korisnik") + "; LOZINKA " + uA.getProperty("lozinka") + "; EVIDENCIJA;");
        } else if (uA.containsKey("datotekaIot") && uA.getProperty("datotekaIot") != null && uA.containsKey("korisnik") 
                && uA.containsKey("lozinka")) {
            commands.add("KORISNIK " + uA.getProperty("korisnik") + "; LOZINKA " + uA.getProperty("lozinka") + "; IOT;");
        }
        return commands;
    }

    /**
     * Sluzi za testiranje odgovora primljenog od servera.
     * Testira odgovor primljen nakon adminova zahtjeva za evidencijom ili popisom IOT uredjaja.
     * Ako je odgovor zadovoljen, pohranjuje CHARSET u varijablu charset
     * @param string odgovor servera u obliku stringa
     * @param sintaksa zadani RegEx koji definira oblik odgovora
     * @return 
     */
    public boolean testInputStringAndExtractChasterAdnSize(String string, String sintaksa) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = false;
        status = m.matches();
        if (status) {
            if (m.groupCount() == 3) {
                charset = m.group(1);
             //   System.out.println(numberOfBytes = Integer.parseInt(m.group(2)));
            }

        } else {
           
        }
        return status;
    }

    /**
     * Vrsi obradu odgovora primljenog od servera. 
     * Ispisuje odgovor korisniku, odnosno salje ga na daljnju obradu.
     * @param baos niz byte-ova primljen od servera
     */
    private void obradaOdgovora(ByteArrayOutputStream baos) {
        String str = new String(baos.toByteArray());
        if (testInputStringAndExtractChasterAdnSize(str, sintaksaAdminEvidencijaIot)) {
           // System.out.println("Vraćen je odgovor sa datotekom:  " + charset + "\n" + numberOfBytes);
            System.out.println(str);
            pohranaDatotekeUZadanomFormatu(baos);
        } else {
            System.out.println(str);
        }

    }
/**
 * Pohranjuje niz byte-ova u datoteku pod nazivom definiranim u argumentima priljenim kroz konstruktor
 * @param baos niz byte-ova datoteke
 */
    private void pohranaDatotekeUZadanomFormatu(ByteArrayOutputStream baos) {
        String nazivDatoteke;
        if (uA.containsKey("datotekaEvidencija")) {
            nazivDatoteke = uA.getProperty("datotekaEvidencija");
        } else if (uA.containsKey("datotekaIot")) {
            nazivDatoteke = uA.getProperty("datotekaIot");
        } else {
            nazivDatoteke = "datoteka.txt";
        }
        // String str = ;//, StandardCharsets.UTF_8);
        ByteArrayOutputStream baosFile = new ByteArrayOutputStream();
        boolean upis = false;
        for (int i = 0; i < baos.toByteArray().length; i++) {
            if (upis) {
                baosFile.write(baos.toByteArray()[i]);
            }
            if (i > 1 && (char) baos.toByteArray()[i] == LF && baos.toByteArray()[i - 1] == CR) {
                upis = true;
            }
        }
        saveByteArrayToFile(nazivDatoteke, new String(baosFile.toByteArray(), Charset.forName(charset)));
    }

    /**
     * Pohranjuje text u datoteku.
     * @param nazivDatoteke - naziv datoteke u koju se pohranjuje/kreira
     * @param baos -text koji se pohranjuje
     */
    private void saveByteArrayToFile(String nazivDatoteke, String baos) {
        FileWriter out = null;
        try {
            out = new FileWriter(nazivDatoteke);
            out.write(baos);
        } catch (IOException ex) {
            Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Nakon kreiranja socketa, salje komandu serveru i ceka odgovor.
     * Po primitku odgovora, salje na daljnju obradu.
     * @param socket socket pomocu kojeg se spaja na server
     */
    private void handle(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();) {
            if (getCommand().size() > 0) {
               /*
                • STANI; – potpuno prekida preuzimanje meteoroloških podataka i preuzimanje komandi. I
završava rad. Vraća OK 10; ako nije bio u postupku prekida, odnosno ERR 16; ako je bio u
postupku prekida.
• STANJE; – vraća trenutno stanje poslužitelja. Vraća OK dd; gdje dd znači: 11 – preuzima sve
komanda i preuzima meteo podatke, 12 - preuzima sve komanda i ne preuzima meteo podatke,
13 – preuzima samo poslužiteljske komanda i preuzima meteo podatke, 14 – preuzima samo
poslužiteljske komanda i ne preuzima meteo podatke.
• LISTAJ; – vraća podatke svih korisnika. Vraća OK 10; [{″ki″: d{1-6} ″prezime″: prezime,
″ime″: ime,},...]; odnosno ERR 17; ako ne postoji.
             • KORISNIK korisnik; LOZINKA lozinka; DODAJ ″prezime″ ″ime″; – Vraća
OK 10; ako ne postoji korisnik i uspješno je dodan odnosno ERR 10; ako već postoji korisnik.
• KORISNIK korisnik; LOZINKA lozinka; – autentikacija korisnika. Vraća ERR 11;
ako ne postoji korisnik ili ne odgovara lozinka. Ako su podaci u redu i nema nastavka komande
vraća OK 10; Odnosno ako ima, prelazi na obradu ostalog dijela komande.
• PAUZA; – privremeno prekida preuzimanje ostalih komande osim za poslužitelja. Vraća OK
10; ako nije bio u pauzi, odnosno ERR 12; ako je bio u pauzi.
• KRENI; – nastavlja s preuzimanjem svih komandi. Vraća OK 10; ako je bio u pauzi, odnosno
ERR 13; ako nije bio u pauzi.
• PASIVNO; – privremeno prekida preuzimanje meteoroloških podataka od sljedećeg ciklusa
Vraća OK 10; ako je bio u aktivnom radu, odnosno ERR 14; ako je bio u pasivnom radu.
• AKTIVNO; – nastavlja s preuzimanjem meteoroloških podataka od sljedećeg ciklusa. Vraća
OK 10; ako je bio u pasivnom radu, odnosno ERR 15; ako je bio u aktivnom radu.   
                
                */
               
               
               /*
               KORISNIK korisnik; LOZINKA lozinka; – autentikacija korisnika. Vraća ERR 11;
ako ne postoji korisnik ili ne odgovara lozinka. Ako su podaci u redu i nema nastavka komande
vraća OK 10; Odnosno ako ima, prelazi na obradu ostalog dijela komande.
• GRUPA DODAJ; – registrira grupu. Vraća OK 20; ako nije bila registrirana (ne postoji),
odnosno ERR 20; ako je bila registrirana.
• GRUPA PREKID; – odjavljuje (deregistrira) grupu. Vraća OK 20; ako je bila registrirana,
odnosno ERR 21; ako nije bila registrirana.
• GRUPA KRENI; – aktivira grupu. Vraća OK 20; ako nije bila aktivna, ERR 22; ako je bila
aktivna odnosno ERR 21; ako ne postoji.
• GRUPA PAUZA; – blokira grupu. Vraća OK 20; ako je bila aktivna, ERR 23; ako nije bila
aktivna odnosno ERR 21; ako ne postoji
• GRUPA STANJE; – vraća status grupe. Vraća OK dd; gdje dd znači: 21 – grupa je aktivna,
22 – grupa blokirana odnosno ERR 21; ako ne postoji.
               */
                outputStream.write("KORISNIK korisnik; LOZINKA lozinka; STANJE;".getBytes());
              /*
                for (String command : getCommand()) {
                    outputStream.write(command.getBytes());
                }
                
                */
            } else {
                System.out.println("ERROR 02; komanda nije ispravna");
            }
            outputStream.flush();
            socket.shutdownOutput();
            //StringBuffer buffer = new StringBuffer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (true) {
                znak = inputStream.read();
                if (znak == -1) {
                    break;
                }
                baos.write(znak);
            }
            
            
            obradaOdgovora(baos);
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
