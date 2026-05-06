package com.minecolonies.core.colony;
import net.minecraft.world.entity.animal.Animal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.minecolonies.api.colony.managers.interfaces.IAnimalDataView;

// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;

public class AnimalDataView implements IAnimalDataView
{
    /**
     * The id of the animal.
     */
    int id;

    /**
     * The colony view.
     */
    ColonyView colonyView;
    
    /**
     * The home building of the animal.
     */
    @Nullable
    private int[] homeBuilding;

    /**
     * The combat cooldown of the animal.
     */
    private float combatCooldown;

    /**
     * Constructor
     * 
     * @param id
     * @param colonyView
     */
    public AnimalDataView(int id, ColonyView colonyView)
    {
        this.id = id;
        this.colonyView = colonyView;
    }

    /**
     * Deserialize the animal data view from a network buffer.
     *
     * @param buf the buffer to deserialize from
     */
    @Override
    public void deserialize(@NotNull PacketBuffer buf)
    {
        homeBuilding = buf.readBoolean() ? buf.readBlockPos() : null;
        combatCooldown = buf.readFloat();
    }

    /**
     * Returns the id of the animal data view.
     *
     * @return the id of the animal data view
     */
    @Override
    public int getId()
    {
        return id;
    }
    
    /**
     * Gets the home building of the animal. This is the building that the animal considers to be its home.
     *
     * @return the home building of the animal, or null if the animal does not have a home building.
     */
    @Override
    @Nullable
    public int[] getHomeBuilding()
    {
        return homeBuilding;
    }

    /**
     * Gets the combat cooldown of the animal.
     *
     * @return the combat cooldown of the animal
     */
    @Override
    public float getCombatCooldown()
    {
        return combatCooldown;
    }
}


