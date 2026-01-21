package readerwriter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Writer thread that repeatedly attempts to write to the library.
 */
public class Writer extends Thread {
    private final Library library;
    private final String name;
    private final int restingTime;
    /**
     * Constructs a writer.
     * @param library The library resource.
     * @param id Writer ID.
     * @param restingTime Time to sleep between operations.
     */
    public Writer(Library library, int id, int restingTime) {
        this.library = library;
        this.name = "Writer-" + id;
        this.restingTime = restingTime;
    }

    /**
     * Core loop. Cycles between writing and sleeping (resting).
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                library.startWriting(name);
                // Writing time: 1-3 seconds
                Thread.sleep(1000 + ThreadLocalRandom.current().nextInt(2001));
                library.stopWriting(name);
                
                // Sleep after writing
                Thread.sleep(restingTime);
            }
        } catch (InterruptedException e) {
            // Thread interrupted, exit
            Thread.currentThread().interrupt();
        }
    }
}
