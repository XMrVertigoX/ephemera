package ephemera.model;

import java.net.URL;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.*;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;
import com.jme.bounding.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 * @author Gudrun Wagner
 *
 */
public class Obstacles{
	
	/**
	 * erstellt ein Hindernis; Busch
	 * 
	 * @param posX 
	 * @param posY 
	 * @param posZ 
	 * @param node Knoten, an den das Objekt angehaengt wird.
	 * @param obs  ArrayListe, zu der das Objekt hinzugefügt wird.
	 */
	public static void createShrub1(float posX, float posY, float posZ, Node node, ArrayList<Spatial> obs){
		
		Node objectNode = new Node();
		Node leavesNode = new Node();
		
		Quaternion rotTrunk = new Quaternion();
		Quaternion rotLeaves = new Quaternion();
		
		
		//Stamm
		Cylinder trunk = new Cylinder("Trunk", 5, 5, .25f, 2f);
		rotTrunk.fromAngleAxis(-FastMath.PI/2f, Vector3f.UNIT_X);
		trunk.setLocalRotation(rotTrunk);
		
		//Laub
		rotLeaves.fromAngleAxis(-FastMath.PI/2f, Vector3f.UNIT_X);
		Vector3f scale = new Vector3f(1, 1, .7f);
		
		Torus leaves1 = new Torus ("Leaves 1", 10,10, 1, 1);
		leaves1.setLocalScale(scale);
		leaves1.setLocalRotation(rotLeaves);
			
		Torus leaves2 = new Torus("Leaves 2", 10, 10, .75f, .75f);
		leaves2.setLocalScale(scale);
		leaves2.setLocalRotation(rotLeaves);
		leaves2.setLocalTranslation(0, .75f, 0);
		
		Torus leaves3 = new Torus("Leaves 3", 10, 10, .52f, .52f);
		leaves3.setLocalScale(scale);
		leaves3.setLocalRotation(rotLeaves);
		leaves3.setLocalTranslation(0, 1.35f, 0);
		
		Sphere leaves4 = new Sphere("Leaves 4", 10, 10, .65f);
		leaves4.setLocalScale(new Vector3f(1, .7f, 1));
		leaves4.setLocalTranslation(0, 1.7f, 0);
			
		//Teilknoten
		leavesNode.attachChild(leaves1);
		leavesNode.attachChild(leaves2);
		leavesNode.attachChild(leaves3);
		leavesNode.attachChild(leaves4);
		
		//Texturen
		setTexture("ephemera/texture/objects/stamm.jpg", trunk);
		setTexture("ephemera/texture/objects/busch1.jpg", leavesNode);
	
		//Objekt Knoten
		objectNode.attachChild(leavesNode);
		objectNode.attachChild(trunk);
	
		//Groeße anpassen
		objectNode.setLocalScale(60);
		
		//BoundingBox
		objectNode.setModelBound(new BoundingSphere());
		objectNode.updateModelBound();
		
		//Position
		objectNode.setLocalTranslation(posX, posY+1f, posZ);
		
		//an uebergebene Weltinstanzen haengen
		obs.add(objectNode);
		node.attachChild(objectNode);
	}
	
