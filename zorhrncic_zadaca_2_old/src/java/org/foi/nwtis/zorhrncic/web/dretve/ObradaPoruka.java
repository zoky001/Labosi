/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.web.dretve;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import static jdk.nashorn.internal.codegen.OptimisticTypesPersistence.store;

/**
 *
 * @author grupa_1
 */
public class ObradaPoruka extends Thread {

    private boolean kraj = false;
    private int spavanje;
    private Folder folder;
    private Store store;
    private Session session;
    private String posluzitelj;
    private String korisnickoIme;
    private String lozinka;
private Message[] messages = null;
    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt();

    }

    @Override
    public void run() {

        while (!kraj) {

            try {
                // Start the session
                java.util.Properties properties = System.getProperties();
                properties.put("mail.smtp.host", posluzitelj);
                session = Session.getInstance(properties, null);

                // Connect to the store
                store = session.getStore("imap");
                store.connect(posluzitelj, korisnickoIme, lozinka);

                // Open the INBOX folder
                folder = store.getFolder("INBOX");
                folder.open(Folder.READ_ONLY);

                
                //TODO, ne dohvatiti sve porukeodjednom, nego po grupama
                messages = folder.getMessages();
                
                
                        // Print each message
        for (int i = 0; i < messages.length; ++i) {
          //TODO pretraži tzv. NWTIS poruke i s njima obavi potrebne radnje    
        }
        
        folder.close(false);
        store.close();
        
                //todo korigiraj vrijeme spavanja
                sleep(spavanje);
            } catch (Exception e) {
                System.out.println("org.foi.nwtis.zorhrncic.web.dretve.ObradaPoruka.run(): " + e.getMessage());
            }
        }

    }

    @Override
    public synchronized void start() {
//TODO preuzmji vrijeme spavanja iz konf
//preuzmi adresu posluzitelja, kor ime , looziinku, vrijeme,

        posluzitelj = "127.0.0.1";
        korisnickoIme = "servis@nwtis.nastava.foi.hr";
        lozinka = "123456";
        spavanje = 30 * 1000;

        super.start();

    }

}
