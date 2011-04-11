/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ephemera.lib;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Circle;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;

/**
 * Started Date: Jul 20, 2004<br>
 * <br>
 * Simple HelloWorld program for jME<br>
 * 
 * Extended to create a rubik's cube and a ring with spheres
 * 
 * @author Jack Lindamood, Ioannis Chouklis
 */
public class HelloWorld extends SimpleGame {

	public static void main(String[] args) {
		HelloWorld app = new HelloWorld();
		app.setConfigShowMode(ConfigShowMode.ShowIfNoConfig); // Signal to show properties dialog
		app.start(); // Start the program
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	protected void simpleInitGame() {
		createRubiksCube(3, 0.87f);	//call the rubik's method
		//createRing(10, 1);	//call the ring method
	}
	
	/**
	 * Create a Rubik's cube
	 * @param number of cubes
	 * @param size of each individual cube
	 */
	private void createRubiksCube(int number, float size) {
		Node rubiksNode = new Node("Rubik");	// Create a node to attach all the rubik's geometry
		for (float x = 1; x <= number; x++) {
			for (float y = 1; y <= number; y++) {
				for (float z = 1; z <= number; z++) {
					Box b = new Box("Box: " + x + ", " + y + ", " + z, 
									new Vector3f(0, 0, 0), 
									new Vector3f(size, size, size)); // Make a box
					b.setLocalTranslation(x, y, z);	// place each box on the location x, y, z
					b.setModelBound(new BoundingBox());	// set a bounding box around each box
					rubiksNode.attachChild(b); // Put it in the scene graph
				}
			}
		}
		
		rubiksNode.setModelBound(new BoundingBox());	// set a bounding box for rubik's cube
		rootNode.attachChild(rubiksNode);	// attach rubik's cube to rootNode
	}
	
	/**
	 * Create a ring with spheres
	 * @param num of spheres
	 * @param radius of each individual sphere
	 */
	private void createRing(int num, float radius) {
		Node ringNode = new Node("Ring");	// create a node to attach all the ring's geometry
		Quaternion quaternion = new Quaternion();	// class to handle all the rotations
		quaternion.fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_X);	// rotate around the X axis 90 degrees
		Circle c = new Circle("Circle", 50, radius);	// create new circle
		c.setLocalRotation(quaternion);	// apply the rotation on the circle
		ringNode.attachChild(c);	//attach the circle to the ring node
		
		for (float j = 0; j < FastMath.PI * 2; j+=FastMath.PI/num) {
			Sphere s = new Sphere("Sphere at X:" + FastMath.sin(j)+ ", Z: " + FastMath.cos(j), 25, 25, 0.05f); // create a sphere
			s.setLocalTranslation(FastMath.sin(j), 0, FastMath.cos(j));	// translate the sphere at the border of the circle 
			ringNode.attachChild(s);	// attache the sphere to the ringnode
		}
		ringNode.setModelBound(new BoundingBox());	// set a bounding box for the ringnode
		ringNode.setLocalTranslation(5, 0, 0);	//translate the ringnod 5 units on the x axis
		rootNode.attachChild(ringNode);	// attach the ringnode to the rootNode
	}
}
		
