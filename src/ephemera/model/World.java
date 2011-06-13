/**
 * World 2011 by Semjon Mooraj
 * diese Klasse stellt die Welt dar in der sich die Fliegen bewegen.
 * Die Welt ist standartmäig ein Quadratischer Würfel der Kantenlänge 2000 
 */

package ephemera.model;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.jme.bounding.*;
import com.jme.image.Texture;
import com.jme.image.Texture.CombinerScale;
import com.jme.math.*;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.scene.state.CullState.Face;
import com.jme.system.DisplaySystem;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.MidPointHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;


public class World extends Node{
	
	private static final long serialVersionUID = 1;
	private TerrainPage terrain;
	private Node objectNode;
	private Node terrainNode;
	
	private Vector3f avoidObstacles; 
	private Skybox skybox ;
	private SkyDome dome;
	
	public World(){
		super("World");
		setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		updateRenderState();
		objectNode = new Node();
		
		setupTerrain();
		//generateTerrain();
		generateNest();
		Obstacles.createHouse(-1800,-450,1084, new Vector3f(200,700,400),objectNode,terrain);
		Obstacles.createHouse(2000,-450,-1350, new Vector3f(350,500,400),objectNode,terrain);
		
		Obstacles.createTree(-1210, -350, -1720, objectNode,terrain);
		Obstacles.createTree(-850, -350, 1550, objectNode,terrain);
		Obstacles.createTree(1450, -350, 0, objectNode,terrain);
		Obstacles.createTree(1810, -350, 1900, objectNode,terrain);
		
		Obstacles.createShrub1(-1550, -400, -50, objectNode,terrain);
		Obstacles.createShrub1(-800, -400, 530, objectNode,terrain);
		Obstacles.createShrub1(1050, -400, 2000, objectNode,terrain);
		Obstacles.createShrub1(1695, -400, 810, objectNode,terrain);
		
		Obstacles.createShrub2(-200, -400, 650, objectNode,terrain);
		Obstacles.createShrub2(600, -400, -1050, objectNode,terrain);
		Obstacles.createShrub2(-2000, -400, 30, objectNode,terrain);	
		Obstacles.createShrub2(1850, -400, -1850, objectNode,terrain);
		Obstacles.createShrub2(1200, -400, 1150, objectNode,terrain);
		Obstacles.createShrub2(1300, -400, -950, objectNode,terrain);
		
		Obstacles.createFir(-2000, -400, 2000, objectNode,terrain);
		Obstacles.createFir(-1730, -400, -910, objectNode,terrain);
		Obstacles.createFir(-1000, -400, -1100, objectNode,terrain);
		Obstacles.createFir(-520, -400, -1820, objectNode,terrain);
		Obstacles.createFir(-260, -400, -890, objectNode,terrain);
		Obstacles.createFir(300, -400, 250, objectNode,terrain);
		objectNode.updateRenderState();
        objectNode.updateModelBound();
		//generateRandomObjects(100);

		initSky();
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
	/*
		dome = new SkyDome("skyDome", new Vector3f(0.0f,0.0f,0.0f), 11, 18, 5800.0f);
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
        dome.setSunEnabled(false);
        dome.setExposure(true, 18.0f);
        dome.setOvercastFactor(0.0f);
        dome.setGammaCorrection(2.5f);
        dome.setRootNode(this);
        dome.setIntensity(.10f);
        // setup a target to LightNode, if you dont want terrain with light's effect remove it.
        objectNode.updateRenderState();
        dome.setTarget(objectNode);
        dome.updateRenderState();
		attachChild(dome);*/
        
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
        TerrainBlock tb = new TerrainBlock("Terrain", mph.getSize(),
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
        objectNode.updateModelBound();
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
		s.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		
	}
	public void generateRandomObjects(int N){
	    objectNode = new Node("Objekte");
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

private void setupTerrain() {
        
        CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setCullFace(Face.Back);
        cs.setEnabled(true);
        this.setRenderState(cs);
        
        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0, 255, 0.75f);
        Vector3f terrainScale = new Vector3f(20,2.95f,20);
        heightMap.setHeightScale( 1.1f);
        terrain = new TerrainPage("Terrain", 33, heightMap.getSize(), terrainScale, heightMap.getHeightMap());
        terrain.setDetailTexture(128, 128);
        //this.attachChild(terrain);

        terrain.setRenderState(cs);
        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
        pt.addTexture(new ImageIcon(World.class.getClassLoader().getResource("ephemera/texture/objects/grassb.png")), -128, 0, 155);
        pt.addTexture(new ImageIcon(World.class.getClassLoader().getResource("ephemera/texture/objects/dirt.jpg")), 0, 155, 220);
        pt.addTexture(new ImageIcon(World.class.getClassLoader().getResource("ephemera/texture/objects/highest.jpg")), 155, 220, 512);
        pt.createTexture(512);
        
        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        ts.setEnabled(true);
        
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
        		Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear,
                true);
        ts.setTexture(t1, 0);
        
        Texture t2 = TextureManager.loadTexture(World.class.getClassLoader().getResource("ephemera/texture/objects/Detail.jpg"),
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
        objectNode.attachChild(terrain);
        objectNode.setLocalTranslation(new Vector3f(0,-1050,0));

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
	
	public Node getObjectNode(){ 
		return objectNode;
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
	
	public void update(){ dome.update();}
	public void render(){ dome.render();}
	public Vector3f getCollisionVector(){
		return avoidObstacles;
	}
}
