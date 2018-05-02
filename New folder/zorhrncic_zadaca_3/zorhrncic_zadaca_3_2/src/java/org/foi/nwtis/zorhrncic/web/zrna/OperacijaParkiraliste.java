/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.foi.nwtis.zorhrncic.rest.klijenti.MeteoRESTKlijent;
import org.foi.nwtis.zorhrncic.ws.serveri.MeteoPodaci;
import org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste;

/**
 *
 * @author grupa_1
 */
@Named(value = "operacijaParkiraliste")
@RequestScoped
public class OperacijaParkiraliste {

    private String naziv;

    private String adresa;
    private List<Parkiraliste> listParkiralista;
    List<Integer> odabranaParkiralista;
    List<MeteoPodaci> meteo;

    /**
     * Creates a new instance of OperacijaParkiraliste
     */
    public OperacijaParkiraliste() {
    }
    
    public String upisiSOAP(){
 //TODO preuzmi geo lokaciju a bazi adrese
        //TODO upisi parkiraliste u bazu podataka
        return "";
    }
    
    public String upisiREST(){
        
        MeteoRESTKlijent klijent = new MeteoRESTKlijent();
        
        String novoPark = "{\"naziv\":\""+naziv+"\",\"adresa\":\""+adresa+"\"}";
        
        String odgovor = klijent.postJson(novoPark,String.class);
      
        System.out.println("OFGOVOR: " + odgovor);
        //klClient.
                
        return "";
    }
    //get

    public List<MeteoPodaci> getMeteo() {
        return meteo;
    }

    public List<Integer> getOdabranaParkiralista() {
        return odabranaParkiralista;
    }

    public void setOdabranaParkiralista(List<Integer> odabranaParkiralista) {
        this.odabranaParkiralista = odabranaParkiralista;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getAdesa() {
        return adresa;
    }

    public void setAdesa(String adesa) {
        this.adresa = adesa;
    }

    public List<Parkiraliste> getListParkiralista() {
        return listParkiralista;
    }



}
