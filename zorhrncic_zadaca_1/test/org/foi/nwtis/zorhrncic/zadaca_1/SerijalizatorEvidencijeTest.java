/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Zoran
 */
public class SerijalizatorEvidencijeTest {

    private static final String nazivKonfiguracije = "konfiguracija.txt";
    private static final String nazivEvidencijeTest = "evidencijaTest.bin";
    private Evidencija evidencija;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private Konfiguracija konf;
    ServerSustava serverSustava;

    public SerijalizatorEvidencijeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.konf = createKonfig();
    }

    @After
    public void tearDown() {
        try {

            File file = new File(nazivKonfiguracije);

            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }
            file = new File(nazivEvidencijeTest);

            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    /**
     * Test of setKrajRada method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testSetKrajRada() {
        System.out.println("setKrajRada");
        boolean b = false;
        SerijalizatorEvidencije instance = new SerijalizatorEvidencije("naziv", konf, evidencija);
        assertEquals(false, instance.isKrajRada());
        boolean result = instance.setKrajRada(true);
        assertEquals(true, result);
        result = instance.setKrajRada(true);
        assertEquals(false, result);
        assertEquals(true, instance.isKrajRada());
    }

    /**
     * Test of isKrajRada method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testIsKrajRada() {
        System.out.println("isKrajRada");
        SerijalizatorEvidencije instance = new SerijalizatorEvidencije();
        assertEquals(false, instance.isKrajRada());
        boolean result = instance.setKrajRada(true);
        assertEquals(true, result);
        result = instance.setKrajRada(true);
        assertEquals(false, result);
        assertEquals(true, instance.isKrajRada());
    }

    /**
     * Test of interrupt method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testInterrupt() {
        System.out.println("interrupt");
        SerijalizatorEvidencije instance = new SerijalizatorEvidencije();
        assertEquals(false, instance.isAlive());
        assertEquals(false, instance.isInterrupted());

    }



    private static Konfiguracija createKonfig() {

        try {
            KonfiguracijaApstraktna.kreirajKonfiguraciju(nazivKonfiguracije);
            Konfiguracija konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivKonfiguracije);
            konf.spremiPostavku("port", "8000");
            konf.spremiPostavku("maks.broj.zahtjeva.cekanje", "50");
            konf.spremiPostavku("maks.broj.radnih.dretvi", "50");
            konf.spremiPostavku("datoteka.evidencije.rada", nazivEvidencijeTest);
            konf.spremiPostavku("interval.za.serijalizaciju", "60");
            konf.spremiPostavku("admin.1.zorgrdjan", "123456");
            konf.spremiPostavku("admin.0.zorhrncic", "123456");
            konf.spremiPostavku("admin.2.ivicelig", "123456");

            konf.spremiPostavku("admin.3.matbodulusic", "123456");
            konf.spremiPostavku("admin.4.nikbukovec", "123456");
            konf.spremiPostavku("admin.5.dkermek", "123456");
            konf.spremiKonfiguraciju();
            System.out.println("Property NAme: " + konf.dajPostavku("maks.broj.radnih.dretvi"));
            System.out.println("Property NAme: " + konf.dajPostavku("port"));
            return konf;
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {

        }

        return null;
    }

    private void createEvidencija() {
        try {
            evidencija = new Evidencija();
            evidencija.dodajNoviZahtjev();
            evidencija.obaviSerijalizaciju(nazivEvidencijeTest);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerSustavaTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
