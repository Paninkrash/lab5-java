import java.util.*;
import java.util.concurrent.*;

class Request {
    private final int floorFrom;
    private final int floorTo;

    public Request(int floorFrom, int floorTo) {
        this.floorFrom = floorFrom;
        this.floorTo = floorTo;
    }

    public int getFloorFrom() {
        return floorFrom;
    }

    public int getFloorTo() {
        return floorTo;
    }
}

class Elevator implements Runnable {
    private final int id;
    private final Queue<Request> requests = new ConcurrentLinkedQueue<>();
    private boolean running = true;

    public Elevator(int id) {
        this.id = id;
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            Request request = requests.poll();
            if (request != null) {
                processRequest(request);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processRequest(Request request) {
        System.out.println("Лифт " + id + " вызван на этаже " + request.getFloorFrom() +
                ", едет на этаж " + request.getFloorTo());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Лифт " + id + " забрал пассажира на этаже " + request.getFloorFrom() +
                " и доставил на этаж " + request.getFloorTo());
    }
}

class ElevatorSystem {
    public final List<Elevator> elevators = new ArrayList<>();
    private final Random random = new Random();

    public ElevatorSystem(int numberOfElevators) {
        for (int i = 1; i <= numberOfElevators; i++) {
            Elevator elevator = new Elevator(i);
            elevators.add(elevator);
            new Thread(elevator).start();
        }
    }

    public void generateRequests(int numberOfRequests) {
        for (int i = 0; i < numberOfRequests; i++) {
            int floorFrom = random.nextInt(10);
            int floorTo;
            do {
                floorTo = random.nextInt(10);
            } while (floorTo == floorFrom);

            Request request = new Request(floorFrom, floorTo);
            assignRequest(request);
        }
    }

    private void assignRequest(Request request) {
        elevators.get(random.nextInt(elevators.size())).addRequest(request);
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ElevatorSystem elevatorSystem = new ElevatorSystem(3);
        elevatorSystem.generateRequests(10);
        Thread.sleep(15000);
        for (Elevator elevator : elevatorSystem.elevators) {
            elevator.stop();
        }
    }
}
//заяки лифтам назначаются рандомно