package readerwriter;

/**
 * Main application class for the Readers-Writers problem.
 */
@SuppressWarnings("java:S106") // To suppress warnings about system.out
public class App {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private App() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Helper record to store application settings.
     *
     * @param numReaders  Number of reader threads.
     * @param numWriters  Number of writer threads.
     * @param restingTime Sleep time between operations.
     */
    private record Settings(int numReaders, int numWriters, int restingTime) {
    }

    /**
     * Entry point of the program.
     *
     * @param args Command line arguments: [numberOfReaders] [numberOfWriters] [restingTime]
     */
    public static void main(String[] args) {
        Settings settings = parseArguments(args);

        Library library = new Library(); // Initializing our resource

        System.out.println("Starting library simulation: " + settings.numReaders() + " readers and " + settings.numWriters() + " writers.");
        System.out.println("Resting time between operations: " + settings.restingTime() + "ms\n");

        for (int i = 1; i <= settings.numReaders(); i++) { // Booting up our readers
            new Reader(library, i, settings.restingTime()).start();
        }

        for (int i = 1; i <= settings.numWriters(); i++) { // Booting up our writers
            new Writer(library, i, settings.restingTime()).start();
        }
    }

    /**
     * Parses command line arguments.
     *
     * @param args Command line arguments.
     * @return Settings object containing parsed values.
     */
    private static Settings parseArguments(String[] args) {
        int numReaders = 10;
        int numWriters = 3;
        int restingTime = 500; // Default sleep time between operations

        if (args.length >= 1) {
            try {
                numReaders = Integer.parseInt(args[0]);
                if (numReaders < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of readers, using default: 10");
            }
        }
        if (args.length >= 2) {
            try {
                numWriters = Integer.parseInt(args[1]);
                if (numWriters < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of writers, using default: 3");
            }
        }
        if (args.length >= 3) {
            try {
                restingTime = Integer.parseInt(args[2]);
                if (restingTime < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid sleep time, using default: 500ms");
            }
        }
        return new Settings(numReaders, numWriters, restingTime);
    }
}
