package net.minecraft;
import java.util.concurrent.ExecutorService;
/** [1.7.10 bridge] Util */
public class Util {
    public static ExecutorService backgroundExecutor() { return java.util.concurrent.ForkJoinPool.commonPool(); }
    public static <T> T make(java.util.function.Supplier<T> supplier) { return supplier.get(); }
}