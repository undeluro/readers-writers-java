# Readers-Writers Problem Simulation

A robust implementation of the classic Third Readers-Writers (Starve-Free) synchronization problem in Java, ensuring fairness and starvation absence using a single Semaphore.

## A little bit of theory

The [Readers-writers problem](https://en.wikipedia.org/wiki/Readers%E2%80%93writers_problem) is a fundamental concurrency challenge involving a shared resource. The goal is to coordinate access such that:
- Multiple **Readers** can access the resource simultaneously.
- Only one **Writer** can access the resource at a time, with exclusive access (no readers or other writers allowed).

### Starvation and Fairness
In many solutions, one type of thread might be prioritized, leading to [starvation](https://en.wikipedia.org/wiki/Starvation_(computer_science)) of the other. For example, if readers are prioritized, a steady stream of readers could block a writer indefinitely.

This implementation guarantees [Fairness](https://en.wikipedia.org/wiki/Fairness_(computer_science)) using a **First-Come, First-Served (FCFS)** policy. Every thread that joins the queue is guaranteed to eventually access the library, provided that each thread stays inside for a finite amount of time.

## Tech Stack

- **Java 21**: Utilizing modern Java features.
- **Maven**: Project management and build automation.
- **JUnit 5**: Unit and integration testing.
- **JaCoCo**: Code coverage verification using the `jacoco-maven-plugin`.
- **Javadoc**: Comprehensive API documentation.
- **SonarQube**: Static code analysis, identifying code smells, and maintaining high code quality.

## Implementation Overview

The core logic resides in the `Library` class, which manages access using a single `java.util.concurrent.Semaphore`.

### Key Constraints:
- **Max 5 Readers**: Up to 5 readers can be in the library at once.
- **Exclusive Writers**: A writer requires exclusive access, meaning 0 readers and 0 other writers.
- **Fairness**: The semaphore is initialized with the `fair = true` flag, which ensures that threads are granted permits in the order they requested them.

### Mechanism:
- The Semaphore is initialized with **5 permits**.
- **Readers** call `acquire(1)` to enter and `release(1)` to leave.
- **Writers** call `acquire(5)` to enter and `release(5)` to leave.
This clever use of permits on a single semaphore naturally enforces both the 5-reader limit and the writer exclusivity while maintaining a single fair queue.

## Usage & Simulation

### Build the project
Ensure you have Maven and JDK 21 installed.
```bash
mvn clean package
```

### Run the simulation
You can run the application with optional parameters:
```bash
java -jar target/reader-writer-project-1.0-SNAPSHOT.jar [numReaders] [numWriters] [restingTime]
```
- `numReaders`: Number of reader threads (default: 10).
- `numWriters`: Number of writer threads (default: 3).
- `restingTime`: Milliseconds a thread sleeps between operations (default: 500).

Example:
```bash
java -jar target/reader-writer-project-1.0-SNAPSHOT.jar 100 5 3000
```

### Simulation Behavior
- Each thread runs in an **infinite loop**.
- **Reading/Writing time**: Randomly assigned between 1 and 3 seconds for each entry.
- **Logging**: Every state change (requesting, entering, leaving) is logged to the console in a structured format.

### Logging Details
The application provides synchronized logs for every event:
- **Event Header**: The specific action taken (e.g., `=========== Reader 1 enters to read ===========`).
- **Inside List**: Shows who is currently in the library and the count of Readers (R) and Writers (W).
- **Queue List**: Shows the order of people waiting at the door (FCFS).

## Project Structure

- `src/main/java/readerwriter/`
    - `App.java`: Entry point, parses arguments and starts the simulation.
    - `Library.java`: The shared resource, implements synchronization logic.
    - `Reader.java`: Reader thread implementation.
    - `Writer.java`: Writer thread implementation.
- `src/test/java/readerwriter/`: Unit and integration tests.
- `javadoc/`: Generated API documentation.

## Documentation

Javadoc documentation is pre-generated in the `javadoc/` folder. To view it, open `javadoc/index.html` in your browser.

To regenerate the documentation:
```bash
mvn javadoc:javadoc
```