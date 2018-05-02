/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.slusaci;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;

/**
 * Web application lifecycle listener.
 *
 * @author grupa_1
 */
public class SlusacAplikacije implements ServletContextListener {

    private String datoteka;
    private BP_Konfiguracija konfiguracija;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
// 

        ServletContext context = sce.getServletContext();
        datoteka = context.getInitParameter("konfiguracija");
        String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;
        konfiguracija = new BP_Konfiguracija(putanja + datoteka);
        context.setAttribute("BP_Konfig", konfiguracija);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute("BP_Konfig");

    }
}
