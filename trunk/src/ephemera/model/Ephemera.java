/**
 * Klasse Flugobjekt in 2011 by Semjon Mooraj
 * Die Klasse stellt ein Flugobjekt im System dar. Jäger und Fliege werden abgeleitet
 **/
package ephemera.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;


import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;

import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.ogrexml.Material;

import ephemera.tester.HelloTexture;

public class Ephemera extends Node{

	private static final long 	serialVersionUID = 1L;
	public static int 			count;	// Fliegennummer
	private static Regeln 		rules; // Verhaltensparameter
	private long				age;	// Alter
	private Vector3f 			acc;	// Beschleunigungsvektor
	private Vector3f			vel;	// Geschwindigkeitsvektor
	private SpatialTransformer 	spatialTransformer; // Animation
	
	//public static ModelController loader=new ModelController();
	
	private Vector3f[] base = {		// Richtungsvektoren für RandomWalk 
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
	public Ephemera(Vector3f pos){
		super("Fly_"+count);		// Instanziiere Node der die Fliege repräsentiert
		acc = new Vector3f(0,0,0);	// Mit 0 initialisieren
		vel = new Vector3f(0,0,0);
		age = System.currentTimeMillis();
		rules = new Regeln();
		// Lade Form
		initDefaultFly();
		setLocalTranslation(pos);
		// Counter Hochzählen
		count++;
	}
	/**
	 * Erstelle Körper und Flügel der Fliege / Initialisiere Animationscontroller mit zwei KeyFrames
	 */
	public void initDefaultFly(){
		
		// Form der Fliege
		Sphere body = new Sphere("Koerper",10,10,2);
		Sphere head = new Sphere("Kopf",10,10,1);
		//body.setIsCollidable(false);
		body.setLocalScale(new Vector3f(.51f,.51f,2));
		
		//Kopf verschieben
		head.setLocalTranslation(0, 0, 4);

		TriMesh fluegelr = getFluegel(-10,0,0,-10,0,5);
		TriMesh fluegell = getFluegel(10,0,0,10,0,5);		
		
		attachChild(body);
		attachChild(head);
		
		attachChild(fluegelr);
		attachChild(fluegell);
		// Node auf pos bewegen
		// Animation wird über SpatioalController gesteuert
		spatialTransformer=new SpatialTransformer(2);
        // Melde Objekte an 
		spatialTransformer.setObject(fluegelr,0,-1);
        spatialTransformer.setObject(fluegell, 1, -1);
        

        
        // Berrechne Quaternion für Rotation
        Quaternion x45=new Quaternion();
        x45.fromAngleAxis(FastMath.DEG_TO_RAD*45,new Vector3f(0,0,1));
        Quaternion xm45=new Quaternion();
        xm45.fromAngleAxis(-FastMath.DEG_TO_RAD*45,new Vector3f(0,0,1));
        // Verknüpfe im Controller Zeitpunkte mit Quaternionen 
        spatialTransformer.setRotation(1, 2, xm45);
        spatialTransformer.setRotation(0,2,x45);
        spatialTransformer.setRotation(1, 4, x45);
        spatialTransformer.setRotation(0,4,xm45);

        // Controller vorrberreiten 
        spatialTransformer.interpolateMissing();
        spatialTransformer.setRepeatType(spatialTransformer.RT_CYCLE);
        spatialTransformer.setActive(true);
        spatialTransformer.setSpeed(1f);//10+FastMath.nextRandomFloat()*10);
        // Node element ist host
        this.addController(spatialTransformer);
        setModelBound(new BoundingSphere());
        updateModelBound();
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

     
        // The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3 makes a triangle.
        int[] indexes={
            0,1,2,1,2,3
        };

        // Feed the information to the TriMesh
        m.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
        		null,null, BufferUtils.createIntBuffer(indexes));
        
        return m;
	}
	/**
	 * Methode die von jeweiliger Fliege implemetiert wird um Flugbahn zu berrechen und anzuwenden
	 * @param boids
	 * @param leittier
	 */
	public void run(ArrayList<Ephemera> boids, Vector3f leittier,World world) {
		berechneAktuelleVektoren(boids,leittier,world);
	    updateMember(); 	
	}
	public Vector3f kollider(Node schwarmNode){
		Vector3f steerAway = new Vector3f();
		List<Spatial> list = schwarmNode.getChildren();
		for (int i=0;i<list.size();i++){
			Spatial s = list.get(i);
			if (s.getWorldBound().intersects(this.getWorldBound())){//hasCollision(s, false)){
				steerAway = getLocalTranslation().subtract(s.getLocalTranslation());
				steerAway.normalizeLocal();
				return steerAway;
			}
		}
		
		return new Vector3f();
	}
	/**
	 * Navigationsmodul
	 * Berechne das Verhalten einer Fliege aufgrund aller Regeln
	 * und Objekte in der Welt
	 * @param flies
	 * @param leittier
	 */
	void berechneAktuelleVektoren(ArrayList<Ephemera> flies,Vector3f leittier,World world) {
	    
		
		Vector3f koll = new Vector3f();
		if (world!=null){
			koll = kollider(world.getObjectNode());
		}
		// Berechne die Vektoren 
		Vector3f target = getLeittierZielVector(leittier);
		Vector3f sep  = separate(flies); 
	    Vector3f ali = align(flies);
	    Vector3f coh = cohesion(flies);
	    //Vector3f randomWalk = randomWalk();
	    
	    target.multLocal(rules.getFollow_weight());
	    sep.multLocal(rules.getSep_weight());
	    ali.multLocal(rules.getAli_weight());
	    coh.multLocal(rules.getCoh_weight());
	    //randomWalk.multLocal(regeln.getRandomWalk_weight());
	    
	    //if (kollider(this.getParent())) sep.multLocal(4f)
	    acc.addLocal(sep);
	    acc.addLocal(ali);
	    acc.addLocal(coh);
	    acc.addLocal(target);
	   
	    
	    // Implement Reynolds: Steering = Desired - Velocity
		//acc.normalizeLocal();
		//acc.multLocal(regeln.getMaxspeed());
		//acc.subtractLocal(vel);
		/*
		if (acc.length()>rules.getMaxspeed()){
			  acc.normalizeLocal();
			  acc.multLocal(rules.getMaxforce());
		}
		*/
	    //acc.addLocal(randomWalk);
		// Kollisionsvermeidung mit Objekten in der Welt
		if (koll.length()!=0)acc = koll.mult(5);  
		// Kollisionsvermeidung mit anderen Schwarmmitgliedern
		//Vector3f koli = kollider(this.getParent());
//		if (koli.length()!=0) acc.add(koli).mult(2f);
		
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
	    
	    if (vel.length()>rules.getMaxspeed()){
			  vel = vel.normalize();
			  vel.mult(rules.getMaxspeed());
		}
	    
	    // "Gucke in Flugrichtung
	    this.lookAt(getLocalTranslation().subtract(vel.mult(-1)),new Vector3f(0,1,0));
	    //this.lookAt(vel.cross(Vector3f.UNIT_Y).cross(vel), vel);
	    // Setze geschiwindigkeit der Flügel annhand 
	    // Bewegung
	    
	    // Passe geschwindigeit an
	    // Position verschieben
	    spatialTransformer.setSpeed(vel.length()*10);
	    vel.multLocal(rules.getFluggeschwindigkeit());
	    getLocalTranslation().addLocal(vel);
	    
	    acc.mult(0);
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
	    for (Ephemera other:flies) {
		  float d = getPos().distance(other.getLocalTranslation());
		  // Ist der Abstabd der >0 also es handelt sich nicht
		  if ((d > 0) && (d < rules.getDesiredSeparation())) {
			// Berechne Vektor der von anderer Fliege wegzeigt 
			Vector3f diff = getLocalTranslation().subtract(other.getLocalTranslation());
			diff = diff.normalize();
			diff.multLocal(1f/d);        // Gewichte anhand der distanz
			steer.addLocal(diff);
			count++;            // Merker wie viele Fliegen einfluss nehmen
			if (this.hasCollision(other, true)) steer.mult(1/rules.getSep_weight());
		  }
		  
		}
	    
		// Teile den Vektor durch anzahl der beeinflussenden Fliegen  
		if (count > 0) {
		  steer.multLocal(1f/(float)count);
		  
		}
		/*
		// solange der Vektor größer ist als 0 
		if (steer.length() > 0) {
		  // Implement Reynolds: Steering = Desired - Velocity
		  steer.normalizeLocal();
		  steer.multLocal(regeln.getMaxspeed());
		  steer.subtractLocal(vel);
		  if (steer.length()>regeln.getMaxforce()){
			  steer = steer.normalize();
			  steer.mult(regeln.getMaxforce());
		  }
		}*/
		return steer.normalizeLocal();
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
	      if ((d > 0) && (d < rules.getNeighborDistance())) {
	        steer.addLocal(other.getVel());
	        count++;
	      }
	    }
	    
	    if (count > 0) {
	      steer.mult(1f/(float)count,steer);
	    }
	    /*
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
	    }*/
    return steer.normalizeLocal();
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
		    if ((d > 0) && (d < rules.getNeighborDistance())) {
		    	sum.addLocal(other.getLocalTranslation()); // Mittelwert über Fliegen innerhalb des Radiuses berechnen
		        count++;
		    } 
		}
		
		 if (count > 0) {
			 sum.multLocal(1f/(float)count);
		     //return steer(sum,true);
		 }	 
		 return sum.normalizeLocal();
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
	      if ((slowdown) && (d < 100.0)) desired.multLocal(rules.getMaxspeed()*(d/100.0f)); // This damping is somewhat arbitrary
	      else desired.multLocal(rules.getMaxspeed());
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
		Vector3f res = base[x].add(base[y]).add(base[z]); 
		// Kamera Rotiert -> ausgleich indem man vektor abbildet auf kreuzprodukt von ....
		return res;	
	}

	/**
	 * Getter und Setter
	 */
	public Regeln getRegeln(){ 
		return rules;
	}
	public void setRegeln(Regeln regel){
		rules = regel;
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