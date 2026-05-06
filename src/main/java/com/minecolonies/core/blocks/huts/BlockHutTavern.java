package com.minecolonies.core.blocks.huts;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_DUPLICATE_TAVERN;

/**
 * HutBlock for the Tavern.
 * [1.7.10] Ported: canPlaceAt uses int x,y,z; Player/World replaced; colony.getWorld().isClientSide → isRemote.
 */
public class BlockHutTavern extends AbstractBlockHut<BlockHutTavern>
{
    /**
     * Block name
     */
    public static final String BLOCKHUT_TAVERN = "blockhuttavern";

    @NotNull
    @Override
    public String getHutName()
    {
        return BLOCKHUT_TAVERN;
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.tavern.get();
    }

    /**
     * Check if the block can be placed at the given position by the player.
     *
     * @param pos the position to check.
     * @param player the player trying to place the block.
     * @return true if the block can be placed.
     */
    @Override
    public boolean canPlaceAt(final int[] pos, final EntityPlayer player)
    {
        final IColony colony = IColonyManager.getInstance().getIColony(player.worldObj, pos[0], pos[1], pos[2]);
        if (colony == null)
        {
            return true;
        }

        for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values())
        {
            if (!colony.getWorld().isRemote && building.hasModule(BuildingModules.TAVERN_VISITOR))
            {
                MessageUtils.format(WARNING_DUPLICATE_TAVERN, building.getPosition()[0] + "," + building.getPosition()[1] + "," + building.getPosition()[2]).sendTo(player);
                return false;
            }
        }
        return true;
    }
}
