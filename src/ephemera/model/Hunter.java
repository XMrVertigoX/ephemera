package ephemera.model;

import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Dome;
import com.jme.scene.shape.Sphere;
import ephemera.view.MyJmeView;
import ephemera.controller.SwarmController;

/**
 * Der Jaeger
 * @author Benedikt Schuld, Carolin Todt
 *
 */
public class Hunter extends Node{

	private static final long serialVersionUID = 1L;
	private long age;
	private Vector3f actualPos;
	private World world;
	private SwarmController swarm;
	private Vector3f average;
	private Vector3f target;
	private float fac = 10f;
	private boolean hungry;
    private Vector3f axis = new Vector3f(1, 0, 0);
    private float angle = 180*(FastMath.PI/180f);
    private Quaternion rotQuat = new Quaternion();
	private Dome upperHead;
	private Dome lowerHead;
	private Sphere eyeL;
	private Sphere eyeR;
	private Sphere inside;
	private int sign;
	private int lifetime;

	/**
	 * Konstruktor, welchem die Startposition des Jaegers, die Welt mit allen Objekten und der Schwarm uebergeben wird. 
	 * @param pos
	 * @param world 
	 * @param swarm
	 */
	public Hunter(Vector3f pos, World world, SwarmController swarm, int lifetime){	
		super("Hunter");
		this.lifetime = lifetime;
		age = System.currentTimeMillis();
		this.actualPos = pos;
		initHunter();
		this.world = world;
		this.swarm = swarm;
		MyJmeView.setExist(true);
		sign = 1;
	}
	
	/**
	 * Methode, welche den Jaeger initialisiert und vom Konstruktor aufgerufen wird.
	 * Hier wird das Aussehen des Jaegers festgelegt, welches dem eines Pacman entpricht.
	 * Jener besteht aus 2 Domes (Halbkugeln) und 3 Spheren (Kugeln), die an eine hunterNode angehängt werden.
	 */
	public void initHunter(){
		
		upperHead = new Dome("hunter",15,15,20f);
	    lowerHead = new Dome("hunter",15,15,20f);
	    
	    eyeL = new Sphere("eye", 10,10,5f);
	    eyeR = new Sphere("eye", 10,10,5f);
	    
	    inside = new Sphere("innen", 15,15,19f);

	    eyeR.setDefaultColor(new ColorRGBA(0,0,0,0));    
	    eyeL.setDefaultColor(new ColorRGBA(0,0,0,0));
	    upperHead.setDefaultColor(new ColorRGBA(1,1,0,0));
	    lowerHead.setDefaultColor(new ColorRGBA(1,1,0,0));
	    inside.setDefaultColor(new ColorRGBA(0.5f,0,0,0));
	    
	    rotQuat.fromAngleAxis(angle, axis);

	    lowerHead.setLocalRotation(rotQuat);
	    eyeL.setLocalTranslation(new Vector3f(11f, 10f, 9));
	    eyeR.setLocalTranslation(new Vector3f(-11f, 10f, 9));
	    
	    attachChild(inside);
	    attachChild(eyeL);
	    attachChild(eyeR);
	    attachChild(upperHead);
	    attachChild(lowerHead);

	    setModelBound(new BoundingSphere());
	    updateModelBound();
	    setLocalScale(0.6f);
		
		setLocalTranslation(actualPos);
	    
		updateRenderState();
	}
	
	
	
	/**
	 * Methode, welche den Schwarmmittelpunkt in Form eines Vektors zurueckgibt.
	 * @return Vector3f average
	 */
	public Vector3f getAverageSwarmPos(){
		
		int count= 0;
		average = new Vector3f(0,0,0);
		
		for (Ephemera other:swarm.getSwarm()){
			
			average = average.add(other.getLocalTranslation());
			count++;
		}
		
		average = average.divide(count);
		return average;	
	}
	
	
	
