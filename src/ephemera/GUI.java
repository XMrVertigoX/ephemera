package ephemera;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Regeln;

import Controller.SchwarmController;
import Controller.WorldController;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

import com.jme.util.stat.StatCollector;

import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;


public class GUI extends JFrame{

	WorldController 		wc;
	SchwarmController 		schwarm;
	Regeln regeln = new Regeln();
	
    public static Node GUINode;


    private static final long serialVersionUID = 1L;

    int width = 640, height = 480;

    MyImplementor impl;
    private CameraHandler camhand;
    private Canvas glCanvas;
    private Node root;
    private Geometry grid;


    private Preferences prefs = Preferences
            .userNodeForPackage(GUI.class);

    private JCheckBoxMenuItem yUp;

    private JCheckBoxMenuItem zUp;


    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {

    		public void run() {
    			try {
    				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    			} catch (Exception e) {
    				//Hier Fehlermeldung bzw logger
    			}
    			new GUI();
    		}});
    }

    public GUI() {
        try {
            init();
            // center the frame
            setLocationRelativeTo(null);

            // show frame
            setVisible(true);

            // init some location dependent sub frames

            while (glCanvas == null) {
            	try { Thread.sleep(100); } catch (InterruptedException e) {}
            }

        } catch (Exception ex) {
        }
    }

    private void init() throws Exception {
        updateTitle();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFont(new Font("Arial", 0, 12));

        setJMenuBar(createMenuBar());
     
        //3D view ----------------------------------------------
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout(new BorderLayout());
        canvasPanel.add(getGlCanvas(), BorderLayout.CENTER);
        Dimension minimumSize = new Dimension(150, 150);
        canvasPanel.setMinimumSize(minimumSize);
        
        //interface ---------------------------------------------
        JPanel interfacePanel = new JPanel();
        interfacePanel.setLayout(new BorderLayout());
        interfacePanel.add(createLayerPanel());
        

        //linke seite unterteilen
       // JSplitPane sideSplit = new JSplitPane();
       // sideSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
       // sideSplit.setTopComponent(createLayerPanel());
       // sideSplit.setDividerLocation(150);

        
        //Bildschirm unterteilen in interface und 3D view
        JSplitPane mainSplit = new JSplitPane();
        mainSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(interfacePanel);
        mainSplit.setRightComponent(canvasPanel);
        mainSplit.setDividerLocation(300);
        getContentPane().add(mainSplit, BorderLayout.CENTER);

        grid = createGrid();
        
        yUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Callable<Void> exe = new Callable<Void>() {
                    public Void call() {
                        camhand.worldUpVector.set(Vector3f.UNIT_Y);
                        Camera cam = impl.getRenderer().getCamera();
                        cam.getLocation().set(0, 850, -850);
                        camhand.recenterCamera();
                        grid.unlock();
                        grid.getLocalRotation().fromAngleAxis(0, Vector3f.UNIT_X);
                        grid.lock();
                        prefs.putBoolean("yUp", true);
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                        .enqueue(exe);
            }
        });
        zUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Callable<Void> exe = new Callable<Void>() {
                    public Void call() {
                        camhand.worldUpVector.set(Vector3f.UNIT_Z);
                        Camera cam = impl.getRenderer().getCamera();
                        cam.getLocation().set(0, -850, 850);
                        camhand.recenterCamera();
                        grid.unlock();
                        grid.getLocalRotation().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
                        grid.lock();
                        prefs.putBoolean("yUp", false);
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                        .enqueue(exe);
            }
        });
        
        Callable<Void> exe = new Callable<Void>() {
            public Void call() {
                if (prefs.getBoolean("yUp", true)) {
                    yUp.doClick();
                } else {
                    zUp.doClick();
                }
                return null;
            }
        };
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                .enqueue(exe);

        setSize(new Dimension(1024, 768));
    }

    private void updateTitle() {
        setTitle("ephemera");
    }

    private JMenuBar createMenuBar() {

        Action showGrid = new AbstractAction("Show Grid") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                grid
                        .setCullHint(grid.getCullHint() == Spatial.CullHint.Always ? Spatial.CullHint.Dynamic
                                : Spatial.CullHint.Always);
                prefs.putBoolean("showgrid", grid.getCullHint() != Spatial.CullHint.Always);
            }
        };
        showGrid.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);

      

        Action recenter = new AbstractAction("Recenter Camera") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                camhand.recenterCamera();
            }
        };
        recenter.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);

        yUp = new JCheckBoxMenuItem("Y-Up Camera");
        yUp.setMnemonic(KeyEvent.VK_Y);
        zUp = new JCheckBoxMenuItem("Z-Up Camera");
        zUp.setMnemonic(KeyEvent.VK_Y);
        ButtonGroup upGroup = new ButtonGroup();
        upGroup.add(yUp);
        upGroup.add(zUp);

        JMenu view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        JCheckBoxMenuItem sgitem = new JCheckBoxMenuItem(showGrid);
        sgitem.setSelected(prefs.getBoolean("showgrid", true));
        view.add(sgitem);
        view.addSeparator();
        view.add(recenter);
        view.add(yUp);
        view.add(zUp);

        JMenuBar mbar = new JMenuBar();
      
        mbar.add(view);
        return mbar;
    }

    
    
    // JPanel, hier werden Buttons etc hinzugefügt allerdings in das "obere" menue (eben: delete & new button)
    private JPanel createLayerPanel() {
        // Slider ----------------------------------------------------------------
        JSlider numberSlider = new JSlider (){

        
            private static final long serialVersionUID = 1L;
        	public void actionPerformed(ActionEvent e) {
                //  Änderung der Fliegenanzahl
              }
        
        };
        numberSlider.setMinimum(0);		// Minmalwert
        numberSlider.setMaximum(500);	// Maximalwert
        numberSlider.setValue(50);		// Beim Start eingestellter Wert
        numberSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
        numberSlider.setExtent(10);		// Zeiger verspringt 10 Einheiten
        numberSlider.setOrientation(JSlider.HORIZONTAL);	// horizontale Ausrichtung
        numberSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
        numberSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
        numberSlider.setPaintTrack(true);	//Balken wird angezeigt
        numberSlider.setEnabled(true);
        
    	final JSlider cohSlider = new JSlider();
    		
    	    cohSlider.addChangeListener(new ChangeListener(){
    	    	public void stateChanged(ChangeEvent ce) {
    	    		
    	    		float value = cohSlider.getValue()/100f;
    	   
    	    		regeln.setCoh_weight(value);
    	    	
    	    		
    	    		System.out.println(value);
    	    	}
    	    });
         
         
         cohSlider.setMinimum(0);		// Minmalwert
         cohSlider.setMaximum(100);	// Maximalwert
        // cohSlider.setValue(5);		// Beim Start eingestellter Wert
         cohSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
         //cohSlider.setExtent(10);		// Zeiger verspringt 10 Einheiten
         cohSlider.setOrientation(JSlider.HORIZONTAL);	// horizontale Ausrichtung
         cohSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
         cohSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
         cohSlider.setPaintTrack(true);	//Balken wird angezeigt
         cohSlider.setEnabled(true);
        
     	JSlider aliSlider = new JSlider(){
    		
   	     private static final long serialVersionUID = 1L;
        	public void actionPerformed(ActionEvent e) {
                //  Änderung der Fliegenanzahl
              }
        
        };
        aliSlider.setMinimum(0);		// Minmalwert
        aliSlider.setMaximum(10);	// Maximalwert
        aliSlider.setValue(5);		// Beim Start eingestellter Wert
        aliSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
        aliSlider.setExtent(10);		// Zeiger verspringt 10 Einheiten
        aliSlider.setOrientation(JSlider.HORIZONTAL);	// horizontale Ausrichtung
        aliSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
        aliSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
        aliSlider.setPaintTrack(true);	//Balken wird angezeigt
        aliSlider.setEnabled(true);
        
        
     	JSlider sepSlider = new JSlider(){
    		
      	     private static final long serialVersionUID = 1L;
           	public void actionPerformed(ActionEvent e) {
                   //  Änderung der Fliegenanzahl
                 }
           
           };
           sepSlider.setMinimum(0);		// Minmalwert
           sepSlider.setMaximum(10);	// Maximalwert
           sepSlider.setValue(5);		// Beim Start eingestellter Wert
           sepSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
           sepSlider.setExtent(10);		// Zeiger verspringt 10 Einheiten
           sepSlider.setOrientation(JSlider.HORIZONTAL);	// horizontale Ausrichtung
           sepSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
           sepSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
           sepSlider.setPaintTrack(true);	//Balken wird angezeigt
           sepSlider.setEnabled(true);   		
    		
    	
        
        JLabel numberLabel = new JLabel("Number: "+numberSlider.getValue());
        JLabel cohLabel = new JLabel("Cohesion");
        JLabel aliLabel = new JLabel("Alignment");
        JLabel sepLabel = new JLabel("Seperation");
    	
    	// New Fly Button ------------------------------------------------------
        JButton flyButton = new JButton(new AbstractAction("Fly") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                // Fliege hinzufügen
            }
        });
        flyButton.setMargin(new Insets(1, 1, 1, 1));
        flyButton.setEnabled(true);
        
    	// Hunter Button ------------------------------------------------------
        JButton hunterButton = new JButton(new AbstractAction("Hunter") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
              //  Jäger hinzufügen
            }
        });
        hunterButton.setMargin(new Insets(1, 1, 1, 1));
        hunterButton.setEnabled(true);

        
        //Shit button ---------------------------------------------------------
        JButton shitButton = new JButton(new AbstractAction("Shit") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
              //  
            }
        });
        shitButton.setMargin(new Insets(1, 1, 1, 1));
        shitButton.setEnabled(true);
        

        //Grid ----------------------------------------------------------------
        JPanel layerPanel = new JPanel(new GridBagLayout());
        layerPanel.add(numberLabel, new GridBagConstraints(0, 0, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(cohLabel, new GridBagConstraints(0, 2, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(aliLabel, new GridBagConstraints(0, 4, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(sepLabel, new GridBagConstraints(0, 6, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));        
        
        // Buttons, Slider zum layerPanel hinzufügen ----------------------------------
        layerPanel.add(numberSlider, new GridBagConstraints(0, 1, 5, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(cohSlider, new GridBagConstraints(0, 3, 5, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(aliSlider, new GridBagConstraints(0, 5, 5, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(sepSlider, new GridBagConstraints(0, 7, 5, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(flyButton, new GridBagConstraints(0, 8, 1, 1,
        		0.0,0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(hunterButton, new GridBagConstraints(1, 8, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(shitButton, new GridBagConstraints(2, 8, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        
        return layerPanel;
    }

 

    private ColorRGBA makeColorRGBA(Color color) {
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }



    protected Canvas getGlCanvas() {
        if (glCanvas == null) {

            // -------------GL STUFF------------------

            // make the canvas:
        	DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
        	display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
            glCanvas = (Canvas)display.createCanvas(width, height);
            glCanvas.setMinimumSize(new Dimension(100, 100));

            // add a listener... if window is resized, we can do something about
            // it.
            glCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    doResize();
                }
            });

            camhand = new CameraHandler();

            glCanvas.addMouseWheelListener(camhand);
            glCanvas.addMouseListener(camhand);
            glCanvas.addMouseMotionListener(camhand);

            // Important! Here is where we add the guts to the canvas:
            impl = new MyImplementor(width, height);

            ((JMECanvas) glCanvas).setImplementor(impl);

            // -----------END OF GL STUFF-------------

            Callable<Void> exe = new Callable<Void>() {
                public Void call() {
                    forceUpdateToSize();
                    ((JMECanvas) glCanvas).setTargetRate(60);
                    return null;
                }
            };
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
        }
        return glCanvas;
    }

    public void forceUpdateToSize() {
        // force a resize to ensure proper canvas size.
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() + 1);
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() - 1);
    }

    class CameraHandler extends MouseAdapter implements MouseMotionListener,
            MouseWheelListener {
        Point last = new Point(0, 0);
        Vector3f focus = new Vector3f();
        private Vector3f vector = new Vector3f();
        private Quaternion rot = new Quaternion();
        public Vector3f worldUpVector = Vector3f.UNIT_Y.clone();

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

        public void mousePressed(MouseEvent arg0) {
            last.x = arg0.getX();
            last.y = arg0.getY();
        }

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

        private void panCamera(float left, float up) {
            Camera cam = impl.getRenderer().getCamera();
            cam.getLeft().mult(left, vector);
            vector.scaleAdd(up, cam.getUp(), vector);
            cam.getLocation().addLocal(vector);
            focus.addLocal(vector);
            cam.onFrameChange();
        }

        private void zoomCamera(float amount) {
            Camera cam = impl.getRenderer().getCamera();
            float dist = cam.getLocation().distance(focus);
            amount = dist - Math.max(0f, dist - amount);
            cam.getLocation().scaleAdd(amount, cam.getDirection(),
                    cam.getLocation());
            cam.onFrameChange();
        }
    }

    protected void doResize() {
        if (impl != null) {
            impl.resizeCanvas(glCanvas.getWidth(), glCanvas.getHeight());
            if (impl.getCamera() != null) {
                Callable<Void> exe = new Callable<Void>() {
                    public Void call() {
                        impl.getCamera().setFrustumPerspective(
                                45.0f,
                                (float) glCanvas.getWidth()
                                        / (float) glCanvas.getHeight(), 1,
                                10000);
                        return null;
                    }
                };
                GameTaskQueueManager.getManager()
                        .getQueue(GameTaskQueue.RENDER).enqueue(exe);
            }
        }
    }

    
    

    // IMPLEMENTING THE SCENE: --------------------------------------------------------------------

    class MyImplementor extends SimpleCanvasImpl {

        /**
         * The root node of our stat graphs.
         */
        protected Node statNode;


        private Quad labGraph;
        public MyImplementor(int width, int height) {
            super(width, height);
        }

        
    	
        //3D gedöns
        public void simpleSetup() {
        	wc = new WorldController();
    		wc.initSky();
    		wc.attatchTo(rootNode);
    		// Schwarm initialisieren
    		schwarm = new SchwarmController();
    		schwarm.addFlies(200);
    		Node n = schwarm.getSwarmNode();
    		
        	Color bg = new Color(prefs.getInt("bg_color", 0));
            renderer.setBackgroundColor(makeColorRGBA(bg));
            cam.setFrustumPerspective(50,50,150, 10000);

            root = rootNode;

            // Finally, a stand alone node (not attached to root on purpose)
            statNode = new Node("stat node");
            statNode.setCullHint(Spatial.CullHint.Never);

            root.attachChild(grid);
            grid.updateRenderState();

           
            //GUINode = new Node("GUI");
            root.attachChild(n);

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

        
        
        
        
        
        /**
         * Set up which stats to graph
         *
         */
      
        
        
        
        /**
         * Set up the graphers we will use and the quads we'll show the stats on.
         *
         */
        
    }

    
    
    
    private static final int GRID_LINES = 51;
    private static final float GRID_SPACING = 100f;

    private Geometry createGrid() {
        Vector3f[] vertices = new Vector3f[GRID_LINES * 2 * 2];
        float edge = GRID_LINES / 2 * GRID_SPACING;
        for (int ii = 0, idx = 0; ii < GRID_LINES; ii++) {
            float coord = (ii - GRID_LINES / 2) * GRID_SPACING;
            vertices[idx++] = new Vector3f(-edge, 0f, coord);
            vertices[idx++] = new Vector3f(+edge, 0f, coord);
            vertices[idx++] = new Vector3f(coord, 0f, -edge);
            vertices[idx++] = new Vector3f(coord, 0f, +edge);
        }
        Geometry grid = new com.jme.scene.Line("grid", vertices, null,
                null, null) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        grid.getDefaultColor().set(ColorRGBA.darkGray.clone());
        grid.setCullHint(prefs.getBoolean("showgrid", true) ? Spatial.CullHint.Dynamic
                : Spatial.CullHint.Always);
        return grid;
    }
}