package ephemera;

import com.jme.app.SimpleGame;
import com.jme.bounding.CollisionTree;
import com.jme.bounding.CollisionTreeManager;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;

import ephemera.controller.SchwarmController;
import ephemera.controller.WorldController;




public class Main extends SimpleGame {
	
	WorldController wc;
	SchwarmController 		schwarm;
	//CollisionTreeManager ctm = CollisionTreeManager.getInstance();
	
	protected void simpleInitGame() {
		// Kamera Position
		cam.setLocation(new Vector3f(50,50,150));
		// Skybox erstellen
		wc = new WorldController();
		
		// Schwarm initialisieren
		schwarm = new SchwarmController();
		schwarm.addFlies(500);
		Node n = schwarm.getSwarmNode();
		rootNode.attachChild(n);
		wc.generateRandomObjects(10);
		rootNode.attachChild(wc.getWorldRootNode());
		//ctm.generateCollisionTree(CollisionTree.Type.Sphere, n, true);
		
		/*
		PointLight pl = new PointLight();
		pl.setLocation(schwarm.getLeittierNode().getLocalTranslation());
		pl.setEnabled(true);
		pl.setDiffuse(ColorRGBA.red);
		lightState.attach(pl);
		*/
	}
	public static void main(String[] args) {
		new Main().start();
	}
	
	protected void simpleUpdate(){
		//schwarm.setLeittier(pc.getPosition());
		schwarm.updateAll();
		
	}
	
}