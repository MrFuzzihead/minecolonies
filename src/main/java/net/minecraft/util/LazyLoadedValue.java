package net.minecraft.util;
import java.util.function.Supplier;
/** [1.7.10] Stub for 1.21 LazyLoadedValue */
public class LazyLoadedValue<T> {
    private final Supplier<T> supplier;
    private T value;
    private boolean resolved;
    public LazyLoadedValue(Supplier<T> supplier) { this.supplier = supplier; }
    public T get() {
        if (!resolved) { value = supplier.get(); resolved = true; }
        return value;
    }
}