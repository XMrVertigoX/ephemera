package ephemera.model;


import com.jme.app.SimpleGame;
import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;
import javax.swing.ImageIcon;
  
/**
 * TestSkyDome.java
 *
 * @author Highnik
 */
public class TestSkyDome extends SimpleGame {
    
    private SkyDome dome;
    private TerrainPage terrain;
    private Vector3f camPos = new Vector3f();
    
    /**
     * Update time
     */
    protected void simpleUpdate() {
        camPos.x = cam.getLocation().x;
        camPos.y = terrain.getHeight(cam.getLocation()) + 10;
        camPos.z = cam.getLocation().z;
        cam.setLocation(camPos);
        dome.update();
    }
    
    /**
     * update colors
     */
    protected void simpleRender() {
        dome.render();
    }
    
    /*
     * (non-Javadoc)
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("TestSkyDome");
        
        lightState.setTwoSidedLighting(true);
        
        setupTerrain();
        
        setupSkyDome();
    }
    
    /**
     * add terrain
     */
    private void setupTerrain() {
        
      Node terrainNode = new Node();
      rootNode.attachChild(terrainNode);
      
      // this piece is pretty much copied from the jme terrain test
      
      FaultFractalHeightMap heightMap = new FaultFractalHeightMap(
          257, 32, 0, 255, 0.75f);
        Vector3f terrainScale = new Vector3f(10,1,10);
        heightMap.setHeightScale( 0.001f);
        terrain = new TerrainPage("Terrain", 33,
            heightMap.getSize(), terrainScale,
            heightMap.getHeightMap());
        terrain.setDetailTexture(1, 16);
        terrainNode.attachChild(terrain);
        
        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
        pt.addTexture(new ImageIcon(TestSkyDome.class.getClassLoader().getResource(
            "jmetest/data/texture/grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(TestSkyDome.class.getClassLoader().getResource(
            "jmetest/data/texture/dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(TestSkyDome.class.getClassLoader().getResource(
            "jmetest/data/texture/highest.jpg")), 128, 255, 384);
        pt.createTexture(512);
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
            pt.getImageIcon().getImage(),
            Texture.MinificationFilter.Trilinear,
            Texture.MagnificationFilter.Bilinear,
            true);
        ts.setTexture(t1, 0);
        Texture t2 = TextureManager.loadTexture(
          TestSkyDome.class.getClassLoader().getResource("ephemera/ObjTextures/Detail.jpg"),
        Texture.MinificationFilter.Trilinear,
            Texture.MagnificationFilter.Bilinear);
        
        ts.setTexture(t2, 1);
        t2.setWrap(Texture.WrapMode.Repeat);
        t1.setApply(Texture.ApplyMode.Combine);
        t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Modulate);
        t1.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
        t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t1.setCombineSrc1RGB(Texture.CombinerSource.PrimaryColor);
        t1.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
        t2.setApply(Texture.ApplyMode.Combine);
          t2.setCombineFuncRGB(Texture.CombinerFunctionRGB.AddSigned);
          t2.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
          t2.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
          t2.setCombineSrc1RGB(Texture.CombinerSource.Previous);
          t2.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
          
        terrainNode.setRenderState(ts);
        FogState fs = display.getRenderer().createFogState();
        fs.setDensity(0.0015f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.5f, 0.55f, 0.5f, 0.5f));
        fs.setDensityFunction(FogState.DensityFunction.Exponential);
        fs.setQuality(FogState.Quality.PerVertex);
        terrainNode.setRenderState(fs);
        
        terrainNode.lock();
        terrainNode.lockBranch();
        
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        terrainNode.setRenderState(cs);
        
        lightState.detachAll();
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dl.setDirection(new Vector3f(1, -0.5f, 1));
        dl.setEnabled(true);
        lightState.attach(dl);
        
        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setDirection(new Vector3f(0.5f, -0.5f, 0).normalizeLocal());
        lightState.attach(dr);
    }
    
    /**
     * Initialize SkyDome
     */
    private void setupSkyDome() {
        dome = new SkyDome("skyDome", new Vector3f(0.0f,0.0f,0.0f), 11, 18, 850.0f);
        dome.setModelBound(new BoundingSphere());
        dome.updateModelBound();
        dome.updateRenderState();
        dome.setUpdateTime(5.0f);
        dome.setTimeWarp(180.0f);
        dome.setDay(267);
        dome.setLatitude(-22.9f);
        dome.setLongitude(-47.083f);
        dome.setStandardMeridian(-45.0f);
        dome.setSunPosition(10.75f);             // 5:45 am
        dome.setTurbidity(2.0f);
        dome.setSunEnabled(true);
        dome.setExposure(true, 18.0f);
        dome.setOvercastFactor(0.0f);
        dome.setGammaCorrection(2.5f);
        dome.setRootNode(rootNode);
        dome.setIntensity(1.0f);
        // setup a target to LightNode, if you dont want terrain with light's effect remove it.
        dome.setTarget(terrain);
        rootNode.attachChild(dome);
    }
    
    /**
     * Entry point
     */
    public static void main(String[] args) {
        TestSkyDome app = new TestSkyDome();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
}