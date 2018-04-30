/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.foi.nwtis.zorhrncic.ws.klijenti.MeteoWSKlijent;
import org.foi.nwtis.zorhrncic.ws.serveri.Lokacija;
import org.foi.nwtis.zorhrncic.ws.serveri.MeteoPodaci;
import org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste;

/**
 *
 * @author Zoran
 */
@Named(value = "operacijaParkiraliste")
@SessionScoped
public class OperacijaParkiraliste implements Serializable {

    private String naziv;
    private String adresa;
    private List<Parkiraliste> parkList;
    private List<Parkiraliste> parkListOdabrana;
    private List<MeteoPodaci> meteoList;
    private final MeteoREST_JerseyClient client;

    /**
     * Creates a new instance of OperacijaParkiraliste
     */
    public OperacijaParkiraliste() {
        client = new MeteoREST_JerseyClient();
        getAllPArking();
    }

    public String upisiSOAP() {
        //   za dodavanje parkirališta pomoću operacije SOAP web
        //servisa. Ako se javi pogreška treba ispisati opis 
        //pogreške u elementu za poruke.
        Lokacija lokacija = new Lokacija();
        lokacija.setLatitude("46.37438789999999");
        lokacija.setLongitude("16.1025389");
        Parkiraliste parkiraliste = new Parkiraliste();
        parkiraliste.setNaziv(naziv);
        parkiraliste.setAdresa(adresa);
        parkiraliste.setGeoloc(lokacija);
        MeteoWSKlijent.dodajParkiraliste(parkiraliste);
//TODO ŠTO S LOKACIJOM?
//message
        return "";
    }

    public String upisiREST() {
        /*za dodavanje parkirališta pomoću operacije REST
        web servisa. Ako se javi pogreška treba ispisati opis
        pogreške u elementu za poruke.
         */

        String post = "{\n"
                + "    \n"
                + "            \"naziv\": \"" + naziv + "\",\n"
                + "            \"adresa\": \"" + adresa + "\"\n"
                + "       \n"
                + "}";

        client.postJson(post);
        return "";
    }

    public String preuzmiREST() {
        /*za preuzimanje podataka odabranog parkirališta 
        pomoću operacije REST web servisa. Podaci o nazivu 
        i adresi prenose se u elemente u obrascu. Ako se javi
        pogreška treba ispisati opis pogreške u elementu za 
        poruke.
         */
        for (Parkiraliste p : parkListOdabrana) {
            System.out.println("" + p.getAdresa());
        }
        return "";
    }

    public String azururajREST() {

        /*za ažuriranje podataka odabranog parkirališta 
        pomoću operacije REST web servisa. Podaci o nazivu 
        i adresi preuzimaju se iz elemenata u obrascu. Ako se
        javi pogreška treba ispisati opis pogreške u elementu 
        za poruke.
         */
        return "";
    }

    public String brisiREST() {
        /*za brisanje odabranog parkirališta pomoću operacije
        REST web servisa. Ako se javi pogreška treba ispisati 
        opis pogreške u elementu za poruke.*/
//TODO message
        if (parkListOdabrana.size() == 1) {
            client.deleteJson(String.valueOf(parkList.get(0).getId()));
            getAllPArking();
        }
        return "";
    }

    public String preuzmiSOAP() {
        /*za preuzimanje podataka odabranog parkirališta pomoću 
        operacije SOAP web servisa. Podaci o nazivu i adresi
        prenose se u elemente u obrascu. Ako se javi pogreška 
        treba ispisati opis pogreške u elementu za poruke.*/
        return "";
    }

    public String preuzmiMeteo() {
        /*za preuzimanje meteo podataka odabranog
        parkirališta pomoću operacije SOAP web servisa. 
        Podaci o meteo podacima prikazuju se u tablici u 
        obrascu. Ako se javi pogreška treba ispisati opis 
        pogreške u elementu za poruke*/

        //TODO message
        meteoList = new ArrayList<>();
        if (parkListOdabrana.size() < 2) {

            //todo message
            return "";
        }
        for (Parkiraliste p : parkListOdabrana) {
            System.out.println("" + p.getAdresa());
            List<MeteoPodaci> l = MeteoWSKlijent.dajSveMeteoPodatke(p.getId());
            meteoList.addAll(l);
        }
        return "";
    }

//getter & setter
    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public List<Parkiraliste> getParkList() {
        return parkList;
    }

    public void setParkList(List<Parkiraliste> parkList) {
        this.parkList = parkList;
    }

    public List<Parkiraliste> getParkListOdabrana() {
        return parkListOdabrana;
    }

    public void setParkListOdabrana(List<Parkiraliste> parkListOdabrana) {
        this.parkListOdabrana = parkListOdabrana;
    }

    public List<MeteoPodaci> getMeteoList() {
        return meteoList;
    }
//rest

    private void getAllPArking() {

        parkList = MeteoWSKlijent.dajSvaParkiralista();
    }

    static class MeteoREST_JerseyClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8088/zorhrncic_zadaca_3_1/webresources";

        public MeteoREST_JerseyClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("meteo");
        }

        public String putJson(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String postJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String deleteJson(String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().delete(String.class);
        }

        public String getJson(String id) throws ClientErrorException {
            WebTarget resource = webTarget;
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

}
