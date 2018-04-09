/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaBin;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

/**
 * Klasa koja je glavna za rad servera.
 *
 * @author Zoran Hrncic
 */
public class ServerSustava {

    private boolean pause_state = false;
    private boolean stop_request = false;
    private Evidencija evidencija;
    private int port;
    private int maksCekanje;
    private String datotekaEvidencije;
    private int maksRadnihDretvi;
    private int brojRadnihDretvi;
    private int redniBrojDrete;
    private boolean krajRada;
    private boolean upis = false;
    private final Gson g = new Gson();
    private final List<RadnaDretva> dretveCekaj = new ArrayList<>();
    private IOT iot;
    private SerijalizatorEvidencije se;

    public List<RadnaDretva> getDretveCekaj() {
        return dretveCekaj;
    }

    /**
     * Po prisilnim gasenjem servera (Ctrl + c) obavlja serijalizaciju prije
     * gasenja.
     *
     * @param k objekt evidencije servera
     * @param konfig objekt konfiguracije servera
     */
    private void onShutDown(Evidencija k, Konfiguracija konfig) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    k.obaviSerijalizaciju(konfig.dajPostavku("datoteka.evidencije.rada"));
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Dodaje dretve koje izvrasavaju operaciju cekanja u listu.
     *
     * @param b dretvA koja spava
     */
    public synchronized void addDretvaCekaj(RadnaDretva b) {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        dretveCekaj.add(b);
        System.out.println("broj dretvi cekanja: " + dretveCekaj.size());
        upis = false;
        notify();
    }

    /**
     * Brise dretvu iz liste dretvi koje spavaju.
     *
     * @param b dretva koja je zavrsila spavanje
     */
    public synchronized void removeDretvaCekaj(RadnaDretva b) {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        dretveCekaj.remove(b);
        System.out.println("broj dretvi cekanja: " + dretveCekaj.size());
        upis = false;
        notify();
    }

    /**
     * postavlja zastavicu za rad servera na false - kraj rada
     *
     * @param b false - kraj rada
     * @return rezultat uspjeha
     */
    public synchronized boolean setKrajRada(boolean b) {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        if (krajRada != b) {
            krajRada = b;
            upis = false;
            notify();
            return true;
        } else {
            upis = false;
            notify();
            return false;
        }
    }

