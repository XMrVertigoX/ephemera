/**
 * Klasse Flugobjekt in 2011 by Semjon Mooraj
 * Die Klasse stellt ein Flugobjekt im System dar. Jäger und Fliege werden abgeleitet
 **/
package ephemera.model;

import java.util.ArrayList;

import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.util.geom.BufferUtils;

public abstract class Flugobjekt extends Node{

	private static final long 	serialVersionUID = 1L;
	public static int 			count;	// Fliegennummer
	private long				age;	// Alter
	private static Regeln 		regeln; // Verhaltensparameter
	private Vector3f 			acc;	// Beschleunigungsvektor
	private Vector3f			vel;	// Geschwindigkeitsvektor
	private SpatialTransformer 	st; // Animation
	private float 				masse;
	//public static ModelController loader=new ModelController();
	private Vector3f[] basis = {		// Richtungsvektoren für RandomWalk 
			new Vector3f(1,0,0),
			new Vector3f(-1,0,0),
			new Vector3f(0,1,0),
			new Vector3f(0,-1,0),
			new Vector3f(0,0,1),
			new Vector3f(0,0,-1)
	};
	/**
	 * Konstruktor
	 * @param pos Position der Fliege 
	 */
	public Flugobjekt(Vector3f pos){
		super("Fliege_"+count);		// Instanziiere Node der das Flugpbjekt repräsentiert
		acc = new Vector3f(0,0,0);	// Mit 0 initialisieren
		vel = new Vector3f(0,0,0);
		age = System.currentTimeMillis();
		regeln = new Regeln();
		
		initDefaultFly();
		setLocalTranslation(pos);
		
		
		
		// Counter Hochzählen
		count++;
	}
	public void initDefaultFly(){
		// Form der Fliege
		Cylinder fly = new Cylinder("Cone",3,3,1f,3f);
		fly.setRadius1(.1f);
		TriMesh fluegelr = getFluegel(-5,0,0,-5,0,1);
		TriMesh fluegell = getFluegel(5,0,0,5,0,1);		
		
		attachChild(fluegelr);
		attachChild(fluegell);
		attachChild(fly);
		setModelBound(new BoundingSphere());
		// Node auf pos bewegen
		// Animation wird über SpatioalController gesteuert
		st=new SpatialTransformer(2);
        // Melde Objekte an 
		st.setObject(fluegelr,0,-1);
        st.setObject(fluegell, 1, -1);
        
        // Berrechne Quaternion für Rotation
        Quaternion x45=new Quaternion();
        x45.fromAngleAxis(FastMath.DEG_TO_RAD*45,new Vector3f(0,0,1));
        Quaternion xm45=new Quaternion();
        xm45.fromAngleAxis(-FastMath.DEG_TO_RAD*45,new Vector3f(0,0,1));
        // Verknüpfe im Controller Zeitpunkte mit Quaternionen 
        st.setRotation(1, 2, xm45);
        st.setRotation(0,2,x45);
        st.setRotation(1, 4, x45);
        st.setRotation(0,4,xm45);

        // Controller vorrberreiten 
        st.interpolateMissing();
        st.setRepeatType(st.RT_CYCLE);
        st.setActive(true);
        st.setSpeed(10+FastMath.nextRandomFloat()*10);
        // Node element ist host
        this.addController(st);
        
	}
	/**
	 * Erstelle Linken und Rechten Flügel der Fliege (default Model)
	 * @return
	 */
	public TriMesh getFluegel(float x,float y,float z,float x1,float y1,float z1){
		TriMesh m=new TriMesh("Fluegel");

        // Eckpunkte des Fluegels
        Vector3f[] vertexes={
            new Vector3f(0,0,0),
            new Vector3f(0,0,1),
            new Vector3f(x,y,z),
            new Vector3f(x1,y1,z1)
        };

        // Normal directions for each vertex position
        Vector3f[] normals={
            new Vector3f(0,1,0),
            new Vector3f(0,1,0),
            new Vector3f(0,1,0),
            new Vector3f(0,1,0)
        };

        // Color for each vertex position
        ColorRGBA[] colors={
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,1,0,1)
        };

