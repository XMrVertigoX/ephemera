package ephemera.model;

import java.util.ArrayList;
import java.util.List;
import com.jme.animation.SpatialTransformer;
import com.jme.bounding.*;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.util.geom.BufferUtils;

/**
 * Die Fliege
 * @author
 *
 */
public class Ephemera extends Node{

	private static final long 	serialVersionUID = 1L;
	public static int 			count;	// Fliegennummer
	private static Rules 		rules; // Verhaltensparameter
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
	 * Konstruktor, welchem die Startposition der Fliege uebergeben wird
	 * Geschwindigkeits- und Beschleunigungsvektor wird mit 0 initialisiert
	 * Position der einzelnen Fliege wird gesetzt
	 * Counter läuft mit und zaehlt erstellte Fliegen
	 * @param pos
	 */
//	public Ephemera(Vector3f pos){
//		super("Fly_"+count);		// Instanziiere Node der die Fliege repräsentiert
//		acc = new Vector3f(0,0,0);	// Mit 0 initialisieren
//		vel = new Vector3f(0,0,0);
//		age = System.currentTimeMillis();
//		rules = new Rules();
//		// Lade Form
//		initDefaultFly();
//		setLocalTranslation(pos);
//		// Counter Hochzählen
//		count++;
//	}
	
	/**
	 * Konstruktor, welchem Startposition und Regeln der Fliege uebergeben wird
	 * Geschwindigkeits- und Beschleunigungsvektor wird mit 0 initialisiert
	 * Position der einzelnen Fliege wird gesetzt
	 * Counter läuft mit und zaehlt erstellte Fliegen
	 * @param pos
	 * @param rules
	 */
	public Ephemera(Rules rules){
		super("Fly_"+count);		// Instanziiere Node der die Fliege repräsentiert
		acc = new Vector3f(0,0,0);	// Mit 0 initialisieren
		vel = new Vector3f(0,0,0);
		age = System.currentTimeMillis();
		this.rules = rules;
		// Lade Form
		initDefaultFly();
		setLocalTranslation(new Vector3f((float) (Math.random()*100), (float) (Math.random()*100), (float) (Math.random()*100)));
		// Counter Hochzählen
		count++;
	}
	
	
	
	/**
	 * Initialisierung der Fliege, hier wird das Aussehen der Fliege festgelegt
	 * Koerper, Kopf und Fluegel der Fliege werden erstellt und an Node gehaengt
	 * Animationscontroller wird mit 2 Keyframes initialisiert
	 */
	public void initDefaultFly(){
		
		// Form der Fliege
		Sphere body = new Sphere("Koerper",10,10,2);
		Sphere head = new Sphere("Kopf",10,10,1);
		//body.setIsCollidable(false);
		body.setLocalScale(new Vector3f(.51f,.51f,2));
		body.setSolidColor(ColorRGBA.black);
		
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
        updateRenderState();
        updateModelBound();
	}

	/**
	 * Methode gibt rechten und linken Fluegel der Fliege zurueck,
	 * uebergebene Parameter bestimmen die Eckpunkte des Fluegels.
	 * @param x
	 * @param y
	 * @param z
	 * @param x1
	 * @param y1
	 * @param z1
	 * @return TriMesh
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
	 * Methode, welche eine Liste mit allen Fliegen, den Leittiervektor und die Welt uebergeben bekommt.
	 * Durch diese Parameter wird die Flugbahn berechnet.
	 * @param boids
	 * @param leittier
	 * @param world
	 */
	public void run(ArrayList<Ephemera> boids, Vector3f leittier,World world) {
		calcSteeringVector(boids,leittier,world);
	    updateMember(); 	
	}
	
	/**
	 * Methode, welche der Kollisionsvermeidung dient
	 * Hier wird berechnet, ob es eine Kollision eines Boids mit Hindernissen und den Weltgrenzen gibt.
	 * Wenn ja, wird ein steerAway-Vektor berechnet, der vom Hindernis wegzeigt.
	 * Wenn nicht, dann wird ein nicht initialisierter Vektor zurueckgegeben.
	 * @param objectNode
	 * @return Vector3f
	 */

	
	/**
	 * Methode, welche das Verhalten der Fliege anhand der Regeln und Umwelteinflüsse, wie
	 * Hindernissen in der Welt, berechnet.
	 * @param flies
	 * @param leittier
	 * @param world
	 */
	void calcSteeringVector(ArrayList<Ephemera> flies, Vector3f leittier, World world) {
	    
		
		
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
	    //randomWalk.multLocal(rules.getRandomWalk_weight());
	    acc = new Vector3f();
	    //if (kollider(this.getParent())) sep.multLocal(4f)
	    acc.addLocal(sep);
	    acc.addLocal(ali);
	    acc.addLocal(coh);
	    acc.addLocal(target);
	    //acc.addLocal(randomWalk);
	    acc.addLocal(world.obstacleAvoidance(this));
	    //System.out.println("Coh: "+rules.getCoh_weight()+" Sep: "+rules.getSep_weight());
		// Kollisionsvermeidung mit Objekten in der Welt
		//if (koll.length()!=0)acc = koll.mult(1);  

	}
	/**
	 * Berechnet Vektor der zum Zentrum des Leittiers zeigt
	 * @param leittier
	 * @return Abstandsvektor normalisiert
	 */
	public Vector3f getLeittierZielVector(Vector3f leittier){
		Vector3f pos = getLocalTranslation();
		Vector3f res = leittier.subtract(pos).normalizeLocal();
		return res;
	}
	/**
	 * 	Flugmodul
	 *  Berrechne neue Position  verschiebe und rotiere die Fliege
	 */
	
	/**
	 * Updatemethode, welche neue Position der Fliege berechnet.
	 * Geschwindigkeit der Fliege wird gesetzt und anhand der lookAt-Methode
	 * sichergestellt, dass Fliege immer in Flugrichtung schaut.
	 */
	void updateMember() {
	    vel.addLocal(acc);
	    // Passe vektor an regeln an
	    
	    if (vel.length()>rules.getSpeed()){
			  vel = vel.normalize();
			  vel.mult(rules.getMaxspeed());
		}
	    
	    // "Gucke in Flugrichtung
	    this.lookAt(getLocalTranslation().subtract(vel.mult(-1)),new Vector3f(0,1,0));
	    spatialTransformer.setSpeed(vel.length()*100*rules.getSpeed());
	    
	    vel.multLocal(rules.getSpeed());
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
	 * Berrechne einen Vektor der innerhalb eines in den Regeln festgelegten Radius liegt.
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
	public Rules getRegeln(){ 
		return rules;
	}
	public void setRegeln(Rules regel){
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