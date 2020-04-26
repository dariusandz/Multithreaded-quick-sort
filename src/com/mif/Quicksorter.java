package com.mif;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mif.Main.forkJoinPool;

public class Quicksorter<T extends Comparable<T>> implements Runnable {

    private int nthThread;

    private T[] data;

    private int left;
    private int right;

    private AtomicInteger activeThreadCount;

    public Quicksorter(T[] data, int left, int right, AtomicInteger threadCount, int nthThread) {
        this.data = data;
        this.left = left;
        this.right = right;
        this.activeThreadCount = threadCount;
        this.nthThread = nthThread;
    }

    @Override
    public void run() {
        if (left < right) {
            Logger.log(nthThread + " thread starting work to sort: ", Arrays.copyOfRange(data, left, right + 1));
        }

        try {
            sort(left, right);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (activeThreadCount) {
            if (activeThreadCount.decrementAndGet() == 0)
                activeThreadCount.notify();
        }
    }

    private void sort(int nLeft, int nRight) throws InterruptedException {
        if (nLeft < nRight) {
            if (!Logger.silent) {
                Thread.sleep(2500);
            }

            int partitionIndex = partition(nLeft, nRight);
            Logger.log(nthThread + " thread says: " + activeThreadCount.get() + "/" + forkJoinPool.getParallelism() + " active threads. Should I create more? " + !shouldNotPartitionIntoThreads());
            if (shouldNotPartitionIntoThreads()) {
                sort(nLeft, partitionIndex - 1);
                sort(partitionIndex + 1, nRight);
            } else {
                Logger.log(nthThread + " thread partitioning into two new threads");
                activeThreadCount.getAndAdd(2);
                forkJoinPool.execute(new Quicksorter<>(data, nLeft, partitionIndex - 1, activeThreadCount, activeThreadCount.get() - 2 + 1));
                forkJoinPool.execute(new Quicksorter<>(data, partitionIndex + 1, nRight, activeThreadCount, activeThreadCount.get() - 2 + 2));
            }
        }
    }

    private boolean shouldNotPartitionIntoThreads() {
        synchronized (activeThreadCount) {
            return activeThreadCount.get() + 2 > forkJoinPool.getParallelism();
        }
    }

    private int partition(int nLeft, int nRight) {
        T pivot = data[nRight];
//        Logger.log(nthThread + " thread's pivot: " + data[right]);
        int tempIndex = nLeft;
        for (int i = nLeft; i < nRight; i++) {
            if (data[i].compareTo(pivot) <= 0) {
                swap(i, tempIndex++);
            }
        }
        swap(tempIndex, nRight);
        return tempIndex;
    }

    private void swap(int i, int j) {
        T temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
}
