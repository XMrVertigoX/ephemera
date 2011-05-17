/**
 * SchwarmController in 2011 by Semjon Mooraj
 */

package ephemera.controller;


import java.util.ArrayList;
import java.util.List;

import ephemera.model.Ephemera;
import ephemera.model.Regeln;


import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

public class SchwarmController {
	
	private Node schwarm;
	private ArrayList<Ephemera> flies;
	private PathController pathController;
	
	/**
	 * Konstruktor erstellt ArrayListe, Pathcontroller
	 */
	public SchwarmController(){
		flies = new ArrayList<Ephemera>();
		pathController = new PathController(new Regeln());
	}
	
	
	/**
	 * gibt Regeln der Fliege zurueck
	 */
	public Regeln getRegeln(){
		return flies.get(0).getRegeln();
	}
	/**
	 * Update der Position aller Fliegen aufgrund der Position des Leittieres
	 */
	public void updateAll(){
		Vector3f temp = pathController.getPosition();
		for (Ephemera e:flies){
			e.run(flies,temp);
		}
		
	}
	
	
	/**
	 * Fliege hinzufeugen
	 * @param newbie
	 */
	void addFly(Ephemera newbie){
		flies.add(newbie);
	}
	
	
	/**
	 * Fliege entfernen
	 * @param dead
	 */
	void deleteFly(Ephemera dead){
		flies.remove(dead);
	}
	
	
	/**
	 * fuegt N Fligen zum Schwarm hinzu
	 * @param N
	 */
	public void addFlies(int N){
		for (int i=0;i<N;i++){
			Ephemera fly= new Ephemera(new Vector3f((float)(Math.random()*100),
													(float)(Math.random()*100),
													(float)(Math.random()*100)));
			flies.add(fly);
		}
		initSwarmNode();
		
	}
	
	
	/**
	 * Getter
	 */
	public void initSwarmNode(){
		// SzeneKnoten f�r den Schwarm
		schwarm = new Node("theSwarm");
		
		
		// Fliegen anmelden 
		for (Ephemera e:flies){
			schwarm.attachChild((Spatial)e.getNode());
		}
		// Leittier anmelden 
		schwarm.attachChild(pathController.getLeittier());
		
		//schwarm.setModelBound(new BoundingSphere());
		//schwarm.setCullHint(Spatial.CullHint.Never);
	}
	
	public Node getSwarmNode(){ return schwarm;}
	public Node getLeittierNode(){
		return pathController.getLeittier();
	}
	public ArrayList<Ephemera> getSchwarm(){
		return flies;
	}
	
}
