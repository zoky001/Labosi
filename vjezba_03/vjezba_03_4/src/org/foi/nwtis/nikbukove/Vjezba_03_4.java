package org.foi.nwtis.nikbukove;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nikbukove.konfiguracije.Konfiguracija;
import org.foi.nwtis.nikbukove.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.nikbukove.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.nikbukove.konfiguracije.NemaKonfiguracije;

public class Vjezba_03_4 {

    public static void main(String[] args) {
       if(args.length > 3 || args.length == 0){
            System.out.println("Broj argumenata ne valja!");
            return;
        }
       
       if(args.length == 1){
            try {
                Konfiguracija konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
                Properties p = konf.dajSvePostavke();
                for(Object k : p.keySet()){
                    String v = konf.dajPostavku((String) k);
                    System.out.println(k + " = " + v);
                }
            } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
                Logger.getLogger(Vjezba_03_4.class.getName()).log(Level.SEVERE, null, ex);
            } 
       }
       
       else if(args.length == 2){
           try {
                Konfiguracija konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
                String p = konf.dajPostavku(args[1]);
               System.out.println(args[1] + " = " + p);
                
            } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
                Logger.getLogger(Vjezba_03_4.class.getName()).log(Level.SEVERE, null, ex);
            } 
       }
       
       if(args.length == 3){
           
       }
    }
    
}
