package ephemera.view;


/**
 * swarmGUI by Kilian Heinrich & Stefan Greuel
 * Diese Klasse stellt die Bedienung der Simulation dar. Dazu wird ein Swing-Interface genutzt.
 * 
 * @author Kilian Heinrich & Stefan Greuel
 */

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jme.math.*;
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
import com.jmex.awt.lwjgl.*;
import ephemera.controller.*;
import ephemera.model.*;


public class GUI extends JFrame {

    private MyJmeView impl;
    private CamHandler camhand;
    private Canvas glCanvas;
    private Geometry grid;
	private static Rules rules = new Rules();
	
	//Groesse des Startfensters
	int width = 1280, height = 720;
	
	// CI Farben festlegen
	Color white = new Color(255,255,255);
	Color blue = new Color(21,159,210);
	Color dgrey= new Color(68,68,68);
    
    private static final int GRID_LINES = 51;
    private static final float GRID_SPACING = 100f;
	public static DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
    private static final long serialVersionUID = 1L;
    private Preferences prefs = Preferences.userNodeForPackage(GUI.class);
    

    
    /**
     * Konstruktor
     * Initialisiert die GUI 
     */
    public GUI() {
    	
    	try {
            init(); 
            // Frame zentrieren
            setLocationRelativeTo(null);
            // Zeige frame
            setVisible(true);           
            
        } catch (Exception ex) {
        }
    }

    
    /**
     * Gibt Display System zurueck
     * @return DisplaySystem
     */
    public static DisplaySystem getDisplay(){
    	return display;
    }
    
    
    /**
     * Unterteilung der GUI: JPanel, JTabbedPane, JSplitPane
     * @throws Exception
     */
    private void init() throws Exception {
   
    	setTitle("ephemera");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFont(new Font("Arial", 0, 12));
        setJMenuBar(createMenuBar());
          
        //3D-Ansicht ----------------------------------------------
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout(new BorderLayout());
        canvasPanel.add(getGlCanvas(), BorderLayout.CENTER);
        Dimension minimumSize = new Dimension(150, 150);
        canvasPanel.setMinimumSize(minimumSize);  

        //Tabs-----------------------------------------------------
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(new JScrollPane(createOptionsPanel(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Grundeinstellungen");
        tabbedPane.add(new JScrollPane(createAdditionalPanel(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "Erweitert");
        tabbedPane.setPreferredSize(new Dimension(300,150));

        //Bildschirm unterteilen in Interface und 3D-Ansicht
        JSplitPane mainSplit = new JSplitPane();
        mainSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setRightComponent(tabbedPane);
        mainSplit.setLeftComponent(canvasPanel);
        mainSplit.setDividerLocation(1000);
        mainSplit.setContinuousLayout(true);
        mainSplit.setOneTouchExpandable(true);     
        getContentPane().add(mainSplit, BorderLayout.CENTER);
        
        grid = createGrid();
        impl.setGrid(grid);
        setSize(new Dimension(width, height));
    }


    /**
     * Erstellt obere Menueleiste
     * @return JMenueBar
     */
    private JMenuBar createMenuBar() {
    	
    	//TODO Neustart  	
        Action newAction = new AbstractAction("Neustart") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                //createNewSystem();
            }
        };
        newAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
    	
        //TODO Standardeinstellungen  	
        Action defaultValues = new AbstractAction("Standardeinstellungen wiederherstellen") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
               
            }
        };
        newAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);        
       
        Action quit = new AbstractAction("Beenden") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        quit.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);

    	JMenu file = new JMenu("Datei");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(newAction);
        file.add(defaultValues);
        file.addSeparator();
        file.add(quit);
    	
