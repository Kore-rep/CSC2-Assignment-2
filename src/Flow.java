import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Flow {
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;
	static JLabel stepCounter;
	static Master m;

	// start timer
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	
	// stop timer, return time elapsed in seconds
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
	
	/**
	 * Intializes the GUI components and adds functionality
	 * @param frameX X Dimension of window
	 * @param frameY Y Dimension of window
	 * @param landdata A Terrain object for creating the FlowPanel
	 * @param w A Water object for creating the flowpanel and for use in the buttons
	 * @param master A Master object to allow controlling of threads and creating of FlowPanel
	 */
	public static void setupGUI(int frameX,int frameY,Terrain landdata, Water w, Master master) {
		
		Dimension fsize = new Dimension(800, 800);
    	JFrame frame = new JFrame("Waterflow"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setLayout(new BorderLayout());
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
   
		fp = new FlowPanel(landdata, w, master);
		fp.setPreferredSize(new Dimension(frameX,frameY));
		g.add(fp);
	    
		fp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//Mouse pressed action
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// mouse released action	
				int xCo = e.getX();
				int yCo = e.getY();
				w.addWater(xCo, yCo);
				fp.repaint();
		 	}
		});

		JPanel b = new JPanel();
		b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
		stepCounter = new JLabel("Simulation Step: ");
		JButton endB = new JButton("End");
		JButton resetB = new JButton("Reset");
		JButton playB = new JButton("Play");
		JButton pauseB = new JButton("Pause");


		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do ask threads to stop
				frame.dispose();
				m.terminateThreads();
				
			}
		});
		resetB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do reset simulation
				w.reset();
				fp.simCounter = 0;
				fp.paused = true;
				fp.repaint();
				//m.resetAll();
			}
		});

		playB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do resume threads
				fp.paused = false;
								
				
			}
		});
		pauseB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do pause
				fp.paused = true;
			}
		});
		b.add(resetB);
		b.add(pauseB);
		b.add(playB);
		b.add(endB);
		b.add(stepCounter);
		g.add(b);
    	
		frame.setSize(frameX, frameY+50);	// a little extra space at the bottom for buttons
      	frame.setLocationRelativeTo(null);  // center window on screen
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
        Thread fpt = new Thread(fp);
        fpt.start();
	}
	
		
	public static void main(String[] args) {
		Terrain landdata = new Terrain();
		
		// check that number of command line arguments is correct
		if(args.length != 1)
		{
			System.out.println("Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
			System.exit(0);
		}
				
		// landscape information from file supplied as argument
		// 
		landdata.readData(args[0]);
		Water waterSim = new Water(landdata.getDimX(), landdata.getDimY());	
		m = new Master(landdata, waterSim);	
		frameX = landdata.getDimX();
		frameY = landdata.getDimY();
		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landdata, waterSim, m));
		
		// to do: initialise and start simulation
		//runSim(landdata, waterSim);
		
	}

	public static void runSimStep(Terrain t, Water w) {
		/* One sim Step is:
			Clear Edges of Water
			Traverse permuted locations
			Adjust water
		*/
		w.clearEdges();
		int counter = 0;
		for (int i = 0; i < t.dim(); i ++) {
			counter++;
			int[] inds = new int[2];
			t.getPermute(i, inds);
			   // Not on an edge 
			   // Increases efficiency instead of clearing edges every time.
			if (inds[0] == 0 || inds[0] == t.getDimX()-1 || inds[1] == 0 || inds[1] == t.getDimY()-1) {
				continue;
			} else {
				if (w.getDepth(inds[0], inds[1]) != 0) {
					//System.out.println("water at: " + inds[0] +" " + inds[1]);
					float lowSur;
					float compSur;
					int[] low = new int[2];
					low[0] = inds[0];
					low[1] = inds[1];
					for (int j = -1; j < 2; j++) {
						for (int k = -1; k < 2; k++) {
							// Loop in a square around the given coords, to determine
							// lowest neighboring point.
							lowSur = t.height[low[0]][low[1]] + (float) 0.01 * w.getDepth(low[0], low[1]);
							compSur = t.height[inds[0] + j][inds[1] + k]
									+ (float) 0.01 * w.getDepth(inds[0] + j, inds[1] + k);
							if (compSur < lowSur) {
								low[0] = inds[0] + j;
								low[1] = inds[1] + k;
							}
						}
					}
					w.shiftWater(inds[0], inds[1], low[0], low[1]);
				}
			//System.out.println("running");
			/*try {
				System.out.println("Waiting");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} */
			
		}
		
		}
	}

	static void setStep(int x) {
		stepCounter.setText("Simulation Step: " + x);
	}
}