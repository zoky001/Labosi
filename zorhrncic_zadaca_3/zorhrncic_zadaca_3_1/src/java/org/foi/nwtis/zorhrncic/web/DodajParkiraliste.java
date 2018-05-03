/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.rest.klijenti.GMKlijent;
import org.foi.nwtis.zorhrncic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.slusaci.SlusacAplikacije;

/**
 * Servelet koji prima podatke iz forme, vrši obradu vezanu za dodavanje
 * parkiališta u bazu podataka, dohvacanje lokacije..
 *
 * Prosljedjuje zahtjec na index.jsp
 *
 * @author Zoran
 */
@WebServlet(name = "DodajParkiraliste", urlPatterns = {"/DodajParkiraliste"})
public class DodajParkiraliste extends HttpServlet {

    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private String usernameAdmin;
    private String lozinka;
    private String url;
    private String uprProgram;
    private String gm_apiKey;
    private String OWM_apikey;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        preuzmiKonfiuraciju();
        request.setCharacterEncoding("UTF-8");
        if (request.getParameter("geolokacija") != null) {
            if (getGeolocation(request)) {
                request.setAttribute("message", "Uspješno dohvaćeni podatci");
            } else {
                request.setAttribute("message", "Greška prilikom dohvaćanja podataka");
            }
        } else if (request.getParameter("spremi") != null) {
            if (saveParking(request)) {
                request.setAttribute("message", "Uspješno dodano parkiralište");
            } else {
                request.setAttribute("message", "Neuspješno dodano parkiralište. Več postoji sa istim nazivom.");
            }//if condition
        } else if (request.getParameter("meteo") != null) {
            if (getMeteoData(request)) {
                request.setAttribute("message", "Uspješno dohvačeni podatci");
            } else {
                request.setAttribute("message", "Greška prilikom dohvaćanja podataka");
            }
        }
        request.setAttribute("hidden_class", "show");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * dohvaćanje geolokacije putem WS na temelju adrese iz zahtjeva.
     *
     * postavljanje podataka lokacije u atribute cije vrijednosti se prikazuju
     * na index.jsp
     *
     * @param request
     * @return
     */
    private boolean getGeolocation(HttpServletRequest request) {
        boolean success = false;
        try {
            String naziv = request.getParameter("naziv");
            String adresa = request.getParameter("adresa");
            GMKlijent gmk = new GMKlijent(gm_apiKey);
            Lokacija lok = gmk.getGeoLocation(adresa);
            request.setAttribute("naziv", naziv);
            request.setAttribute("adresa", adresa);
            request.setAttribute("geoLoc", lok.getLatitude() + " " + lok.getLongitude());
            request.setAttribute("geoLocLon", lok.getLongitude());
            request.setAttribute("geoLocLat", lok.getLatitude());
            success = true;
        } catch (Exception e) {
        }
        return success;
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /**
     * Preuzimanje podataka iz konfiguracije i pohranjivanje u globalne
     * varijable.
     *
     */
    private void preuzmiKonfiuraciju() {

        ServletContext servletContext = (ServletContext) SlusacAplikacije.getServletContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data
        usernameAdmin = konfiguracijaBaza.getAdminUsername();
        lozinka = konfiguracijaBaza.getAdminPassword();
        url = konfiguracijaBaza.getServerDatabase() + konfiguracijaBaza.getAdminDatabase();
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        gm_apiKey = konfiguracija.dajPostavku("gmapikey");
        OWM_apikey = konfiguracija.dajPostavku("apikey");

    }

    /**
     * PReuzimanje podataka o parkiralistu iz zahtjeva i pohrana istih u bazu podataka
     * 
     * @param request
     * @return 
     */
    private boolean saveParking(HttpServletRequest request) {
        boolean succes = false;
        try {
            String naziv = request.getParameter("naziv");
            String adresa = request.getParameter("adresa");
            String geoLocLon = request.getParameter("geoLocLon");
            String geoLocLat = request.getParameter("geoLocLat");
            request.setAttribute("naziv", naziv);
            request.setAttribute("adresa", adresa);
            request.setAttribute("geoLoc", request.getParameter("geoLocLat") + " " + request.getParameter("geoLocLon"));
            request.setAttribute("geoLocLon", request.getParameter("geoLocLon"));
            request.setAttribute("geoLocLat", request.getParameter("geoLocLat"));
            if (!checkIfExistParkingByName(naziv)) {
                succes = addParkingnInDatabase(naziv, adresa, geoLocLat, geoLocLon);
            }
        } catch (Exception e) {

        }
        return succes;
    }

    /**
     * Dohvacanje MEteo podataka prema lokaciji upisanog parkiralista.
     * 
     * Pohranjivanje istih podatak au atribute čije se vrijednosti prikazuju u index.jsp
     * @param request
     * @return 
     */
    private boolean getMeteoData(HttpServletRequest request) {
        boolean succes = false;
        try {
            String naziv = request.getParameter("naziv");
            String adresa = request.getParameter("adresa");
            String geoLocLon = request.getParameter("geoLocLon");
            String geoLocLat = request.getParameter("geoLocLat");
            GMKlijent gmk = new GMKlijent(gm_apiKey);
            Lokacija lok = gmk.getGeoLocation(adresa);
            OWMKlijent owmk = new OWMKlijent(OWM_apikey);
            MeteoPodaci mp = owmk.getRealTimeWeather(lok.getLatitude(), lok.getLongitude());
            request.setAttribute("temp", mp.getTemperatureValue() + " " + mp.getTemperatureUnit());
            request.setAttribute("vlaga", mp.getHumidityValue() + " " + mp.getHumidityUnit());
            request.setAttribute("tlak", mp.getPressureValue() + " " + mp.getPressureUnit());
            request.setAttribute("naziv", naziv);
            request.setAttribute("adresa", adresa);
            request.setAttribute("geoLoc", request.getParameter("geoLocLat") + " " + request.getParameter("geoLocLon"));
            request.setAttribute("geoLocLon", request.getParameter("geoLocLon"));
            request.setAttribute("geoLocLat", request.getParameter("geoLocLat"));
            succes = true;
        } catch (Exception e) {

        }
        return succes;
    }

    
       /**
     * Provjera postoji li u bazi podataka parkiraliste sa prosljedjenim
     * nazivom.
     *
     * @param name - naziv parkiralista
     * @return true - postoji; false - ne postoji
     */
    private boolean checkIfExistParkingByName(String name) {
        String upit = "SELECT * FROM PARKIRALISTA WHERE NAZIV = '" + name + "'";
        boolean exist = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {

        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                exist = true;
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return exist;
        }
    }

      /**
     * Upis parkiralista u bazu podataka.
     *
     * @param name - naziv parkiralista
     * @param address - adresa park
     * @param lat - latitude
     * @param lon - longitude
     * @return @return true - uspjeh; false - neuspjeh
     */
    private boolean addParkingnInDatabase(String name, String address, String lat, String lon) {
        String upit = "INSERT INTO PARKIRALISTA (NAZIV, ADRESA, LATITUDE, LONGITUDE) \n"
                + "	VALUES ('" + name + "', '" + address + "', " + lat + ", " + lon + ")";
        boolean success = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {

        }
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();) {
            stmt.execute(upit);
            success = true;
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return success;
        }

    }

}
