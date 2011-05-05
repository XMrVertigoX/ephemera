import com.jme.app.SimpleGame;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;


public class lightTest extends SimpleGame{

	public static void main(String[] args) {
		new lightTest().start();
	}
	@Override
	protected void simpleInitGame() {
		// TODO Auto-generated method stub
		Node n = new Node("dasd");
		
		Box b = new Box("Box",new Vector3f(0,0,0),new Vector3f(1,1,1));
		n.attachChild(b);
		lightState.detachAll();
		DirectionalLight dl = new DirectionalLight();
		dl.setEnabled(true);
		dl.setDirection(new Vector3f(1,0,0));
		lightState.attach(dl);
		
		
		TriMesh trimesh = new TriMesh("");
		
		rootNode.attachChild(n);
	}

}
