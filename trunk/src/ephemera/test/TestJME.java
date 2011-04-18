package ephemera.test;
// Testkommentar von Stefan
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;public class TestJME extends SimpleGame {

	public static void main(String[] args) {
		TestJME app = new TestJME();
		app.setConfigShowMode(ConfigShowMode.ShowIfNoConfig); // Signal to show properties dialog
		app.start(); // Start the program
	}

	protected void simpleInitGame() {
		createRubiksCube(3, 0.85f);	//call the rubik's method
	}
	
	private void createRubiksCube(int number, float size) {
		Node rubiksNode = new Node("Rubik");	// Create a node to attach all the rubik's geometry
		for (float x = 1; x <= number; x++) {
			for (float y = 1; y <= number; y++) {
				for (float z = 1; z <= number; z++) {
					Box b = new Box("Box: " + x + ", " + y + ", " + z, 
									new Vector3f(0, 0, 0), 
									new Vector3f(size, size, size)); // Make a box
					b.setLocalTranslation(x, y, z);	// place each box on the location x, y, z
					b.setModelBound(new BoundingBox());	// set a bounding box around each box
					rubiksNode.attachChild(b); // Put it in the scene graph
				}
			}
		}
		
		rubiksNode.setModelBound(new BoundingBox());	// set a bounding box for rubik's cube
		rootNode.attachChild(rubiksNode);	// attach rubik's cube to rootNode
	}
}