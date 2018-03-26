/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.BindException;
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
public class ServerSustavaTest {

    private final String nazivKonfiguracije = "konfiguracija.txt";
    private final String nazivEvidencijeTest = "evidencijaTest.bin";
    private Evidencija evidencija;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private Konfiguracija konf;

    public ServerSustavaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        createKonfig();
     
        createEvidencija();
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
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
     * Test of addDretvaCekaj method, of class ServerSustava.
     */
    @Test
    public void testAddDretvaCekaj() {
        System.out.println("addDretvaCekaj");
        RadnaDretva b = new RadnaDretva();
        RadnaDretva c = new RadnaDretva();
        ServerSustava instance = new ServerSustava();
        instance.addDretvaCekaj(b);
        instance.addDretvaCekaj(c);
        assertEquals(2, instance.getDretveCekaj().size());
        assertEquals(true, instance.getDretveCekaj().contains(c));
        assertEquals(true, instance.getDretveCekaj().contains(b));
    }

    /**
     * Test of removeDretvaCekaj method, of class ServerSustava.
     */
    @Test
    public void testRemoveDretvaCekaj() {
        System.out.println("removeDretvaCekaj");
        RadnaDretva b = new RadnaDretva();
        RadnaDretva c = new RadnaDretva();
        ServerSustava instance = new ServerSustava();
        instance.addDretvaCekaj(b);
        instance.addDretvaCekaj(c);
        assertEquals(true, instance.getDretveCekaj().contains(b));
        assertEquals(2, instance.getDretveCekaj().size());
        instance.removeDretvaCekaj(b);
        assertEquals(false, instance.getDretveCekaj().contains(b));
        assertEquals(1, instance.getDretveCekaj().size());

    }

    /**
     * Test of setKrajRada method, of class ServerSustava.
     */
    @Test
    public void testSetKrajRada() {
        System.out.println("setKrajRada");
        boolean b = true;
        ServerSustava instance = new ServerSustava();

        boolean result = instance.setKrajRada(b);
        assertEquals(true, result);
        assertEquals(b, instance.isKrajRada());
        result = instance.setKrajRada(b);
        assertEquals(false, result);
        assertEquals(b, instance.isKrajRada());

    }

    /**
     * Test of isKrajRada method, of class ServerSustava.
     */
    @Test
    public void testIsKrajRada() {
        System.out.println("isKrajRada");
        ServerSustava instance = new ServerSustava();
        boolean expResult = false;
        boolean result = instance.isKrajRada();
        assertEquals(expResult, result);
        instance.setKrajRada(true);
        result = instance.isKrajRada();
        assertEquals(true, result);
    }

    /**
     * Test of beginStoppingServer method, of class ServerSustava.
     */
    @Test
    public void testBeginStoppingServer() {
        System.out.println("beginStoppingServer");
        ServerSustava instance = new ServerSustava();
        boolean expResult = true;
        boolean result = instance.beginStoppingServer();
        assertEquals(expResult, result);
        result = instance.beginStoppingServer();
        assertEquals(false, result);
        assertEquals(true, instance.isStopRequest());
    }

    /**
     * Test of isStopRequest method, of class ServerSustava.
     */
    @Test
    public void testIsStopRequest() {
        System.out.println("isStopRequest");
        ServerSustava instance = new ServerSustava();
        assertEquals(false, instance.isStopRequest());
        instance.beginStoppingServer();
        assertEquals(true, instance.isStopRequest());
    }

    /**
     * Test of isPause method, of class ServerSustava.
     */
    @Test
    public void testIsPause() {
        System.out.println("isPause");
        ServerSustava instance = new ServerSustava();
        assertEquals(false, instance.isPause());
        instance.setServerPause();
        assertEquals(true, instance.isPause());

    }

    /**
     * Test of setServerPause method, of class ServerSustava.
     */
    @Test
    public void testSetServerPause() {
        System.out.println("setServerPause");
        ServerSustava instance = new ServerSustava();
        assertEquals(false, instance.isPause());
        assertEquals(true, instance.setServerPause());
        assertEquals(true, instance.isPause());
        assertEquals(false, instance.setServerPause());
    }

    /**
     * Test of setServerStart method, of class ServerSustava.
     */
    @Test
    public void testSetServerStart() {
        System.out.println("setServerStart");
        ServerSustava instance = new ServerSustava();
        assertEquals(false, instance.isPause());
        assertEquals(false, instance.setServerStart());
        assertEquals(false, instance.isPause());
        instance.setServerPause();
        assertEquals(true, instance.isPause());
        assertEquals(true, instance.setServerStart());
        assertEquals(false, instance.isPause());
    }

    /**
     * Test of main method, of class ServerSustava.
     */
    @Test
    public void testMainNemaKonfiguracije() {
        System.out.println("main");
        String[] args = {"NWTiS_zorhrncic.txt", "sds"};
        ServerSustava.main(args);
        System.out.println("main ispis\n" + outContent.toString());
    }

    /**
     * Test of ucitajEvidenciju method, of class ServerSustava.
     */
    @Test(expected = NemaKonfiguracije.class)
    public void testUcitajEvidenciju() throws Exception {
        System.out.println("ucitajEvidenciju");
        ServerSustava instance = new ServerSustava();
        instance.ucitajEvidenciju(nazivEvidencijeTest);
        assertNotNull(instance.getEvidencija());
        ServerSustava instance1 = new ServerSustava();
        instance.ucitajEvidenciju("NazivNepostojecaEvidencija");
    }

    /**
     * Test of smanjiBrojRadnihDretvi method, of class ServerSustava.
     */
    @Test
    public void testSmanjiBrojRadnihDretvi() {
        System.out.println("smanjiBrojRadnihDretvi");
        ServerSustava instance = new ServerSustava();
        assertEquals(0, instance.getBrojRadnihDretvi());
        instance.smanjiBrojRadnihDretvi();
        assertEquals(-1, instance.getBrojRadnihDretvi());
    }

    /**
     * Test of getBrojRadnihDretvi method, of class ServerSustava.
     */
    @Test
    public void testGetBrojRadnihDretvi() {
        System.out.println("getBrojRadnihDretvi");
        ServerSustava instance = new ServerSustava();
        assertEquals(0, instance.getBrojRadnihDretvi());
        instance.smanjiBrojRadnihDretvi();
        assertEquals(-1, instance.getBrojRadnihDretvi());
    }

    /**
     * Test of zaustaviServer method, of class ServerSustava.
     */
    @Test
    public void testZaustaviServer() {
        System.out.println("zaustaviServer");
      //  ServerSustava instance = new ServerSustava();
    
      //  instance.pokreniPosluzitelj(konf);
       // System.out.println("KONF: " + konf.dajPostavku("port"));
      //  instance.setEvidencija(evidencija);
        boolean expResult = true;
      //  boolean result = instance.zaustaviServer(konf);
        //assertEquals(expResult, result);
    
    }

    private void createKonfig() {

        try {
            KonfiguracijaApstraktna.kreirajKonfiguraciju(nazivKonfiguracije);
           konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivKonfiguracije);
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

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
           
        }
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
