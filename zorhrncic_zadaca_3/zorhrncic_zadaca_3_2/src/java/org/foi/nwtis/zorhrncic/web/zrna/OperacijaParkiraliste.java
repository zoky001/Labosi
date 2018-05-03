/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.foi.nwtis.zorhrncic.ws.klijenti.MeteoRESTKlijent;
import org.foi.nwtis.zorhrncic.ws.klijenti.MeteoRESTKlijentId;
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
    private MeteoRESTKlijent client = null;
    private MeteoRESTKlijentId clientId = null;
    private Gson gson;

    /**
     * Creates a new instance of OperacijaParkiraliste
     */
    public OperacijaParkiraliste() {
        // client = new MeteoRESTKlijent();
        client = new MeteoRESTKlijent();
        gson = new Gson();
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
        if (MeteoWSKlijent.dodajParkiraliste(parkiraliste)) {
            setMessage("Uspješno dodano parkiralište");
        } else {
            setMessage("Greška kod dodavanja.");
        }

//TODO ŠTO S LOKACIJOM?
        getAllPArking();
        return "";
    }

    public String upisiREST() {
        /*za dodavanje parkirališta pomoću operacije REST
        web servisa. Ako se javi pogreška treba ispisati opis
        pogreške u elementu za poruke.
         */

        if (naziv.length() == 0 || adresa.length() == 0) {
            setMessage("Moraju biti popunjeni naziv i adresa");
            return "";
        }
        String post = Json.createObjectBuilder()
                .add("naziv", naziv)
                .add("adresa", adresa)
                .build()
                .toString();
        // 2. JSON to Java object, read it from a Json String.

        ResponseJson response = gson.fromJson(client.postJson(post, String.class), ResponseJson.class);
        System.out.println("");
        try {
            if (response.getStatus().equalsIgnoreCase("OK")) {
                setMessage("Uspješno dodano parkiralište");
            } else {
                setMessage("Greška kod dodavanja. \n " + response.getPoruka());
            }
        } catch (Exception e) {
            setMessage("Greška kod dodavanja. ");
        }
        getAllPArking();
        return "";
    }

    public String preuzmiREST() {
        /*za preuzimanje podataka odabranog parkirališta 
        pomoću operacije REST web servisa. Podaci o nazivu 
        i adresi prenose se u elemente u obrascu. Ako se javi
        pogreška treba ispisati opis pogreške u elementu za 
        poruke.
         */

        try {
            if (parkListOdabrana.size() != 1) {
                setMessage("Mora biti odabrano točno jesno parkiralište");
                getAllPArking();
                return "";
            }
            clientId = new MeteoRESTKlijentId(String.valueOf(parkListOdabrana.get(0).getId()));
            ResponseJson response = gson.fromJson(client.getJson(String.class), ResponseJson.class);
            if (response.getStatus().equalsIgnoreCase("OK")) {
                for (Odgovor odgovor : response.getOdgovor()) {
                    if (odgovor.getId().equalsIgnoreCase(String.valueOf(parkListOdabrana.get(0).getId()))) {
                        naziv = odgovor.getNaziv();
                        adresa = odgovor.getAdresa();
                        setMessage("Uspješno dohvačeno parkiralište");
                    }
                }
            } else {
                setMessage("Greška kod dohvačanja. \n " + response.getPoruka());
            }

        } catch (Exception e) {
            setMessage("Greška kod dodavanja parkirališta. ");
        }
        getAllPArking();
        return "";
    }

    public String azururajREST() {

        /*za ažuriranje podataka odabranog parkirališta 
        pomoću operacije REST web servisa. Podaci o nazivu 
        i adresi preuzimaju se iz elemenata u obrascu. Ako se
        javi pogreška treba ispisati opis pogreške u elementu 
        za poruke.
         */

 /*
                try {
            com.google.gson.JsonObject jsonObject = new JsonParser().parse(client.postJson(post, String.class)).getAsJsonObject();
            if (jsonObject.get("status").getAsString().equalsIgnoreCase("OK")) {
                setMessage("Uspješno dodano parkiralište");
            } else {
                setMessage("Greška kod dodavanja. \n " + jsonObject.get("poruka").toString());
            }
        } catch (Exception e) {
            setMessage("Greška kod dodavanja. ");
        }
        
         */
        getAllPArking();
        return "";
    }

    public String brisiREST() {
        /*za brisanje odabranog parkirališta pomoću operacije
        REST web servisa. Ako se javi pogreška treba ispisati 
        opis pogreške u elementu za poruke.*/
//TODO message

        try {
            if (parkListOdabrana.size() == 1) {
                clientId = new MeteoRESTKlijentId(String.valueOf(parkListOdabrana.get(0).getId()));
            //    com.google.gson.JsonObject jsonObject = new JsonParser().parse(clientId.deleteJson(String.class)).getAsJsonObject();
                ResponseJson response = gson.fromJson(clientId.deleteJson(String.class), ResponseJson.class);    
                if (response.getStatus().equalsIgnoreCase("OK")) {
                    setMessage("Uspješno obrisano parkiralište");
                } else {
                    setMessage("Greška kod brisanja. \n " + response.getPoruka());
                }
            } else {
                setMessage("Mora biti odabrano točno JEDNO parkiralište.");
            }
        } catch (Exception e) {
            setMessage("Greška kod brisanja. ");
        }
        getAllPArking();
        return "";
    }

    public String preuzmiSOAP() {
        /*za preuzimanje podataka odabranog parkirališta pomoću 
        operacije SOAP web servisa. Podaci o nazivu i adresi
        prenose se u elemente u obrascu. Ako se javi pogreška 
        treba ispisati opis pogreške u elementu za poruke.*/
        if (parkListOdabrana.size() != 1) {
            setMessage("Mora biti odabrano točno jesno parkiralište");
            return "";
        }
        naziv = parkListOdabrana.get(0).getNaziv();
        adresa = parkListOdabrana.get(0).getAdresa();
        /*
                try {
            com.google.gson.JsonObject jsonObject = new JsonParser().parse(client.postJson(post, String.class)).getAsJsonObject();
            if (jsonObject.get("status").getAsString().equalsIgnoreCase("OK")) {
                setMessage("Uspješno dodano parkiralište");
            } else {
                setMessage("Greška kod dodavanja. \n " + jsonObject.get("poruka").toString());
            }
        } catch (Exception e) {
            setMessage("Greška kod dodavanja. ");
        }
         */
        getAllPArking();
        return "";
    }

    public String preuzmiMeteo() {
        /*za preuzimanje meteo podataka odabranog
        parkirališta pomoću operacije SOAP web servisa. 
        Podaci o meteo podacima prikazuju se u tablici u 
        obrascu. Ako se javi pogreška treba ispisati opis 
        pogreške u elementu za poruke*/

        //TODO message
        try {
            meteoList = new ArrayList<>();
            if (parkListOdabrana.size() < 2) {
                setMessage("Moraju biti odabrna minimalno dva parkirališta");
                return "";
            }
            for (Parkiraliste p : parkListOdabrana) {
                System.out.println("" + p.getAdresa());
                List<MeteoPodaci> l = MeteoWSKlijent.dajSveMeteoPodatke(p.getId());
                meteoList.addAll(l);
            }
            setMessage("Uspješno dohvaćeni podatci");
        } catch (Exception e) {
            setMessage("Greška kod dohvačanja podataka ");
        }

        getAllPArking();
        return "";
    }

    private void setMessage(String message) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(message);
        facesContext.addMessage(null, facesMessage);
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
        try {
            parkList = MeteoWSKlijent.dajSvaParkiralista();
        } catch (Exception e) {
            setMessage("Greška kod dohvačanja podataka ");
        }
    }

}
