package parallel.computing.executor.runnable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import parallel.computing.Sum;

public class SumExecutorRunnable implements Sum {

    private static final int THREAD_POOL_SIZE = 10;
    private static final int THREAD_NUM = 10;

    private final int[] array;

    public SumExecutorRunnable(final int[] array) {
        this.array = array;
    }

    @Override
    public Long sum() {
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

    @Override
    public String getImpl() {
        return "ExecutorRunnable";
    }
}