package ephemera.model;

import com.jme.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * 
 * @author Gudrun Wagner, Benedikt Schuld
 *
 */
public final class BSpline extends Curve {

	private static final long serialVersionUID = 1L;
	
	int numOfControlPoints;
	Vector3f[] data;
	int order;
	float[] nodeVector;
	String name;
	Vector3f position; 
	
	/**
	 * Konstruktor, erzeugt B-Spline-Kurve
	 * 
	 * @param name Name der Kurve
	 * @param data Kontrollpunkte-Array
	 */
	public BSpline(String name,Vector3f[] data){
		super(name);
		this.data = data;
		numOfControlPoints = data.length;
		order = 3;
//		position = new Vector3f(0f,0f,0f);
		position = data[0]; 
		nodeVector = createNodeVector(order,data.length);
	}
	
	/**
	 * berechnet den Knotenvektor der Kurve und gibt ihn zurück
	 * 
	 * @param order Ordnung der Kurve
	 * @param numOfPoints Anzahl der Kontrollpunkte
	 * @return float[] Knotenvektor
	 */
	private static float[] createNodeVector(int order, int numOfPoints){
		
		int length = order+numOfPoints;
		float[] nodeVector = new float[length];
		float distance = 1f/(float)(length-1f); 
		float sum = 0f;
		
		for(int i=0;i<length;i++){
			nodeVector[i] = sum;
			sum+= distance;
		}
		
		return nodeVector;
	}
	
	/**
	 * löst die Basisfunktionen der B-Spline-Kurve rekursiv
	 * 
	 * @param nodeVector Knotenvektor der B-Spline-Kurve
	 * @param t Parameter der B-Spline-Kurve
	 * @param order Ordnung der Kurve
	 * @param counter Zählvariable
	 * @return float Lösung
	 */
	private static float basisfunction(float[] nodeVector, float t, int order, int counter) {
		
		if(order==1){
			
			if((nodeVector[counter]<=t)&& (t<=nodeVector[counter+1])){
				return 1f;
			}
			
			else{
				return 0f;
			}
			
		}
		
		else{
			return ((t-nodeVector[counter])/(nodeVector[counter+order-1]-nodeVector[counter])*basisfunction(nodeVector,t,order-1,counter))
					+((nodeVector[counter+order]-t)/(nodeVector[counter+order]-nodeVector[counter+1])*basisfunction(nodeVector,t,order-1,counter+1));
		}
   }
	
	/**
	 * gibt den Punkt der Kurve zur Zeit t zurück
	 * 
	 * @param t 
	 * @return Vector3f Punkt
	 */
	public Vector3f getPoint(float t){
		
		Vector3f sum = new Vector3f(0,0,0);
		Vector3f aux;
		
		for(int i=0;i<data.length;i++){
		
			float basis = basisfunction(nodeVector,t,order,i);
			aux = data[i].mult(basis);
			
			sum = sum.add(aux);
		}
		
		position = sum;
		
		return sum;
	}
	
	
	/**
	 * Methode der Elternklasse Curve; hier unabhängig von arg1 implementiert 
	 */
	public Vector3f getPoint(float arg0, Vector3f arg1) {
		
		return getPoint(arg0);
	}
	
	/**
	 * Methode der Elternklasse Curve; gibt Position zurück
	 * @return Vector3f Position
	 */
	public Vector3f getPoint(){
		return position;
	}
	
	/**
	 * Methode der Elternklasse Curve; wird nicht benötigt.
	 */
	public Matrix3f getOrientation(float arg0, float arg1) {
		
		return null;
	}

	/**
	 * Methode der Elternklasse Curve; wird nicht benötigt.
	 */
	public Matrix3f getOrientation(float arg0, float arg1, Vector3f arg2) {
		
		return null;
	}

	/**
	 * Methode der Elternklasse Curve; wird nicht benötigt.
	 */
	public void findCollisions(Spatial arg0, CollisionResults arg1, int arg2) {		

	}

	/**
	 * Methode der Elternklasse Curve; wird nicht benötigt.
	 */
	public boolean hasCollision(Spatial arg0, boolean arg1, int arg2) {
		
		return false;
	}

}
