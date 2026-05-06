package com.minecolonies.core.colony.buildings.modules;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IEntityListModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;

import com.minecolonies.api.colony.buildings.modules.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for all buildings that need a list of mobs to toggle for various reasons.
 */
public class EntityListModule extends AbstractBuildingModule implements IEntityListModule, IPersistentModule
{
    /**
     * NBTBase to store the EntityCreature list.
     */
    private static final String TAG_MOBLIST = "newmoblist";

    /**
     * List of allowed items.
     */
    private final Set<ResourceLocation> mobsAllowed = new HashSet<>();

    /**
     * Unique id of this module.
     */
    private final String id;

    /**
     * Construct a new grouped itemlist module with the unique list identifier.
     * @param id the list id.
     */
    public EntityListModule(final String id)
    {
        super();
        this.id = id;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound)
    {
        if (compound.contains(id))
        {
            compound = compound.getCompoundTag(id);
        }

        final NBTTagList filterableList = compound.getTagList(TAG_MOBLIST, NBTBase.TAG_STRING);
        for (int i = 0; i < filterableList.size(); ++i)
        {
            final ResourceLocation res = new ResourceLocation(filterableList.getString(i));
            if (ForgeRegistries.ENTITY_TYPES.containsKey(res))
            {
                mobsAllowed.add(res);
            }
        }
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        @NotNull final NBTTagList filteredMobs = new NBTTagList();
        for (@NotNull final ResourceLocation EntityCreature : mobsAllowed)
        {
            filteredMobs.add(NBTTagString.valueOf(EntityCreature.toString()));
        }
        compound.setTag(TAG_MOBLIST, filteredMobs);
    }

    @Override
    public void addEntity(final ResourceLocation item)
    {
        mobsAllowed.add(item);
        markDirty();
    }

    @Override
    public boolean isEntityInList(final ResourceLocation entity)
    {
        return mobsAllowed.contains(entity);
    }

    @Override
    public void removeEntity(final ResourceLocation item)
    {
        mobsAllowed.remove(item);
        markDirty();
    }

    @Override
    public ImmutableList<ResourceLocation> getList()
    {
        return ImmutableList.copyOf(mobsAllowed);
    }

    @Override
    public String getListIdentifier()
    {
        return this.id;
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(mobsAllowed.size());
        for (final ResourceLocation entity : mobsAllowed)
        {
            buf.writeRegistryIdUnsafe(ForgeRegistries.ENTITY_TYPES, entity);
        }
    }

    @Override
    public String getId()
    {
        return this.id;
    }
}





