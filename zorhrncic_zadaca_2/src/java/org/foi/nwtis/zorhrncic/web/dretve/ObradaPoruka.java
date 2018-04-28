/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.dretve;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
import javax.servlet.ServletContext;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * Dretva koja se okida nakon svakog intervala i obavlja sortiranje poruka u
 * mape i pohranjivanje IOT uredjaja u bazu podataka.
 *
 * @author Zoran Hrncic
 */
public class ObradaPoruka extends Thread {

    private String formatIspisa = "|%-25s|%-25s|\n";
    private String line20 = "-------------------------";
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
    private String sintaksaJSON = "^\\{\\\"id\\\": ([0-9]{0,4})\\, \\\"komanda\\\"\\: \\\"(dodaj|azuriraj)\\\"\\,((( ((\\\"([A-Za-z0-9_]{1,30})\\\"\\: ((\\d{1,3},|(\\b(?!0\\d{1,2}\\.)\\d{1,3}\\.\\d{1,2}\\b\\,))|\\\"[a-zA-Z0-9_ ]*{1,30}\\\"\\,))))){1,5}) \\\"vrijeme\\\"\\: \\\"((19|20)\\d\\d).(0?[1-9]|1[012]).(0?[1-9]|[12][0-9]|3[01]) ([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])\\\"}";

    private String patternDateTimeSQL = "yyyy-MM-dd H:m:s";
    private final SimpleDateFormat sqlDateFormat;
    private String jsonString;
    private UIDFolder uf;
    private long messageID;
    private int processingNumber = 0;
    private int messageNumber_Total = 0;
    private int addedIot = 0;
    private int updatedIot = 0;
    private int messageNumber_incorrect = 0;
    private int toMessage;
    private int fromMessage;
    private int numMessagesInFolder;
    private long sleepTime;
    private static String AttachFormat1 = "";
    private static String AttachFormat2 = "";

    /**
     * Pokrece pruzimanje podataka iz konfiguracije. Definira format datuma SQL
     * baze
     *
     */
    public ObradaPoruka() {
        preuzmiKonfiuraciju();
        sqlDateFormat = new SimpleDateFormat(patternDateTimeSQL);
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
        AttachFormat1 = "TEXT/JSON; charset=utf-8; name=" + nazivAttachmenta;
        AttachFormat2 = "APPLICATION/JSON; charset=utf-8; name=" + nazivAttachmenta;

    }

    @Override
    public void interrupt() {
        krajRada = true;
        super.interrupt();

    }

    /**
     * pokrece obradu i odredjuje vrijeme NERADA dretve
     *
     */
    @Override
    public void run() {
        int broj = 0;
        while (!krajRada) {
            pocetak = System.currentTimeMillis();
            processingNumber++;
            messageNumber_Total = 0;
            addedIot = 0;
            updatedIot = 0;
            messageNumber_incorrect = 0;
            fromMessage = 1;
            toMessage = 1;
            numMessagesInFolder = 0;
            if (kraj != 0) {
                System.out.println("Razlika od prosle serijalizacije: " + (pocetak - kraj) / 1000 + " sec");
            }
            try {
                runProcessing();
                System.out.println("Gotova obrada: " + broj++);
                kraj = System.currentTimeMillis();
                razlika = kraj - pocetak;
                sleepTime = (spavanje * 1000 - razlika) + (long) (koef * (spavanje * 1000 - razlika));
                if (sleepTime < 0) {
                    sleepTime = 0;
                }
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                System.out.println("org.foi.nwtis.zorhrncic.web.dretve.ObradaPoruka.run(): " + e.getMessage());
            }
        }

    }

    /**
     * Pozivanje redom potrebnih metoda za obradu poruka. Na kraju pohrana
     * podataka o radu u datoteku.
     *
     * @throws MessagingException
     * @throws Exception
     */
    private void runProcessing() throws MessagingException, Exception {
        folder = connectAndReadINBOX();
        uf = (UIDFolder) folder;
        numMessagesInFolder = folder.getMessageCount();
        while (numMessagesInFolder > toMessage) {
            if (fromMessage < toMessage) {
                fromMessage = toMessage + 1;
            }
            toMessage = (fromMessage + brojPorukaZaUcitavanje) - 1;
            if (toMessage > numMessagesInFolder) {
                toMessage = numMessagesInFolder;
            }
            messages = readMessages(folder, fromMessage, toMessage);
            prosessMessageRange();
        }
        folder.expunge();
        folder.close(false);
        store.close();
        spremiPodatkeRadu(pocetak, System.currentTimeMillis());
    }

