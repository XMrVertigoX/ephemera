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

public class SwarmController {
	
	private Node swarm;
	private ArrayList<Ephemera> flies;
	private PathController pathController;
	private World world;
	private Rules rules;
	
	/**
	 * Konstruktor erstellt ArrayListe, Pathcontroller
	 */
	public SwarmController(){
		rules = new Rules();
		flies = new ArrayList<Ephemera>();
		pathController = new PathController(rules);
	}
	
	public void setWorld(World w){
		world = w;
	}
	
	/**
	 * gibt Regeln der Fliege zurueck
	 */
	public Rules getRules(){
		return rules;
	}
	
	/**
	 * Update der Position aller Fliegen aufgrund der Position des Leittieres
	 */
	public void updateAll(){
		Vector3f temp = pathController.getLeader().getPos();
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
		swarm.detachChildNamed(dead.getName());
		Ephemera.count--;
	}
	
	/**
	 * fuegt N Fligen zum Schwarm hinzu
	 * @param N
	 */
	public void addFlies(int N){
		for (int i=0;i<N;i++){
			Ephemera fly= new Ephemera(new Vector3f((float) (Math.random()*100), (float) (Math.random()*100), (float) (Math.random()*100)), rules);
			flies.add(fly);
		}
		
		initSwarmNode();	
	}
	
	public void addFly(Vector3f pos){
		Ephemera fly= new Ephemera(pos);
		flies.add(fly);
		swarm.attachChild(fly);
	}
	
	public void addFly(Vector3f pos, Rules rules){
		Ephemera fly= new Ephemera(pos,rules);
		flies.add(fly);
		swarm.attachChild(fly);
	}
	
	
	/**
	 * Getter
	 */
	public void initSwarmNode(){
		// SzeneKnoten fŸr den Schwarm
		swarm = new Node("theSwarm");
		// Fliegen anmelden 
		for (Ephemera e:flies){
			swarm.attachChild((Spatial)e);
		}
		// Leittier anmelden 
		//schwarm.attachChild(pathController.getLeittier());
		
		//schwarm.setModelBound(new BoundingSphere());
		//schwarm.setCullHint(Spatial.CullHint.Never);
	}
	
	public Node getSwarmNode(){ return swarm;}
	public Node getLeaderNode(){
		return pathController.getLeaderNode();
	}
	public ArrayList<Ephemera> getSwarm(){
		return flies;
	}
	public PathController getPathController(){ return pathController;}
}
