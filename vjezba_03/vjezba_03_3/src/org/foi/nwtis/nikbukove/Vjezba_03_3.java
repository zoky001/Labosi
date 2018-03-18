
package org.foi.nwtis.nikbukove;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

public class Vjezba_03_3 {

    public static void main(String[] args) {
        
        if(args.length != 1){
            System.out.println("Broj argumenata ne valja!");
            return;
        }
        try {
            KonfiguracijaApstraktna.kreirajKonfiguraciju(args[0]);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(Vjezba_03_3.class.getName()).log(Level.SEVERE, null, ex);
        } 
       
    }
    
}
