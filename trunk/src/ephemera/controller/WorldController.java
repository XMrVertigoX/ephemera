/**
 * WorldController 2011 by Semjon Mooraj
 * diese Klasse stellt die Welt dar in der sich die Fliegen bewegen.
 * Die Welt ist standartmäig ein Quadratischer Würfel der Kantenlänge 2000 
 */

package ephemera.controller;

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
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;

import ephemera.tester.HelloTexture;

public class WorldController {

	private Node worldRootNode;
	
	
	public WorldController(){
		worldRootNode = new Node("World Root Node");
		Skybox sky = new Skybox("Skybox",2000,2000,2000);
		initSky(sky);
		worldRootNode.attachChild(sky);
	}
	
	public WorldController(int xSize,int ySize,int zSize){
		worldRootNode = new Node("World Root Node");
		Skybox sky = new Skybox("WorldCubeBox",xSize,ySize,zSize);
		initSky(sky);
		worldRootNode.attachChild(sky);
	}
	
	
	/**
	 * Lade die Texturen und verknuepfe diese mit Skybox
	 */
	public void initSky(Skybox sky){
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
	 * @param N
	 * @return
	 */
	public void generateRandomObjects(int N){
		Node obj = new Node("Objekte");
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		TextureState ts = createTextureState(display);
		for (int i=0;i<N;i++){
			float x = FastMath.nextRandomInt(1, 100);
			float y = FastMath.nextRandomInt(1, 100);
			float z = FastMath.nextRandomInt(1, 100);
			
			TriMesh box = new Box("Box_"+i,new Vector3f(0,0,0),new Vector3f(x,y,z));
			box.setModelBound(new BoundingBox());
			x = FastMath.nextRandomInt(-1000, 1000);
			y = 0;//FastMath.nextRandomInt(-1000, 1000);
			z = FastMath.nextRandomInt(-1000, 1000);
			box.setLocalTranslation(new Vector3f(x,y,z));
			
			// Hier sollten die Texturen angemeldet werden
			init(box,display,ts,obj);
			//obj.attachChild(box);
		}
		worldRootNode.attachChild(obj);
	}
	/**
	 * Initialisiering einer TextureState für Objekte und Gegenstände
	 * @param display wird von der GUI übergeben 
	 * @return TextureState an die einzelne Spartiale angemeldet werden
	 */
	public TextureState createTextureState(DisplaySystem display) {
        TextureState textureState = display.getRenderer().createTextureState();
        textureState.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
                HelloTexture.class.getClassLoader().getResource("ephemera/ObjTextures/5016.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        textureState.setTexture(t1);
        return textureState;
	}
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
