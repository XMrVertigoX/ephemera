package ephemera.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.stat.StatCollector;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;


import ephemera.controller.SchwarmController;

public class swarmGUI extends JFrame {

	//
    private static final int GRID_LINES = 51;
    private static final float GRID_SPACING = 100f;

	private SchwarmController 		schwarm;
	
    private static final long serialVersionUID = 1L;

    int width = 640, height = 480;

    MyJmeView impl ;
    
    private CamHandler camhand;
    private Canvas glCanvas;
    private Geometry grid;
    private boolean flycam=false;

    private Preferences prefs = Preferences
            .userNodeForPackage(swarmGUI.class);

    private JCheckBoxMenuItem yUp;

    private JCheckBoxMenuItem zUp;

	// Farben festlegen
	Color white = new Color(255,255,255);
	Color blue = new Color(21,159,210);
	Color dgrey= new Color(68,68,68);
	
	

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {

    		public void run() {
    			try {
    				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    			} catch (Exception e) {
    				//Hier Fehlermeldung bzw logger
    			}
    			new swarmGUI();
    		}});
    }

    public swarmGUI() {
    	
    	try {
            init();
            //camhand.setJmeView(impl);
            
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
     //   tabbedPane.setMinimumSize(minimumSize);
        canvasPanel.setMinimumSize(minimumSize);  

        
       
        //Tabs---------------------
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(new JScrollPane(createOptionsPanel(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Grundeinstellungen");
        tabbedPane.add(new JScrollPane(createAdditionalPanel(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Weiteres");
        tabbedPane.setPreferredSize(new Dimension(300,150));


        
        //Bildschirm unterteilen in interface und 3D view
        JSplitPane mainSplit = new JSplitPane();
        mainSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setRightComponent(tabbedPane);
        mainSplit.setLeftComponent(canvasPanel);
        mainSplit.setDividerLocation(750);
        getContentPane().add(mainSplit, BorderLayout.CENTER);

        grid = createGrid();
        impl.setGrid(grid);
        //schwarm = impl.getSchwarm();
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
    private JPanel createOptionsPanel() {
    	

        final JLabel countLabel = new JLabel("Maximale Fliegenanzahl");
        countLabel.setForeground(white);
        JLabel speedLabel = new JLabel("Simulationsgeschwindigkeit");
        speedLabel.setForeground(white);
        JLabel cohLabel = new JLabel("Kohäsion");
        cohLabel.setForeground(white);
        JLabel aliLabel = new JLabel("Ausrichtung");
        aliLabel.setForeground(white);
        JLabel sepLabel = new JLabel("Trennung");
        sepLabel.setForeground(white);
        
    	final JSlider countSlider = new JSlider (){

            private static final long serialVersionUID = 1L;
        	public void actionPerformed(ActionEvent e) {
                //  Fliege hinzufügen
            }
        
        };
        
        countSlider.addChangeListener(new ChangeListener() {
			
		
			public void stateChanged(ChangeEvent ce) {
				// TODO Auto-generated method stub
				float value = countSlider.getValue()/100f;
				System.out.println("Maximale Fliegenanzahl "+value);
				if (schwarm!=null){
					
					//schwarm.getRegeln().setFluggeschwindigkeit(value);
					updateCountLabel(countLabel, countSlider);
				}
			}
		});
        
   	// 	countSlider.setMinorTickSpacing(250);
   	 	countSlider.setMajorTickSpacing(1000);
        countSlider.setMinimum(0);		// Minmalwert
        countSlider.setMaximum(3000);	// Maximalwert
        countSlider.setValue(100);		// Beim Start eingestellter Wert
        //countSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
        //countSlider.setExtent(5);		// Zeiger verspringt 10 Einheiten
        countSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
        countSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
        countSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
        countSlider.setPaintTrack(true);	//Balken wird angezeigt
        countSlider.setEnabled(true);
        countSlider.setForeground(white);
        
    	final JSlider speedSlider = new JSlider (){

            private static final long serialVersionUID = 1L;
        	public void actionPerformed(ActionEvent e) {
                //  Fliege hinzufügen
            }
        
        };
        
        speedSlider.addChangeListener(new ChangeListener() {
			
		
			public void stateChanged(ChangeEvent ce) {
				// TODO Auto-generated method stub
				float value = speedSlider.getValue()/10f;
				System.out.println("Simulationsgeschwindigkeit "+value);
				if (impl.getSchwarm()!=null){
					
					impl.getSchwarm().getRegeln().setFluggeschwindigkeit(value);
				}
			}
		});
        
        speedSlider.setMinimum(0);		// Minmalwert
        speedSlider.setMaximum(40);	// Maximalwert
        speedSlider.setValue(1);		// Beim Start eingestellter Wert
        speedSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
        speedSlider.setExtent(1);		// Zeiger verspringt 10 Einheiten
        speedSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
        speedSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
        speedSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
        speedSlider.setPaintTrack(true);	//Balken wird angezeigt
        speedSlider.setEnabled(true);
        speedSlider.setForeground(white);
        
        // Fliege hinzufuegen-Button
        JButton addFlyButton = new JButton(new AbstractAction("Eine Fliege hinzufügen"){
        	private static final long serialVersionUID = 1L;

        	public void actionPerformed(ActionEvent e) {
        		impl.getSchwarm().addFly(new Vector3f());
        	}
        });
        
        addFlyButton.setFont(new Font("DIN", Font.BOLD, 12));
        addFlyButton.setMargin(new Insets(2, 2, 2, 2));

    	// Jaeger hinzufügen-Button
        JButton hunterButton = new JButton(new AbstractAction("Hunter") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
              //  Jäger hinzufügen
            }
        });
        hunterButton.setMargin(new Insets(1, 1, 1, 1));
        hunterButton.setEnabled(true);
        
        final JSlider cohSlider = new JSlider();
		
	    cohSlider.addChangeListener(new ChangeListener(){
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		float value = cohSlider.getValue()/100f;
	   
	    		impl.getSchwarm().getRegeln().setCoh_weight(value);

	    	
	    		
	    		System.out.println("Kohäsionswert:"+value);
	    	}
	    });
     
	 cohSlider.setMinorTickSpacing(5);
	 cohSlider.setMajorTickSpacing(20);
     cohSlider.setMinimum(0);		// Minmalwert
     cohSlider.setMaximum(100);	// Maximalwert
     cohSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
     cohSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
     cohSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
     cohSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
     cohSlider.setPaintTrack(true);	//Balken wird angezeigt
     cohSlider.setEnabled(true);
     cohSlider.setForeground(white);
    
     final	JSlider aliSlider = new JSlider();
		
	    aliSlider.addChangeListener(new ChangeListener(){
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		float value = aliSlider.getValue()/100f;
	   
	    		impl.getSchwarm().getRegeln().setAli_weight(value);

	    		System.out.println("Ausrichtungswert:"+value);
	    	}
	    });
	   
	aliSlider.setMinorTickSpacing(5);
	aliSlider.setMajorTickSpacing(20);    
    aliSlider.setMinimum(0);		// Minmalwert
    aliSlider.setMaximum(100);	// Maximalwert
    aliSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
    aliSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
    aliSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
    aliSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
    aliSlider.setPaintTrack(true);	//Balken wird angezeigt
    aliSlider.setEnabled(true);
    aliSlider.setForeground(white);
    
    
 	final JSlider sepSlider = new JSlider();
		
	   sepSlider.addChangeListener(new ChangeListener(){
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		float value = sepSlider.getValue()/100f;
	   
	    		impl.getSchwarm().getRegeln().setSep_weight(value);

	    		System.out.println("Trennungswert: "+value);
	    	}
	    });
	   
	   sepSlider.setMinorTickSpacing(5);
	   sepSlider.setMajorTickSpacing(20);
       sepSlider.setMinimum(0);		// Minmalwert
       sepSlider.setMaximum(100);	// Maximalwert
       sepSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
       sepSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
       sepSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
       sepSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
       sepSlider.setPaintTrack(true);	//Balken wird angezeigt
       sepSlider.setEnabled(true); 
       sepSlider.setForeground(white);
       

       	// Erstelle Panel für Grundeinstellungen
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(dgrey);
        // Füge dem Tab die Labels, Buttons und Slider hinzu 
        optionsPanel.add(countLabel, new GridBagConstraints(0, 0, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(countSlider, new GridBagConstraints(0, 1, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(speedLabel, new GridBagConstraints(0, 2, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(speedSlider, new GridBagConstraints(0, 3, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));        
        optionsPanel.add(addFlyButton, new GridBagConstraints(0, 4, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(addFlyButton, new GridBagConstraints(0, 5, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(cohLabel, new GridBagConstraints(0, 6, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(cohSlider, new GridBagConstraints(0, 7, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(aliLabel, new GridBagConstraints(0, 8, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(aliSlider, new GridBagConstraints(0, 9, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(sepLabel, new GridBagConstraints(0, 10, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(sepSlider, new GridBagConstraints(0, 11, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));

        return optionsPanel;
    }
    
    private JPanel createAdditionalPanel() {

        final JSlider followSlider = new JSlider();
        	followSlider.addChangeListener(new ChangeListener(){
        		public void stateChanged(ChangeEvent ce) {
	    		
        			float value = followSlider.getValue()/100f;
	   
        			impl.getSchwarm().getRegeln().setFollow_weight(value);

        			System.out.println("Folge Leittier-Wert: "+value);
	    	}
	    });
           
       	   followSlider.setMinorTickSpacing(5);
    	   followSlider.setMajorTickSpacing(20);
           followSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
           followSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
           followSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
           followSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
           followSlider.setPaintTrack(true);	//Balken wird angezeigt
           followSlider.setEnabled(true); 
           followSlider.setForeground(white);
    	
 
        final JSlider desiredSlider = new JSlider();
        	desiredSlider.addChangeListener(new ChangeListener(){
        		public void stateChanged(ChangeEvent ce) {
	    		
        			float value = desiredSlider.getValue();
	   
        			impl.getSchwarm().getRegeln().setDesiredSeparation(value);

        			System.out.println("Gewünschter Abstand: "+value);
	    	}
	    });
           
        	desiredSlider.setMinorTickSpacing(5);
        	desiredSlider.setMajorTickSpacing(20);
        	desiredSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
        	desiredSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
        	desiredSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
        	desiredSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
        	desiredSlider.setPaintTrack(true);	//Balken wird angezeigt
        	desiredSlider.setEnabled(true); 
        	desiredSlider.setForeground(white);          
 
           final JSlider neighborSlider = new JSlider();

           neighborSlider.addChangeListener(new ChangeListener(){
       		public void stateChanged(ChangeEvent ce) {
	    		
       			float value = neighborSlider.getValue();
	   
       			impl.getSchwarm().getRegeln().setNeighborDistance(value);

       			System.out.println("Abstand zum Nachbarn: "+value);
	    	}
	    });
           
       	   neighborSlider.setMinorTickSpacing(5);
       	   neighborSlider.setMajorTickSpacing(20);
          neighborSlider.setMinimum(0);		// Minmalwert
          neighborSlider.setMaximum(100);	// Maximalwert
          neighborSlider.setSnapToTicks(true);	// Automatisches Versetzen deaktiviert
          neighborSlider.setOrientation(SwingConstants.HORIZONTAL);	// horizontale Ausrichtung
          neighborSlider.setPaintTicks(true);	//Striche werden nicht angezeigt
          neighborSlider.setPaintLabels(true);	//Zahlen werden nicht angezeigt
          neighborSlider.setPaintTrack(true);	//Balken wird angezeigt
          neighborSlider.setEnabled(true);  
          neighborSlider.setForeground(white);
         
    	
        JLabel followLabel = new JLabel("Folge Leittier");
        followLabel.setForeground(white);
        JLabel desiredLabel = new JLabel("Gewünschter Abstand");
        desiredLabel.setForeground(white);
        JLabel neighborLabel = new JLabel ("Abstand zum Nachbarn");
        neighborLabel.setForeground(white);
        
        //Kot hinzufügen-Button
        JButton shitButton = new JButton(new AbstractAction("Kot hinzufügen") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
              //  
            }
        });
        
        shitButton.setFont(new Font("DIN", Font.BOLD, 12));
        shitButton.setMargin(new Insets(2, 2, 2, 2));
        shitButton.setEnabled(true);
        
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBackground(dgrey);
        

        addPanel.add(shitButton, new GridBagConstraints(0, 12, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(followLabel, new GridBagConstraints(0, 13, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));  
        addPanel.add(followSlider, new GridBagConstraints(0, 14, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(desiredLabel, new GridBagConstraints(0, 15, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0)); 
        addPanel.add(desiredSlider, new GridBagConstraints(0, 16, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(neighborLabel, new GridBagConstraints(0, 17, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0)); 
        addPanel.add(neighborSlider, new GridBagConstraints(0, 18, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        
        
        return addPanel;
    }
    
    private void updateCountLabel(JLabel cl, JSlider cs) {
        JLabel countLabel=cl;
        JSlider countSlider=cs;
    	int val = countSlider.getValue();
        countLabel.setText("Maximale Fliegenanzahl: " + val);
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
                @Override
				public void componentResized(ComponentEvent ce) {
                    doResize();
                }
            });
            impl = new MyJmeView(width, height);
            camhand = new CamHandler();
            camhand.setJmeView(impl);
            glCanvas.addMouseWheelListener(camhand);
            glCanvas.addMouseListener(camhand);
            glCanvas.addMouseMotionListener(camhand);

            // Important! Here is where we add the guts to the canvas:

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