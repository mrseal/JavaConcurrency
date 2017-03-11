package parallel.computing.executor.callable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import parallel.computing.Sum;

public class SumExecutorCallable implements Sum {

    private static final int THREAD_POOL_SIZE = 10;
    private static final int THREAD_NUM = 10;

    private final int[] array;

    public SumExecutorCallable(final int[] array) {
        this.array = array;
    }

    @Override
    public Long sum() {
        final List<Future<Long>> results = Collections.synchronizedList(new ArrayList<>());
        Long sum = 0l;
        final ExecutorService exec = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        final int arraySize = array.length;
        final int blockSize = Math.round((float) array.length / THREAD_NUM);

        int i = 0;
        while (i < arraySize - blockSize) {
            final Future<Long> result = exec.submit(new SumCallable(i, i + blockSize - 1));
            results.add(result);
            i += blockSize;
        }
        results.add(exec.submit(new SumCallable(i, array.length - 1)));

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

    @Override
    public String getImpl() {
        return "ExecutorCallable";
    }

    class SumCallable implements Callable<Long> {
        int start;
        int end;

        public SumCallable(final int start, final int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Long call() throws Exception {
            long sum = 0l;
            for (int i = start; i <= end; i++) {
                sum += array[i];
            }
            // System.out.println(Thread.currentThread().getName() +
            // " calculated "
            // + start + " - " + end + ": " + sum);
            return sum;
        }

    }

}
