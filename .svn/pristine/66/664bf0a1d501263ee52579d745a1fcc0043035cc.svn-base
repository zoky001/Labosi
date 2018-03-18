package org.foi.nwtis.nikbukove.konfiguracije;

import java.util.Properties;

public abstract class KonfiguracijaApstraktna implements Konfiguracija{
    protected String datoteka;
    protected Properties postavke;

    public KonfiguracijaApstraktna(String datoteka) {
        this.datoteka = datoteka;
        this.postavke = new Properties();
    }

    @Override
    public void dodajKonfiguraciju(Properties postavke) {
        for(Object k1: postavke.keySet()){
            String k = (String) k1;
            String v = (String) postavke.getProperty(k);
            this.postavke.setProperty(k,v);
        }
    }

    @Override
    public void kopirajKonfiguraciju(Properties postavke) {
        this.postavke.clear();
        dodajKonfiguraciju(postavke);
    }

    @Override
    public Properties dajSvePostavke() {
        return postavke;
    }

    @Override
    public boolean obrisiSvePostavke() {
        if(this.postavke.isEmpty()){
            return false;
        }
        this.postavke.clear();
        return true;
    }

    @Override
    public String dajPostavku(String postavka) {
       return this.postavke.getProperty(postavka);
    }

    @Override
    public boolean spremiPostavku(String postavka, String vrijednost) {
        if(this.postavke.containsKey(postavka))
            return false;
        this.postavke.setProperty(postavka, vrijednost);
        return true;
    }

    @Override
    public boolean azurirajPostavku(String postavka, String vrijednost) {
       if(this.postavke.containsKey(postavka)){
           this.postavke.setProperty(postavka, vrijednost);
           return true;
       }
       return false;
    }

    @Override
    public boolean postojiPostavka(String postavka) {
        return this.postavke.containsKey(postavka);
    }

    @Override
    public boolean obrisiPostavku(String postavka) {
        if(!postojiPostavka(postavka))
            return false;
        
        this.postavke.remove(postavka);
        return true;
    }
    
    public static Konfiguracija kreirajKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija{
        Konfiguracija konfig = null;
        
        if(datoteka.toLowerCase().endsWith(".txt")){
            konfig = new KonfiguracijaTxt(datoteka);
            konfig.spremiKonfiguraciju();
        }else if(datoteka.toLowerCase().endsWith(".xml")){
            konfig = new KonfiguracijaXML(datoteka);
            konfig.spremiKonfiguraciju();
        } else if(datoteka.toLowerCase().endsWith(".json")){
            konfig = new KonfiguracijaJSON(datoteka);
            konfig.spremiKonfiguraciju();
        } else if(datoteka.toLowerCase().endsWith(".bin")){
            konfig = new KonfiguracijaBin(datoteka);
            konfig.spremiKonfiguraciju();
        } else{
            throw new NemaKonfiguracije("Neispravna ekstenzija!");
        }
        
        konfig.spremiKonfiguraciju();
        return konfig;
    }
    
    public static Konfiguracija preuzmiKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija{
        
        Konfiguracija konfig = null;
        
        if(datoteka.toLowerCase().endsWith(".txt")){
            konfig = new KonfiguracijaTxt(datoteka);
            konfig.spremiKonfiguraciju();
        }else if(datoteka.toLowerCase().endsWith(".xml")){
            konfig = new KonfiguracijaXML(datoteka);
            konfig.spremiKonfiguraciju();
        } else if(datoteka.toLowerCase().endsWith(".json")){
            konfig = new KonfiguracijaJSON(datoteka);
            konfig.spremiKonfiguraciju();
        } else if(datoteka.toLowerCase().endsWith(".bin")){
            konfig = new KonfiguracijaBin(datoteka);
            konfig.spremiKonfiguraciju();
        } else{
            throw new NemaKonfiguracije("Neispravna ekstenzija!");
        }
        
        konfig.ucitajKonfiguraciju();
        return konfig;
    }
    
    
}
