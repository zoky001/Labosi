
package org.foi.nwtis.nikbukove.konfiguracije;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class KonfiguracijaXML extends KonfiguracijaApstraktna {

    public KonfiguracijaXML(String datoteka) {
        super(datoteka);
    }

    @Override
    public void ucitajKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        ucitajKonfiguraciju(this.datoteka);
    }

    @Override
    public void ucitajKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        if(datoteka == null || datoteka.length() == 0){
            throw new NemaKonfiguracije("naziv datoteke nedostaje");
        }
        
        File datKonf = new File(datoteka);
        
        if(!datKonf.exists()){
            throw new NemaKonfiguracije("Datoteka: "+datoteka+" ne postoji!");    
           }
        else if(datKonf.isDirectory()){
            throw new NeispravnaKonfiguracija(datoteka+ " nije datoteka već direktorij");
        }
        
        try{
            InputStream is = Files.newInputStream(datKonf.toPath(), StandardOpenOption.READ);      
            this.postavke.loadFromXML(is);
        }catch (IOException ex){
            throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke "+datKonf.getAbsolutePath());
        }
    }

    @Override
    public void spremiKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        spremiKonfiguraciju(this.datoteka);
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
         if(datoteka == null || datoteka.length() == 0){
            throw new NemaKonfiguracije("Naziv datoteke nedostaje");
        }
        
        File datKonf = new File(datoteka);
        
        if(datKonf.exists() && datKonf.isDirectory()){
           throw new NeispravnaKonfiguracija(datoteka+ " nije datoteka već direktorij");
        }
            try{
                OutputStream os = Files.newOutputStream(datKonf.toPath(), StandardOpenOption.CREATE);      
                this.postavke.storeToXML(os, "Konfiguracija NWTIS grupa 2");
            }catch (IOException ex){
                throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke "+datKonf.getAbsolutePath());
            }
            
        
    }
    
}
