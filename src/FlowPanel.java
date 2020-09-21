import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * Handles displaying the GUI and updating the images for water running
 */
public class FlowPanel extends JPanel implements Runnable {
	Terrain land;
	Water water;
	volatile Boolean paused = true;
	int simCounter = 0;
	Master m;

	/**
	 * Main Constructor for FlowPanel
	 * @param terrain A Terrain object
	 * 
	 */
	
	FlowPanel(Terrain terrain, Water w, Master master) {
		land=terrain;
		water=w;
		m = master;
	}
		
	/**
	 * Paints the water and terrain as images
	 */
	@Override
    protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		  
		super.paintComponent(g);
		
		// draw the landscape in greyscale as an image
		if (land.getImage() != null){
			g.drawImage(land.getImage(), 0, 0, null);
		}
		// Overlay water image
		if (water.getImage() != null){
			g.drawImage(water.getImage(), 0, 0, null);
		}
	}
	

	/**
	 * Executes simulation steps, redrawing and counts simulation steps 
	 * and allows for pausing
	 */
	public void run() {	
		// display loop here
		// to do: this should be controlled by the GUI
		// to allow stopping and starting
		
		while(true) {
			
			if (this.paused) {
				continue;
			}
			Flow.setStep(simCounter);
			simCounter++;
			// Sequential
			// Flow.runSimStep(land, water);
			m.runSimStep();
			
			repaint();
			//try { Thread.sleep(); } catch (InterruptedException e) {e.printStackTrace(); }
			
		} 
		
	}

}