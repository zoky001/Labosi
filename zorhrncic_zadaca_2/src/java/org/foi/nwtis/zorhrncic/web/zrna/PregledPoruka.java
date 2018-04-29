/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
 * Klasa sadrzi sve metode koje su potrebne za dohvacanje poruka sa mail servera
 * i prikazivanje sadrazaja istih.
 *
 * @author Zoran Hrncic
 */
@Named(value = "pregledPoruka")
@RequestScoped
public class PregledPoruka {

    private String posluzitelj, korisnickoIme, lozinka, odabranaMapa;
    private List<Izbornik> nizMapa;
    private List<Poruka> nizPoruka;
    private int ukupanBrojPoruka, messageToShow, pozicijaOd = -1, pozicijaDo = 0;
    private Session session;
    private Store store;
    private Folder folder;
    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private String nazivMape;
    private int portServera;
    private String nazivAttachmenta, privitak;
    private static boolean previousBoolean = false, nextBoolean = false;
    private static String AttachFormat1 = "";
    private static String AttachFormat2 = "";

    /**
     * Postavlja INBOX kao pocetnu odabranu mapa iz koje se ispisuju poruke.
     * Poziva metodu za preuzimanje podataka iz konfiguracije. Poziva metodu za
     * dohvacanje mapa sa prukama. Poziva metodu za preuzimanje poruka iz
     * odabrane mape.
     *
     */
    public PregledPoruka() {
        odabranaMapa = "INBOX";
        preuzmiKonfiuraciju();
        preuzmiMape();
        preuzmiPoruke();
    }

