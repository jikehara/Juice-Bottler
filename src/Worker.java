public class Worker {
    
    private int timeToComplete;
    private boolean working = true;

	public Worker(int passedTimeToComplete) {
		timeToComplete = passedTimeToComplete;
	}
    
	private void doTask() {
		// sleep for the necessary time to complete task
		try {
            Thread.sleep(timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
	}
}
