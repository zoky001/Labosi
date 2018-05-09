/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.rest.serveri;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author grupa_1
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
        //TODO return proper representation object
        
        
        return " {\"odgovor\": [{\"id\":1,\"naziv\":\"Podzemna garaža\",\"adresa\":\"Kapucinski trg 1, Varaždin\"}],"
                + " \"status\": \"OK\"}";
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.zorhrncic.rest.serveri.MeteoREST
     *
     * @return an instance of java.lang.String
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postJson(String podatci) {
        //TODO provjeri dali postoji parkiraliste s ID iz parametra
        //TODO ako ne postoji, dodati atribut u bazu podataka

        
        System.out.println("PRIMIO: " + podatci);
        return " {\"odgovor\": [],"
                + " \"status\": \"OK\"}";

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String getJson(@PathParam("id") String id) {
        //TODO provjeri dali postoji parkiraliste s ID iz parametra
        if (Integer.parseInt(id) == 0) {
            return " {\"odgovor\": [],"
                    + " \"status\": \"ERROR\", \"poruka\":\"Parkiralište ne postoji\"}";

        } else {
            return " {\"odgovor\": [{\"id\":1,\"naziv\":\"Podzemna garaža\",\"adresa\":\"Kapucinski trg 1, Varaždin\"}],"
                    + " \"status\": \"OK\"}";
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String postJson(@PathParam("id") String id, String podatci) {

        //TODO provjeri dali postoji parkiraliste s ID iz parametra
        //TODO ako ne postoji, dodati atribut u bazu podataka
        
            return " {\"odgovor\": [],"
                    + " \"status\": \"ERROR\", \"poruka\":\"Nije dozvoljeno\"}";

      
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String putJson(@PathParam("id") String id, String podatci) {

        //TODO provjeri dali postoji parkiraliste s ID iz parametra
        //TODO ako postoji, onda azurirati
        
        
        if (Integer.parseInt(id) == 0) {
            return " {\"odgovor\": [],"
                    + " \"status\": \"ERROR\", \"poruka\":\"Parkiralište već postoji\"}";

        } else {
            return " {\"odgovor\": [],"
                    + " \"status\": \"OK\"}";
        }
    }
}
