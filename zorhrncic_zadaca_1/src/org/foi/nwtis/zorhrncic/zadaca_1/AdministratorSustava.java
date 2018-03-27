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
 *
 * @author grupa_1
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

    public void preuzmiKontrolu() {
        try {
            Socket socket = new Socket(uA.getProperty("adresa"), Integer.parseInt(uA.getProperty("port")));
            handle(socket);
        } catch (IOException ex) {
            Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    private void handle(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();) {
            if (getCommand().size() > 0) {
                for (String command : getCommand()) {
                    outputStream.write(command.getBytes());
                }
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
