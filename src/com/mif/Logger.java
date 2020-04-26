package com.mif;

import java.util.Arrays;

public class Logger {

    public static void log(String message) {
//        System.out.println(message);
    }

    public static <T> void log(T[] arr) {
        System.out.println("Array data: " + Arrays.toString(arr));
    }
}
