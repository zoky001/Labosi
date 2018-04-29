/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.rest.serveri;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Zoran
 */
@Path("meteo")
public class MeteoREST {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MeteoREST
     */
    public MeteoREST() {
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
        //TODO 
        /*
       u klasi MeteoREST u metodi getJson() 
        staviti da vraća popis svih parkirališta 
        koja se nalaze u tablici PARKIRALISTA iz baze
        podataka u application/json formatu. 
        Potrebno je napraviti upit prema bazi podataka.
        Pripremiti odgovor u application/json formatu i 
        zadanom strukturom.
         */

        JsonArrayBuilder nesto =  createArrayJSONdata_allParkingData();
       

        //with error
        String json = Json.createObjectBuilder()
                .add("odgovor", nesto
                )
                .add("status", "ERR")
                .add("poruka", "Dogodila se pogreška")
                .build()
                .toString();
 

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
        //TODO
        /*u metodi postJson(String) staviti da dodaje 
       parkiralište. Prima podatke u application/json 
       formatu. Pripremiti odgovor u application/json 
       formatu i zadanom strukturom.
         */
        // TODO provjeri da li postoji parkiraliĹˇte s id iz argumenta
        // TODO ako ne postoji dodati u bazu podataka

        return "{\"odgovor\": [],"
                + "\"status\": \"OK\"}";
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
        if (Integer.parseInt(id) == 0) {
            return "{\"odgovor\": [],"
                    + "\"status\": \"ERR\", \"poruka\": \"ParkiraliĹˇte ne postoji\"}";
        } else {
            return "{\"odgovor\": [],"
                    + "\"status\": \"OK\"}";
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
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
        return "";
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
        if (Integer.parseInt(id) == 0) {
            return "{\"odgovor\": [],"
                    + "\"status\": \"ERR\", \"poruka\": \"ParkiraliĹˇte ne postoji\"}";
        } else {
            return "{\"odgovor\": [{\"id\": 1, "
                    + "\"naziv\": \"Podzemna garaĹľa\","
                    + "\"adresa\": \"Kapucinski trg 1, VaraĹľdin\"}],"
                    + "\"status\": \"OK\"}";
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String postJson(@PathParam("id") String id, String podaci) {
        return "{\"odgovor\": [],"
                + "\"status\": \"ERR\", \"poruka\": \"Nije dozvoljeno\"}";
    }

    private JsonArrayBuilder createArrayJSONdata_allParkingData() {
        JsonArrayBuilder nesto = Json.createArrayBuilder();

        for (int i = 0; i < 10; i++) {
            nesto.add(Json.createObjectBuilder()
                    .add("id", i)
                    .add("naziv", "Podzemna garaža")
                    .add("adresa", "Kapucinski trg 9")
                    .add("latitude", "46.2201")
                     .add("longitude", "46.2201")
            );

        }
        return nesto;
    }
}
