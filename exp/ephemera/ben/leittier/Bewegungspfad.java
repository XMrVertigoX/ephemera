package ephemera.ben.leittier;

import com.jme.app.SimpleGame;
import com.jme.curve.CurveController;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;

/**
 * Diese Klasse erstellt und kontrolliert den Bewegungspfad 
 * @author Benedikt Schuld, Gudrun Wagner
 *
 */


public class Bewegungspfad extends SimpleGame{

    private Leittier lt;
    private BSpline bs;
    private CurveController cc;
    private Vector3f position;
    
    
    public Bewegungspfad(Vector3f[] data){
    	
    	   BSpline bs = new BSpline("bspline",data);
           lt = new Leittier();
           CurveController cc = new CurveController(bs,lt);   
           lt.addController(cc);
           cc.setRepeatType(Controller.RT_WRAP);
           cc.setSpeed(.1f);
    }

    /**
     * Diese Methode fuegt das Leittier dem rootNode an
     * 
     */
     
    protected void simpleInitGame() {
        rootNode.attachChild(lt);
        rootNode.attachChild(lt.s); //Hilfskugel bitte entfernen wenn Scharm der Bewegungskurve folgt!
    }

    /**
     * Diese Methode aktualisiert die Position des Leittiers auf dem Bewegungspfad 
     * 
     */

    protected void simpleUpdate(){
 
    	position = lt.getLocalTranslation(); 
    	
    	lt.s.setLocalTranslation(position); //Hilfskugel bitte entfernen wenn Scharm der Bewegungskurve folgt!
    }

    
    /**
     * Diese Methode gibt die aktuelle Position des Leittiers auf dem Bewegungspfad zurueck
     * @return position
     */
    
    public Vector3f getPosition(){
    	return position;
    }

    
    public static void main(String[] args) {
    	
    	Vector3f[] data = new Vector3f[7];

	    data[0] = new Vector3f(5f,5f,0f);
	    data[1] = new Vector3f(10f,0f,5f);
	    data[2] = new Vector3f(5f,-5f,10f);
	    data[3] = new Vector3f(0f,0f,15f);
        data[4] = new Vector3f(-5f,5f,10f);
        data[5] = new Vector3f(-10f,0f,5f);
	    data[6] = new Vector3f(-5f,-5f,0f);
       
    
        Bewegungspfad bf = new Bewegungspfad(data);
        bf.start();
    }

}
