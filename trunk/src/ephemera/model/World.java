/**
 * World 2011 by Semjon Mooraj
 * diese Klasse stellt die Welt dar in der sich die Fliegen bewegen.
 * Die Welt ist standartmäig ein Quadratischer Würfel der Kantenlänge 2000 
 */

package ephemera.model;

import java.net.URL;
import java.util.ArrayList;

import com.jme.bounding.*;
import com.jme.image.Texture;
import com.jme.math.*;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;


public class World extends Node{
	
	private static final long serialVersionUID = 1;
	
	private Node objectNode;
	private Node terrainNode;
	
	private Vector3f avoidObstacles; 
	private ArrayList<Spatial> obs;
	private Skybox skybox ;
	
	
	public World(){
		super("World");
		
		obs = new ArrayList<Spatial>();
		objectNode = new Node();
		
		initSky();
		generateTerrain();
		generateNest();
		
		Obstacles.createHouse(-1800,-450,1085, new Vector3f(200,700,400),objectNode);
		Obstacles.createHouse(2000,-450,0, new Vector3f(200,900,300),objectNode);
		Obstacles.createHouse(2000,-450,-1350, new Vector3f(350,500,400),objectNode);
		
		Obstacles.createTree(-1210, -350, -1720, objectNode);
		Obstacles.createTree(-850, -350, 1550, objectNode);
		Obstacles.createTree(1450, -350, 0, objectNode);
		Obstacles.createTree(1810, -350, 1900, objectNode);
		
		Obstacles.createShrub1(-1550, -400, -50, objectNode);
		Obstacles.createShrub1(-800, -400, 530, objectNode);
		Obstacles.createShrub1(1050, -400, 2000, objectNode);
		Obstacles.createShrub1(1695, -400, 810, objectNode);
		
		Obstacles.createShrub2(-200, -400, 650, objectNode);
		Obstacles.createShrub2(600, -400, -1050, objectNode);
		Obstacles.createShrub2(-2000, -400, 30, objectNode);	
		Obstacles.createShrub2(1850, -400, -1850, objectNode);
		Obstacles.createShrub2(1200, -400, 1150, objectNode);
		Obstacles.createShrub2(1300, -400, -950, objectNode);
		
		Obstacles.createFir(-2000, -400, 2000, objectNode);
		Obstacles.createFir(-1730, -400, -910, objectNode);
		Obstacles.createFir(-1000, -400, -1100, objectNode);
		Obstacles.createFir(-520, -400, -1820, objectNode);
		Obstacles.createFir(-260, -400, -890, objectNode);
		Obstacles.createFir(300, -400, 250, objectNode);
		

		attachChild(skybox);
		attachChild(objectNode);
	}
	
	
	public Skybox getSkybox(){
		return skybox;
	}
	
	
	/**
	 * Lade die Texturen und verknuepfe diese mit der Skybox
	 * @author ...
	 */
	public void initSky(){
	
		skybox = new Skybox("Skybox", 5000, 5000, 5000);
		
		// Lade die Texturen 
		Texture north = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/texture/skybox/reef_north.bmp"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture east = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/texture/skybox/reef_east.bmp"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture south = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/texture/skybox/reef_south.bmp"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture west = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/texture/skybox/reef_west.bmp"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture up = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/texture/skybox/reef_up.bmp"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture down = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/texture/skybox/reef_down.bmp"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		
		skybox.setTexture(Skybox.Face.North, west);
		skybox.setTexture(Skybox.Face.East, north);
		skybox.setTexture(Skybox.Face.South, east);
		skybox.setTexture(Skybox.Face.West, south);
		skybox.setTexture(Skybox.Face.Up, up);
		skybox.setTexture(Skybox.Face.Down, down);
		
		skybox.preloadTextures();
		
	}
	
