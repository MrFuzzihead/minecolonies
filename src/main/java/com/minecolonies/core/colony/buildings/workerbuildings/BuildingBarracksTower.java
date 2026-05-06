package com.minecolonies.core.colony.buildings.workerbuildings;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.minecolonies.api.advancements.AdvancementTriggers;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.workorders.WorkOrderType;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.util.AdvancementUtils;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] NbtUtils removed
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_UPGRADE_BARRACKS;

/**
 * Building class for the Barracks Tower.
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class BuildingBarracksTower extends AbstractBuildingGuards
{
    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String TAG_POS = "pos";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    /**
     * Our constants. The Schematic names, Defence bonus, and Offence bonus.
     */
    private static final String SCHEMATIC_NAME = "barrackstower";

    /**
     * Position of the barracks for this tower.
     */
    private int[] barracks = null;

    /**
     * The abstract constructor of the building.
     *
     * @param c the colony
     * @param l the position
     */
    public BuildingBarracksTower(@NotNull final IColony c, final int[] l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return SCHEMATIC_NAME;
    }

    @SuppressWarnings("squid:S109")
    @Override
    public int getMaxBuildingLevel()
    {
        return 5;
    }

    @Override
    public void requestUpgrade(final Player player, final int[] builder)
    {
        final int buildingLevel = getBuildingLevel();
        final IBuilding building = getColony().getServerBuildingManager().getBuilding(barracks);

        if (building != null && buildingLevel < getMaxBuildingLevel() && buildingLevel < building.getBuildingLevel())
        {
            if (buildingLevel == 0)
            {
                requestWorkOrder(WorkOrderType.BUILD, builder);
            }
            else
            {
                requestWorkOrder(WorkOrderType.UPGRADE, builder);
            }
        }
        else
        {
            MessageUtils.format(WARNING_UPGRADE_BARRACKS).sendTo(player);
        }
    }

    @Override
    public boolean canDeconstruct()
    {
        return false;
    }

    @Override
    public int getClaimRadius(final int newLevel)
    {
        return 0;
    }

    @Override
    public void onUpgradeComplete(@Nullable final Blueprint blueprint, final int newLevel)
    {
        super.onUpgradeComplete(blueprint, newLevel);
        final IBuilding barrack = colony.getServerBuildingManager().getBuilding(barracks);
        if (barrack == null)
        {
            return;
        }

        if (newLevel == barrack.getMaxBuildingLevel())
        {
            boolean allUpgraded = true;
            for (int[] tower : ((BuildingBarracks) barrack).getTowers())
            {
                if (colony.getServerBuildingManager().getBuilding(tower).getBuildingLevel() != barrack.getMaxBuildingLevel())
                {
                    allUpgraded = false;
                }
            }

            if (allUpgraded)
            {
                AdvancementUtils.TriggerAdvancementPlayersForColony(colony, AdvancementTriggers.ALL_TOWERS::trigger);
            }
        }
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        barracks = NbtUtils.readBlockPos(compound.getCompoundTag(TAG_POS));
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        if (barracks != null)
        {
            compound.setTag(TAG_POS, NbtUtils.writeBlockPos(barracks));
        }

        return compound;
    }

    /**
     * Adds the position of the main barracks.
     *
     * @param pos the int[].
     */
    public void addBarracks(final int[] pos)
    {
        barracks = pos;
    }
}




