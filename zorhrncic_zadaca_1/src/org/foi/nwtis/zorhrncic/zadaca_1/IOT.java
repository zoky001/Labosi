/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
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

    public boolean isUpis() {
        return upis;
    }

    public void setUpis(boolean upis) {
        this.upis = upis;
    }

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
        while (isUpis()) {
            System.out.println("Netko upisuje u IOT");
            wait();
        }
        setUpis(true);
        String s = addOrUpdateDevice_privat(iotUredjaj);
        setUpis(false);
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

    public synchronized byte[] toStringser(Charset charset) throws InterruptedException, IOException {
        // upis = false;
        while (isUpis()) {
            System.out.println("Netko upisuje");
            wait();
        }
        setUpis(true);
        byte[] b = toStringserPrivate(charset);
        setUpis(false);
        System.out.println("Posao obavljen");
        notify();
        return b;
    }

    private byte[] toStringserPrivate(Charset charset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileWriter out = new FileWriter("tmp");
         out.write(String.format("|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|\n", "ID","Buka","Svjetlost","Temperatura","Vjetar","Vlaga"));
         out.write(String.format("|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|\n", "--------------------", "--------------------", "--------------------", "--------------------", "--------------------", "--------------------"));
        for (Uredjaj_A uredjaj_A : popisUredjaja) {
                  out.write(String.format("|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|\n", uredjaj_A.getID(), uredjaj_A.getBuka(), uredjaj_A.getSvjetlost(), uredjaj_A.getTemperatura(), uredjaj_A.getVjetar(), uredjaj_A.getVlaga()));
        }
        out.close();
        File inputFile = new File("tmp");
        BufferedReader bf = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(inputFile), charset));
        String linija = null;
        while ((linija = bf.readLine()) != null) {
            String s = linija + "\n";
            baos.write(s.getBytes(charset));
        }
        bf.close();

        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //%-40s
           String s = String.format("%-20s%-20s%-20s%-20s%-20s%-20s\n", "ID","Buka","Svjetlost","Temperatura","Vjetar","Vlaga");
            baos.write(charset.encode(s).array());
               s = String.format("%-20s%-20s%-20s%-20s%-20s%-20s\n", "--------------------", "--------------------", "--------------------", "--------------------", "--------------------", "--------------------");
            baos.write(charset.encode(s).array());
        for (Uredjaj_A uredjaj_A : popisUredjaja) {
            s = String.format("%-20s%-20s%-20s%-20s%-20s%-20s\n", uredjaj_A.getID(),uredjaj_A.getBuka(),uredjaj_A.getSvjetlost(),uredjaj_A.getTemperatura(),uredjaj_A.getVjetar(),uredjaj_A.getVlaga());
            baos.write(charset.encode(s).array());
        }*/
        return baos.toByteArray();
    }

}
