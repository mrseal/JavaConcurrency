package locking;

public class CounterPessimistic implements Counter {

    int counter = 0;

    @Override
    public synchronized void increment() {
        final int temp = counter;
        try {
            Thread.sleep(1);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        counter = temp + 1;
    }

    @Override
    public synchronized int get() {
        return counter;
    }

}
