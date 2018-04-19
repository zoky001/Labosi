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
import javax.ejb.Local;
import javax.faces.context.FacesContext;

/**
 *
 * @author grupa_1
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {

    private String odabraniJezik;

    public String getOdabraniJezik() {
        odabraniJezik = FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
        return odabraniJezik;
    }
    
    /**
     * Creates a new instance of Lokalizacija
     */
    public Lokalizacija() {
    }
    
    public Object odaberiJezik(String jezik){
        Locale local= new Locale(jezik);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(local);
    return "";
    
    }
    
     public String saljiPoruku(){
         return "saljiPoruku";
     }
     
     
     public String pregledPoruka(){ 
         return "pregledPoruka" ;
     }
     
     public String pregledDnevnika() {
         return "pregledDnevnika";
     }
}
