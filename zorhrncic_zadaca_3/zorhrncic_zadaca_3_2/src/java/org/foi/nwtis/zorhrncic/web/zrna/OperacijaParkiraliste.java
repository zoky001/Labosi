/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import org.foi.nwtis.zorhrncic.web.podatci.Odgovor;
import org.foi.nwtis.zorhrncic.web.podatci.ResponseJson;
import com.google.gson.Gson;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.json.Json;
import org.foi.nwtis.zorhrncic.rest.klijenti.MeteoRESTKlijent;
import org.foi.nwtis.zorhrncic.rest.klijenti.MeteoRESTKlijentId;
import org.foi.nwtis.zorhrncic.ws.klijenti.MeteoWSKlijent;
import org.foi.nwtis.zorhrncic.ws.serveri.MeteoPodaci;
import org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste;

/**
 * Managed bean koji vrsi obradu vezanu za dodavanje,brisanje, azuriranje
 * parkiralisat putem REST i SOAP WS
 *
 * Dohvacanje meteo podataka parkiralista
 *
 *
 * @author Zoran Hrnčić
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
    private String id = null;

    /**
     * Creates a new instance of OperacijaParkiraliste
     *
     * kreiranje novog klijenta REST ws
     *
     * dohvacanje svih zapisa parkrialista
     */
    public OperacijaParkiraliste() {
        client = new MeteoRESTKlijent();
        gson = new Gson();
        getAllPArking();
    }

    
    /**
     * Dodavanje novog parkiralista putem SOAP ws
     * 
     * @return 
     */
    public String upisiSOAP() {
        if (naziv.length() == 0 || adresa.length() == 0) {
            setMessage("Moraju biti popunjeni naziv i adresa");
            return "";
        }
        Parkiraliste parkiraliste = new Parkiraliste();
        parkiraliste.setNaziv(naziv);
        parkiraliste.setAdresa(adresa);
        if (MeteoWSKlijent.dodajParkiraliste(parkiraliste)) {
            setMessage("Uspješno dodano parkiralište");
        } else {
            setMessage("Greška kod dodavanja. Pokušajte promjeniti naziv parkirališta.");
        }
        getAllPArking();
        return "";
    }

    /**
     * Dodavanje novog parkiralista putem REST ws
     * 
     * 
     * @return 
     */
    public String upisiREST() {
        if (naziv.length() == 0 || adresa.length() == 0) {
            setMessage("Moraju biti popunjeni naziv i adresa");
            return "";
        }
        String post = Json.createObjectBuilder()
                .add("naziv", naziv)
                .add("adresa", adresa)
                .build()
                .toString();
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

    
    /**
     * Dohvacanje podataka jednog parkiralista putem REST servisa
     * 
     * 
     * @return 
     */
    public String preuzmiREST() {
        try {
            if (parkListOdabrana.size() != 1) {
                setMessage("Mora biti odabrano točno JEDNO parkiralište");
                getAllPArking();
                return "";
            }
            ResponseJson response = gson.fromJson(client.getJson(String.class), ResponseJson.class);
            if (response.getStatus().equalsIgnoreCase("OK")) {
                for (Odgovor odgovor : response.getOdgovor()) {
                    if (odgovor.getId().equalsIgnoreCase(String.valueOf(parkListOdabrana.get(0).getId()))) {
                        naziv = odgovor.getNaziv();
                        adresa = odgovor.getAdresa();
                        id = odgovor.getId();
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

    
    /**
     * Azuriranje podataka jednog parkiralista putem REST ws
     * 
     * 
     * @return 
     */
    public String azururajREST() {
        if (naziv.length() == 0 || adresa.length() == 0 || id == null) {
            setMessage("Mora bti odabrano točnoo JEDNO parkiralište. \n Moraju biti popunjeni naziv i adresa");
            return "";
        }
        String put = Json.createObjectBuilder()
                .add("naziv", naziv).add("adresa", adresa).build().toString();
        try {
            clientId = new MeteoRESTKlijentId(String.valueOf(id));
            ResponseJson response = gson.fromJson(clientId.putJson(put, String.class), ResponseJson.class);

            if (response.getStatus().equalsIgnoreCase("OK")) {
                setMessage("Uspješno ažurirano parkiralište");
                naziv = "";
                adresa = "";
                id = null;
            } else {
                setMessage("Greška prilikom ažuriranja. \n " + response.getPoruka());
            }
        } catch (Exception e) {
            setMessage("Greška prilikom ažuriranja.");
        }
        getAllPArking();
        return "";
    }

    
    
    /**
     * Brisanje parkrialista putem REST ws
     * 
     * 
     * @return 
     */
    public String brisiREST() {
        try {
            if (parkListOdabrana.size() == 1) {
                clientId = new MeteoRESTKlijentId(String.valueOf(parkListOdabrana.get(0).getId()));
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

    
    /**
     * Preuzimanje podataka jednog parkiralista putem SOAP ws
     * 
     * 
     * @return 
     */
    public String preuzmiSOAP() {
        if (parkListOdabrana.size() != 1) {
            setMessage("Mora biti odabrano točno JEDNO parkiralište");
            getAllPArking();
            return "";
        }
        for (Parkiraliste parkiraliste : MeteoWSKlijent.dajSvaParkiralista()) {
            if (parkiraliste.getId() == parkListOdabrana.get(0).getId()) {
                naziv = parkiraliste.getNaziv();
                adresa = parkiraliste.getAdresa();
                id = String.valueOf(parkiraliste.getId());
                setMessage("Uspješno dohvačeno parkiralište");
                getAllPArking();
                return "";
            }
        }
        setMessage("Greška prilikom dohvačanja parirališta");
        getAllPArking();
        return "";

    }

    
    /**
     * Preuzimanje meteo podataka svih odabranih parkiralista
     * 
     * 
     * @return 
     */
    public String preuzmiMeteo() {
        try {
            meteoList = new ArrayList<>();
            if (parkListOdabrana.size() < 2) {
                setMessage("Moraju biti odabrna MINIMALNO DVA parkirališta");
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

    
    /**
     * Postavljanje poruke koja se prikazuje unutar JSF stranice.
     * 
     * 
     * @param message 
     */
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

    
    /**
     * Dohvacanje svih podataka parkiralista putem SOAP servisa
     * 
     * Pohrana podataka u listu koja se prikazuje u obliku Multiple Choice list box-a
     * 
     * 
     */
    private void getAllPArking() {
        try {
            parkList = MeteoWSKlijent.dajSvaParkiralista();
        } catch (Exception e) {
            setMessage("Greška kod dohvačanja podataka ");
        }
    }

}
