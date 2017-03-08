package executor.future.callable.runnable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class BigArraySumRunnable {

    private static final int THREAD_POOL_SIZE = 10;
    private static final int THREAD_NUM = 10;

    public static void main(final String[] args) {
        final int[] array = prepareArray(500000000);

        long start = System.currentTimeMillis();
        long sum = 0;
        for (final int value : array) {
            sum += value;
        }
        final long singleTTimeCost = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        final long mtSum = sum(array);
        final long tPoolTimeCost = System.currentTimeMillis() - start;

        System.out.println("Total: " + mtSum + " (Should be " + sum + ")");
        System.out.println("Single Thread: " + singleTTimeCost + "ms");
        System.out.println("Thread Pool: " + tPoolTimeCost + "ms");
    }

    private static int[] prepareArray(final int length) {
        final int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = (int) (Math.random() * 20);
        }
        return array;
    }

    public static Long sum(final int[] array) {
        final AtomicLong sum = new AtomicLong();
        final CountDownLatch latch = new CountDownLatch(THREAD_NUM - 1);

        final int arraySize = array.length;
        final int blockSize = Math.round((float) arraySize / (float) THREAD_NUM);

        final ExecutorService exec = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        int i = 0;
        while (i < arraySize - blockSize) {
            final int index = i;
            exec.submit(new Runnable() {

                @Override
                public void run() {
                    long subsum = 0;
                    final int end = index + blockSize;
                    for (int j = index; j < end; j++) {
                        subsum += array[j];
                    }
                    sum.addAndGet(subsum);
                    latch.countDown();
                }
            });
            i += blockSize;
        }
        exec.shutdown();

        int subsum = 0;
        while (i < arraySize) {
            subsum += array[i];
            i++;
        }
        try {
            latch.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return sum.addAndGet(subsum);
    }
}