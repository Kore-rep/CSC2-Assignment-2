import java.awt.Graphics;
import javax.swing.JPanel;

public class FlowPanel extends JPanel implements Runnable {
	Terrain land;
	Water water;
	volatile Boolean paused = true;
	
	FlowPanel(Terrain terrain, Water w) {
		land=terrain;
		water=w;
	}
		
	// responsible for painting the terrain and water
	// as images
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
	
	public void run() {	
		// display loop here
		// to do: this should be controlled by the GUI
		// to allow stopping and starting
		
		while(this.paused) {
			continue;
		}
		while(!this.paused) {
			repaint();
		}
	}
}