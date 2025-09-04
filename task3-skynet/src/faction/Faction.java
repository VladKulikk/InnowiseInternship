package faction;

import factory.Factory;

import java.util.concurrent.CountDownLatch;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Faction implements Runnable {
    private final String name;
    private final Factory factory;
    private final Random random = new Random();

    private CountDownLatch factoryLatch;
    private CountDownLatch factionsLatch;

    private final Map<String, Integer> parts = new HashMap<>();
    private int robotsBuilt = 0;
    private static final int MAX_PARTS_PER_DAY = 5;

    public Faction(String name, Factory factory) {
        this.name = name;
        this.factory = factory;
        parts.put("head", 0);
        parts.put("torso", 0);
        parts.put("hand", 0);
        parts.put("feet", 0);
    }

    public void setLatches(CountDownLatch factoryLatch, CountDownLatch factionsLatch) {
        this.factoryLatch = factoryLatch;
        this.factionsLatch = factionsLatch;
    }

    @Override
    public void run() {
        try {
            factoryLatch.await();

            Thread.sleep(random.nextInt(150));

            int partsCollectedToday = 0;
            while (partsCollectedToday < MAX_PARTS_PER_DAY) {
                boolean collected = factory.collectOnePart(name);
                if (!collected) { break; }

                partsCollectedToday++;

                String[] partTypes = {"head", "torso", "hand", "feet"};
                String partType = partTypes[random.nextInt(partTypes.length)];
                parts.put(partType, parts.get(partType) + 1);

                Thread.sleep(random.nextInt(150) + 30);
            }

            if (partsCollectedToday > 0) {
                System.out.println(STR."\{name}: Total collected today: \{partsCollectedToday} parts");
                buildRobots();
            } else {
                System.out.println(STR."\{name}: No parts collected today");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            factionsLatch.countDown();
        }
    }

    private void buildRobots() {
        int robotsCanBuild = Integer.MAX_VALUE;

        robotsCanBuild = Math.min(robotsCanBuild, parts.get("head"));
        robotsCanBuild = Math.min(robotsCanBuild, parts.get("torso"));
        robotsCanBuild = Math.min(robotsCanBuild, parts.get("hand") / 2);
        robotsCanBuild = Math.min(robotsCanBuild, parts.get("feet") / 2);

        if (robotsCanBuild > 0) {
            parts.put("head", parts.get("head") - robotsCanBuild);
            parts.put("torso", parts.get("torso") - robotsCanBuild);
            parts.put("hand", parts.get("hand") - (robotsCanBuild * 2));
            parts.put("feet", parts.get("feet") - (robotsCanBuild * 2));

            robotsBuilt += robotsCanBuild;
        }
    }

    public int getRobotsBuilt() {
        return robotsBuilt;
    }
}