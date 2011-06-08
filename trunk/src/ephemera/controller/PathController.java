/**
 * PathController 2011 by ...
 */

package ephemera.controller;

import ephemera.model.BSpline;
import ephemera.model.Rules;
import ephemera.model.Leader;

import com.jme.curve.CurveController;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;


public class PathController{
	
	private CurveController curveControllerc;
	private Leader Leittier;
	private Node pfad;
	private Rules regeln;
	
	public PathController(Rules regeln){	
		// erstelle Pfad
		this.regeln=regeln;
		pfad = new Node("Pfad");
		Vector3f[] data = new Vector3f[5];
		data[0] = new Vector3f(0f,0f,-1000f);
		data[1] = new Vector3f(1000f,0f,0f);
		data[2] = new Vector3f(0f,0f,1000f);
		data[3] = new Vector3f(-1000f,0f,0f);
		data[4] = new Vector3f(0f,0f,-1000f);
		BSpline bp = new BSpline("Bewegungspfad",data);
		
		// erstelle das Leittier 
		Leittier = new Leader();
		
		
		// Kurvencontroller erstellen 
		curveControllerc = new CurveController(bp,Leittier);
		curveControllerc.setRepeatType(Controller.RT_WRAP);
		
		// Leittier an Kurve anmelden 
		Leittier.addController(curveControllerc);
		
		// Geschwindigkeit des Leittieres
		curveControllerc.setSpeed(regeln.getLeittierSpeed());
		pfad.attachChild(Leittier);	
	}
	
	
	/**
	 * gibt Pfad des Leittieres zurueck
	 * @return pfad
	 */
	public Node getLeittier(){
		return pfad;
	}
	
	
	/**
	 * gibt Position des Leittieres zurueck
	 * @return Vector3f 
	 */
	public Vector3f getPosition(){
		return Leittier.getLocalTranslation();
	}
			
}
