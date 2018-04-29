/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ws.serveri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.podaci.Parkiraliste;
import org.foi.nwtis.zorhrncic.web.slusaci.SlusacAplikacije;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author Zoran
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {


    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private String usernameAdmin;
    private String lozinka;
    private String url;
    private String upit;
    private String uprProgram;
   

    public GeoMeteoWS() {
     
        preuzmiKonfiuraciju();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajSvaParkiralista")
    public java.util.List<Parkiraliste> dajSvaParkiralista() {
        //TODO
        /*
        dajSvaParkiralista() 
        - vraća List<Parkiraliste> sa svim parkiralištima 
        koja se nalaze u tablici PARKIRALISTA 
         */
        java.util.List<Parkiraliste> svaParkiralista = new ArrayList<>();
        int i = 0;
        svaParkiralista.add(new Parkiraliste(i++, "Podzemna garaĹľa", "Kapucinski trg 1, VaraĹľdin", new Lokacija("", "")));
        svaParkiralista.add(new Parkiraliste(i++, "FOI 1", "HabdeliÄ‡eva 3, VaraĹľdin", new Lokacija("", "")));
        svaParkiralista.add(new Parkiraliste(i++, "FOI 2", "Petra KreĹˇimira IV 15, VaraĹľdin", new Lokacija("", "")));
        return svaParkiralista;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dodajParkiraliste")
    public void dodajParkiraliste(@WebParam(name = "parkiraliste") Parkiraliste parkiraliste) {
        //TODO 
        /*
        dodajParkiraliste(Parkiraliste)  
        - dodaje novo parkiralište u tablicu PARKIRALISTA 
         */
        //return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajSveMeteoPodatke")
    public java.util.List<MeteoPodaci> dajSveMeteoPodatke(@WebParam(name = "id") int id) {
        //TODO 
        /*
        o	dajSveMeteoPodatke(int) 
        - vraća sve spremljene podatke iz baze podataka za uneseno 
        parkirališta, ukoliko nema podataka vraća null
         */
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajSveMeteoPodatke_1")
    @RequestWrapper(className = "org.dajSveMeteoPodatke_1")
    @ResponseWrapper(className = "org.dajSveMeteoPodatke_1Response")
    public java.util.List<MeteoPodaci> dajSveMeteoPodatke(@WebParam(name = "id") int id, @WebParam(name = "form") long form, @WebParam(name = "to") long to) {
        //TODO 
        /*
        o	dajSveMeteoPodatke(int, long, long) 
        - vraća sve spremljene podatke iz baze podataka za 
        uneseno parkirališta i interval (timestamp), ukoliko nema 
        podataka vraća null
         */
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajZadnjeMeteoPodatke")
    public MeteoPodaci dajZadnjeMeteoPodatke(@WebParam(name = "id") int id) {
        //TODO 
        /*
        o	dajZadnjeMeteoPodatke(int) - vraća posljednje 
        spremljene meteo podatake 
        iz baze podatka za uneseno parkiralište ukoliko 
        nema podataka vraća null
         */

        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajVazeceMeteoPodatke")
    public MeteoPodaci dajVazeceMeteoPodatke(@WebParam(name = "id") int id) {
        //TODO
        /*
       	dajVazeceMeteoPodatke(int) 
        - vraća važeće meteo podatake na bazi 
        web servisa  za uneseno parkiralište ukoliko 
        nema podataka vraća null
         */
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajMinMaxTemp")
    public String dajMinMaxTemp(@WebParam(name = "id") int id, @WebParam(name = "from") long from, @WebParam(name = "to") long to) {
        //TODO 
        /*o	dajMinMaxTemp(int, long, long) - 
        vraća min i max važeće temperature iz baze podatka 
        za uneseno parkiralište i interval, ukoliko nema 
        podataka vraća null*/
        return null;
    }

    private void preuzmiKonfiuraciju() {
/*
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data

        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza

        usernameAdmin = konfiguracijaBaza.getAdminUsername();
        lozinka = konfiguracijaBaza.getAdminPassword();
        url = konfiguracijaBaza.getServerDatabase() + konfiguracijaBaza.getAdminUsername();
        uprProgram = konfiguracijaBaza.getDriverDatabase();*/

    }

    private ResultSet executeQuery(String upit) {
        ResultSet result = null;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            System.out.println("GREŠKA: " + ex.getMessage());
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {

            /* while (rs.next()) {
                String mb = rs.getString("kor_ime");
                String pr = rs.getString("prezime");
                String im = rs.getString("ime");

            }*/
            result = rs;
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
        return result;
    }

}
