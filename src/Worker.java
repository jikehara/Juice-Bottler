public class Worker {

	private int timeToComplete;
	private boolean orangeIn;
	private boolean orangeOut;
	private boolean processingOrange = false;

	public Worker(int passedTimeToComplete, boolean passOrangeIn, boolean passOrangeOut) {
		timeToComplete = passedTimeToComplete;
		orangeIn = passOrangeIn;
		orangeOut = passOrangeOut;
	}

	private synchronized void doTask() {
		// If worker needs oranges to come in to do task and there are none currently passed in, then worker waits
		while (orangeIn && !processingOrange) {
			// wait for a notification that an orange has been passed in
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			processingOrange = true;
			Thread.sleep(timeToComplete);
			if (orangeOut) {
				// notify the next worker that an orange has finished this step
				notifyAll();
			}
			processingOrange = false;
		} catch (InterruptedException e) {
			System.err.println("Incomplete orange processing, juice may be bad");
		}
	}
}
