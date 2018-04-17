/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.kontrole;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.ServerException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author grupa_1
 */
@WebServlet(name = "Kontroler", urlPatterns = {"/Kontroler", "/PrijavaKorisnika", "/OdjavaKorisnika", "/ProvjeraKorisnika", "/IspisPodataka", "/IspisAktivnihKorisnika", "/IspisKorisnika"})
public class Kontroler extends HttpServlet {

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

        String url = request.getServletPath();
        String odlazniUrl = "";
        switch (url) {
            case "/Kontroler":
                odlazniUrl = "/jsp/index.html";
                break;
            case "/PrijavaKorisnika":
                odlazniUrl = "/jsp/login.jsp";
                break;
            case "/OdjavaKorisnika":

                //TODO odjavljuje aktivnog korisnika
                HttpSession sesija1 = request.getSession();
                sesija1.removeAttribute("NWTIS_korisnik");
                sesija1.invalidate();
                odlazniUrl = "/Kontroler";
                
                break;
            case "/ProvjeraKorisnika":
                //TODO  provjerava u tablici polaznici i ako je uspješno onda ide na /IspisPodataka
                String korime = null,
                 lozinka = null;
                korime = (String) request.getParameter("korime");
                lozinka = (String) request.getParameter("korlozinka");
         
                System.out.println("korime: " + korime);
                if (korime.equalsIgnoreCase("admin") && lozinka.equalsIgnoreCase("lozinka")) {
                    odlazniUrl = "/privatno/ispisPodataka.jsp";
                    HttpSession sesija = request.getSession();

                    Korisnik k = new Korisnik(sesija.getId(), korime, "Zoran", "Hrnčić", request.getRemoteAddr(),
                            0);
                    sesija.setAttribute("NWTIS_korisnik", k);

                } else {
                    throw new NeuspjesnaPrijava("Prijava nije uspjesna");
                }

                odlazniUrl = "/IspisPodataka";

                //TODO ako nije gen iznimku
                break;
            case "/IspisPodataka":
                odlazniUrl = "/privatno/ispisPodataka.jsp";
                break;

            case "/IspisAktivnihKorisnika":
                odlazniUrl = "/admin/ispisAktivnihKorisnika.jsp";
                break;
            case "/IspisKorisnika":
                odlazniUrl = "/admin/ispisKorisnika.jsp";
                break;
            default:
                odlazniUrl = null;
        }
        if (odlazniUrl == null) {
            throw new ServerException("Nepoznata adresa prusmjeravanja! : '" + odlazniUrl + "'!'");
        } else {
            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(odlazniUrl);
            dispatcher.forward(request, response);

        }
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
