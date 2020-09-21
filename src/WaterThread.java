
/**
 * A thread class for implementation of parallel water simulation
 */
public class WaterThread extends java.lang.Thread {
    int[] indexes;
    Terrain land;
    Water water;
    int dimx, dimy;
    Master master;

    /**
     * Main constructor for WaterThread
     * @param t Terrain object to get information from
     * @param w Water object to manipulate
     * @param ind An index of size 2, with a lower and upper bound of indexes to traverse
     * @param m Master object to use for synchronization
     */
    public WaterThread(Terrain t, Water w, int[] ind, Master m) {
        indexes = ind;
        land = t;
        water = w;
        dimx = land.getDimX();
        dimy = land.getDimY();
        master = m;
    }

    /**
     * Does 1 unit of work, then notifies master that it is finished
     * and waits to be notified
     */
    public void run() {
        int[] low = new int[2];
        float lowSur;
        float compSur;
        int[] ind = new int[2];
        
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
                    if (water.getDepth(ind[0], ind[1]).get() != 0) {                       
                        
                        low[0] = ind[0];
                        low[1] = ind[1];
                        for (int j = -1; j < 2; j++) {
                            for (int k = -1; k < 2; k++) {
                                // Loop in a square around the given coords, checking if water
                                // needs to flow.
                                // Has to update original surface each time in case
                                // water flows out.
                                lowSur = land.height[low[0]][low[1]] + (float) 0.01 * water.getDepth(low[0], low[1]).get();
                                compSur = land.height[ind[0] + j][ind[1] + k]
                                        + (float) 0.01 * water.getDepth(ind[0] + j, ind[1] + k).get();

                                if (compSur < lowSur) {
                                    low[0] = ind[0] + j;
                                    low[1] = ind[1] + k;
                                }
                            }

                        }
                        water.shiftWater(ind[0], ind[1], low[0], low[1]);
                    }
                }
            }

            master.completedThreads.getAndIncrement(); // Notify Master then wait
            try { synchronized(this) { wait(); } } catch (InterruptedException e) { return; }
        }
        
    }
    
    
}
