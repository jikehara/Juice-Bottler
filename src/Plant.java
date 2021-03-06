import java.util.LinkedList;
import java.util.Queue;

/*
 *  Adapted from class lab example of Plant.java
 *  Code and concepts for Queue retrieved from https://docs.oracle.com/javase/7/docs/api/java/util/Queue.html
 */
public class Plant implements Runnable {
	// How long do we want to run the juice processing, oranges are we going to juice in each bottle, 
	// and how many workers for the plant
	public static final long PROCESSING_TIME = 5 * 1000;
	public static final int ORANGES_PER_BOTTLE = 4;
	public static final int WORKERS_PER_PLANT = 5;
	// How many plants do we want to process the oranges?
	private static final int NUM_PLANTS = 2;	
	// queues for each "assembly line" stage of an orange
	private Queue<Orange> fetched = new LinkedList<Orange>();
	private Queue<Orange> peeled = new LinkedList<Orange>();
	private Queue<Orange> juiced = new LinkedList<Orange>();
	private Queue<Orange> bottled = new LinkedList<Orange>();
	private Queue<Orange> processed = new LinkedList<Orange>();

	// We have workers, oranges provided, processed, and time to work.
	private Worker[] workers;
	private final Thread thread;
	private volatile int orangesProvided;
	private volatile int orangesProcessed;
	private volatile boolean timeToWork;

	public static void main(String[] args) {
		// Initialize and start "NUM_PLANTS" amount of plants
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
		System.out.println("");
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

	// spawn workers and start them, setting first worker as the fetcher
	public void startPlant() {
		timeToWork = true;
		workers = new Worker[WORKERS_PER_PLANT];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Worker(false);
		}
		workers[0].setFetcher(true);
		thread.start();
	}

	// ends the plant threads
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

	/*
	 * Plant only provides oranges while the plant is running and a worker is
	 * available to begin processing orange
	 */
	public void run() {
		System.out.println(Thread.currentThread().getName() + " Processing oranges");
		for (int i = 0; i < workers.length; i++) {
			workers[i].startWorker();
		}			
		while (timeToWork) {
			// if worker is available, have them begin work
			try {
				if (!workers[0].isProcessingOrange()) {
					fetchOrange(new Orange(), workers[0]);
				} 
				if (!workers[1].isProcessingOrange()) {
					peelOrange(workers[1]);
				} 
				if (!workers[2].isProcessingOrange()) {
					juiceOrange(workers[2]);
				} 
				if (!workers[3].isProcessingOrange()) {
					bottleOrange(workers[3]);
				}
				if (!workers[4].isProcessingOrange()) {
					finishOrange(workers[4]);
				} 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < workers.length; i++) {
			workers[i].stopWorker();
		}
	}

	/*
	 * if the worker can pull an orange from the previous queue then he takes it and
	 * processes it and puts it in the next queue
	 */
	public void processOrange(Queue<Orange> in, Queue<Orange> out) {		
		if (in.peek() != null) {
			Orange o = (Orange) in.remove();
			o.runProcess();
			out.add(o);
		}
	}

	// fetches an orange into the fetched queue and begins the assembly line
	public void fetchOrange(Orange o, Worker w) throws InterruptedException {
		fetched.add(o);
		System.out.print("f");
		orangesProvided++;
	}

	/*
	 * following four processes take orange from one queue, process it, then send it
	 * to the next queue WARNING: Does not follow DRY (Although I did trim it down
	 * substantially)
	 */
	public void peelOrange(Worker w) {
		processOrange(fetched, peeled);
		System.out.print("pe");
	}

	public void juiceOrange(Worker w) {
		processOrange(peeled, juiced);
		System.out.print("j");
	}

	public void bottleOrange(Worker w) {
		processOrange(juiced, bottled);
		System.out.print("b");
	}

	public void finishOrange(Worker w) {
		processOrange(bottled, processed);
		System.out.print("finish");
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

	// returns total full bottles
	public int getBottles() {
		return orangesProcessed / ORANGES_PER_BOTTLE;
	}

	// returns number of oranges that were processed but not bottled
	public int getWaste() {
		return orangesProvided - orangesProcessed + (orangesProcessed % ORANGES_PER_BOTTLE);
	}
}