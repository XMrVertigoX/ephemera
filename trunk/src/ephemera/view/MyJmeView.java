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
import ephemera.controller.WorldController;
import ephemera.model.Jaeger;

public class MyJmeView extends SimpleCanvasImpl {
	/**
     * The root node of our stat graphs.
     */
	protected Node statNode;
	private WorldController 		worldController;
	private SchwarmController 		schwarm;
	private TextureState 			textureState;
	private Node root;
    private Geometry grid;
    

    private Quad labGraph;
    public MyJmeView(int width, int height) {
        super(width, height);
    }

    
	
    //3D gedšns
    public void simpleSetup() {
    	
    	worldController = new WorldController();
		worldController.generateRandomObjects(40);
		Node worldNode = worldController.getWorldRootNode();
		rootNode.attachChild(worldNode);
		// Schwarm initialisieren
		schwarm = new SchwarmController();
		schwarm.addFlies(400);

		Node schwarmNode = schwarm.getSwarmNode();
		
		rootNode.attachChild(schwarmNode);
		 Jaeger j = new Jaeger(new Vector3f(0,0,0),schwarm.getSchwarm());
	
		//rootNode.attachChild(j.s);
		
    	//Color bg = new Color(prefs.getInt("bg_color", 0));
        renderer.setBackgroundColor(ColorRGBA.black);
        cam.setFrustumPerspective(50,50,150, 10000);

        root = rootNode;

        // Licht und Schatten
        LightState lightState = renderer.createLightState();

        worldNode.setRenderState(lightState);            
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
        // Finally, a stand alone node (not attached to root on purpose)
        statNode = new Node("stat node");
        statNode.setCullHint(Spatial.CullHint.Never);

        root.attachChild(grid);
        grid.updateRenderState();

       
        //GUINode = new Node("GUI");
        root.attachChild(schwarmNode);

        ZBufferState zbuf = renderer.createZBufferState();
        zbuf.setWritable(false);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        
        //GUINode.setRenderState(zbuf);
        //GUINode.updateRenderState();

        statNode.updateGeometricState(0, true);
        statNode.updateRenderState();
        
  
      

    };

    

    
    
    
    
    public void simpleUpdate() {
    	/*
    	if (flycam){
    		Vector3f pos = schwarm.getSchwarm().get(0).getLocalTranslation();
    		Vector3f vel = schwarm.getSchwarm().get(0).getVel();
    		cam.setLocation(pos);
    		cam.lookAt(pos.subtract(vel.mult(-1)),new Vector3f(0,1,0));
    	    
    	}
    	*/
    	schwarm.updateAll();
    	if (Debug.stats) {
            StatCollector.update();
            labGraph.setLocalTranslation((renderer.getWidth()-.5f*labGraph.getWidth()), (renderer.getHeight()-.5f*labGraph.getHeight()), 0);
        }
    }

    
    
    @Override
    public void simpleRender() {
        statNode.draw(renderer);
        
    }        
}



