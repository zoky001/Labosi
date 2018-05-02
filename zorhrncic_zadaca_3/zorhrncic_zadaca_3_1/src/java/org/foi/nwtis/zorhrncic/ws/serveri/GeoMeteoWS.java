/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ws.serveri;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.podaci.Parkiraliste;
import org.foi.nwtis.zorhrncic.web.slusaci.SlusacAplikacije;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.foi.nwtis.zorhrncic.rest.klijenti.GMKlijent;
import org.foi.nwtis.zorhrncic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.zorhrncic.rest.serveri.MeteoREST;

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

    @Resource
    private WebServiceContext context;
    private String OWM_apikey;
    private String gm_apiKey;
    private String patternDateTimeSQL = "yyyy-MM-dd HH:mm:ss";

    public GeoMeteoWS() {
        //preuzmiKonfiuraciju();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajSvaParkiralista")
    public java.util.List<Parkiraliste> dajSvaParkiralista() {
        preuzmiKonfiuraciju();
        java.util.List<Parkiraliste> svaParkiralista = getArrayallParkingDataFromDatabase();
        return svaParkiralista;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dodajParkiraliste")
    public boolean dodajParkiraliste(@WebParam(name = "parkiraliste") Parkiraliste parkiraliste) {
        preuzmiKonfiuraciju();
        //TODO 
        try {

            if (!checkIfExistParkingByName(parkiraliste.getNaziv())) {
                return addParkingnInDatabase(parkiraliste.getNaziv(), parkiraliste.getAdresa(), parkiraliste.getGeoloc().getLatitude(), parkiraliste.getGeoloc().getLongitude());

            }
        } catch (Exception e) {
            return false;
        }
        return false;
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
        preuzmiKonfiuraciju();
        List<MeteoPodaci> list = createMeteodata_ByParkingID(id);

        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list;
        }
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
        preuzmiKonfiuraciju();
        List<MeteoPodaci> list = createMeteodataInRange_ByParkingID(id, form, to);
        if (form > to) {
            return null;
        }

        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list;
        }
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
        preuzmiKonfiuraciju();
        List<MeteoPodaci> list = createLastMeteodataByParkingID(id);

        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
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
        preuzmiKonfiuraciju();
        if (!checkIfExistParkingByID(id)) {
            return null;
        }
        MeteoPodaci meteo = getCurrentValidMeteoDataByParkingID(id);

        if (meteo == null) {
            return null;
        } else {
            return meteo;
        }

    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajMinMaxTemp")
    public ArrayList<Float> dajMinMaxTemp(@WebParam(name = "id") int id, @WebParam(name = "from") long from, @WebParam(name = "to") long to) {
        //TODO 
        /*o	dajMinMaxTemp(int, long, long) - 
        vraća min i max važeće temperature iz baze podatka 
        za uneseno parkiralište i interval, ukoliko nema 
        podataka vraća null*/
        preuzmiKonfiuraciju();
        ArrayList<Float> list = getMinMaxTempValueInRange_ByParkingID(id, from, to);
        if (from > to) {
            return null;
        }

        if (list == null || list.size() != 2) {
            return null;
        } else {
            return list;
        }
    }

    private void preuzmiKonfiuraciju() {
        ServletContext servletContext
                = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        konfiguracija = (Konfiguracija) SlusacAplikacije.getServletContext().getAttribute("All_Konfig");//all config data
        konfiguracijaBaza = (BP_Konfiguracija) SlusacAplikacije.getServletContext().getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        usernameAdmin = konfiguracijaBaza.getAdminUsername();
        lozinka = konfiguracijaBaza.getAdminPassword();
        url = konfiguracijaBaza.getServerDatabase() + konfiguracijaBaza.getAdminDatabase();
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        gm_apiKey = konfiguracija.dajPostavku("apikey");
        OWM_apikey = konfiguracija.dajPostavku("OWM_apikey");
    }

    private java.util.List<Parkiraliste> getArrayallParkingDataFromDatabase() {
        String upit = "SELECT * FROM PARKIRALISTA";
        java.util.List<Parkiraliste> svaParkiralista = null;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet results = stmt.executeQuery(upit);) {
            svaParkiralista = createParkiralistaArrayFromResultset(results);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return svaParkiralista;
    }

    private java.util.List<Parkiraliste> createParkiralistaArrayFromResultset(ResultSet results) {
        java.util.List<Parkiraliste> svaParkiralista = new ArrayList<>();
        if (results != null) {
            try {
                while (results.next()) {
                    String latitude = results.getString("LATITUDE");
                    String longitude = results.getString("LONGITUDE");
                    String id = results.getString("ID");
                    String naziv = results.getString("NAZIV");
                    String adresa = results.getString("ADRESA");
                    svaParkiralista.add(new Parkiraliste(Integer.parseInt(id), naziv, adresa, new Lokacija(latitude, longitude)));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return svaParkiralista;

    }

    private boolean checkIfExistParkingByName(String name) {
        String upit = "SELECT * FROM PARKIRALISTA WHERE NAZIV = '" + name + "'";
        boolean exist = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {

        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                exist = true;
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return exist;
        }
    }

    private boolean checkIfExistParkingByID(int id) {
        String upit = "SELECT * FROM PARKIRALISTA WHERE ID =" + id;
        boolean exist = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {

        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                exist = true;
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return exist;
        }
    }

    private boolean addParkingnInDatabase(String name, String address, String lat, String lon) {
        String upit = "INSERT INTO PARKIRALISTA (NAZIV, ADRESA, LATITUDE, LONGITUDE) \n"
                + "	VALUES ('" + name + "', '" + address + "', " + lat + ", " + lon + ")";
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

    private MeteoPodaci getCurrentValidMeteoDataByParkingID(int id) {

        MeteoPodaci meteo = null;
        String upit = "SELECT * FROM PARKIRALISTA where ID = " + id;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet results = stmt.executeQuery(upit);) {
            meteo = getCurrentValidaMeteoDataByResultSet(results);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return meteo;
    }

    private List<MeteoPodaci> createLastMeteodataByParkingID(int id) {

        List<MeteoPodaci> nesto = null;
        DateFormat df = new SimpleDateFormat(patternDateTimeSQL);
        String upit = "SELECT * FROM METEO where ID = " + id + " ORDER BY PREUZETO DESC FETCH FIRST 1 ROWS ONLY";
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet results = stmt.executeQuery(upit);) {
            nesto = createMeteoPodatciArrayFromResultset_Weather(results);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return nesto;
    }

    private List<MeteoPodaci> createMeteodataInRange_ByParkingID(int id, long from, long to) {
        preuzmiKonfiuraciju();
        List<MeteoPodaci> nesto = null;
        DateFormat df = new SimpleDateFormat(patternDateTimeSQL);
        String upit = "SELECT * FROM METEO where ID = " + id + " AND PREUZETO BETWEEN  '" + df.format(new Date(from)) + "' AND '" + df.format(new Date(to)) + "' ORDER BY PREUZETO DESC";
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet results = stmt.executeQuery(upit);) {
            nesto = createMeteoPodatciArrayFromResultset_Weather(results);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return nesto;
    }

    private List<MeteoPodaci> createMeteodata_ByParkingID(int id) {
        preuzmiKonfiuraciju();
        List<MeteoPodaci> nesto = null;
        String upit = "SELECT * FROM METEO where ID = " + id + " ORDER BY PREUZETO DESC";
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet results = stmt.executeQuery(upit);) {
            nesto = createMeteoPodatciArrayFromResultset_Weather(results);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return nesto;
    }

    private List<MeteoPodaci> createMeteoPodatciArrayFromResultset_Weather(final ResultSet results) throws SQLException {
        List<MeteoPodaci> arrayList = new ArrayList<>();
        if (results != null) {
            while (results.next()) {
                String latitude = results.getString("LATITUDE");
                String longitude = results.getString("LONGITUDE");
                String vrijeme = results.getString("VRIJEME");
                String adresaStanice = results.getString("ADRESASTANICE");
                String vrijemeOpis = results.getString("VRIJEMEOPIS");
                String temp = results.getString("TEMP");
                String tempMin = results.getString("TEMPMIN");
                String tempMax = results.getString("TEMPMAX");
                String vlaga = results.getString("VLAGA");
                String tlak = results.getString("TLAK");
                String vjetar = results.getString("VJETAR");
                String vjetarSmjer = results.getString("VJETARSMJER");
                String preuzeto = results.getString("PREUZETO");
                String idmeto = results.getString("IDMETEO");
                //TODO staviti pravi datum
                createMeteoPodatciObject(preuzeto, arrayList, temp, tempMin, tempMax, vlaga, tlak, vjetar, vjetarSmjer, vrijeme, vrijemeOpis, idmeto);

            }
        }
        return arrayList;
    }

    private ArrayList<Float> getMinMAxTempValueFromResultSet(final ResultSet results) throws SQLException {
        ArrayList<Float> arrayList = new ArrayList<>();
        if (results != null) {
            while (results.next()) {
                String min = results.getString("MIN");
                String max = results.getString("MAX");
                arrayList.add(Float.valueOf(min));
                arrayList.add(Float.valueOf(max));

            }
        }
        return arrayList;
    }

    private MeteoPodaci getCurrentValidaMeteoDataByResultSet(final ResultSet results) throws SQLException {
        MeteoPodaci meteo = null;
        if (results != null) {
            while (results.next()) {
                String latitude = results.getString("LATITUDE");
                String longitude = results.getString("LONGITUDE");
                Lokacija lok = new Lokacija(latitude, longitude);
                OWMKlijent owmk = new OWMKlijent(OWM_apikey);
                meteo = owmk.getRealTimeWeather(lok.getLatitude(), lok.getLongitude());
            }
        }
        return meteo;
    }

    private void createMeteoPodatciObject(String preuzeto, List<MeteoPodaci> arrayList, String temp, String tempMin, String tempMax, String vlaga, String tlak, String vjetar, String vjetarSmjer, String vrijeme, String vrijemeOpis, String idmeto) throws NumberFormatException {
        try {
            DateFormat df = new SimpleDateFormat(patternDateTimeSQL);
            Date preuzetoDate = df.parse(preuzeto);
            arrayList.add(new MeteoPodaci(
                    new Date(), //sunRise
                    new Date(), //sunSet
                    Float.parseFloat(temp), //tempreatureVlue
                    Float.parseFloat(tempMin),//temperatureMinValue
                    Float.parseFloat(tempMax), //temperatureAMxValue
                    "°C", //temperatureUnit
                    Float.parseFloat(vlaga), //humidity value
                    "%", //humidityUnit
                    Float.parseFloat(tlak), //pressureValue
                    "hPa", //pressureUnit
                    Float.parseFloat(vjetar), //windSpeedValue
                    "m/s", //windSpeedName
                    Float.parseFloat(vjetarSmjer), //windDirectionValue
                    "", //windDirectionCode
                    "", //windDirectionNAme
                    0, //cloud value
                    "", //cloudName
                    vrijeme, //visibility
                    Float.NaN, //precipationValue
                    vrijemeOpis, //precipation mode
                    "", //precipationUnit
                    Integer.parseInt(idmeto), //weather number
                    "", //weatherValue
                    "", //weatherIcon
                    preuzetoDate)//last update
            );

        } catch (ParseException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<Float> getMinMaxTempValueInRange_ByParkingID(int id, long from, long to) {
        ArrayList<Float> nesto = null;
        DateFormat df = new SimpleDateFormat(patternDateTimeSQL);
        String upit = "SELECT MIN(TEMPMIN) as \"MIN\", MAX(TEMPMAX) as \"MAX\" FROM METEO where ID = " + id + " AND PREUZETO BETWEEN  '" + df.format(new Date(from)) + "' AND '" + df.format(new Date(to)) + "'";

        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet results = stmt.executeQuery(upit);) {
            nesto = getMinMAxTempValueFromResultSet(results);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return nesto;
    }

}
