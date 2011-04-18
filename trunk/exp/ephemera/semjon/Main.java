package ephemera.semjon;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.Node;


public class Main extends SimpleGame {

	Schwarm schwarm = new Schwarm();
	@Override
	protected void simpleInitGame() {
		this.cam.setLocation(new Vector3f(50,50,150));
		schwarm.addFlies(400);
		Node n = schwarm.getSwarmNode();
		rootNode.attachChild(n);
	}
	public static void main(String[] args) {
		new Main().start();
	}
	protected void simpleUpdate(){
		schwarm.updateAll();
	
	}
}
