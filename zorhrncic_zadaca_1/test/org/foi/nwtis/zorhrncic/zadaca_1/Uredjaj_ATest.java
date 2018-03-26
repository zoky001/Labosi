/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

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
public class Uredjaj_ATest {

    public Uredjaj_ATest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getID method, of class Uredjaj_A.
     */
    @Test
    public void testGetID() {
        System.out.println("getID");
        Uredjaj_A instance = new Uredjaj_A();
        int expResult = 0;
        instance.setID(expResult);
        int result = instance.getID();
        assertEquals(expResult, result);
    }

    /**
     * Test of setID method, of class Uredjaj_A.
     */
    @Test
    public void testSetID() {
        System.out.println("setID");
        int ID = 0;
        Uredjaj_A instance = new Uredjaj_A();
        instance.setID(ID);
        int result = instance.getID();
        assertEquals(ID, result);
    }

    /**
     * Test of getTemp method, of class Uredjaj_A.
     */
    @Test
    public void testGetTemp() {
        System.out.println("getTemp");
        Uredjaj_A instance = new Uredjaj_A();
        int expResult = 0;
        instance.setTemperatura(expResult);
        int result = instance.getTemp();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTemperatura method, of class Uredjaj_A.
     */
    @Test
    public void testSetTemperatura() {
        System.out.println("setTemperatura");
        int temperatura = 0;
        Uredjaj_A instance = new Uredjaj_A();
        instance.setTemperatura(temperatura);
        int result = instance.getTemp();
        assertEquals(temperatura, result);
    }

    /**
     * Test of getVlaga method, of class Uredjaj_A.
     */
    @Test
    public void testGetVlaga() {
        System.out.println("getVlaga");
        Uredjaj_A instance = new Uredjaj_A();
        int expResult = 0;
        instance.setVlaga(expResult);
        int result = instance.getVlaga();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVlaga method, of class Uredjaj_A.
     */
    @Test
    public void testSetVlaga() {
        System.out.println("setVlaga");
        int vlaga = 0;
        Uredjaj_A instance = new Uredjaj_A();
        instance.setVlaga(vlaga);
        int result = instance.getVlaga();
        assertEquals(vlaga, result);
    }

    /**
     * Test of getSvje method, of class Uredjaj_A.
     */
    @Test
    public void testGetSvje() {
        System.out.println("getSvje");
        Uredjaj_A instance = new Uredjaj_A();
        int expResult = 0;
        instance.setSvjetlost(expResult);
        int result = instance.getSvje();
        assertEquals(expResult, result);

    }

    /**
     * Test of setSvjetlost method, of class Uredjaj_A.
     */
    @Test
    public void testSetSvjetlost() {
        System.out.println("setSvjetlost");
        int svjetlost = 0;
        Uredjaj_A instance = new Uredjaj_A();
        instance.setSvjetlost(svjetlost);
        int result = instance.getSvje();
        assertEquals(svjetlost, result);
    }

    /**
     * Test of getVjetar method, of class Uredjaj_A.
     */
    @Test
    public void testGetVjetar() {
        System.out.println("getVjetar");
        Uredjaj_A instance = new Uredjaj_A();
        int expResult = 0;
        instance.setVjetar(expResult);
        int result = instance.getVjetar();
        assertEquals(expResult, result);
    }

    /**
     * Test of setVjetar method, of class Uredjaj_A.
     */
    @Test
    public void testSetVjetar() {
        System.out.println("setVjetar");
        int vjetar = 0;
        Uredjaj_A instance = new Uredjaj_A();
        instance.setVjetar(vjetar);
        int result = instance.getVjetar();
        assertEquals(vjetar, result);
    }

    /**
     * Test of getBuka method, of class Uredjaj_A.
     */
    @Test
    public void testGetBuka() {
        System.out.println("getBuka");
        Uredjaj_A instance = new Uredjaj_A();
        int expResult = 0;
        instance.setBuka(expResult);
        int result = instance.getBuka();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBuka method, of class Uredjaj_A.
     */
    @Test
    public void testSetBuka() {
        System.out.println("setBuka");
        int buka = 0;
        Uredjaj_A instance = new Uredjaj_A();
        instance.setBuka(buka);
        assertEquals(buka, instance.getBuka());
    }

}
