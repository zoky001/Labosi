/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.dretve;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
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
    private int iot_id;
    private String iot_command;
    private Date iot_time;
    private Properties iot_atributs;
    private String upit;
    private String uprProgram;
    private String usernameAdminDatabase;
    private String lozinkaDatabase;
    private String urlDatabase;
    private String patternDateTimeSQL = "yyyy-MM-dd H:m:s";
    private final SimpleDateFormat sqlDateFormat;
    private String jsonString;
    private UIDFolder uf;
    private long messageID;

    public ObradaPoruka() {
        preuzmiKonfiuraciju();
        sqlDateFormat = new SimpleDateFormat(patternDateTimeSQL);
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
        usernameAdminDatabase = konfiguracijaBaza.getUserUsername();
        lozinkaDatabase = konfiguracijaBaza.getUserPassword();
        urlDatabase = konfiguracijaBaza.getServerDatabase() + konfiguracijaBaza.getUserDatabase();

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

                folder = connectAndReadINBOX();
                uf = (UIDFolder) folder;
                messages = readMessages(folder);

                for (int i = 0; i < messages.length; ++i) {
                    jsonString = "nema privitka";
                    messageID = uf.getUID(messages[i]);
                    System.out.println("\n--------------------------------------------------------------------------");
                    System.out.println("INBOX  [" + i + "] poruka: " + messages[i].getReceivedDate().toString());
                    Flags flags = messages[i].getFlags();

                    MimeBodyPart part = checkIfExistAttachment(messages[i]); // ovo je nwtis message
                    if (part != null) {
                        jsonString = processingMessage(messages[i], part);
                        if (jsonString != null) {
                            if (valdiateJSONAttachment(jsonString)) {
                                processIOTdeviceData(jsonString);
                                //prebaciti u mapu

                                moveMessageToNWTISFolder(messages[i], nazivMape, folder);
                            } else {

                            }
                        } else {

                        }
                    } else {

                        if (flags.contains(Flags.Flag.ANSWERED)) {
                            messages[i].setFlag(Flags.Flag.ANSWERED, true);
                        }
                        if (flags.contains(Flags.Flag.DELETED)) {
                            messages[i].setFlag(Flags.Flag.DELETED, true);
                        }
                        if (flags.contains(Flags.Flag.DRAFT)) {
                            messages[i].setFlag(Flags.Flag.DRAFT, true);
                        }
                        if (flags.contains(Flags.Flag.FLAGGED)) {
                            messages[i].setFlag(Flags.Flag.FLAGGED, true);
                        }
                        if (flags.contains(Flags.Flag.RECENT)) {
                            //messages[i].setFlag(Flags.Flag.RECENT, true);
                        }
                        if (flags.contains(Flags.Flag.SEEN)) {
                            messages[i].setFlag(Flags.Flag.SEEN, true);
                        }
                        if (flags.contains(Flags.Flag.USER)) {
                            messages[i].setFlag(Flags.Flag.USER, true);
                        }

                    }

                    saveMessageToLog(messages[i], jsonString, messageID);

                }
                folder.expunge();
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

    private Folder connectAndReadINBOX() throws NoSuchProviderException, MessagingException {
        Folder folder = null;

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
        return folder;
    }

    private Message[] readMessages(Folder folder) {
        Message[] messages = null;
        try {
            //TODO, ne dohvatiti sve porukeodjednom, nego po grupama
            messages = folder.getMessages();
            return messages;
            /*  // Print each message
            for (int i = 0; i < messages.length; ++i) {
                System.out.println("\n--------------------------------------------------------------------------");
                // System.out.println("poruka: " + messages[i].getFrom()[0]);
                checkIfExistAttachment(messages[i]);
            }*/

        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            return messages;
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

    private boolean valdiateJSONAttachment(String JSONfILE) {
        Gson gson = new Gson();
        //TODO valdiate json string
        return true;

    }

    public static boolean testInputString(String sintaksa, String string) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        return m.matches();
    }

    private void processIOTdeviceData(String JSONfILE) {
        iot_id = -1;
        iot_command = "";
        iot_time = null;
        iot_atributs = new Properties();
        try {
            JsonObject jsonObject = new JsonParser().parse(JSONfILE).getAsJsonObject();
            for (String en : jsonObject.keySet()) {
                if (en.equals("id")) {
                    iot_id = Integer.parseInt(jsonObject.get(en).getAsString());
                    //device.put(en.toLowerCase(), jsonObject.get(en).getAsString());
                } else if (en.equals("komanda")) {
                    iot_command = jsonObject.get(en).getAsString();
                } else if (en.equals("vrijeme")) {
                    String s = jsonObject.get(en).getAsString();
                    String pattern = "yyyy.dd.MM H:m:s";
                    DateFormat df = new SimpleDateFormat(pattern);
                    iot_time = df.parse(s);
                } else {
                    iot_atributs.put(en.toLowerCase(), jsonObject.get(en).getAsString());
                }
            }

        } catch (Exception e) {
        }
        if (iot_id != -1 && (iot_command.equals("dodaj") || iot_command.equals("azuriraj")) && iot_time != null && iot_atributs.size() < 6 && iot_atributs.size() > 0) {
            /*  System.out.println("ISPRAVAN JSON PRIVITAK");
            System.out.println(iot_id);
            System.out.println(iot_command);
            System.out.println(iot_time);*/
            for (Object object : iot_atributs.keySet()) {
                //System.out.println(object.toString() + " - " + iot_atributs.get(object).toString());
            }
            writeInDatabase(iot_id, iot_command, iot_atributs, iot_time, JSONfILE);
        }

    }

    private void writeInDatabase(int IOT_id, String IOT_command, Properties IOT_atributs, Date IOT_time, String json) {

        if (IOT_command.equals("dodaj")) {
            addIot(IOT_id, IOT_atributs, IOT_time, json);
        } else if (IOT_command.equals("azuriraj")) {
            updateIot(IOT_id, IOT_atributs, IOT_time, json);
        }

    }

    private void addIot(int IOT_id, Properties IOT_atributs, Date IOT_time, String json) {
        if (!checkIfExistIOT(IOT_id) && IOT_atributs.getProperty("naziv") != null) {
            if (addIOTinDatabase(IOT_id, IOT_atributs.getProperty("naziv"), json)) {
                //TODO uspjesno dodan uređaj
            } else {
                //grešk kod dodavanja uređaja
            }
        } else {
            //TODO vrati pogrski da postoji IOT
        }
    }

    private void updateIot(int IOT_id, Properties IOT_atributs, Date IOT_time, String json) {
        if (checkIfExistIOT(IOT_id)) {
            if (updateIOTinDatabase(IOT_id, IOT_atributs, json)) {
                //TODO uspjesno update uređaj

            } else {
                //grešk kod dodavanja uređaja
            }
        } else {
            //TODO vrati pogrski da postoji IOT
        }

    }

    private boolean checkIfExistIOT(int IOT_id) {
        upit = "SELECT * FROM `uredaji` WHERE `id` = " + IOT_id;
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean exist = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                exist = true;
                //TODO pogreška ??
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return exist;
        }
    }

    private boolean addIOTinDatabase(int IOT_id, String nameIot, String json) {
        upit = "INSERT INTO `uredaji` (`id`, `naziv`, `sadrzaj`, `vrijeme_promjene`, `vrijeme_kreiranja`) VALUES ('" + IOT_id + "', '" + nameIot + "', '" + json + "', '" + sqlDateFormat.format(new Date()) + "' , '" + sqlDateFormat.format(new Date()) + "')";
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean success = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();) {
            success = stmt.execute(upit);

            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return success;
        }

    }

    private boolean updateIOTinDatabase(int IOT_id, Properties IOT_atributs, String json) {
        if (IOT_atributs.getProperty("naziv") != null) {
            upit = "UPDATE `uredaji` SET `naziv`= '" + IOT_atributs.getProperty("naziv") + "',`sadrzaj`='" + json + "',`vrijeme_promjene`='" + sqlDateFormat.format(new Date()) + "' WHERE `id`= " + IOT_id;
        } else {
            upit = "UPDATE `uredaji` SET `sadrzaj`= '" + json + "',`vrijeme_promjene`='" + sqlDateFormat.format(new Date()) + "' WHERE `id`=" + IOT_id;

        }
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean success = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();) {
            success = stmt.execute(upit);

            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return success;
        } finally {
            return success;
        }

    }

    private void moveMessageToNWTISFolder(Message mArray, String folderName, Folder currentFolder) throws Exception {
        Message[] array = new Message[1];
        array[0] = mArray;
        Folder f = findFolder(folderName);

        currentFolder.copyMessages(array, f);

        // Now the delete the messages from Current Folder
        for (int i = 0; i < array.length; i++) {
            array[i].setFlag(Flags.Flag.DELETED, true);
        }
        currentFolder.expunge();
    }

    private Folder findFolder(String folderName) {
        try {
            Folder folder = null;

            // Start the session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", adresaServera);
            session = Session.getInstance(properties, null);
            // Connect to the store
            store = session.getStore("imap");
            store.connect(adresaServera, korisnickoIme, lozinka);
            // Open the INBOX folder
            folder = store.getFolder(folderName);
            if (folder.exists()) {
                folder.open(Folder.READ_WRITE);
                return folder;
            } else {
                Folder f = createFolder(store, folderName);
                if (f != null) {
                    return f;
                }

            }

            return null;
        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private Folder createFolder(Store parent, String folderName) {
        boolean isCreated = true;
        Folder newFolder = null;
        try {
            newFolder = parent.getFolder(folderName);
            isCreated = newFolder.create(Folder.HOLDS_FOLDERS);
            System.out.println("created: " + isCreated);

        } catch (Exception e) {
            System.out.println("Error creating folder: " + e.getMessage());
            e.printStackTrace();
            isCreated = false;
        }
        if (isCreated) {
            return newFolder;
        } else {
            return null;
        }
    }

    private void saveMessageToLog(Message message, String jsonString, Long id) {

        if (checkIfExistMessageInLog(id)) {
            return;
        }
        upit = "INSERT INTO `dnevnik` (`id`, `sadrzaj`, `vrijeme`) VALUES ('" + id + "', '" + jsonString + "', '" + sqlDateFormat.format(new Date()) + "')";

        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean success = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();) {
            success = stmt.execute(upit);

            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

    }

    private boolean checkIfExistMessageInLog(long IOT_id) {
        upit = "SELECT * FROM `dnevnik` WHERE `id` = " + IOT_id;
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean exist = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                exist = true;
                //TODO pogreška ??
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return exist;
        }
    }

}
