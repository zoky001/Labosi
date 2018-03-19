package org.foi.nwtis.zorhrncic.konfiguracije;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KonfiguracijaJSON extends KonfiguracijaApstraktna {
    
    public KonfiguracijaJSON(String datoteka) {
        super(datoteka);
    }
    
    @Override
    public void ucitajKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        ucitajKonfiguraciju(this.datoteka);
    }
    
    @Override
    public void ucitajKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NemaKonfiguracije("naziv datoteke nedostaje");
        }
        
        File datKonf = new File(datoteka);
        
        if (!datKonf.exists()) {
            throw new NemaKonfiguracije("Datoteka: " + datoteka + " ne postoji!");            
        } else if (datKonf.isDirectory()) {
            throw new NeispravnaKonfiguracija(datoteka + " nije datoteka već direktorij");
        }

        //TO DO dovrsiti
        
Gson gson = new Gson();

        try {
            // 1. JSON to Java object, read it from a file.
            this.postavke = gson.fromJson(new FileReader(datKonf), Properties.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KonfiguracijaJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void spremiKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        spremiKonfiguraciju(this.datoteka);
    }
    
    @Override
    public void spremiKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NemaKonfiguracije("Naziv datoteke nedostaje");
        }
        
        File datKonf = new File(datoteka);
        
        if (datKonf.exists() && datKonf.isDirectory()) {
            throw new NeispravnaKonfiguracija(datoteka + " nije datoteka već direktorij");
        }
        try {
            OutputStream os = Files.newOutputStream(datKonf.toPath(), StandardOpenOption.CREATE);
            Gson gsonObj = new Gson();
            String strJson = gsonObj.toJson(this.postavke);
            os.write(strJson.getBytes());
            //this.postavke.storeToXML(os, "Konfiguracija NWTIS grupa 2");
        } catch (IOException ex) {
            throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke " + datKonf.getAbsolutePath());
        }
        
    }
    
}
