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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTemp() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public int getVlaga() {
        return vlaga;
    }

    public void setVlaga(int vlaga) {
        this.vlaga = vlaga;
    }

    public int getSvje() {
        return svjetlost;
    }

    public void setSvjetlost(int svjetlost) {
        this.svjetlost = svjetlost;
    }

    public int getVjetar() {
        return vjetar;
    }

    public void setVjetar(int vjetar) {
        this.vjetar = vjetar;
    }

    public int getBuka() {
        return buka;
    }

    public void setBuka(int buka) {
        this.buka = buka;
    }
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
