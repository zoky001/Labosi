/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;
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

/**
 *
 * @author Zoran
 */
public class KlijentSustavaTest {

    private static ServerSustava ss;
    private static Konfiguracija konfig;
    private static Thread thread;

    private Properties upisaniAurumenti;

    public KlijentSustavaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        String datotekaKonfig = "NWTiS_zorhrncic.txt";
        thread = new Thread() {
            public void run() {
                System.out.println("Thread Running");
                try {
                    konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfig);
                    ss = new ServerSustava();
                    ss.pokreniPosluzitelj(konfig);
                } catch (NemaKonfiguracije ex) {
                    System.out.println("Ne postoji datoteka konfiguracije!!");
                    return;
                } catch (NeispravnaKonfiguracija ex) {
                    System.out.println("Greška u datoteci konfiguracije!!");
                    return;
                }
            }
        };
        thread.start();
    }

    @AfterClass
    public static void tearDownClass() {
        thread.interrupt();
    }

    @Before
    public void setUp() {
        upisaniAurumenti = new Properties();
        upisaniAurumenti.setProperty("adresa", "127.0.0.1");
        upisaniAurumenti.setProperty("port", "8000");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of preuzmiKontrolu method, of class KlijentSustava.
     */
    @Test()
    public void testPreuzmiKontrolu() {
        System.out.println("preuzmiKontrolu");
        KlijentSustava instance = new KlijentSustava(upisaniAurumenti);
        assertNull(instance.getSocket());
        instance.preuzmiKontrolu();
        assertNotNull(instance.getSocket());
    }
    
}
