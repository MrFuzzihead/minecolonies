package net.minecraft.world.item;

/**
 * [1.7.10] Compatibility shim for 1.21 InteractionResult.
 * In 1.7.10, item-use methods return boolean.
 */
public enum InteractionResult
{
    SUCCESS,
    CONSUME,
    PASS,
    FAIL;

    public boolean consumesAction()
    {
        return this == SUCCESS || this == CONSUME;
    }
}

