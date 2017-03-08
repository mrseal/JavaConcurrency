package executor.future.callable.runnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BigArraySumCallable {

    private static final int THREAD_POOL_SIZE = 100;
    private static final int THREAD_NUM = 100;

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
        final List<Future<Long>> results = Collections.synchronizedList(new ArrayList<>());
        Long sum = 0l;

        final int arraySize = array.length;
        final int blockSize = Math.round((float) array.length / (float) THREAD_NUM);

        final ExecutorService exec = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        int i = 0;
        while (i < arraySize - blockSize) {
            final Future<Long> result = exec.submit(new SumCallable(array, i, i + blockSize - 1));
            results.add(result);
            i += blockSize;
        }
        results.add(exec.submit(new SumCallable(array, i, array.length - 1)));

        for (final Future<Long> result : results) {
            try {
                sum += result.get();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } catch (final ExecutionException e) {
                e.printStackTrace();
            }
        }
        exec.shutdown();
        return sum;
    }

}

class SumCallable implements Callable<Long> {
    int[] array;
    int start;
    int end;

    public SumCallable(final int[] array, final int start, final int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    public Long call() throws Exception {
        Long sum = 0l;
        for (int i = start; i <= end; i++) {
            sum += array[i];
        }
        // System.out.println(Thread.currentThread().getName() + " calculated "
        // + start + " - " + end + ": " + sum);
        return sum;
    }

}
