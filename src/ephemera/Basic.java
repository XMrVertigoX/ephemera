package ephemera;

import com.jme.app.SimpleGame;

import ephemera.controller.SwarmController;
import ephemera.model.World;

/**
 * In dieser Klasse wird die Simulation ohne GUI und deren Funtionen gestartet.
 * @author Caspar Friedrich
 *
 */
public class Basic extends SimpleGame {
	
	World world;
	SwarmController schwarm;
	
	protected void simpleInitGame() {
		
		/**
		 * Stellt die Rendertiefe der jME ein.
		 */
		cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 1f, 20000);
		
		// Welt initialisieren
		world = new World();
		
		// Schwarm initialisieren
		schwarm = new SwarmController();
		schwarm.addFlies(50);
		schwarm.setWorld(world);
		
		// Node-Struktur erstellen
		rootNode.attachChild(world);
		rootNode.attachChild(schwarm.getLeaderNode());
		rootNode.attachChild(schwarm.getSwarmNode());
	}
	
	protected void simpleUpdate(){
		schwarm.updateAll();
		world.update();
		world.render();
	}
	
	public static void main(String[] args) {
		new Basic().start();
	}
}
