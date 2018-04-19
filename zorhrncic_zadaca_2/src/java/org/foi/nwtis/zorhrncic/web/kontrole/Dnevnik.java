/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.kontrole;

import java.util.Date;

/**
 *
 * @author dkermek
 */
public class Dnevnik {

    private int id;
    private String sadrzaj;
    private Date vrijemeZapisa;

    public Dnevnik(int id, String sadrzaj, Date vrijemeZapisa) {
        this.id = id;
        this.sadrzaj = sadrzaj;
        this.vrijemeZapisa = vrijemeZapisa;
    }

    public int getId() {
        return id;
    }

    public String getSadrzaj() {
        return sadrzaj;
    }

    public Date getVrijemeZapisa() {
        return vrijemeZapisa;
    }
}