    /**
     * Dohvacanje i obrada poruka redom po opsegu definiranom u konfiguraciji
     *
     * @throws Exception
     */
    private void prosessMessageRange() throws Exception {
        for (int i = 0; i < messages.length; ++i) {
            messageNumber_Total++;
            jsonString = "nema privitka";
            messageID = uf.getUID(messages[i]);
            Flags flags = messages[i].getFlags();
            MimeBodyPart part = checkIfExistAttachment(messages[i]);
            if (part != null) {
                jsonString = processingMessage(messages[i], part);
                if (jsonString != null) {
                    if (valdiateJSONAttachment(jsonString)) {
                        processIOTdeviceData(jsonString);
                        moveMessageToNWTISFolder(messages[i], nazivMape, folder);
                    } else {
                        messageNumber_incorrect++;
                        setFlags(flags, i);
                    }
                } else {
                    messageNumber_incorrect++;
                    setFlags(flags, i);
                }
            } else {
                messageNumber_incorrect++;
                setFlags(flags, i);
            }
            saveMessageToLog(messages[i], jsonString, messageID);
        }
    }

    /**
     * Postavlja zastavice koje oznacavaju stanje poruka na vrijednost koja je
     * bila prije obrade.
     *
     * @param flags
     * @param i
     * @throws MessagingException
     */
    private void setFlags(Flags flags, int i) throws MessagingException {
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

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * Pohranjuje podatke o rezultatu obrade u txt datotku.
     *
     * @param pocetak
     * @param currentTimeMillis
     */
    private void spremiPodatkeRadu(long pocetak, long currentTimeMillis) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileWriter out = new FileWriter(nazivDatotekePodatciRadu);
            printAllData(out, pocetak, currentTimeMillis);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Kreiranje formata zpisa datoteke koja se kreira.
     *
     * Sa svim podatcima.
     *
     * @param out
     * @param pocetak
     * @param currentTimeMillis
     * @throws IOException
     */
    private void printAllData(FileWriter out, long pocetak, long currentTimeMillis) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss.SSS");
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Obrada poruka broj: ", processingNumber));
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Obrada započela u: ", dateFormat.format(new Date(pocetak))));
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Obrada završila u: ", dateFormat.format(new Date(currentTimeMillis))));
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Trajanje obrade u ms: ", currentTimeMillis - pocetak));
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Broj poruka: ", messageNumber_Total));
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Broj dodanih IOT: ", addedIot));
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Broj ažuriranih IOT: ", updatedIot));
        out.write(String.format(formatIspisa, line20, line20));
        out.write(String.format(formatIspisa, "Broj neispravnih poruka: ", messageNumber_incorrect));
        out.write(String.format(formatIspisa, line20, line20));
    }

    /**
     * Otvaranje INBOX mape za sitanje poruka.
     *
     * @return
     * @throws NoSuchProviderException
     * @throws MessagingException
     */
    private Folder connectAndReadINBOX() throws NoSuchProviderException, MessagingException {
        Folder folder = null;
        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", adresaServera);
        session = Session.getInstance(properties, null);
        store = session.getStore("imap");
        store.connect(adresaServera, korisnickoIme, lozinka);
        folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    /**
     * Dohvacanje poruka iz mape koja je prosljedjena. Poruke se dohvacaju prema
     * prosljedjenim pozicijama: Od, Do
     *
     * @param folder
     * @param from
     * @param to
     * @return
     */
    private Message[] readMessages(Folder folder, int from, int to) {
        Message[] messages = null;
        try {
            messages = folder.getMessages(from, to);
            return messages;
        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class
                    .getName()).log(Level.SEVERE, null, ex);
            return messages;
        }
    }

