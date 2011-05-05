/**
 * Klasse Flugobjekt in 2011 by Semjon Mooraj
 * Die Klasse stellt ein Flugobjekt im System dar. Jäger und Fliege werden abgeleitet
 **/
package model;

import java.util.ArrayList;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.util.geom.BufferUtils;

public abstract class Flugobjekt extends Node{

	private static final long 		serialVersionUID = 1L;
	public static int 				count;	// Fliegennummer
	private long					age;	// Alter
	private static RegelnFliege 	regeln; // Verhaltensparameter
	private Vector3f 				acc;	// Beschleunigungsvektor
	private Vector3f				vel;	// Geschwindigkeitsvektor
	//public static ModelController loader=new ModelController();
	
	/**
	 * Konstruktor
	 * @param pos Position der Fliege 
	 */
	public Flugobjekt(Vector3f pos){
		super("Fliege_"+count);		// Instanziiere Node der das Flugpbjekt repraesentiert
		acc = new Vector3f(0,0,0);	// Mit 0 initialisieren
		vel = new Vector3f(0,0,0);
		age = System.currentTimeMillis();
		regeln = new RegelnFliege();
		
		// Form der Fliege
		Cylinder fly = new Cylinder("Cone",3,3,2f,5f);
		fly.setRadius1(.1f);
		TriMesh fluegelr = getFluegelR();
		TriMesh fluegell = getFluegelL();		
		
		
		
		// Modelloader ????
		/*
		Node n = loader.getNode();
		n.setLocalScale(.1f);
		*/
		//
		attachChild(fluegelr);
		attachChild(fluegell);
		attachChild(fly);
		setModelBound(new BoundingSphere());
		
		// Node auf pos bewegen
		setLocalTranslation(pos);
		
		// Counter hochzaehlen
		count++;
	}
	
	/**
	 * Methode, welche die den rechten Fluegel einer Fliege initialisiert 
	 * @return m TriMesh
	 */
	public TriMesh getFluegelR(){
		TriMesh m=new TriMesh("Fluegel");

        // Eckpunkte des Fluegels
        Vector3f[] vertexes={
            new Vector3f(0,0,0),
            new Vector3f(0,0,1),
            new Vector3f(0,-5,0),
            new Vector3f(0,-5,1)
        };

        // normale Richtung für jeden Eckpunkt
        Vector3f[] normals={
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1)
        };