    /**
     * Ispituje trenutno stanje zastavice za rad servera
     *
     * @return rezultat usojeha
     */
    public synchronized boolean isKrajRada() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        boolean ret = krajRada;
        upis = false;
        notify();
        return ret;
    }

    /**
     * POstavlja zastavicu koja oznacava pocetak zaustavljanja servera ua true
     *
     * @return rezultat uspjeha
     */
    public synchronized boolean beginStoppingServer() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        if (stop_request == false) {
            stop_request = true;
            upis = false;
            notify();
            return true;
        } else {
            upis = false;
            notify();
            return false;
        }

    }

    /**
     * Ispituje je li vec pokrenut zahtjev za zaustavljanjem servera
     *
     * @return rezultat uspjeha
     */
    public synchronized boolean isStopRequest() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        boolean ret = stop_request;
        upis = false;
        notify();
        return ret;

    }

    /**
     * Ispituje je li server u stanju pauze.
     *
     * @return rezultat uspjeha
     */
    public synchronized boolean isPause() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        boolean ret = pause_state;
        upis = false;
        notify();
        return ret;

    }

    /**
     * Postavlja server u stanje pauze.
     *
     * Server vise ne prihvaca korisnikove zahtijeve
     *
     * @return rezultat uspjeha
     */
    public synchronized boolean setServerPause() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        if (pause_state == true) {
            upis = false;
            notify();
            return false;
        } else {
            pause_state = true;
            upis = false;
            notify();
            return true;
        }

    }

    /**
     * Pokrece server u rad nakon stanja pauze
     *
     * @return rezultat uspjeha
     */
    public synchronized boolean setServerStart() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        if (pause_state == true) {
            pause_state = false;
            upis = false;
            notify();
            return true;
        } else {
            upis = false;
            notify();
            return false;
        }

    }

    /**
     * Glavna main klasa koja pokrece server. Prima parametre za pokretanje
     * servera
     *
     * @param args datoteka konfiguracije.
     */
    public static void main(String[] args) {
        String datotekaKonfig;
        if (args.length != 1) {
            System.out.println("Premalo ili previše argumenata\n");
            return;
        }
        datotekaKonfig = args[0];
        try {
            Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datotekaKonfig);
            ServerSustava ss = new ServerSustava();
            ss.pokreniPosluzitelj(konfig);
        } catch (NemaKonfiguracije ex) {
            System.out.println("Ne postoji datoteka konfiguracije!!");
            return;
        } catch (NeispravnaKonfiguracija ex) {
            System.out.println("Greška u datoteci konfiguracije!!");
            return;
        }
    }

    /**
     * Metoda servera koja se prva pokrece. Ucitava sve potrebne postavke da bi
     * server mogau uspjesno krenuti s radom.
     *
     * @param konfig konfiguracija postavki servera
     */
    public void pokreniPosluzitelj(Konfiguracija konfig) {
        port = Integer.parseInt(konfig.dajPostavku("port"));
        maksCekanje = Integer.parseInt(konfig.dajPostavku("maks.broj.zahtjeva.cekanje"));
        datotekaEvidencije = konfig.dajPostavku("datoteka.evidencije.rada");
        maksRadnihDretvi = Integer.parseInt(konfig.dajPostavku("maks.broj.radnih.dretvi"));
        brojRadnihDretvi = 0;
        redniBrojDrete = 0;
        postaviEvidencijuRada(datotekaEvidencije);
        onShutDown(evidencija, konfig);
        setKrajRada(false);
        System.out.println("Evidencija: \n" + g.toJson(evidencija));
        iot = new IOT();
        se = new SerijalizatorEvidencije("zorhrncic - serijalizator", konfig, evidencija);
        se.start();
        handleRequest(konfig);
    }

    /**
     * Vraća korisniku odgovor da trenutno nema slobodnih radnih dretvi.
     *
     * @param socket socket na koji se salje odgovor
     */
    private void vratiOdgovorDaNemaSlobodnihRadnihDretvi(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();) {
            int znak;
            StringBuffer buffer = new StringBuffer();
            String inputLine, outputLine;
            while (true) {
                znak = inputStream.read();
                if (znak == -1) {
                    break;
                }
                buffer.append((char) znak);
            }
            outputStream.write("ERROR 01; nema više slobodnih radnih dretvi".getBytes(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
    }

    /**
     * Ucitava datoteku evidencije ako postoji i vrsi njenu deserijalizaciju u
     * objekt evidencije.
     *
     * @param datoteka datoteka konfiguracije
     * @throws NemaKonfiguracije datoteka ne postojji
     * @throws NeispravnaKonfiguracija datoteka je neispravna
     */
    public void ucitajEvidenciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NemaKonfiguracije("naziv datoteke nedostaje");
        }
        File datKonf = new File(datoteka);
        if (!datKonf.exists()) {
            throw new NemaKonfiguracije("Datoteka: " + datoteka + " ne postoji!");
        } else if (datKonf.isDirectory()) {
            throw new NeispravnaKonfiguracija(datoteka + " nije datoteka već direktorij");
        }
        try {
            InputStream is = Files.newInputStream(datKonf.toPath(), StandardOpenOption.READ);
            ObjectInputStream ois = new ObjectInputStream(is);
            evidencija = (Evidencija) ois.readObject();
            ois.close();
        } catch (IOException ex) {
            throw new NeispravnaKonfiguracija("Problem kod učitavanja datoteke " + datKonf.getAbsolutePath());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KonfiguracijaBin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Evidencija getEvidencija() {
        return evidencija;
    }

    /**
     * Ucitava evidenciju iz datoteke u objekt.
     *
     * @param datotekaEvidencije datoteka evidencije
     */
    private void postaviEvidencijuRada(String datotekaEvidencije) {
        try {
            FileInputStream in = new FileInputStream(datotekaEvidencije);
            ObjectInputStream s = new ObjectInputStream(in);
            evidencija = (Evidencija) s.readObject();
            s.close();
        } catch (Exception e) {
            System.out.println("Problem kod učitavanja podataka evidencije: " + e.getMessage());
        } finally {
            if (evidencija == null) {
                evidencija = new Evidencija();
                System.out.println("Problem kod učitavanja podataka evidencije: ");
            }
        }
    }

    /**
     * Povećava brojac radnih dretvi za 1
     */
    private synchronized void povecajBrojRadnihDretvi() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        if (this.redniBrojDrete >= 63) {
            redniBrojDrete = 0;
        } else {
            redniBrojDrete++;
        }
        this.brojRadnihDretvi++;
        System.out.println("Povećan broj radnih dretvi: " + brojRadnihDretvi);
        upis = false;
        notify();
    }

    /**
     * Smanjuje broj radnih dretvi za 1
     */
    public synchronized void smanjiBrojRadnihDretvi() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        this.brojRadnihDretvi--;
        System.out.println("Smanjen broj radnih dretvi: " + brojRadnihDretvi);
        if (brojRadnihDretvi == 0 && krajRada) {
            System.exit(0);
        }
        upis = false;
        notify();
    }

    /**
     * Dohvaca trenutni broj radnih dretvi
     *
     * @return broj radnih dretvi
     */
    public synchronized int getBrojRadnihDretvi() {
        while (upis) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        upis = true;
        int b = brojRadnihDretvi;
        System.out.println("Smanjen broj radnih dretvi: " + brojRadnihDretvi);
        upis = false;
        notify();
        return b;
    }

    /**
     * Zaustavlja server i gasi istog.
     *
     * 1. postavlja zastavicu za gasenje servera na true 2. prekida sve dretve
     * koje spavaju. 3. Ceka da zavrse sve dretve osim ove trenutne.
     *
     * @param konfig
     * @return rezultat uspjeha
     */
    public boolean zaustaviServer(Konfiguracija konfig) {
        try {
            beginStoppingServer();
            for (RadnaDretva radnaDretva : dretveCekaj) {

                radnaDretva.setKrajRada(true);
                //evidencija.dodajOdbijenZahtjevJerNemaDretvi();
                radnaDretva.interrupt();
            }
            while (getBrojRadnihDretvi() > 1) {
                System.out.println("Cekma da zavrse sve dretve;");
            }
            evidencija.obaviSerijalizaciju(konfig.dajPostavku("datoteka.evidencije.rada"));

            se.setKrajRada(true);
            se.interrupt();
            setKrajRada(true);
            if (getBrojRadnihDretvi() == 0) {
                System.exit(0);
            }
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void setEvidencija(Evidencija evidencija) {
        this.evidencija = evidencija;
    }

    /**
     * Pokrece server na odredjenom socketu i ceka spajanje korisnika. Nakon
     * uspjesnog spajanja korinika, dodjeljuje m u radnu drevu.
     *
     * @param konfig konfiguracija posluzitelja
     */
    private void handleRequest(Konfiguracija konfig) {
        try {
            ServerSocket serverSocket = new ServerSocket(port, maksCekanje);
            while (!isKrajRada()) {
                System.out.println("Cekam");
                Socket socket = serverSocket.accept();
                if (isKrajRada()) {
                    return;
                }
                System.out.println("Korisnik se spojio");
                evidencija.dodajNoviZahtjev();//test monitor
                if (brojRadnihDretvi >= maksRadnihDretvi) {
                    vratiOdgovorDaNemaSlobodnihRadnihDretvi(socket);
                    evidencija.dodajOdbijenZahtjevJerNemaDretvi();
                } else {
                    povecajBrojRadnihDretvi();
                    RadnaDretva radnaDretva = new RadnaDretva(socket, "zorhrncic - " + Integer.toBinaryString(redniBrojDrete), konfig, evidencija, this, iot);
                    radnaDretva.start();
                }
            }
        } catch (java.net.BindException e) {
            System.out.println("\nVec je pokrenut server na ovom portu!! \nPromjenite broj porta u konfiguraciji");
            System.exit(0);
        } catch (IOException ex) {
            System.exit(0);
        }
    }

}
