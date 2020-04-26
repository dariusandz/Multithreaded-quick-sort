import java.util.Arrays;

public class Logger {

    public static boolean silent = false;

    public static void setSilent(boolean silentMode) {
        silent = silentMode;
    }

    public static void log(String message) {
        if (!silent) {
            System.err.println(message);
        }
    }

    public static <T> void log(T[] arr) {
        if (!silent) {
            System.out.println("Array data: " + Arrays.toString(arr));
        }
    }

    public static <T> void log(String message, T[] arr) {
        if (!silent) {
            System.out.println(message + " \nArray data: " + Arrays.toString(arr));
        }
    }
}
