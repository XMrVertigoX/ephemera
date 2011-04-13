package ephemera.swarm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jmex.model.collada.ColladaImporter;

public class Swarm {
	private int count;
	private ArrayList<Ephemera> flies;
	private Node swarm;
	
	public Swarm(){
		// erstelle Schwarm
		count = 0;
		swarm = new Node("theSwarm");
		flies = new ArrayList<Ephemera>();
	}
	
	
	ArrayList<Ephemera> getSwarm(){
		return flies;
	}
	public Node getSwarmNode(){
		return swarm;
	}
	void addFly(Ephemera newbie){
		flies.add(newbie);
		Node n1= new Node("Fliege_"+count);
		Box b = new Box(""+count,new Vector3f(0,0,0),new Vector3f(1,1,1));
	
		n1.attachChild(b);
		n1.setLocalTranslation(newbie.getPos());
		swarm.attachChild(n1);
		count++;
		
	}
	void addFlies(int N){
		for (int i=0;i<N;i++){
			Ephemera f= new Ephemera(new Vector3f(	(float)(Math.random()*100),
													(float)(Math.random()*100),
													(float)(Math.random()*100)));
			flies.add(f);
			Node n1= new Node("Fliege_"+count);
			Box b = new Box(""+count,new Vector3f(0,0,0),new Vector3f(1,1,1));
			n1.attachChild(b);
			n1.setLocalTranslation(f.getPos());
			swarm.attachChild(n1);
			count++;
		}
		
	}
	
	
	
	
	/* NO NEED FOR THIS
	public Node createSwarm(ArrayList<Ephemera> e){
		Node n = new Node("Schwarm");
		int count=0;
		for (Ephemera f:e){
			Node n1= new Node("Fliege_"+count);
			Box b = new Box(""+count,new Vector3f(0,0,0),new Vector3f(1,1,1));
			n1.attachChild(b);
			n1.setLocalTranslation(f.getPos());
			n.attachChild(n1);
			count++;
		}
		return n;
	}
	*/
}
