package org.foi.nwtis.nikbukove.kvadrati;

public class NeparniKvadrati_4 extends NeparniKvadrati_2 implements Ispisivac_2 {
	
	public NeparniKvadrati_4(int odBroja, int doBroja) {
		super(odBroja,doBroja);
	}
	
	public void ispisiPodatkeLinijski(){
		int prviNeparni = (this.odBroja % 2) == 0 ? this.odBroja + 1 : this.odBroja;
		for(int i=prviNeparni;i <= this.doBroja;i += 4) {
//			System.out.println(i + " * " + i + " = " + i*i);
			System.out.printf("%3d * %3d = %3d; ", i, i, i*i);
		}
	}
}