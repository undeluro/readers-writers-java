package readerwriter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReaderWriterTest {

    @Test
    void testReaderWriterRun() throws InterruptedException {
        Library library = new Library();
        // Use short sleep time for testing
        Reader reader = new Reader(library, 1, 100);
        Writer writer = new Writer(library, 1, 100);

        reader.start();
        writer.start();

        // Let them run for a bit
        Thread.sleep(6000);

        reader.interrupt();
        writer.interrupt();

        reader.join(2000);
        writer.join(2000);

        assertFalse(reader.isAlive());
        assertFalse(writer.isAlive());
    }

    @Test
    void testReaderInterruptedInitially() throws InterruptedException {
        Library library = new Library();
        Reader reader = new Reader(library, 2, 100);
        reader.interrupt();
        reader.start();
        reader.join(1000);
        assertFalse(reader.isAlive()); // ensures that if a thread is canceled before it begins its task
    }

    @Test
     void testWriterInterruptedInitially() throws InterruptedException {
        Library library = new Library();
        Writer writer = new Writer(library, 2, 100);
        writer.interrupt();
        writer.start();
        writer.join(1000);
        assertFalse(writer.isAlive());
    }
}
