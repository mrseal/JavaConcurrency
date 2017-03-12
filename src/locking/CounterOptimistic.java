package locking;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterOptimistic implements Counter {

    AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void increment() {
        boolean updated = false;
        while (!updated) {
            final int temp = counter.get();
            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            updated = counter.compareAndSet(temp, temp + 1);
        }
    }

    @Override
    public int get() {
        return counter.get();
    }

}
