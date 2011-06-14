package ephemera.view;

import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;

import com.jme.system.DisplaySystem;
import com.jme.system.canvas.SimpleCanvasImpl;

import ephemera.controller.SwarmController;
import ephemera.model.Hunter;
import ephemera.model.World;

/**
 * 
 * @author Stefan Greuel, Kilian Heinrich
 *
 */
public class MyJmeView extends SimpleCanvasImpl {

	private GUI gui;
	private World world;
	private SwarmController swarm;
    private Hunter hunter;
    private static boolean exist;
    private long birthTime;
    private long deathTime;
    private float farPlane = 10000.0f;
    
    /**
     * Konstruktor
     * @param width
     * @param height
     * @param gui
     */
    public MyJmeView(int width, int height, GUI gui) {
        super(width, height);
        this.gui = gui;
    }
    

	
	/**
	 * Initialisieren der Schwarmsimulation
	 */
    public void simpleSetup() {
       
    	birthTime = System.currentTimeMillis();
    	deathTime = System.currentTimeMillis();
    	
    	world = new World();
    	setupEnvironment();

		// Schwarm initialisieren
		swarm = new SwarmController(world);

		Node schwarmNode = swarm.getSwarmNode();		
		
		renderer.setBackgroundColor(ColorRGBA.darkGray);
        

        // Licht und Schatten
        LightState lightState = renderer.createLightState();
        lightState.detachAll();
        
        DirectionalLight dl = new DirectionalLight();
    	dl.setEnabled(true);
    	dl.setDirection(new Vector3f(1,0,0));
    	lightState.attach(dl);
    	
    	//schwarmNode.setRenderState(lightState);
        
        rootNode.attachChild(world);
        
        world.attachChild(schwarmNode);
        world.attachChild(swarm.getLeaderNode());
        world.updateGeometricState(0, true);
        world.updateRenderState();
        world.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        
        gui.defaultValues();
		
    };

    /**
     * Update der Simulation
     */
    public void simpleUpdate() {
    	
        world.getSkybox().getLocalTranslation().set(cam.getLocation());
    	world.getSkybox().updateGeometricState(0.0f, true);
    	gui.updateCurrentLabel();
    	
    	long t = System.currentTimeMillis() - birthTime;
    	
    	if (t > 1000) {
        	if (swarm.getSwarm().size() < gui.getCountValue()) {
        		swarm.addFly(swarm.getRules());
        	}
        	
    		birthTime = System.currentTimeMillis();
    	}
    	
    	if (t > 10) {
        	if (swarm.getSwarm().size() > gui.getCountValue() && swarm.getSwarm().size() > 0) {
        		swarm.deleteFly(swarm.getSwarm().get(swarm.getSwarm().size() - 1));
        	}
        	
    		deathTime = System.currentTimeMillis();
    	}
    	
//    	if (schwarm.getSwarm().size() > schwarm.getMaxFlies()) {
//    		schwarm.deleteFly(schwarm.getSwarm().get(schwarm.getSwarm().size() - 1));
//    	}
    	
//    	swarm.updateAll();
    	
    	if(exist){
    		hunter.updateHunter();
    	}
    	
    	world.updateRenderState();
    }
    
    /**
     * Kamera Initialisieren
     */
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
    	swarm.updateAll();
        rootNode.draw(renderer);
        //world.render();
    }       
    
    /**
     * Hunter hinzufuegen
     * @param lifetime
     */
    public void addNewHunter(int lifetime){ 
    	if(!exist && lifetime > 0 && swarm.getSwarm().size() > 0){
    		hunter = new Hunter(new Vector3f(0, 0, 0), world, swarm, lifetime);
    		world.attachChild(hunter);
    		exist = true;
//        	System.out.println("Jaeger hinzugefuegt");
    	}
    	
    	else {
    		System.out.println("Jaeger ist bereits hinzugefuegt oder Lebenszeit zu gering eingestellt!");
    	}
    }
    
    /**
     * Getter fuer Hunter
     * @return hunter
     */
    
    public Hunter getHunter(){
    	return hunter;
    }
    
    /**
     * Getter fuer SwarmController
     * @return swarm
     */
    
    public SwarmController getSwarm(){
    	return swarm;
    }
    
    /**
     * Getter fuer world
     * @return world
     */
    
	public World getWorld(){
		return world;
	}
}
