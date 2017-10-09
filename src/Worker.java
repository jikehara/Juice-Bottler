

public class Worker implements Runnable {

	/*
	 * Booleans to check if a worker is fetching oranges or performing other tasks,
	 * if they're working or not, and if they're performing a task or not.
	 */
	private boolean isFetcher = false;
	private boolean processingOrange = false;
	private boolean timeToWork = false;
	private Orange o;
	Thread thread;

	/**
	 * Constructor with isFetcher param to determine if a worker fetches oranges
	 * or performs a different task
	 * @param isFetcher
	 */
	public Worker(boolean isFetcher) {
		this.isFetcher = isFetcher;
		thread = new Thread("Worker");
	}

	public void startWorker() {
		timeToWork = true;
		thread.start();
	}

	public void stopWorker() {
		timeToWork = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Method for the worker to perform their task Adapted from the in-class Mutex
	 */
	private synchronized void doTask() {
		while (this.processingOrange) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setProcessingOrange(true);
	}

	private synchronized void completeTask() {
		setProcessingOrange(false);
		notifyAll();
	}

	/*
	 * getters and setters whether the worker is a fetcher, is processing an orange, and get an orange
	 */
	public boolean amIFetcher() {
		return isFetcher;
	}

	public void setFetcher(boolean fetcher) {
		isFetcher = fetcher;
	}

	public boolean isProcessingOrange() {
		return processingOrange;
	}

	/**
	 * returns the boolean value of whether a worker is processing orange or not
	 * @param processingOrange
	 */
	public void setProcessingOrange(boolean processingOrange) {
		this.processingOrange = processingOrange;
	}

	/**
	 * returns the state of a passed Orange o
	 * @param o
	 */
	public void getOrange(Orange o) {
		this.o = o;
	}
	/**
	 * Allows worker to process oranges
	 */
	public void run() {
		// TODO Auto-generated method stub
		while (timeToWork) {
			 doTask();
			 o.runProcess();
			 completeTask();
		}
	}
}
