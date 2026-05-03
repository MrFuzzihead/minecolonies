package net.minecraft.world.level.block.state.properties;
import net.minecraft.core.Direction;
/** 1.7.10 stub for BlockStateProperties. */
public class BlockStateProperties {
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    public static final DirectionProperty FACING_HOPPER = DirectionProperty.create("facing");
    public static final BooleanProperty CONNECTED_NORTH = BooleanProperty.create("north");
    public static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty CONNECTED_EAST = BooleanProperty.create("east");
    public static final BooleanProperty CONNECTED_WEST = BooleanProperty.create("west");
}
