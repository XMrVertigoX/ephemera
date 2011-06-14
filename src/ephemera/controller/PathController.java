package ephemera.controller;

import com.jme.curve.CurveController;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;

import ephemera.model.BSpline;
import ephemera.model.Leader;
import ephemera.model.Rules;

/**
 * Diese Klasse erstellt einen Bewegungspfad und laesst darauf ein Leittier laufen.
 * @author Benedikt Schuld, Gudrun Wagner
 *
 */
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
		Vector3f[] data = new Vector3f[6];
		data[0] = new Vector3f(1000f,-200f,-2000f);
		data[1] = new Vector3f(2000f,0f,0f);
		data[2] = new Vector3f(1000f,200f,2000f);
		data[3] = new Vector3f(-1000f,0f,-2000f);
		data[4] = new Vector3f(-2000f,-200f,0f);
		data[5] = new Vector3f(-1000f,0f,2000f);
		BSpline bp = new BSpline("Bewegungspfad",data);
		
		// erstelle das Leittier 
		leader = new Leader();
				
		// Kurvencontroller erstellen 
		curveController = new CurveController(bp,leader.getGeometry()); 
		curveController.setRepeatType(Controller.RT_WRAP);
		
		// Leittier an Kurve anmelden 
		leader.getGeometry().addController(curveController); 
		
		// Geschwindigkeit des Leittieres
		curveController.setSpeed(this.rules.getLeittierSpeed());
		path.attachChild(leader.getGeometry());	 
	}
	
	
	/**
	 * @return Der Pfad des Leittieres.
	 */
	public Node getLeaderNode(){
		return path;
	}
	
	
	/**
	 * @return Die Position des Leittieres. 
	 */
	public Vector3f getPosition(){
		return leader.getLocalTranslation();
	}
	
	public Leader getLeader(){ return leader;}
			
}
