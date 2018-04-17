/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.kontrole;

/**
 *
 * @author grupa_1
 */
public class Korisnik {

    private String id;
    private String korime;
    private String ime;
    private String prezime;
    private String remoteAddr;
    private int tip;

    Korisnik(String id, String korime, String ime, String prez, String remoteAddr, int i) {
        this.id = id;
        this.korime = korime;
        this.prezime = prez;
        this.ime = ime;
        this.remoteAddr = remoteAddr;
        this.tip = i;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

}
