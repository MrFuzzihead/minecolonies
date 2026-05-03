package com.minecolonies.core.tileentities;

import com.ldtteam.structurize.storage.StructurePackMeta;
import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.IBuildingContainer;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;
import com.minecolonies.api.tileentities.ITickable;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.BuildingConstants.DEACTIVATED;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BUILDING_TYPE;

/**
 * Class which handles the tileEntity of our colonyBuildings.
 */
public class TileEntityColonyBuilding extends AbstractTileEntityColonyBuilding implements ITickable
{
    /**
     * NBTTag to store the colony id.
     */
    private static final String TAG_COLONY = "colony";
    private static final String TAG_MIRROR = "mirror";
    private static final String TAG_PACK   = "pack";
    private static final String TAG_PATH   = "path";

    /**
     * The colony id.
     */
    private int colonyId = 0;

    /**
     * The colony.
     */
    private IColony colony;

    /**
     * The building the tileEntity belongs to.
     */
    private IBuilding building;

    /**
     * Check if the building has a mirror.
     */
    private boolean mirror;

    /**
     * The style pack name of the building.
     */
    private String packMeta = "";

    /**
     * Path of the blueprint.
     */
    private String path = "";

    /**
     * The name of the building location.
     */
    public ResourceLocation registryName;

    /**
     * Default constructor.
     */
    public TileEntityColonyBuilding()
    {
        super();
    }

    /**
     * Returns the colony ID.
     *
     * @return ID of the colony.
     */
    @Override
    public int getColonyId()
    {
        return colonyId;
    }

    /**
     * Returns the colony of the tile entity.
     *
     * @return Colony of the tile entity.
     */
    @Override
    public IColony getColony()
    {
        if (colony == null)
        {
            updateColonyReferences();
        }
        return colony;
    }

    /**
     * Synchronises colony references from the tile entity.
     */
    private void updateColonyReferences()
    {
        if (colony == null && worldObj != null)
        {
            if (colonyId == 0)
            {
                colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldObj, xCoord, yCoord, zCoord);
            }
            else if (worldObj.isRemote)
            {
                colony = IColonyManager.getInstance().getColonyView(colonyId, worldObj.provider.dimensionId);
            }
            else
            {
                colony = IColonyManager.getInstance().getColonyByWorld(colonyId, worldObj);
            }
        }

