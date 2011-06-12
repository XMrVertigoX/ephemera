package ephemera.view;

import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.SimpleCanvasImpl;

import ephemera.controller.SchwarmController;
import ephemera.model.Hunter;
import ephemera.model.World;

public class MyJmeView extends SimpleCanvasImpl {

	private World world;
	private SchwarmController schwarm;
    private Geometry grid;
    private Hunter hunter;
    private static boolean exist;
    private long time;
    private int flies = 10;
    private float farPlane = 10000.0f;
    
    public MyJmeView(int width, int height) {
        super(width, height);
    }
    
    public void addNewHunter(float lifetime){ 
    	if(!exist){
    		System.out.println("dabei");
    		hunter = new Hunter(new Vector3f(300,300,300), world, schwarm);
    		world.attachChild(hunter);
    		exist = true;
    	}
    }
    
    public Hunter getHunter(){
    	return hunter;
    }
    
    public SchwarmController getSchwarm(){
    	return schwarm;
    }
    
	public void setGrid(Geometry grid){
		this.grid = grid;
	}
	
	public World getWorld(){
		return world;
	}
	
    //3D gedšns
    public void simpleSetup() {
       
    	time = System.currentTimeMillis();
    	
    	world = new World();
    	setupEnvironment();

		// Schwarm initialisieren
		schwarm = new SchwarmController();
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
    	
    	grid.updateRenderState();  
        rootNode.attachChild(grid);
        
       
        
        ZBufferState zbuf = renderer.createZBufferState();
        zbuf.setWritable(false);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        
        rootNode.attachChild(world);
        rootNode.attachChild(schwarmNode);
        rootNode.attachChild(schwarm.getLeittierNode());
        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    };

    
    public void simpleUpdate() {
        world.getSkybox().getLocalTranslation().set(cam.getLocation());
    	world.getSkybox().updateGeometricState(0.0f, true);
    	
    	float t = (System.currentTimeMillis()-time)/1000f;
    	if (t>2) {
    		time = System.currentTimeMillis();
        	if (schwarm.getSchwarm().size()<schwarm.getRegeln().getFlyCount()) schwarm.addFly(new Vector3f(),schwarm.getRegeln());
    	}
    	schwarm.updateAll();
    	
    	if(exist){
    		hunter.updateHunter();
    	}
    }
    
	private void setupEnvironment() {
    	
    	DisplaySystem display = GUI.getDisplay();
    	
    	cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 1f, farPlane);
    	cam.getLocation().set(0, 850, 850);
    	cam.update();
    }
 
    public static void setExist(boolean value){
    	exist = value;
    	
    	if(value==false){
    		
    	}
    }
    
    public float getFarPlane(){
    	return farPlane;
    }

    
    
    @Override
    public void simpleRender() {
        rootNode.draw(renderer);
    }        
}
