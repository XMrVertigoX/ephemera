package ephemera.model;
import java.util.List;

import javax.swing.ImageIcon;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.image.Texture.CombinerScale;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.scene.state.CullState.Face;
import com.jme.scene.state.MaterialState.MaterialFace;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;




public class World2 extends Node{
	RenderPass rPass = new RenderPass();
	BasicPassManager pManager;
	private static ShadowedRenderPass sPass;
	
	private Node objectNode;
	private TerrainPage terrain;
	private SkyDome dome;
	
	public World2(Node statNode){
		super("World");
		setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		setupTerrain();
        setupSkyDome();		
        generateRandomObjects(10);
        dome.setTarget(objectNode);
        attachChild(objectNode);
        
        sPass = new ShadowedRenderPass();
		sPass.add(this);
        sPass.addOccluder(objectNode);
        sPass.setRenderShadows(true);
        sPass.setLightingMethod(ShadowedRenderPass.LightingMethod.Additive);

        pManager =  new BasicPassManager();
        pManager.add(sPass);

       
	}
	
	 
    /**
     * add terrain
     */
    private void setupTerrain() {
        
        CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setCullFace(Face.None);
        cs.setEnabled(true);
        this.setRenderState(cs);
        
        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0, 255, 0.75f);
        Vector3f terrainScale = new Vector3f(20,2.95f,20);
        heightMap.setHeightScale( 1.1f);
        terrain = new TerrainPage("Terrain", 33, heightMap.getSize(), terrainScale, heightMap.getHeightMap());
        terrain.setDetailTexture(128, 128);
        this.attachChild(terrain);

        terrain.setRenderState(cs);
        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
        pt.addTexture(new ImageIcon(World2.class.getClassLoader().getResource("grassb.png")), -128, 0, 155);
        pt.addTexture(new ImageIcon(World2.class.getClassLoader().getResource("dirt.jpg")), 0, 155, 220);
        pt.addTexture(new ImageIcon(World2.class.getClassLoader().getResource("highest.jpg")), 155, 220, 512);
        pt.createTexture(512);
        
        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        ts.setEnabled(true);
        
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
        		Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear,
                true);
        ts.setTexture(t1, 0);
        
        Texture t2 = TextureManager.loadTexture(World2.class.getClassLoader().getResource("Detail.jpg"),
        		Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t2, 1);
        t2.setWrap(Texture.WrapMode.BorderClamp);
        
        t1.setApply(Texture.ApplyMode.Combine);
        t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Modulate);
        t1.setCombineSrc0RGB(Texture.CombinerSource.TextureUnit0);
        t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t1.setCombineSrc1RGB(Texture.CombinerSource.PrimaryColor);
        t1.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
        t1.setCombineScaleRGB(CombinerScale.One);
        
        t2.setApply(Texture.ApplyMode.Combine);
        t2.setCombineFuncRGB(Texture.CombinerFunctionRGB.AddSigned);
        t2.setCombineSrc0RGB(Texture.CombinerSource.TextureUnit0);
        t2.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t2.setCombineSrc1RGB(Texture.CombinerSource.Previous);
        t2.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
        t2.setCombineScaleRGB(CombinerScale.One);
        terrain.setSize(1000);
        terrain.setRenderState(ts);
        
    }
    
    /**
     * Initialize SkyDome
     */
    private void setupSkyDome() {
        dome = new SkyDome("skyDome", new Vector3f(0.0f,0.0f,0.0f), 11, 18, 5850.0f);
        dome.setModelBound(new BoundingSphere());
        dome.updateModelBound();
        dome.updateRenderState();
        dome.setUpdateTime(.10f);
        dome.setTimeWarp(180.0f);
        dome.setDay(267);
        dome.setLatitude(-22.9f);
        dome.setLongitude(-47.083f);
        dome.setStandardMeridian(-45.0f);
        dome.setSunPosition(5.75f);             // 5:45 am
        dome.setTurbidity(2.0f);
        dome.setSunEnabled(true);
        dome.setExposure(true, 18.0f);
        dome.setOvercastFactor(0.0f);
        dome.setGammaCorrection(2.5f);
        dome.setRootNode(this);
        dome.setIntensity(.10f);
        // setup a target to LightNode, if you dont want terrain with light's effect remove it.
        dome.setTarget(this);
        this.attachChild(dome);
        dome.updateRenderState();
    }
	public void update(){
		dome.update();
	}
	public void render(){
		pManager.renderPasses(DisplaySystem.getDisplaySystem().getRenderer());
		dome.render();
		//rPass.renderPass(DisplaySystem.getDisplaySystem().getRenderer());
	}
	
	/**
	 * Erstelle Zufallig N Objekte der zufälligen Ausdehnung von 100 RE
	 * @param N anzahl Objekte die erstellt werden sollen
	 * @return ObjectNode
	 */
	public void generateRandomObjects(int N){
	    objectNode = new Node("Objekte");
		// DisplaySystem berreit stellen 
		DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
		// TextureState erstellen 
		TextureState ts = createTextureState(display,"fenster.jpg");
		
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
			y = 0;//FastMath.nextRandomInt(-1000, 1000);
			z = FastMath.nextRandomInt(-1000, 1000);
			Vector3f pos = new Vector3f(x,y,z);
			y = terrain.getHeight(pos);
			pos = new Vector3f(x,y,z);
			// Verschiebe Objekt
			box.setLocalTranslation(pos);
			// Hier werden die Texturen angemeldet
			init(box,display,ts,objectNode);
			box.updateRenderState();
			box.updateModelBound();
			
		}
        objectNode.updateRenderState();
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
                World2.class.getClassLoader().getResource(path), Texture.MinificationFilter.BilinearNearestMipMap,
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
	        spatial.setRenderQueueMode( Renderer.QUEUE_OPAQUE );
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
	public SkyDome getSkyDome(){return dome;}
	public TerrainPage getTerrain(){ 
		return terrain;
	}
	public Vector3f obstacleAvoidance(Node animal){
		Vector3f avoidObstacles = new Vector3f(0,0,0);   
		
		// Abbrechen wenn keine Hindernisse vorhanden.
		List<Spatial> obs = objectNode.getChildren();
		// Wenn Hindernisse vorhanden durchlaufen.
		
		for (int i=0;i<obs.size();i++) {
			
			if(animal.hasCollision(obs.get(i), false)){
				
				// Einen Kurs weg vom Zentrum waehlen.
				avoidObstacles = new Vector3f(animal.getLocalTranslation().subtract(obs.get(i).getLocalTranslation()));
				avoidObstacles.normalizeLocal();
				if (obs.get(i).getName()=="Terrain") avoidObstacles=new Vector3f(0,4,0);
				return avoidObstacles;
			}
		}
		return new Vector3f();
	}
}