/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

/**
 *
 * @author Zoran
 */



public class Uredjaj_A {
    private int ID;
    private int temperatura;
    private int vlaga;
    private int svjetlost;
    private int vjetar;
    private int buka;

    public Uredjaj_A(int ID, int temperatura, int vlaga, int svjetlost, int vjetar, int buka) {
        this.ID = ID;
        this.temperatura = temperatura;
        this.vlaga = vlaga;
        this.svjetlost = svjetlost;
        this.vjetar = vjetar;
        this.buka = buka;
    }

    public Uredjaj_A() {
    }
    
    
}
