package ephemera.ben.leittier;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;

/**
 * Diese Klasse erstellt ein unsichtbares Leittier Objekt, das sich entlang eines Pfades bewegen kann
 * @author Benedikt Schuld, Gudrun Wagner
 *
 */


public class Leittier extends Spatial {
	
	protected Sphere s; //Hilfskugel bitte entfernen wenn Scharm der Bewegungskurve folgt!
	
	public Leittier(){
		 s = new Sphere("kugel",25,25,1f); //Hilfskugel bitte entfernen wenn Scharm der Bewegungskurve folgt!
		
	}
	
	/**
	 * Diese Methoden sind von der Spatial Klasse geerbt und werden nicht benötigt
	 * 
	 */
	
	public void draw(Renderer arg0) {			
	}

	public void findCollisions(Spatial arg0, CollisionResults arg1, int arg2) {		
	}
	
	public void findPick(Ray arg0, PickResults arg1, int arg2) {		
	}

	
	public int getTriangleCount() {

		return 0;
	}

	
	public int getVertexCount() {
	
		return 0;
	}


	public boolean hasCollision(Spatial arg0, boolean arg1, int arg2) {
		
		return false;
	}

	
	public void setModelBound(BoundingVolume arg0) {
	}


	public void updateModelBound() {
	}

	
	public void updateWorldBound() {
	}
	
}
