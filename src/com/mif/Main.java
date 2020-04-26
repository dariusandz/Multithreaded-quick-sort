package com.mif;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Main {

    public static ForkJoinPool forkJoinPool;

    public static void main(String[] args) {
        if (args.length > 3) {
            System.out.println("arg[0] - number of threads (1...n) \n arg[1] - data sample size (1...MAX_INT) \n arg[2] - silent mode (false, true)");
        }

        int nThreads = Integer.parseInt(args[0]);
        int bound = Integer.parseInt(args[1]);
        boolean silent = args[2] != null ? Boolean.parseBoolean(args[2]) : true;
        Logger.setSilent(silent);

        forkJoinPool = new ForkJoinPool(nThreads);

        final AtomicInteger activeThreadCount = new AtomicInteger(1);
        Integer[] arr = generateData(r -> r.nextInt(bound), Integer.class, bound);
        forkJoinPool.execute(new Quicksorter<>(arr, 0, arr.length - 1, activeThreadCount, 1));

        Long startTime = System.nanoTime();
        try {
            synchronized (activeThreadCount) {
                activeThreadCount.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long endTitme = System.nanoTime();

        if (silent) {
            System.out.println("Thread count: " + nThreads);
            System.out.println("Sample size: 0..." + bound);
            System.out.println("Data variation: 0..." + bound);
            System.out.println("Execution time: " + (endTitme - startTime) / 1000000000. + " seconds");
        }

        Boolean isSorted = isSorted(arr);
        System.out.println("Is array sorted? : " + isSorted);
    }

    /** Generates an array of size n
        @param generator function that generates a number
        @param type array data type
        @param n size of data
        @return generated array
     **/
    private static <T> T[] generateData(Function<Random, T> generator, Class<T> type, int n) {
        T[] arr = (T[]) Array.newInstance(type, n);
        Random r = new Random();
        for (int i = 0; i < n; i++)
            arr[i] = generator.apply(r);
        return arr;
    }

    private static <T extends Comparable<T>> boolean isSorted(T[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i].compareTo(arr[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }
}
