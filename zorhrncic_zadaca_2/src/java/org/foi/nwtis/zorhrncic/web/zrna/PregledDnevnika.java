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
    private int ukupanBrojZapisa, brojZapisaZaPrikaz, pozicijaOd = 0, pozicijaDo;
    private String odDatuma, doDatuma;
     private Date fromDate, toDate;
    private String datoteka;
    private BP_Konfiguracija konfiguracija;
    private String usernameAdmin;
    private String lozinka;
    private String url;
    private String upit;
    private String uprProgram;

    /**
     * Creates a new instance of PregledDnevnika
     */
    public PregledDnevnika() {
        //preuzmiZapise();
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
        datoteka = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("konfiguracija");
        String putanja = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF") + java.io.File.separator;
        konfiguracija = new BP_Konfiguracija(putanja + datoteka);

        usernameAdmin = konfiguracija.getUserUsername();
        lozinka = konfiguracija.getUserPassword();
        url = konfiguracija.getServerDatabase() + konfiguracija.getUserDatabase();
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
          
            
        
        upit = "SELECT * FROM `dnevnik` WHERE `vrijeme` > '"+df.format(fromDate)+"'";// + df.format(fromDate)+"'";
        uprProgram = konfiguracija.getDriverDatabase();

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
                String id = rs.getString("id");
                String sadrzaj = rs.getString("sadrzaj");
                String vrijeme = rs.getString("vrijeme");

                String pattern = "yyyy-MM-dd H:m:s";

               df = new SimpleDateFormat(pattern);
                Date today = df.parse(vrijeme);

                preuzetiZapisniciDnevnika.add(new Dnevnik(Integer.valueOf(id), sadrzaj, today));
                ukupanBrojZapisa++;
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
            String s = (String)odDatuma;
            String pattern = "dd.MM.yyyy";
            
            DateFormat df = new SimpleDateFormat(pattern);
            Date today = df.parse(s);
            this.fromDate = today;
            
            this.odDatuma = s;//today;
        } catch (ParseException ex) {
            Logger.getLogger(PregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        preuzmiZapise();
    }

    public String getOdDatuma() {
        return odDatuma;
    }

    public String getDoDatuma() {
        return doDatuma;
    }

    public void setDoDatuma(String doDatuma) {
        this.doDatuma = doDatuma;
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
         preuzmiZapise();
    }

    public void setPozicijaDo(int pozicijaDo) {
        this.pozicijaDo = pozicijaDo;
        
    }

    //navigacija
    public String promjenaIntervala() {
        return "PromjenaIntervala";
    }

    public String prethodniZapisi() {
        return "PrethodniZapisi";
    }

    public String sljedeciZapisi() {
        return "SljedeciZapisi";
    }

    public String promjenaJezika() {
        return "promjenaJezika";
    }

    public String saljiPoruku() {
        return "saljiPoruku";
    }

    public String pregledPoruka() {
        return "pregledPoruka";
    }

}
