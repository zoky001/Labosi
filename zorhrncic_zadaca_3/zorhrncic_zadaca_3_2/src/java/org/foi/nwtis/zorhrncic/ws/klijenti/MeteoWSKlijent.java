/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ws.klijenti;

/**
 *
 * @author Zoran
 */
public class MeteoWSKlijent {

  



    public static java.util.List<org.foi.nwtis.zorhrncic.ws.serveri.MeteoPodaci> dajSveMeteoPodatke(int id) {
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSveMeteoPodatke(id);
    }

    public static boolean dodajParkiraliste(org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste parkiraliste) {
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dodajParkiraliste(parkiraliste);
    }

    public static java.util.List<org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste> dajSvaParkiralista() {
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSvaParkiralista();
    }

    public static java.util.List<org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste> dajSvaParkiralista_1() {
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSvaParkiralista();
    }
    
    
    
}
