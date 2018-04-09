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
public class Vjezba_06_3 {

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
        Vjezba_06_3 vjezba_06_2 = new Vjezba_06_3();
        vjezba_06_2.ispisiPodatke(args[0]);
    }
    private String usernameAdmin;
    private String url;
    private String lozinka;
    private String upit;

    void ispisiPodatke(String datoteka) {

        konfiguracija = new BP_Konfiguracija(datoteka);
        if (konfiguracija == null) {
            return;
        }
        usernameAdmin = konfiguracija.getAdminUsername();
        lozinka = konfiguracija.getAdminPassword();
        url = konfiguracija.getServerDatabase() + konfiguracija.getUserDatabase();
        upit = "create table test_zorhrncic ("
                + "kor_ime varchar(10) NOT NULL DEFAULT '',"
                + "zapis varchar(250) NOT NULL DEFAULT ''"
                + ")";
        try (
                Connection con = DriverManager.getConnection(url, usernameAdmin, lozinka);
                Statement stmt = con.createStatement();) {
            boolean start = stmt.execute(upit);

            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

    }

}
