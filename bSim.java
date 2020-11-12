import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import acm.graphics.GLabel;
import acm.graphics.GRect;

import acm.gui.DoubleField;
import acm.gui.IntField;
import acm.gui.TableLayout;

import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

/** The main class in Assignment 4. 
 *  Here the layout (the sliders, the JToggleButton and the fields), the canvas and ball objects  
 *  are created and the resulting simulation runs its course.  
 *  Each ball created is registered in the bTree.
 * 
 *  @author tamara
 *  
 */

public class bSim extends GraphicsProgram{

	//parameters used in the program
	private static final int WIDTH = 1200;									//Screen Width
    private static final int HEIGHT = 600;									//Screen height
    private static final int OFFSET = 200;									//Screen Offset
    
    private static final int NUMBALLS = 60;									// Number of balls used in the simulation
    private static final double MINSIZE = 1.0; 								// Minimum ball radius (meters)
    private static final double MAXSIZE = 25.0; 							// Maximum ball radius (meters)
    private static final double EMIN = 0; 									// Minimum loss coefficient
    private static final double EMAX = 1.0; 								// Maximum loss coefficient
    private static final double VoMIN = 1.0; 								// Minimum velocity (meters/sec)
    private static final double VoMAX = 200.0; 								// Maximum velocity (meters/sec)
    private static final double ThetaMIN = 1.0; 							// Minimum launch angle (degrees)
    private static final double ThetaMAX = 180.0; 							// Maximum launch angle (degrees)
   
    private RandomGenerator rgen = RandomGenerator.getInstance(); 			//setup of random generator
    bTree myTree = new bTree();												//setup of B-Tree
    //static boolean keepGoing = false;
    
    boolean simRun;
    boolean ableToStack = false;
    boolean TracePoint;
    
    //Declaration of the fields used in the program
    IntField Numballs = new IntField (1);
    DoubleField MinSize = new DoubleField(1);
    DoubleField MaxSize = new DoubleField(1);
    DoubleField LossMin = new DoubleField (0);
    DoubleField LossMax = new DoubleField (0);
    DoubleField VelMin = new DoubleField (1);
    DoubleField VelMax = new DoubleField (1);
    DoubleField AngleMin = new DoubleField(1);
    DoubleField AngleMax = new DoubleField (1);
    
    JToggleButton button = new JToggleButton("Trace");					//Creation of the button to Trace the balls
    String simulations[]={"","Run","Stack","Stop","Clear","Quit"};  	//Creation of an array list to store the possible actions
    JComboBox simulationChooser;										//Creation of the main JComboBox
    JPanel panel;
    
    
     
    public void doSim() {    											//start of simulation   
    	this.resize(WIDTH, HEIGHT+OFFSET); 								//display setup
     	rgen.setSeed( (long) 424242 );									//chosen seed for the assignment
  
     	
	    GRect rect = new GRect(0, HEIGHT, 1200, 3); //creation of the "x axis"
	    rect.setFilled(true);
		rect.setColor(Color.black);					//setting the x axis' color to black
		add(rect);									//adding the x axis to the canvas
    	
		ableToStack = false;						//stacking the balls is not possible
		
    	for (int i=0 ; i<Numballs.getValue(); i++) { //creation and start of NUMBALLS instances of aBall
    		
    		
	    	double bSize = rgen.nextDouble(MinSize.getValue(),MaxSize.getValue());		//current size
	        Color bColor = rgen.nextColor();											//current color
	        double loss = rgen.nextDouble(LossMin.getValue(),LossMax.getValue());		//current loss coefficient
	        double Vo = rgen.nextDouble(VelMin.getValue(),VelMax.getValue());  			//current initial velocity
	        double theta = rgen.nextDouble(AngleMin.getValue(),AngleMax.getValue());	//current angle of propulsion
	        
	        double Xi = 100;															//current X position
	        double Yi = bSize;															//current Y position
			
			aBall iBall = new aBall(Xi, Yi, Vo, theta, bSize, bColor, loss, this);  	//Generate instance
			add(iBall.myBall);													
			myTree.addNode(iBall);														//save instance
			iBall.start();																//start instance
       
    	}
    	
    	while (myTree.isRunning());									//block until simulation is over
		ableToStack = true;											//when the simulation is over, stacking the balls is possible
					
    }	
    
   /**
    * Wrapper method to trace the balls
    * @return
    */
    public boolean getTracePoint() {
    	return TracePoint;
    }
    
   //method to shut down the simulation and applet
    public void Quit() {
    	System.exit(0);
    }
    
    //method to stop the balls at any time
    public void Stop() {
    	if (myTree.root!=null) {
    		 myTree.stopSim();
    	}
    }
    	
    //method to clear everything from the canvas except the x axis
    public void Clear() {
    	removeAll();
    	GRect rect = new GRect(0, HEIGHT, 1200, 3);	 //creation of the "x axis" after it was cleared
	    rect.setFilled(true);
		rect.setColor(Color.black);
		add(rect);
		myTree.root = null;							//setting root to null to clear the stored nodes in the tree
    }
   
