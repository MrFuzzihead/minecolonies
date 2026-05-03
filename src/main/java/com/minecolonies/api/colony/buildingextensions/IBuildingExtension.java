package com.minecolonies.api.colony.buildingextensions;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildingextensions.modules.IBuildingExtensionModule;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.modules.IModuleContainer;
import com.minecolonies.api.util.BlockPosUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;

/**
 * Interface for building extension instances.
 */
public interface IBuildingExtension extends IModuleContainer<IBuildingExtensionModule>
{
    /**
     * Return the building extension type for this building extension.
     *
     * @return the building extension registry entry.
     */
    @NotNull BuildingExtensionRegistries.BuildingExtensionEntry getBuildingExtensionType();

    /**
     * Gets the position of the building extension.
     *
     * @return central location of the building extension.
     */
    @NotNull int[] getPosition();

    /**
     * Getter for the owning building of the building extension.
     *
     * @return the id or null.
     */
    @Nullable int[] getBuildingId();

    /**
     * Sets the owning building of the building extension.
     *
     * @param buildingId id of the building.
     */
    void setBuilding(final int[] buildingId);

    /**
     * Resets the ownership of the building extension.
     */
    void resetOwningBuilding();

    /**
     * Has the building extension been taken.
     *
     * @return true if the building extension is not free to use, false after releasing it.
     */
    boolean isTaken();

    /**
     * Get the distance to a building.
     *
     * @param building the building to get the distance to.
     * @return the distance
     */
    int getSqDistance(IBuildingView building);

    /**
     * Stores the NBT data of the building extension.
     */
    @NotNull NBTTagCompound serializeNBT();

    /**
     * Reconstruct the building extension from the given NBT data.
     *
     * @param compound the compound to read from.
     */
    void deserializeNBT(@NotNull NBTTagCompound compound);

    /**
     * Serialize a building extension to a buffer.
     *
     * @param buf the buffer to write the building extension data to.
     */
    void serialize(@NotNull PacketBuffer buf);

    /**
     * Deserialize a building extension from a buffer.
     *
     * @param buf the buffer to read the building extension data from.
     */
    void deserialize(@NotNull PacketBuffer buf);

    /**
     * Condition to check whether this building extension instance is currently properly placed down.
     *
     * @param colony the colony this building extension is in.
     * @return true if the building extension is correctly placed at the current position.
     */
    boolean isValidPlacement(IColony colony);

    /**
     * Hashcode implementation for this building extension.
     */
    int hashCode();

    /**
     * Equals implementation for this building extension.
     */
    boolean equals(Object other);

    /**
     * Get the unique extension id.
     * @return the unique id.
     */
    ExtensionId getId();

    /**
     * Register a specific module to the object.
     *
     * @param module the module to register.
     */
    void registerModule(@NotNull final IBuildingExtensionModule module);

    /**
     * Unique extension id.
     * @param pos the pos it's at.
     * @param entry it's entry type.
     */
    record ExtensionId(int[] pos, BuildingExtensionRegistries.BuildingExtensionEntry entry)
    {
        public NBTBase serializeNBT()
        {
            final NBTTagCompound NBTBase = new NBTTagCompound();
            BlockPosUtil.write(NBTBase, TAG_POS, pos);
            NBTBase.putString(TAG_ID, entry.getRegistryName().toString());
            return NBTBase;
        }

        public static ExtensionId deserializeNBT(final NBTTagCompound nbt)
        {
            return new ExtensionId(BlockPosUtil.read(nbt, TAG_POS), BuildingExtensionRegistries.getBuildingExtensionRegistry().getValue(ResourceLocation.tryParse(nbt.getString(TAG_ID))));
        }
    }
}




