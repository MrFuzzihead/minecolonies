package net.minecraft;

/**
 * [1.7.10] Compatibility stub for 1.21 net.minecraft.Util.
 */
public class Util
{
    public static <T> T make(T object, java.util.function.Consumer<T> initializer)
    {
        initializer.accept(object);
        return object;
    }

    public static long getMillis() { return System.currentTimeMillis(); }
    public static long getNanos() { return System.nanoTime(); }
}

