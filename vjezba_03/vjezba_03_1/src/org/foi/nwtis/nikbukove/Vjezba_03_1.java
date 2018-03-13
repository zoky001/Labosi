package org.foi.nwtis.nikbukove;

import org.foi.nwtis.nikbukove.kvadrati.NeparniKvadrati_5;
import org.foi.nwtis.nikbukove.kvadrati.NeparniKvadrati_3;
import org.foi.nwtis.nikbukove.kvadrati.Kvadrati;
import org.foi.nwtis.nikbukove.kvadrati.Ispisivac_1;
import org.foi.nwtis.nikbukove.kvadrati.NeparniKvadrati_2;
import org.foi.nwtis.nikbukove.kvadrati.NeparniKvadrati_1;

public class Vjezba_03_1 {

	public static void main(String args[]) {
		if(args.length != 3) {
			System.out.println("Broj argumenta ne odgovara");		
		}
		
		int odBroja = Integer.parseInt(args[0]);
		int doBroja = Integer.parseInt(args[1]);

		int vrsta = Integer.parseInt(args[2]);
		Kvadrati kvad = null;
		
		switch(vrsta) {
		case 0:
			kvad = new Kvadrati(odBroja, doBroja);
			kvad.ispis();
			break;
		case 1:
			kvad = new NeparniKvadrati_1(odBroja, doBroja);
			kvad.ispis();
			break;
		case 2:
			/*
			Ovo nije ispravno zbog razlike u odnosu klasa
			NeparniKvadrati_1 nkvad = new Kvadrati(odBroja, doBroja);
			nkvad.ispis();
			break;
			*/
		case 3:
			kvad = new NeparniKvadrati_2(odBroja, doBroja);
			kvad.ispis();
			break;
		case 4:
			kvad = new NeparniKvadrati_3(odBroja, doBroja);
			kvad.ispis();
			break;
		case 5:
			Ispisivac_1 isp = new NeparniKvadrati_3(odBroja, doBroja);
			isp.ispisiPodatke();
			break;
		case 6:
			/*NeparniKvadrati_4 nkvad = new NeparniKvadrati_4(odBroja, doBroja);
			nkvad.ispisiPodatkeLinijski();
			break;*/
		case 10:
			for(int i=0; i<10; i++){
				Ispisivac_1 isp1 = NeparniKvadrati_5.kreirajIspisivac_1(odBroja,doBroja);
				isp1.ispisiPodatke();
				System.out.println("Klasa: " + isp1.getClass().toString());
			}
			break;
		default:
			System.out.println("Argumenti ne odgovaraju");
		}
			
	}
}
		
		