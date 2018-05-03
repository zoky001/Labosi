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
import java.util.ArrayList;
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
 * @author Zoran
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
                    .add("poruka", "Dogodila se pogreska prilikom upisivanja podataka")
                    .build()
                    .toString();
        }
        return json;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String putJson(@PathParam("id") String id, String content) {
        //TODO
        /*
  u metodi putJson(String) i delete Json staviti da 
        vraća pogrešku. Pripremiti odgovor u 
        application/json formatu i zadanom strukturom
        
  u metodi putJson(@PathParam("id") String id) staviti da ažurira 
        parkiralište. Prima podatke u application/json formatu. 
        Pripremiti odgovor u application/json formatu i zadanom strukturom.
         */
        // TODO provjeri da li postoji parkiraliĹˇte s id iz argumenta
        // TODO ako postoji aĹľurirati u bazi podataka
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
        return json;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String deleteJson(@PathParam("id") String id) {
        //TODO
        /*
  u metodi putJson(String) i delete Json staviti da 
        vraća pogrešku. Pripremiti odgovor u 
        application/json formatu i zadanom strukturom
        
    	u metodi deleteJson(@PathParam("id") String id) staviti 
        da briše parkiralište. Pripremiti odgovor u application/json 
        formatu i zadanom strukturom.
         */
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
                    .add("poruka", "Dogodila se pogreška prilikom brisanja podataka")
                    .build()
                    .toString();
        }
        return json;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String getJson(@PathParam("id") String id) {
        /*TODO
     u metodi getJson() staviti da vraća na bazi putanje 
        {id} važeće meteorološke podatke izabranog IoT uređaja. 
        Pripremiti odgovor u application/json formatu i 
        zadanom strukturom.
         */

        // TODO provjeri da li postoji parkiraliĹˇte s id iz argumenta
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
                    .add("poruka", "Dogodila se pogreška prilikom dohvaćanja podataka")
                    .build()
                    .toString();
        }

        return json;
    }

  /*  @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String postJson(@PathParam("id") String id, String podaci) {
        return "{\"odgovor\": [],"
                + "\"status\": \"ERR\", \"poruka\": \"Nije dozvoljeno\"}";
    }
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

    private JsonArrayBuilder createJSONArrayFromResultset_Weather(final ResultSet results, JsonArrayBuilder nesto) throws SQLException {
        if (results != null) {
            while (results.next()) {
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
        return nesto;
    }

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

    private boolean processingAddParking(String naziv, String adresa) {
        GMKlijent gmk = new GMKlijent(gm_apiKey);
        Lokacija lok = gmk.getGeoLocation(adresa);
        return addParkingnInDatabase(naziv, adresa, lok.getLatitude(), lok.getLongitude());
    }

    private boolean processingUpdateParking(String naziv, String adresa, int id) {
        GMKlijent gmk = new GMKlijent(gm_apiKey);
        Lokacija lok = gmk.getGeoLocation(adresa);
        return updateParkingnInDatabase(naziv, adresa, lok.getLatitude(), lok.getLongitude(), id);
    }

}
