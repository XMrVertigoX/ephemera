package ephemera.loader.collada;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jmex.model.collada.ColladaImporter;

public class HelloCollada extends SimpleGame{

	static final Logger logger = Logger.getLogger(HelloCollada.class.getName());
	AnimationController ac;
    boolean boneOn = false;
    
	public static void main(String[] args) {
		Logger.getLogger("com.jme").setLevel(Level.WARNING);
		new HelloCollada().start();
	}
	
	@Override
	protected void simpleInitGame() {
		
		try 
    	{
	    	File fileToLoad = new File("fly3.dae");
	    	URI uri = fileToLoad.toURI();
	    	URL modelURL = uri.toURL();
	 
	    	InputStream source = modelURL.openStream();
	    	
	    	ColladaImporter.load(source, "loadedModel");
	    	
	    	Spatial spatial = ColladaImporter.getModel();
	    	
	    	ArrayList<String> liste = ColladaImporter.getSkeletonNames();
	    	for (String s:liste)
	    	logger.log(Level.WARNING, ""+s);
	    	
	    	
	    	rootNode.attachChild(spatial);
	    	
	        ColladaImporter.cleanUp();
    	}
    	catch (IOException e) 
    	{
    		System.out.println("can't find collada file");
    	}


		
		
		/*
		InputStream input;
		Node n = new Node("Node");
		try {
			input = new FileInputStream(file);
			ColladaImporter.load(input, "fliege");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	
	//logger.log(Level.WARNING, ""+file.canRead());
	}

}
