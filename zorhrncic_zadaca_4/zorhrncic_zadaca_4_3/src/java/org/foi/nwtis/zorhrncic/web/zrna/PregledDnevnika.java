/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import org.foi.nwtis.zorhrncic.ejb.eb.Parkiralista;
import org.foi.nwtis.zorhrncic.ejb.sb.DnevnikFacade;
import org.foi.nwtis.zorhrncic.ejb.sb.MeteoKlijentZrno;
import org.foi.nwtis.zorhrncic.ejb.sb.ParkiralistaFacade;
import org.foi.nwtis.zorhrncic.web.kontrole.Izbornik;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPrognoza;
import org.foi.nwtis.zorhrncic.web.podaci.Parkiraliste;

/**
 *
 * @author grupa_1
 */
@Named(value = "pregledDnevnika")
@SessionScoped
public class PregledDnevnika implements Serializable {

    @EJB
    private DnevnikFacade dnevnikFacade;



    /**
     * Creates a new instance of Pregled
     */
    public PregledDnevnika() {
    }

    
    
}
