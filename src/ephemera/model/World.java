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

import ephemera.controller.SchwarmController;

public class World extends Node{
	
	private static final long serialVersionUID = 1;
	
	private Node objectNode;
	private Node terrainNode; 
	private ArrayList<Spatial> obs;
	private Skybox skybox ;
	
	
	public World(){
		super("World Root Node");
		
		obs = new ArrayList<Spatial>();
		objectNode = new Node();
		
		initSky();
		generateTerrain();
		generateRandomObjects(10);
		PlantObstacles.createTree(0, -150, 0, this, objectNode.getChildren());
		
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
        terrainNode.attachChild(tb);
        objectNode.attachChild(tb);
        obs.add(tb);
    }
	
	public Node getTerrainNode(){
		return terrainNode;
	}
	
	
	/**
	 * Erstelle Zufallig N Objekte der zufälligen Ausdehnung von 100 RE
	 * @param N anzahl Objekte die erstellt werden sollen
	 * @return ObjectNode
	 */
	public void generateRandomObjects(int N){
		

		// DisplaySystem berreit stellen 
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		// TextureState erstellen 
		TextureState ts = createTextureState(display,"ephemera/texture/objects/5016.jpg");
		for (int i=0;i<N;i++){
			// Größe
			float x = FastMath.nextRandomInt(1, 200);
			float y = FastMath.nextRandomInt(1, 900);
			float z = FastMath.nextRandomInt(1, 300);
			// Erstelle Objekt
			TriMesh box = new Box("Box_"+i,new Vector3f(0,0,0),new Vector3f(x,y,z));
			box.setModelBound(new BoundingBox());
			
			// Zufällige Position
			x = FastMath.nextRandomInt(-1000, 1000);
			y = -400;//FastMath.nextRandomInt(-1000, 1000);
			z = FastMath.nextRandomInt(-1000, 1000);
			// Verschiebe Objekt
			box.setLocalTranslation(new Vector3f(x,y,z));
			// Hier werden die Texturen angemeldet
			init(box,display,ts,objectNode);
			obs.add(box);
	
			box.updateModelBound();
		}
		
		objectNode.updateModelBound();
		
		
	}
	
	
	
	
	/**
	 * erzeuge einen Fliegenbrutkasten an pos Vector3f mit rate t[s] 
	 */
	public Node initNest(SchwarmController sc,Vector3f pos,float t){
		Node nest = new Node("Nest");
		//TextureState ts1 = createTextureState(DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER),"ephemera/ObjTextures/nest.jpg");
		TriMesh form = new Sphere("Nest",new Vector3f(0,0,0),20,20,20);
		form.setLocalTranslation(pos);
		
		
		return nest;
		
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
	
	/**
	 * Methode zur Erkennung von Kollisionen mit Hindernissen in der Welt.
	 * Hierbei wird eine Liste mit Hindernissen durchlaufen und geprueft, ob
	 * es Kollisionen mit dem uebergebenen Node des Flugtieres gibt. Falls ja,
	 * wird true zurueckgegeben, wenn nicht false.
	 * @param animal
	 * @return boolean
	 */
	public boolean obstacleAvoidance(Node animal){  
		
		// Abbrechen wenn keine Hindernisse vorhanden.
		if (obs==null) return false;
		
		// Wenn Hindernisse vorhanden durchlaufen.
		for (int i=0;i<obs.size();i++) {
			
			if(animal.hasCollision(obs.get(i), false)){
				return true;
			}
		}
		return false;
	}
	
}
