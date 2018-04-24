/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.faces.context.FacesContext;

/**
 *
 * @author grupa_1
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {

    private static final long serialVersionUID = 1L;
    private String odabraniJezik;
    private Locale locale;

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }

    public String getOdabraniJezik() {
        odabraniJezik = FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
        return odabraniJezik;
    }

    public void setOdabraniJezik(String odabraniJezik) {
        this.odabraniJezik = odabraniJezik;
    }

    /**
     * Creates a new instance of Lokalizacija
     */
   /* public Lokalizacija() {
        String odabraniJezik1 = FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
        odabraniJezik = odabraniJezik1;
        Locale local = new Locale(odabraniJezik1);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(local);
    }
*/
    public Object odaberiJezik(String jezik) {
        locale = new Locale(jezik);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        return "";

    }

    public String saljiPoruku() {
        return "saljiPoruku";
    }

    public String pregledPoruka() {
        return "pregledPoruka";
    }

    public String pregledDnevnika() {
        return "pregledDnevnika";
    }
}
