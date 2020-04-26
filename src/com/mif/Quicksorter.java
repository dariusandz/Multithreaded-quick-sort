package com.mif;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mif.Main.forkJoinPool;
import static com.mif.Logger.log;

public class Quicksorter<T extends Comparable<T>> implements Runnable {

    private T[] data;

    private int left;
    private int right;

    private AtomicInteger activeThreadCount;

    public Quicksorter(T[] data, int left, int right, AtomicInteger threadCount) {
        this.data = data;
        this.left = left;
        this.right = right;
        this.activeThreadCount = threadCount;
    }

    @Override
    public void run() {
        sort(left, right);
        synchronized (activeThreadCount) {
            if (activeThreadCount.decrementAndGet() == 0)
                activeThreadCount.notify();
        }
    }

    private void sort(int nLeft, int nRight) {
        if (nLeft < nRight) {
            int partitionIndex = partition(nLeft, nRight);
            if (activeThreadCount.get() >= forkJoinPool.getParallelism()) {
                sort(nLeft, partitionIndex - 1);
                sort(partitionIndex + 1, nRight);
            } else {
                activeThreadCount.getAndAdd(2);
                forkJoinPool.execute(new Quicksorter<>(data, nLeft, partitionIndex - 1, activeThreadCount));
                forkJoinPool.execute(new Quicksorter<>(data, partitionIndex + 1, nRight, activeThreadCount));
            }
        }
    }

    private int partition(int nLeft, int nRight) {
        T pivot = data[nRight];
        int tempIndex = nLeft;
        for (int i = nLeft; i < nRight; i++) {
            if (data[i].compareTo(pivot) < 0) {
                log("Comparing " + data[i] + " to " + pivot);
                swap(i, tempIndex++);
            }
        }
        swap(tempIndex, nRight);
        return tempIndex;
    }

    private void swap(int i, int j) {
        log("Swapping " + data[i] + " with " + data[j]);
        T temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    private void logWithBounds(String message) {
        System.out.println(message + Arrays.toString(Arrays.copyOfRange(data, left, right)));
    }
}
