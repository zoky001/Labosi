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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import sun.misc.IOUtils;

/**
 *
 * @author grupa_1
 */
public class AdministratorSustava extends KorisnikSustava {

    private final String sintaksaAdminEvidencijaIot = "^OK; ZN-KODOVI ([^\\s]+); DUZINA ([0-9]+)\r\n([\\s\\S]+)";
    private String charset;
    private int numberOfBytes;
    private int znak;
    public final static char CR = (char) 0x0D;
    public final static char LF = (char) 0x0A;

    public AdministratorSustava(Properties upisaniAurumenti) {
        super();

        this.upisaniArgumenti = upisaniAurumenti;
    }

    public void preuzmiKontrolu() {

        try {
            Socket socket = new Socket(upisaniArgumenti.getProperty("adresa"), Integer.parseInt(upisaniArgumenti.getProperty("port")));
            try (
                    InputStream inputStream = socket.getInputStream();
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
                    // buffer.append((char) znak);
                    baos.write(znak);
                }
                obradaOdgovora(baos);
            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<String> getCommand() {
        List<String> commands = new ArrayList();

        if (upisaniArgumenti.containsKey("pauza") && "1".equals(upisaniArgumenti.getProperty("pauza")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; PAUZA;");
            System.out.println("pauza");
        } else if (upisaniArgumenti.containsKey("kreni") && "1".equals(upisaniArgumenti.getProperty("kreni")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; KRENI;");
            System.out.println("kreni");

        } else if (upisaniArgumenti.containsKey("zaustavi") && "1".equals(upisaniArgumenti.getProperty("zaustavi")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; ZAUSTAVI;");
            System.out.println("zaustavi");

        } else if (upisaniArgumenti.containsKey("stanje") && "1".equals(upisaniArgumenti.getProperty("stanje")) && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; STANJE;");
            System.out.println("stanje");

        } else if (upisaniArgumenti.containsKey("datotekaEvidencija") && upisaniArgumenti.getProperty("datotekaEvidencija") != null && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; EVIDENCIJA;");
            System.out.println("evidencija");

        } else if (upisaniArgumenti.containsKey("datotekaIot") && upisaniArgumenti.getProperty("datotekaIot") != null && upisaniArgumenti.containsKey("korisnik") && upisaniArgumenti.containsKey("lozinka")) {
            commands.add("KORISNIK " + upisaniArgumenti.getProperty("korisnik") + "; LOZINKA " + upisaniArgumenti.getProperty("lozinka") + "; IOT;");
            System.out.println("iot");

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
                System.out.println(numberOfBytes = Integer.parseInt(m.group(2)));
            }

        } else {
            System.out.println("Ne odgovara!");
        }
        return status;
    }

    private void obradaOdgovora(ByteArrayOutputStream baos) {
        String str = new String(baos.toByteArray());
        if (testInputStringAndExtractChasterAdnSize(str, sintaksaAdminEvidencijaIot)) {
            System.out.println("Vraćen je odgovor sa datotekom:  " + charset + "\n" + numberOfBytes);
            System.out.println("ODGOVOR:" + str);
            pohranaDatotekeUZadanomFormatu(baos);
        } else {
            System.out.println("buffer: " + str);
        }

    }

    private void pohranaDatotekeUZadanomFormatu(ByteArrayOutputStream baos) {
        String nazivDatoteke;
        if (upisaniArgumenti.containsKey("datotekaEvidencija")) {
            nazivDatoteke = upisaniArgumenti.getProperty("datotekaEvidencija");
        } else if (upisaniArgumenti.containsKey("datotekaIot")) {
            nazivDatoteke = upisaniArgumenti.getProperty("datotekaIot");
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

}
