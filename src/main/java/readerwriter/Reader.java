package readerwriter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Reader thread that repeatedly attempts to read from the library.
 */
public class Reader extends Thread {
    private final Library library;
    private final String name;
    private final int restingTime;
    /**
     * Constructs a reader.
     * @param library The library resource.
     * @param id Reader ID.
     * @param restingTime Time to sleep between operations.
     */
    public Reader(Library library, int id, int restingTime) {
        this.library = library;
        this.name = "Reader-" + id;
        this.restingTime = restingTime;
    }

    /**
     * Core loop. Cycles between reading and sleeping (resting).
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                library.startReading(name);
                // Reading time: 1-3 seconds
                Thread.sleep(1000 + ThreadLocalRandom.current().nextInt(2001));

                library.stopReading(name);
                
                // Sleep after reading
                Thread.sleep(restingTime);
            }
        } catch (InterruptedException e) {
            // Thread interrupted, exit
            Thread.currentThread().interrupt();
        }
    }
}
