
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WaterThread extends java.lang.Thread {
    int[] indexes;
    Terrain land;
    Water water;
    int dimx, dimy;
    AtomicBoolean finished;
    Master master;

    public WaterThread(Terrain t, Water w, int[] ind, Master m) {
        indexes = ind;
        land = t;
        water = w;
        dimx = land.getDimX();
        dimy = land.getDimY();
        master = m;
        //System.out.println(getName() + ": " + indexes[0] + " -> " + indexes[1]);
    }

    public void run() {
        int[] low = new int[2];
        float lowSur;
        float compSur;
        int[] ind = new int[2];
        //System.out.println("Started " + this.getName() + "  " + this.indexes[0] + " -> " + this.indexes[1]);
        
        while (true) {
            

            for (int i = indexes[0]; i < indexes[1]; i++) {
                
                 // Loop through all grid coords
                // Generate new coords to check
                land.getPermute(i, ind);
                // If this set isn't on any edges
                if (ind[0] == 0 || ind[0] == dimx - 1 || ind[1] == 0 || ind[1] == dimy - 1) {
                    continue;
                } else {
                    // If it has water
                    if (water.getDepth(ind[0], ind[1]) != 0) {
                        //System.out.println("Looping water " + this.getName());
                        
                        
                        low[0] = ind[0];
                        low[1] = ind[1];
                        for (int j = -1; j < 2; j++) {
                            for (int k = -1; k < 2; k++) {
                                // Loop in a square around the given coords, checking if water
                                // needs to flow.
                                // Has to update original surface each time in case
                                // water flows out.
                                lowSur = land.height[low[0]][low[1]] + (float) 0.01 * water.getDepth(low[0], low[1]);
                                compSur = land.height[ind[0] + j][ind[1] + k]
                                        + (float) 0.01 * water.getDepth(ind[0] + j, ind[1] + k);

                                if (compSur < lowSur) {
                                    low[0] = ind[0] + j;
                                    low[1] = ind[1] + k;
                                }
                            }

                        }
                        water.shiftWater(ind[0], ind[1], low[0], low[1]);
                        //System.out.println(this.getName() + " moved water (" + ind[0] + ", " + ind[1] + ") -> (" + low[0] + ", " + low[1] + ")");
                    }
                }
            }

            master.completedThreads.getAndIncrement();
            try { synchronized(this) { wait(); } } catch (InterruptedException e) { return; }
        }
        
    }
    
    
}
