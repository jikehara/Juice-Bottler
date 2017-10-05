import java.util.Queue;

/*
 *  Adapted from class lab example of Plant.java
 *  Code and concepts for Queue retrieved from https://docs.oracle.com/javase/7/docs/api/java/util/Queue.html
 */
public class Plant implements Runnable {
	// How long do we want to run the juice processing
	public static final long PROCESSING_TIME = 5 * 1000;
	// How many plants do we want to process the oranges?
	private static final int NUM_PLANTS = 1;
	// How many oranges are we going to juice in each bottle
	public final int ORANGES_PER_BOTTLE = 4;
	// How many workers per plant
	public static final int WORKERS_PER_PLANT = 5;
	// times for workers to complete a task
//	private Queue<Object> fetched;
	private Queue<Object> peeled;
	private Queue<Object> juiced;
	private Queue<Object> bottled;
	private Queue<Object> processed;

	// We have oranges provided, procesed, and time to work.
	private final Thread thread;
	private int orangesProvided;
	private int orangesProcessed;
	private volatile boolean timeToWork;


	public static void main(String[] args) {
		// Intialize and start "NUM_PLANTS" amount of plants
		Plant[] plants = new Plant[NUM_PLANTS];
		for (int i = 0; i < NUM_PLANTS; i++) {
			plants[i] = new Plant();
			plants[i].startPlant();
		}

		// Give the plants time to process oranges
		delay(PROCESSING_TIME, "Plant malfunction");

		// End the time for orange processing and wait for it to shutdown
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
		System.out.println("Created " + totalBottles + " bottles of orange juice, wasted " + totalWasted + " oranges");
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

	// spawn workers and start them
	public void startPlant() {
		timeToWork = true;
		Worker[] workers = new Worker[WORKERS_PER_PLANT];
		thread.start();
	}

	// Lines 88-98 End all threads
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

	// 
	public void processEntireOrange(Orange o) {
		while (o.getState() != Orange.State.Bottled) {
			o.runProcess();
		}
		orangesProcessed++;
	}

	// returns total oranges fetched/provided
	public int getProvidedOranges() {
		return orangesProvided;
	}

	// returns total number of oranges processed
	public int getProcessedOranges() {
		return orangesProcessed;
	}

	// returns total bottles
	public int getBottles() {
		return orangesProcessed / ORANGES_PER_BOTTLE;
	}

	// returns number of oranges that were processed but not bottled
	public int getWaste() {
		return orangesProcessed % ORANGES_PER_BOTTLE;
	}
}