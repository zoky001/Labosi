/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa sadrzi sve metode koje sluze za upravljanje slanjem e-mail poruka.
 *
 * @author Zoran Hrncic
 */
@Named(value = "slanjePoruka")
@RequestScoped
public class SlanjePoruka {

    private String posluziteljAddress, prima, salje, predmet, privitak, odabranaDatoteka;
    private BP_Konfiguracija konfiguracijaBaza;
    private Konfiguracija konfiguracija;
    private List<String> naziviDatoteka;
    private Integer posluziteljPort;
    private final String sintaksaJSON = "([^\\s]+\\.(?i)(json|json))";
    private String odabranaDatotekaPath;
    private String nazivAttachmenta;
    private String poruka = "";

    /**
     * Creates a new instance of SlanjePoruka. Poziva metodu za preuzimanje
     * konfiguracije i podataka potrebnih za rad. Poziva metodu za osvjezavanje
     * niza datoteka koje se prikazuju.
     */
    public SlanjePoruka() {
        preuzmiKonfiuraciju();
        osvjeziNizDatoteka();
        privitak = "{}";
        odabranaDatoteka = "";
    }

    /**
     * Preuzima konfiguraciju iz kontexta i pohranjije potrebne podatke u
     * globalne vrijable.
     *
     */
    private void preuzmiKonfiuraciju() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().
                getExternalContext().getContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");
        posluziteljAddress = konfiguracija.dajPostavku("mail.server");
        posluziteljPort = Integer.valueOf(konfiguracija.dajPostavku("mail.imap.port"));
        prima = konfiguracija.dajPostavku("mail.usernameThread");
        salje = konfiguracija.dajPostavku("mail.usernameEmailAddress");
        predmet = konfiguracija.dajPostavku("mail.subjectEmail");
        nazivAttachmenta = konfiguracija.dajPostavku("mail.attachmentFilename");

    }

    /**
     * Dohvaca sve JSON datoteke koje se nalaze u direktoriju WEB-INF.
     * Pohranjuje ih u niz "nazivDatoteke"
     */
    private void osvjeziNizDatoteka() {
        naziviDatoteka = new ArrayList<>();
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;
        final File folder = new File(putanja);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
            } else {
                System.out.println(fileEntry.getName());
                if (testInputString(sintaksaJSON, fileEntry.getName())) {
                    naziviDatoteka.add(fileEntry.getName());
                }
            }
        }
    }

    /**
     * Preuzima sadrzaj iz odabrane JSON datoteke i pohranjije ga u varijablu
     * "privitak"
     *
     * @return
     */
    public String preuzmiSadrzaj() {
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;
        privitak = getJsonFile(putanja + odabranaDatoteka);
        odabranaDatotekaPath = putanja + odabranaDatoteka;
        return "";
    }

    /**
     * Salje email poruku sa sadrzajem privitka u JSON formatu. Svi podtci
     * moraju biti prethodno definiriani
     *
     * @return
     */
    public String saljiPoruku() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ResourceBundle resBund = ctx.getApplication().getResourceBundle(ctx, "p");
        try {
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluziteljAddress);
            Session session = Session.getInstance(properties, null);
            MimeMessage message = new MimeMessage(session);
            Address fromAddress = new InternetAddress(salje);
            message.setFrom(fromAddress);
            Address[] toAddresses = InternetAddress.parse(prima);
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            message.setSubject(predmet);
            Multipart multipart = createMessageMultipart_Body_Attachment();
            message.setContent(multipart);
            Transport.send(message);
            poruka = resBund.getString("slanjePoruka.uspjesnoPoslano");
            privitak = "{}";
            //status = "Your message was sent.";
        } catch (Exception e) {
            poruka = resBund.getString("slanjePoruka.neuspjesnoPoslano");
        }
        return "";
    }

    /**
     * Kreira multipart objekt koji sadrzi attachment i body poruke
     *
     * @param messageBodyPart
     * @return multipart objekt email poruke
     * @throws MessagingException
     */
    private Multipart createMessageMultipart_Body_Attachment() throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("");
        Multipart multipart = new MimeMultipart();
        MimeBodyPart messageAttachPart = new MimeBodyPart();
        messageAttachPart.setContent(privitak, "application/json; charset=utf-8");
        messageAttachPart.setFileName(nazivAttachmenta);
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(messageAttachPart);
        return multipart;
    }

    /**
     * Provjerava postoji li datoteka sa prosljedjenim nazivom na lokaciji
     *
     * @param datoteka . naziv datoteke sa putanjom
     * @return json sadrzaj datoteke ako postojei, inace null
     */
    private String getJsonFile(String datoteka) {
        try {
            if (datoteka == null || datoteka.length() == 0) {
                throw new NemaKonfiguracije("naziv datoteke nedostaje");
            }
            File datKonf = new File(datoteka);
            if (!datKonf.exists()) {
                throw new NemaKonfiguracije("Datoteka: " + datoteka + " ne postoji!");
            } else if (datKonf.isDirectory()) {
                throw new NeispravnaKonfiguracija(datoteka + " nije datoteka već direktorij");
            }
            return readFromFile(datoteka);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.out.println("Nema datoteke sa IOT podatcima");
        }
        return null;
    }

    /**
     * Cita sadrzaj prosljedjene datoteke i vraca sadrzaj ako postoji, inace
     * vrati null
     *
     * @param datoteka naziv datoteke sa putanjom
     * @return sadrzaj datoteke
     */
    private String readFromFile(String datoteka) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(datoteka));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("errog: " + e.getMessage());
        }
        return null;
    }

    /**
     * Metoda za testiranje podudaranja ulaznog stringa i uzorka REgex-a
     *
     * @param sintaksa uzorak Regex
     * @param string string za testiranje
     * @return true ako odgovara, inace false
     */
    public static boolean testInputString(String sintaksa, String string) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        return m.matches();
    }

    //validator methods
    /**
     * Validacija unosa privitka na foramu za unos. MORA biti JSON format, inace
     * se okida iznimka.
     *
     */
    public void validateJSON(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String selectedRadio = (String) value;
        FacesContext ctx = FacesContext.getCurrentInstance();
        ResourceBundle resBund = ctx.getApplication().getResourceBundle(ctx, "p");
        String msg = resBund.getString("slanjePoruka.nijeJSON");
        try {
            JsonObject jsonObject = new JsonParser().parse(selectedRadio).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            privitak = "{}";
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        } catch (Exception e) {
            privitak = "{}";
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        }
    }

    //getter & setter
    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public String getPoruka() {
        return poruka;
    }

    public String getPrivitak() {
        return privitak;
    }

    public void setPrivitak(String privitak) {
        this.privitak = privitak;
    }

    public String obrisiPrivitak() {
        privitak = "{}";
        return "";
    }

    public String getPrima() {
        return prima;
    }

    public void setPrima(String prima) {
        this.prima = prima;
    }

    public String getSalje() {
        return salje;
    }

    public void setSalje(String salje) {
        this.salje = salje;
    }

    public String getPredmet() {
        return predmet;
    }

    public void setPredmet(String predmet) {
        this.predmet = predmet;
    }

    public List<String> getNizDatoteka() {
        return naziviDatoteka;
    }

    public String getOdabranaDatoteka() {
        return odabranaDatoteka;
    }

    public void setOdabranaDatoteka(String odabranaDatoteka) {
        this.odabranaDatoteka = odabranaDatoteka;
    }

    //navigacija
    public String promjenaJezika() {
        return "promjenaJezika";
    }

    public String pregledPoruka() {
        return "pregledPoruka";
    }

    public String pregledDnevnika() {
        return "pregledDnevnika";
    }

}
