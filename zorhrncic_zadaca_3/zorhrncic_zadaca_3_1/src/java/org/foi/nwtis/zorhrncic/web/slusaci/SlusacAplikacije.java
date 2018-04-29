/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.slusaci;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.PreuzmiMeteoPodatke;

/**
 * Web application lifecycle listener.
 *
 * @author grupa_1
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    private String datoteka;
    private BP_Konfiguracija konfiguracija;
    private PreuzmiMeteoPodatke meteoPodatke;
    private static ServletContext servletContext;
    private Object konfiguracijaSve;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            //

            
            ServletContext context = sce.getServletContext();
            this.servletContext = context;
            datoteka = context.getInitParameter("konfiguracija");
            String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;
            konfiguracija = new BP_Konfiguracija(putanja + datoteka); //BP config data
            konfiguracijaSve = KonfiguracijaApstraktna.preuzmiKonfiguraciju(putanja + datoteka);//all config data
            context.setAttribute("BP_Konfig", konfiguracija);
            context.setAttribute("All_Konfig", konfiguracijaSve);
           // meteoPodatke = new PreuzmiMeteoPodatke();
           // meteoPodatke.start();
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       if (meteoPodatke != null && meteoPodatke.isAlive()) {

            meteoPodatke.interrupt();

        }
        ServletContext context = sce.getServletContext();
        context.removeAttribute("BP_Konfig");
        context.removeAttribute("All_Konfig");

    }

    public static ServletContext getServletContext() {
        return servletContext;
    }
}
