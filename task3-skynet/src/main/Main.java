package main;

import faction.Faction;
import factory.Factory;

import java.util.concurrent.CountDownLatch;

public class Main {
    private static final int DAYS = 100;

    public static void main(String[] args) {
        Factory factory = new Factory();

        Faction world = new Faction("World", factory);
        Faction wednesday = new Faction("Wednesday", factory);

        for (int day = 0; day < DAYS; day++) {
            System.out.println(STR."\nDAY \{day + 1}\n");

            CountDownLatch factoryLatch = new CountDownLatch(1);
            CountDownLatch factionsLatch = new CountDownLatch(2);

            world.setLatches(factoryLatch, factionsLatch);
            wednesday.setLatches(factoryLatch, factionsLatch);
            factory.setLatches(factoryLatch, factionsLatch);

            Thread tFactory = new Thread(factory::runDay, "Factory");
            Thread tWorld = new Thread(world, "World");
            Thread tWednesday = new Thread(wednesday, "Wednesday");

            tFactory.start();
            tWorld.start();
            tWednesday.start();

            try {
                tFactory.join();
                tWorld.join();
                tWednesday.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        int worldRobots = world.getRobotsBuilt();
        int wednesdayRobots = wednesday.getRobotsBuilt();

        System.out.println(STR."\nFINAL RESULTS AFTER \{DAYS} DAYS");
        System.out.println(STR."World built \{worldRobots} robots, Wednesday built \{wednesdayRobots} robots");
    }
}