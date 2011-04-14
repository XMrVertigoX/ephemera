package ephemera.controller;
import java.util.ArrayList;

import ephemera.model.*;
public class Swarm {
	private ArrayList<Ephemera> flies;
	
	public Swarm(){
		flies = new ArrayList<Ephemera>();
	}
	void addFly(Ephemera newbie){
		flies.add(newbie);
	}
}
