# Queue Simulator

A multithreaded simulation of clients arriving at service queues, such as checkout lines, with a live animated Swing GUI. Each queue runs on its own thread, and clients are sent to a queue by a strategy you can choose.

Built as a university assignment for the **Programming Techniques** course (2026).

## Features

- You set the number of clients, the number of queues, the simulation length, and the intervals for arrival time and service time. Clients are then generated randomly.
- **Two dispatch strategies:**
  - **Shortest Queue:** the client joins the queue with the fewest people
  - **Shortest Time:** the client joins the queue with the lowest total waiting time
- An **animated view** of clients moving through the queues, updated every second
- Results at the end:
  - average waiting time
  - average service time
  - **peak hour**, the moment with the most clients in the queues
- The full simulation log is written to `log.txt`. Sample logs for both strategies are in [`Teste_ShortestQueue`](Teste_ShortestQueue) and [`Teste_ShortestTime`](Teste_ShortestTime).

## Concurrency design

- Each `Server` (queue) is a `Runnable` running in a fixed thread pool (`ExecutorService`).
- Clients wait in a thread-safe `LinkedBlockingQueue`, and the waiting time of each queue is kept in an `AtomicInteger`.
- `SimulationManager` runs on its own thread. Every second it sends the clients that have arrived, updates the statistics and refreshes the GUI.
- The **Strategy pattern** (`Strategy`, `ConcreteStrategyQueue` and `ConcreteStrategyTime`) lets the dispatch policy change at runtime through `SelectionPolicy`.

| Package | Responsibility |
|---|---|
| `model` | `Task` (client) and `Server` (queue thread) |
| `strategy` | Dispatch strategies |
| `businesslogic` | `Scheduler` and `SimulationManager` (simulation loop) |
| `gui` | Swing frame and the queue animation panel |

UML diagrams (class, package and use-case, in draw.io format) are in [`src/main/resources`](src/main/resources).

## Tech stack

Java 25 · Swing · `java.util.concurrent` · Maven

## Running

```bash
mvn compile
mvn exec:java -Dexec.mainClass="org.example.main.Main"
```

Or open the project in IntelliJ IDEA and run `org.example.main.Main`.
