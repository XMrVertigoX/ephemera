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
import ephemera.controller.SchwarmController;

/**
 * Der Jaeger
 * @author Benedikt Schuld, Carolin Todt
 *
 */
public class Hunter extends Node{

	private static final long serialVersionUID = 1L;
	public Sphere s; // stellt erst einmal den jaeger dar
	private long age; // alter des jaegers
	private Vector3f actualPos;
	private World world;
	private SchwarmController swarm;
	private Vector3f average;
	private Vector3f target;
	private float fac = 10f;
	private boolean hungry;
	private int index = 1;
	private float lifetime = 20f;

    private Vector3f axis = new Vector3f(1, 0, 0);
    private float angle = 180*(FastMath.PI/180f);

    private Quaternion rotQuat = new Quaternion();

	private static Node hunterNode = new Node();
	private Dome oben;
	private Dome unten;
	private Sphere augeL;
	private Sphere augeR;
	private int sign;

	/**
	 * konstruktor
	 * @param pos
	 */
	public Hunter(Vector3f pos, World world, SchwarmController swarm){	
		
		super("Hunter");
		age = System.currentTimeMillis();
		this.actualPos = pos;
		initHunter();
		this.world = world;
		this.swarm = swarm;
		MyJmeView.setExist(true);
		sign = 1;
	}
	
	/**
	 * initialisiert das jaegermodel
	 */
	public void initHunter(){
	
		Sphere innen = new Sphere("innen", 15,15,19f);
		
		oben = new Dome("hunter",15,15,20f);
	    unten = new Dome("hunter",15,15,20f);
	    
	    augeL = new Sphere("eye", 10,10,5f);
	    augeR = new Sphere("eye", 10,10,5f);

	    augeR.setDefaultColor(new ColorRGBA(0,0,0,0));    
	    augeL.setDefaultColor(new ColorRGBA(0,0,0,0));
	    oben.setDefaultColor(new ColorRGBA(1,1,0,0));
	    unten.setDefaultColor(new ColorRGBA(1,1,0,0));
	    innen.setDefaultColor(new ColorRGBA(0.5f,0,0,0));

	    
	    rotQuat.fromAngleAxis(angle, axis);

	    unten.setLocalRotation(rotQuat);
	    augeL.setLocalTranslation(new Vector3f(11f, 10f, 9));
	    augeR.setLocalTranslation(new Vector3f(-11f, 10f, 9));



	    hunterNode.setModelBound(new BoundingSphere());
	    hunterNode.updateModelBound();
	    
	    hunterNode.attachChild(innen);
	    hunterNode.attachChild(augeL);
	    hunterNode.attachChild(augeR);
	    hunterNode.attachChild(oben);
	    hunterNode.attachChild(unten);
		
		
		
		attachChild(hunterNode);
		hunterNode.setModelBound(new BoundingSphere());
		hunterNode.setLocalTranslation(actualPos);
	}
	
	
	
	/**
	 * berechnet mittelpunkt des schwarms
	 * gibt diesen als vektor zurueck
	 * @return
	 */
	public Vector3f getAverageSwarmPos(){
		
		int count= 0;
		average = new Vector3f(0,0,0);
		
		for (Ephemera other:swarm.getSchwarm()){
			
			average = average.add(other.getLocalTranslation());
			count++;
		}
		
		average = average.divide(count);
		return average;	
	}
	
	
	
	/**
	 * berrechnet neuen bewegungsschritt des jaegers
	 * falls jaeger ein bestimmtes alter hat, verlaesst er die simulation
	 */
	public void updateHunter(){
		
		
		/**
		 * update Mund Position
		 */
		if(angle <= 130){
			  sign = 1;
			  
			  
		  }else if(angle >=180){
			  sign = -1;
		  }
		  
		  angle +=(2f*sign); 
		   
	      rotQuat.fromAngleAxis((-angle*(FastMath.PI/180f)), axis);

	      unten.setLocalRotation(rotQuat);
			
		
		
		/**
		 * wenn jaeger aelter als 20 sekunden ist, dann verlaesst er die simulation,
		 * indem er zum rand der simulation fliegt (skybox)
		 */
	
		if(getAge()>lifetime || (swarm.getSchwarm().size() == 0)){

//			Vector3f weg = new Vector3f(300,300,300);
//			target = weg.subtract(actualPos);	
//
//			/**
//			 * wenn jaeger am rand der skybox angekommen ist,
//			 * wird er entfernt
//			 */
//			if(actualPos.distance(weg)<5){
//				
//				deleteHunter();
//			}
			
			deleteHunter();
		}
		else{
			/**
			 * wenn jaeger noch zu "jung" ist dann konzentriert er sich auf den schwarmmittelpunkt
			 * oder (wenn eatBoid = true) peilt jaeger einen ausgewaehlten boid an
			 */
			if(!hungry){
				target = getAverageSwarmPos().subtract(actualPos);	
			}
			else{
				if(index>=0){
					if(index>0){
					index = (swarm.getSchwarm().size())-1;
					}
					Vector3f flyPos = swarm.getSchwarm().get(index).getLocalTranslation();
					target = flyPos.subtract(actualPos);
					eatBoid(flyPos, index);
	
				}
			}
			
		}
		
		target.normalizeLocal();
		
		/**
		 * wenn abstand zum schwarm einen gewissen wert (50) unterschreitet,
		 * wird die geschwindigkeit reduziert (erst einmal nur zu beobachtungszwecken)
		 */
		if(actualPos.distance(getAverageSwarmPos())<50){
			target.multLocal(fac/2f);
			hungry = true;
		}
		else{
			target.multLocal(fac);
		}
		
		if(world.obstacleAvoidance(this)){

		//	actualPos.addLocal(world.getCollisionVector());
			
			float angle = FastMath.PI/2f;
			
			Matrix3f rotMat = new Matrix3f(1,0,0,0,FastMath.cos(angle),FastMath.sin(angle)*-1f,0,FastMath.sin(angle),FastMath.cos(angle));
			
			target = rotMat.mult(target);

			
		}
	//	else{
	
			actualPos.addLocal(target);
// 		}
		
		setPos(actualPos);
		
		
		
		
	}
	
	/**
	 * wenn jaeger fast die position hat wie der boid, so wird der boid gefressen
	 * momentan bleiben die totan boids noch in der simulation, wird noch geaendert
	 * @param flyPos
	 * @param numberBoid
	 * @return
	 */
	public void eatBoid(Vector3f flyPos, int numberBoid){
		
		if(actualPos.distance(flyPos)<5f){
			Ephemera e = swarm.getSchwarm().get(numberBoid);
			swarm.getSchwarm().remove(e);
			swarm.getSwarmNode().detachChildNamed(e.getName());
		}
	}
	
	/**
	 * entfernt jaeger aus der simulation
	 */
	public void deleteHunter(){
		System.out.println("deleted");
		MyJmeView.setExist(false);
		this.detachChild(s);
	}
	
	
	public void setLifetime(float time){
		lifetime = time;
	}
	
	public float getLifetime(){
		return lifetime;
	}
	
	
	public float getAge(){
	
		return ((System.currentTimeMillis()-age)/1000f);
		
	}
	
	public void setPos(Vector3f pos){
		this.actualPos = pos; 
	}
	
	
}