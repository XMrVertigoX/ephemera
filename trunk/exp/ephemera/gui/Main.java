package ephemera.gui;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;





public class Main extends SimpleGame  {

   Schwarm schwarm = new Schwarm();
   GUI gui = new GUI();
   private Node guiNode;

   
   @Override
   protected void simpleInitGame() {
       
       //GUI ----------------------------------------------
       // create a node for ortho gui stuff
       guiNode = new Node( "gui" );
       guiNode.setRenderQueueMode( Renderer.QUEUE_ORTHO );
       gui.init(guiNode);
     
       
       //Swarm ---------------------------------------------
       this.cam.setLocation(new Vector3f(50,50,150));
       schwarm.addFlies(400);
       Node n = schwarm.getSwarmNode();
       rootNode.attachChild(n);
   }
   
   
   public static void main(String[] args) { 
       new Main().start();    
       
   }
   
   
   
   protected void simpleUpdate(){
       schwarm.updateAll();
       display.getRenderer().draw( guiNode );

   }
}
