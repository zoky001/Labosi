/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author grupa_1
 */
public class IOT {


    private List<Properties> popisUredjajaNew = new ArrayList<>();
    private OutputStream os;
    private boolean upis = false;
    private String formatIspisa = "|%-20s|%-20s|%-20s|%-20s|%-20s|%-20s|\n";
    private String line20 = "--------------------";
    private Gson gson = new Gson();

    public boolean isUpis() {
        return upis;
    }

    public void setUpis(boolean upis) {
        this.upis = upis;
    }


    public synchronized String addOrUpdateDevice(String iotUredjaj)
            throws InterruptedException {
        while (isUpis()) {
            System.out.println("Netko upisuje u IOT");
            wait();
        }
        setUpis(true);
        String s = addDeviceFromJson(iotUredjaj);
        setUpis(false);
        notify();
        return s;
    }

    public List<Properties> getPopisUredjajaNew() {
        return popisUredjajaNew;
    }

    public void setPopisUredjajaNew(List<Properties> popisUredjajaNew) {
        this.popisUredjajaNew = popisUredjajaNew;
    }



    public synchronized byte[] toStringser(Charset charset) {
        try {
            while (isUpis()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(IOT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            setUpis(true);
            byte[] b = toStringserPrivate(charset);
            setUpis(false);
            notify();
            return b;
        } catch (IOException ex) {
            Logger.getLogger(IOT.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private byte[] toStringserPrivate(Charset charset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileWriter out = new FileWriter("tmp");
        printAllDevice(out);
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
        return baos.toByteArray();
    }

    private String addDeviceFromJson(String string) {
        try {
            JsonObject jsonObject = new JsonParser().parse(string).getAsJsonObject();
            if (getIDIfExist(jsonObject) == -1) {
                return RadnaDretva.ERROR_21;
            } else {
                if (getDeviceWithIdIfExist(getIDIfExist(jsonObject)) != null) {
                    if (updateDevice(getDeviceWithIdIfExist(getIDIfExist(jsonObject)), jsonObject)) {
                        return RadnaDretva.OK_21;
                    } else {
                        return RadnaDretva.ERROR_21;
                    }
                } else {
                    if (addDevice(jsonObject)) {
                        return RadnaDretva.OK_20;
                    } else {
                        return RadnaDretva.ERROR_21;
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            return RadnaDretva.ERROR_20;
        }
    }

    private int getIDIfExist(JsonObject jsonObject) {
        try {
            if (jsonObject.get("id") != null) {
                return Integer.parseInt(jsonObject.get("id").toString());
            } else if (jsonObject.get("ID") != null) {
                return Integer.parseInt(jsonObject.get("ID").toString());
            } else {
                return -1;//error 21
            }
        } catch (Exception e) {
            return -1;//error 21
        }
    }

    private Properties getDeviceWithIdIfExist(int id) {
        try {
            for (Properties properties : popisUredjajaNew) {
                if (properties.containsKey("id") && Integer.parseInt(properties.getProperty("id")) == id) {
                    return properties;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean addDevice(JsonObject jsonObject) {
        try {
            Properties prop = new Properties();
            for (String en : jsonObject.keySet()) {
                if (en.equals("id") || en.equals("ID")) {
                    prop.put(en.toLowerCase(), jsonObject.get(en).getAsString());
                } else {
                    prop.put(en, jsonObject.get(en).getAsString());
                }
            }
            popisUredjajaNew.add(prop);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateDevice(Properties device, JsonObject jsonObject) {
        try {
            for (String en : jsonObject.keySet()) {
                if (en.equals("id") || en.equals("ID")) {
                    device.put(en.toLowerCase(), jsonObject.get(en).getAsString());
                } else {
                    device.put(en, jsonObject.get(en).getAsString());
                }

            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void printAllDevice(FileWriter out) throws IOException {
        int i = 0;
        // out.write(String.format(formatIspisa, "ID", "Buka", "Svjetlost", "Temperatura", "Vjetar", "Vlaga"));
        // out.write(String.format(formatIspisa, line20, line20, line20, line20, line20, line20));
        /*for (Uredjaj_A uA : popisUredjaja) {
            out.write(String.format(formatIspisa, uA.getID(), uA.getBuka(), uA.getSvje(), uA.getTemp(), uA.getVjetar(), uA.getVlaga()));
        }*/
        for (Properties properties : popisUredjajaNew) {
            System.out.println(" \n\nuređaj - " + i++);
            out.write(String.format("|%-20s|%-20s|\n", line20, line20));
            out.write(String.format("|%-20s|%-20s|\n", "ID", properties.getProperty("id")));
            out.write(String.format("|%-20s|%-20s|\n", line20, line20));

            for (Object object : properties.keySet()) {
                if (object.equals("id") || object.equals("ID")) {
                } else {
                    out.write(String.format("|%-20s|%-20s|\n", object, properties.get(object)));
                    System.out.println(object + " - " + properties.get(object));
                }

            }
        }

    }

   

}
