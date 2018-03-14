/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

/**
 *
 * @author grupa_1
 */
public class KorisnikSustava {

    String korisnik;
    String lozinka;
    String adresa;
    int port;
    boolean administrator = false;
    String[] args;
    

    public static void main(String[] args) {

        KorisnikSustava ks = new KorisnikSustava();
        ks.preuzmiPostavke(args);
        ks.args = args;
        
        if (ks.administrator) {
//TODO kreiraj objekt administrator sustava i predaj mu kontrolu
        }else{
 //TODO kreiraj objekt korisnika sustava i predaj mu kontrolu
       
        }

    }

    private void preuzmiPostavke(String[] args) {
        //TODO Provjeri upisane argumente
        korisnik = "zorhrncic";
        lozinka = "123456";
        adresa = "127.0.0.1";
        port = 8000;

        if (korisnik != null) {
            korisnik = korisnik.trim();
            if (!korisnik.isEmpty()) {
                administrator = true;

            }
        }

        if (lozinka != null) {
            lozinka = lozinka.trim();
            if (!lozinka.isEmpty()) {
                administrator = true;
            } else {
                administrator = false;

            }
        } else {
            administrator = false;

        }
//TODO Provjeri da li je korisnik koa admin u postavkama

    }

}
