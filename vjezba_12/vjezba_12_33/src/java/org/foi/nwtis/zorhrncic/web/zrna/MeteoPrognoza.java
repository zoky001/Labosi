package org.foi.nwtis.zorhrncic.web.zrna;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import org.foi.nwtis.zorhrncic.ejb.sb.MeteoOsvjezivac;
import org.foi.nwtis.zorhrncic.ejb.sb.MeteoPrognosticar;
import org.foi.nwtis.zorhrncic.web.websocket.ParkiralisteEndpoint;
import org.foi.nwtis.zorhrncic.ws.serveri.MeteoPodaci;
import org.foi.nwtis.zorhrncic.ws.serveri.Parkiraliste;


/**
 *
 * @author grupa_1
 */
@Named(value = "meteoPrognoza")
@Dependent
public class MeteoPrognoza {

    @EJB
    private MeteoOsvjezivac meteoOsvjezivac;

    @EJB
    private MeteoPrognosticar meteoPrognosticar;

    String odabraniUredaj;
    List<String> parkiralista;
    List<MeteoPodaci> meteoPodaci;
    private String adresa;

    private String naziv;
    

    /**
     * Creates a new instance of MeteoPrognoza
     */
    public MeteoPrognoza() {
    }

    public String dodajParkiralište() {
       // parkiralista = meteoPrognosticar.dajParkiralista();
        Parkiraliste p = new Parkiraliste();
        p.setNaziv(naziv);
        p.setAdresa(adresa);
        parkiralista.add(adresa);
        parkiralista.add(naziv);
 
      //  meteoOsvjezivac.sendJMSMessageToNWTiS_vjezba_12("Dodano parkiralište: " + naziv + " " +adresa);
        
        ParkiralisteEndpoint.obavijestiPromjenu("Dodano parkiralište: ");
        return "";
    }

    public MeteoPrognosticar getMeteoPrognosticar() {
        return meteoPrognosticar;
    }

    public void setMeteoPrognosticar(MeteoPrognosticar meteoPrognosticar) {
        this.meteoPrognosticar = meteoPrognosticar;
    }

    public String getOdabraniUredaj() {
        return odabraniUredaj;
    }

    public void setOdabraniUredaj(String odabraniUredaj) {
        this.odabraniUredaj = odabraniUredaj;
    }

    public List<String> getParkiralista() {

        return parkiralista;
    }

    public void setParkiralista(List<String> parkiralista) {
        this.parkiralista = parkiralista;
    }

    public List<MeteoPodaci> getMeteoPodaci() {
        
       meteoPodaci = meteoPrognosticar.getMeteoPodaci();
        return meteoPodaci;
    }

    public void setMeteoPodaci(List<MeteoPodaci> meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
}
