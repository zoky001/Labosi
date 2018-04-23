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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaJSON;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author grupa_1
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
     * Creates a new instance of SlanjePoruka
     */
    public SlanjePoruka() {
        //todo preuzeti iz postavki
        preuzmiKonfiuraciju();
        osvjeziNizDatoteka();
        privitak = "{}";
        odabranaDatoteka = "";

    }

    private void preuzmiKonfiuraciju() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konfiguracijaBaza = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");//new BP_Konfiguracija(putanja + datoteka);//baza
        konfiguracija = (Konfiguracija) servletContext.getAttribute("All_Konfig");//all config data

        posluziteljAddress = konfiguracija.dajPostavku("mail.server");
        posluziteljPort = Integer.valueOf(konfiguracija.dajPostavku("mail.imap.port"));
        prima = konfiguracija.dajPostavku("mail.usernameThread");
        salje = konfiguracija.dajPostavku("mail.usernameEmailAddress");
        predmet = konfiguracija.dajPostavku("mail.subjectEmail");
        nazivAttachmenta = konfiguracija.dajPostavku("mail.attachmentFilename");

    }

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

    public String preuzmiSadrzaj() {
//todo preuzmoi sadrzaj datoteke ciji je naziv u varijabli odabrana datoteka i pridruzi varijabli privitak
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;
        privitak = getJsonFile(putanja + odabranaDatoteka);
        odabranaDatotekaPath = putanja + odabranaDatoteka;
        //odabranaDatoteka;
        System.out.println("dat: " + odabranaDatotekaPath);
        return "";

    }

    public String saljiPoruku() {
       // preuzmiSadrzaj();
        try {
            // Create the JavaMail session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluziteljAddress);
            Session session = Session.getInstance(properties, null);
            // Construct the message
            MimeMessage message = new MimeMessage(session);
            // Set the from address
            Address fromAddress = new InternetAddress(salje);
            message.setFrom(fromAddress);
            // Parse and set the recipient addresses
            Address[] toAddresses = InternetAddress.parse(prima);
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            // Set the subject and text
            message.setSubject(predmet);
            message.setText("");

            //TODO treba kreirati privitaki u njega staviti sadržaj varijable privitak
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageAttachPart = new MimeBodyPart();

            messageAttachPart.setContent(privitak, "text/json; charset=utf-8");

            messageAttachPart.setFileName(nazivAttachmenta);

            multipart.addBodyPart(messageAttachPart);
            // Send the complete message parts
            message.setContent(multipart);
            Transport.send(message);
            poruka = "Uspješno poslana poruka!";
            privitak = "{}";
            //status = "Your message was sent.";
        } catch (Exception e) {
            
            poruka = "Greška u slanju poruke!!";
            //status = "There was an error parsing the addresses.";
        } 

        return "";
    }

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

    private String readFromFile(String datoteka) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(datoteka));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("errog: " + e.getMessage());
        }
        return null;
    }

    public static boolean testInputString(String sintaksa, String string) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        return m.matches();
    }
//validate

    /**
     *
     * Validation method 3
     */
    public void validateJSON(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String selectedRadio = (String) value;
        String msg = "Nije ispravan JSON format";
        try {
            JsonObject jsonObject = new JsonParser().parse(selectedRadio).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        }

    }

    public String getPoruka() {
        return poruka;
    }

    //getter & setter
    public void setPoruka(String poruka) {
        this.poruka = poruka;
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
