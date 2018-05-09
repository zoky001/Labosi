/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.foi.nwtis.zorhrncic.ejb.eb.Parkiralista;
import org.foi.nwtis.zorhrncic.ejb.sb.MeteoKlijentZrno;
import org.foi.nwtis.zorhrncic.ejb.sb.ParkiralistaFacade;
import org.foi.nwtis.zorhrncic.web.kontrole.Izbornik;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPrognoza;
import org.foi.nwtis.zorhrncic.web.podaci.Parkiraliste;

/**
 *
 * @author grupa_1
 */
@Named(value = "pregled")
@SessionScoped
public class Pregled implements Serializable {

    @EJB
    private MeteoKlijentZrno meteoKlijentZrno;

    @EJB
    private ParkiralistaFacade parkiralistaFacade;

    List<Izbornik> raspolozivaParkiralistaIzbornik;
    List<String> raspolozivaParkiralistaString;
    List<Izbornik> odabranaParkiralistaIzbornik;
    List<String> odabranaParkiralistaList;

    Parkiraliste azuriranoParkiraliste;

    Parkiraliste novoParkiraliste;

    List<String> listMeteoPrognoza;

    List<MeteoPrognoza> tableMeteoPrognoza;

    /*
    , prikaz ažuriranjaparkirališta, prikaz pregleda prognoza. 
     */
    private String adresa;

    private String naziv;

    private Integer id;

    /**
     * Creates a new instance of Pregled
     */
    public Pregled() {

        odabranaParkiralistaIzbornik = new ArrayList<Izbornik>();
        raspolozivaParkiralistaIzbornik = new ArrayList<Izbornik>();
        //getAllParking();
    }

    public String dodajParkiraliste() {
        // parkiralista = meteoPrognosticar.dajParkiralista();
        Lokacija lok = meteoKlijentZrno.dajLokaciju(adresa);
        Parkiralista p = new Parkiralista();
        p.setNaziv(naziv);
        p.setAdresa(adresa);
        p.setId(id);
        float lat = Float.parseFloat(lok.getLatitude());
        float longi = Float.parseFloat(lok.getLongitude());
        p.setLatitude(lat);
        p.setLongitude(longi);
        parkiralistaFacade.create(p);
        getAllParking();
        return "";
    }

    public String azurirajParkiraliste() {
        // parkiralista = meteoPrognosticar.dajParkiralista();
        Parkiralista p = new Parkiralista();
        p.setNaziv(naziv);
        p.setAdresa(adresa);
        p.setId(id);

        parkiralistaFacade.edit(p);
        getAllParking();
        return "";
    }

    public boolean dodajOdabrana() {

        for (Izbornik izbornik : raspolozivaParkiralistaIzbornik) {

            for (String string : raspolozivaParkiralistaString) {
                if (string.equalsIgnoreCase(izbornik.getVrijednost())) {

                    odabranaParkiralistaIzbornik.add(izbornik);
                    raspolozivaParkiralistaIzbornik.remove(izbornik);

                }
            }
        }

        /*
        dodavanje odabranog(ih) parkirališta u odabrana parkirališta
         */
        // getAllParking();
        return true;
    }

    public boolean izbrisiOdabrana() {
        /*
        izbacivanje odabranog(ih) parkirališta iz popisa odabranihparkirališta, 
         */
        for (Izbornik izbornik : odabranaParkiralistaIzbornik) {

            for (String string : odabranaParkiralistaList) {
                if (string.equalsIgnoreCase(izbornik.getVrijednost())) {
                    raspolozivaParkiralistaIzbornik.add(izbornik);
                    odabranaParkiralistaIzbornik.remove(izbornik);
                   

                }
            }
        }
        return true;
    }

    /*
    parkirališta iz popisa odabranihparkirališta, 
    pregled prognoza za odabrana parkirališta itd
    
     */
    public void getAllParking() {
        raspolozivaParkiralistaIzbornik = new ArrayList<Izbornik>();
        Izbornik izbornik;

        List<Parkiralista> parki = parkiralistaFacade.findAll();

        for (Parkiralista park : parki) {
            izbornik = new Izbornik(park.getNaziv(), park.getNaziv());
            raspolozivaParkiralistaIzbornik.add(izbornik);
        }
    }

    public Parkiralista getParkingDataByID(int id) {

        return parkiralistaFacade.find(id);

    }

//gett setrt
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ParkiralistaFacade getParkiralistaFacade() {
        return parkiralistaFacade;
    }

    public void setParkiralistaFacade(ParkiralistaFacade parkiralistaFacade) {
        this.parkiralistaFacade = parkiralistaFacade;
    }

    public List<Izbornik> getRaspolozivaParkiralistaIzbornik() {
        if (raspolozivaParkiralistaIzbornik != null) {
            Collections.sort(raspolozivaParkiralistaIzbornik, new LexicographicComparator());
        }

        return raspolozivaParkiralistaIzbornik;
    }

    class LexicographicComparator implements Comparator<Izbornik> {

        @Override
        public int compare(Izbornik a, Izbornik b) {
            return a.getLabela().compareToIgnoreCase(b.getLabela());
        }
    }

    public void setRaspolozivaParkiralistaIzbornik(List<Izbornik> raspolozivaParkiralistaIzbornik) {
        this.raspolozivaParkiralistaIzbornik = raspolozivaParkiralistaIzbornik;
    }

    public List<String> getRaspolozivaParkiralistaString() {
        return raspolozivaParkiralistaString;
    }

    public void setRaspolozivaParkiralistaString(List<String> raspolozivaParkiralistaString) {
        this.raspolozivaParkiralistaString = raspolozivaParkiralistaString;
    }

    public List<Izbornik> getOdabranaParkiralistaIzbornik() {
        return odabranaParkiralistaIzbornik;
    }

    public void setOdabranaParkiralistaIzbornik(List<Izbornik> odabranaParkiralistaIzbornik) {
        this.odabranaParkiralistaIzbornik = odabranaParkiralistaIzbornik;
    }

    public List<String> getOdabranaParkiralistaList() {
        return odabranaParkiralistaList;
    }

    public void setOdabranaParkiralistaList(List<String> odabranaParkiralistaList) {
        this.odabranaParkiralistaList = odabranaParkiralistaList;
    }

    public Parkiraliste getAzuriranoParkiraliste() {
        return azuriranoParkiraliste;
    }

    public void setAzuriranoParkiraliste(Parkiraliste azuriranoParkiraliste) {
        this.azuriranoParkiraliste = azuriranoParkiraliste;
    }

    public Parkiraliste getNovoParkiraliste() {
        return novoParkiraliste;
    }

    public void setNovoParkiraliste(Parkiraliste novoParkiraliste) {
        this.novoParkiraliste = novoParkiraliste;
    }

    public List<String> getListMeteoPrognoza() {
        return listMeteoPrognoza;
    }

    public void setListMeteoPrognoza(List<String> listMeteoPrognoza) {
        this.listMeteoPrognoza = listMeteoPrognoza;
    }

    public MeteoKlijentZrno getMeteoKlijentZrno() {
        return meteoKlijentZrno;
    }

    public void setMeteoKlijentZrno(MeteoKlijentZrno meteoKlijentZrno) {
        this.meteoKlijentZrno = meteoKlijentZrno;
    }

    public List<MeteoPrognoza> getTableMeteoPrognoza() {
        return tableMeteoPrognoza;
    }

    public void setTableMeteoPrognoza(List<MeteoPrognoza> tableMeteoPrognoza) {
        this.tableMeteoPrognoza = tableMeteoPrognoza;
    }

}
