/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.konfiguracije;

/**
 *
 * @author grupa_2
 */
public class NemaKonfiguracije extends Exception {

    /**
     * Creates a new instance of <code>NemaKonfiguracije</code> without detail
     * message.
     */
    public NemaKonfiguracije() {
    }

    /**
     * Constructs an instance of <code>NemaKonfiguracije</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NemaKonfiguracije(String msg) {
        super(msg);
    }
}
