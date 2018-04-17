/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.slusaci;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.foi.nwtis.zorhrncic.web.kontrole.Korisnik;

/**
 * Web application lifecycle listener.
 *
 * @author grupa_1
 */
public class SlusacSesije implements HttpSessionListener, HttpSessionAttributeListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {

        if (event.getName().compareTo("NWTIS_korisnik") == 0) {

            ServletContext sc = event.getSession().getServletContext();

            List<Korisnik> lista = null;
            Object o = sc.getAttribute("PRIJAVLJENI_KORISNICI");
            //(List<Korisnik>)
            if (o == null) {
                lista = new ArrayList<>();
            } else if (o instanceof List) {
                lista = (List<Korisnik>) o;

            }
            // Korisnik k = (Korisnik) event.getValue();
            if (event.getValue() instanceof Korisnik) {
                Korisnik k = (Korisnik) event.getValue();
                lista.add(k);
                sc.setAttribute("PRIJAVLJENI_KORISNICI", lista);
                System.out.println("Korisnik prijavnljen: " + k.getIme());
            } else {
                System.err.println("Nepoznata klasa u sesiji");
            }

        }

    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (event.getName().compareTo("NWTIS_korisnik") == 0) {

            ServletContext sc = event.getSession().getServletContext();

            List<Korisnik> lista = null;
            Object o = sc.getAttribute("PRIJAVLJENI_KORISNICI");
            //(List<Korisnik>)
            if (o == null) {
                lista = new ArrayList<>();
            } else if (o instanceof List) {
                lista = (List<Korisnik>) o;

            }
            // Korisnik k = (Korisnik) event.getValue();
            if (event.getValue() instanceof Korisnik) {
                
                Korisnik k = (Korisnik) event.getValue();
                
                lista.remove(k);             
                sc.setAttribute("PRIJAVLJENI_KORISNICI", lista);
                System.out.println("Korisnik prijavnljen: " + k.getIme());
            } else {
                System.err.println("Nepoznata klasa u sesiji");
            }

        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
    }
}
