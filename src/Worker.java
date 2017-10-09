public class Worker {

	// boolean to check if this worker will be getting from a queue (check if they are fetcher or not)
	private boolean isFetcher = false;
	private boolean processingOrange = false;
	Thread thread;

	public Worker(boolean isFetcher) {
		this.isFetcher = isFetcher;
		thread = new Thread("Worker");
	}
	
	public void startWorker() {
		thread.start();
	}
	
	public void stopWorker() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 *  Method for the worker to perform their task
	 *  Adapted from the in-class Mutex demo
	 */
	protected synchronized void doTask() {
		while (isProcessingOrange()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setProcessingOrange(true);		
	}
	
	protected synchronized void completeTask() {
		setProcessingOrange(false);
		notifyAll();
	}	
	/*
	 * gets whether the worker is a fetcher
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

	public void setProcessingOrange(boolean processingOrange) {
		this.processingOrange = processingOrange;
	}
}
