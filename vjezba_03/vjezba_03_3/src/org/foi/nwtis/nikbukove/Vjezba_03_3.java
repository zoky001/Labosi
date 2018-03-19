package org.foi.nwtis.nikbukove;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

public class Vjezba_03_3 {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Broj argumenata ne valja!");
            return;
        }
        try {
            KonfiguracijaApstraktna.kreirajKonfiguraciju(args[0]);
            Konfiguracija konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

            /*
         port=8000
maks.broj.zahtjeva.cekanje=50
maks.broj.radnih.dretvi=50
datoteka.evidencije.rada=NWTis_zorhrncic_evidencija_rada.bin
interval.za.serijalizaciju=60
admin.0.zorhrncic=123456
admin.1.zorgrdjan=123456
admin.2.ivicelig=123456
admin.3.matbodulusic=123456
admin.4.nikbukovec=123456
admin.5.dkermek=123456
             */
            konf.spremiPostavku("port", "8000");
            konf.spremiPostavku("maks.broj.zahtjeva.cekanje", "50");
            konf.spremiPostavku("maks.broj.radnih.dretvi", "50");
            konf.spremiPostavku("datoteka.evidencije.rada", "NWTis_zorhrncic_evidencija_rada.bin");
            konf.spremiPostavku("interval.za.serijalizaciju", "60");
            konf.spremiPostavku("admin.1.zorgrdjan", "123456");
            konf.spremiPostavku("admin.0.zorhrncic", "123456");
            konf.spremiPostavku("admin.2.ivicelig", "123456");

            konf.spremiPostavku("admin.3.matbodulusic", "123456");
            konf.spremiPostavku("admin.4.nikbukovec", "123456");
            konf.spremiPostavku("admin.5.dkermek", "123456");
            konf.spremiKonfiguraciju();
            System.out.println("Property NAme: " + konf.dajPostavku("maks.broj.radnih.dretvi"));
            System.out.println("Property NAme: " + konf.dajPostavku("port"));

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(Vjezba_03_3.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
