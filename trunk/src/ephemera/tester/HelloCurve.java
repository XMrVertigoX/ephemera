package ephemera.tester;

import com.jme.app.SimpleGame;
import com.jme.curve.BezierCurve;
import com.jme.curve.CurveController;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cone;


public class HelloCurve extends SimpleGame{

	BezierCurve bc;
	CurveController cc;
	Box b;
	
	public static void main(String[] args) {
		new HelloCurve().start();
	}
	@Override
	protected void simpleInitGame() {
		Vector3f[] data = {	new Vector3f(0,0,0),
							new Vector3f(40,0,0)	};
		
		bc =new BezierCurve("bezier",data);
		b = new Box("Box",new Vector3f(0,0,0),new Vector3f(2,2,2));
		
		cc = new CurveController(bc,b);
		cc.setRepeatType(Controller.RT_CYCLE);
		b.addController(cc);
		cc.setSpeed(.5f);
		
		
Cone c = new Cone("Cone",20,20,2f,4f);
		
		//c.setLocalTranslation(new Vector3f(1,0,0));
		c.getLocalRotation().fromAngleNormalAxis(-90, Vector3f.UNIT_X);
		
		//c.getLocalRotation().fromAngleNormalAxis(0, Vector3f.UNIT_Y);
		rootNode.attachChild(c);
		
		rootNode.attachChild(b);
	}
	protected void simpleUpdate(){
		System.out.println(b.getLocalTranslation());
	}

	
}
