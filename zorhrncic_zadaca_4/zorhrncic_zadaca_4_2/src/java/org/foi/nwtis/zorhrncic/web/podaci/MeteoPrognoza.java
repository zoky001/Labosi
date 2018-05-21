/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.podaci;

import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zoran Hrncic
 */
public class MeteoPrognoza {

    private int id;
    private int sat;
    private Date date;
    private MeteoPodaci prognoza;

    public MeteoPrognoza() {
    }

    public MeteoPrognoza(int id, int dan, MeteoPodaci prognoza) {
        this.id = id;
        this.sat = dan;
        this.prognoza = prognoza;
        long millis = (long) sat * 1000;
        this.date = new Date(millis);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        long millis = sat * 1000;
        this.date = new Date(millis);
        this.sat = sat;
    }

    public MeteoPodaci getPrognoza() {
        return prognoza;
    }

    public void setPrognoza(MeteoPodaci prognoza) {
        this.prognoza = prognoza;
    }

}
