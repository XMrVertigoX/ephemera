/**
 * SchwarmController in 2011 by Semjon Mooraj
 */

package ephemera.controller;


import java.util.ArrayList;
import java.util.List;

import ephemera.model.Ephemera;
import ephemera.model.Rules;
import ephemera.model.World;


import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;

public class SchwarmController {
	
	private Node schwarm;
	private ArrayList<Ephemera> flies;
	private PathController pathController;
	World world;
	/**
	 * Konstruktor erstellt ArrayListe, Pathcontroller
	 */
	public SchwarmController(){
		flies = new ArrayList<Ephemera>();
		pathController = new PathController(new Rules());
	}
	public void setWorld(World w){
		world = w;
	}
	
	/**
	 * gibt Regeln der Fliege zurueck
	 */
	public Rules getRegeln(){
		return flies.get(0).getRegeln();
	}
	
	/**
	 * Update der Position aller Fliegen aufgrund der Position des Leittieres
	 */
	public void updateAll(){
		Vector3f temp = pathController.getPosition();
		for (Ephemera e:flies){
			e.run(flies,temp,world);
			//e.kollider(schwarm);
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
		schwarm.detachChildNamed(dead.getName());
		Ephemera.count--;
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
	public void addFly(Vector3f pos){
		Ephemera fly= new Ephemera(pos);
		flies.add(fly);
		schwarm.attachChild(fly);
		
	}
	
	
	/**
	 * Getter
	 */
	public void initSwarmNode(){
		// SzeneKnoten fŸr den Schwarm
		schwarm = new Node("theSwarm");
		// Fliegen anmelden 
		for (Ephemera e:flies){
			schwarm.attachChild((Spatial)e);
		}
		// Leittier anmelden 
		//schwarm.attachChild(pathController.getLeittier());
		
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
	public PathController getPathController(){ return pathController;}
}
