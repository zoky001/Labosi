/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nikbukove.konfiguracije.Konfiguracija;

/**
 *
 * @author grupa_1
 */
public class AdministratorSustava extends KorisnikSustava {

    Konfiguracija konfig;

    public AdministratorSustava(Konfiguracija konfig) {
        super();
        this.konfig = konfig;
    }

    public void preuzmiKontrolu() {

        try {
            Socket socket = new Socket(adresa, port);

            try (
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();) {

                String komanda = "KORISNIK  " + korisnik + "; LOZINKA " + lozinka + "; PAUZA;"; // korisnik iz korisnik sustava 

                outputStream.write(komanda.getBytes());
                outputStream.flush();
                outputStream.close();

                int znak;
                StringBuffer buffer = new StringBuffer();
                while (true) {
                    znak = inputStream.read();
                    if (znak == -1) {
                        break;
                    }
                    buffer.append((char) znak);

                }
                System.out.println("buffer: " + buffer.toString());

            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            Logger.getLogger(AdministratorSustava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
