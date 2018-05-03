/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.rest.serveri;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.rest.klijenti.GMKlijent;
import org.foi.nwtis.zorhrncic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.slusaci.SlusacAplikacije;

/**
 * REST Web Service
 *
 * Vrši obradu podataka parkirališta. Upis, dohvačanje svih iz baze i vračanje
 * podataka, vračanje podataka vremena na lokacijama parkirališta.
 *
 * @author Zoran Hrnčić
 */
@Path("meteo")
public class MeteoREST {

    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private String usernameAdmin;
    private String lozinka;
    private String url;
    private String uprProgram;
    private String json;
    private String gm_apiKey;
    private String OWM_apikey;

    /**
     * Creates a new instance of MeteoREST
     */
    public MeteoREST() {
        preuzmiKonfiuraciju();
    }

    /**
     * Vraća sve zapise iz baze podataka iz tablice "parkirališta".
     *
     * Retrieves representation of an instance of
     * org.foi.nwtis.zorhrncic.rest.serveri.MeteoREST
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        json = "";
        JsonArrayBuilder allParkingData = createArrayJSONdata_allParkingData();
        if (allParkingData != null) {
            json = Json.createObjectBuilder()
                    .add("odgovor", allParkingData
                    )
                    .add("status", "OK")
                    .build()
                    .toString();
        } else {
            //with error
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "ERR")
                    .add("poruka", "Dogodila se pogreška prilikom dohvaćanja podataka")
                    .build()
                    .toString();
        }

        return json;
    }

    /**
     * Upisuje nove podatke o novom parkiralištu u bazu podataka.
     *
     * Vrača ERR ako postojji parkiraliste sa istim nazivom.
     *
     * PUT method for updating or creating an instance of MeteoREST
     *
     * @param content representation for the resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postJson(String content) {
        boolean succes = false;
        json = "";
        String naziv, adresa;
        try {
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            naziv = jsonObject.get("naziv").getAsString();
            adresa = jsonObject.get("adresa").getAsString();
            if (!checkIfExistParkingByName(naziv)) {
                succes = processingAddParking(naziv, adresa);
            }
        } catch (Exception e) {

        }
        if (succes) {
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "OK")
                    .build()
                    .toString();
        } else {
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "ERR")
                    .add("poruka", "Dogodila se pogreska prilikom upisivanja podataka. Pokušajte promjeniti naziv parkirališta.")
                    .build()
                    .toString();
        }
        return json;
    }

    /**
     * Vraća ERR jer ova metoda nije podržana.
     *
     * PUT method for updating or creating an instance of MeteoREST
     *
     * @param content representation for the resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String postJson(@PathParam("id") String id, String content) {

        json = Json.createObjectBuilder()
                .add("odgovor", Json.createArrayBuilder())
                .add("status", "ERR")
                .add("poruka", "Metoda nije podržana")
                .build()
                .toString();
        return json;
    }

    /**
     * Ažurira parkiralište sa novoprosljeđenim podatcima.
     *
     * @param id - id parkirališta
     * @param content - podatci parkiralista
     * @return rezultat uspjeha
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String putJson(@PathParam("id") String id, String content) {
        boolean succes = false;
        json = "";
        String naziv, adresa;
        try {
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
            naziv = jsonObject.get("naziv").getAsString();
            adresa = jsonObject.get("adresa").getAsString();
            if (checkIfExistParkingByID(Integer.parseInt(id))) {
                succes = processingUpdateParking(naziv, adresa, Integer.parseInt(id));
            }
        } catch (Exception e) {

        }
        createPutResponse(succes);
        return json;
    }

    /**
     * Kreiranje odgorova PUT metode.
     *
     * @param succes
     */
    private void createPutResponse(boolean succes) {
        if (succes) {
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "OK")
                    .build()
                    .toString();
        } else {
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "ERR")
                    .add("poruka", "Dogodila se pogreška prilikom izmjene podataka")
                    .build()
                    .toString();
        }
    }

    /**
     * Vraća ERR, jer je to nepodržaa metoda.
     *
     * @param content
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson(String content) {

        json = Json.createObjectBuilder()
                .add("odgovor", Json.createArrayBuilder())
                .add("status", "ERR")
                .add("poruka", "Metoda nije podržana")
                .build()
                .toString();

        return json;
    }

    /**
     * Vraća ERR jer metoda nije podržana.
     *
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJson() {

        json = Json.createObjectBuilder()
                .add("odgovor", Json.createArrayBuilder())
                .add("status", "ERR")
                .add("poruka", "Metoda nije podržana")
                .build()
                .toString();

        return json;
    }

    /**
     * Briše parkiralište iz baze podataka sa prosljeđenim ID. Ako ne postoji,
     * vraca grešku.
     *
     * @param id
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String deleteJson(@PathParam("id") String id) {
        boolean succes = false;
        json = "";
        String naziv, adresa;
        try {
            if (checkIfExistParkingByID(Integer.parseInt(id))) {
                succes = deleteMeteoInDatabase(Integer.parseInt(id));
                succes = deleteParkingnInDatabase(Integer.parseInt(id));
            }
        } catch (Exception e) {
            succes = false;
        }
        createDeleteResponse(succes);
        return json;
    }

    /**
     * Kreiranje odgovora DELETE metode
     *
     * @param succes
     */
    private void createDeleteResponse(boolean succes) {
        if (succes) {
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "OK")
                    .build()
                    .toString();
        } else {
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "ERR")
                    .add("poruka", "Dogodila se pogreška prilikom brisanja podataka. Možda ste prosljedili pogrešan ID.")
                    .build()
                    .toString();
        }
    }

    /**
     * Vraća METEO podatke parkirališta čiji je ID prosljeđen.
     *
     * @param id - id parkirališta
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String getJson(@PathParam("id") String id) {
        json = "";
        JsonArrayBuilder allParkingData = createJSONdataWeather_ByParkingID(Integer.parseInt(id));
        if (allParkingData != null) {
            json = Json.createObjectBuilder()
                    .add("odgovor", allParkingData)
                    .add("status", "OK")
                    .build()
                    .toString();
        } else {
            //with error
            json = Json.createObjectBuilder()
                    .add("odgovor", Json.createArrayBuilder())
                    .add("status", "ERR")
                    .add("poruka", "Dogodila se pogreška prilikom dohvaćanja podataka. Možda ste prosljedili pogrešan ID.")
                    .build()
                    .toString();
        }

        return json;
    }

    /**
     * Dohvacanje podataka svih parkiralista iz baze podataka.
     *
     * Kreiranje JSON Array objekta koji sadrzi podatke o svim parkiralistima.
     *
     * @return
     */
    private JsonArrayBuilder createArrayJSONdata_allParkingData() {
        JsonArrayBuilder nesto = Json.createArrayBuilder();
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
            nesto = createJSONArrayFromResultset(results, nesto);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return nesto;
    }

    /**
     * Kreiranje JSON Array objekta iz rezultata izvršavanja SQL upita nad bazom
     * podataka.
     *
     * @param results
     * @param nesto
     * @return
     */
    private JsonArrayBuilder createJSONArrayFromResultset(ResultSet results, JsonArrayBuilder nesto) {
        if (results != null) {
            try {
                while (results.next()) {
                    String latitude = results.getString("LATITUDE");
                    String longitude = results.getString("LONGITUDE");
                    String id = results.getString("ID");
                    String naziv = results.getString("NAZIV");
                    String adresa = results.getString("ADRESA");

                    nesto.add(Json.createObjectBuilder()
                            .add("id", id)
                            .add("naziv", naziv)
                            .add("adresa", adresa)
                            .add("latitude", latitude)
                            .add("longitude", longitude)
                    );
                }
            } catch (SQLException ex) {
                Logger.getLogger(MeteoREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return nesto;

    }

    /**
     * Dohvacanje podataka o lokaciji parkirališta iz baze podataka.
     *
     * Kreiranje Json Array objekta sa trenutnim podatcima o vremenu.
     *
     * @param id
     * @return
     */
    private JsonArrayBuilder createJSONdataWeather_ByParkingID(int id) {
        JsonArrayBuilder nesto = Json.createArrayBuilder();
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
            nesto = createJSONArrayFromResultset_Weather(results, nesto);
            results.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
        return nesto;
    }

    /**
     * Dohvaćanje trenutnih meteo podataka putem WEB-servisa.
     *
     * Kreiranje JSON Array objekta sa podatcima.
     *
     * @param results rezultat upita sa podatcima jednog parkiralista
     * @param nesto djelomicno kriran JSON array
     * @return
     * @throws SQLException
     */
    private JsonArrayBuilder createJSONArrayFromResultset_Weather(final ResultSet results, JsonArrayBuilder nesto) throws SQLException {
        boolean exist = false;
        if (results != null) {
            while (results.next()) {
                exist = true;
                String latitude = results.getString("LATITUDE");
                String longitude = results.getString("LONGITUDE");
                OWMKlijent owmk = new OWMKlijent(OWM_apikey);
                MeteoPodaci mp = owmk.getRealTimeWeather(latitude, longitude);
                nesto.add(Json.createObjectBuilder()
                        .add("temp", mp.getTemperatureValue())
                        .add("vlaga", mp.getHumidityValue())
                        .add("tlak", mp.getPressureValue())
                );
            }
        }
        if (!exist) {
            return null;
        }
        return nesto;
    }

    /**
     * Preuzimanje podataka iz konfiguracije i pohranjivanje u globalne
     * varijable.
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
        gm_apiKey = konfiguracija.dajPostavku("gmapikey");
        OWM_apikey = konfiguracija.dajPostavku("apikey");

    }

    /**
     * Provjera postoji li u bazi podataka parkiraliste sa prosljedjenim
     * nazivom.
     *
     * @param name - naziv parkiralista
     * @return true - postoji; false - ne postoji
     */
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

    /**
     * Provjera postoji li u bazi podataka parkiralište sa prosljedjenim ID
     *
     * @param id - id parkialista
     * @return true - postoji; false - ne postoji
     */
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

    /**
     * Upis parkiralista u abzu podataka.
     *
     * @param name - naziv parkiralista
     * @param address - adresa park
     * @param lat - latitude
     * @param lon - longitude
     * @return @return true - uspjeh; false - neuspjeh
     */
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

    /**
     * Azuriranje podataka parkiralista u bazi podataka.
     *
     * @param name - naziv parkiralista
     * @param address - adresa parkiralista
     * @param lat - latitude
     * @param lon - longitude
     * @param id - id parkiralista koje se azurira
     * @return true - uspjeh; false - neuspjeh
     */
    private boolean updateParkingnInDatabase(String name, String address, String lat, String lon, int id) {
        String upit = "UPDATE PARKIRALISTA\n"
                + "  SET NAZIV='" + name + "', ADRESA='" + address + "', LATITUDE=" + lat + ", LONGITUDE=" + lon + "\n"
                + "  WHERE ID = " + id;
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

    /**
     * Brisanje parkiralista u bazi podataka.
     *
     * @param id - id parkiralista koje se brise
     * @return true - uspjeh; false - neuspjeh
     */
    private boolean deleteParkingnInDatabase(int id) {
        String upit = "DELETE from PARKIRALISTA WHERE ID = " + id;
        boolean success = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {

        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();) {
            success = stmt.execute(upit);
            success = true;
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return success;
        }

    }

    /**
     * Brisanje meteo podataka odabranog parkiralista iz baze podataka.
     *
     * @param id - id parkiralista za koje se brisu meteo podatci
     * @return true - uspjeh; false - neuspjeh
     */
    private boolean deleteMeteoInDatabase(int id) {
        String upit = "DELETE FROM METEO WHERE ID = " + id;
        boolean success = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {

        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();) {
            success = stmt.execute(upit);
            success = true;
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return success;
        }

    }

    /**
     * Obrada dodavanja parkiralista.
     *
     * Dohvacanje lokacije putem WS na temelju adrese.
     *
     * @param naziv - naziv parkiraliste
     * @param adresa - adresa parkiralista
     * @return rez uspjeha
     */
    private boolean processingAddParking(String naziv, String adresa) {
        GMKlijent gmk = new GMKlijent(gm_apiKey);
        Lokacija lok = gmk.getGeoLocation(adresa);
        return addParkingnInDatabase(naziv, adresa, lok.getLatitude(), lok.getLongitude());
    }

    /**
     * Obrada ažuriranja parkirališta.
     *
     *
     * Dohvacanje lokacije putem WS na temelju adrese.
     *
     * @param naziv - naziv parkiraliste
     * @param adresa - adresa parkiralista
     * @return rez uspjeha
     */
    private boolean processingUpdateParking(String naziv, String adresa, int id) {
        GMKlijent gmk = new GMKlijent(gm_apiKey);
        Lokacija lok = gmk.getGeoLocation(adresa);
        return updateParkingnInDatabase(naziv, adresa, lok.getLatitude(), lok.getLongitude(), id);
    }

}
