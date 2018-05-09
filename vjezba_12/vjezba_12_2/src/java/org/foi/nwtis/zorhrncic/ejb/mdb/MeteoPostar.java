/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.ejb.mdb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author grupa_1
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_vjezba_12")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MeteoPostar implements MessageListener {
    
    public MeteoPostar() {
    }
    
    @Override
    public void onMessage(Message message) {
        
        try {
            System.out.println("Poruka: " + message.getBody(String.class));
        } catch (JMSException ex) {
            Logger.getLogger(MeteoPostar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
