package ephemera.model;

import java.util.ArrayList;
import com.jme.math.Vector3f;

public class Ephemera {

	Vector3f 	pos,
				acc,
				vel;
	
	long		age;
	float 		maxspeed,
				maxforce,
				coh_weight=1f,
				ali_weight=1f,
				sep_weight=1.5f;
	
	
	public Ephemera(Vector3f pos){
		this.pos = pos;
		acc = new Vector3f(0,0,0);
		vel = new Vector3f(0,0,0);
		age = System.currentTimeMillis();
		maxspeed = 1;
		maxforce = 1;
	}
	
	void run(ArrayList<Ephemera> boids) {
	    flock(boids);
	    update();
	    //borders(); auf eventuelle Grenzen des Systems reagieren
	    //render();  auf Anzeigepanel darstellen
	}
	
	// Calculate behavior  
	void flock(ArrayList<Ephemera> flies) {
	    Vector3f sep = separate(flies);   // Separation
	    Vector3f ali = align(flies);      // Alignment
	    Vector3f coh = cohesion(flies);   // Cohesion
	    // Arbitrarily weight these forces
	    sep.mult(sep_weight);
	    ali.mult(ali_weight);
	    coh.mult(coh_weight);
	    // Add the force vectors to acceleration
	    acc.add(sep);
	    acc.add(ali);
	    acc.add(coh);
	}
	
	// Update the Position of the Fly
	void update() {
	    // Update velocity
	    vel.add(acc);
	    // Limit speed
	    //vel.limit(maxspeed);
	    pos.add(vel);
	    // Reset accelertion to 0 each cycle
	    acc.mult(0);
	  }

	
	// A method that calculates a steering vector towards a target
	// Takes a second argument, if true, it slows down as it approaches the target
	Vector3f steer(Vector3f target, boolean slowdown) {
		Vector3f steer;  // The steering vector
		Vector3f desired = target.subtract(target,pos);  // A vector pointing from the location to the target
		float d = desired.length(); // Distance from the target is the magnitude of the vector
	    // If the distance is greater than 0, calc steering (otherwise return zero vector)
	    if (d > 0) {
	      // Normalize desired
	      desired.normalize();
	      // Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
	      if ((slowdown) && (d < 100.0)) desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
	      else desired.mult(maxspeed);
	      // Steering = Desired minus Velocity
	      steer = target.subtract(desired,vel);
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
    float desiredseparation = 15.0f;
    Vector3f steer = new Vector3f(0,0,0);
    int count = 0;
    // For every boid in the system, check if it's too close
	for (int i = 0 ; i < flies.size(); i++) {
	  Ephemera other = flies.get(i);
	  float d = pos.distance(other.pos);
	  // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
	  if ((d > 0) && (d < desiredseparation)) {
	    // Calculate vector pointing away from neighbor
		Vector3f diff = pos.subtract(other.pos);
		diff.normalize();
		diff.divide(d);        // Weight by distance
		steer.add(diff);
		count++;            // Keep track of how many
	  }
	}
	// Average -- divide by how many
	if (count > 0) {
	  steer.divide((float)count);
	}
	
	// As long as the vector is greater than 0
	if (steer.length() > 0) {
	  // Implement Reynolds: Steering = Desired - Velocity
	  steer.normalize();
	  steer.mult(maxspeed);
	  steer.subtract(vel);
	  //steer.limit(maxforce);
	    }
	    return steer;
  }
	
	// Alignment
	// For every nearby boid in the system, calculate the average velocity
	Vector3f align (ArrayList<Ephemera> flies) {
    float neighbordist = 25.0f;
    Vector3f steer = new Vector3f(0,0,0);
    int count = 0;
    for (int i = 0 ; i < flies.size(); i++) {
      Ephemera other = flies.get(i);
      float d = pos.distance(other.pos);
      if ((d > 0) && (d < neighbordist)) {
        steer.add(other.vel);
        count++;
      }
    }
    if (count > 0) {
      steer.divide((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.length() > 0) {
      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize();
      steer.mult(maxspeed);
      steer.subtract(vel);
      //steer.limit(maxforce);
    }
    return steer;
  }
	
	// Cohesion
	// For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
	Vector3f cohesion (ArrayList<Ephemera> flies) {
		float neighbordist = 25.0f;
		Vector3f sum = new Vector3f(0,0,0);   // Start with empty vector to accumulate all locations
		int count = 0;
		for (int i = 0 ; i < flies.size(); i++) {
			Ephemera other = flies.get(i);
		    float d = pos.distance(other.pos);
		    if ((d > 0) && (d < neighbordist)) {
		    	sum.add(other.pos); // Add location
		        count++;
		    }
		 }
		 if (count > 0) {
			 sum.divide((float)count);
		     return steer(sum,false);  // Steer towards the location
		 }
		 return sum;
	}
		
	float getAge(){ 
		return (System.currentTimeMillis()-age)/1000.0f;
	}
	
}
