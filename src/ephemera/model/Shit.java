
package ephemera.model;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;

import ephemera.view.MyJmeView;




public class Shit {

	
	private static int numShits = 0;
	MaterialState shitMaterial;
	MyJmeView impl ;
	private Sphere shit;
	private static Vector3f shitPos;
	
	
	
	public Shit( MyJmeView impl){
		this.impl = impl;
	}
	
	
	public void mouseShot() {
 
		

		Camera cam = impl.getCamera();
		
		
		shit = new Sphere("shit" + numShits++, 10, 10, 2);
		shit.setModelBound(new BoundingSphere());
		shit.updateModelBound();
	
		/** An Camera Position bewegen */
		shit.setLocalTranslation(new Vector3f(cam.getLocation()));
		shit.setRenderState(shitMaterial);

		shit.updateGeometricState(0, true);
	
		/**
		 * BewegungsController hinzufuegen "ShitMover"
		 */
		shit.addController(new ShitMover(shit, new Vector3f(cam.getDirection())));
		
		impl.getWorld().attachChild(shit);
		shit.updateRenderState();
			
		
	}
    
	public static int getShit(){
		return numShits;
	}
	
	public static Vector3f getShitPos(){
		return shitPos;
	}
	
   class ShitMover extends Controller {
		private static final long serialVersionUID = 1L;
		
			TriMesh shit;
 
			/** Richtung des Objektes */
			Vector3f direction;
 
			/** Geschwindigkeit */
			float speed = 300;
			
		
			/**Lebenszeit*/
			float lifeTime = 20;
						
		
			ShitMover(TriMesh shit, Vector3f direction) {
				this.shit = shit;
				this.direction = direction;
				this.direction.normalizeLocal();
		}
 
			
		
		
		public void update(float time) {
			lifeTime -= time;
				
			
			
			/** Entfernen wenn "lebenszeit" vorbei */
			if (lifeTime < 0) {
				impl.getWorld().detachChild(shit);
				shit.removeController(this);
				return;
			}
			
			shitPos = shit.getLocalTranslation();
			shitPos.addLocal(direction.mult(time * speed));
			shit.setLocalTranslation(shitPos);
			
			
			/**
			 * Kollisionsabfrage
			 */
			if (shit.getWorldBound().intersects(impl.getWorld().getTerrainNode().getWorldBound())) {

				speed = 0;
 
			}
		
			
		}
	}
	
}
