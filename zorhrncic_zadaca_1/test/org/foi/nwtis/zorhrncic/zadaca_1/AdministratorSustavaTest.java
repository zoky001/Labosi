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
public class AdministratorSustavaTest {

    public AdministratorSustavaTest() {
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
     * Test of testInputStringAndExtractChasterAdnSize method, of class
     * AdministratorSustava.
     */
    @Test
    public void testTestInputStringAndExtractChasterAdnSize() {
        System.out.println("testInputStringAndExtractChasterAdnSize");
        String string = "OK; ZN-KODOVI ISO-8859-1; DUZINA 0\r\nnestopise";
        String sintaksa ="^OK; ZN-KODOVI ([^\\s]+); DUZINA ([0-9]+)\r\n([\\s\\S]+)";
        AdministratorSustava instance = new AdministratorSustava();
        boolean result = instance.testInputStringAndExtractChasterAdnSize(string, sintaksa);
        assertTrue(result);
        assertEquals("ISO-8859-1", instance.getCharset());

    }

}
