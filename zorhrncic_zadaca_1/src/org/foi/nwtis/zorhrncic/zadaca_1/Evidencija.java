/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author grupa_1
 */
public class Evidencija implements Serializable {

    private long ukupanbrojZahtjeva = 0;
    private long brojNeispravnihZahtjeva = 0;
    private long brojNedozvoljenihZahtjeva = 0;
    private long brojUspjesnihZahtjeva = 0;
    private long brojPrkinutihZahtjeva = 0;
    private long ukupnoVrijemeRadaRadnihDretvi = 0;
    private long brojObavljanjaSerijalizacije = 0;
    private boolean upis = false;

    public long getUkupanbrojZahtjeva() {
        return ukupanbrojZahtjeva;
    }

    public void setUkupanbrojZahtjeva(long ukupanbrojZahtjeva) {
        this.ukupanbrojZahtjeva = ukupanbrojZahtjeva;
    }

    public long getBrojNeispravnihZahtjeva() {
        return brojNeispravnihZahtjeva;
    }

    public void setBrojNeispravnihZahtjeva(long brojNeispravnihZahtjeva) {
        this.brojNeispravnihZahtjeva = brojNeispravnihZahtjeva;
    }

    public long getBrojNedozvoljenihZahtjeva() {
        return brojNedozvoljenihZahtjeva;
    }

    public void setBrojNedozvoljenihZahtjeva(long brojNedozvoljenihZahtjeva) {
        this.brojNedozvoljenihZahtjeva = brojNedozvoljenihZahtjeva;
    }

    public long getBrojUspjesnihZahtjeva() {
        return brojUspjesnihZahtjeva;
    }

    public void setBrojUspjesnihZahtjeva(long brojUspjesnihZahtjeva) {
        this.brojUspjesnihZahtjeva = brojUspjesnihZahtjeva;
    }

    public long getBrojPrkinutihZahtjeva() {
        return brojPrkinutihZahtjeva;
    }

    public void setBrojPrkinutihZahtjeva(long brojPrkinutihZahtjeva) {
        this.brojPrkinutihZahtjeva = brojPrkinutihZahtjeva;
    }

    public long getUkupnoVrijemeRadaRadnihDretvi() {
        return ukupnoVrijemeRadaRadnihDretvi;
    }

    public void setUkupnoVrijemeRadaRadnihDretvi(long ukupnoVrijemeRadaRadnihDretvi) {
        this.ukupnoVrijemeRadaRadnihDretvi = ukupnoVrijemeRadaRadnihDretvi;
    }

    public long getBrojObavljanjaSerijalizacije() {
        return brojObavljanjaSerijalizacije;
    }

    public void setBrojObavljanjaSerijalizacije(long brojObavljanjaSerijalizacije) {
        this.brojObavljanjaSerijalizacije = brojObavljanjaSerijalizacije;
    }

    public synchronized void dodajNoviZahtjev()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }

        upis = true;

        //radi
        this.ukupanbrojZahtjeva++;
        

        upis = false;
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized void dodajNeispravanZahtjev()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }

        upis = true;

        //radi
        this.brojNeispravnihZahtjeva++;

        upis = false;
        System.out.println("Posao obavljen");
        notify();
    }



}
