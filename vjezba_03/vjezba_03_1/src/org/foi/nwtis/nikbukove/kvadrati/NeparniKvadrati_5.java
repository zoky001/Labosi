package org.foi.nwtis.nikbukove.kvadrati;

public class NeparniKvadrati_5{
	
	public static Ispisivac_1 kreirajIspisivac_1(int odBroja, int doBroja){
		long vrijeme = System.currentTimeMillis();
		int ost = (int) (vrijeme % 3);
		
		switch(ost){
			case 0:
				return new NeparniKvadrati_3(odBroja,doBroja);
			case 1:
				return new NeparniKvadrati_4(odBroja,doBroja);
			case 2:
				return new KolikoJeSati();
		}
		return null;
	}
}