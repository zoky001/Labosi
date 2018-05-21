/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ejb.sb;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import org.foi.nwtis.zorhrncic.rest.klijenti.GMKlijent;
import org.foi.nwtis.zorhrncic.rest.klijenti.OWMKlijentPrognoza;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPrognoza;

/**
 * Klasa koja se koristi za dohvacanje meteo podataka i podataka o lokaciji
 * putem Web Servisa.
 *
 * @author Zoran Hrnčić
 */
@Stateless
@LocalBean
public class MeteoKlijentZrno {

    private String apiKey;
    private String gmapiKey;

    /**
     * Postavlja api keys za koristenje web servisa za dohvacanje lokacije na
     * temlju adrese i dohvacanje meteo podataka na temelju lokacije.
     *
     *
     * @param apiKey - meteo api key
     * @param gmapiKey - google api key
     */
    public void postaviKorisnickePodatke(String apiKey, String gmapiKey) {
        this.apiKey = apiKey;
        this.gmapiKey = gmapiKey;

    }

    /**
     * Dohvaca lokacijiu putem WS na temelju prosljedjene adrese.
     *
     * @param adresa - geografska adresa
     * @return - geolokacija
     */
    public Lokacija dajLokaciju(String adresa) {
        GMKlijent gMKlijent = new GMKlijent(gmapiKey);
        Lokacija lokacija = gMKlijent.getGeoLocation(adresa);
        return lokacija;
    }

    /**
     * Dohvaca meteo prognozu za narednih 5 dana na temelju prosljedjene
     * geografske adrese.
     *
     * @param id
     * @param adresa
     * @return
     */
    public org.foi.nwtis.zorhrncic.web.podaci.MeteoPrognoza[] dajMeteoPrognoze(int id, String adresa) {
        OWMKlijentPrognoza klijentPrognoza = new OWMKlijentPrognoza(apiKey);
        Lokacija lokacija = dajLokaciju(adresa);
        MeteoPrognoza[] meteoPrognoze = klijentPrognoza.getWeatherForecast(id, lokacija.getLatitude(), lokacija.getLongitude());
        return meteoPrognoze;
    }

}
