/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.File;
import java.nio.charset.Charset;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
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
public class EvidencijaTest {

    private String nazivDatotekeZaSerijalizaciju = "SerijalizacijaTEst.bin";

    public EvidencijaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        try {

            File file = new File(nazivDatotekeZaSerijalizaciju);

            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @After
    public void tearDown() {
        try {

            File file = new File(nazivDatotekeZaSerijalizaciju);

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
     * Test of isUpis method, of class Evidencija.
     */
    @Test
    public void testIsUpis() {
        System.out.println("isUpis");
        Evidencija instance = new Evidencija();
        assertFalse(instance.isUpis());
        instance.setUpis(true);
        assertTrue(instance.isUpis());
    }

    /**
     * Test of setUpis method, of class Evidencija.
     */
    @Test
    public void testSetUpis() {
        System.out.println("setUpis");
        boolean upis = false;
        Evidencija instance = new Evidencija();
        assertFalse(instance.isUpis());
        instance.setUpis(true);
        assertTrue(instance.isUpis());
        instance.setUpis(false);
        assertFalse(instance.isUpis());
    }

    /**
     * Test of dodajUspjesnoObavljenZahtjev method, of class Evidencija.
     */
    @Test
    public void testDodajUspjesnoObavljenZahtjev() {
        System.out.println("dodajUspjesnoObavljenZahtjev");
        Evidencija instance = new Evidencija();
        assertEquals(0, instance.getBrojUspjesnihZahtjeva());
        instance.dodajUspjesnoObavljenZahtjev();
        assertEquals(1, instance.getBrojUspjesnihZahtjeva());
    }

    /**
     * Test of dodajOdbijenZahtjevJerNemaDretvi method, of class Evidencija.
     */
    @Test
    public void testDodajOdbijenZahtjevJerNemaDretvi() {
        System.out.println("dodajOdbijenZahtjevJerNemaDretvi");
        Evidencija instance = new Evidencija();

        assertEquals(0, instance.getBrojPrkinutihZahtjeva());
        instance.dodajOdbijenZahtjevJerNemaDretvi();
        assertEquals(1, instance.getBrojPrkinutihZahtjeva());
    }

    /**
     * Test of dodajNoviZahtjev method, of class Evidencija.
     */
    @Test
    public void testDodajNoviZahtjev() {
        System.out.println("dodajNoviZahtjev");
        Evidencija instance = new Evidencija();

        assertEquals(0, instance.getUkupanbrojZahtjeva());
        instance.dodajNoviZahtjev();
        assertEquals(1, instance.getUkupanbrojZahtjeva());
    }

    /**
     * Test of dodajNeispravanZahtjev method, of class Evidencija.
     */
    @Test
    public void testDodajNeispravanZahtjev() {
        System.out.println("dodajNeispravanZahtjev");
        Evidencija instance = new Evidencija();

        assertEquals(0, instance.getBrojNeispravnihZahtjeva());
        instance.dodajNeispravanZahtjev();
        assertEquals(1, instance.getBrojNeispravnihZahtjeva());
    }

    /**
     * Test of dodajVrijemeRadaDretve method, of class Evidencija.
     */
    @Test
    public void testDodajVrijemeRadaDretve() {
        System.out.println("dodajVrijemeRadaDretve");
        long sec = 0L;
        Evidencija instance = new Evidencija();
        instance.dodajVrijemeRadaDretve(sec);
        assertEquals(0, instance.getUkupnoVrijemeRadaRadnihDretvi());
        instance.dodajVrijemeRadaDretve(10);
        assertEquals(10, instance.getUkupnoVrijemeRadaRadnihDretvi());
        instance.dodajVrijemeRadaDretve(10);
        assertEquals(20, instance.getUkupnoVrijemeRadaRadnihDretvi());
    }

    /**
     * Test of obaviSerijalizaciju method, of class Evidencija.
     */
    @Test
    public void testObaviSerijalizaciju() throws Exception {
        System.out.println("obaviSerijalizaciju");

        File datKonf = new File(nazivDatotekeZaSerijalizaciju);
        assertFalse(datKonf.exists());
        assertFalse(datKonf.isDirectory());
        Evidencija instance = new Evidencija();
        instance.obaviSerijalizaciju(nazivDatotekeZaSerijalizaciju);
        assertTrue(datKonf.exists());
        assertFalse(datKonf.isDirectory());

    }

    /**
     * Test of dodajNedozvoljeniZahtjev method, of class Evidencija.
     */
    @Test
    public void testDodajNedozvoljeniZahtjev() {
        System.out.println("dodajNedozvoljeniZahtjev");
        Evidencija instance = new Evidencija();

        assertEquals(0, instance.getBrojNedozvoljenihZahtjeva());
        instance.dodajNedozvoljeniZahtjev();
        assertEquals(1, instance.getBrojNedozvoljenihZahtjeva());
    }

}
