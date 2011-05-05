

import com.jme.app.SimpleGame;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;

import Controller.SchwarmController;
import Controller.WorldController;




public class Main extends SimpleGame {
	
	WorldController wc;
	SchwarmController 		schwarm;
	
	@Override
	protected void simpleInitGame() {
		// Kamera Position
		cam.setLocation(new Vector3f(50,50,150));
		// Skybox erstellen
		wc = new WorldController();
		rootNode.attachChild(wc.getCubeNode());
		// Schwarm initialisieren
		schwarm = new SchwarmController();
		schwarm.addFlies(500);
		Node n = schwarm.getSwarmNode();
		rootNode.attachChild(n);	
	
		PointLight pl = new PointLight();
		pl.setEnabled(true);
		pl.setDiffuse(ColorRGBA.red);
		lightState.attach(pl);
	}
	public static void main(String[] args) {
		new Main().start();
	}
	protected void simpleUpdate(){
		//schwarm.setLeittier(pc.getPosition());
		schwarm.updateAll();
	
	}
	
}
	

