package parallel.computing.fork.join.pool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import parallel.computing.Sum;

public class SumForkJoinPool implements Sum {

    private final static int THREAD_NUM = 10;
    private final static int THRESHOLD = 5000;
    ForkJoinPool pool = new ForkJoinPool(THREAD_NUM);
    private final int[] array;

    public SumForkJoinPool(final int[] array) {
        this.array = array;
    }

    @Override
    public Long sum() {
        return pool.invoke(new SumTask(0, array.length));
    }

    @Override
    public String getImpl() {
        return "ForkJoinPool";
    }

    class SumTask extends RecursiveTask<Long> {

        private static final long serialVersionUID = -711143499207916531L;

        int left;
        int right;

        public SumTask(final int left, final int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        protected Long compute() {
            long sum = 0l;
            if (right - left <= THRESHOLD) {
                // System.out.println(Thread.currentThread().getName() + ": " +
                // left + " - " + right);
                for (int i = left; i < right; i++) {
                    sum += array[i];
                }
            } else {
                final int mid = (left + right) / 2;
                final RecursiveTask<Long> leftTask = new SumTask(left, mid);
                final RecursiveTask<Long> rightTask = new SumTask(mid, right);
                leftTask.fork();
                rightTask.fork();
                return leftTask.join() + rightTask.join();
            }
            return sum;
        }
    }
}
