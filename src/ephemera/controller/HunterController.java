package ephemera.controller;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;

import ephemera.model.Ephemera;
import ephemera.model.Jaeger;
import ephemera.model.World;

public class HunterController {
	
	private boolean exits;
	private World world;
	private SchwarmController schwarm;
	private Jaeger j;
	private Node hunter;
	private Vector3f average;
	private Vector3f actualPos;
	private Vector3f ZielPos;
	private Vector3f lastPos;
	private float fac = 5f;
	private boolean hungry;
	private boolean eat;
	private int index = 1;
	
	
	public HunterController(World world, SchwarmController schwarm){
		this.world = world;
		this.schwarm = schwarm;
		exits = false;
		
	}
	
	
	/**
	 * initialisiert den jaeger
	 */
	public void createHunter(){
		
		if(!exits){
			
			actualPos = new Vector3f(1000,1000,1000);
			ZielPos = actualPos;
			j = new Jaeger(actualPos);
			hunter = new Node("Hunter");
			hunter.attachChild(j);
			exits = true;
		}
	}
	
	
	
	/**
	 * berechnet mittelpunkt des schwarms
	 * gibt diesen als vektor zurueck
	 * @return
	 */
	public Vector3f getAverageSwarmPos(){
		
		int count= 0;
		average = new Vector3f(0,0,0);
		
		for (Ephemera other:schwarm.getSchwarm()){
			
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
		
		if(!exits){return;}
		
		lastPos = ZielPos;
		
		/**
		 * wenn jaeger aelter als 20 sekunden ist, dann verlaesst er die simulation,
		 * indem er zum rand der simulation fliegt (skybox)
		 */
		if(j.getAge()>200){

			Vector3f weg = new Vector3f(1000,1000,1000);
			ZielPos = weg.subtract(actualPos);	
			
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
				ZielPos = getAverageSwarmPos().subtract(actualPos);	
			}
			else{
				if(index>0){
					index = (schwarm.getSchwarm().size())-1;
					Vector3f flyPos = schwarm.getSchwarm().get(index).getLocalTranslation();
					ZielPos = flyPos.subtract(actualPos);
					eat = eatBoid(flyPos, index);
					
					if(eat){
						eat = false;
					}
				}
			}
			
		}
		
//		ZielPos = checkAngle(lastPos, actualPos, ZielPos); // pruefen auf bewegungswinkel 
		ZielPos = ZielPos.normalize();
		
		/**
		 * wenn abstand zum schwarm einen gewissen wert (50) unterschreitet,
		 * wird die geschwindigkeit reduziert (erst einmal nur zu beobachtungszwecken)
		 */
		if(actualPos.distance(getAverageSwarmPos())<50){
			ZielPos = ZielPos.mult(fac/10f);
			hungry = true;
		}
		else{
			ZielPos = ZielPos.mult(fac);
		}
		
		if(world.obstacleAvoidance(j)){
			
			actualPos = actualPos.addLocal(world.getCollisionVector());
			
		}else{
	
			actualPos = actualPos.addLocal(ZielPos);
 		}
		
//		System.out.println(FastMath.RAD_TO_DEG*((lastPos.normalize()).angleBetween((actualPos.normalize()))));
		
		j.setPos(actualPos);
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
			Ephemera e = schwarm.getSchwarm().get(numberBoid);
			schwarm.getSchwarm().remove(e);
			schwarm.getSwarmNode().detachChildNamed(e.getName());
			
			return true;
		}
		else
			return false;
	}
	
	/**
	 * entfernt jaeger aus der simulation
	 */
	public void deleteHunter(){
		
		exits = false;
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
	 * wird nicht verwendet
	 * nur ein dummer test...
	 * @param old
	 * @param actual
	 * @param next
	 * @return
	 */
	private Vector3f checkAngle(Vector3f old, Vector3f actual, Vector3f next){
		
		Vector3f test = old.normalize();
		Vector3f test2 = actual.normalize();
		Vector3f test3 = next.normalize();
		// winkel zwischen alten und neuem vektor wird berrechnet und in grad umgerechnet
		float angle = test.angleBetween(test3);
		// ist winkel groesser als 45 grad und kleiner als 315 grad ist, so muss vektor rotiert werden
		
		if(angle>FastMath.PI/4f && angle<FastMath.PI*(7f/4f)){
			angle = FastMath.PI/6f;
		/*	float distanceOldtoNext = old.distance(next); //C
			float distanceOldtoActual = old.distance(actual); //A
			float distanceActualtoNext = FastMath.sqrt(distanceOldtoNext + distanceOldtoActual); //B
			
			float x = FastMath.sqrt(FastMath.pow(distanceOldtoNext, 2)+FastMath.pow(distanceActualtoNext, 2)+2*distanceOldtoActual*distanceActualtoNext*FastMath.cos(angle));
			float y = FastMath.sqrt(FastMath.pow(distanceOldtoNext, 2)+FastMath.pow(distanceOldtoActual, 2)+2*distanceOldtoActual*distanceActualtoNext*FastMath.cos(angle));;
			float z = test3.getZ();
			
			test3.setX(x);
			test3.setY(y);
			test3.setZ(z);
			
			test3 = test3.normalize();
		*/
			float oldX = test3.getX();
			float oldY = test3.getY();
			test3.setX((test3.getX()*FastMath.cos(FastMath.PI/6f))+oldX);	
			test3.setY((test3.getY()*FastMath.sin(FastMath.PI/6f))+oldY);
		}
		
		return test3;
	}
}
