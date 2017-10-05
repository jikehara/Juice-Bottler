/*
 * This class is taken from in-class discussion and lab work.  
 */
public class Orange {

	public enum State {
		// numbers show the time it takes to pass from one state to the next
		Fetched(10), Peeled(20), Squeezed(30), Bottled(20), Processed(40);

		private static final int finalIndex = State.values().length - 1;

		final int timeToComplete;

		State(int timeToComplete) {
			this.timeToComplete = timeToComplete;
		}

		State getNext() {
			int currIndex = this.ordinal();
			if (currIndex >= finalIndex) {
				throw new IllegalStateException("Already at final state");
			}
			return State.values()[currIndex + 1];
		}
	}

	private State state;

	// constructor -> initialized state to "Fetched" and then begin processing Orange
	public Orange() {
		state = State.Fetched;
		doWork();
	}

	public State getState() {
		return state;
	}

	public void runProcess() {
		// Don't attempt to process an already completed orange
		if (state == State.Processed) {
			throw new IllegalStateException("This orange has already been processed");
		}
		state = state.getNext();
		doWork();
	}

	private void doWork() {
		// Sleep for the amount of time necessary to do the work
		try {
			Thread.sleep(state.timeToComplete);
		} catch (InterruptedException e) {
			System.err.println("Incomplete orange processing, juice may be bad");
		}
	}
}