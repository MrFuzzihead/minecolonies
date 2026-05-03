package net.minecraft.world.level.block.state.properties;
/** 1.7.10 stub for Property<T>. */
public abstract class Property<T extends Comparable<T>> {
    private final String name;
    protected Property(String name) { this.name = name; }
    public String getName() { return name; }
}
