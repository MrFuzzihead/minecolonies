package com.minecolonies.api.util;

import com.ldtteam.structurize.api.util.Log;
import com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.management.Manager;
import com.ldtteam.structurize.operations.PlaceStructureOperation;
import com.ldtteam.structurize.placement.StructurePlacer;
import com.ldtteam.structurize.placement.structure.CreativeStructureHandler;
import com.ldtteam.structurize.placement.structure.IStructureHandler;
import com.ldtteam.structurize.storage.StructurePacks;
import com.ldtteam.structurize.util.PlacementSettings;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.core.entity.ai.workers.util.ConstructionTapeHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] tags removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.block.Mirror;
import net.minecraft.world.block.Rotation;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Future;

import static com.ldtteam.structurize.blockentities.interfaces.IBlueprintDataProviderBE.TAG_BLUEPRINTDATA;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_NAME;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_PACK;

/**
 * Minecolonies specific creative structure handler. Main difference related to registering blocks to colonies.
 */
public final class CreativeBuildingStructureHandler extends CreativeStructureHandler
{
    /**
     * The building associated with this placement.
     */
    private IBuilding building;

    /**
     * The minecolonies specific creative structure placer.
     *
     * @param world          the world.
     * @param pos            the pos it is placed at.
     * @param blueprint      the blueprint of the structure.
     * @param settings       the placement settings.
     * @param fancyPlacement if fancy or complete.
     */
    public CreativeBuildingStructureHandler(final World world, final int[] pos, final Blueprint blueprint, final PlacementSettings settings, final boolean fancyPlacement)
    {
        super(world, pos, blueprint, settings, fancyPlacement);
        setupBuilding();
    }

    /**
     * The minecolonies specific creative structure placer.
     *
     * @param world          the world.
     * @param pos            the pos it is placed at.
     * @param blueprint      the blueprint of the structure.
     * @param settings       the placement settings.
     * @param fancyPlacement if fancy or complete.
     */
    public CreativeBuildingStructureHandler(final World world, final int[] pos, final Future<Blueprint> blueprint, final PlacementSettings settings, final boolean fancyPlacement)
    {
        super(world, pos, blueprint, settings, fancyPlacement);
        setupBuilding();
    }

    /**
     * Setup the building to register things to.
     */
    private void setupBuilding()
    {
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(getWorld(), getCenterPos());
        if (colony != null)
        {
            this.building = colony.getServerBuildingManager().getBuilding(getCenterPos());
        }
    }

    @Override
    public void triggerSuccess(final int[] pos, final List<ItemStack> list, final boolean placement)
    {
        super.triggerSuccess(pos, list, placement);
        final int[] worldPos = getProgressPosInWorld(pos);

        final Blueprint blueprint = getBluePrint();
        final NBTTagCompound teData = blueprint.getTileEntityData(worldPos, pos);
        if (teData != null && teData.contains(TAG_BLUEPRINTDATA))
        {
            final BlockEntity te = getWorld().getBlockEntity(worldPos);
            if (te instanceof IBlueprintDataProviderBE blueprintDataProviderBE)
            {
                final NBTTagCompound tagData = teData.getCompound(TAG_BLUEPRINTDATA);
                final String schematicPath = tagData.getString(TAG_NAME);
                final String location = StructurePacks.getStructurePack(blueprint.getPackName()).getSubPath(Utils.resolvePath(blueprint.getFilePath(), schematicPath));

                tagData.putString(TAG_NAME, location);
                tagData.putString(TAG_PACK, blueprint.getPackName());

                if (te instanceof AbstractTileEntityColonyBuilding colonyBuilding && colonyBuilding.getBuilding() != null)
                {
                    colonyBuilding.getBuilding().setDeconstructed();
                }

                blueprintDataProviderBE.readSchematicDataFromNBT(teData);
                ((ServerLevel) getWorld()).getChunkSource().blockChanged(worldPos);
                te.setChanged();
            }
        }

        if (building != null)
        {
            building.registerBlockPosition(blueprint.getBlockState(pos), worldPos, this.getWorld());
        }
    }

    @Override
    public boolean isStackFree(@Nullable final ItemStack itemStack)
    {
        return itemStack == null
                 || itemStack.isEmpty()
                 || itemStack.is(ItemTags.LEAVES)
                 || itemStack.getItem() == new ItemStack(ModBlocks.blockDecorationPlaceholder, 1).getItem();
    }

    /**
     * Load a structure into this world and place it in the right position and rotation.
     *
     * @param worldObj       the world to load it in.
     * @param future         the structures blueprint future.
     * @param pos            coordinates.
     * @param rotation       the rotation.
     * @param mirror         the mirror used.
     * @param fancyPlacement if fancy or complete.
     * @param player         the placing player.
     * @return the placed blueprint.
     */
    public static Blueprint loadAndPlaceStructureWithRotation(
      final World worldObj, @NotNull final Future<Blueprint> future,
      @NotNull final int[] pos, final Rotation rotation,
      @NotNull final Mirror mirror,
      final boolean fancyPlacement,
      @Nullable final EntityPlayerMP player)
    {
        try
        {
            @NotNull final IStructureHandler structure = new CreativeBuildingStructureHandler(worldObj, pos, future, new PlacementSettings(mirror, rotation), fancyPlacement);
            if (structure.hasBluePrint())
            {
                @NotNull final StructurePlacer instantPlacer = new StructurePlacer(structure);
                Manager.addToQueue(new PlaceStructureOperation(instantPlacer, player));
            }
            return structure.getBluePrint();
        }
        catch (final IllegalStateException e)
        {
            Log.getLogger().warn("Could not load structure!", e);
        }
        return null;
    }

    @Override
    public void onCompletion()
    {
        super.onCompletion();
        if (building != null)
        {
            ConstructionTapeHelper.removeConstructionTape(building.getCorners(), getWorld());
        }
    }
}