    //method to stack the balls
    public void doStack() {
    	myTree.stackBalls(this);
    }

   
    public void init() {
    	
    	add(button, SOUTH);								//adding the Trace button to the bottom of the applet
    	panel = new JPanel ();							//creation of a panel to create an interface with the user
    	panel.setLayout(new TableLayout(12,5,0,0));		//setting the layout of the panel
    	add(panel, EAST);
    	makeBoard(panel);
    	
    	
    	
    	addMouseListeners(); 
     	addActionListeners();
   		
		simulationChooser = new JComboBox(simulations);
		simulationChooser.addItemListener(
				new ItemListener() {
					public void itemStateChanged(ItemEvent e) {					//depending on the chosen action from the main JComboBox
																				//execute the specific action
					if (e.getStateChange()==ItemEvent.SELECTED) {				
						if(simulationChooser.getSelectedIndex()==1) {			//if the chosen action is "Run", run the simulation
							simRun = true;
							System.out.println("Running the simulation");
						}
					
						else if (simulationChooser.getSelectedIndex()==2) {		//if the chosen action is "Stack", and the condition for stacking
							if (ableToStack) {									//is verified, stack the balls
								doStack();
							}
							System.out.println("Stacking the balls");
						}
						else if (simulationChooser.getSelectedIndex()==3) {		//If the chosen action is "Stop", stop the simulation (stop the balls 
							//keepGoing = false;								//from moving
							Stop();
							System.out.println("Balls Stopping");
						}
						
						else if (simulationChooser.getSelectedIndex()==4) {		//if the chosen action is "Clear", clear the canvas and the stored information
							Clear();
							System.out.println("Clearing the canvas");
						}
		
						else if (simulationChooser.getSelectedIndex()==5) {		//if the chosen action is "Quit", shut down the program
							Quit();
							System.out.println("Quitting");
						}
					}
					}
				}	
				
			);	
		add(simulationChooser, NORTH);											//adding the main JCobomboBox to the top of the screen

				JComboBox file = new JComboBox<String>();						//adding the other non used JComboBox to the top of the screen
				file.addItem("File");
				add(file, NORTH);

				JComboBox edit = new JComboBox<String>();
				edit.addItem("Edit");
				add(edit, NORTH);

				JComboBox help = new JComboBox<String>();
				help.addItem("Help");
				add(help, NORTH);
						
    }


    public void actionPerformed(ActionEvent e) {							// if the Trace button is used
    	String cmd = e.getActionCommand(); 
    	if (cmd.equals("Trace")) {
    		if (this.TracePoint) {
    			this.TracePoint=false;
    		}
    	else {
    		TracePoint=true;
    	
    		}
    	}
    } 
    
    private void makeBoard(JPanel panel) {								//creation of the layout 
    	
    	panel.add(new JLabel("General Simulation Parameters"));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	panel.add(new JLabel(" "));
    	
    	//creation of the number of balls slider and adding it to the panel
    	panel.add(new JLabel("NUMBALLS: "));
    	panel.add(new JLabel ("1"));
    	JSlider numballsSlider = new JSlider (1, 255, 1);
    	numballsSlider.addChangeListener(
    			new ChangeListener() {
    				
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();
    					if (Slider.getValueIsAdjusting()) {				//getting the value of the slider
    						Numballs.setValue(Slider.getValue());		//setting the value of the slider to the field
    					}
    					
    				}
    				
    				}
    			);
    	panel.add(numballsSlider);
    	panel.add(new JLabel ("255"));
    	panel.add(Numballs);
    	
