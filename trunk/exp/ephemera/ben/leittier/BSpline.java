package ephemera.ben.leittier;

import com.jme.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * Diese Klasse enthält die mathematische Logik fuer die B- Spline Berechnung
 * @author Benedikt Schuld, Gudrun Wagner
 *
 */


public class BSpline extends Curve {

	private Vector3f[] data;
	private int ordnung;
	private float[] knotenVec;
	private String name;
	
	
	public BSpline(String name,Vector3f[] data){
		super(name);
		this.data = data;
		ordnung = 3;
		knotenVec = erzeugeKnotenVec(ordnung,data.length);
		
		
	}
	
	/**
	 * Methode zur Erzeugung des Knotenvektors
	 * @param ordnung
	 * @param anzPoints
	 * @return knotenVec
	 */
	
	private static float[] erzeugeKnotenVec(int ordnung, int anzPoints){
		
		int laenge = ordnung+anzPoints;
		float[] knotenVec = new float[laenge];
		float abstand = 1f/(float)(laenge-1f); 
		float sum = 0f;
		
		for(int i=0;i<laenge;i++){
			
			knotenVec[i] = sum;
			sum+= abstand;
			
		}
		
		return knotenVec; 
		
	}
	
	/**
	 * rekursive Methode zur Berechnung der jeweiligen Spline- Koeffizienten 
	 * @param knotenVec
	 * @param t
	 * @param ordnung
	 * @param counter
	 * @return 0, 1, basisfunktion
	 */
	
	private static float basisfunktion(float[] knotenVec, float t, int ordnung, int counter) {
		
		if(ordnung==1){
			
			if((knotenVec[counter]<=t)&& (t<=knotenVec[counter+1])){
				return 1f;
				
			}else{
				return 0f;
			}
			
		}else{
			
			return ((t-knotenVec[counter])/(knotenVec[counter+ordnung-1]-knotenVec[counter])*basisfunktion(knotenVec,t,ordnung-1,counter))
					+((knotenVec[counter+ordnung]-t)/(knotenVec[counter+ordnung]-knotenVec[counter+1])*basisfunktion(knotenVec,t,ordnung-1,counter+1));
			
		}
    }
	
	/**
	 * Methode zur Berechnung der jeweiligen Position auf dem Bewegungspfad 
	 * @param t
	 * @return sum
	 */
	
	public Vector3f getPoint(float t){
		
		Vector3f sum = new Vector3f(0,0,0);
		Vector3f aux;
		
		
		for(int i=0;i<data.length;i++){
			
			float basis = basisfunktion(knotenVec,t,ordnung,i);
			
			aux = data[i].mult(basis);
			
			sum = sum.add(aux);
			
		}
		
		return sum;
		
	}
	
	/**
	 * Methode zur Berechnung der jeweiligen Position auf dem Bewegungspfad 
	 * @param arg0
	 * @param arg1
	 * @return getPoint
	 */
	
	
	public Vector3f getPoint(float arg0, Vector3f arg1) {
		
		return getPoint(arg0);
	}
	
	/**
	 * Diese Methoden sind von der Curve Klasse geerbt und werden nicht benötigt
	 * 
	 */
	
	
	public Matrix3f getOrientation(float arg0, float arg1) {
		
		return null;
	}


	public Matrix3f getOrientation(float arg0, float arg1, Vector3f arg2) {
		
		return null;
	}

	public void findCollisions(Spatial arg0, CollisionResults arg1, int arg2) {		

	}

	public boolean hasCollision(Spatial arg0, boolean arg1, int arg2) {
		
		return false;
	}
	
	public static void main(String[] args) {
		
		
		Vector3f[] data = new Vector3f[4];
		
		data[0] = new Vector3f(0,0,0);
		data[1] = new Vector3f(0.1f,0.2f,0.6f);
		data[2] = new Vector3f(0.4f,0.4f,0.8f);
		data[3] = new Vector3f(0.7f,0.8f,0.5f);
		
		BSpline b1 = new BSpline("test",data);
		
		

	}




}
