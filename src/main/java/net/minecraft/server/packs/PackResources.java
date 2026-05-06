package net.minecraft.server.packs;
/** [1.7.10 bridge] PackResources */
public interface PackResources extends AutoCloseable {
    String packId();
    boolean isBuiltin();
    void close();
}
