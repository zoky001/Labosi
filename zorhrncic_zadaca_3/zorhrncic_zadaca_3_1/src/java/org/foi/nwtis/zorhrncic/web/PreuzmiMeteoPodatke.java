/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeBodyPart;
import javax.servlet.ServletContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.slusaci.SlusacAplikacije;

/**
 *
 * Dretva koja se okida nakon svakog intervala i obavlja sortiranje poruka u
 * mape i pohranjivanje IOT uredjaja u bazu podataka.
 *
 * @author Zoran Hrncic
 */
public class PreuzmiMeteoPodatke extends Thread {

    private String formatIspisa = "|%-25s|%-25s|\n";
    private String line20 = "-------------------------";
    private boolean krajRada = false;
    private int spavanje;
    private Folder folder;
    private Store store;
    private Session session;
    private String adresaServera;
    private String korisnickoIme;
    private String lozinka;
    private Message[] messages = null;
    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private long razlika = 0;
    private long pocetak = 0;
    private long kraj = 0;
    private double koef = 0.01666666666;
    private int portServera;
    private int brojPorukaZaUcitavanje;
    private String nazivAttachmenta;
    private String nazivMape;
    private String nazivDatotekePodatciRadu;
    private long krajObrade;
    private int brojPoruka = 0, brojdodanihIOT = 0, brojAzuriranihIOT = 0, brojNeispravnihPoruka = 0;
    private final String protokol = "imap";
    private int iot_id;
    private String iot_command;
    private Date iot_time;
    private Properties iot_atributs;
    private String upit;
    private String uprProgram;
    private String usernameAdminDatabase;
    private String lozinkaDatabase;
    private String urlDatabase;

    private String patternDateTimeSQL = "yyyy-MM-dd H:m:s";
    private final SimpleDateFormat sqlDateFormat;

    private long sleepTime;


    /**
     * Pokrece pruzimanje podataka iz konfiguracije. Definira format datuma SQL
     * baze
     *
     */
    public PreuzmiMeteoPodatke() {
        preuzmiKonfiuraciju();
        sqlDateFormat = new SimpleDateFormat(patternDateTimeSQL);
    }

    /**
     * Preuzima konfiguraciju iz kontexta i pohranjije potrebne podatke u
     * globalne vrijable.
     *
     */
    private void preuzmiKonfiuraciju() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data
       /* spavanje = Integer.parseInt(konfiguracija.dajPostavku("mail.timeSecThreadCycle"));
        adresaServera = konfiguracija.dajPostavku("mail.server");
        portServera = Integer.parseInt(konfiguracija.dajPostavku("mail.imap.port"));
        korisnickoIme = konfiguracija.dajPostavku("mail.usernameThread");
        lozinka = konfiguracija.dajPostavku("mail.passwordThread");
        brojPorukaZaUcitavanje = Integer.parseInt(konfiguracija.dajPostavku("mail.numMessagesToRead"));
        nazivAttachmenta = konfiguracija.dajPostavku("mail.attachmentFilename");
        nazivMape = konfiguracija.dajPostavku("mail.folderNWTiS");
        nazivDatotekePodatciRadu = konfiguracija.dajPostavku("mail.threadCycleLogFilename");
        usernameAdminDatabase = konfiguracijaBaza.getUserUsername();
        lozinkaDatabase = konfiguracijaBaza.getUserPassword();
        urlDatabase = konfiguracijaBaza.getServerDatabase() + konfiguracijaBaza.getUserDatabase();  */
    }

    @Override
    public void interrupt() {
        krajRada = true;
        super.interrupt();

    }

    /**
     * pokrece obradu i odredjuje vrijeme NERADA dretve
     *
     */
    @Override
    public void run() {
        int broj = 0;
        while (!krajRada) {
            pocetak = System.currentTimeMillis();
            
            if (kraj != 0) {
                System.out.println("Razlika od prosle serijalizacije: " + (pocetak - kraj) / 1000 + " sec");
            }
            try {
                System.out.println("Gotova obrada: " + broj++);
                
                //radi
                kraj = System.currentTimeMillis();
                razlika = kraj - pocetak;
                sleepTime = (spavanje * 1000 - razlika) + (long) (koef * (spavanje * 1000 - razlika));
                if (sleepTime < 0) {
                    sleepTime = 0;
                }
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                System.out.println("org.foi.nwtis.zorhrncic.web.dretve.ObradaPoruka.run(): " + e.getMessage());
            }
        }

    }

   

    @Override
    public synchronized void start() {
        super.start();
    }

  

}