    /**
     * Preuzima konfiguraciju iz kontexta i pohranjije potrebne podatke u
     * globalne vrijable.
     *
     */
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
        AttachFormat1 = "TEXT/JSON; charset=utf-8; name=" + nazivAttachmenta;
        AttachFormat2 = "APPLICATION/JSON; charset=utf-8; name=" + nazivAttachmenta;
    }

    /**
     * Dohvaca sve mape poruka i trazene dodaje u niz "nizMapa"
     */
    private void preuzmiMape() {
        Session session;
        Store store;
        Folder[] folders;
        nizMapa = new ArrayList<>();
        ukupanBrojPoruka = 0;
        try {
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluzitelj);
            session = Session.getInstance(properties, null);
            store = session.getStore("imap");
            store.connect(posluzitelj, korisnickoIme, lozinka);
            Folder[] f = store.getDefaultFolder().list();
            for (Folder fd : f) {
                if (fd.getName().equalsIgnoreCase(nazivMape) || fd.getName().equalsIgnoreCase("INBOX")) {
                    nizMapa.add(new Izbornik(fd.getName(), fd.getName()));
                }
            }
            store.close();
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Pokrece preuzimanje svih poruke iz odabrane mape i smjesta ih u niz
     * "nizPoruka"
     */
    private void preuzmiPoruke() {
        try {
            nizPoruka = new ArrayList<>();
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluzitelj);
            session = Session.getInstance(properties, null);
            store = session.getStore("imap");
            store.connect(posluzitelj, korisnickoIme, lozinka);
            getMessgeFromFolder(odabranaMapa);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Dohvaca sve poruke iz mape ciji naziv je prosljedjen u metodu
     *
     * @param odabranaMapa naziv mape iz koje se dohvacaju poruke
     */
    private void getMessgeFromFolder(String odabranaMapa) {
        nextBoolean = true;
        previousBoolean = true;
        try {
            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_ONLY);
            Poruka.VrstaPoruka vrsta;
            if (odabranaMapa.equals(nazivMape)) {
                vrsta = Poruka.VrstaPoruka.NWTiS_poruka;
            } else {
                vrsta = Poruka.VrstaPoruka.neNWTiS_poruka;
            }
            ukupanBrojPoruka = folder.getMessageCount();
            if (ukupanBrojPoruka == 0) {
                return;
            }
            int end = validateFrom_To_valueForFetchingMessages();
            fetchRequiredMessages(folder, end, vrsta);
            Collections.reverse(nizPoruka);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Dohvaca poruke sa mail servera prema prosljedjenim parametrima.
     *
     * @param folder1 mapa iz koje se dohvacaju poruke
     * @param end kraj dohvacanja poruka, pozicija
     * @param vrsta vrsta poruka
     * @throws MessagingException
     */
    private void fetchRequiredMessages(Folder folder1, int end, Poruka.VrstaPoruka vrsta) throws MessagingException {
        for (Message m : folder1.getMessages(pozicijaOd, end)) {
            privitak = "";
            MimeBodyPart part = checkIfExistAttachment(m); // ovo je nwtis message
            if (part != null) {
                privitak = processingMessage(part);
            }
            nizPoruka.add(new Poruka(Integer.toString(m.getMessageNumber()),
                    m.getSentDate(),
                    m.getReceivedDate(),
                    m.getFrom()[0].toString(),
                    m.getSubject(),
                    privitak,
                    vrsta)
            );
        }
    }

    /**
     * Provjera i ustimavanje pocetne i zavrsne pozicije poruka koje se
     * dohvacaju.
     *
     * @return zavrsna pozicija do koje se dohvacaju poruke.
     */
    private int validateFrom_To_valueForFetchingMessages() {
        if (pozicijaOd == -1) {
            pozicijaDo = pozicijaOd + messageToShow - 1;//moakni gore
            pozicijaDo = ukupanBrojPoruka;
            pozicijaOd = pozicijaDo - messageToShow + 1;
        }
        if (pozicijaDo >= ukupanBrojPoruka) {
            pozicijaDo = ukupanBrojPoruka;
            previousBoolean = false;
        }
        if (pozicijaOd > ukupanBrojPoruka) {
            pozicijaOd = ukupanBrojPoruka - messageToShow + 1;
        }
        if (pozicijaOd <= 1) {
            pozicijaOd = 1;
            nextBoolean = false;
        }
        int end = pozicijaDo;
        return end;
    }

    /**
     * Provjera sadrzi li poruka attachment. Ako sadrzi, onda ga vraca, ako ne
     * onda vraca null.
     *
     * @param message poruka koja se provjerava
     * @return attachment, ili null ako ne postoji
     */
    private MimeBodyPart checkIfExistAttachment(Message message) {
        int numAttach = 0;
        boolean isNwtisMessage = false;
        MimeBodyPart attachment = null;
        try {
            String contentType = message.getContentType();
            if (contentType.contains("multipart")) {
                Multipart multiPart = (Multipart) message.getContent();
                for (int i = 0; i < multiPart.getCount(); i++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        numAttach++;
                        if (part.getFileName().equalsIgnoreCase(nazivAttachmenta)) {
                            isNwtisMessage = true;
                            attachment = part;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        if (isNwtisMessage && numAttach == 1 && attachment != null) {
            return attachment;
        } else {
            return null;
        }
    }

    /**
     * Dohvaca podatke iz attachmenta poruke.
     *
     * @param attachment attachment
     * @return sadrzaj definiranog attachment ili null ako ne postoji
     */
    private String processingMessage(MimeBodyPart attachment) {
        try {
            if (AttachFormat1.equalsIgnoreCase(attachment.getContentType())
                    || AttachFormat2.equalsIgnoreCase(attachment.getContentType())) {
                BufferedReader reader
                        = new BufferedReader(
                                new InputStreamReader(
                                        attachment.getInputStream()));
                String s;
                String JSONfILE = "";
                while ((s = reader.readLine()) != null) {
                    JSONfILE = JSONfILE + s;
                }
                return JSONfILE;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Provjera postoje li prethodni zapisi poruka za prikazati. hidden -klasa
     * frameworka bootstrap koja sakriva element
     *
     * @return ako nepostoje vraca "hidden", ako ne postoji vraca ""
     */
    public String isPrevious() {
        if (previousBoolean) {
            return "";
        } else {
            return "hidden";
        }
    }

    /**
     * Provjera postoje li sljedeci zapisi poruka za prikazati. hidden -klasa
     * frameworka bootstrap koja sakriva element
     *
     * @return ako ne postoje vraca "hidden", ako ne postoji vraca ""
     */
    public String isNext() {
        if (nextBoolean) {
            return "";
        } else {
            return "hidden";
        }
    }

//getter & setter
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
    /**
     * definira pocetne vrijednosti pozicija za prikazivanje poruka iz mapa.
     *
     * @return
     */
    public String promjenaMape() {
        pozicijaDo = 0;
        pozicijaOd = -1;
        preuzmiPoruke();
        return "PromjenaMape";
    }

    /**
     * Definira pozicije, rang, iz kojeg se dohvacaju prethodne poruke
     *
     * @return
     */
    public String prethodnePoruke() {
        pozicijaOd = pozicijaDo + 1;
        pozicijaDo = pozicijaOd + messageToShow - 1;
        preuzmiPoruke();
        return "PrethodnePoruke";
    }

    /**
     * Definira pozicije, rang, iz kojeg se dohvacaju sljedece poruke
     *
     * @return
     */
    public String sljedecePoruke() {
        pozicijaDo = pozicijaOd - 1;
        pozicijaOd = pozicijaOd - messageToShow;
        if (pozicijaOd < 1) {
            pozicijaOd = 1;
        }
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
