/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author grupa_1
 */
public class DretvaPromjena extends Thread {

    SlusacPromjena sp;
    int broj;
    String klasa;
    private Brojaci objekt;

    public DretvaPromjena(SlusacPromjena sp, int broj, String klasa) {
        this.sp = sp;
        this.broj = broj;
        this.klasa = klasa;

    }

    @Override
    public void interrupt() {
        super.interrupt();

    }

    @Override
    public void run() {

        try {
            while (true) {
                objekt.run();
                sleep(this.broj * 1000);

            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DretvaPromjena.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public synchronized void start() {

        try {
            Class klasaUcitana = Class.forName(this.klasa);
            objekt = (Brojaci) klasaUcitana.newInstance();
            objekt.dodajSlusaca(sp);
            super.start();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DretvaPromjena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
