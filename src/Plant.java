/*
 *  Adapted from class lab example of Plant.java
 */
public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;
    // How many plants do we want to process the oranges?
    private static final int NUM_PLANTS = 5;
    // How many oranges are we going to juice in each bottle
    public final int ORANGES_PER_BOTTLE = 4;
    // How many workers per plant
    public static final int WORKERS_PER_PLANT = 5; 
    // times for workers to complete a task
    public static final int timeToFetch = 10;
    public static final int timeToPeel = 40;
    public static final int timeToJuice = 40;
    public static final int timeToBottle = 30;
    public static final int timeToProcess = 40;
    
    // We have oranges provided, procesed, and time to work.
    private final Thread thread;
    private int orangesProvided;
    private int orangesProcessed;
    private volatile boolean timeToWork;
    
    // oranges
//    private int orangesFetched;
//    private int orangesPeeled;
//    private int orangesJuiced;
//    private int orangesBottled;
//    private int orangesProcessed;
    
    public static void main(String[] args) {
        // Startup the plants
    	Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
           plants[i] = new Plant(); 
           plants[i].startPlant();
        }

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) {
           p.stopPlant();
        }
        for (Plant p : plants) {
           p.waitToStop();
        }

        // Summarize the results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles +
                           " bottles of orange juice, wasted " + totalWasted + " oranges");
    }

    // Give time for the program to run, or pass it an error message if it breaks
    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

    Plant() {
        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, "Plant");
    }

    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    public void stopPlant() {
        timeToWork = false;
    }

    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " Processing oranges");
        while (timeToWork) {
        	orangesProvided++;
            processEntireOrange(new Orange());            
            System.out.print(".");
        }
        System.out.println("");
    }

    // tracks the total number of fully processed oranges
    public void processEntireOrange(Orange o) {
        while (o.getState() != Orange.State.Bottled) {
            o.runProcess();
        }
        orangesProcessed++;
    }

    public int getProvidedOranges() {
        return orangesProvided;
    }

    public int getProcessedOranges() {
        return orangesProcessed;
    }

    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }
}