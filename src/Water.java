import java.awt.image.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Color;

/**
 * A class representing water depth across a set of points
 * Generates an image to be overlaid on the terrain image
 * and updated to represent movement
 */
public class Water {
    AtomicInteger[][] waterDepth; // Array parallel to Terrain containing water unit values
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
        waterDepth = new AtomicInteger[dimx][dimy];
        this.reset();
    }

    /**
     * Obtain the current iteration of the image
     * @return BufferedImage of the current image
     */
    public BufferedImage getImage() {
        return img;
    }

    /**
     * Checks how much water is at a location
     * @param x X coordinate to check
     * @param y Y coordiante to check
     * @return Integer with the depth of the water at given location
     */
    synchronized public AtomicInteger getDepth(int x, int y) {
        return waterDepth[x][y];
    }

    /**
     * Adds water in a radius around given point
     * @param x X coordinate of point to add
     * @param y Y coordinate of point to add
     */
    public void addWater(int x, int y) {
        int radius = 3;
        int amount = 3;
        // Loop and +3 the value of every coordinate in a 3 block radius
        for (int i = -radius; i < radius; i ++) {
            for (int j = -radius; j < radius; j++) {
                // If a valid coordinate
                if (x + i >= 0 && x + i <= dimx - 1  && y + j >= 0 && y + j <= dimy - 1) {
                    waterDepth[x + i][y + j].getAndAdd(amount);
                }
            }
        }
        updateImg();
    }

    /**
     * Moves 1 unit of water from baseCoords to destCoords
     * @param baseX X coordinate of base
     * @param baseY Y coordinate of base
     * @param destX X coordinate of dest
     * @param destY Y coordinate of dest
     */
    synchronized public void shiftWater(int baseX, int baseY, int destX, int destY) {
        waterDepth[baseX][baseY].getAndDecrement();
        waterDepth[destX][destY].getAndIncrement();
        
    }

     
    /**
     * Generates initial blank image, only used on construction
     */
    void deriveImg() {
        img = new BufferedImage(dimx, dimy, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Updates the image to have blue where there is water 
     * and set transparent where there isn't
     * 
     * Gets called after every cycle of work is complete
     */
    void updateImg() { 
        BufferedImage ximg = this.img;

        for(int x=0; x < dimx; x++) {
			for(int y=0; y < dimy; y++) {
                // Set to blue where there is water
                if (waterDepth[x][y].get() > 0) {

				    Color col = Color.BLUE;
                    ximg.setRGB(x, y, col.getRGB());
                } else {
                    ximg.setRGB(x, y, 0);
                }
			}
        }
        this.img = ximg;
    }

    /**
     * Set the image to null and restore 0s in the array of water depth
     */
    void reset() {
        this.img = null;
        for (int j = 0; j < dimx; j++){
            for (int i = 0; i < dimy; i++) {
                waterDepth[j][i] = new AtomicInteger(0);
            }
        }
        this.deriveImg();
    }

    /**
     * Sets the water depth to 0 on all the edges.
     */
    void clearEdges() {

        // Cant combine into 1 loop becuase of non-square grids
        for (int i = 0; i < dimx; i++) {
            waterDepth[0][i].set(0);
            waterDepth[dimx - 1][i].set(0);

        }
        for (int i = 0; i < dimy; i++) {
            waterDepth[i][0].set(0);
            waterDepth[i][dimy - 1].set(0);

        } 
    }

    
}