        Action showGrid = new AbstractAction("Zeige Grid") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                grid.setCullHint(grid.getCullHint() == Spatial.CullHint.Always ? Spatial.CullHint.Dynamic
                                : Spatial.CullHint.Always);
                prefs.putBoolean("showgrid", grid.getCullHint() != Spatial.CullHint.Always);
            }
        };
        showGrid.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
    
        JMenu view = new JMenu("Ansicht");
        view.setMnemonic(KeyEvent.VK_V);
        JCheckBoxMenuItem sgitem = new JCheckBoxMenuItem(showGrid);
        sgitem.setSelected(prefs.getBoolean("showgrid", true));
        view.add(sgitem);
      
    	//TODO HILFE  	     
        Action help = new AbstractAction("Hilfe") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
            	               
            }
        };
        newAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        
    	//TODO UEBER  	
        Action about = new AbstractAction("Ueber") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
               
            }
        };
        newAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);

        JMenu info = new JMenu("Info");
        info.setMnemonic(KeyEvent.VK_V);
        info.add(help);
        info.add(about);

        JMenuBar mbar = new JMenuBar();
        mbar.add(file);
        mbar.add(view);
        mbar.add(info);
        
        return mbar;
    }

    /**
     * JPanel Hauptfunktionen des Menues
     * @return JPanel
     */
    private JPanel createOptionsPanel() {
    	
        JLabel ephemeraLabel = new JLabel("ephemera");
        ephemeraLabel.setForeground(blue);
        ephemeraLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        final JLabel countLabel = new JLabel("Maximale Fliegenanzahl");
        countLabel.setForeground(white);
        JLabel speedLabel = new JLabel("Simulationsgeschwindigkeit");
        speedLabel.setForeground(white);
        JLabel cohLabel = new JLabel("Kohaesion");
        cohLabel.setForeground(white);
        JLabel aliLabel = new JLabel("Ausrichtung");
        aliLabel.setForeground(white);
        JLabel sepLabel = new JLabel("Trennung");
        sepLabel.setForeground(white);
    
	   
    	final JSlider countSlider = new JSlider ();
        
        countSlider.addChangeListener(new ChangeListener() {	
			public void stateChanged(ChangeEvent ce) {
				float value = countSlider.getValue();
				System.out.println("Maximale Fliegenanzahl "+value);
					//TODO: Fliegenanzahl dynamisch einstellbar
					updateCountLabel(countLabel, countSlider);
					if (impl.getSchwarm()!=null) 
						impl.getSchwarm().getRegeln().setFlyCount((int)value);
			}
		});
        
   	 	countSlider.setMajorTickSpacing(250);
        countSlider.setMinimum(0);
        countSlider.setMaximum(1000);
        
        //TODO: FLiegenanzahl soll sich gezogen werden
        countSlider.setValue(100);		// Beim Start eingestellter Wert
        countSlider.setOrientation(SwingConstants.HORIZONTAL);
        countSlider.setPaintTicks(true);
        countSlider.setPaintLabels(true);	
        countSlider.setPaintTrack(true);
        countSlider.setEnabled(true);
        countSlider.setForeground(white);
        
    	final JSlider speedSlider = new JSlider ();
		speedSlider.setValue((int)(rules.getFluggeschwindigkeit()*10));
		System.out.println("Simulationsgeschwindigkeit:"+speedSlider.getValue());
        
        speedSlider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent ce) {

				float value = speedSlider.getValue()/10f;
				System.out.println("Simulationsgeschwindigkeit "+value);
				if (impl.getSchwarm()!=null){
					
					impl.getSchwarm().getRegeln().setFluggeschwindigkeit(value);
				}
			}
		});
        
        speedSlider.setMinimum(0);	
        speedSlider.setMaximum(50);	
   	 	speedSlider.setMajorTickSpacing(10);
        speedSlider.setOrientation(SwingConstants.HORIZONTAL);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);	
        speedSlider.setPaintTrack(true);
        speedSlider.setEnabled(true);
        speedSlider.setForeground(white);

        
        JButton addFlyButton = new JButton(new AbstractAction("Fliege hinzufuegen"){
        	private static final long serialVersionUID = 1L;

        	public void actionPerformed(ActionEvent e) {
        		impl.getSchwarm().addFly(new Vector3f());
        		System.out.println ("Fliege hinzugefuegt");
        	}
        });
        
        addFlyButton.setFont(new Font("Arial", Font.BOLD, 12));
        addFlyButton.setMargin(new Insets(2, 2, 2, 2));
        
        
        final JSlider cohSlider = new JSlider();
		cohSlider.setValue((int)(rules.getCoh_weight()*100));
	    cohSlider.addChangeListener(new ChangeListener(){
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		
	    		float value = cohSlider.getValue()/100f;
	    		impl.getSchwarm().getRegeln().setCoh_weight(value);
	    		System.out.println("Kohaesionswert:"+value);
	    	}
	    });
     
	    cohSlider.setMinorTickSpacing(5);
	    cohSlider.setMajorTickSpacing(20);
	    cohSlider.setMinimum(0);
	    cohSlider.setMaximum(100);	
	    cohSlider.setSnapToTicks(true);
	    cohSlider.setOrientation(SwingConstants.HORIZONTAL);
	    cohSlider.setPaintTicks(true);
	    cohSlider.setPaintLabels(true);
	    cohSlider.setPaintTrack(true);
	    cohSlider.setEnabled(true);
	    cohSlider.setForeground(white);
    
	    final JSlider aliSlider = new JSlider();
   
		aliSlider.setValue((int)(rules.getAli_weight()*100));
	    aliSlider.addChangeListener(new ChangeListener(){
	    	
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		float value = aliSlider.getValue()/100f;
	    		impl.getSchwarm().getRegeln().setAli_weight(value);
	    		System.out.println("Ausrichtungswert:"+value);
	    	}
	    });
	   
	    aliSlider.setMinorTickSpacing(5);
	    aliSlider.setMajorTickSpacing(20);    
	    aliSlider.setMinimum(0);
	    aliSlider.setMaximum(100);
	    aliSlider.setSnapToTicks(true);	
	    aliSlider.setOrientation(SwingConstants.HORIZONTAL);
	    aliSlider.setPaintTicks(true);
	    aliSlider.setPaintLabels(true);
	    aliSlider.setPaintTrack(true);
	    aliSlider.setEnabled(true);
    	aliSlider.setForeground(white);
    
    
    	final JSlider sepSlider = new JSlider();
    	sepSlider.setValue((int)(rules.getSep_weight()*100));
		
    	sepSlider.addChangeListener(new ChangeListener(){
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		float value = sepSlider.getValue()/100f;
	   
	    		impl.getSchwarm().getRegeln().setSep_weight(value);

	    		System.out.println("Trennungswert: "+value);
	    	}
	    });
	   
	   sepSlider.setMinorTickSpacing(5);
	   sepSlider.setMajorTickSpacing(20);
       sepSlider.setMinimum(0);
       sepSlider.setMaximum(100);
       sepSlider.setSnapToTicks(true);
       sepSlider.setOrientation(SwingConstants.HORIZONTAL);
       sepSlider.setPaintTicks(true);
       sepSlider.setPaintLabels(true);
       sepSlider.setPaintTrack(true);
       sepSlider.setEnabled(true); 
       sepSlider.setForeground(white);
       

       	// Arrangement des Grundeinstellungspanel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(dgrey);
        optionsPanel.add(ephemeraLabel, new GridBagConstraints(0, 0, 1, 1,
                0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));        
        optionsPanel.add(countLabel, new GridBagConstraints(0, 1, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(countSlider, new GridBagConstraints(0, 2, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(speedLabel, new GridBagConstraints(0, 3, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(speedSlider, new GridBagConstraints(0, 4, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));        
        optionsPanel.add(addFlyButton, new GridBagConstraints(0, 5, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(addFlyButton, new GridBagConstraints(0, 6, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(cohLabel, new GridBagConstraints(0, 7, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(cohSlider, new GridBagConstraints(0, 8, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(aliLabel, new GridBagConstraints(0, 9, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(aliSlider, new GridBagConstraints(0, 10, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(sepLabel, new GridBagConstraints(0, 11, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));
        optionsPanel.add(sepSlider, new GridBagConstraints(0, 12, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));

        return optionsPanel;
    }
    
    /**
     * Erstellt JPanel des erweiterten Menues
     * @return JPanel
     */
    private JPanel createAdditionalPanel() {
    	
        JLabel hunterLabel = new JLabel("Jaeger-Lebensdauer in Sekunden");
        hunterLabel.setForeground(white);  
        JLabel followLabel = new JLabel("Folge Leittier");
        followLabel.setForeground(white);
        JLabel desiredLabel = new JLabel("Gewuenschter Abstand");
        desiredLabel.setForeground(white);
        JLabel neighborLabel = new JLabel ("Sichtweite");
        neighborLabel.setForeground(white);
    	
        final JSlider hunterSlider = new JSlider();
        hunterSlider.setValue(20);
      
        hunterSlider.addChangeListener(new ChangeListener(){
    		public void stateChanged(ChangeEvent ce) {
    	
    					
    			float value = (float)hunterSlider.getValue();
    			if (impl.getHunter()!=null)
    				impl.getHunter().setLifetime(value);
    			System.out.println("Jaegerlebensdauer: "+value);
    		}
        });
       
        hunterSlider.setMinorTickSpacing(20);
        hunterSlider.setMajorTickSpacing(40);
        hunterSlider.setMinimum(20);		
        hunterSlider.setMaximum(200);	
        hunterSlider.setSnapToTicks(true);	
        hunterSlider.setOrientation(SwingConstants.HORIZONTAL);	
        hunterSlider.setPaintTicks(true);	
        hunterSlider.setPaintLabels(true);	
        hunterSlider.setPaintTrack(true);	
        hunterSlider.setEnabled(true); 
        hunterSlider.setForeground(white);   

        final JSlider followSlider = new JSlider();
		followSlider.setValue((int)(rules.getFollow_weight()*100));	
        followSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent ce) {
	    		
        		float value = followSlider.getValue()/100f;   
        		impl.getSchwarm().getRegeln().setFollow_weight(value);
        		System.out.println("Folge Leittier-Wert: "+value);
	    	}
	    });
           
        followSlider.setMinorTickSpacing(5);
        followSlider.setMajorTickSpacing(20);
        followSlider.setSnapToTicks(true);	
        followSlider.setOrientation(SwingConstants.HORIZONTAL);	
        followSlider.setPaintTicks(true);	
        followSlider.setPaintLabels(true);	
        followSlider.setPaintTrack(true);	
        followSlider.setEnabled(true); 
        followSlider.setForeground(white);
    	
 
        final JSlider desiredSlider = new JSlider();
		desiredSlider.setValue((int)(rules.getDesiredSeparation()));    
        desiredSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent ce) {
	    		
        		float value = desiredSlider.getValue();
        		impl.getSchwarm().getRegeln().setDesiredSeparation(value);
        		System.out.println("Gewuenschter Abstand: "+value);
	    	}
	    });
           
        desiredSlider.setMinorTickSpacing(5);
        desiredSlider.setMajorTickSpacing(20);
        desiredSlider.setSnapToTicks(true);	
        desiredSlider.setOrientation(SwingConstants.HORIZONTAL);	
        desiredSlider.setPaintTicks(true);	
        desiredSlider.setPaintLabels(true);	
        desiredSlider.setPaintTrack(true);	
        desiredSlider.setEnabled(true); 
        desiredSlider.setForeground(white);          
 
        final JSlider neighborSlider = new JSlider();
   		neighborSlider.setValue((int)(rules.getNeighborDistance()));
        neighborSlider.addChangeListener(new ChangeListener(){
       		public void stateChanged(ChangeEvent ce) {
	    		
       			float value = neighborSlider.getValue();
	   
       			impl.getSchwarm().getRegeln().setNeighborDistance(value);

       			System.out.println("Abstand zum Nachbarn: "+value);
	    	}
	    });
           
       	neighborSlider.setMinorTickSpacing(5);
       	neighborSlider.setMajorTickSpacing(20);
        neighborSlider.setMinimum(0);		
        neighborSlider.setMaximum(100);	
        neighborSlider.setSnapToTicks(true);	
        neighborSlider.setOrientation(SwingConstants.HORIZONTAL);	
        neighborSlider.setPaintTicks(true);	
        neighborSlider.setPaintLabels(true);	
        neighborSlider.setPaintTrack(true);	
        neighborSlider.setEnabled(true);  
        neighborSlider.setForeground(white);
          
        
        JButton hunterButton = new JButton(new AbstractAction("Jaeger hinzufuegen") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
            	impl.addNewHunter((float)hunterSlider.getValue());
            	System.out.println("Jaeger hinzugefuegt");
      
            }
        });
        hunterButton.setFont(new Font("Arial", Font.BOLD, 12));
        hunterButton.setMargin(new Insets(2, 2, 2, 2));
        hunterButton.setEnabled(true);
        
        JButton shitButton = new JButton(new AbstractAction("Kot hinzufuegen") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
            }
        });
        
        shitButton.setFont(new Font("Arial", Font.BOLD, 12));
        shitButton.setMargin(new Insets(2, 2, 2, 2));
        shitButton.setEnabled(true);
        
        // Arrangement des erweiterten Panels 
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBackground(dgrey);

        addPanel.add(hunterButton, new GridBagConstraints(0, 1, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(hunterLabel, new GridBagConstraints(0, 2, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));         
        addPanel.add(hunterSlider, new GridBagConstraints(0, 3, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(shitButton, new GridBagConstraints(0, 4, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(followLabel, new GridBagConstraints(0, 5, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0));  
        addPanel.add(followSlider, new GridBagConstraints(0, 6, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(desiredLabel, new GridBagConstraints(0, 7, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0)); 
        addPanel.add(desiredSlider, new GridBagConstraints(0, 8, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        addPanel.add(neighborLabel, new GridBagConstraints(0, 9, 1, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 10, 10), 0, 0)); 
        addPanel.add(neighborSlider, new GridBagConstraints(0, 10, 5, 1,
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 10, 10), 0, 0));
        
        return addPanel;
    }
    
    /**
     * Aktualisiert countLabel nach betätigen von countSlider
     * @param JLabel
     * @param JSlider
     */
    private void updateCountLabel(JLabel cl, JSlider cs) {
        JLabel countLabel=cl;
        JSlider countSlider=cs;
    	int val = countSlider.getValue();
        countLabel.setText("Maximale Fliegenanzahl: " + val);
    }

    /**
     * Erstellt 3D-Fenster und gibt es zurueck
     * @return Canvas
     */
    protected Canvas getGlCanvas() {
        if (glCanvas == null) {

            // -----------GL Canvas-------------
        	display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
            glCanvas = (Canvas)display.createCanvas(width, height);
            glCanvas.setMinimumSize(new Dimension(100, 100));

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

            ((JMECanvas) glCanvas).setImplementor(impl);

            // -----------ENDE GL Canvas-------------
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

    /**
     * Aendert Canvas-Fenstergroesse
     */
    public void forceUpdateToSize() {
        // force a resize to ensure proper canvas size.
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() + 1);
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight() - 1);
    }

    
    /**
     * doResize aendert die Fenstergroesse bei Vergroesserung/Verkleinerung
     */
    protected void doResize() {
        if (impl != null) {
            impl.resizeCanvas(glCanvas.getWidth(), glCanvas.getHeight());
            if (impl.getCamera() != null) {
                Callable<Void> exe = new Callable<Void>() {
                    public Void call() {
                        impl.getCamera().setFrustumPerspective( 45.0f, (float) glCanvas.getWidth() / (float) glCanvas.getHeight(), 1, impl.getFarPlane());
                        return null;
                    }
                };
                GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(exe);
            }
        }
    }
    
    /**
     * Erstellt Grid
     * @return Geometry
     */
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
        
        Geometry grid = new com.jme.scene.Line("grid", vertices, null, null, null) {
            private static final long serialVersionUID = 1L;
            @Override
            public void draw(Renderer r) {
                StatCollector.pause();
                super.draw(r);
                StatCollector.resume();
            }
        };
        
        grid.getDefaultColor().set(ColorRGBA.darkGray.clone());
        grid.setCullHint(prefs.getBoolean("showgrid", true) ? Spatial.CullHint.Dynamic: Spatial.CullHint.Always);
        
        return grid;
    }
}