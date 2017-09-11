public class Plant {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 8 * 1000;

    public static void main(String[] args) {
        // Startup a single plant
        Plant p = new Plant();

        // Give the plants time to do work
        long endTime = System.currentTimeMillis() + PROCESSING_TIME;
        int processed = 0;
        while (System.currentTimeMillis() < endTime) {
            p.processEntireOrange(new Orange());
            processed++;
        }

        // Summarize the results
        System.out.println("Total processed/processed = " + processed + "/" + p.getProcessedOranges());
        System.out.println("Created " + p.getBottles() +
                           ", wasted " + p.getWaste() + " oranges");
    }

    public final int ORANGES_PER_BOTTLE = 4;

    private int orangesProcessed;

    Plant() {
        orangesProcessed = 0;
    }

    public void processEntireOrange(Orange o) {
        while (o.getState() != Orange.State.Bottled) {
            o.runProcess();
            // o.nextState();
        }
        orangesProcessed++;
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