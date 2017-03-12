package locking;

public class Main {

    public static void main(final String[] args) throws Exception {

        final Counter counterUnsafe = new CounterUnsafe();
        final Counter counterOptimistic = new CounterOptimistic();
        final Counter counterPessimistic = new CounterPessimistic();

        for (int i = 0; i < 360; i++) {
            final Thread t1 = new Thread(new CounterRunnable(counterUnsafe));
            final Thread t2 = new Thread(new CounterRunnable(counterOptimistic));
            final Thread t3 = new Thread(new CounterRunnable(counterPessimistic));
            t1.start();
            t2.start();
            t3.start();
        }

        Thread.sleep(2000);
        System.out.println("Counter Unsafe: " + counterUnsafe.get());
        System.out.println("Counter Optimistic: " + counterOptimistic.get());
        System.out.println("Counter Pessimistic: " + counterPessimistic.get());
    }

}

class CounterRunnable implements Runnable {

    Counter counter;

    public CounterRunnable(final Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        counter.increment();
    }

}
