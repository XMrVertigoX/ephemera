package ephemera.view;

import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.SimpleCanvasImpl;

import ephemera.controller.SwarmController;
import ephemera.model.Hunter;
import ephemera.model.World;

public class MyJmeView extends SimpleCanvasImpl {

	private World world;
	private SwarmController schwarm;

    private Hunter hunter;
    private static boolean exist;
    private long time;
    private int flies = 200;
    private float farPlane = 10000.0f;
    
    public MyJmeView(int width, int height) {
        super(width, height);
    }
    
    public void addNewHunter(int lifetime){ 
    	if(!exist && lifetime > 0){
    		hunter = new Hunter(new Vector3f(0, 0, 0), world, schwarm, lifetime);
    		world.attachChild(hunter);
    		exist = true;
//        	System.out.println("Jaeger hinzugefuegt");
    	}
    	
    	else {
    		System.out.println("Jaeger ist bereits hinzugefuegt oder Lebenszeit zu gering eingestellt!");
    	}
    }
    
    public Hunter getHunter(){
    	return hunter;
    }
    
    public SwarmController getSwarm(){
    	return schwarm;
    }
    
	public World getWorld(){
		return world;
	}
	
    public void simpleSetup() {
       
    	time = System.currentTimeMillis();
    	
    	world = new World();
    	setupEnvironment();

		// Schwarm initialisieren
		schwarm = new SwarmController();
		schwarm.setWorld(world);
		schwarm.addFlies(flies);

		Node schwarmNode = schwarm.getSwarmNode();		
		
		renderer.setBackgroundColor(ColorRGBA.darkGray);
        

        // Licht und Schatten
        LightState lightState = renderer.createLightState();
        lightState.detachAll();
        
        DirectionalLight dl = new DirectionalLight();
    	dl.setEnabled(true);
    	dl.setDirection(new Vector3f(1,0,0));
    	lightState.attach(dl);
    	
    	//schwarmNode.setRenderState(lightState);
        
    	// Finally, a stand alone node (not attached to root on purpose)

        ZBufferState zbuf = renderer.createZBufferState();
        zbuf.setWritable(false);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        
        rootNode.attachChild(world);
        
        world.attachChild(schwarmNode);
        world.attachChild(schwarm.getLeaderNode());
        world.updateGeometricState(0, true);
        world.updateRenderState();
        world.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		
    };

    public void simpleUpdate() {
    	
        world.getSkybox().getLocalTranslation().set(cam.getLocation());
    	world.getSkybox().updateGeometricState(0.0f, true);
    	
//    	float t = (System.currentTimeMillis()-time)/1000f;
//    	
//    	if (t > 2) {
//    		time = System.currentTimeMillis();
//        	if (schwarm.getSwarm().size()<schwarm.getRules().getFlyCount()) schwarm.addFly(schwarm.getRules());
//    	}
    	
//    	if (schwarm.getSwarm().size() > schwarm.getMaxFlies()) {
//    		schwarm.deleteFly(schwarm.getSwarm().get(schwarm.getSwarm().size() - 1));
//    	}
    	
    	schwarm.updateAll();
    	
    	if(exist){
    		hunter.updateHunter();
    	}
    	
    	world.updateRenderState();
    }
    
	private void setupEnvironment() {
    	
    	DisplaySystem display = GUI.getDisplay();
    	
    	cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 1f, farPlane);
    	cam.getLocation().set(0, 850, 850);
    	cam.update();
    }
 
    public static void setExist(boolean value){
    	exist = value;
    }
    
    public float getFarPlane(){
    	return farPlane;
    }
    
    @Override
    public void simpleRender() {
        rootNode.draw(renderer);
        //world.render();
    }        
}