	/**
	 * erstellt Hinderniss; Busch
	 * 
	 * @param posX
	 * @param posY
	 * @param posZ
	 * @param node	Knoten, an das Objekt angehängt wird.
	 * @param obs	ArrayListe, zu der das Objekt hinzugefügt wird.
	 */
	public static void createShrub2(float posX, float posY, float posZ, Node node, ArrayList<Spatial> obs){
		
		Node rotNode = new Node();
		Node objectNode = new Node();
		
		Vector3f scale = new Vector3f(1, 3.5f, 1);
		
		Quaternion rot45Z = new Quaternion();
		Quaternion rotM45Z = new Quaternion();
		Quaternion rot45X = new Quaternion();
		Quaternion rotM45X = new Quaternion();
		Quaternion rot90Z = new Quaternion();
		Quaternion rotM90Z = new Quaternion();
		Quaternion rot90X = new Quaternion();
		Quaternion rotM90X = new Quaternion();
		Quaternion rot45Y = new Quaternion();
		
		
		//Laub
		Sphere leaves1 = new Sphere ("Leaves 1", 10, 10, .3f);
		leaves1.setLocalScale(scale);
		rot90X.fromAngleAxis(FastMath.PI/2f, Vector3f.UNIT_X);
		leaves1.setLocalRotation(rot90X);
		leaves1.setLocalTranslation(new Vector3f(0, -.525f, 0.5f));
		
		Sphere leaves2 = new Sphere ("Leaves 2", 10, 10, .3f);
		leaves2.setLocalScale(scale);
		rotM90X.fromAngleAxis(-FastMath.PI/2f, Vector3f.UNIT_X);
		leaves2.setLocalRotation(rotM90X);
		leaves2.setLocalTranslation(new Vector3f(0, -.525f, -0.5f));
			
		Sphere leaves3 = new Sphere ("Leaves 3", 10, 10, .3f);
		leaves3.setLocalScale(scale);
		rot90Z.fromAngleAxis(FastMath.PI/2f, Vector3f.UNIT_Z);
		leaves3.setLocalRotation(rot90Z);
		leaves3.setLocalTranslation(new Vector3f(0.5f, -.525f, 0));
		
		Sphere leaves4 = new Sphere ("Leaves 4", 10, 10, .3f);
		leaves4.setLocalScale(scale);
		rotM90Z.fromAngleAxis(-FastMath.PI/2f, Vector3f.UNIT_Z);
		leaves4.setLocalRotation(rotM90Z);
		leaves4.setLocalTranslation(new Vector3f(-0.5f, -.525f, 0));
		
		Sphere leaves5 = new Sphere ("Leaves 5", 10, 10, .255f);
		leaves5.setLocalScale(scale);
		rot45X.fromAngleAxis(FastMath.PI/4f, Vector3f.UNIT_X);
		leaves5.setLocalRotation(rot45X);
		leaves5.setLocalTranslation(new Vector3f(0, 0, 0.73f));
		
		Sphere leaves6 = new Sphere ("Leaves 6", 10, 10, .255f);
		leaves6.setLocalScale(scale);
		rotM45X.fromAngleAxis(-FastMath.PI/4f, Vector3f.UNIT_X);
		leaves6.setLocalRotation(rotM45X);
		leaves6.setLocalTranslation(new Vector3f(0, 0, -0.73f));
			
		Sphere leaves7 = new Sphere ("Leaves 7", 10, 10, .255f);
		leaves7.setLocalScale(scale);
		rot45Z.fromAngleAxis(FastMath.PI/4f, Vector3f.UNIT_Z);
		leaves7.setLocalRotation(rot45Z);
		leaves7.setLocalTranslation(new Vector3f(-0.73f, 0, 0));
		
		Sphere leaves8 = new Sphere ("Leaves 8", 10, 10, .255f);
		leaves8.setLocalScale(scale);
		rotM45Z.fromAngleAxis(-FastMath.PI/4f, Vector3f.UNIT_Z);
		leaves8.setLocalRotation(rotM45Z);
		leaves8.setLocalTranslation(new Vector3f(0.73f, 0, 0));
		
		Sphere leaves9 = new Sphere ("Leaves 9", 10, 10, .3f);
		leaves9.setLocalScale(scale);

		//Teilknoten
		rotNode.attachChild(leaves5);
		rotNode.attachChild(leaves6);
		rotNode.attachChild(leaves7);
		rotNode.attachChild(leaves8);
		
		rot45Y.fromAngleAxis(FastMath.PI/4f, Vector3f.UNIT_Y);
		rotNode.setLocalRotation(rot45Y);
		
		//Objekt Knoten
		objectNode.attachChild(leaves1);
		objectNode.attachChild(leaves2);
		objectNode.attachChild(leaves3);
		objectNode.attachChild(leaves4);
		objectNode.attachChild(leaves9);
		objectNode.attachChild(rotNode);
		
		//Textur
		setTexture("ephemera/texture/objects/busch2.jpg", objectNode);
		
		//Groeße anpassen
		objectNode.setLocalScale(60);
		
		//BoundingBox
		objectNode.setModelBound(new BoundingSphere());
		objectNode.updateModelBound();
		
		//Position
		objectNode.setLocalTranslation(posX, posY+0.825f, posZ); 
		
		//an uebergebene Weltinstanzen haengen
		obs.add(objectNode);
		node.attachChild(objectNode);
	}
	
