package ephemera.controller;

import java.util.ArrayList;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import ephemera.model.Ephemera;
import ephemera.model.Rules;
import ephemera.model.World;

/**
 * Initialisiert den Schwarm und erstellt die Fliegen.
 * @author Semjon Mooraj
 *
 */
public class SwarmController {
	
	private Node swarm;
	private ArrayList<Ephemera> flies;
	private PathController pathController;
	private World world;
	private Rules rules;
	
	/**
	 * Konstruktor erstellt ArrayListe, Pathcontroller
	 * @param world Die World, innerhalb derer sich der Schwarm bewegt.
	 */
	public SwarmController(World world){
		this.world = world;
		
		rules = new Rules();
		flies = new ArrayList<Ephemera>();
		pathController = new PathController(rules);
		
		addFlies(50);
	}
	
	/**
	 * @return Regelobjekt der Fliegen
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
		}
	}
	
	/**
	 * Fuegt dem Schwarm eine Konkrete Fliege hinzu.
	 * @param Ephemera-Objekt
	 */
	void addFly(Ephemera newbie){
		flies.add(newbie);
	}
	
	/**
	 * Entfernt eine konkrete Fliege aus dem Schwarm.
	 * @param Ephemera-Objekt
	 */
	public void deleteFly(Ephemera dead){
		flies.remove(dead);
		swarm.detachChildNamed(dead.getName());
		Ephemera.count--;
	}
	
	/**
	 * Fuegt N Fligen zum Schwarm hinzu
	 * @param N Anzahl der Fliegen
	 */
	public void addFlies(int N){
		for (int i=0;i<N;i++){
			Ephemera fly= new Ephemera(rules);
			flies.add(fly);
		}
		
		initSwarmNode();	
	}
	
	/**
	 * Erstellt im Schwarm eine neue Fliege hinzu.
	 */
	public void addFly(){
		Ephemera fly= new Ephemera(rules);
		flies.add(fly);
		swarm.attachChild(fly);
	}
	
	/**
	 * Initialisiert den Schwarm.
	 */
	public void initSwarmNode(){
		
		// SzeneKnoten fŸr den Schwarm
		swarm = new Node("theSwarm");
		
		// Fliegen anmelden 
		for (Ephemera e:flies){
			swarm.attachChild((Spatial)e);
		}
	}
	
	/**
	 * @return Node des Schwarms
	 */
	public Node getSwarmNode(){
		return swarm;
		}
	
	/**
	 * @return Node des Leittieres, dem der Schwarm folgt.
	 */
	public Node getLeaderNode(){
		return pathController.getLeaderNode();
	}
	
	/**
	 * @return Array-List mit den einzelnen Fliegen.
	 */
	public ArrayList<Ephemera> getSwarm(){
		return flies;
	}
	
	/**
	 * @return Path Controller des Schwarms.
	 */
	public PathController getPathController(){
		return pathController;
		}
}
