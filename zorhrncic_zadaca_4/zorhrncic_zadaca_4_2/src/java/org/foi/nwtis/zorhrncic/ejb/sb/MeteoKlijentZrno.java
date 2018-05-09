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
 *
 * @author grupa_1
 */
@Stateless
@LocalBean
public class MeteoKlijentZrno {

    private String apiKey;
    private String gmapiKey;

    public void postaviKorisnickePodatke(String apiKey, String gmapiKey) {
        this.apiKey = apiKey;
        this.gmapiKey = gmapiKey;

    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public Lokacija dajLokaciju(String adresa) {
        GMKlijent gMKlijent = new GMKlijent(gmapiKey); //prosljediti api key
        Lokacija lokacija = gMKlijent.getGeoLocation(adresa);

        return lokacija;
    }

    public org.foi.nwtis.zorhrncic.web.podaci.MeteoPrognoza[] dajMeteoPrognoze(int id, String adresa) {
        OWMKlijentPrognoza klijentPrognoza = new OWMKlijentPrognoza(apiKey);
        Lokacija lokacija = dajLokaciju(adresa);
        MeteoPrognoza[] meteoPrognoze = klijentPrognoza.getWeatherForecast(id, lokacija.getLatitude(), lokacija.getLongitude());
        return meteoPrognoze;
    }

}
