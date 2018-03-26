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
public class SerijalizatorEvidencijeTest {

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
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setKrajRada method, of class SerijalizatorEvidencije.
     */
    @Test
    public void testSetKrajRada() {
        System.out.println("setKrajRada");
        boolean b = false;
        SerijalizatorEvidencije instance = new SerijalizatorEvidencije();
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


}
