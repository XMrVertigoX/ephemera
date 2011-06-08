package ephemera;

import com.jme.app.SimpleGame;

import ephemera.controller.SchwarmController;
import ephemera.model.World;

public class Basic extends SimpleGame {
	
	World world;
	SchwarmController schwarm;
	
	protected void simpleInitGame() {
		
		cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 1f, 20000);
		
		//Welt initialisieren
		world = new World();
		
		// Schwarm initialisieren
		schwarm = new SchwarmController();
		schwarm.addFlies(50);
		schwarm.setWorld(world);
		rootNode.attachChild(world);
		rootNode.attachChild(schwarm.getLeittierNode());
		rootNode.attachChild(schwarm.getSwarmNode());
	}
	
	public static void main(String[] args) {
		new Basic().start();
	}
	
	protected void simpleUpdate(){
		schwarm.updateAll();
	}
}
