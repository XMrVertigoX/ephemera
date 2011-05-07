package ephemera.gui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.jme.app.SimpleGame;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmex.awt.swingui.JMEAction;
import com.jmex.awt.swingui.JMEDesktop;




public class GUI extends SimpleGame{
   
   public void init(final Node guiNode){
           
       // create the desktop Quad
       final JMEDesktop desktop = new JMEDesktop( "desktop", 300,1200, input );
       // and attach it to the gui node
       guiNode.attachChild( desktop );
       // center it on screen
        SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                   // make it transparent blue
                   desktop.getJDesktop().setBackground( new Color( 0, 0, 1, 0.2f ) );

                   // create a swing button
                   final JButton button = new JButton( "click me" );
                   // and put it directly on the desktop
                   desktop.getJDesktop().add( button );
                   // desktop has no layout - we layout ourselfes (could assign a layout to desktop here instead)
                   button.setLocation( 200, 200 );
                   button.setSize( button.getPreferredSize() );
                   // add some actions
                   // standard swing action:
                   button.addActionListener( new ActionListener() {
                       public void actionPerformed( ActionEvent e ) {
                           // this gets executed in swing thread
                           // alter swing components ony in swing thread!
                           button.setLocation( FastMath.rand.nextInt( 400 ), FastMath.rand.nextInt( 300 ) );
                       }
                   } );
                   // action that gets executed in the update thread:
                   button.addActionListener( new JMEAction( "my action", input ) {
                       public void performAction( InputActionEvent evt ) {
                           // this gets executed in jme thread
                           // do 3d system calls in jme thread only!
                           guiNode.updateRenderState(); // this call has no effect but should be done in jme thread 
                       }
                   });
               }
           } );
           
           
           // don't cull the gui away
           guiNode.setCullHint( Spatial.CullHint.Never );
           // gui needs no lighting
           guiNode.setLightCombineMode( Spatial.LightCombineMode.Off );
           // update the render states (especially the texture state of the deskop!)
           guiNode.updateRenderState();
           // update the world vectors (needed as we have altered local translation of the desktop and it's
           //  not called in the update loop)
           guiNode.updateGeometricState( 0, true );
           
           // finally show the system mouse cursor to allow the user to click our button
           MouseInput.get().setCursorVisible( true );
   }
   
   
   
   
   

   public static void main(String[] args) throws Exception {
   }






   @Override
   protected void simpleInitGame() {
       // TODO Auto-generated method stub

   }
   
}