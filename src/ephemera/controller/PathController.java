package ephemera.controller;
/**
 * PathController 2011 by ...
 */




import com.jme.curve.CurveController;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;

import ephemera.model.BSpline;
import ephemera.model.Leader;
import ephemera.model.Rules;



public class PathController{
	
	private static final long serialVersionUID = 1L;
	
	private CurveController curveController;
	private Leader leader;
	private Node path;
	private Rules rules;
	
	/**
	 * erzeugt BSpline als Bewegungspfad und laesst das Leittier darauf laufen.
	 * Die Geschwindigkeit wird in den uebergebenen Regeln festgelegt. 
	 * @param rules
	 */
	public PathController(Rules rules){	
		// erstelle Pfad
		this.rules = rules;
		path = new Node("Pfad");
		Vector3f[] data = new Vector3f[5];
		data[0] = new Vector3f(0f,0f,-1000f);
		data[1] = new Vector3f(1000f,0f,0f);
		data[2] = new Vector3f(0f,0f,1000f);
		data[3] = new Vector3f(-1000f,0f,0f);
		data[4] = new Vector3f(0f,0f,-1000f);
		BSpline bp = new BSpline("Bewegungspfad",data);
		
		// erstelle das Leittier 
		leader = new Leader();
				
		// Kurvencontroller erstellen 
		curveController = new CurveController(bp,leader.getGeometry()); //wenn im Leitttier die Kugel entfernt wird, hier ".s" loeschen
		curveController.setRepeatType(Controller.RT_WRAP);
		
		// Leittier an Kurve anmelden 
		leader.getGeometry().addController(curveController); //wenn im Leitttier die Kugel entfernt wird, hier ".s" loeschen
		
		// Geschwindigkeit des Leittieres
		curveController.setSpeed(rules.getLeittierSpeed());
		path.attachChild(leader.getGeometry());	 //wenn im Leitttier die Kugel entfernt wird, hier ".s" loeschen
	}
	
	
	/**
	 * gibt Pfad des Leittieres zurueck
	 * @return pfad
	 */
	public Node getLeaderNode(){
		return path;
	}
	
	
	/**
	 * gibt Position des Leittieres zurueck
	 * @return Vector3f 
	 */
	public Vector3f getPosition(){
		return leader.getLocalTranslation();
	}
	public Leader getLeader(){ return leader;}
			
}
