import java.awt.image.*;
import java.util.Arrays;
import java.awt.Color;


public class Water {
    int[][] waterDepth; // Array parallel to Terrain containing water unit values
    int dimx, dimy; // Dimensions of water array
    volatile BufferedImage img; 

    /**
     * A constructor to create a water object
     * @param x1 The x-dimension on the object array
     * @param y1 The y-dimension of the object array
     */
    public Water(int x1, int y1) {
        dimx = x1;
        dimy = y1;
        waterDepth = new int[dimx][dimy];
        deriveImg();
    }

    public BufferedImage getImage() {
        return img;
    }

    synchronized public int getDepth(int x, int y) {
        return waterDepth[x][y];
    }

    public void addWater(int x, int y) {
        int radius = 3;
        int amount = 3;
        // Loop and +3 the value of every coordinate in a 3 block radius
        for (int i = -radius; i < radius; i ++) {
            for (int j = -radius; j < radius; j++) {
                // If a valid coordinate
                if (x + i >= 0 && x + i <= dimx - 1  && y + j >= 0 && y + j <= dimy - 1) {
                    waterDepth[x + i][y + j] += amount;
                }
            }
        }
        updateImg();
    }

    // Moves 1 unit of water from baseCoords to destCoords
    synchronized public void shiftWater(int baseX, int baseY, int destX, int destY) {
        waterDepth[baseX][baseY]--;
        waterDepth[destX][destY]++;
        //updateImg();
        
    }

    // Generates initial blank image, only used on construction
    void deriveImg() {
        img = new BufferedImage(dimx, dimy, BufferedImage.TYPE_INT_ARGB);
		// Generate image
    }

    // Updates the image to have blue where there is water
    // To be called every time water moves or is created
    void updateImg() { 
        BufferedImage ximg = this.img;

        for(int x=0; x < dimx; x++) {
			for(int y=0; y < dimy; y++) {
                // Set to blue where there is water
                if (waterDepth[x][y] > 0) {

				    Color col = Color.BLUE;
                    ximg.setRGB(x, y, col.getRGB());
                } else {
                    ximg.setRGB(x, y, 0);
                }
			}
        }
        this.img = ximg;
    }

    // Set image to null and the array back to 0s
    void reset() {
        this.img = null;
        for (int[] row: waterDepth){
            Arrays.fill(row, 0);
        }
        this.deriveImg();
    }

    // Remove the water from all edges.
    void clearEdges() {

        // Cant combine into 1 loop becuase of non-square grids
        for (int i = 0; i < dimx; i++) {
            waterDepth[0][i] = 0;
            waterDepth[dimx - 1][i] = 0;

        }
        for (int i = 0; i < dimy; i++) {
            waterDepth[i][0] = 0;
            waterDepth[i][dimy - 1] = 0;

        } 
    }

    
}