	/**
	 * erstellt Hindernis; Baum
	 * 
	 * @param posX
	 * @param posY
	 * @param posZ
	 * @param node	Knoten, an das Objekt angehängt wird.
	 * @param obs	ArrayListe, zu der das Objekt hinzugefügt wird.
	 */
	public static void createTree(float posX, float posY, float posZ, Node node, List<Spatial> obs){
		
		Node objectNode = new Node();
		Node trunkNode = new Node();
		Node leavesNode = new Node();
		
		Quaternion rotTrunk = new Quaternion();
		Quaternion rotBranchX = new Quaternion();
		Quaternion rotBranchY = new Quaternion();
		Quaternion rotLeaves = new Quaternion();
		Quaternion rotLeaves2 = new Quaternion();
		
		//Stamm
		Cylinder trunk = new Cylinder("Trunk", 5, 5, .5f, 7f);
		rotTrunk.fromAngleAxis(-FastMath.PI/2f, Vector3f.UNIT_X);
		trunk.setLocalRotation(rotTrunk);
		
		//Ast
		Cylinder branch = new Cylinder ("Branch", 5, 5, .1f, 2.5f);
		rotBranchX.fromAngleAxis(FastMath.PI/6f, Vector3f.UNIT_X);
		rotBranchY.fromAngleAxis(FastMath.PI/2f, Vector3f.UNIT_Y);
		branch.setLocalRotation(rotBranchX);
		branch.setLocalTranslation(0, -.5f, -.65f);
		
		//Laub
		Sphere leaves = new Sphere ("Leaves", 10, 10, 3.f);
		rotLeaves.fromAngleAxis(FastMath.PI/2, Vector3f.UNIT_X);
		leaves.setLocalRotation(rotLeaves);
		leaves.setLocalTranslation(0, 5f, 0);
		
		Sphere leaves2 = new Sphere("Leaves 2", 10, 10, .6f);
		rotLeaves2.fromAngleAxis(FastMath.PI/4, Vector3f.UNIT_X);
		leaves2.setLocalRotation(rotLeaves2);
		leaves2.setLocalTranslation(0, .4f, -2f);
		
		//Teilknoten
		trunkNode.attachChild(trunk);
		trunkNode.attachChild(branch);
		leavesNode.attachChild(leaves);
		leavesNode.attachChild(leaves2);
		
		//Texturen
		setTexture("ephemera/texture/objects/laub.jpg", leavesNode);
		setTexture("ephemera/texture/objects/stamm.jpg", trunkNode);
		
		//Objekt Knoten
		objectNode.attachChild(leavesNode);
		objectNode.attachChild(trunkNode);
		
		//Groeße anpassen
		objectNode.setLocalScale(60);
		
		//BoundingBox
		objectNode.setModelBound(new BoundingSphere());
		objectNode.updateModelBound();
		
		//Position
		objectNode.setLocalTranslation(posX, posY+3.5f, posZ); 
		
		// an uebergebene Weltinstanzen haengen
		obs.add(objectNode);
		node.attachChild(objectNode);
	}

