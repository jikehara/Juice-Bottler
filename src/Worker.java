public class Worker {
    	
    private int timeToComplete;
    private boolean orangeIn;
    private boolean orangeOut;

	public Worker(int passedTimeToComplete, boolean passOrangeIn, boolean passOrangeOut) {
		timeToComplete = passedTimeToComplete;
		orangeIn = passOrangeIn;
		orangeOut = passOrangeOut;
	}
	
	private void doTask() {
		// sleep for the necessary time to complete task
		if (orangeIn) {
			// wait for a notification that an orange has been passed in
		}
		try {
            Thread.sleep(timeToComplete);
            if (orangeOut) {
            	// notify the next worker that an orange has finished this step
            }            
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
	}
}
