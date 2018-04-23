/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import static com.oracle.wls.shaded.org.apache.xalan.lib.ExsltDatetime.date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.kontrole.Dnevnik;

/**
 *
 * @author Zoran
 */
@Named(value = "pregledDnevnika")
@RequestScoped
public class PregledDnevnika {

    private List<Dnevnik> preuzetiZapisniciDnevnika;
    //TODO broj zapisa za prikazati
    //TODO renderiranje buttona
    private int ukupanBrojZapisa, brojZapisaZaPrikaz = 5, pozicijaOd = 0, pozicijaDo = 0;
    private String odDatuma, doDatuma;
    private Date fromDate, toDate;
    private String datoteka;
    private BP_Konfiguracija konfiguracijaBaza;
    private String usernameAdmin;
    private String lozinka;
    private String url;
    private String upit;
    private String uprProgram;
    private String patternDateTimeSQL = "yyyy-MM-dd H:m:s";
    private Konfiguracija konfiguracija;

    /**
     * Creates a new instance of PregledDnevnika
     */
    public PregledDnevnika() {
        preuzmiKonfiuraciju();
        //preuzmiZapise();
    }

    private void preuzmiKonfiuraciju() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data
        
        usernameAdmin = konfiguracijaBaza.getUserUsername();
        lozinka = konfiguracijaBaza.getUserPassword();
        url = konfiguracijaBaza.getServerDatabase() + konfiguracijaBaza.getUserDatabase();
        brojZapisaZaPrikaz = Integer.parseInt(konfiguracija.dajPostavku("mail.numLogItemsToShow"));
    }

    void preuzmiZapise() {
        preuzetiZapisniciDnevnika = new ArrayList<>();
        /*
        koja preuzima zapise iz tablice DNEVNIK u bazi podataka na temelju intervala Od Do. Za vježbe se kreira 5 zapisa pomoću linije koju kasnije treba zamijeniti kodom koji će preuzeti stvarne zapise
o	new Dnevnik(Integer.toString(i++), "{'id': 1, 'komanda': 'dodaj', 'naziv': 'Senzor temperature', 'vrijeme': '2018.04.08 11:20:45'}");

         */

        //TODO preuzeti podatke iz baze
        int i = 0;
        /* preuzetiZapisniciDnevnika.add(new Dnevnik(i++, "{'id': 1, 'komanda': 'dodaj', 'naziv': 'Senzor temperature', 'vrijeme': '2018.04.08 11:20:45'}", new Date()));
        preuzetiZapisniciDnevnika.add(new Dnevnik(i++, "{'id': 1, 'komanda': 'dodaj', 'naziv': 'Senzor temperature', 'vrijeme': '2018.04.08 11:20:45'}", new Date()));
        preuzetiZapisniciDnevnika.add(new Dnevnik(i++, "{'id': 1, 'komanda': 'dodaj', 'naziv': 'Senzor temperature', 'vrijeme': '2018.04.08 11:20:45'}", new Date()));
        preuzetiZapisniciDnevnika.add(new Dnevnik(i++, "{'id': 1, 'komanda': 'dodaj', 'naziv': 'Senzor temperature', 'vrijeme': '2018.04.08 11:20:45'}", new Date()));
         */
        DateFormat df = new SimpleDateFormat(patternDateTimeSQL);

        if (fromDate == null || toDate == null) {
            return;
        }
        upit = "SELECT * FROM `dnevnik` WHERE `vrijeme` > '" + df.format(fromDate) + "' AND `vrijeme` < '" + df.format(toDate) + "' ORDER BY `vrijeme` DESC";
        uprProgram = konfiguracijaBaza.getDriverDatabase();

        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            ukupanBrojZapisa = 0;
            while (rs.next()) {
                ukupanBrojZapisa++;
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());

        }

        upit = "SELECT * FROM `dnevnik` WHERE `vrijeme` > '" + df.format(fromDate) + "' AND `vrijeme` < '" + df.format(toDate) + "' ORDER BY `vrijeme` DESC LIMIT " + pozicijaOd + "," + brojZapisaZaPrikaz;

        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {

            while (rs.next()) {
                String id = rs.getString("id");
                String sadrzaj = rs.getString("sadrzaj");
                String vrijeme = rs.getString("vrijeme");

                df = new SimpleDateFormat(patternDateTimeSQL);
                Date today = df.parse(vrijeme);

                preuzetiZapisniciDnevnika.add(new Dnevnik(Integer.valueOf(id), sadrzaj, today));
                pozicijaDo++;
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());

        }
    }

    //getter and setter
    public void setOdDatuma(String odDatuma) {
        try {
            String s = (String) odDatuma;
            String pattern = "dd.MM.yyyy H:m:s";

            DateFormat df = new SimpleDateFormat(pattern);
            Date today = df.parse(s);
            this.fromDate = today;

            this.odDatuma = s;//today;
        } catch (ParseException ex) {
            Logger.getLogger(PregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getOdDatuma() {
        return odDatuma;
    }

    public String getDoDatuma() {
        return doDatuma;
    }

    public void setDoDatuma(String doDatuma) {
        try {
            String s = (String) doDatuma;
            String pattern = "dd.MM.yyyy H:m:s";

            DateFormat df = new SimpleDateFormat(pattern);
            Date today = df.parse(s);
            this.toDate = today;

            this.doDatuma = s;//today;
        } catch (ParseException ex) {
            Logger.getLogger(PregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getBrojZapisaZaPrikaz() {

        return brojZapisaZaPrikaz;
    }

    public void setBrojZapisaZaPrikaz(int brojZapisaZaPrikaz) {
        this.brojZapisaZaPrikaz = brojZapisaZaPrikaz;
    }

    public List<Dnevnik> getPreuzetiZapisniciDnevnika() {
        return preuzetiZapisniciDnevnika;
    }

    public int getPozicijaOd() {
        return pozicijaOd;
    }

    public int getUkupanBrojZapisa() {
        return ukupanBrojZapisa;
    }

    public void setUkupanBrojZapisa(int ukupanBrojZapisa) {
        this.ukupanBrojZapisa = ukupanBrojZapisa;
    }

    public int getPozicijaDo() {
        return pozicijaDo;
    }

    public void setPozicijaOd(int pozicijaOd) {

        this.pozicijaOd = pozicijaOd;

    }

    public void setPozicijaDo(int pozicijaDo) {
        this.pozicijaDo = pozicijaDo;

    }

    //navigacija
    public String promjenaIntervala() {
        pozicijaDo = 0;
        pozicijaOd = 0;
        preuzmiZapise();
        return "PromjenaIntervala";
    }

    public String prethodniZapisi() {

        pozicijaOd = pozicijaOd - brojZapisaZaPrikaz;

        if (pozicijaOd < 0) {
            pozicijaOd = 0;
        }
        pozicijaDo = pozicijaOd;
        preuzmiZapise();
        return "PrethodniZapisi";
    }

    public String sljedeciZapisi() {
        if (pozicijaDo < ukupanBrojZapisa) {
            pozicijaOd = pozicijaDo;
        }

        pozicijaDo = pozicijaOd;

        preuzmiZapise();
        return "SljedeciZapisi";
    }

    public String promjenaJezika() {
        return "promjenaJezika";
    }

    public String saljiPoruku() {
        return "saljiPoruke";
    }

    public String pregledPoruka() {
        return "pregledPoruka";
    }

}