	/**
	 * erstellt ein Hindernis; Tanne
	 * 
	 * @param posX	
	 * @param pos	
	 * @param posZ	
	 * @param node	Knoten, an den das Objekt angehängt wird
	 * @param obs	ArrayListe, zu der das Objekt hinzugefügt wird.
	 */
	public static void createFirStraight(float posX, float posY, float posZ, Node node, ArrayList<Spatial> obs){
		
		Node leavesNode = new Node();
		Node objectNode = new Node();
		
		Quaternion rotTrunk = new Quaternion();
		
		//Stamm
		Cylinder trunk = new Cylinder("Trunk", 25, 25, .65f, 2);
		rotTrunk.fromAngleAxis(-FastMath.PI/2f, Vector3f.UNIT_X);
		trunk.setLocalRotation(rotTrunk);
			
		//Laub
		Pyramid leaves1 = new Pyramid("Leaves 1", 5, 5);
		leaves1.setLocalTranslation(0f, 2.75f, 0f);
		
		Pyramid leaves2 = new Pyramid("Leaves 2", 4.5f, 4.5f);
		leaves2.setLocalTranslation(0f, 3.25f, 0f);
		
		Pyramid leaves3 = new Pyramid("Leaves 3", 4f, 4);
		leaves3.setLocalTranslation(0f, 4f, 0f);
		
		Pyramid leaves4 = new Pyramid("Leaves 4", 3.5f, 3.5f);
		leaves4.setLocalTranslation(0f, 4.75f, 0f);
	
		Pyramid leaves5 = new Pyramid("Leaves 5", 3f, 3);
		leaves5.setLocalTranslation(0f, 5.5f, 0f);
		
		Pyramid leaves6 = new Pyramid("TLeaves 6", 2.5f, 2.5f);
		leaves6.setLocalTranslation(0f, 6.25f, 0f);
	
		//Teilknoten
		leavesNode.attachChild(leaves1);
		leavesNode.attachChild(leaves2);
		leavesNode.attachChild(leaves3);
		leavesNode.attachChild(leaves5);
		leavesNode.attachChild(leaves4);
		leavesNode.attachChild(leaves6);
		
		//Texturen
		setTexture("ephemera/texture/objects/stamm.jpg", trunk);
		setTexture("ephemera/texture/objects/tanne.jpg", leavesNode);		
		
		//Objekt Knoten
		objectNode.attachChild(trunk);
		objectNode.attachChild(leavesNode);
		
		//Groeße anpassen
		objectNode.setLocalScale(60);
		
		//BoundingBox
		objectNode.setModelBound(new BoundingSphere());
		objectNode.updateModelBound();
		
		//Position
		objectNode.setLocalTranslation(posX, posY+1f, posZ); 
		
		// an uebergebene Weltinstanzen haengen
		obs.add(objectNode);
		node.attachChild(objectNode);
	}

