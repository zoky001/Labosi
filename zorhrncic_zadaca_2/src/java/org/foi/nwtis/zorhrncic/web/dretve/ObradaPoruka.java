/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.dretve;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import static jdk.nashorn.internal.codegen.OptimisticTypesPersistence.store;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author grupa_1
 */
public class ObradaPoruka extends Thread {

    private boolean krajRada = false;
    private int spavanje;
    private Folder folder;
    private Store store;
    private Session session;
    private String adresaServera;
    private String korisnickoIme;
    private String lozinka;
    private Message[] messages = null;
    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private long razlika = 0;
    private long pocetak = 0;
    private long kraj = 0;
    private double koef = 0.01666666666;
    private int portServera;
    private int brojPorukaZaUcitavanje;
    private String nazivAttachmenta;
    private String nazivMape;
    private String nazivDatotekePodatciRadu;
    private long krajObrade;
    private int brojPoruka = 0, brojdodanihIOT = 0, brojAzuriranihIOT = 0, brojNeispravnihPoruka = 0;
    private final String protokol = "imap";

    public ObradaPoruka() {
        preuzmiKonfiuraciju();
    }

    private void preuzmiKonfiuraciju() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data

        spavanje = Integer.parseInt(konfiguracija.dajPostavku("mail.timeSecThreadCycle"));
        adresaServera = konfiguracija.dajPostavku("mail.server");
        portServera = Integer.parseInt(konfiguracija.dajPostavku("mail.imap.port"));
        korisnickoIme = konfiguracija.dajPostavku("mail.usernameThread");
        lozinka = konfiguracija.dajPostavku("mail.passwordThread");
        brojPorukaZaUcitavanje = Integer.parseInt(konfiguracija.dajPostavku("mail.numMessagesToRead"));
        nazivAttachmenta = konfiguracija.dajPostavku("mail.attachmentFilename");
        nazivMape = konfiguracija.dajPostavku("mail.folderNWTiS");
        nazivDatotekePodatciRadu = konfiguracija.dajPostavku("mail.threadCycleLogFilename");

    }

    @Override
    public void interrupt() {
        krajRada = true;
        super.interrupt();

    }

    @Override
    public void run() {
        int broj = 0;
        while (!krajRada) {
            pocetak = System.currentTimeMillis();
            if (kraj != 0) {
                /*0.01666666666;*/

                System.out.println("Razlika od prosle serijalizacije: " + (pocetak - kraj) / 1000 + " sec");
            }
            try {

                connectAndReadINBOX();

                readMessages();

                folder.close(false);
                store.close();

                //TODO
                spremiPodatkeRadu(pocetak, System.currentTimeMillis());

                System.out.println("Gotova obrada: " + broj++);
                kraj = System.currentTimeMillis();
                razlika = kraj - pocetak;
                Thread.sleep((spavanje * 1000 - razlika) + (long) (koef * (spavanje * 1000 - razlika)));

            } catch (Exception e) {
                System.out.println("org.foi.nwtis.zorhrncic.web.dretve.ObradaPoruka.run(): " + e.getMessage());
            }
        }

    }

    @Override
    public synchronized void start() {
//TODO preuzmji vrijeme spavanja iz konf
//preuzmi adresu posluzitelja, kor ime , looziinku, vrijeme,

        super.start();

    }

    private void spremiPodatkeRadu(long pocetak, long currentTimeMillis) {
        /*
        Dretva na kraju svakog ciklusa dodaje podatke o radu u datoteku (naziv određen konfiguracijom, mail.threadCycleLogFilename, i sadržaj u UTF-8) u sljedećem obliku:

Obrada poruka broj:
Obrada započela u: vrijeme_1 (dd.MM.yyyy hh.mm.ss.zzz)
Obrada završila u: vrijeme_2 (dd.MM.yyyy hh.mm.ss.zzz) 
Trajanje obrade u ms: n
Broj poruka: n - odnosi se na jedan ciklus
Broj dodanih IOT: n 
Broj ažuriranih IOT: n 
Broj neispravnih poruka: n
<prazan redak>*/

    }

    private void connectAndReadINBOX() {
        try {
            // Start the session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", adresaServera);
            session = Session.getInstance(properties, null);
            // Connect to the store
            store = session.getStore("imap");
            store.connect(adresaServera, korisnickoIme, lozinka);
            // Open the INBOX folder
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readMessages() {

        try {
            //TODO, ne dohvatiti sve porukeodjednom, nego po grupama
            messages = folder.getMessages();

            // Print each message
            for (int i = 0; i < messages.length; ++i) {
                System.out.println("\n--------------------------------------------------------------------------");
                System.out.println("poruka: " + messages[i].getFrom()[0]);
                //TODO pretraži tzv. NWTIS poruke i s njima obavi potrebne radnje
                checkAndProcessMessage(messages[i]);
            }
        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void checkAndProcessMessage(Message message) {

        checkIfExistAttachment(message);

    }

    private void checkIfExistAttachment(Message message) {
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
        }
        if (isNwtisMessage && numAttach == 1 && attachment != null) {
            processingMessage(message, attachment);
        }

    }

    private void processingMessage(Message message, MimeBodyPart attachment) {
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

            } else {
                //System.out.println("Primljeni je OSTALO");
                Object o = message.getContent();

                // If the text is plain, just print it
                if (o instanceof String) {
                    // System.out.println(o);
                } else {
                    // Print the content type

                    //System.out.println(attachment.getContentType());
                    // If it is a multipart, list the parts
                    if (o instanceof MimeMultipart) {
                        // listParts((MimeMultipart) o, writer);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("GREŠKA: " + e.getMessage());
        }
    }

}
