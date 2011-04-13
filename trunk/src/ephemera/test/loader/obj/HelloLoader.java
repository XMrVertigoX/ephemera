package ephemera.test.loader.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;


import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.util.export.binary.BinaryImporter;

public class HelloLoader extends SimpleGame {

	
	public static void main(String[] args) {
		new HelloLoader().start();
	}
	
	@Override
	protected void simpleInitGame() {
		Node n = LoadJmeFile("Fliege","jmetest/data/model/thefly2.jme");
		rootNode.attachChild(n);
		
	}
	
	
	
	
	public static Node LoadJmeFile(String s,String s1){
		Node node = new Node(s);
		URL url = HelloLoader.class.getClassLoader().getResource(s1);
		try{
			Node node1 = (Node)BinaryImporter.getInstance().load(url);
			node1.setLocalScale(.1f);
			node1.setLocalRotation((new Quaternion()).fromAngleAxis(0, new Vector3f(1f,0f,0f)));
			node1.setModelBound(new BoundingBox());
			node1.updateModelBound();
			node.attachChild(node1);
			
		}
		catch(IOException e){
			e.printStackTrace();
			System.exit(0);
		}
		return node;
		
	} 

}
