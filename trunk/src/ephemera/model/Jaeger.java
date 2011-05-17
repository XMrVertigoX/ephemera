/**
 * klasse jaeger
 * diese version ist als bastelversion fuer ben und caro gedacht,
 * also noch nicht zur weiterverwendung gedacht
 */

package ephemera.model;

import java.util.ArrayList;

import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;


public class Jaeger extends Node{

	private static final long serialVersionUID = 1L;
	public Sphere s; // stellt erst einmal den jaeger dar
	private long age; // alter des jaegers
	private Vector3f actualPos;
	

	/**
	 * konstruktor
	 * @param pos
	 * @param flies
	 */
	public Jaeger(Vector3f pos){	
		
		super("Hunter");
		age = System.currentTimeMillis();
		this.actualPos = pos;
		initHunter();
	}
	
	/**
	 * initialisiert das jaegermodel
	 */
	public void initHunter(){
	
		s = new Sphere("jaeger",25,25,5f);
		attachChild(s);
	//	s.setModelBound(new BoundingSphere());
		s.setLocalTranslation(actualPos);
	}
	
	/**
	 * gibt aktuelle jaegerposition zurueck
	 * @return
	 */
	public Vector3f getPos(){
		
		return actualPos;
	}
	
	public float getAge(){
		return ((System.currentTimeMillis()-age)/1000f);
	}
	
	public void setPos(Vector3f pos){
		this.actualPos = pos; 
	}
	
	
}
