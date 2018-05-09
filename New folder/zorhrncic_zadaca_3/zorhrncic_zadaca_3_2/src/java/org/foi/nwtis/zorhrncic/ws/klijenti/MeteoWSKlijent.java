/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ws.klijenti;

/**
 *
 * @author grupa_1
 */
public class MeteoWSKlijent {

    private static java.util.List<org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste> dajSvaParkiralista() {
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSvaParkiralista();
    }
    
}
