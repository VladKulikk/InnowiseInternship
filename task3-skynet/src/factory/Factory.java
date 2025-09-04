package factory;

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
    private int availableParts = 0;

    public void setLatches(CountDownLatch factoryLatch, CountDownLatch factionsLatch) {
        this.factoryLatch = factoryLatch;
        this.factionsLatch = factionsLatch;
    }

    public void runDay() {
        try {
            availableParts = random.nextInt(MAX_PARTS_PER_DAY) + 1;

            for (int i = 0; i < availableParts; i++) {
                Thread.sleep(20);
            }

            System.out.println(STR."Factory: Production completed. Produced \{availableParts} parts");
            factoryLatch.countDown();

            factionsLatch.await();

            if (availableParts > 0) {
                System.out.println(STR."ERROR: \{availableParts} parts left in factory! This shouldn't happen.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean collectOnePart(String factionName) {
        lock.lock();
        try {
            if (availableParts <= 0) {
                return false;
            }

            availableParts--;
            return true;

        } finally {
            lock.unlock();
        }
    }
}