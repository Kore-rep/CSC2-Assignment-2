import java.lang.Thread.State;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An object used for coordinating threads
 */
public class Master {

    Terrain land;
    Water water;
    
    // An array used for ease of storing threads
    WaterThread[] threadList;

    // An integer that gets incremented when a thread completed a unit of work
    AtomicInteger completedThreads;
    Boolean started = false;

    /**
     * Constructor for Master Object
     * @param t A terrain object to get height information from
     * @param w A water object to get info from and make changes to.
     */
    Master(Terrain t, Water w) {

        land = t;
        water = w;
        threadList = new WaterThread[4];
        completedThreads = new AtomicInteger(0);
        createThreads();

    }


    /**
     * Notifys all threads that are asleep and are alive
     * @throws InterruptedException required for notifying
     */
    public void resumeThreads() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            if (threadList[i].getState() == State.WAITING && threadList[i].isAlive()) {
                synchronized (threadList[i]) {
                    threadList[i].notify();
                }

            }
        }
    }

    /**
     * Calls interrupt() for all threads
     */
    public void terminateThreads() {
        for (int i = 0; i < 4; i++) {
            threadList[i].interrupt();

        }
    }

    /**
     * 
     * @return A boolean value for sync in FlowPanel, will always be true unless a
     * catastrophic failure occurs
     */
    public boolean syncThreads() {
        while (true) {

            if (completedThreads.get() == 4) {
                completedThreads.set(0);
                return true;
            }
        }
    }

    /**
     * Runs a single step of the simulation
     * Clear the edges of water
     * Do work in the threads
     * Synchronize at the end
     * Update the image
     * 
     * If this is the first time running the threads, start them instead
     */
    public void runSimStep() {
        water.clearEdges();
        if (!started) {
            System.out.println("Starting");
            for (int i = 0; i < 4; i++) {
                threadList[i].start();

            }
            started = true;
        } else {
            for (int i = 0; i < 4; i++) {
                synchronized(threadList[i]) {
                    threadList[i].notify();
                }
                
    
            }
        }
        syncThreads();
        water.updateImg();


    }

    /**
     * Kill threads and recreate them
     * Auxillary Method, not implemented currently
     */
    public void resetAll() {
        started = false;
        terminateThreads();
        createThreads();
    }

    /**
     * Creates the four threads by dividing up the work into four parts
     * Prepare them for starting
     */
    private void createThreads() {

        // Divide the dimensions of the data into 4 equal parts
        // Assign each thread a secion of this
        int dims = (land.dim()/4);
        int[] temp1 = new int[2];
        int[] temp2 = new int[2];
        int[] temp3 = new int[2];
        int[] temp4 = new int[2];
        temp1[0] = 0;
        temp1[1] = dims;
        threadList[0] = new WaterThread(land, water, temp1, this);

        temp2[0] = dims;
        temp2[1] = dims*2;
        threadList[1] = new WaterThread(land, water, temp2, this);

        temp3[0] = dims*2;
        temp3[1] = dims*3;
        threadList[2] = new WaterThread(land, water, temp3, this);

        temp4[0] = dims*3;
        temp4[1] = dims*4;   
        threadList[3] = new WaterThread(land, water, temp4, this);
    }
    
}
