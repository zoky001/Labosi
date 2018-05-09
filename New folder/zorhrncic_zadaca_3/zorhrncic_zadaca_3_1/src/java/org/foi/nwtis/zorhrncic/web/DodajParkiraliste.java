/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.zorhrncic.web.podaci.GMKlijent;
import org.foi.nwtis.zorhrncic.web.podaci.Lokacija;
import org.foi.nwtis.zorhrncic.web.podaci.MeteoPodaci;
import org.foi.nwtis.zorhrncic.web.podaci.OWMKlijent;

/**
 *
 * @author grupa_1
 */
@WebServlet(name = "DodajParkiraliste", urlPatterns = {"/DodajParkiraliste"})
public class DodajParkiraliste extends HttpServlet {

    private String datoteka;
    private BP_Konfiguracija konfiguracija;
    private String usernameAdmin;
    private String lozinka;
    private String url;
    private String upit;

  

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
        String naziv, adresa;// = get
        naziv = request.getParameter("naziv");
        adresa = request.getParameter("adresa");
        GMKlijent gmk = new GMKlijent();
        Lokacija lok = gmk.getGeoLocation(adresa);
        
         konfiguracija = (BP_Konfiguracija)this.getServletContext().getAttribute("BP_Konfig");
        if (konfiguracija == null) {
            return;
        }
        usernameAdmin = konfiguracija.getUserUsername();
        lozinka = konfiguracija.getUserPassword();
        url = konfiguracija.getServerDatabase()+konfiguracija.getUserDatabase();
        upit = "INSERT INTO parkiralista (naziv,adresa,latitude,longitude) "
                + "VALUES ("+naziv+","+adresa+","+lok.getLatitude()+","+lok.getLongitude()+")";
        try(
                Connection con = DriverManager.getConnection(url,usernameAdmin,lozinka);
                Statement stmt = con.createStatement();
              
              ){
              stmt.execute(upit);
            System.err.println("Popis: ");
           
            stmt.close();
        con.close();
        }catch(Exception e){
            System.out.println("error: " + e.getMessage());
        }
        
        OWMKlijent owmk = new OWMKlijent("7505f1b2a843433f4c408932f2d4300d");
        MeteoPodaci met = owmk.getRealTimeWeather(lok.getLatitude(), lok.getLongitude());
        System.out.println("TEmp: " + met.getTemperatureUnit());
        
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

}
