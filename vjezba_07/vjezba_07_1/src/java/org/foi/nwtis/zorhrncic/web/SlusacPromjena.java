/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author grupa_1
 */
public class SlusacPromjena implements PropertyChangeListener {

    static int brojPromjena;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        brojPromjena++;
        String varijabla = evt.getPropertyName();
        Object staravrijednost = evt.getOldValue();
        Object novaVrijednost = evt.getNewValue();
        System.out.println("Varijabla: " + varijabla + 
                " stara vr. = " + staravrijednost +
                " nova vr. = " + novaVrijednost +
                "broj promjena = " + brojPromjena);
    }

}
