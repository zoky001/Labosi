/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.nikbukove.konfiguracije;

import java.io.File;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author grupa_2
 */
public class KonfiguracijaTxtTest {
    
    File datKonf;
    
    public KonfiguracijaTxtTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        String datoteka = "nikbukove_test_conf.txt";
        datKonf = new File(datoteka);
    }
    
    @After
    public void tearDown() {
        if(!datKonf.exists()){
            datKonf.delete();
        }
    }

    /**
     * Test of ucitajKonfiguraciju method, of class KonfiguracijaTxt.
     */
    @Ignore
    @Test
    public void testUcitajKonfiguraciju() throws Exception {
        System.out.println("ucitajKonfiguraciju");
        KonfiguracijaTxt instance = null;
        instance.ucitajKonfiguraciju();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ucitajKonfiguraciju method, of class KonfiguracijaTxt.
     */
    @Test(expected = NemaKonfiguracije.class)
    public void testUcitajKonfiguraciju_String() throws Exception {
        System.out.println("ucitajKonfiguraciju");
        String datoteka = "nepostojeca_datoteka.txt";
        KonfiguracijaTxt instance = new KonfiguracijaTxt(datoteka);
        instance.spremiPostavku("1", "Pero");
        instance.ucitajKonfiguraciju(datoteka);
        
    }

    /**
     * Test of spremiKonfiguraciju method, of class KonfiguracijaTxt.
     */
    
    @Test
    public void testSpremiKonfiguraciju() throws Exception {
        System.out.println("spremiKonfiguraciju");
        KonfiguracijaTxt instance = new KonfiguracijaTxt(datKonf.getName());
        instance.spremiKonfiguraciju();  
        assertTrue(datKonf.exists());
    }

    /**
     * Test of spremiKonfiguraciju method, of class KonfiguracijaTxt.
     */
   
    @Test
    public void testSpremiKonfiguraciju_String() throws Exception {
        System.out.println("spremiKonfiguraciju");
        File novi = new File("testConf.txt");
        KonfiguracijaTxt instance = new KonfiguracijaTxt(novi.getName());
        instance.spremiKonfiguraciju(novi.getName());  
        assertTrue(novi.exists());
        novi.delete();
    }
    
}