    	//creation of the minimum size slider and adding it to the panel
    	panel.add(new JLabel ("MIN SIZE: "));
    	panel.add(new JLabel ("1.0"));
    	JSlider minsizeSlider = new JSlider(10, 250, 10);
    	minsizeSlider.addChangeListener(
    			new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();			
    					if (Slider.getValueIsAdjusting()) {						//getting the value of the slider
    						MinSize.setValue((double)Slider.getValue()/10);		//setting the value of the slider to the field 
    					}														//dividing by 10 to get a more specific interval between two values
    			}
    			}
    				);
    	panel.add(minsizeSlider);
    	panel.add(new JLabel("25.0"));
    	panel.add(MinSize);
    	
    	//creation of the maximum size slider and adding it to the panel
    	panel.add(new JLabel ("MAX SIZE: "));
    	panel.add(new JLabel ("1.0"));
    	JSlider maxsizeSlider = new JSlider(10, 250, 10);
    	maxsizeSlider.addChangeListener(
    			new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();
    					if (Slider.getValueIsAdjusting()) {						//getting the value of the slider
    						MaxSize.setValue((double)Slider.getValue()/10);		//setting the value of the slider to the field
    					}														//dividing by 10 to get a more specific interval between two values
    			}
    			}
    				);
    	panel.add(maxsizeSlider);
    	panel.add(new JLabel ("25.0"));
    	panel.add(MaxSize);
    	
    	//creation of the minimum loss coefficient slider and adding it to the panel
    	panel.add(new JLabel ("LOSS MIN: "));
    	panel.add(new JLabel ("0.0"));
    	JSlider minlossSlider = new JSlider (0,10 , 0);
    	minlossSlider.addChangeListener(
    			new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();
    					if (Slider.getValueIsAdjusting()) {						//getting the value of the slider
    						LossMin.setValue((double)Slider.getValue()/10);		//setting the value of the slider to the field
    					}														//dividing by 10 to get a more specific interval between two values
    			}
    			}
    				);
    	panel.add(minlossSlider);
    	panel.add(new JLabel ("1.0"));
    	panel.add(LossMin);
    	
    	//creation of the maximum loss coefficient slider and adding it to the panel
    	panel.add(new JLabel ("LOSS MAX: "));
    	panel.add(new JLabel ("0.0"));
    	JSlider maxlossSlider = new JSlider (0,10 , 0);
    	maxlossSlider.addChangeListener(
    			new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();
    					if (Slider.getValueIsAdjusting()) {						//getting the value of the slider
    						LossMax.setValue((double)Slider.getValue()/10);		//setting the value of the slider to the field
    					}														//dividing by 10 to get a more specific interval between two values
    			}
    			}
    				);
    	panel.add(maxlossSlider);
    	panel.add(new JLabel ("1.0"));
    	panel.add(LossMax);
    	
    	//creation of the minimum initial velocity slider and adding it to the panel 
    	panel.add(new JLabel ("MIN VEL: "));
    	panel.add(new JLabel ("1.0"));
    	JSlider minvelocitySlider = new JSlider (10, 2000, 10);
    	minvelocitySlider.addChangeListener(
    			new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();
    					if (Slider.getValueIsAdjusting()) {						//getting the value of the slider
    						VelMin.setValue((double)Slider.getValue()/10);		//setting the value of the slider to the field
    					}														//dividing by 10 to get a more specific interval between two values
    			}
    			}
    				);
    	panel.add(minvelocitySlider);
    	panel.add(new JLabel ("200.0"));
    	panel.add(VelMin);
    	
    	//creation of the maximum initial velocity slider
    	panel.add(new JLabel ("MAX VEL: "));
    	panel.add(new JLabel("1.0"));
    	JSlider maxvelocitySlider = new JSlider (10, 2000, 10);
    	maxvelocitySlider.addChangeListener(
    			new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();
    					if (Slider.getValueIsAdjusting()) {						//getting the value of the slider
    						VelMax.setValue((double)Slider.getValue()/10);		//setting the value of the slider to the field
    					}														//dividing by 10 to get a more specific interval between two values
    			}	
    			}
    				);
    	panel.add(maxvelocitySlider);
    	panel.add(new JLabel("200.0"));
    	panel.add(VelMax);
    	
    	//creation of the minimum angle of propulsion slider and adding it to the panel 
    	panel.add(new JLabel ("TH MIN: "));
    	panel.add(new JLabel ("1.0"));
    	JSlider minangleSlider = new JSlider (10, 1800, 10);
    	minangleSlider.addChangeListener(
    			new ChangeListener() {
    				public void stateChanged(ChangeEvent e) {
    					JSlider Slider = (JSlider)e.getSource();
    					if (Slider.getValueIsAdjusting()) {						//getting the value of the slider
    						AngleMin.setValue((double)Slider.getValue()/10);	//setting the value of the slider to the field
    					}														//dividing by 10 to get a more specific interval between two values
    			}
    			}
    				);
    	panel.add(minangleSlider);
    	panel.add(new JLabel ("180.0"));
    	panel.add(AngleMin);
    	
    	//creation of the maximum angle of propulsion slider and adding it to the panel
    	panel.add(new JLabel ("TH MAX: "));
    	panel.add(new JLabel ("1.0"));
    	JSlider maxangleSlider = new JSlider (10, 1800, 10);
    	maxangleSlider.addChangeListener(
    	new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider Slider = (JSlider)e.getSource();
				if (Slider.getValueIsAdjusting()) {							//getting the value of the slider
					AngleMax.setValue((double)Slider.getValue()/10);		//setting the value of the slider to the field
				}															//dividing by 10 to get a more specific interval between two values
		}
		}
			);
    	panel.add(maxangleSlider);
    	panel.add(new JLabel ("180.0"));
    	panel.add(AngleMax);    	
    }
    
    public void run() {														//if the Trace button is selected, trace the trajectory of the ball
    	while(true) {
    		pause(200);
    		if(simRun) {
    			if (button.isSelected()) {
    				TracePoint = true;
    			}
    			
    			else { 
    				TracePoint = false;										//if the trace button is not selected, do not trace the trajectory of the ball
    			}
    			doSim();
    			simulationChooser.setSelectedIndex(0);						//before choosing any action, set the action to nothing
    			simRun = false;
    			
    		}
    		
    			
    	}
    	
    	
    }
   
    
    
}











