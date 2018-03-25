/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.zorhrncic.zadaca_1;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.zorhrncic.konfiguracije.Konfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.zorhrncic.konfiguracije.KonfiguracijaBin;
import org.foi.nwtis.zorhrncic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.zorhrncic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author grupa_1
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
    private Gson g = new Gson();
    private List<RadnaDretva> dretveCekaj = new ArrayList<>();
    private IOT iot;
    private SerijalizatorEvidencije se;

    public synchronized void addDretvaCekaj(RadnaDretva b) {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
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

    public synchronized void removeDretvaCekaj(RadnaDretva b) {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
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

    public synchronized boolean setKrajRada(boolean b) {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
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

    public synchronized boolean isKrajRada() {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
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

    public synchronized boolean beginStoppingServer() {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
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

    public synchronized boolean isStopRequest()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        boolean ret = stop_request;
        upis = false;
        notify();
        return ret;

    }

    public synchronized boolean isPause()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
        }
        upis = true;
        boolean ret = pause_state;
        upis = false;
        notify();
        return ret;

    }

    public synchronized boolean setServerPause()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
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

    public synchronized boolean setServerStart()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
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

    public static void main(String[] args) {
        String datotekaKonfig;
        if (args.length != 1) {
            System.out.println("Premalo ili previše argumenata");
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

    private void pokreniPosluzitelj(Konfiguracija konfig) {
        port = Integer.parseInt(konfig.dajPostavku("port"));
        maksCekanje = Integer.parseInt(konfig.dajPostavku("maks.broj.zahtjeva.cekanje"));
        datotekaEvidencije = konfig.dajPostavku("datoteka.evidencije.rada");
        maksRadnihDretvi = Integer.parseInt(konfig.dajPostavku("maks.broj.radnih.dretvi"));
        brojRadnihDretvi = 0;
        redniBrojDrete = 0;
        postaviEvidencijuRada(datotekaEvidencije);
        setKrajRada(false);
        System.out.println(g.toJson(evidencija));
        iot = new IOT();
        se = new SerijalizatorEvidencije("zorhrncic - serijalizator", konfig, evidencija);
        se.start();
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
        } catch (IOException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void vratiOdgovorDaNemaSlobodnihRadnihDretvi(Socket socket) {
        try (
                InputStream inputStream = socket.getInputStream();
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
            System.out.println("Klijent je napisao KOMDANDU: " + buffer.toString());
            outputStream.write("ERROR 01; nema više slobodnih radnih dretvi".getBytes());
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }

        /*try (
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();) {         
            int znak;
            StringBuffer buffer = new StringBuffer();
            while (true) {
                znak = inputStream.read();
                if (znak == -1) {
                    break;
                }
                buffer.append((char) znak);
            }
           outputStream.write("ERROR 01; nema više slobodnih radnih dretvi".getBytes());
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
           
        }*/
    }

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

    private synchronized void povecajBrojRadnihDretvi()
            throws InterruptedException {
        while (upis) {
            System.out.println("Netko upisuje");
            wait();
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
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized void smanjiBrojRadnihDretvi() {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
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
        evidencija.dodajUspjesnoObavljenZahtjev();        
        upis = false;
        System.out.println("Posao obavljen");
        notify();
    }

    public synchronized int getBrojRadnihDretvi() {
        while (upis) {
            try {
                System.out.println("Netko upisuje");
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

    public boolean zaustaviServer(Konfiguracija konfig) {
        try {
            beginStoppingServer();
            for (RadnaDretva radnaDretva : dretveCekaj) {
                radnaDretva.setKrajRada(true);
            }

            while (getBrojRadnihDretvi() > 1) {
                System.out.println("Cekma da zavrse sve dretve;");
            }

            evidencija.obaviSerijalizaciju(konfig.dajPostavku("datoteka.evidencije.rada"));
            se.setKrajRada(true);
            setKrajRada(true);
         ///   System.exit(0);
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
