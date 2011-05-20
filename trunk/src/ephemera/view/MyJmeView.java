package ephemera.view;

import java.awt.Color;

import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.util.Debug;
import com.jme.util.stat.StatCollector;

import ephemera.controller.SchwarmController;
import ephemera.model.Jaeger;
import ephemera.model.World;

public class MyJmeView extends SimpleCanvasImpl {
	/**
     * The root node of our stat graphs.
     */
	protected Node statNode;
	private World 		worldController;
	private SchwarmController 		schwarm;
	private TextureState 			textureState;
	private Node root;
    private Geometry grid;
    
    boolean flycam=false;
    private Quad labGraph;
    public MyJmeView(int width, int height) {
        super(width, height);
    }
    
    
    public SchwarmController getSchwarm(){
    	return schwarm;
    }
	public void setGrid(Geometry grid){
		this.grid=grid;
	}
    //3D gedšns
    public void simpleSetup() {
    	
    	worldController = new World();
		Node worldNode = worldController.getWorldRootNode();
		// Schwarm initialisieren
		schwarm = new SchwarmController();
		schwarm.setWorld(worldController);
		schwarm.addFlies(300);

		Node schwarmNode = schwarm.getSwarmNode();
		rootNode.attachChild(worldNode);
		rootNode.attachChild(schwarmNode);	
    	//Color bg = new Color(prefs.getInt("bg_color", 0));
        renderer.setBackgroundColor(ColorRGBA.black);
        cam.setFrustumPerspective(50,50,150, 10000);

        

        // Licht und Schatten
        LightState lightState = renderer.createLightState();
        lightState.detachAll();
        rootNode.setRenderState(lightState);
        /*
    	PointLight pl = new PointLight();
		pl.setDiffuse(ColorRGBA.yellow);
		pl.setLinear(1f);
		pl.setEnabled(true);
		pl.setLocation(schwarm.getLeittierNode().getLocalTranslation());
		*/
        //lightState.attach(pl);
        DirectionalLight dl = new DirectionalLight();
    	dl.setEnabled(true);
    	dl.setDirection(new Vector3f(1,0,0));
    	lightState.attach(dl);
    	schwarmNode.setRenderState(lightState);            
        
    	//schwarmNode.setRenderState(lightState);
        // Finally, a stand alone node (not attached to root on purpose)
    	
    	grid.updateRenderState();  
        rootNode.attachChild(grid);
        
       
        //GUINode = new Node("GUI");
        
        ZBufferState zbuf = renderer.createZBufferState();
        zbuf.setWritable(false);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        
        //GUINode.setRenderState(zbuf);
        //GUINode.updateRenderState();

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
        
  
      

    };

    

    
    
    
    
    public void simpleUpdate() {
    	
    	schwarm.updateAll();
    	
    }

    
    
    @Override
    public void simpleRender() {
        rootNode.draw(renderer);
        
    }        
}



