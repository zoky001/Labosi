/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
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
public class RadnaDretvaTest {

    private static final String nazivKonfiguracije = "konfiguracija.txt";
    private Socket socket;
    private String nazivDretve = "DretvaTest";
    private Konfiguracija konfig;
    private Evidencija evidencija;
    private ServerSustava serverSustava;
    private IOT iot;
    private Konfiguracija konf;
    private static final String nazivEvidencijeTest = "evidencijaTest.bin";

    public RadnaDretvaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.socket = new Socket();
        this.serverSustava = new ServerSustava();
        this.konf = createKonfig();
        this.evidencija = createEvidencija();
        this.iot = new IOT();

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setKrajRada method, of class RadnaDretva.
     */
    @Test
    public void testSetKrajRada() {
        System.out.println("setKrajRada");
        boolean b = false;
        RadnaDretva instance = new RadnaDretva(socket, nazivDretve, konf, evidencija, serverSustava, iot);
        boolean expResult = false;
        assertEquals(false, instance.isKrajRada());
        assertEquals(true, instance.setKrajRada(true));
        assertEquals(false, instance.setKrajRada(true));
        assertEquals(true, instance.isKrajRada());
    }

    /**
     * Test of isKrajRada method, of class RadnaDretva.
     */
    @Test
    public void testIsKrajRada() {
        System.out.println("isKrajRada");
        RadnaDretva instance = new RadnaDretva(socket, nazivDretve, konf, evidencija, serverSustava, iot);
        boolean expResult = false;
        assertEquals(false, instance.isKrajRada());
        assertEquals(true, instance.setKrajRada(true));
        assertEquals(false, instance.setKrajRada(true));
        assertEquals(true, instance.isKrajRada());
    }

    /**
     * Test of interrupt method, of class RadnaDretva.
     */
    @Test
    public void testInterrupt() {
        System.out.println("interrupt");
        RadnaDretva instance = new RadnaDretva(socket, nazivDretve, konf, evidencija, serverSustava, iot);
        instance.start();
        assertEquals(true, instance.isAlive());
        instance.interrupt();
        assertEquals(true, instance.isInterrupted());
    }

    /**
     * Test of run method, of class RadnaDretva.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        RadnaDretva instance = new RadnaDretva(socket, nazivDretve, konf, evidencija, serverSustava, iot);
        assertEquals(false, instance.isAlive());
        instance.start();
        assertEquals(true, instance.isAlive());
    }

    /**
     * Test of start method, of class RadnaDretva.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        RadnaDretva instance = new RadnaDretva(socket, nazivDretve, konf, evidencija, serverSustava, iot);
        assertEquals(false, instance.isAlive());
        instance.start();
        assertEquals(true, instance.isAlive());
    }

    /**
     * Test of testInputStringAndExtractUsernameAdnPassword method, of class
     * RadnaDretva.
     */
    @Test
    public void testTestInputStringAndExtractUsernameAdnPassword() {
        System.out.println("testInputStringAndExtractUsernameAdnPassword");
        String string = "KORISNIK korisnik; LOZINKA lozinka; PAUZA;";
        //KORISNIK korisnik; LOZINKA lozinka; PAUZA;
        String sintaksa = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); PAUZA;";
        RadnaDretva instance = new RadnaDretva();
        assertNull(instance.getUsername());
        assertNull(instance.getPassword());
        boolean result = instance.testInputStringAndExtractUsernameAdnPassword(string, sintaksa);
        assertEquals(true, result);
        assertEquals("korisnik", instance.getUsername());
        assertEquals("lozinka", instance.getPassword());
        string = "KORISNIK korisnik; LOZINKA lozinka; PAUZA";//nedostaje ; na kraju
        result = instance.testInputStringAndExtractUsernameAdnPassword(string, sintaksa);
        assertEquals(false, result);
    }

    /**
     * Test of testInputStringAndExtractSleepTimeAndJSON method, of class
     * RadnaDretva.
     */
    @Test
    public void testTestInputStringAndExtractSleepTimeAndJSON() {
        System.out.println("testInputStringAndExtractSleepTimeAndJSON");
        String string = "CEKAJ 5;";
        String sintaksa = "CEKAJ ([0-9]+);";
        boolean cekaj = true;
        RadnaDretva instance = new RadnaDretva();
        assertEquals(0, instance.getVrijemeCekanja());
        boolean result = instance.testInputStringAndExtractSleepTimeAndJSON(string, sintaksa, cekaj);
        assertEquals(true, result);
        assertEquals(5, instance.getVrijemeCekanja());
        sintaksa = "IOT (.*);";
        string = "IOT {\"ID\":4,\"temperatura\":32,\"vlaga\":24,\"svjetlost\":26,\"vjetar\":12,\"buka\":2};";
        cekaj = false;
        assertNull(instance.getStringJSON());
        result = instance.testInputStringAndExtractSleepTimeAndJSON(string, sintaksa, cekaj);       
        assertEquals("{\"ID\":4,\"temperatura\":32,\"vlaga\":24,\"svjetlost\":26,\"vjetar\":12,\"buka\":2}", instance.getStringJSON());
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

    private Evidencija createEvidencija() {
        try {
            evidencija = new Evidencija();
            evidencija.dodajNoviZahtjev();
            evidencija.obaviSerijalizaciju(nazivEvidencijeTest);
            return evidencija;
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerSustavaTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
