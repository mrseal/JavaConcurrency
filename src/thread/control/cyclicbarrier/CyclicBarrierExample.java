package thread.control.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {

    public static void main(final String[] args) {
        final CyclicBarrier barrier = new CyclicBarrier(3, new Runnable() {
            int i = 1;

            @Override
            public void run() {
                System.out.println("Group " + i++);
            }
        });
        for (int i = 0; i < 6; i++) {
            final Thread t = new Thread(new Worker(barrier));
            t.start();
        }
    }

}

class Worker implements Runnable {

    private final CyclicBarrier barrier;

    public Worker(final CyclicBarrier b) {
        barrier = b;
    }

    @Override
    public void run() {
        final String t = Thread.currentThread().getName();
        final int sleep = (int) (Math.random() * 10000);
        System.out.println(t + " is sleeping for " + sleep / 1000 + "s");
        try {
            Thread.sleep(sleep);
            System.out.println(t + " is waked up. Waiting for the others...");
            barrier.await();
            System.out.println(t + " GO ");
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final BrokenBarrierException e) {
            e.printStackTrace();
        }

    }

}
