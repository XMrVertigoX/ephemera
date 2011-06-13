/**
 * GUI
 * Diese Klasse stellt die Bedienung der Simulation dar. Dazu wird ein Swing-Interface genutzt.
 * 
 * @author Kilian Heinrich & Stefan Greuel
 */

package ephemera.view;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jmex.awt.lwjgl.*;
import ephemera.controller.CamHandler;


public class GUI extends JFrame {

    private MyJmeView impl;
    private CamHandler camhand;
    private Canvas glCanvas;

    
	JSlider countSlider;
//	final JSlider speedSlider = new JSlider ();
//    JButton addFlyButton;
//    final JSlider cohSlider = new JSlider();
//    final JSlider aliSlider = new JSlider();
//	final JSlider sepSlider = new JSlider();
//	
//    JButton hunterButton;
//    final JSlider hunterSlider = new JSlider();
//    
//    final JSlider followSlider = new JSlider();
//    final JSlider desiredSlider = new JSlider();
//    final JSlider neighborSlider = new JSlider();
    
	//Groesse des Startfensters
	int width = 1280, height = 720;
	
	// CI Farben festlegen
	Color white = new Color(255,255,255);
	Color blue = new Color(21,159,210);
	Color dgrey= new Color(68,68,68);

	public static DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
    private static final long serialVersionUID = 1L;


    
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
        

