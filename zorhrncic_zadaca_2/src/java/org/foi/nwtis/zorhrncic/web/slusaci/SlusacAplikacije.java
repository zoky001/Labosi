/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.slusaci;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.dretve.ObradaPoruka;

/**
 * Web application lifecycle listener.
 *
 * @author grupa_1
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    private String datoteka;
    private BP_Konfiguracija konfiguracija;
    private ObradaPoruka obradaPoruka = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
// 

        ServletContext context = sce.getServletContext();
        datoteka = context.getInitParameter("konfiguracija");
        String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;
        konfiguracija = new BP_Konfiguracija(putanja + datoteka);
        context.setAttribute("BP_Konfig", konfiguracija);

        obradaPoruka = new ObradaPoruka();
       
//TODO mkani kom nakon što james bude instaliran, i konfiguriran        
//obradaPoruka.start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        if (obradaPoruka != null && obradaPoruka.isAlive()) {

            obradaPoruka.interrupt();

        }
        ServletContext context = sce.getServletContext();
        context.removeAttribute("BP_Konfig");

    }
}
