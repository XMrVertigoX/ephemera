package ephemera.swarm;

import java.util.ArrayList;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;


public class Main extends SimpleGame {

	Swarm schwarm = new Swarm();
	@Override
	protected void simpleInitGame() {
		schwarm.addFlies(400);
		Node n = schwarm.getSwarmNode();
		rootNode.attachChild(n);
	}
	public static void main(String[] args) {
		new Main().start();
	}
}
