/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.bp;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;


/**
 *
 * @author grupa_1
 */
public class Vjezba_06_2 {
    private BP_Konfiguracija konfiguracija;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
                if (args.length != 1) {
            System.out.println("Premalo ili previše argumenata");
            return;
        }
        Vjezba_06_2 vjezba_06_2 = new Vjezba_06_2();
        vjezba_06_2.ispisiPodatke(args[0]);
    }
    private String usernameAdmin;
    private String url;
    private String lozinka;
    private String upit;
    
    void ispisiPodatke(String datoteka){
        
        konfiguracija = new BP_Konfiguracija(datoteka);
        if (konfiguracija == null) {
            return;
        }
        usernameAdmin = konfiguracija.getUserUsername();
        lozinka = konfiguracija.getUserPassword();
        url = konfiguracija.getServerDatabase()+konfiguracija.getUserDatabase();
        upit = "select kor_ime, prezime, ime from polaznici";
        try(
                Connection con = DriverManager.getConnection(url,usernameAdmin,lozinka);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);
              ){
            
            System.err.println("Popis: ");
            
            while (rs.next()) {                
                String mb = rs.getString("kor_ime");
                String pr = rs.getString("prezime");
                String im = rs.getString("ime");
                System.out.println(mb + " " + pr + " " + im);
            }
            rs.close();
            stmt.close();
        con.close();
        }catch(Exception e){
            System.out.println("error: " + e.getMessage());
        }
        
        
    }
    
}
