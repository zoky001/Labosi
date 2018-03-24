/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author grupa_1
 */
public class IOT {

    private List<Uredjaj_A> popisUredjaja = new ArrayList<>();
    private OutputStream os;
    private boolean upis = false;

//TODO Definirati strukturu i klasu za podatke pojedinog IOT uređaja
    public void createMOckJsonFile() {

        try {
            Uredjaj_A ua = new Uredjaj_A(0, 32, 23, 26, 12, 2);
            File datKonf = new File("proba.json");
            os = Files.newOutputStream(datKonf.toPath(), StandardOpenOption.CREATE);
            Gson gsonObj = new Gson();
            String strJson = gsonObj.toJson(ua);
            System.out.println(strJson);
            os.write(strJson.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(IOT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public synchronized String addOrUpdateDevice(Uredjaj_A iotUredjaj)
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje u IOT");
            wait();
        }
        upis = true;
        String s = addOrUpdateDevice_privat(iotUredjaj);
        upis = false;
        notify();
        return s;
    }
    
    private String addOrUpdateDevice_privat(Uredjaj_A iotUredjaj) {
        try {
            for (Uredjaj_A uredjaj_A : popisUredjaja) {
                if (uredjaj_A.getID() == iotUredjaj.getID()) {
                    uredjaj_A = iotUredjaj;
                    return RadnaDretva.OK_21;
                }
            }
            popisUredjaja.add(iotUredjaj);
            return RadnaDretva.OK_20;
        } catch (Exception e) {
            return RadnaDretva.ERROR_21;
        }
    }

}
