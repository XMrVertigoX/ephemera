package ephemera.swarm;

import java.util.ArrayList;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

public class Schwarm {
	private ArrayList<Ephemera> flies;
	
	public Schwarm(){
		// erstelle Schwarm
		flies = new ArrayList<Ephemera>();
	}
	
	
	ArrayList<Ephemera> getSwarm(){
		return flies;
	}
	public void updateAll(){
		for (Ephemera e:flies){
			e.run(flies);
		}
	}
	public Node getSwarmNode(){
		Node swarm = new Node("theSwarm");
		for (Ephemera e:flies){
			swarm.attachChild(e.getNode());
		}
		swarm.setModelBound(new BoundingSphere());
		return swarm;
	}
	void addFly(Ephemera newbie){
		flies.add(newbie);
	}
	void addFlies(int N){
		for (int i=0;i<N;i++){
			Ephemera f= new Ephemera(new Vector3f(	(float)(Math.random()*100),
													(float)(Math.random()*100),
													(float)(Math.random()*100)));
			flies.add(f);
		}		
	}
	
}
