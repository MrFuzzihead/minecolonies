package net.minecraftforge.common.util;

import java.util.function.Supplier;

/** [1.7.10 stub] Lazy<T>. */
public class Lazy<T> implements Supplier<T>
{
    private Supplier<T> supplier;
    private T value;
    private boolean initialized = false;

    private Lazy(Supplier<T> supplier) { this.supplier = supplier; }

    public static <T> Lazy<T> of(Supplier<T> supplier) { return new Lazy<>(supplier); }

    @Override
    public T get()
    {
        if (!initialized) { value = supplier.get(); initialized = true; }
        return value;
    }
}

