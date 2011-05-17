/**
 * WorldController 2011 by Semjon Mooraj
 * diese Klasse stellt die Welt dar in der sich die Fliegen bewegen.
 * Die Welt ist standartmäig ein Quadratischer Würfel der Kantenlänge 2000 
 */

package ephemera.controller;

import java.net.URL;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;

import ephemera.tester.HelloTexture;

public class WorldController {

	private Node worldRootNode;
	
	
	public WorldController(){
		worldRootNode = new Node("World Root Node");
		//worldRootNode.setCullHint(Spatial.CullHint.Never);
		//initSky();
		//generateRandomObjects(5);
		generateTerrain();
	}
	/**
	 * 
	 */
	public Vector3f calcPosition(Vector3f pos){
		// TO DO
		// ..
		// .
		return new Vector3f(0,0,0);
	}
	/**
	 * Lade die Texturen und verknuepfe diese mit Skybox
	 */
	public void initSky(){
		Skybox sky = new Skybox("Skybox",2000,2000,2000);
		// Lade die Texturen 
		Texture north = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear); // custom/1.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture east = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture south = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_x.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture west = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_z.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture up = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_positive_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		Texture down = TextureManager.loadTexture(WorldController.class.getClassLoader().getResource("ephemera/SkyboxSkin/cubemap_arch/arch_negative_y.jpg"),Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
		
		sky.setTexture(Skybox.Face.North, north);
		sky.setTexture(Skybox.Face.East, east);
		sky.setTexture(Skybox.Face.South, south);
		sky.setTexture(Skybox.Face.West, west);
		sky.setTexture(Skybox.Face.Up, up);
		sky.setTexture(Skybox.Face.Down, down);
		sky.preloadTextures();
		sky.updateRenderState();
		
		worldRootNode.attachChild(sky);
		
	}
	
	
	private void generateTerrain() {
		Node node = new Node("Terrain");
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		// This will be the texture for the terrain.
        URL grass=HelloTerain.class.getClassLoader().getResource(
        		"ephemera/ObjTextures/dirt.jpg");

        //  Use the helper class to create a terrain for us.  The terrain will be 64x64
        MidPointHeightMap mph=new MidPointHeightMap(64,1.5f);
        // Create a terrain block from the created terrain map.
        TerrainBlock tb=new TerrainBlock("midpoint block",mph.getSize(),
                new Vector3f(-30,.911f,-30),
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
        tb.setLocalTranslation(new Vector3f(1000,-300,1000));
        // Attach the terrain TriMesh to rootNode
        worldRootNode.attachChild(tb);
        
    }
	
	/**
	 * Szenenknoten des Wuerfels
	 * @return
	 */
	public Node getWorldRootNode(){
		return worldRootNode;
	}
	/**
	 * Erstelle Zufallig N Objekte der zufälligen Ausdehnung von 100 RE
	 * @param N anzahl Objekte die erstellt werden sollen
	 * @return ObjectNode
	 */
	public void generateRandomObjects(int N){
		Node obj = new Node("Objekte");
		// DisplaySystem berreit stellen 
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		// TextureState erstellen 
		TextureState ts = createTextureState(display,"ephemera/ObjTextures/5016.jpg");
		for (int i=0;i<N;i++){
			// Größe
			float x = FastMath.nextRandomInt(1, 200);
			float y = FastMath.nextRandomInt(1, 500);
			float z = FastMath.nextRandomInt(1, 300);
			// Erstelle Objekt
			TriMesh box = new Box("Box_"+i,new Vector3f(0,0,0),new Vector3f(x,y,z));
			box.setModelBound(new BoundingBox());
			// Zufällige Position
			x = FastMath.nextRandomInt(-1000, 1000);
			y = 0;//FastMath.nextRandomInt(-1000, 1000);
			z = FastMath.nextRandomInt(-1000, 1000);
			// Verschiebe Objekt
			box.setLocalTranslation(new Vector3f(x,y,z));
			// Hier werden die Texturen angemeldet
			init(box,display,ts,obj);
		}
		worldRootNode.attachChild(obj);
		
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
                HelloTexture.class.getClassLoader().getResource(path), Texture.MinificationFilter.BilinearNearestMipMap,
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
	private void init( TriMesh spatial, DisplaySystem display, TextureState textureState, Node ObjectNode ) {
	        BlendState alphaState = display.getRenderer().createBlendState();
	        alphaState.setEnabled( true );
	        alphaState.setBlendEnabled( true );
	        alphaState.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
	        alphaState.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );

	        ObjectNode.attachChild( spatial );
	        spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
	        spatial.setRenderState( alphaState );

	        MaterialState material = display.getRenderer().createMaterialState();
	        material.setShininess( 128 );
	        ColorRGBA color = new ColorRGBA( 0.7f, 0.7f, 0.7f, 1f );
	        material.setDiffuse( color );
	        material.setAmbient( color.mult( new ColorRGBA( 0.1f, 0.1f, 0.1f, 1 ) ) );
	        spatial.setRenderState( material );

	        spatial.setRenderState( textureState );
//	        spatial.setRenderState( display.getRenderer().createWireframeState() );
	    }
}
