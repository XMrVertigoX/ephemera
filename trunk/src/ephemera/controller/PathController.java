/**
 * PathController 2011 by ...
 */

package ephemera.controller;

import ephemera.model.BSpline;
import ephemera.model.RegelnFliege;

import com.jme.curve.CurveController;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;


public class PathController{
	
	private CurveController curveControllerc;
	private Sphere Leittier;
	private Node pfad;
	private RegelnFliege regeln;
	
	public PathController(RegelnFliege regeln){	
		// erstelle Pfad
		this.regeln=regeln;
		pfad = new Node("Pfad");
		Vector3f[] data = new Vector3f[5];
		data[0] = new Vector3f(0f,0f,-200f);
		data[1] = new Vector3f(200f,0f,0f);
		data[2] = new Vector3f(0f,0f,200f);
		data[3] = new Vector3f(-200f,0f,0f);
		data[4] = new Vector3f(0f,0f,-200f);
		BSpline bp = new BSpline("Bewegungspfad",data);
		
		// erstelle das Leittier 
		Leittier = new Sphere("Leittier",new Vector3f(0,0,0),25,25,5f);
		Leittier.setDefaultColor(ColorRGBA.green);
		
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
