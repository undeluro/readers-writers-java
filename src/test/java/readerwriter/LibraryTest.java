package readerwriter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class LibraryTest {

    @Test
    void testLibraryInitialState() { // No readers or writers initially
        Library library = new Library();
        assertTrue(library.getInside().isEmpty());
        assertTrue(library.getQueue().isEmpty());
    }

    @Test
    void testSingleReader() throws InterruptedException { // One in one out
        Library library = new Library();
        library.startReading("Reader-1");
        List<String> inside = library.getInside();
        assertEquals(1, inside.size());
        assertEquals("Reader-1", inside.getFirst());
        assertTrue(library.getQueue().isEmpty());

        library.stopReading("Reader-1");
        assertTrue(library.getInside().isEmpty());
    }

    @Test
    void testMaxReaders() throws InterruptedException {
        Library library = new Library();
        for (int i = 1; i <= 5; i++) {
            library.startReading("Reader-" + i);
        }
        assertEquals(5, library.getInside().size());

        CountDownLatch latch = new CountDownLatch(1);
        Thread sixthReader = new Thread(() -> {
            try {
                library.startReading("Reader-6");
                latch.countDown();
            } catch (InterruptedException e) {
                // Ignore
            }
        });
        sixthReader.start();

        assertFalse(latch.await(500, TimeUnit.MILLISECONDS)); // False since occupied by 5 other
        assertEquals(5, library.getInside().size()); // No overflowing, full load
        assertTrue(library.getQueue().contains("Reader-6")); // But in queue

        library.stopReading("Reader-1");
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS)); // countdown reached
        assertEquals(5, library.getInside().size());
        assertTrue(library.getInside().contains("Reader-6")); // in library
        assertFalse(library.getQueue().contains("Reader-6")); // not in queue
        
        for (int i = 2; i <= 6; i++) {
            library.stopReading("Reader-" + i);
        }
        assertTrue(library.getInside().isEmpty());
    }

    @Test
    void testSingleWriter() throws InterruptedException { // one in one out
        Library library = new Library();
        library.startWriting("Writer-1");
        assertEquals(1, library.getInside().size());
        assertEquals("Writer-1", library.getInside().getFirst());

        library.stopWriting("Writer-1");
        assertTrue(library.getInside().isEmpty());
    }

    @Test
    void testWriterExclusivityWithReader() throws InterruptedException {
        Library library = new Library();
        library.startReading("Reader-1");

        CountDownLatch latch = new CountDownLatch(1);
        Thread writer = new Thread(() -> {
            try {
                library.startWriting("Writer-1");
                latch.countDown(); // signal that writer entered
            } catch (InterruptedException e) {
                // Ignore
            }
        });
        writer.start();

        assertFalse(latch.await(500, TimeUnit.MILLISECONDS));
        assertEquals(1, library.getInside().size());
        assertTrue(library.getQueue().contains("Writer-1"));

        library.stopReading("Reader-1");
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
        assertEquals(1, library.getInside().size()); // writer in library alone
        assertTrue(library.getInside().contains("Writer-1"));

        library.stopWriting("Writer-1");
    }

    @Test
    void testWriterExclusivityWithWriter() throws InterruptedException { // only one writer at a time
        Library library = new Library();
        library.startWriting("Writer-1");

        CountDownLatch latch = new CountDownLatch(1);
        Thread writer2 = new Thread(() -> {
            try {
                library.startWriting("Writer-2");
                latch.countDown();
            } catch (InterruptedException e) {
                // Ignore
            }
        });
        writer2.start();

        assertFalse(latch.await(500, TimeUnit.MILLISECONDS));
        assertEquals(1, library.getInside().size());
        assertTrue(library.getQueue().contains("Writer-2"));

        library.stopWriting("Writer-1");
        assertTrue(latch.await(500, TimeUnit.MILLISECONDS)); // writer2 entered
        assertEquals(1, library.getInside().size()); // alone
        assertTrue(library.getInside().contains("Writer-2")); // writer2 in library

        library.stopWriting("Writer-2");
    }

    @Test
    void testFairness() throws InterruptedException {
        Library library = new Library();
        library.startReading("Reader-1");

        CountDownLatch writerLatch = new CountDownLatch(1);
        Thread writer = new Thread(() -> {
            try {
                library.startWriting("Writer-1");
                writerLatch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        writer.start();
        
        // Wait a bit to ensure writer is in queue
        Thread.sleep(100);

        CountDownLatch readerLatch = new CountDownLatch(1);
        Thread reader2 = new Thread(() -> {
            try {
                library.startReading("Reader-2");
                readerLatch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        reader2.start();

        // Reader-1 is inside. Writer-1 and Reader-2 are in queue.
        // Because it's fair, Writer-1 must enter before Reader-2.
        
        assertFalse(writerLatch.await(500, TimeUnit.MILLISECONDS));
        assertFalse(readerLatch.await(500, TimeUnit.MILLISECONDS));

        library.stopReading("Reader-1");
        
        assertTrue(writerLatch.await(500, TimeUnit.MILLISECONDS));
        assertFalse(readerLatch.await(500, TimeUnit.MILLISECONDS));
        
        assertEquals(1, library.getInside().size());
        assertTrue(library.getInside().contains("Writer-1"));

        library.stopWriting("Writer-1");
        assertTrue(readerLatch.await(500, TimeUnit.MILLISECONDS));
        assertTrue(library.getInside().contains("Reader-2"));
        
        library.stopReading("Reader-2");
    }

    @Test
    void testInterruptionInStartReading() throws InterruptedException {
        Library library = new Library();
        library.startWriting("Writer-1");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean interruptedCaught = new AtomicBoolean(false);
        Thread reader = new Thread(() -> {
            try {
                library.startReading("Reader-1");
            } catch (InterruptedException e) {
                interruptedCaught.set(true);
                latch.countDown();
            }
        });
        reader.start();
        Thread.sleep(100);
        reader.interrupt();

        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
        assertTrue(interruptedCaught.get());
        
        library.stopWriting("Writer-1");
    }

    @Test
    void testInterruptionInStartWriting() throws InterruptedException {
        Library library = new Library();
        library.startReading("Reader-1");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean interruptedCaught = new AtomicBoolean(false);
        Thread writer = new Thread(() -> {
            try {
                library.startWriting("Writer-1");
            } catch (InterruptedException e) {
                interruptedCaught.set(true);
                latch.countDown();
            }
        });
        writer.start();
        Thread.sleep(3500);
        writer.interrupt();

        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
        assertTrue(interruptedCaught.get());
        
        library.stopReading("Reader-1");
    }
}