        setSize(new Dimension(width, height));
    }


    /**
     * Erstellt obere Menueleiste
     * @return JMenueBar
     */
    private JMenuBar createMenuBar() {

        //TODO Standardeinstellungen  	
        Action defaultValues = new AbstractAction("Standardeinstellungen wiederherstellen") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
               defaultValues();
            }
        };
        defaultValues.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);        
       
        Action quit = new AbstractAction("Beenden") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        quit.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);

    	JMenu file = new JMenu("Datei");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(defaultValues);
        file.addSeparator();
        file.add(quit);

        
        /**
         * Hilfe 
         * Oeffnet ein neues Fenster mit Textinhalt
         */
        final JFrame helpFrame = new JFrame("Hilfe");
        helpFrame.setSize(400, 450);
        helpFrame.setBackground(dgrey);
        helpFrame.setLocation(300, 50);
        helpFrame.setResizable(false);     
        JLabel helpText = new JLabel();
        helpText.setForeground(white);
        helpText.setText("<html><b><font size=\"6\" color=\"#159fd2\">Hilfe</font></b> <br><br>"+                  
                " <b>Kohäsion</b> <br>" +
                " Ernöglicht es einem Schwarmmitglied in der nähe seiner Nachbarn zu bleiben.<br><br>" +
                " <b>Ausrichtung</b> <br>" +
                " Passt ein Schwarmmitglied seine Bewegungsrichtung oder seine Geschwindigkeit seinen Schwarmnachbarn an.<br><br>" +
                " <b>Trennung</b> <br>" +
                " Abstand der einzelnen Schwarmmitglieder.<br><br>" +
                " <b>Folge Leittier</b> <br>" +
                " Abstand der Schwarmmitglieder zum Leittier.<br><br>" +
                " <b>Gewünschter Abstand </b><br>" +
                " Mindestabstand der Schwarmmitglieder.<br><br>" +
                " <b>Sichtweite</b> <br>" +
                " Radius in dem die Schwarmmitglieder sich untereinander wahrnehmen. <br><br> </html>");
        
        Border border = BorderFactory.createLineBorder(dgrey);
        Border margin = new EmptyBorder(10, 10, 10, 10);
        helpText.setBorder(new CompoundBorder(border, margin));
        helpFrame.add(helpText);
        
         	     
        Action help = new AbstractAction("Hilfe") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
            	helpFrame.setVisible(true);            
            }
        };
        help.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        
        
        /**
         * Ueber das Projekt
         * Oeffnet ein neues Fenster mit Textinhalt (Informationen ueber das Projekt)
         */
        final JFrame aboutFrame = new JFrame("ephemera");
        aboutFrame.setSize(400, 300);
        aboutFrame.setBackground(dgrey);
        aboutFrame.setLocation(400, 50);
        aboutFrame.setResizable(false);  
        
        JLabel aboutText = new JLabel();
        aboutText.setForeground(white);
        aboutText.setText("<html><b><font size=\"6\" color=\"#159fd2\">ephemera</font></b> <br><br>"+                 
                " ephemera die eintagsfliegenscheiße <br>" +
                " blablablablalba <br>" +
                " asdasdasdasdasd <br>" +
                " asdasdasd <br>" +
                " asdasd</html>");
        
        aboutText.setBorder(new CompoundBorder(border, margin));  
        aboutFrame.add(aboutText);
        
        	
        Action about = new AbstractAction("Ueber") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
            	aboutFrame.setVisible(true);
            }
        };
        
        about.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);

        JMenu info = new JMenu("Info");
        info.setMnemonic(KeyEvent.VK_V);
        info.add(help);
        info.add(about);

        JMenuBar mbar = new JMenuBar();
        mbar.add(file);
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
    
	   
    	countSlider = new JSlider ();
        
        countSlider.addChangeListener(new ChangeListener() {	
			public void stateChanged(ChangeEvent ce) {
				float value = countSlider.getValue();
				System.out.println("Maximale Fliegenanzahl "+value);
//					impl.getSwarm().setMaxFlies((int) value);
					updateCountLabel(countLabel, countSlider);
				}
		});
        
   	 	countSlider.setMajorTickSpacing(100);
        countSlider.setMinimum(0);
        countSlider.setMaximum(500);
        
        countSlider.setValue(200);
        countSlider.setOrientation(SwingConstants.HORIZONTAL);
        countSlider.setPaintTicks(true);
        countSlider.setPaintLabels(true);	
        countSlider.setPaintTrack(true);
        countSlider.setEnabled(true);
        countSlider.setForeground(white);
        
    	final JSlider speedSlider = new JSlider ();
        
        speedSlider.setMinimum(0);	
        speedSlider.setMaximum(10);	
        speedSlider.setValue(4);
   	 	speedSlider.setMajorTickSpacing(2);
        speedSlider.setOrientation(SwingConstants.HORIZONTAL);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);	
        speedSlider.setPaintTrack(true);
        speedSlider.setEnabled(true);
        speedSlider.setForeground(white);
        
        speedSlider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent ce) {
				float value = speedSlider.getValue();
				
				impl.getSwarm().getRules().setSpeed(value);
				System.out.println("Geschwindigkeit der Fliegen: " + value);
				
			}
		});
        
        JButton addFlyButton = new JButton(new AbstractAction("Fliege hinzufuegen"){
        	private static final long serialVersionUID = 1L;

        	public void actionPerformed(ActionEvent e) {
        		if (impl.getSwarm().getSwarm().size() < countSlider.getValue()) {
        			impl.getSwarm().addFly(impl.getSwarm().getRules());
            		System.out.println ("Fliege hinzugefuegt");
        		}
        		
        		else {
        			System.out.println("Maximale Fliegenanzahl erreicht");
        		}
        		
        	}
        });
        
        addFlyButton.setFont(new Font("Arial", Font.BOLD, 12));
        addFlyButton.setMargin(new Insets(2, 2, 2, 2));
        
        final JSlider cohSlider = new JSlider();
		
        cohSlider.addChangeListener(new ChangeListener(){
	    	public void stateChanged(ChangeEvent ce) {
	    		float value = cohSlider.getValue()/100f;
	    		if (impl.getSwarm()!=null)
		    		impl.getSwarm().getRules().setCoh_weight(value);
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
   
		aliSlider.addChangeListener(new ChangeListener(){
	    	
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		float value = aliSlider.getValue()/100f;
	    		if (impl.getSwarm()!=null)
		    		impl.getSwarm().getRules().setAli_weight(value);
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
    	
    	sepSlider.addChangeListener(new ChangeListener(){
	    	public void stateChanged(ChangeEvent ce) {
	    		
	    		float value = sepSlider.getValue()/100f;
	   
	    		if (impl.getSwarm()!=null)
	    		impl.getSwarm().getRules().setSep_weight(value);
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
                0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
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
    			
    			if (impl.getSwarm()!=null)
		    		impl.getSwarm().getRules().setLifeTime(value);
       			
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
		followSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent ce) {
	    		
        		float value = followSlider.getValue()/100f;   
        		if (impl.getSwarm()!=null)
		    		impl.getSwarm().getRules().setFollow_weight(value);
       			
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
		desiredSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent ce) {
	    		
        		float value = desiredSlider.getValue();
        		if (impl.getSwarm()!=null)
		    		impl.getSwarm().getRules().setNeighborDistance(value);
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
   		neighborSlider.addChangeListener(new ChangeListener(){
       		public void stateChanged(ChangeEvent ce) {
	    		
       			float value = neighborSlider.getValue();
       			if (impl.getSwarm()!=null)
		    		impl.getSwarm().getRules().setDesiredSeparation(value);
       			
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
        
        JButton showHunterButton = new JButton(new AbstractAction("Leittier anzeigen") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
            	impl.getSwarm().getPathController().getLeader().toggleVisible();
            }
        });
        
        showHunterButton.setFont(new Font("Arial", Font.BOLD, 12));
        showHunterButton.setMargin(new Insets(2, 2, 2, 2));
        showHunterButton.setEnabled(true);
        
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
        addPanel.add(showHunterButton, new GridBagConstraints(0, 4, 5, 1,
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
    
    private void defaultValues(){
    	
    	impl.getSwarm().getRules().reset();

    	// Standardwerte fuer countSlider wiederherstellen
		//impl.getSchwarm().getRegeln().setFlyCount(defaultRules.getFlyCount());
		//countSlider.setValue(impl.getSwarm().getRules().getFlyCount());
		//System.out.println("Maximal Fliegenanzahl auf Standardwert zurueckgesetzt:" +impl.getSwarm().getRules().getFlyCount());
	
    	// Standardwerte fuer countSlider wiederherstellen
		//impl.getSchwarm().getRegeln().setFluggeschwindigkeit(defaultRules.getFluggeschwindigkeit());
		//speedSlider.setValue((int)(impl.getSwarm().getRules().setFluggeschwindigkeit()*10));
		//System.out.println("Simulationsgeschwindigkeit auf Standardwert zurueckgesetzt:" +defaultRules.getFluggeschwindigkeit());
/*    	
    	// Standardwerte fuer cohSlider wiederherstellen
		impl.getSchwarm().getRegeln().setCoh_weight(defaultRules.getCoh_weight());
		cohSlider.setValue((int)(defaultRules.getCoh_weight()*100));
		System.out.println("Kohaesion auf Standardwert zurueckgesetzt:" +defaultRules.getCoh_weight());
		
		//Standardwerte fuer aliSlider wiederherstellen
		impl.getSchwarm().getRegeln().setAli_weight(defaultRules.getAli_weight());
		aliSlider.setValue((int)(defaultRules.getAli_weight()*100));
		System.out.println("Ausrichtung auf Standardwert zurueckgesetzt:" +defaultRules.getAli_weight());
		
		//Standardwerte fuer hunterSlider wiederherstellen	
		impl.getHunter().setLifetime(20f);
        hunterSlider.setValue(20);
		System.out.println("Jaegerlebensdauer auf Standardwert zurueckgesetzt: "+hunterSlider.getValue());
			
		//Standardwerte fuer sepSlider wiederherstellen
		impl.getSchwarm().getRegeln().setSep_weight(defaultRules.getSep_weight());
    	sepSlider.setValue((int)(defaultRules.getSep_weight()*100));
		System.out.println("Separation auf Standardwert zurueckgesetzt:" +defaultRules.getSep_weight());
		
		//Standardwerte fuer followSlider wiederherstellen
		impl.getSchwarm().getRegeln().setFollow_weight(defaultRules.getFollow_weight());
		followSlider.setValue((int)(defaultRules.getFollow_weight()*100));
		System.out.println("Folge Leittier auf Standardwert zurueckgesetzt:" +defaultRules.getFollow_weight());
		
		//Standardwerte fuer desiredSlider wiederherstellen
		impl.getSchwarm().getRegeln().setDesiredSeparation(defaultRules.getDesiredSeparation());
		desiredSlider.setValue((int)(defaultRules.getDesiredSeparation()));
		System.out.println("Gewuenschter Abstand auf Standardwert zurueckgesetzt:" +defaultRules.getDesiredSeparation());
		
		//Standardwerte fuer neighborSlider wiederherstellen
		impl.getSchwarm().getRegeln().setNeighborDistance(defaultRules.getNeighborDistance());
   		neighborSlider.setValue((int)(defaultRules.getNeighborDistance()));
		System.out.println("Sichtweite auf Standardwert zurueckgesetzt:" +defaultRules.getNeighborDistance());
		
	*/	
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
}