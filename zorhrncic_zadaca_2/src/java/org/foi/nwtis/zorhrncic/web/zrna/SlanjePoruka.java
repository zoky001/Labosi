/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.zrna;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author grupa_1
 */
@Named(value = "slanjePoruka")
@RequestScoped
public class SlanjePoruka {

    private String posluzitelj, prima, salje, predmet, privitak, odabranaDatoteka;

    public String getPrivitak() {
        return privitak;
    }

    public void setPrivitak(String privitak) {
        this.privitak = privitak;
    }
    private List<String> nizDatoteka;

    /**
     * Creates a new instance of SlanjePoruka
     */
    public SlanjePoruka() {
        //todo preuzeti iz postavki
        posluzitelj = "127.0.0.1";
        prima = "servis@nwtis.nastava.foi.hr";
        salje = "admin@nwtis.nastava.foi.hr";
        predmet = "IOT poruka";
        privitak = "{}";
        osvjeziNizDatoteka();

    }

    private void osvjeziNizDatoteka() {
        nizDatoteka = new ArrayList<>();
//TODO preuzmi nazive datoteka .jsno s web inf direktorija
        nizDatoteka.add("primjer1.json");
        nizDatoteka.add("primjer2.json");
        nizDatoteka.add("primjer3.json");
        nizDatoteka.add("primjer4.json");
        nizDatoteka.add("primjer5.json");
        nizDatoteka.add("primjer6.json");
        nizDatoteka.add("primjer7.json");
    }

    public String preuzmiSadrzaj() {
//todo preuzmoi sadrzaj datoteke ciji je naziv u varijabli odabrana datoteka i pridruzi varijabli privitak
        privitak = odabranaDatoteka;

        return "";

    }

    public String saljiPoruku() {
        try {
            // Create the JavaMail session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", posluzitelj);

            Session session
                    = Session.getInstance(properties, null);

            // Construct the message
            MimeMessage message = new MimeMessage(session);

            // Set the from address
            Address fromAddress = new InternetAddress(salje);
            message.setFrom(fromAddress);

            // Parse and set the recipient addresses
            Address[] toAddresses = InternetAddress.parse(prima);
            message.setRecipients(Message.RecipientType.TO, toAddresses);
/*
            Address[] ccAddresses = InternetAddress.parse(cc);
            message.setRecipients(Message.RecipientType.CC, ccAddresses);
*/
           /* Address[] bccAddresses = InternetAddress.parse(bcc);
            message.setRecipients(Message.RecipientType.BCC, bccAddresses);
*/
            // Set the subject and text
            message.setSubject(predmet);
            message.setText("");
            
            //TODO treba kreirati privitaki u njega staviti sadržaj varijable privitak

            Transport.send(message);

            //status = "Your message was sent.";

        } catch (AddressException e) {
            e.printStackTrace();
            //status = "There was an error parsing the addresses.";
        } catch (SendFailedException e) {
            e.printStackTrace();
            //status = "There was an error sending the message.";
        } catch (MessagingException e) {
            e.printStackTrace();
            //status = "There was an unexpected error.";
        }
        
        privitak = "{}";
        return "";
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
        return nizDatoteka;
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
