package model;

import com.jme.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;


public final class BSpline extends Curve {

	int anzkontrollPunkte;
	Vector3f[] data;
	int ordnung;
	float[] knotenVec;
	String name;
	Vector3f position; 
	
	public BSpline(String name,Vector3f[] data){
		super(name);
		this.data = data;
		anzkontrollPunkte = data.length;
		ordnung = 3;
		position = new Vector3f(0f,0f,0f); 
		knotenVec = erzeugeKnotenVec(ordnung,data.length);
		
	}
	
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
	
	
	public Vector3f getPoint(float t){
		
		Vector3f sum = new Vector3f(0,0,0);
		Vector3f aux;
		
		for(int i=0;i<data.length;i++){
		
			float basis = basisfunktion(knotenVec,t,ordnung,i);
			aux = data[i].mult(basis);
			
			sum = sum.add(aux);
			
		}
		
		position = sum;
		
		return sum;
		
	}
	
	public Vector3f getPoint(float arg0, Vector3f arg1) {
		
		return getPoint(arg0);
	}
	
	public Vector3f getPoint(){
		return position;
	}
	
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
