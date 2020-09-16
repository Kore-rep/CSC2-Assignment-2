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

	// start timer
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	
	// stop timer, return time elapsed in seconds
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
	
	public static void setupGUI(int frameX,int frameY,Terrain landdata, Water w) {
		
		Dimension fsize = new Dimension(800, 800);
    	JFrame frame = new JFrame("Waterflow"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setLayout(new BorderLayout());
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
   
		fp = new FlowPanel(landdata, w);
		fp.setPreferredSize(new Dimension(frameX,frameY));
		g.add(fp);
	    
		// to do: add a MouseListener, buttons and ActionListeners on those buttons
		fp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//Mouse pressed action
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// mouse released action
				if (fp.paused) {	
			 		int xCo = e.getX();
			 		int yCo = e.getY();
					w.addWater(xCo, yCo);
					fp.repaint();
				}
		 	}
		});

		JPanel b = new JPanel();
	    b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
		JButton endB = new JButton("End");
		JButton resetB = new JButton("Reset");
		JButton playB = new JButton("Play");
		JButton pauseB = new JButton("Pause");
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do ask threads to stop
				frame.dispose();
				;
			}
		});
		resetB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do reset simulation
				w.reset();
			}
		});
		playB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do resume threads
				fp.paused = false;
				runSim(landdata, w);
				
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
		
		frameX = landdata.getDimX();
		frameY = landdata.getDimY();
		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landdata, waterSim));
		
		// to do: initialise and start simulation
		//runSim(landdata, waterSim);
	}

	public static void runSim(Terrain t, Water w) {
		int x1 = t.getDimX();
		int y1 = t.getDimY();
		int counter = 0;
		/* One sim Step is:
			Clear Edges of Water
			Traverse permuted locations
			Adjust water
		*/
		int[] inds = new int[2];
		while(counter < 10) {
			counter++;
			fp.repaint();
			System.out.println(counter);
			if (fp.paused) {
				continue;
			}
			w.clearEdges();
			for (int i = 0; i < x1*y1; i ++) {
				t.getPermute(i, inds);
				// If not on an edge
				if (inds[0] == 0 || inds[0] == x1 - 1 || inds[1] == 0 || inds[1] == y1 - 1) {
                    continue;
                } else {
                    // If it has water
                    if (w.waterDepth[inds[0]][inds[1]] != 0) {
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
				}
			}
		}
	}
}