package ephemera.model;

import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;


public class Jaeger extends Node{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Sphere s;
	private Vector3f pos;
	private Vector3f average;
	private ArrayList<Ephemera> flies;
	
	public Jaeger(Vector3f pos,ArrayList<Ephemera> flies){
		
		this.pos = pos;
		this.flies = flies;
		s = new Sphere("jaeger",pos,25,25,5f);
		average = new Vector3f(0,0,0);
	//	setLocalTranslation(pos);
	}
	
	public Vector3f getPos(){
		return pos;
	}
	
	public Vector3f getAverageSwarmPos(){
		
		int count= 0;
		average = new Vector3f(0,0,0);
		
		for (Ephemera other:flies) {
			
			average.addLocal(other.getPos());
			count++;
		}
		
		average.divideLocal(count);
		
		return average;
		
	}
	
	public void updateHunter(){
		
		pos = getAverageSwarmPos();
		//System.out.println("pos1 "+pos);
		//pos = pos.normalize();
		//System.out.println("posNorm "+pos);
		s.setLocalTranslation(pos);
		
		
		
	}
	

}
