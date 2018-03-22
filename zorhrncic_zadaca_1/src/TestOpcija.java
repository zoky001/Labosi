/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author dkermek
 */
public class TestOpcija {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // -server -konf datoteka(.txt | .xml) [-load]
        String sintaksa11 = "^-k -konf ([^\\s]+\\.(?i)(jpg|png|gif|bmp))$";
        String sintaksa1 = "(^-server.+)|(^-admin.+)|(^-user.+)|(^-show.+)";

        String sintaksa2 = "([^\\s]+\\.(?i)txt|xml)$";

        //terminal commanda admina
        //username radi
        //lozink radi
        // adresu treba posebno
        //datoteku posebno putanju
        //ostalo radi  ([1-9]|[1-9][0-9]|[1-9][0-9][0-9])
        String sintaksaAdmin = "^-k ([^[a-zA-Z0-9_-]]{3,10}) -l ([^[a-zA-Z0-9[#!]_-]]{3,10}) -s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) (--pauza|--kreni|--zaustavi|--stanje|--evidencija ([^\\s]+\\.(?i)(txt|xml|json|bin))|--iot ([^\\s]+\\.(?i)(txt|xml|json|bin)))";
        //sintaksa client -s [ipadresa | adresa] -p port [--spavanje nnn] datoteka 
        // klijentu provjeriti ogovara li koja od voe dvije
        String sintaksa = "^-s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) (?:--spavanje (0?[1-9]|[1-9][0-9]|[1-5][0-9][0-9]|[6-6][0-0][0-0]))? ([^\\s]+\\.(?i)(txt|xml|json|bin))";// (--spavanje ([^\\s]+) | --ne )";// ([^\\s]+\\.(?i)txt|xml|json)";
        String sintaksaBezSpavanja = "^-s ([^\\s]+) -p ([8-9][0-9][0-9][0-9]) ([^\\s]+\\.(?i)(txt|xml|json|bin))";// (--spavanje ([^\\s]+) | --ne )";// ([^\\s]+\\.(?i)txt|xml|json)";

        //server pokretanej, svee
        String sintaksaServer = "([^\\s]+\\.(?i)(txt|xml|json|bin))";

        //comanda 1. Admin
        //KORISNIK korisnik; LOZINKA lozinka; PAUZA;
        String sintaksaPauza = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); PAUZA;";
        //KORISNIK korisnik; LOZINKA lozinka; KRENI;
        String sintaksaKreni = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); KRENI;";
        //KORISNIK korisnik; LOZINKA lozinka; ZAUSTAVI;
        String sintaksaZaustavi = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); ZAUSTAVI;";
        //KORISNIK korisnik; LOZINKA lozinka; STANJE;
        String sintaksaStanje = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); STANJE;";

        //KORISNIK korisnik; LOZINKA lozinka; EVIDENCIJA;
        String sintaksaEvidencija = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); EVIDENCIJA;";
        //KORISNIK korisnik; LOZINKA lozinka; IOT;
        String sintaksaIot = "^KORISNIK ([^[a-zA-Z0-9_-]]{3,10}); LOZINKA ([^[a-zA-Z0-9[#!]_-]]{3,10}); IOT;";

        //IOT sadržajDatoteke; 
        String sintaksaSadrzajDatoteke = "IOT (.*);";

        String sintaksaIP = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5]))$";
        String sintaksaIP_URL = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5]))|((?:http(?:s)?\\:\\/\\/)?[a-zA-Z0-9_-]+(?:.[a-zA-Z0-9_-]+)*.[a-zA-Z]{2,4}(?:\\/[a-zA-Z0-9_]+)*(?:\\/[a-zA-Z0-9_]+.[a-zA-Z]{2,4}(?:\\?[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)?)?(?:\\&[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)*)$";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();

        // p="-k korisnik -l lozinka -s knjkjk -p port";// [--pauza | --kreni | --zaustavi | --stanje | --evidencija datoteka1 | --iot datoteka2]";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (status) {
            int poc = 0;

            int kraj = m.groupCount();
            for (int i = poc; i <= kraj; i++) {
                System.out.println(i + ". " + m.group(i));
            }
        } else {
            System.out.println("Ne odgovara!");
        }

    }

    public boolean testInputArgs(String sintaksa, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();

        // p="-k korisnik -l lozinka -s knjkjk -p port";// [--pauza | --kreni | --zaustavi | --stanje | --evidencija datoteka1 | --iot datoteka2]";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (status) {
            int poc = 0;

            int kraj = m.groupCount();
            for (int i = poc; i <= kraj; i++) {
                System.out.println(i + ". " + m.group(i));
            }
            return true;
        } else {
            System.out.println("Ne odgovara!");
            return false;
        }

    }
}
