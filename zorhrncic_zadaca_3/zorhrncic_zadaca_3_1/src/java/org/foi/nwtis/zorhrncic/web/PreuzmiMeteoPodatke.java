/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.ServletContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.zorhrncic.rest.serveri.MeteoREST;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.podaci.Parkiraliste;
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

    private String patternDateTimeSQL = "yyyy-MM-dd HH:mm:ss";

    private final SimpleDateFormat sqlDateFormat;

    private long sleepTime;
    private String usernameAdmin;
    private String url;
    private String OWM_apikey;
    private String gm_apiKey;

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

        ServletContext servletContext = (ServletContext) SlusacAplikacije.getServletContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data
        usernameAdmin = konfiguracijaBaza.getAdminUsername();
        lozinka = konfiguracijaBaza.getAdminPassword();
        url = konfiguracijaBaza.getServerDatabase() + konfiguracijaBaza.getAdminDatabase();
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        gm_apiKey = konfiguracija.dajPostavku("apikey");
        OWM_apikey = konfiguracija.dajPostavku("OWM_apikey");
        spavanje = Integer.parseInt(konfiguracija.dajPostavku("intervalDretveZaMeteoPodatke"));

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
                processing();
                kraj = System.currentTimeMillis();
                razlika = kraj - pocetak;
                sleepTime = (spavanje * 1000 - razlika) + (long) (koef * (spavanje * 1000 - razlika));
                if (sleepTime < 0) {
                    sleepTime = 0;
                }
                System.out.println("Gotova obrada: " + broj++);
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

    private void processing() {
        try {
            List<Parkiraliste> parkiralistes = getAllParkingData();
            if (parkiralistes != null) {
                for (Parkiraliste parkiraliste : parkiralistes) {
                    try {
                        MeteoPodaci meteoPodaci = getMeteoData(parkiraliste.getGeoloc());
                        addMeteoDataInDatabase(meteoPodaci, parkiraliste);
                    } catch (Exception e) {
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("GREŠKA: " + e.getMessage());
        }

    }

    private List<Parkiraliste> getAllParkingData() {
        List<Parkiraliste> nesto = null;
        String upit = "SELECT * FROM PARKIRALISTA";
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet results = stmt.executeQuery(upit);) {
            nesto = createArrayFromResultset(results);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return nesto;
    }

    private List<Parkiraliste> createArrayFromResultset(ResultSet results) {
        List<Parkiraliste> list = new ArrayList<>();
        if (results != null) {
            try {
                while (results.next()) {
                    String latitude = results.getString("LATITUDE");
                    String longitude = results.getString("LONGITUDE");
                    String id = results.getString("ID");
                    String naziv = results.getString("NAZIV");
                    String adresa = results.getString("ADRESA");
                    list.add(new Parkiraliste(Integer.parseInt(id), naziv, adresa, new Lokacija(latitude, longitude)));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    private MeteoPodaci getMeteoData(Lokacija lok) {
        OWMKlijent owmk = new OWMKlijent(OWM_apikey);
        return owmk.getRealTimeWeather(lok.getLatitude(), lok.getLongitude());
    }

    private boolean addMeteoDataInDatabase(MeteoPodaci meteoPodaci, Parkiraliste p) {
        DateFormat df = new SimpleDateFormat(patternDateTimeSQL);

        String upit = "INSERT INTO METEO "
                + "(ID, ADRESASTANICE, LATITUDE, LONGITUDE, VRIJEME, VRIJEMEOPIS, TEMP, TEMPMIN, TEMPMAX, VLAGA, TLAK, VJETAR, VJETARSMJER, PREUZETO)"
                + "VALUES "
                + "(" + p.getId() + ", '', " + p.getGeoloc().getLatitude() + ", " + p.getGeoloc().getLongitude() + ", '" + meteoPodaci.getWeatherValue() + "' , '" + meteoPodaci.getPrecipitationMode() + "', " + meteoPodaci.getTemperatureValue() + ","
                + " " + meteoPodaci.getTemperatureMin() + ", " + meteoPodaci.getTemperatureMax() + ", " + meteoPodaci.getHumidityValue() + ", " + meteoPodaci.getPressureValue() + ", " + meteoPodaci.getWindSpeedValue() + ", " + meteoPodaci.getWindDirectionValue() + ", '" + df.format(meteoPodaci.getLastUpdate()) + "')";

        boolean success = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {

        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();) {
            stmt.execute(upit);
            success = true;
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return success;
        }

    }

}
