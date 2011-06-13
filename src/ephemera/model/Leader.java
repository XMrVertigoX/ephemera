package ephemera.model;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;

/**
 * Diese Klasse erstellt ein unsichtbares Leittier Objekt, das sich entlang eines Pfades bewegen kann
 * @author Benedikt Schuld, Gudrun Wagner
 *
 */
public class Leader extends Spatial {
	
	private static final long serialVersionUID = 1L;
	
	private Sphere s;
	private boolean visible;
	
	public Leader(){
		visible = false;
		
		s = new Sphere("Leader", new Vector3f(0,0,0), 20, 20, 0.1f);
		s.setDefaultColor(new ColorRGBA(255, 0, 0, 0.5f));
	}
	
	public Sphere getGeometry() {
		return s;
	}
	
	/**
	 * ...
	 */
	public void toggleVisible() {
		if (visible) {
			s.setLocalScale(0.1f);
			visible = false;
		}
		
		else {
			s.setLocalScale(200f);
			visible = true;
		}
	}
	
	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public void draw(Renderer arg0) {}

	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public void findCollisions(Spatial arg0, CollisionResults arg1, int arg2) {}
	
	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public void findPick(Ray arg0, PickResults arg1, int arg2) {}

	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public int getTriangleCount() {

		return 0;
	}

	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public int getVertexCount() {
	
		return 0;
	}

	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public boolean hasCollision(Spatial arg0, boolean arg1, int arg2) {
		
		return false;
	}

	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public void setModelBound(BoundingVolume arg0) {}

	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public void updateModelBound() {}

	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benoetigt
	 * 
	 */
	public void updateWorldBound() {}
	public Vector3f getPos(){ return s.getLocalTranslation();}
}