        // Farbe für jeden Eckpunkt
        ColorRGBA[] colors={
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,1,0,1)
        };

        // Texturkoordinaten für jede Position
        Vector2f[] texCoords={
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1)
        };

        // Index fuer Vertex/Normal/Color/TexCoord wird gesetzt, 3 ergeben ein Dreieck
        int[] indexes={
            0,1,2,1,2,3
        };

        // uebergibt Information an die TreMesh
        m.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
                null, null, BufferUtils.createIntBuffer(indexes));

        // setzen der Grenzen
        m.setModelBound(new BoundingBox());
        m.updateModelBound();
        return m;
	}
	
	
	/**
	 * Methode, welche die den linken Fluegel einer Fliege initialisiert 
	 * @return m TriMesh
	 */
	public TriMesh getFluegelL(){
		TriMesh m=new TriMesh("Fluegel");

        // Eckpunkte des Fluegels
        Vector3f[] vertexes={
            new Vector3f(0,0,0),
            new Vector3f(0,0,1),
            new Vector3f(0,5,0),
            new Vector3f(0,5,1)
        };

        // normale Richtung für jeden Eckpunkt
        Vector3f[] normals={
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1)
        };

        // Farbe für jeden Eckpunkt
        ColorRGBA[] colors={
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,1,0,1)
        };

        // Texturkoordinaten für jede Position
        Vector2f[] texCoords={
            new Vector2f(0,0),
            new Vector2f(1,0),
            new Vector2f(0,1),
            new Vector2f(1,1)
        };

        // Index fuer Vertex/Normal/Color/TexCoord wird gesetzt, 3 ergeben ein Dreieck
        int[] indexes={
            0,1,2,1,2,3
        };

        // uebergibt Information an die TreMesh
        m.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
                null, null, BufferUtils.createIntBuffer(indexes));

        // setzen der Grenzen
        m.setModelBound(new BoundingBox());
        m.updateModelBound();
        return m;
	}
	
	
	/**
	 * Abstrakte Methode, die von jeweiligem Flugtier implemetiert wird, um Flugbahn zu berrechen
	 * @param boids
	 * @param leittier
	 */
	public abstract void run(ArrayList<Ephemera> boids,Vector3f leittier); 
	
	
	/**
	 * Berechne die Bewegung einer Fliege aufgrund aller Regeln
	 * und Objekte in der Welt
	 * @param flies
	 * @param leittier
	 */
	void berechneAktuelleVektoren(ArrayList<Ephemera> flies,Vector3f leittier){
	    // Berechne die Vektoren 
		Vector3f target = getLeittierZielVector(leittier);
		Vector3f sep  = separate(flies); 
	    Vector3f ali = align(flies);
	    Vector3f coh = cohesion(flies);
	     
	    // Gewichte mit eingestellten Parametern (siehe Regeln)
	    sep.mult(regeln.getSep_weight(), sep);
	    ali.mult(regeln.getAli_weight(), ali);
	    coh.mult(regeln.getCoh_weight(), coh);
	    target.mult(regeln.getFollow_weight(), target);
	    
	    // Diese Vektoren auf Beschleunigung aufaddieren
	    acc.add(target,acc);
	    acc.add(sep,acc);
	    acc.add(ali,acc);
	    acc.add(coh,acc);
	    
	    // Abschließende Gewichtung der Geschwindigkeit
	    acc = acc.mult(regeln.getMaxspeed());
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
	 *  Berrechne neue Position, verschiebe und rotiere die Fliege
	 */
	void update() {
	    vel.add(acc,vel);
	    // Passe Vektor an Regeln an
	    if (vel.length()>regeln.getMaxforce()){
			  vel = vel.normalize();
			  vel.mult(regeln.getMaxforce());
		}
	    
	    // "Gucke in Flugrichtung
	    this.lookAt(getLocalTranslation().subtract(vel.mult(-1)),acc);
	    
	    // Bewegung
	    getLocalTranslation().addLocal(vel);
	    acc.mult(0);
	  }
	
	
	/**
	 * Separation aus: Flocking by Daniel Shiffman. 
	 * Diese Methode berechnet einen Vektor
	 * @param flies im System angemeldete Fliegen 
	 * @return bewegungsVektor
	 */
	Vector3f separate (ArrayList<Ephemera> flies){
	    Vector3f steer = new Vector3f(0,0,0);
	    int count = 0;
	    // Für alle Fliegen im System
		for (Ephemera other:flies) {
		  float d = getPos().distance(other.getPos());
		  // Ist der Abstabd der >0 also es handelt sich nicht
		  if ((d > 0) && (d < regeln.getDesiredSeparation())) {
		    // Berechne Vektor der von anderer Fliege wegzeigt 
			Vector3f diff = getPos().subtract(other.getPos());
			diff = diff.normalize();
			diff.mult(1f/d,diff);        // Gewichte anhand der distanz
			steer.add(diff,steer);
			count++;            // Merker wie viele Fliegen einfluss nehmen
			
		  }
		  
		}
		
		// Teile den Vektor durch Anzahl der beeinflussenden Fliegen  
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
	Vector3f align (ArrayList<Ephemera> flies){
		Vector3f steer = new Vector3f(0,0,0);
	    int count = 0;
	    for (Ephemera other:flies){
	      float d = getPos().distance(other.getPos());
	      if ((d > 0) && (d < regeln.getNeighborDistance())) {
	        steer.add(other.getVel(),steer);
	        count++;
	      }
	    }
	    
	    if (count > 0){
	      steer.mult(1f/(float)count,steer);
	    }
	    
	    //solange größer als 0
	    if (steer.length() > 0){
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
	Vector3f cohesion (ArrayList<Ephemera> flies){
		Vector3f sum = new Vector3f(0,0,0);
	  	int count = 0;
		for (Ephemera other:flies) {
			float d = getPos().distance(other.getPos());
		    if ((d > 0) && (d < regeln.getNeighborDistance())) {
		    	sum.add(other.getPos(),sum); // Mittelwert über Fliegen innerhalb des Radiuses berechnen
		        count++;
		    }
		 }
		
		 if (count > 0) {
			 sum.mult(1f/(float)count,sum);
		     return sum;
		 }	 
		 return sum;
	}	
	

	/**
	 * Getter und Setter
	 */
	public Node getNode(){	
		return this;
	}
	
	public RegelnFliege getRegeln(){ 
		return regeln;
	}
	
	public void setRegeln(RegelnFliege regel){
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