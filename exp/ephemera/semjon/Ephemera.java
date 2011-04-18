package ephemera.semjon;

import java.util.ArrayList;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;

public class Ephemera {

	public static int count;
	Vector3f 	acc,
				vel;
	
	Node 		node;
	long		age;
	float 		maxspeed,
				maxforce,
				coh_weight=0.0001f,
				ali_weight=0.0001f,
				sep_weight=0.0005f,
				desiredSeparation=15.0f,
				neighborDistance=25.0f;
	
	
	public Ephemera(Vector3f pos){
		acc = new Vector3f(0,0,0);
		vel = new Vector3f(0,0,0);
		age = System.currentTimeMillis();
		maxspeed = 10;
		maxforce = 1;
		
		node= new Node("Fliege_"+count);
		Box b = new Box(""+count,new Vector3f(0,0,0),new Vector3f(1,1,1));
		b.setModelBound(new BoundingBox());
		node.attachChild(b);
		node.setLocalTranslation(pos);
		count++;
	}
	Node getNode(){	
		return node;
	}
	public Vector3f getPos(){
		return this.node.getLocalTranslation();
	}
	
	
	
	void run(ArrayList<Ephemera> boids) {
	    flock(boids);
	    update();
	    //borders(); auf eventuelle Grenzen des Systems reagieren
	    //render();  auf Anzeigepanel darstellen
	}
	
	// Calculate behavior  
	void flock(ArrayList<Ephemera> flies) {
	    Vector3f sep  = separate(flies);   // Separation
	    Vector3f ali = align(flies);
	    Vector3f coh = cohesion(flies);
	     
	    // Arbitrarily weight these forces
	    sep.mult(sep_weight, sep);
	    ali.mult(ali_weight, ali);
	    coh.mult(coh_weight, coh);
	    
	    // Add the force vectors to acceleration
	    acc.add(sep,acc);
	    acc.add(ali,acc);
	    acc.add(coh,acc);
	}
	
	// Update the Position of the Fly
	void update() {
	    // Update velocity
	    vel.add(acc,vel);
	    // Limit speed
	    //vel.limit(maxspeed);
	    //pos.add(vel);
	    // Reset accelertion to 0 each cycle
		
		//float x= (float)(.01f*Math.random());
		//float y= 0;//(float)(.02f*Math.random());
		//float z= 0;//(float)(.03f*Math.random());
		//Vector3f p = new Vector3f(x,y,z);
		
	    node.getLocalTranslation().addLocal(acc);
	    
	    
	    acc.mult(0);
	  }

	
	// A method that calculates a steering vector towards a target
	// Takes a second argument, if true, it slows down as it approaches the target
	Vector3f steer(Vector3f target, boolean slowdown) {
		Vector3f steer;  // The steering vector
		Vector3f desired = target.subtract(getPos());  // A vector pointing from the location to the target
		float d = desired.length(); // Distance from the target is the magnitude of the vector
	    // If the distance is greater than 0, calc steering (otherwise return zero vector)
	    if (d > 0) {
	      // Normalize desired
	      desired = desired.normalize();
	      // Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
	      if ((slowdown) && (d < 100.0)) desired=desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
	      else desired = desired.mult(maxspeed);
	      // Steering = Desired minus Velocity
	      steer = desired.subtract(vel);
	      //steer.limit(maxforce);  // Limit to maximum steering force
	    } 
	    else {
	      steer = new Vector3f(0,0,0);
	    }
	    return steer;
	}
	
	// Separation
	// Method checks for nearby boids and steers away
	Vector3f separate (ArrayList<Ephemera> flies) {
     Vector3f steer = new Vector3f(0,0,0);
    int count = 0;
    // For every boid in the system, check if it's too close
	for (int i = 0 ; i < flies.size(); i++) {
	  Ephemera other = flies.get(i);
	  float d = getPos().distance(other.getPos());
	  // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
	  if ((d > 0) && (d < desiredSeparation)) {
	    // Calculate vector pointing away from neighbor
		Vector3f diff = getPos().subtract(other.getPos());
		diff = diff.normalize();
		
		diff = diff.divide(d);        // Weight by distance
		steer = steer.add(diff);
		
		count++;            // Keep track of how many
		return diff;
	  }
	}
	// Average -- divide by how many
	if (count > 0) {
	  steer = steer.divide((float)count);
	}
	
	// As long as the vector is greater than 0
	if (steer.length() > 0) {
	  // Implement Reynolds: Steering = Desired - Velocity
	  steer = steer.normalize();
	  steer = steer.mult(maxspeed);
	  steer = steer.subtract(vel);
	  //steer.limit(maxforce);
	    }
	
	return steer;
  }
	
	// Alignment
	// For every nearby boid in the system, calculate the average velocity
	Vector3f align (ArrayList<Ephemera> flies) {
     Vector3f steer = new Vector3f(0,0,0);
    int count = 0;
    for (int i = 0 ; i < flies.size(); i++) {
      Ephemera other = flies.get(i);
      float d = getPos().distance(other.getPos());
      if ((d > 0) && (d < neighborDistance)) {
        steer = steer.add(other.vel);
        count++;
      }
    }
    if (count > 0) {
      steer = steer.divide((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.length() > 0) {
      // Implement Reynolds: Steering = Desired - Velocity
      steer = steer.normalize();
      steer = steer.mult(maxspeed);
      steer = steer.subtract(vel);
      //steer.limit(maxforce);
    }
    return steer;
  }
	
	// Cohesion
	// For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
	Vector3f cohesion (ArrayList<Ephemera> flies) {
		Vector3f sum = new Vector3f(0,0,0);
	
		
	  	int count = 0;
		for (int i = 0 ; i < flies.size(); i++) {
			Ephemera other = flies.get(i);
		    float d = getPos().distance(other.getPos());
		    if ((d > 0) && (d < neighborDistance)) {
		    	sum.add(other.getPos(),sum); // Add location
		        count++;
		    }
		 }
		 if (count > 0) {
			 sum.mult(1/(float)count,sum);
		     return steer(sum,true);  // Steer towards the location
		 }
		 
		 return sum;
	}
		
	float getAge(){ 
		return (System.currentTimeMillis()-age)/1000.0f;
	}
	
}
