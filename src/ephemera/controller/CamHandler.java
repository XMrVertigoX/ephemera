/**
 * CamHandler - Maus- und Kamerasteuerung
 * Modifiziert von Stefan Greuel, Kilian Heinrich, Sejmon Mooraj
 * @author Joshua Slack
 *
 */

package ephemera.controller;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.Callable;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import ephemera.view.MyJmeView;


public class CamHandler extends MouseAdapter implements MouseMotionListener,MouseWheelListener {
	
	MyJmeView impl;
	
	Point last = new Point(0, 0);
	Vector3f focus = new Vector3f();
	private Vector3f vector = new Vector3f();
	private Quaternion rot = new Quaternion();
	public Vector3f worldUpVector = Vector3f.UNIT_Y.clone();
	
	public void setJmeView(MyJmeView i){
		impl = i;
	}
	
	/**
	 * Änderung bei gedrückter und bewegter linker/mittlerer/rechter Maustaste
	 */
	public void mouseDragged(final MouseEvent arg0) {
	    Callable<Void> exe = new Callable<Void>() {
	        public Void call() {
	            int difX = last.x - arg0.getX();
	            int difY = last.y - arg0.getY();
	            int mult = arg0.isShiftDown() ? 10 : 1;
	            last.x = arg0.getX();
	            last.y = arg0.getY();
	
	            int mods = arg0.getModifiers();
	            if ((mods & InputEvent.BUTTON1_MASK) != 0) {
	                rotateCamera(worldUpVector, difX * 0.0025f);
	                rotateCamera(impl.getRenderer().getCamera().getLeft(),
	                        -difY * 0.0025f);
	            }
	            if ((mods & InputEvent.BUTTON2_MASK) != 0 && difY != 0) {
	                zoomCamera(difY * mult);
	            }
	            if ((mods & InputEvent.BUTTON3_MASK) != 0) {
	                panCamera(-difX, -difY);
	            }
	            return null;
	        }
	    };
	    GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
	            .enqueue(exe);
	}
	
	public void mouseMoved(MouseEvent arg0) {
	}
	
	/**
	 * Änderung bei gedrückter Maustaste
	 */
	public void mousePressed(MouseEvent arg0) {
	    last.x = arg0.getX();
	    last.y = arg0.getY();
	}
	
	/**
	 * Änderung bei Betätigen des Mausrads
	 */
	public void mouseWheelMoved(final MouseWheelEvent arg0) {
	    Callable<Void> exe = new Callable<Void>() {
	        public Void call() {
	            zoomCamera(arg0.getWheelRotation()
	                    * (arg0.isShiftDown() ? -100 : -20));
	            return null;
	        }
	    };
	    GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
	            .enqueue(exe);
	}
	
	/**
	 * Kamera  zentrieren
	 */
	public void recenterCamera() {
	    Callable<Void> exe = new Callable<Void>() {
	        public Void call() {
	            Camera cam = impl.getRenderer().getCamera();
	            Vector3f.ZERO.subtract(focus, vector);
	            cam.getLocation().addLocal(vector);
	            focus.addLocal(vector);
	            cam.lookAt(focus, worldUpVector );
	            cam.onFrameChange();
	            return null;
	        }
	    };
	    GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
	            .enqueue(exe);
	}
	
	/**
	 * Kameradrehung
	 * @param axis
	 * @param amount
	 */
	private void rotateCamera(Vector3f axis, float amount) {
	    Camera cam = impl.getRenderer().getCamera();
	    if (axis.equals(cam.getLeft())) {
	        float elevation = -FastMath.asin(cam.getDirection().z);
	        // keep the camera constrained to -89 -> 89 degrees elevation
	        amount = Math.min(Math.max(elevation + amount,
	                -(FastMath.DEG_TO_RAD * 89)), (FastMath.DEG_TO_RAD * 89))
	                - elevation;
	    }
	    rot.fromAngleAxis(amount, axis);
	    cam.getLocation().subtract(focus, vector);
	    rot.mult(vector, vector);
	    focus.add(vector, cam.getLocation());
	    cam.lookAt(focus, worldUpVector );
	}
	
	/**
	 * Kameraschwenk
	 * @param left
	 * @param up
	 */
	private void panCamera(float left, float up) {
	    Camera cam = impl.getRenderer().getCamera();
	    cam.getLeft().mult(left, vector);
	    vector.scaleAdd(up, cam.getUp(), vector);
	    cam.getLocation().addLocal(vector);
	    focus.addLocal(vector);
	    cam.onFrameChange();
	}
	
	/**
	 * Kamerazoom
	 * @param amount
	 */
	private void zoomCamera(float amount) {
	    Camera cam = impl.getRenderer().getCamera();
	    float dist = cam.getLocation().distance(focus);
	    amount = dist - Math.max(0f, dist - amount);
	    cam.getLocation().scaleAdd(amount, cam.getDirection(),
	            cam.getLocation());
	    cam.onFrameChange();
	}
	
}