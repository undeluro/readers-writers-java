package readerwriter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Library class implementing the Readers-Writers problem with constraints.
 * It uses a single Semaphore to manage access and ensure fairness.
 */
public class Library {
    private static final int MAX_READERS = 5;
    private final Semaphore semaphore; // Fair semaphore with MAX_READERS permits.
    private final List<String> inside = new LinkedList<>();
    private final List<String> queue = new LinkedList<>();

    /**
     * Initializes the library with fair semaphore.
     */
    public Library() {
        // Fair semaphore with MAX_READERS permits.
        // Writers will take all permits, readers will take 1 permit each. This allows us to use only one semaphore.
        this.semaphore = new Semaphore(MAX_READERS, true);
    }

    /**
     * Called by a reader who wants to start reading.
     * @param name Name of the reader.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void startReading(String name) throws InterruptedException {
        synchronized (this) {
            queue.add(name);
            log(name + " wants to read"); // Signaling intention to read
        }

        semaphore.acquire(1);

        synchronized (this) {
            queue.remove(name);
            inside.add(name);
            log(name + " enters to read"); // Entering the library
        }
    }

    /**
     * Called by a reader who finished reading.
     * @param name Name of the reader.
     */
    public void stopReading(String name) {
        synchronized (this) {
            inside.remove(name);
            log(name + " leaves after reading"); // Leaving the library
        }
        semaphore.release(1);
    }

    /**
     * Called by a writer who wants to start writing.
     * @param name Name of the writer.
     * @throws InterruptedException If the thread is interrupted.
     */
    public void startWriting(String name) throws InterruptedException {
        synchronized (this) {
            queue.add(name);
            log(name + " wants to write"); // Signaling intention to write
        }

        semaphore.acquire(MAX_READERS);

        synchronized (this) {
            queue.remove(name);
            inside.add(name);
            log(name + " enters to write"); // Entering the library
        }
    }

    /**
     * Called by a writer who finished writing.
     * @param name Name of the writer.
     */
    public void stopWriting(String name) {
        synchronized (this) {
            inside.remove(name);
            log(name + " leaves after writing"); // Leaving the library
        }
        semaphore.release(MAX_READERS); // Release all permits
    }

    /**
     * Logs the current status of the library.
     * @param event The event that triggered logging.
     */
    @SuppressWarnings("java:S106")
    private synchronized void log(String event) {
        int readersInside = 0;
        int writersInside = 0;
        for (String s : inside) { // Count readers and writers
            if (s.startsWith("Reader")) {
                readersInside++;
            } else {
                writersInside++;
            }
        }

        System.out.println("=========== " + event + " ==========="); // Showing the state
        System.out.println("Inside (" + inside.size() + "): " + inside + " [R:" + readersInside + ", W:" + writersInside + "]");
        System.out.println("Queue (" + queue.size() + "): " + queue);
        System.out.println();
    }

    /**
     * Returns the list of people inside for testing.
     * @return List of names.
     */
    public synchronized List<String> getInside() {
        return new LinkedList<>(inside);
    }

    /**
     * Returns the list of people in the queue for testing.
     * @return List of names.
     */
    public synchronized List<String> getQueue() {
        return new LinkedList<>(queue);
    }
}
