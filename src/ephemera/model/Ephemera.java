package ephemera.model;

import java.util.ArrayList;
import com.jme.animation.SpatialTransformer;
import com.jme.bounding.*;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.util.geom.BufferUtils;

/**
 * Die Fliege
 * @author Semjom Mooray, Caolin Todt
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
	

	
	/**
	 * Konstruktor, welchem Startposition und Regeln der Fliege uebergeben wird.
	 * Geschwindigkeits- und Beschleunigungsvektor wird mit 0 initialisiert.
	 * Position der einzelnen Fliege wird gesetzt.
	 * Counter läuft mit und zaehlt erstellte Fliegen.
	 * 
	 * @param pos Startposition der Fliege
	 * @param rules Regeln nach denen sich Fliege richtet
	 */
	public Ephemera(Rules rules){
		
		// instanziiert Node, der die Fliege repraesentiert
		super("Fly_"+count);		
		
		// Geschwindigkeits- und Beschleunigungsvektor wird mit 0 initialisiert
		acc = new Vector3f(0,0,0);
		vel = new Vector3f(0,0,0);
		age = System.currentTimeMillis();
		this.rules = rules;
		
		// Methode initDefaultFly wird aufgerufen, initialisiert Form der Fliege
		initDefaultFly();
		setLocalTranslation(new Vector3f((float) (Math.random()*70), (float) (Math.random()*70), (float) (Math.random()*70)));
		
		// Counter wird hochgezaehlt
		count++;
	}
	
	
	
	/**
	 * Initialisierung der Fliege, hier wird das Aussehen der Fliege festgelegt
	 * Koerper, Kopf und Fluegel der Fliege werden erstellt und an Node gehaengt.
	 * Ein Animationscontroller wird mit 2 Keyframes initialisiert.
	 */
	public void initDefaultFly(){
		
		// Form der Fliege, Kopf und Rumpf in Form einer Sphere
		Sphere body = new Sphere("Koerper",10,10,2);
		Sphere head = new Sphere("Kopf",10,10,1);
		//body.setIsCollidable(false);
		body.setLocalScale(new Vector3f(.51f,.51f,2));
		body.setSolidColor(ColorRGBA.black);
		
		// Kopf auf richtige Position verschieben
		head.setLocalTranslation(0, 0, 4);

		TriMesh fluegelr = getWings(-10,0,0,-10,0,5);
		TriMesh fluegell = getWings(10,0,0,10,0,5);		
		
		attachChild(body);
		attachChild(head);
		
		attachChild(fluegelr);
		attachChild(fluegell);
		
		// Animation wird ueber SpatialController gesteuert
		spatialTransformer=new SpatialTransformer(2);
        // an diesem wird Objekt angemeldet 
		spatialTransformer.setObject(fluegelr,0,-1);
        spatialTransformer.setObject(fluegell, 1, -1);
        

        
        // berrechnet Quaternion für die Rotation
        Quaternion x45=new Quaternion();
        x45.fromAngleAxis(FastMath.DEG_TO_RAD*45,new Vector3f(0,0,1));
        Quaternion xm45=new Quaternion();
        xm45.fromAngleAxis(-FastMath.DEG_TO_RAD*45,new Vector3f(0,0,1));
      
        // verknuepft im Controller Zeitpunkte mit Quaternionen 
        spatialTransformer.setRotation(1, 2, xm45);
        spatialTransformer.setRotation(0,2,x45);
        spatialTransformer.setRotation(1, 4, x45);
        spatialTransformer.setRotation(0,4,xm45);

        // Controller vorrberreiten 
        spatialTransformer.interpolateMissing();
        spatialTransformer.setRepeatType(spatialTransformer.RT_CYCLE);
        spatialTransformer.setActive(true);
        spatialTransformer.setSpeed(1f);//10+FastMath.nextRandomFloat()*10);
       
        // Node-Element ist Host
        this.addController(spatialTransformer);
        setModelBound(new BoundingSphere());
        updateRenderState();
        updateModelBound();
	}

	/**
	 * Methode gibt rechten und linken Fluegel der Fliege zurueck,
	 * uebergebene Parameter bestimmen die Eckpunkte des Fluegels.
	 * 
	 * @param x x-Wert fuer Eckpunkt-Koordinate des Fliegenfluegels
	 * @param y y-Wert fuer Eckpunkt-Koordinate des Fliegenfluegels
	 * @param z z-Wert fuer Eckpunkt-Koordinate des Fliegenfluegels
	 * @param x1 zweiter x-Wert fuer Eckpunkt-Koordinate des Fliegenfluegels
	 * @param y1 zweiter y-Wert fuer Eckpunkt-Koordinate des Fliegenfluegels
	 * @param z1 zweiter z-Wert fuer Eckpunkt-Koordinate des Fliegenfluegels
	 * 
	 * @return m Fluegel der Fliege als TriMesh
	 */
	public TriMesh getWings(float x,float y,float z,float x1,float y1,float z1){
		TriMesh m=new TriMesh("Wings");

        // Eckpunkte des Fluegels
        Vector3f[] vertexes={
            new Vector3f(0,0,0),
            new Vector3f(0,0,1),
            new Vector3f(x,y,z),
            new Vector3f(x1,y1,z1)
        };

        // Richtung fuer jeden Eckpunkt
        Vector3f[] normals={
            new Vector3f(0,1,0),
            new Vector3f(0,1,0),
            new Vector3f(0,1,0),
            new Vector3f(0,1,0)
        };

        // Farbe fuer jeden Eckpunkt
        ColorRGBA[] colors={
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(1,0,0,1),
            new ColorRGBA(0,1,0,1),
            new ColorRGBA(0,1,0,1)
        };

     
        // Indizes fuer Eckpunkte des Fluegels, Richtung, Farbe
        // 3 Indizes ergeben ein Dreieck
        int[] indexes={
            0,1,2,1,2,3
        };

        // Information wid der TriMesh uebergeben
        m.reconstruct(BufferUtils.createFloatBuffer(vertexes), BufferUtils.createFloatBuffer(normals),
        		null,null, BufferUtils.createIntBuffer(indexes));
        
        return m;
	}
	
	
	/**
	 * Methode, welche eine Liste mit allen Fliegen, den Leittiervektor und die Welt uebergeben bekommt.
	 * Durch diese Parameter wird die Flugbahn berechnet.
	 * 
	 * @param boids Liste mit allen Fliegen
	 * @param leader Vektor des Leittieres
	 * @param world Welt, in der sich die Fliegen bewegen
	 */
	public void run(ArrayList<Ephemera> boids, Vector3f leader,World world) {
		calcSteeringVector(boids,leader,world);
	    updateMember(); 	
	}
	

	/**
	 * Methode, welche das Verhalten der Fliege anhand der Regeln und Umwelteinflüsse, wie
	 * Hindernissen in der Welt, berechnet. Dabei werden die Vektoren fuer das Leittier, Separation,
	 * Alignment und Cohesion errechnet. Danach findet eine Gewichtung der einzelnen Vektoren statt,
	 * diese gibt an, welcher Vektor wie stark in die Bewegungsberechnung der Fliegen einfließt.
	 * Zum Schluss erfolgt eine Kollisionsvermeidung durch die in der Klasse world befindliche
	 * obstaclesAvoidance-Methode.
	 * 
	 * @param flies Liste mit allen Fliegen
	 * @param leader Vektor des Leittieres
	 * @param world Welt, in der sich die Fliegen bewegen
	 */
	void calcSteeringVector(ArrayList<Ephemera> flies, Vector3f leader, World world) {

		// Berechnung der Vektoren fuer Separation, Alignment, Cohesion und des Leittieres		
		Vector3f target = getLeaderTargetVector(leader);
		Vector3f sep  = separate(flies); 
	    Vector3f ali = align(flies);
	    Vector3f coh = cohesion(flies);
	    Vector3f randomWalk = randomWalk();
	    
	    // Multiplikation der oben errechneten Vektoren mit den eingestellten Gewichten
	    target.multLocal(rules.getFollow_weight());
	    sep.multLocal(rules.getSep_weight());
	    ali.multLocal(rules.getAli_weight());
	    coh.multLocal(rules.getCoh_weight());
	    randomWalk.multLocal(rules.getRandomWalk_weight());
	    // Addierung der einzelnen Vektoren auf den Beschleunigungsvektor acc
	    acc = new Vector3f();
	    
	    acc.addLocal(sep);
	    acc.addLocal(ali);
	    acc.addLocal(coh);
	    acc.addLocal(target);
	    acc.addLocal(randomWalk);
	    
	    // Kollisionsvermeidung
	    Vector3f kol = world.obstacleAvoidance(this);
	    if (kol.length()>0)acc = kol;
	    //System.out.println("Coh: "+rules.getCoh_weight()+" Sep: "+rules.getSep_weight());
		// Kollisionsvermeidung mit Objekten in der Welt
		//if (koll.length()!=0)acc = koll.mult(1);  

	    acc.mult(rules.getSpeed(),acc);
	    if (rules.getSpeed()==0) {
	    	vel.multLocal(0);
	    	acc.multLocal(0);
	    }
	}
	
	/**
	 * Berechnet Vektor, der zum Zentrum des Leittiers zeigt.
	 * 
	 * @param leader Vektor des Leittieres
	 * @return res Abstandsvektor normalisiert
	 */
	public Vector3f getLeaderTargetVector(Vector3f leader){
		Vector3f pos = getLocalTranslation();
		return leader.subtract(pos).normalizeLocal();
	}
	
	
	/**
	 * Updatemethode, welche neue Position der Fliege berechnet.
	 * Geschwindigkeit und neue Position der Fliege werden gesetzt und anhand der lookAt-Methode
	 * sichergestellt, dass Fliege immer in Flugrichtung schaut.
	 */
	void updateMember() {
		
		// Beschleunigung wird auf Geschwindigkeit addiert
	    vel.addLocal(acc);
	   
	    // Geschwindigkeit wird auf Konformität mit Regeln ueberprueft
	    if (vel.length()>rules.getMaxspeed()){
			  vel = vel.normalize();
			  vel.mult(rules.getMaxspeed());
		}
	    // stellt sicher, dass Fliege immer in Flugrichtung schaut
	    this.lookAt(getLocalTranslation().subtract(vel.mult(-1)),new Vector3f(0,1,0));
	    spatialTransformer.setSpeed(vel.length()*100*rules.getSpeed());
	    
	    // Position setzen
	    getLocalTranslation().addLocal(vel);
	    
	    acc.mult(0);
	  }
	
	
	/**
	 * Separation aus: Flocking by Daniel Shiffman. 
	 * Diese Methode berechnet einen Vektor, der von benachbarter Fliege wegzeigt.
	 * 
	 * @param flies im System angemeldete Fliegen 
	 * @return Bewegungsvektor normalisiert
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
		
		return steer.normalizeLocal();
	}
	
	/**
	 * Alignment aus: Flocking by Daniel Shiffman. 
	 * Diese Methode berechnet einen Vektor, der in die gleiche Richtung zeigt, wie
	 * die Richtungsvektoren der anderen Fliegen.
	 * 
	 * @param flies im System angemeldete Fliegen 
	 * @returns Bewegungsvektor normalisiert
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
	  
	    return steer.normalizeLocal();
	}
	
	 
	/**
	 * Cohesion aus: Flocking by Daniel Shiffman.
	 * Diese Methode berechnet einen Vektor, der sicherstellt, dass eine Fliege
	 * den Schwarmmitgliedern in einem definierten Radius folgt.
	 * 
	 * @param flies im System angemeldete Fliegen 
	 * @return Bewegungsvektor normalisiert
	 */
	Vector3f cohesion (ArrayList<Ephemera> flies) {
		Vector3f sum = new Vector3f(0,0,0);
	  	int count = 0;
	  	
		for (Ephemera other:flies) {
			float d = getPos().distance(other.getPos());
			
		    if ((d > 0) && (d < rules.getNeighborDistance())) {
		    	// Mittelwert über Fliegen innerhalb des Radius berechnen
		    	sum.addLocal(other.getLocalTranslation()); 
		        count++;
		    } 
		}
		
		if (count > 0) {
			 sum = getLeaderTargetVector(sum.multLocal(1f/(float)count));
		     //return steer(sum,true);
		 }	 
		
		return sum.normalizeLocal();
	}
	/**
	 * RandomWalk
	 * Berrechnet einen Vektor, der innerhalb eines in den Regeln festgelegten Radius liegt.
	 * Diese Methode dient dazu, dass das Flugverhalten der Boids denen realer Fliegen angenaehert wird, 
	 * denn diese schwirren beim Fliegen scheinbar wahllos um die eigene Achse.
	 * 
	 * @return res Bewegungsvektor
	 */	
	public Vector3f randomWalk(){
		Vector3f[] base = {		 
				new Vector3f(1,0,0),
				new Vector3f(-1,0,0),
				new Vector3f(0,1,0),
				new Vector3f(0,-1,0),
				new Vector3f(0,0,1),
				new Vector3f(0,0,-1)
		};
		
		int x = FastMath.nextRandomInt(0, 1);
		int y = FastMath.nextRandomInt(2, 3);
		int z = FastMath.nextRandomInt(4, 5);
		Vector3f res = base[x].add(base[y]).add(base[z]); 
	
		return res;
	}

	/**
	 * Getter fuer Regeln der Fliege
	 * 
	 * @return rules Regeln der Fliege
	 */
	public Rules getRules(){ 
		return rules;
	}
	
	/**
	 * Setter fuer Regeln der Fliege
	 * 
	 * @param regel Regel, die gesetzt wird
	 */
	public void setRules(Rules rule){
		rules = rule;
	}
	
	/**
	 * Getter fuer Position der Fliege
	 * 
	 * @return Vektor der aktuellen Position der Fliege
	 */
	public Vector3f getPos(){
		return getLocalTranslation();
	}
	
	/**
	 * Getter fuer Geschwindkeit der Fliege
	 * 
	 * @return vel Geschwindkeitsvektor
	 */
	public Vector3f getVel(){
		return vel;
	}
	
	/**
	 * Getter fuer Beschleunigung der Fliege
	 * 
	 * @return acc Beschleunigungsvektor
	 */
	public Vector3f getAcc(){
		return acc;
	}
	
	/**
	 * Getter fuer Alter der Fliege
	 * 
	 * @return Alter der Fliege in Sekunden
	 */
	float getAge(){ 
		return (System.currentTimeMillis()-age)/1000.0f;
	}	
}