        // Texture Coordinates for each position
        Vector2f[] texCoords={
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1)
        };

        // The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3 makes a triangle.
        int[] indexes={
            0,1,2,1,2,3
        };

        // Feed the information to the TriMesh
        m.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
                null, null, BufferUtils.createIntBuffer(indexes));

        // Create a bounds
        m.setModelBound(new BoundingBox());
        m.updateModelBound();
        return m;
	}
	
	public Node getNode(){	
		return this;
	}
	/**
	 * Abstrakte Methode die von jeweiligem Flugtier implemetiert wird um Flugbahn zu berrechen
	 * @param boids
	 * @param leittier
	 */
	public abstract void run(ArrayList<Ephemera> boids,Vector3f leittier); 
	
	/**
	 * Navigationsmodul
	 * Berechne das Verhalten einer Fliege aufgrund aller Regeln
	 * und Objekte in der Welt
	 * @param flies
	 * @param leittier
	 */
	void berechneAktuelleVektoren(ArrayList<Ephemera> flies,Vector3f leittier) {
	    // Berechne die Vektoren 
		Vector3f target = getLeittierZielVector(leittier);
		Vector3f sep  = separate(flies); 
	    Vector3f ali = align(flies);
	    Vector3f coh = cohesion(flies);
	    Vector3f randomWalk = randomWalk();
	    // Gewichte mit eingestellten Parametern (siehe Regeln)
	    sep.multLocal(regeln.getSep_weight());
	    ali.multLocal(regeln.getAli_weight());
	    coh.multLocal(regeln.getCoh_weight());
	    target.multLocal(regeln.getFollow_weight());
	    randomWalk.multLocal(regeln.getRandomWalk_weight());
	    // Diese Vektoren auf Beschl. aufaddieren
	    acc=new Vector3f(0,0,0);
	    //acc.addLocal(randomWalk);
	    acc.addLocal(target);
	    acc.addLocal(sep);
	    acc.addLocal(ali);
	    acc.addLocal(coh);
	}
	/**
	 * Berechnet Vektor der zum Zentrum des Leittiers zeigt
	 * @param leittier
	 * @return abstandsvektor normalisiert
	 */
	public Vector3f getLeittierZielVector(Vector3f leittier) {
		Vector3f pos = getLocalTranslation();
		Vector3f res = leittier.subtract(pos).normalizeLocal();
		return res;
	}
	/**
	 * 	Flugmodul
	 *  Berrechne neue Position  verschiebe und rotiere die Fliege
	 */
	void updateMember() {
	    vel.addLocal(acc);
	    // Passe vektor an regeln an
	    if (vel.length()>regeln.getMaxforce()){
			  vel = vel.normalize();
			  vel.mult(regeln.getMaxforce());
		}
	    // Passe geschwindigeit an
	    vel.multLocal(regeln.getFluggeschwindigkeit());
	    // "Gucke in Flugrichtung
	    this.lookAt(getLocalTranslation().subtract(vel.mult(-1)),new Vector3f(0,1,0));
	    //this.lookAt(vel.cross(Vector3f.UNIT_Y).cross(vel), vel);
	    // Setze geschiwindigkeit der Flügel annhand 
	    // Bewegung
	    getLocalTranslation().addLocal(vel);
	    
	    //acc.mult(0);
	  }
	
	/**
	 * Separation aus: Flocking by Daniel Shiffman. 
	 * Diese Methode berechnet einen Vektor
	 * @param flies im System angemeldete Fliegen 
	 * @return bewegungsVektor
	 */
	Vector3f separate (ArrayList<Ephemera> flies) {
	    Vector3f steer = new Vector3f(0,0,0);
	    int count = 0;
	    // Für alle Fliegen im System
		for (Ephemera other:flies) {
		  float d = getPos().distance(other.getLocalTranslation());
		  // Ist der Abstabd der >0 also es handelt sich nicht
		  if ((d > 0) && (d < regeln.getDesiredSeparation())) {
		    // Berechne Vektor der von anderer Fliege wegzeigt 
			Vector3f diff = getLocalTranslation().subtract(other.getLocalTranslation());
			diff = diff.normalize();
			diff.mult(1f/d,diff);        // Gewichte anhand der distanz
			steer.add(diff,steer);
			count++;            // Merker wie viele Fliegen einfluss nehmen
			
		  }
		  
		}
		// Teile den Vektor durch anzahl der beeinflussenden Fliegen  
		if (count > 0) {
		  steer.mult(1f/(float)count,steer);
		}
		
		// solange der Vektor größer ist als 0 
		if (steer.length() > 0) {
		  // Implement Reynolds: Steering = Desired - Velocity
		  steer = steer.normalize();
		  steer.mult(regeln.getMaxspeed(),steer);
		  steer.subtract(vel,steer);
		  if (steer.length()>regeln.getMaxforce()){
			  steer = steer.normalize();
			  steer.mult(regeln.getMaxforce());
		  }
		}
		return steer;
  }
	/**
	 * Alignment aus: Flocking by Daniel Shiffman. 
	 * "Bewege dich in die gleiche Richtung wie die anderen Fliegen"
	 * @returns bewegungsvektor
	 */
	Vector3f align (ArrayList<Ephemera> flies) {
		Vector3f steer = new Vector3f(0,0,0);
	    int count = 0;
	    for (Ephemera other:flies) {
	      float d = getPos().distance(other.getPos());
	      if ((d > 0) && (d < regeln.getNeighborDistance())) {
	        steer.add(other.getVel(),steer);
	        count++;
	      }
	    }
	    if (count > 0) {
	      steer.mult(1f/(float)count,steer);
	    }
	    //solange größer als 0
	    if (steer.length() > 0) {
	      // Implement Reynolds: Steering = Desired - Velocity
	      steer = steer.normalize();
	      steer.mult(regeln.getMaxspeed(),steer);
	      steer.subtract(vel,steer);
	      if (steer.length()>regeln.getMaxforce()){
			  steer = steer.normalize();
			  steer.mult(regeln.getMaxforce());
		  }	
	    }
    return steer;
	}
	/**
	 * Cohesion aus: Flocking by Daniel Shiffman. 
	 * Folge den Schwarmmitgliedern in einem def radius 
	 */
	Vector3f cohesion (ArrayList<Ephemera> flies) {
		Vector3f sum = new Vector3f(0,0,0);
	  	int count = 0;
		for (Ephemera other:flies) {
			float d = getPos().distance(other.getPos());
		    if ((d > 0) && (d < regeln.getNeighborDistance())) {
		    	sum.addLocal(other.getLocalTranslation()); // Mittelwert über Fliegen innerhalb des Radiuses berechnen
		        count++;
		    }
		 }
		 if (count > 0) {
			 sum.multLocal(1f/(float)count);
		     return steer(sum,true);
		 }	 
		 return sum;
	}
	/**
	 * steer aus: Flocking by Daniel Shiffman. 
	 * @param target
	 * @param slowdown
	 * @return
	 */
	Vector3f steer(Vector3f target, boolean slowdown) {
	    Vector3f steer;  // The steering vector
	    Vector3f desired = target.subtract(getLocalTranslation());  // A vector pointing from the location to the target
	    float d = desired.length(); // Distance from the target is the magnitude of the vector
	    // If the distance is greater than 0, calc steering (otherwise return zero vector)
	    if (d > 0) {
	      // Normalize desired
	      desired.normalize();
	      // Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
	      if ((slowdown) && (d < 100.0)) desired.multLocal(regeln.getMaxspeed()*(d/100.0f)); // This damping is somewhat arbitrary
	      else desired.multLocal(regeln.getMaxspeed());
	      // Steering = Desired minus Velocity
	      steer = desired.subtractLocal(vel);
	      //steer.mult(maxforce);  // Limit to maximum steering force
	    } 
	    else {
	      steer = new Vector3f(0,0,0);
	    }
	    return steer;
	  }
	
	
	
	/**
	 * RandomWalk
	 * Berrechne einen Vektor der innerhalb eines in den Regeln festgelegten radius liegt
	 */	
	public Vector3f randomWalk(){
		int x = FastMath.nextRandomInt(0, 1);
		int y = FastMath.nextRandomInt(2, 3);
		int z = FastMath.nextRandomInt(4, 5);
		Vector3f res = basis[x].add(basis[y]).add(basis[z]); 
		// Kamera Rotiert -> ausgleich indem man vektor abbildet auf kreuzprodukt von ....
		return res;	
	}

	/**
	 * Getter und Setter
	 */
	public Regeln getRegeln(){ 
		return regeln;
	}
	public void setRegeln(Regeln regel){
		regeln = regel;
	}
	public Vector3f getPos(){
		return getLocalTranslation();
	}
	public Vector3f getVel(){
		return vel;
	}
	public Vector3f getAcc(){
		return acc;
	}
	float getAge(){ 
		return (System.currentTimeMillis()-age)/1000.0f;
	}	
}