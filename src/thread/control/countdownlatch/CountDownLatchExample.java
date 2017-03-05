package thread.control.countdownlatch;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {

    public static void main(final String[] args) {
        final CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            final Thread t = new Thread(new Worker(latch));
            t.start();
        }
    }
}

class Worker implements Runnable {

    private final CountDownLatch latch;

    public Worker(final CountDownLatch l) {
        latch = l;
    }

    @Override
    public void run() {
        final String t = Thread.currentThread().getName();
        final int sleep = (int) (Math.random() * 10000);
        System.out.println(t + " is sleeping for " + sleep / 1000 + "s");
        try {
            Thread.sleep(sleep);
            System.out.println(t + " is waked up. Waiting for the others...");
            latch.countDown();
            latch.await();
            System.out.println(t + " GO ");
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

}
