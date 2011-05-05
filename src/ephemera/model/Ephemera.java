/**
 * Klasse Ephemera 2011 by Semjon Mooraj
 * Diese Klasse repräsentiert eine Fliege des Systems
 */
package model;

import java.util.ArrayList;

import com.jme.math.Vector3f;

public class Ephemera extends Flugobjekt{

	private static final long serialVersionUID = 1L;

	public Ephemera(Vector3f pos){
		super(pos);
	}
	
	
	/**
	 * Diese Methode berechnet die Reaktion der Fliege auf Umwelt, Leittier und Schwarmmitglieder 
	 */
	@Override
	public void run(ArrayList<Ephemera> boids, Vector3f leittier) {
		    berechneAktuelleVektoren(boids,leittier);
		    update(); 	
	}
	
}
