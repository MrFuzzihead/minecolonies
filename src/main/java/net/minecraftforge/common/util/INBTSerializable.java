package net.minecraftforge.common.util;
/** [1.7.10 shim] INBTSerializable - replaces Forge 1.8+ generic NBT serialization. */
public interface INBTSerializable<T> {
    T serializeNBT();
    void deserializeNBT(T nbt);
}
