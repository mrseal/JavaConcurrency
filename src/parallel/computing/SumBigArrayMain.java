package parallel.computing;

import parallel.computing.executor.callable.SumExecutorCallable;
import parallel.computing.executor.runnable.SumExecutorRunnable;
import parallel.computing.fork.join.pool.SumForkJoinPool;

public class SumBigArrayMain {

    public static void main(final String[] args) {

        System.out.println("Preparing array ...");
        final int[] array = prepareArray(500000000);

        System.out.println("Calculating ...");
        final Sum simpleSum = new SimpleSum(array);
        final Sum forkJoinSum = new SumForkJoinPool(array);
        final Sum runnableSum = new SumExecutorRunnable(array);
        final Sum callableSum = new SumExecutorCallable(array);
        run(simpleSum);
        run(forkJoinSum);
        run(runnableSum);
        run(callableSum);
    }

    private static int[] prepareArray(final int length) {
        final int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = (int) (Math.random() * 20);
        }
        return array;
    }

    private static void run(final Sum sum) {
        final long start = System.currentTimeMillis();
        final Long result = sum.sum();
        final long cost = System.currentTimeMillis() - start;
        System.out.println(sum.getImpl() + ": " + result + " [time cost: " + cost + "ms]");
    }

}

class SimpleSum implements Sum {

    private final int[] array;

    public SimpleSum(final int[] array) {
        this.array = array;
    }

    @Override
    public Long sum() {
        long sum = 0;
        for (final int value : array) {
            sum += value;
        }
        return sum;
    }

    @Override
    public String getImpl() {
        return "SimpleSum";
    }

}
