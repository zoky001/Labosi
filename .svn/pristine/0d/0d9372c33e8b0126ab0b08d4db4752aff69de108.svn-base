package org.foi.nwtis.nikbukove.konfiguracije;

import java.util.Properties;

public interface Konfiguracija {

    public void ucitajKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija;

    public void ucitajKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija;

    public void spremiKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija;

    public void spremiKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija;

    public void dodajKonfiguraciju(Properties postavke);

    public void kopirajKonfiguraciju(Properties postavke);

    public Properties dajSvePostavke();

    public boolean obrisiSvePostavke();

    public String dajPostavku(String postavka);

    public boolean spremiPostavku(String postavka, String vrijednost);

    public boolean azurirajPostavku(String postavka, String vrijednost);

    public boolean postojiPostavka(String postavka);

    public boolean obrisiPostavku(String postavka);
}