	private void generateTerrain() {
		terrainNode = new Node("Terrain");
		
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		
		// This will be the texture for the terrain.
        URL grass=World.class.getClassLoader().getResource("ephemera/texture/objects/gras.jpg");

        //  Use the helper class to create a terrain for us.  The terrain will be 64x64
        MidPointHeightMap mph = new MidPointHeightMap(64,1.5f);
       
        // Create a terrain block from the created terrain map.
        TerrainBlock tb = new TerrainBlock("midpoint block", mph.getSize(),
                new Vector3f(-90,.911f,-90),
                mph.getHeightMap(),
                new Vector3f(-100,-100,-100));

        // Add the texture
        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(grass,
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));
        tb.setRenderState(ts);

        // Give the terrain a bounding box.
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();
        tb.setLocalTranslation(new Vector3f(3000,-550,3000));
        
        //Attach the terrain TriMesh to rootNode
        objectNode.attachChild(tb);
    }
	
	public Node getTerrainNode(){
		return terrainNode;
	}
	
	
	/**
	 * Erstellt das Nest
	 */
	public void generateNest(){
		
		// DisplaySystem berreit stellen 
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		// TextureState erstellen 
		TextureState ts = createTextureState(display,"ephemera/texture/objects/nest.jpg");
		//Nest 
		TriMesh s = new Sphere("nest",10,10,70f);
		init(s,display,ts,this);
		objectNode.updateModelBound();		
	}

	
	/**
	 * Initialisiering einer TextureState für Objekte und Gegenstände
	 * @param display wird von der GUI übergeben 
	 * @return TextureState an die einzelne Spartiale angemeldet werden
	 */
	public TextureState createTextureState(DisplaySystem display,String path) {
        TextureState textureState = display.getRenderer().createTextureState();
        textureState.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
                World.class.getClassLoader().getResource(path), Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        textureState.setTexture(t1);
        return textureState;
	}
	/**
	 * Stelle Textur und Material für ein Objekt in der Welt berreit und verknüpfe alles miteinander  
	 * @param spatial das Objekt
	 * @param display DispaySystem
	 * @param textureState TexturState
	 * @param ObjectNode rootNode
	 */
	public void init(TriMesh spatial, DisplaySystem display, TextureState textureState, Node ObjectNode ) {
	        BlendState alphaState = display.getRenderer().createBlendState();
	        alphaState.setEnabled( true );
	        alphaState.setBlendEnabled( true );
	        alphaState.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
	        alphaState.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );

	        ObjectNode.attachChild( spatial );
	        spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
	        spatial.setRenderState( alphaState );
	        spatial.updateModelBound();
	        MaterialState material = display.getRenderer().createMaterialState();
	        material.setShininess( 128 );
	        ColorRGBA color = new ColorRGBA( 0.7f, 0.7f, 0.7f, 1f );
	        material.setDiffuse( color );
	        material.setAmbient( color.mult( new ColorRGBA( 0.1f, 0.1f, 0.1f, 1 ) ) );
	        spatial.setRenderState( material );
	        spatial.setRenderState( textureState );
//	        spatial.setRenderState( display.getRenderer().createWireframeState() );
	    }
	
	public Node getObjectNode(){ 
		return objectNode;
	}
	
	public boolean obstacleAvoidance(Node animal){
		avoidObstacles = new Vector3f(0,0,0);   
		
		// Abbrechen wenn keine Hindernisse vorhanden.
		if (obs==null) return false;
		
		// Wenn Hindernisse vorhanden durchlaufen.
		for (int i=0;i<obs.size();i++) {
			
			if(animal.hasCollision(obs.get(i), false)){
				
				// Einen Kurs weg vom Zentrum waehlen.
				avoidObstacles = new Vector3f(animal.getLocalTranslation().subtract(obs.get(i).getLocalTranslation()));
				avoidObstacles.normalizeLocal();
				return true;
			}
		}
		return false;
	}
	
	public Vector3f getCollisionVector(){
		return avoidObstacles;
	}
}