	/**
	 * erstellt Hindernis; Tanne
	 * 
	 * @param posX
	 * @param posY
	 * @param posZ
	 * @param node	Knoten, an das Objekt angehängt wird.
	 * @param obs	ArrayListe, zu der das Objekt hinzugefügt wird.
	 */
	public static void createFirTwisted(float posX, float posY, float posZ, Node node, ArrayList<Spatial> obs){
		
		Node branchesNode = new Node();
		Node objectNode = new Node();
		Quaternion rotTrunk = new Quaternion();
		Quaternion rotBranches = new Quaternion();
		
		//Stamm
		Cylinder trunk = new Cylinder("Trunk", 5, 5, .65f, 2);
		rotTrunk.fromAngleAxis(-FastMath.PI/2f, Vector3f.UNIT_X);
		trunk.setLocalRotation(rotTrunk);
			
		//Laub
		Pyramid leaves1 = new Pyramid("Leaves 1", 5, 5);
		leaves1.setLocalTranslation(0f,2.75f,0f);
		
		Pyramid leaves2 = new Pyramid("Leaves 2", 4.5f, 4.5f);
		leaves2.setLocalTranslation(0f, 3.25f, 0f);
		rotBranches.fromAngleAxis(-FastMath.PI/4f, Vector3f.UNIT_Y);
		leaves2.setLocalRotation(rotBranches);
		
		Pyramid leaves3 = new Pyramid("Leaves 3", 4f, 4);
		leaves3.setLocalTranslation(0f, 4f, 0f);
		
		Pyramid leaves4 = new Pyramid("Leaves 4", 3.5f, 3.5f);
		leaves4.setLocalTranslation(0f, 4.75f, 0f);
		rotBranches.fromAngleAxis(-FastMath.PI/4f, Vector3f.UNIT_Y);
		leaves4.setLocalRotation(rotBranches);
		
		Pyramid leaves5 = new Pyramid("Leaves 5", 3f, 3);
		leaves5.setLocalTranslation(0f, 5.5f, 0f);
		
		Pyramid leaves6 = new Pyramid("Leaves 6", 2.5f, 2.5f);
		leaves6.setLocalTranslation(0f, 6.25f, 0f);
		rotBranches.fromAngleAxis(-FastMath.PI/4f, Vector3f.UNIT_Y);
		leaves6.setLocalRotation(rotBranches);
	
		//Teilknoten
		branchesNode.attachChild(leaves1);
		branchesNode.attachChild(leaves2);
		branchesNode.attachChild(leaves3);
		branchesNode.attachChild(leaves5);
		branchesNode.attachChild(leaves4);
		branchesNode.attachChild(leaves6);
		
		//Texturen
		setTexture("ephemera/texture/objects/stamm.jpg", trunk);
		setTexture("ephemera/texture/objects/tanne.jpg", branchesNode);
		
		//Objekt Knoten
		objectNode.attachChild(trunk);
		objectNode.attachChild(branchesNode);
		
		//Groeße anpassen
		objectNode.setLocalScale(60);
		
		//BoundingBox
		objectNode.setModelBound(new BoundingSphere());
		objectNode.updateModelBound();
		
		//Position
		objectNode.setLocalTranslation(posX, posY+1f, posZ); //Y-Position noch festlegen
		
		//an uebergebene Weltinstanzen haengen
		obs.add(objectNode);
		node.attachChild(objectNode);
	}
	
	public static void createHouse(float posX, float posY, float posZ, Vector3f size, Node node, ArrayList<Spatial> obs){
		
			// Erstelle Objekt
			TriMesh box = new Box("Box",new Vector3f(0,0,0),new Vector3f(size));
			box.setModelBound(new BoundingBox());
			box.updateModelBound();
	
			// Verschiebe Objekt
			box.setLocalTranslation(new Vector3f(posX,posY,posZ));
			
			// Textur
			setTexture("ephemera/texture/objects/5016.jpg",box);
			
			//an uebergebene Weltinstabzen haengen
			obs.add(box);
			node.attachChild(box);
	}
	
	
	
	
	/**
	 * Texturiert ein Objekt
	 * 
	 * @param resource	Pfad zur Textur 
	 * @param object	Object, das Texturiert werden soll
	 */
	private static void setTexture(String resource, TriMesh object){
		URL texLoc;
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		texLoc = Obstacles.class. getClassLoader (). getResource(resource);
		TextureState tsPlant = display. getRenderer (). createTextureState ();
		Texture texPlant = TextureManager.loadTexture (texLoc, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
		tsPlant.setTexture(texPlant);
		object.setRenderState(tsPlant);
	}

	/**
	 * Texturiert ein Objekt
	 * 
	 * @param resource	Pfad zur Textur 
	 * @param object	Object, das Texturiert werden soll
	 */
	private static void setTexture(String resource, Node object){
		URL texLoc;
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		texLoc = Obstacles.class. getClassLoader (). getResource(resource);
		TextureState tsPlant = display. getRenderer (). createTextureState ();
		Texture texPlant = TextureManager.loadTexture (texLoc, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
		tsPlant.setTexture(texPlant);
		object.setRenderState(tsPlant);
	}
}