    /**
     * Provjerava postoji li definirani attachment u poruci.
     *
     * @param message poruka
     * @return attachment ako postoji, ako ne postoji onda null
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
        return defineReturnValueForCheckAttachmentMethod(isNwtisMessage, numAttach, attachment);
    }

    /**
     * Provjerava rezultate provjera attachmetna i definira odgovor prema
     * trazenim uvjetima
     *
     * @param isNwtisMessage
     * @param numAttach
     * @param attachment
     * @return
     */
    private MimeBodyPart defineReturnValueForCheckAttachmentMethod(boolean isNwtisMessage, int numAttach, MimeBodyPart attachment) {
        if (isNwtisMessage && numAttach == 1 && attachment != null) {
            return attachment;
        } else {
            return null;
        }
    }

    /**
     * Vraca sadrzaj privitka poruke ako je sve po zahtjevima, inace vraca null
     *
     * @param message poruka
     * @param attachment privitak
     * @return null or attachment
     */
    private String processingMessage(Message message, MimeBodyPart attachment) {
        try {
            if (AttachFormat1.equalsIgnoreCase(attachment.getContentType()) || AttachFormat2.equalsIgnoreCase(attachment.getContentType())) {
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
                System.out.println("Primljeni je : " + message.getContentType());
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validira dali je json zapis u privitku ispravan json format sa
     * definiranim poljima.
     *
     * @param JSONfILE
     * @return
     */
    private boolean valdiateJSONAttachment(String JSONfILE) {
        Gson gson = new Gson();
        boolean odg = false;
        try {
            JsonObject jsonObject = new JsonParser().parse(JSONfILE).getAsJsonObject();
            odg = testInputString(sintaksaJSON, JSONfILE);
        } catch (JsonSyntaxException e) {
            odg = false;
        } catch (Exception e) {
            odg = false;
        }
        return odg;
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

    /**
     * Obrada json zapisa i prepoznavanje komande zahtjeva, te pozivanje upisa u
     * bazu podataka
     *
     * @param JSONfILE
     */
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
            writeInDatabase(iot_id, iot_command, iot_atributs, iot_time, JSONfILE);
        }
    }

    /**
     *
     * Ovisno o prosljedjenoj kondandu vrsi daljjnje akcije sa JSON zapisom
     * Poziva dodavanje uredjaja, ili update uredjaja.
     *
     * @param IOT_id
     * @param IOT_command
     * @param IOT_atributs
     * @param IOT_time
     * @param json
     */
    private void writeInDatabase(int IOT_id, String IOT_command, Properties IOT_atributs, Date IOT_time, String json) {
        if (IOT_command.equals("dodaj")) {
            addIot(IOT_id, IOT_atributs, IOT_time, json);
        } else if (IOT_command.equals("azuriraj")) {
            updateIot(IOT_id, IOT_atributs, IOT_time, json);
        }
    }

    /**
     * Prvjerava postoji li IOT uredjaj u bazi i ako ne postoji, onda pokrece
     * dodavanej novog. Update-a podatke o radu dretve
     *
     * @param IOT_id
     * @param IOT_atributs
     * @param IOT_time
     * @param json
     */
    private void addIot(int IOT_id, Properties IOT_atributs, Date IOT_time, String json) {
        if (!checkIfExistIOT(IOT_id) && IOT_atributs.getProperty("naziv") != null) {
            if (addIOTinDatabase(IOT_id, IOT_atributs.getProperty("naziv"), json)) {
                addedIot++;
            } else {
                messageNumber_incorrect++;
            }
        } else {
            messageNumber_incorrect++;
        }
    }

    /**
     * Prvjerava postoji li IOT uredjaj u bazi i ako postoji, onda pokrece
     * update uredjaja. Update-a podatke o radu dretve
     *
     * @param IOT_id
     * @param IOT_atributs
     * @param IOT_time
     * @param json
     */
    private void updateIot(int IOT_id, Properties IOT_atributs, Date IOT_time, String json) {
        if (checkIfExistIOT(IOT_id)) {
            if (updateIOTinDatabase(IOT_id, IOT_atributs, json)) {
                updatedIot++;
            } else {
                messageNumber_incorrect++;
            }
        } else {
            messageNumber_incorrect++;
        }

    }

    /**
     * Provjerava postoji li uredjaja za prosljedjenim ID u bazi podataka.
     *
     * @param IOT_id
     * @return true - ako postoji, false - ako ne postoji
     */
    private boolean checkIfExistIOT(int IOT_id) {
        upit = "SELECT * FROM `uredaji` WHERE `id` = " + IOT_id;
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean exist = false;
        try {
            Class.forName(uprProgram);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(upit);) {
            while (rs.next()) {
                exist = true;
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

    /**
     * Upisuje IOT uredjaj u bazu podataka
     *
     * @param IOT_id
     * @param nameIot
     * @param json
     * @return true - uspjesno upisan, false - neuspjesno
     */
    private boolean addIOTinDatabase(int IOT_id, String nameIot, String json) {
        upit = "INSERT INTO `uredaji` (`id`, `naziv`, `sadrzaj`, `vrijeme_promjene`, `vrijeme_kreiranja`) VALUES ('" + IOT_id + "', '" + nameIot + "', '" + json + "', '" + sqlDateFormat.format(new Date()) + "' , '" + sqlDateFormat.format(new Date()) + "')";
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean success = false;
        try {
            Class.forName(uprProgram);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();) {
            stmt.execute(upit);
            success = true;
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            return success;
        }

    }

    /**
     * Uredjuje podatke o postojecem uredjaju u bazi podataka, sa novim
     * podatcima.
     *
     * @param IOT_id
     * @param IOT_atributs
     * @param json
     * @return
     */
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
            Logger.getLogger(ObradaPoruka.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try (
                Connection con = DriverManager.getConnection(urlDatabase, usernameAdminDatabase, lozinkaDatabase);
                Statement stmt = con.createStatement();) {
            success = stmt.execute(upit);
            success = true;
            stmt.close();
            con.close();
        } catch (Exception e) {
            return success;
        } finally {
            return success;
        }
    }

    /**
     * Premjesta poruke iz mape INBOX u novu mapu. Brise poruke iz mape INBOX
     *
     * @param mArray
     * @param folderName
     * @param currentFolder
     * @throws Exception
     */
    private void moveMessageToNWTISFolder(Message mArray, String folderName, Folder currentFolder) throws Exception {
        Message[] array = new Message[1];
        array[0] = mArray;
        Folder f = findFolder(folderName);
        currentFolder.copyMessages(array, f);
        for (int i = 0; i < array.length; i++) {
            array[i].setFlag(Flags.Flag.DELETED, true);
        }
        currentFolder.expunge();
    }

    /**
     * Pronalazenje mapre prema imenu
     *
     * @param folderName
     * @return ako postoji vraca mapu, ako ne postoji vraca null
     */
    private Folder findFolder(String folderName) {
        try {
            Folder folder = null;
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", adresaServera);
            session = Session.getInstance(properties, null);
            store = session.getStore("imap");
            store.connect(adresaServera, korisnickoIme, lozinka);
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
            Logger.getLogger(ObradaPoruka.class
                    .getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Kreiranje novog direktorija prema nazivu i parent direktoriju
     *
     * @param parent
     * @param folderName
     * @return novokreirani direktorij
     */
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

    /**
     * Upisivanje poruka u dnevnik u bazi podataka.
     *
     * @param message
     * @param jsonString
     * @param id
     */
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
            Logger.getLogger(ObradaPoruka.class
                    .getName()).log(Level.SEVERE, null, ex);
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

    /**
     * Provjerava da li postoji poruka upisna u dnevnik. Ako postoji upisana,
     * onda vraca true, inace false
     *
     * @param IOT_id
     * @return true - zapis postoji, false - zapis ne postoji
     */
    private boolean checkIfExistMessageInLog(long IOT_id) {
        upit = "SELECT * FROM `dnevnik` WHERE `id` = " + IOT_id;
        uprProgram = konfiguracijaBaza.getDriverDatabase();
        boolean exist = false;
        try {
            Class.forName(uprProgram);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObradaPoruka.class
                    .getName()).log(Level.SEVERE, null, ex);
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
