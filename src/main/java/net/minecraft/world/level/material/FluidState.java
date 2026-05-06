package net.minecraft.world.level.material;

/** [1.7.10 bridge] FluidState - 1.7.10 uses block-level fluid checks */
public class FluidState
{
    public static final FluidState EMPTY = new FluidState(true);

    private final boolean empty;

    public FluidState(boolean empty) { this.empty = empty; }

    public boolean isEmpty() { return empty; }

    public boolean isSource() { return false; }

    public boolean is(Object fluid) { return false; }
}

