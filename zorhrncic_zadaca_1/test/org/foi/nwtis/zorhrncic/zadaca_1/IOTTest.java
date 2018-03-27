/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.nio.charset.Charset;
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
public class IOTTest {

    public IOTTest() {
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
     * Test of isUpis method, of class IOT.
     */
    @Test
    public void testIsUpis() {
        System.out.println("isUpis");
        IOT instance = new IOT();
        assertFalse(instance.isUpis());
        instance.setUpis(true);
        assertTrue(instance.isUpis());
    }

    /**
     * Test of setUpis method, of class IOT.
     */
    @Test
    public void testSetUpis() {
        System.out.println("setUpis");
        IOT instance = new IOT();
        assertFalse(instance.isUpis());
        instance.setUpis(true);
        assertTrue(instance.isUpis());
    }

    /**
     * Test of addOrUpdateDevice method, of class IOT.
     */
    @Test
    public void testAddDevice() throws Exception {
        System.out.println("addOrUpdateDevice");
        String iotUredjaj = "{\"ID\":3,\"temperatura\":322.244,\"vlaga\":0,\"svjetlost\":26,\"vjetar\":12,\"buka\":22}";
        IOT instance = new IOT();
        assertEquals(instance.getPopisUredjajaNew().size(), 0);
        String result = instance.addOrUpdateDevice(iotUredjaj);
        assertEquals(instance.getPopisUredjajaNew().size(), 1);
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("id"), "3");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("temperatura"), "322.244");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("vlaga"), "0");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("svjetlost"), "26");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("vjetar"), "12");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("buka"), "22");
        String iotUredjaj1 = "{\"ID\":3,\"temperatura\":322.244,\"vlaga\":0,\"svjetlost\":26,\"vjetar\":12,\"buka\":23,\"vrijeme\":\"23:22\"}";
        System.out.println(instance.addOrUpdateDevice(iotUredjaj1));
         assertEquals(instance.getPopisUredjajaNew().size(), 1);
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("id"), "3");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("temperatura"), "322.244");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("vlaga"), "0");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("svjetlost"), "26");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("vjetar"), "12");
        assertEquals(instance.getPopisUredjajaNew().get(0).getProperty("buka"), "23");
        assertEquals("23:22",instance.getPopisUredjajaNew().get(0).getProperty("vrijeme").toString());
    }

}
