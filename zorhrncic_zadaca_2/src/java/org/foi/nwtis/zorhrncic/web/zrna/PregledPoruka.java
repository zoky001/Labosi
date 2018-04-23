/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.servlet.ServletContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;
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

    private int ukupanBrojPoruka, messageToShow, pozicijaOd = 1, pozicijaDo = 0;
    private Session session;
    private Store store;
    private Folder folder;
    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private String nazivMape;
    private int portServera;
    private String nazivAttachmenta;
    private String privitak;
    private boolean previous = false;
    private boolean next = false;

    /**
     * Creates a new instance of PregledPoruka
     */
    public PregledPoruka() {
        odabranaMapa = "INBOX";
        //TODO preuzmi iz konf
        preuzmiKonfiuraciju();
        preuzmiMape();
        preuzmiPoruke();

    }

    private void preuzmiKonfiuraciju() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data

        posluzitelj = konfiguracija.dajPostavku("mail.server");
        portServera = Integer.parseInt(konfiguracija.dajPostavku("mail.imap.port"));
        korisnickoIme = konfiguracija.dajPostavku("mail.usernameThread");
        lozinka = konfiguracija.dajPostavku("mail.passwordThread");
        nazivMape = konfiguracija.dajPostavku("mail.folderNWTiS");
        messageToShow = Integer.parseInt(konfiguracija.dajPostavku("mail.numMessagesToShow"));
        nazivAttachmenta = konfiguracija.dajPostavku("mail.attachmentFilename");

    }

    private void preuzmiMape() {
        Session session;
        Store store;
        Folder[] folders;
        nizMapa = new ArrayList<>();
        ukupanBrojPoruka = 0;
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
            Folder[] f = store.getDefaultFolder().list();
            for (Folder fd : f) {
                //  System.out.println(">> " + fd.getName());
                nizMapa.add(new Izbornik(fd.getName(), fd.getName()));
            }
            store.close();
            //TODO provjeri da ne postoji trazena mapa u sanducicu prema nazivu iz postavki
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
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluzitelj);
            session = Session.getInstance(properties, null);

            // Connect to the store
            store = session.getStore("imap");
            store.connect(posluzitelj, korisnickoIme, lozinka);
            getMessgeFromFolder(odabranaMapa);

        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void getMessgeFromFolder(String odabranaMapa) {
        try {
            // Open the INBOX folder
            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_ONLY);
            Poruka.VrstaPoruka vrsta;
            if (odabranaMapa.equals(nazivMape)) {
                vrsta = Poruka.VrstaPoruka.NWTiS_poruka;
            } else {
                vrsta = Poruka.VrstaPoruka.neNWTiS_poruka;
            }
            //TODO, ne dohvatiti sve porukeodjednom, nego po grupama
            //dohvatitiprivitak NE NWTIS PORUKA

            ukupanBrojPoruka = folder.getMessageCount();
            if (ukupanBrojPoruka == 0) {
                return;
            }
            int end = 0;
            if (ukupanBrojPoruka < pozicijaOd + messageToShow) {
                end = ukupanBrojPoruka;
            } else {
                end = pozicijaOd + messageToShow - 1;
            }
            for (Message m : folder.getMessages(pozicijaOd, end)) {
                privitak = "";
                MimeBodyPart part = checkIfExistAttachment(m); // ovo je nwtis message
                if (part != null) {
                    privitak = processingMessage(m, part);
                }

                nizPoruka.add(new Poruka(Integer.toString(m.getMessageNumber()),
                        m.getSentDate(),
                        m.getReceivedDate(),
                        m.getFrom()[0].toString(),
                        m.getSubject(),
                        privitak,
                       vrsta)
                );

                pozicijaDo++;
            }
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private MimeBodyPart checkIfExistAttachment(Message message) {
// suppose 'message' is an object of type Message
        int numAttach = 0;
        boolean isNwtisMessage = false;
        MimeBodyPart attachment = null;
        try {

            String contentType = message.getContentType();

            if (contentType.contains("multipart")) {
                // this message may contain attachment
                Multipart multiPart = (Multipart) message.getContent();

                for (int i = 0; i < multiPart.getCount(); i++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        numAttach++;
                        // this part is attachment
                        // code to save attachment...
                        if (part.getFileName().equalsIgnoreCase(nazivAttachmenta)) { // OVO JE MOZDA NEPOTREBNA PROVJERA IMENA.. CONTENT
                            //System.out.println("HAVE ATTACH: " + part.getFileName());
                            isNwtisMessage = true;
                            attachment = part;

                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
        if (isNwtisMessage && numAttach == 1 && attachment != null) {
            return attachment;
            // processingMessage(message, attachment);
        } else {
            return null;
        }

    }

    private String processingMessage(Message message, MimeBodyPart attachment) {
        /*obrađuju se tako da se ispituje sadržaj 
        datoteke privitka. Sadržaj datoteke treba biti 
        u "text/json" ili "application/json" formatu i može
        biti sa sljedećem sintaksom:*/

        try {
            // If the text is in HTML, just print it
            if ("TEXT/JSON; charset=utf-8; name=NWTiS_privitak.json".equalsIgnoreCase(attachment.getContentType()) || "APPLICATION/JSON; charset=utf-8; name=NWTiS_privitak.json".equalsIgnoreCase(attachment.getContentType())) {
                System.out.println("Primljeni je TEXT_JSOON: ");

                BufferedReader reader
                        = new BufferedReader(
                                new InputStreamReader(
                                        attachment.getInputStream()));

                String s;
//OVJE JE PRIMLJENI JSON MESSG
                String JSONfILE = "";
                while ((s = reader.readLine()) != null) {

                    JSONfILE = JSONfILE + s;
                }

                System.out.print(JSONfILE);
                return JSONfILE;
                /*
                if (valdiateJSONAttachment(JSONfILE)) {
                    processIOTdeviceData(JSONfILE);
                }
                 */
            } else {
                return null;
            }

        } catch (Exception e) {
            System.out.println("GREŠKA: " + e.getMessage());
            return null;
        }
    }

    public boolean isPrevious() {
        if (pozicijaOd == 0) {
            return false;

        } else {
            return true;
        }

    }

    public void setPrevious(boolean previous) {
        this.previous = previous;
    }

    public boolean isNext() {
         if (pozicijaOd == ukupanBrojPoruka) {
            return false;

        } else {
            return true;
        }
    }

    //Getter & setter
    public void setNext(boolean next) {
        this.next = next;
    }

    public int getUkupanBrojPoruka() {
        return ukupanBrojPoruka;
    }

    public void setUkupanBrojPoruka(int ukupanBrojPoruka) {
        this.ukupanBrojPoruka = ukupanBrojPoruka;
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

    public int getPozicijaOd() {
        return pozicijaOd;
    }

    public void setPozicijaOd(int pozicijaOd) {
        this.pozicijaOd = pozicijaOd;
    }

    public int getPozicijaDo() {
        return pozicijaDo;
    }

    public void setPozicijaDo(int pozicijaDo) {
        this.pozicijaDo = pozicijaDo;
    }

    //navigacija
    public String promjenaMape() {
        pozicijaDo = 0;
        pozicijaOd = 1;
        preuzmiPoruke();
        return "PromjenaMape";
    }

    public String prethodnePoruke() {
        pozicijaOd = pozicijaOd - messageToShow;

        if (pozicijaOd < 1) {
            pozicijaOd = 1;
        }
        pozicijaDo = pozicijaOd - 1;
        preuzmiPoruke();
        //
        return "PrethodnePoruke";
    }

    public String sljedecePoruke() {
        if (pozicijaDo < ukupanBrojPoruka) {
            pozicijaOd = pozicijaDo + 1;
        }

        pozicijaDo = pozicijaOd - 1;
        preuzmiPoruke();
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
