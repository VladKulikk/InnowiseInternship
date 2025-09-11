package factory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Factory {
    private static final int MAX_PARTS_PER_DAY = 10;
    private final Random random = new Random();
    private final Lock lock = new ReentrantLock();

    private CountDownLatch factoryLatch;
    private CountDownLatch factionsLatch;
    private Deque<String> partsDeque = new ArrayDeque<>();
    private final String[] partTypes = {"head", "torso", "hand", "feet"};

    public void setLatches(CountDownLatch factoryLatch, CountDownLatch factionsLatch) {
        this.factoryLatch = factoryLatch;
        this.factionsLatch = factionsLatch;
    }

    public void runDay() {
        try {
            int partsToProduce = random.nextInt(MAX_PARTS_PER_DAY) + 1;

            for (int i = 0; i < partsToProduce; i++) {
                Thread.sleep(20);

                String partType = partTypes[random.nextInt(partTypes.length)];
                partsDeque.add(partType);
            }

            System.out.println(STR."Factory: Production completed. Produced \{partsToProduce} parts");
            factoryLatch.countDown();

            factionsLatch.await();

            if (!partsDeque.isEmpty()) {
                System.out.println(STR."ERROR: \{partsToProduce} parts left in factory! This shouldn't happen.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String takePart() {
        lock.lock();
        try {
            if (partsDeque.isEmpty()) {
                return null;
            }

            return partsDeque.removeFirst();

        } finally {
            lock.unlock();
        }
    }
}