package net.minecraft.server.packs.resources;
import java.io.IOException;
/** [1.7.10 bridge] IoSupplier */
@FunctionalInterface
public interface IoSupplier<T> {
    T get() throws IOException;
}