        if (building == null && colony != null && !worldObj.isRemote)
        {
            building = colony.getServerBuildingManager().getBuilding(xCoord, yCoord, zCoord);
            if (building != null)
            {
                registryName = building.getBuildingType().getRegistryName();
                building.setTileEntity(this);
            }
        }
    }

    /**
     * Returns the position of the tile entity as [x, y, z].
     *
     * @return position.
     */
    @Override
    public int[] getPosition()
    {
        return new int[]{xCoord, yCoord, zCoord};
    }

    /**
     * Check for a certain item and return the position of the chest containing it as [x, y, z].
     *
     * @param itemStackSelectionPredicate the stack to search for.
     * @return the position or null.
     */
    @Override
    @Nullable
    public int[] getPositionOfChestWithItemStack(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        final Predicate<ItemStack> notEmptyPredicate = itemStackSelectionPredicate.and(ItemStackUtils.NOT_EMPTY_PREDICATE);
        @Nullable final IBuildingContainer theBuilding = getBuilding();

        if (theBuilding != null)
        {
            for (final int[] pos : theBuilding.getContainers())
            {
                if (WorldUtil.isBlockLoaded(worldObj, pos[0], pos[2]))
                {
                    final TileEntity entity = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
                    if (entity instanceof AbstractTileEntityRack)
                    {
                        if (((AbstractTileEntityRack) entity).hasItemStack(notEmptyPredicate))
                        {
                            return pos;
                        }
                    }
                    // TODO: no ICapabilityProvider in 1.7.10; skip non-rack containers
                }
            }
        }
        return null;
    }

    /**
     * Sets the colony of the tile entity.
     *
     * @param c Colony to set in references.
     */
    @Override
    public void setColony(final IColony c)
    {
        colony = c;
        colonyId = c.getID();
        markDirty();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if (building != null)
        {
            building.markDirty();
        }
    }

    /**
     * Returns the building associated with the tile entity.
     *
     * @return {@link IBuilding} associated with the tile entity.
     */
    @Override
    public IBuilding getBuilding()
    {
        if (building == null)
        {
            updateColonyReferences();
        }
        return building;
    }

    /**
     * Sets the building associated with the tile entity.
     *
     * @param b {@link IBuilding} to associate with the tile entity.
     */
    @Override
    public void setBuilding(final IBuilding b)
    {
        building = b;
    }

    /**
     * Returns the view of the building associated with the tile entity.
     *
     * @return {@link IBuildingView} the tile entity is associated with.
     */
    @Override
    public IBuildingView getBuildingView()
    {
        final IColonyView c = IColonyManager.getInstance().getColonyView(colonyId, worldObj.provider.dimensionId);
        return c == null ? null : c.getClientBuildingManager().getBuilding(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(@NotNull final NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_COLONY))
        {
            colonyId = compound.getInteger(TAG_COLONY);
        }
        mirror = compound.getBoolean(TAG_MIRROR);
        packMeta = compound.getString(TAG_PACK);
        path = compound.getString(TAG_PATH);

        if (compound.hasKey(TAG_BUILDING_TYPE))
        {
            registryName = new ResourceLocation(compound.getString(TAG_BUILDING_TYPE));
        }
        // buildingPos in 1.7.10 is just the TE's own position
        buildingPosX = xCoord;
        buildingPosY = yCoord;
        buildingPosZ = zCoord;
    }

    @Override
    public void writeToNBT(@NotNull final NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger(TAG_COLONY, colonyId);
        compound.setBoolean(TAG_MIRROR, mirror);
        compound.setString(TAG_PACK, packMeta == null ? "" : packMeta);
        compound.setString(TAG_PATH, path == null ? "" : path);
        if (registryName != null)
        {
            compound.setString(TAG_BUILDING_TYPE, registryName.toString());
        }
    }

    @Override
    public void tick()
    {
        if (!worldObj.isRemote && colonyId == 0)
        {
            final IColony tempColony = IColonyManager.getInstance().getColonyByPosFromWorld(worldObj, xCoord, yCoord, zCoord);
            if (tempColony != null)
            {
                colonyId = tempColony.getID();
            }
        }

        if (colonyId != 0 && colony == null)
        {
            updateColonyReferences();
        }

        // TODO: Sign text update logic (SignBlockEntity/SignText) has no 1.7.10 equivalent
        // TODO: pendingBlueprintFuture / processBlueprint — port if Structurize 1.7.10 has blueprint async loading
    }

    @Override
    public void updateEntity()
    {
        // In 1.7.10, TEs tick via updateEntity() — delegate to tick()
        tick();
    }

    /**
     * Checks if the player has permission to access the hut.
     *
     * @param player Player to check permission of.
     * @return True when player has access, or building doesn't exist, otherwise false.
     */
    @Override
    public boolean hasAccessPermission(final EntityPlayer player)
    {
        return building == null || building.getColony().getPermissions().hasPermission(player, Action.ACCESS_HUTS);
    }

    /**
     * Set if the entity is mirrored.
     *
     * @param mirror true if so.
     */
    @Override
    public void setMirror(final boolean mirror)
    {
        this.mirror = mirror;
    }

    /**
     * Check if building is mirrored.
     *
     * @return true if so.
     */
    @Override
    public boolean isMirrored()
    {
        return mirror;
    }

    /**
     * Getter for the style.
     *
     * @return the pack of it.
     */
    @Override
    public StructurePackMeta getStructurePack()
    {
        return StructurePacks.getStructurePack(this.packMeta);
    }

    /**
     * Set the style of the tileEntity.
     *
     * @param style the style to set.
     */
    @Override
    public void setStructurePack(final StructurePackMeta style)
    {
        this.packMeta = style.getName();
    }

    @Override
    public void setBlueprintPath(final String path)
    {
        this.path = path;
    }

    @Override
    public String getBlueprintPath()
    {
        return path;
    }

    @Override
    public ResourceLocation getBuildingName()
    {
        if (registryName != null && !registryName.getResourcePath().isEmpty())
        {
            return registryName;
        }
        final net.minecraft.block.Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
        if (block instanceof com.minecolonies.api.blocks.AbstractColonyBlock)
        {
            return ((com.minecolonies.api.blocks.AbstractColonyBlock<?>) block).getBuildingEntry().getRegistryName();
        }
        return null;
    }

    @Override
    public void updateBlockState()
    {
        // noop — block state updates handled via metadata in 1.7.10
    }

    /**
     * Get the combined inventory handler for this building.
     * In 1.7.10, we iterate containers directly rather than using LazyOptional capability.
     *
     * @return combined item handler.
     */
    public com.minecolonies.api.inventory.api.CombinedItemHandler getCombinedInventory()
    {
        if (getBuilding() == null)
        {
            return new com.minecolonies.api.inventory.api.CombinedItemHandler(getSchematicName(), getInventory());
        }

        final java.util.Set<net.minecraftforge.items.IItemHandlerModifiable> handlers = new java.util.LinkedHashSet<>();
        final net.minecraft.world.World world = colony.getWorld();
        if (world != null)
        {
            for (final int[] pos : building.getContainers())
            {
                if (WorldUtil.isBlockLoaded(world, pos[0], pos[2]) && !com.minecolonies.api.util.BlockPosUtil.equals(pos[0], pos[1], pos[2], xCoord, yCoord, zCoord))
                {
                    final TileEntity te = world.getTileEntity(pos[0], pos[1], pos[2]);
                    if (te instanceof AbstractTileEntityRack)
                    {
                        handlers.add(((AbstractTileEntityRack) te).getInventory());
                        ((AbstractTileEntityRack) te).setBuildingPos(xCoord, yCoord, zCoord);
                    }
                    else if (te != null)
                    {
                        building.removeContainerPosition(pos);
                    }
                }
            }
        }
        handlers.add(this.getInventory());
        return new com.minecolonies.api.inventory.api.CombinedItemHandler(building.getSchematicName(),
          handlers.toArray(new net.minecraftforge.items.IItemHandlerModifiable[0]));
    }
}

