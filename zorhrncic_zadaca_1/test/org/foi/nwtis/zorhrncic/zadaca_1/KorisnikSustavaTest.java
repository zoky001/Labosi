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
public class KorisnikSustavaTest {

    public KorisnikSustavaTest() {
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
     * Test of testInputArgs method, of class KorisnikSustava.
     */
    @Test
    public void testTestInputArgs() {
        System.out.println("testInputArgs");
        String sintaksa = "^-k ([^[a-zA-Z0-9_-]]{3,10}) -l ([^[a-zA-Z0-9[#!]_-]]{3,10}) -s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) (--pauza|--kreni|--zaustavi|--stanje|--evidencija ([^\\s]+\\.(?i)(txt|xml|json|bin))|--iot ([^\\s]+\\.(?i)(txt|xml|json|bin)))";
        String[] args = {"-k", "zorhrncic", "-l", "123456", "-s", "127.0.0.1", "-p", "8000", "--zaustavi"};
        boolean result = KorisnikSustava.testInputArgs(sintaksa, args);
        assertTrue(result);
        String[] args1 = {"-k", "zorhrncic", "-l", "123456", "-s", "127.0.0.1", "-p", "8000", "--zaustavi", "--pauza"};//previse parametare
        result = KorisnikSustava.testInputArgs(sintaksa, args1);
        assertFalse(result);
    }

    /**
     * Test of testInputString method, of class KorisnikSustava.
     */
    @Test
    public void testTestInputString() {
        System.out.println("testInputString");
        String sintaksa = "^-k ([^[a-zA-Z0-9_-]]{3,10}) -l ([^[a-zA-Z0-9[#!]_-]]{3,10}) -s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) (--pauza|--kreni|--zaustavi|--stanje|--evidencija ([^\\s]+\\.(?i)(txt|xml|json|bin))|--iot ([^\\s]+\\.(?i)(txt|xml|json|bin)))";
        String string = "-k zorhrncic -l 123456 -s 127.0.0.1 -p 8000 --zaustavi";
        boolean result = KorisnikSustava.testInputString(sintaksa, string);
        assertTrue(result);
        string = "-k zorhrncic -l 123456 -s 127.0.0.1 -p 8000 --zaustav"; //fali "i"
        result = KorisnikSustava.testInputString(sintaksa, string);
        assertFalse(result);
    }

}
