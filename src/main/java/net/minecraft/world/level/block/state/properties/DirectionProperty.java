package net.minecraft.world.level.block.state.properties;
import net.minecraft.core.Direction;
/** 1.7.10 stub for DirectionProperty. */
public class DirectionProperty extends Property<Direction> {
    private DirectionProperty(String name) { super(name); }
    public static DirectionProperty create(String name) { return new DirectionProperty(name); }
    public static DirectionProperty create(String name, Direction... dirs) { return new DirectionProperty(name); }
}
