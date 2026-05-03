package net.minecraftforge.common.capabilities;

import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * [1.7.10] Shim for 1.21 ICapabilityProvider.
 * In 1.7.10, there's no capability system. This interface is used as a marker
 * for objects that can provide an IItemHandler.
 */
public interface ICapabilityProvider extends IItemHandler
{
    /**
     * [1.7.10] Stub for getCapability. Since ICapabilityProvider IS an IItemHandler,
     * we just return ourselves wrapped in Optional, ignoring side.
     */
    default Optional<IItemHandler> getCapability(final Object capabilityKey, @Nullable final Direction side)
    {
        if (ForgeCapabilities.ITEM_HANDLER.equals(capabilityKey))
        {
            return Optional.of(this);
        }
        return Optional.empty();
    }
}
