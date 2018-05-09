/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ejb.sb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.xml.ws.WebServiceRef;
import jdk.nashorn.internal.parser.TokenType;
import org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS_Service;
import org.foi.nwtis.zorhrncic.ws.serveri.MeteoPodaci;
import org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste;

/**
 *
 * @author grupa_1
 */
@Singleton
@LocalBean
public class MeteoOsvjezivac {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8084/zorhrncic_zadaca_3_1/GeoMeteoWS.wsdl")
    private GeoMeteoWS_Service service;

    @Resource(mappedName = "jms/NWTiS_vjezba_12")
    private Queue nWTiS_vjezba_12;

    @Inject
    @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
    private JMSContext context;

    List<String> parkiralista;
    List<MeteoPodaci> meteoPodaci = new ArrayList<>();

    public List<MeteoPodaci> getMeteoPodaci() {
        return meteoPodaci;
    }

    public void setMeteoPodaci(List<MeteoPodaci> meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }

 
            
    //@EJB
    //private MeteoPrognosticar meteoPrognosticar;
    @Schedule(hour = "*", minute = "*/2")

    public void myTimer() {
        System.out.println("Timer event: " + new Date());
        List<String> l = getParkiralista();
        // List<MeteoPodatci> meteoPodatci = new ArrayList<MeteoPodatci>();
        //TODO preuzeti meteo podatke iz Web Servisa

        for (String p : l) {
            //List<MeteoPodatci> meteoPodatci =    //poziv metode DAj sve meteo podatke za parkirališta sa WS
            /* for (MeteoPodaci mpl : mp) {
            
            //ovdje složiti adapter za meteo podatke,, jre postoje dvije vrste!!
            MeteoPodatci mp2 ?= new MteoPodatci();
            mp2.setTemp(mp1.getTempo);
            mp2.setTemp(mp1.getTempo);//press
            mp2.setTemp(mp1.getTempo);//wind
                meteoPodatci.add(mpl);

            }*/

        }
        // meteoPrognosticar.setMeteoPodaci(meteoPodaci);

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void sendJMSMessageToNWTiS_vjezba_12(String messageData) {
        context.createProducer().send(nWTiS_vjezba_12, messageData);
    }

    public List<String> getParkiralista() {
        parkiralista = new ArrayList<>();
        List<Parkiraliste> nesto = dajSvaParkiralista();
        for (Parkiraliste p : nesto) {
            parkiralista.add(String.valueOf(p.getId()));
        }
        return parkiralista;
    }

    private java.util.List<org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste> dajSvaParkiralista() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.zorhrncic.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSvaParkiralista();
    }

}
