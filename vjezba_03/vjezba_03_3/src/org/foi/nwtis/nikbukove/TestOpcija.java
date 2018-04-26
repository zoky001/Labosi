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
        String sintaksa1 = "^-server -konf ([^\\s]+\\.(?i)txt|xml)( +-load)?$";
        String sintaksa = "(^-server.+)|(^-admin.+)|(^-user.+)|(^-show.+)";
        String sintaksaJSON = "^\\{\\\"id\\\": ([0-9]{0,4})\\, \\\"komanda\\\"\\: \\\"(dodaj|azuriraj)\\\"\\,((( ((\\\"([A-Za-z0-9_]{1,30})\\\"\\: ((\\d{1,3},|(\\b(?!0\\d{1,2}\\.)\\d{1,3}\\.\\d{1,2}\\b\\,))|\\\"[a-zA-Z0-9_ ]*{1,30}\\\"\\,))))){1,5}) \\\"vrijeme\\\"\\: \\\"((19|20)\\d\\d).(0?[1-9]|1[012]).(0?[1-9]|[12][0-9]|3[01]) ([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])\\\"}";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = "{\"id\": 1, \"komanda\": \"dodaj\", \"naziv\": \"Senzor temperature\", \"vrijeme\": \"2018.04.08 11:20:45\"}";//sb.toString().trim();
       String o ="{\"id\": 1, \"komanda\": \"azuriraj\", \"temp\": 20.4, \"vrijeme\": \"2018.04.08 11:26:10\"}";
       String mm = "{\"id\": 2, \"komanda\": \"dodaj\", \"naziv\": \"Senzor RFID\", \"korisnik\": \"pero\", \"vrijeme\": \"2018.04.09 17:21:56\"}";
        String nn = "{\"id\": 2, \"komanda\": \"azuriraj\", \"korisnik\": \"mato\", \"vrijeme\": \"2018.04.09 21:09:01\"}";
        String rr = "{\"id\": 3, \"komanda\": \"dodaj\", \"naziv\": \"Meteo stanica\", \"temp\": 22.0, \"vlaga\": 77, \"vjetar\": \"NW\", \"vrijeme\": \"2018.04.09 18:19:41\"}";
        Pattern pattern = Pattern.compile(sintaksaJSON);
        Matcher m = pattern.matcher(rr);
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
        public static boolean testInputString(String sintaksa, String string) {
        String p = string.trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        return m.matches();
    }
}
