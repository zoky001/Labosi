/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import org.foi.nwtis.zorhrncic.web.kontrole.Izbornik;
import org.foi.nwtis.zorhrncic.web.kontrole.Poruka;

/**
 *
 * @author grupa_1
 */
@Named(value = "pregledPoruka")
@RequestScoped
public class PregledPoruka {

    private String posluzitelj;
    private String korisnickoIme;
    private String lozinka, odabranaMapa;
    private List<Izbornik> nizMapa;
    private List<Poruka> nizPoruka;

    private int ukupanBrojPoruka, brojPorukaZaPrikaz, odPrikazPoruka, doPrkazPoruka;
    private Session session;
    private Store store;
    private Folder folder;

    /**
     * Creates a new instance of PregledPoruka
     */
    public PregledPoruka() {
        //TODO preuzmi iz konf
        posluzitelj = "127.0.0.1";
        korisnickoIme = "servis@nwtis.nastava.foi.hr";
        lozinka = "123456";
        preuzmiMape();
        preuzmiPoruke();

    }

    private void preuzmiMape() {
        Session session;
        Store store;
        Folder[] folders;
        nizMapa = new ArrayList<>();
        try {
            // Connect to the store
            // Start the session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluzitelj);
            session = Session.getInstance(properties, null);
            // Connect to the store
            store = session.getStore("imap");
            store.connect(posluzitelj, korisnickoIme, lozinka);
            // Open the INBOX folder
           
            
            folders = store.getUserNamespaces(korisnickoIme);
            for (Folder folder : folders) {
                nizMapa.add(new Izbornik(folder.getName(), folder.getName()));
                System.out.println("dodana mapa: " + folder.getName());
            }
            
            
            store.close();
            //TODO provjeri da ne postoji trazena mapa u sanducicu prema nazivu iz postavki
            nizMapa.add(new Izbornik("NWTiS dkermek poruke", "NWTiS dkermek poruke"));
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void preuzmiPoruke() {
        try {
            nizPoruka = new ArrayList<>();
            //TODO pruzmi poruke s email poslužiteja
            
            int i = 0;
            nizPoruka.add(new Poruka(Integer.toString(i), new Date(), new Date(), "zorhrncic@foi.hr", "Poruka " + i++, "", Poruka.VrstaPoruka.NWTiS_poruka));
            nizPoruka.add(new Poruka(Integer.toString(i), new Date(), new Date(), "zorhrncic@foi.hr", "Poruka " + i++, "", Poruka.VrstaPoruka.NWTiS_poruka));
            nizPoruka.add(new Poruka(Integer.toString(i), new Date(), new Date(), "zorhrncic@foi.hr", "Poruka " + i++, "", Poruka.VrstaPoruka.NWTiS_poruka));
            nizPoruka.add(new Poruka(Integer.toString(i), new Date(), new Date(), "zorhrncic@foi.hr", "Poruka " + i++, "", Poruka.VrstaPoruka.NWTiS_poruka));
             

            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluzitelj);
            session = Session.getInstance(properties, null);

            // Connect to the store
            store = session.getStore("imap");
            store.connect(posluzitelj, korisnickoIme, lozinka);

            // Open the INBOX folder
         /*   folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_ONLY);

            //TODO, ne dohvatiti sve porukeodjednom, nego po grupama
            //dohvatitiprivitak NE NWTIS PORUKA
           
            for (Message m : folder.getMessages()) {
                nizPoruka.add(new Poruka(Integer.toString(m.getMessageNumber()),
                        m.getSentDate(),
                        m.getReceivedDate(),
                        m.getFrom()[0].toString(),
                        m.getSubject(),
                        "privitak",
                        Poruka.VrstaPoruka.NWTiS_poruka));
            }
*/
            
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Izbornik> getNizMapa() {
        return nizMapa;
    }

    public String getOdabranaMapa() {
        return odabranaMapa;
    }

    public void setOdabranaMapa(String odabranaMapa) {
        this.odabranaMapa = odabranaMapa;
    }

    public List<Poruka> getNizPoruka() {
        return nizPoruka;
    }
    

    //navigacija
    public String promjenaMape() {
        return "PromjenaMape";
    }

    public String prethodnePoruke() {
        return "PrethodnePoruke";
    }

    public String sljedecePoruke() {
        return "SljedecePoruke";
    }

    public String promjenaJezika() {

        return "promjenaJezika";
    }

    public String saljiPoruku() {
        return "saljiPoruku";
    }

    public String pregledDnevnika() {
        return "pregledDnevnika";
    }
}
