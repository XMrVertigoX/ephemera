/**
 * SchwarmController in 2011 by Semjon Mooraj
 */

package ephemera.controller;

import java.util.ArrayList;

import ephemera.model.Ephemera;
import ephemera.model.RegelnFliege;


import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

public class SchwarmController {
	
	private ArrayList<Ephemera> flies;
	private PathController pathController;
	
	/**
	 * Konstruktor erstellt ArrayListe, Pathcontroller
	 */
	public SchwarmController(){
		flies = new ArrayList<Ephemera>();
		pathController = new PathController(new RegelnFliege());
	}
	
	
	/**
	 * gibt Regeln der Fliege zurueck
	 */
	public RegelnFliege getRegeln(){
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
	}
	
	
	/**
	 * Getter
	 */
	public Node getSwarmNode(){
		// SzeneKnoten fŸr den Schwarm
		Node swarm = new Node("theSwarm");
		
		// Leittier anmelden 
		swarm.attachChild(pathController.getLeittier());
		
		// Fliegen anmelden 
		for (Ephemera e:flies){
			swarm.attachChild(e.getNode());
		}
		
		swarm.setModelBound(new BoundingSphere());
		return swarm;
	}
	
	
	public Node getLeittierNode(){
		return pathController.getLeittier();
	}
	
}