	/**
	 * Updatemethode des Jaegers, hier wird die neue Position des Jaegers und sein Ziel (etwa Schwarmmittelpunkt
	 * oder ein einzelnes Boid) errechnet. Weiterhin findet eine Neuberechnung des Jaegermundes statt, welcher
	 * sich regelmaeßig oeffnet und schließt.
	 * 
	 */
	public void updateHunter(){
		
		
		/**
		 * Update Mund Jaeger
		 */
		if(angle <= 130){
			  sign = 1;
			    
		  }
		
		else if(angle >=180){
			  sign = -1;
		  }
		  
		  angle +=(2f*sign); 
	      rotQuat.fromAngleAxis((-angle*(FastMath.PI/180f)), axis);
	      lowerHead.setLocalRotation(rotQuat);
			
		
		
		/**
		 * Wenn Jaeger aelter als per Slider eingestellte Zeit (lifetime) ist oder kein Schwarm vorhanden ist,
		 * wird die delteHunter-Methode aufgerufen und der Jäeger verschwindet aus der Simulation.
		 * Sollte dies noch nicht der Fall sein, so wird das Ziel des Jaegers ermittelt. Wenn hungry auf false
		 * steht, ist Jaeger noch zu weit vom Schwarm entfernt und bekommt als Zielvektor den Schwarmmittelpunkt
		 * uebergeben. Wenn nicht, ist der Jaeger hungrig und macht Jagd auf ein einzelnes Boid, dessen Position
		 * nun sein Zielvektor ist.
		 */

		if (getAge() >= lifetime || (swarm.getSwarm().size() == 0)) {

			deleteHunter();
		}
		
		else{
			
			if(!hungry){
				target = getAverageSwarmPos().subtract(actualPos);	
			}
			
			else{
				Vector3f flyPos = swarm.getSwarm().get(0).getLocalTranslation();
				target = flyPos.subtract(actualPos);
				eatBoid(flyPos, 0);
			}
			
		}
		
		target.normalizeLocal();
		
		/**
		 * Wenn der Abstand zum Schwarm unter 50 ist, bekommt Jaeger Hunger und verringert seine Geschwindigkeit,
		 * die Variavle hungry wird auf true gesetzt.
		 * Wenn Abstand noch zu groß ist, dann behaelt Jaeger seine Geschwindkeit bei.
		 */
		if(actualPos.distance(getAverageSwarmPos())<50){
			target.multLocal(fac/2f);
			hungry = true;
		}
		
		else{
			target.multLocal(fac);
		}
		
		/**
		 * Hier wird die Kollision mit Hindernissen in der Welt vermieden. Wird eine Kollision erkannt,
		 * so wird der Zielvektor durch eine Rotationsmatrix um 30 Grad rotiert.
		 */
		if(world.obstacleAvoidance(this).length()!=0){
			
			float angle = FastMath.PI/2f;
			Matrix3f rotMat = new Matrix3f(1,0,0,0,FastMath.cos(angle),FastMath.sin(angle)*-1f,0,FastMath.sin(angle),FastMath.cos(angle));
			target = rotMat.mult(target);
		}
	
		// lookAt-Methode garantiert, dass Jaeger immer in Flugrichtung schaut
		lookAt(actualPos.add(target), new Vector3f(0,1,0));		
		actualPos.addLocal(target);		
	}
	
	
	/**
	 * Methode, welche das Fressen eines Boids durch den Jaeger regelt.
	 * Unterschreitet der Jaeger einen bestimmten Abstand zum Boid, so gilt dieser als
	 * gefressen und wird aus dem Schwarm entfernt.
	 * @param flyPos
	 * @param numberBoid
	 */
	public void eatBoid(Vector3f flyPos, int numberBoid){
		
		if(actualPos.distance(flyPos)<5f){
			Ephemera e = swarm.getSwarm().get(numberBoid);
			swarm.getSwarm().remove(e);
			swarm.getSwarmNode().detachChildNamed(e.getName());
		}
	}
	
	
	/**
	 * Diese Methode enfernt die Geometrie vom Node des Jaegers und macht ihn unsichtbar.
	 */
	public void deleteHunter(){
		MyJmeView.setExist(false);
		world.detachChild(this);
	}
	
	/**
	 * Methode setzt Lebenszeit des Jaegers.
	 * @param time
	 */
	public void setLifetime(int time){
		lifetime = time;
	}
	
	/**
	 * Methode uebergibt eingestellte Lebenszeit des Jaegers.
	 * @return lifetime
	 */
	public float getLifetime(){
		return lifetime;
	}
	
	
	/**
	 * Methode, welche das Alter des Jaegers in Sekunden zurueckgibt.
	 * @return Lebenszeit [s]
	 */
	public float getAge(){
		return ((System.currentTimeMillis()-age)/1000f);	
	}
	
}
