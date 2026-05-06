package com.minecolonies.core.placementhandlers;
import com.ldtteam.structurize.placement.IPlacementContext;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.minecolonies.api.util.Tuple;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
/**
 * [1.7.10] Lectern does not exist in 1.7.10. Handler is a no-op stub.
 */
public class LecternPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState)
    {
        return false;
    }
    @Override
    public ActionProcessingResult handle(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState,
                                         @Nullable final NBTTagCompound tileEntityData,
                                         @NotNull final IPlacementContext context)
    {
        return ActionProcessingResult.PASS;
    }
    @Override
    public List<ItemStack> getRequiredItems(@NotNull final World world, @NotNull final int[] pos, @NotNull final BlockState blockState,
                                            @Nullable final NBTTagCompound tileEntityData,
                                            @NotNull final IPlacementContext context)
    {
        return List.of();
    }
}