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
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.podaci.GMKlijent;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.podaci.OWMKlijent;
import org.foi.nwtis.zorhrncic.web.podaci.Parkiraliste;

/**
 *
 * @author grupa_1
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {

    private BP_Konfiguracija konfiguracija;

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajSvaParkiralista")
    public java.util.List<Parkiraliste> dajSvaParkiralista() {

        java.util.List<Parkiraliste> svaParkiralista = new ArrayList<Parkiraliste>();
        int i = 0;
        svaParkiralista.add(new Parkiraliste(i++, "Podzemna garaža", "kapucinski Trg 1", new Lokacija("", "")));
        svaParkiralista.add(new Parkiraliste(i++, "FOI 1", "Habdelićeva 3", new Lokacija("", "")));
        svaParkiralista.add(new Parkiraliste(i++, "FOI 2", "UL Petra Krešimira IV 15", new Lokacija("", "")));
        return svaParkiralista;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajSveMeteoPodatke")
    public Parkiraliste dajSveMeteoPodatke(@WebParam(name = "id") int id, @WebParam(name = "from") long from, @WebParam(name = "to") long to) {
        //TODO write your implementation code here:
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajVazeceMeteoPodatke")
    public MeteoPodaci dajVazeceMeteoPodatke(@WebParam(name = "id") int id) {
        //TODO write your implementation code here:
        String naziv, adresa;// = get
    

        /*konfiguracija = (BP_Konfiguracija) //this.c.getAttribute("BP_Konfig");
        if (konfiguracija == null) {
            return;
        }
        usernameAdmin = konfiguracija.getUserUsername();
        lozinka = konfiguracija.getUserPassword();
        url = konfiguracija.getServerDatabase() + konfiguracija.getUserDatabase();
        upit = "INSERT INTO parkiralista (naziv,adresa,latitude,longitude) "
                + "VALUES (" + naziv + "," + adresa + "," + lok.getLatitude() + "," + lok.getLongitude() + ")";
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {

            System.err.println("Popis: ");

            while (rs.next()) {
                String mb = rs.getString("kor_ime");
                String pr = rs.getString("prezime");
                String im = rs.getString("ime");
                System.out.println(mb + " " + pr + " " + im);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
        
        
        OWMKlijent owmk = new OWMKlijent("7505f1b2a843433f4c408932f2d4300d");
        MeteoPodaci met = owmk.getRealTimeWeather(lok.getLatitude(), lok.getLongitude());*/
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajZadnjeMeteoPodatke")
    public MeteoPodaci dajZadnjeMeteoPodatke(@WebParam(name = "id") int id) {
        //TODO write your implementation code here:
        return null;
    }

}
