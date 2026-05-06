package net.minecraft.data;

/** [1.7.10 bridge] DataGenerator - no 1.7.10 equivalent (data generation) */
public class DataGenerator
{
    public PackOutput getPackOutput() { return new PackOutput(); }
    public void addProvider(final boolean condition, final DataProvider provider) {}
}

