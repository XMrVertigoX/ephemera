package ephemera.model;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;

/**
 * Diese Klasse erstellt ein unsichtbares Leittier Objekt, das sich entlang eines Pfades bewegen kann
 * @author Benedikt Schuld, Gudrun Wagner
 *
 */
public class Leader extends Spatial {
	
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
}
