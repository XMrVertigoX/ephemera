/**
 * klasse jaeger
 * diese version ist als bastelversion fuer ben und caro gedacht,
 * also noch nicht zur weiterverwendung gedacht
 */

package ephemera.model;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;

import ephemera.controller.SchwarmController;


public class Hunter extends Node{

	private static final long serialVersionUID = 1L;
	public Sphere s; // stellt erst einmal den jaeger dar
	private long age; // alter des jaegers
	private Vector3f actualPos;
	private World world;
	private SchwarmController swarm;
	private Node hunter;
	private Vector3f average;
	private Vector3f target;
	private float fac = 5f;
	private boolean hungry;
	private boolean eat;
	private int index = 1;
	

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
	}
	
	/**
	 * initialisiert das jaegermodel
	 */
	public void initHunter(){
	
		s = new Sphere("jaeger",25,25,5f);
		s.setDefaultColor(new ColorRGBA(1,0,0,0));
		attachChild(s);
		s.setModelBound(new BoundingSphere());
		s.setLocalTranslation(actualPos);
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
		 * wenn jaeger aelter als 20 sekunden ist, dann verlaesst er die simulation,
		 * indem er zum rand der simulation fliegt (skybox)
		 */
		if(getAge()>20){

			Vector3f weg = new Vector3f(1000,1000,1000);
			target = weg.subtract(actualPos);	
			
			/**
			 * wenn jaeger am rand der skybox angekommen ist,
			 * wird er entfernt
			 */
			if(actualPos.distance(weg)<3){
				
				deleteHunter();
			}	
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
				if(index>0){
					index = (swarm.getSchwarm().size())-1;
					Vector3f flyPos = swarm.getSchwarm().get(index).getLocalTranslation();
					target = flyPos.subtract(actualPos);
					eat = eatBoid(flyPos, index);
					
					if(eat){
						eat = false;
					}
				}
			}
			
		}
		
		target = target.normalize();
		
		/**
		 * wenn abstand zum schwarm einen gewissen wert (50) unterschreitet,
		 * wird die geschwindigkeit reduziert (erst einmal nur zu beobachtungszwecken)
		 */
		if(actualPos.distance(getAverageSwarmPos())<50){
			target = target.mult(fac/10f);
			hungry = true;
		}
		else{
			target = target.mult(fac);
		}
		
		if(world.obstacleAvoidance(this)){
			
			actualPos = actualPos.addLocal(world.getCollisionVector());
			
		}else{
	
			actualPos = actualPos.addLocal(target);
 		}
		
//		System.out.println(FastMath.RAD_TO_DEG*((lastPos.normalize()).angleBetween((actualPos.normalize()))));
		
		setPos(actualPos);
	}
	
	/**
	 * wenn jaeger fast die position hat wie der boid, so wird der boid gefressen
	 * momentan bleiben die totan boids noch in der simulation, wird noch geaendert
	 * @param flyPos
	 * @param numberBoid
	 * @return
	 */
	public boolean eatBoid(Vector3f flyPos, int numberBoid){
		
		if(actualPos.distance(flyPos)<1){
			Ephemera e = swarm.getSchwarm().get(numberBoid);
			swarm.getSchwarm().remove(e);
			swarm.getSwarmNode().detachChildNamed(e.getName());
			
			return true;
		}
		else
			return false;
	}
	
	/**
	 * entfernt jaeger aus der simulation
	 */
	public void deleteHunter(){
		System.out.println("deleted");
		hunter.detachAllChildren();
		
	}
	
	
	/**
	 * gibt hunternode zurueck
	 * @return
	 */
	public Node getHunterNode(){
		return hunter;
	}
	
	/**
	 * gibt aktuelle jaegerposition zurueck
	 * @return
	 */
	public Vector3f getPos(){
		
		return actualPos;
	}
	
	public float getAge(){
		return ((System.currentTimeMillis()-age)/1000f);
	}
	
	public void setPos(Vector3f pos){
		this.actualPos = pos; 
	}
	
	
}
