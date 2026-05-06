package com.minecolonies.core.colony.buildings.modules;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.core.colony.buildings.modules.expedition.ExpeditionLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Building module that stores an expedition log
 */
public class ExpeditionLogModule extends AbstractBuildingModule implements IPersistentModule
{
    private static final String TAG_LOG = "expedition";

    @NotNull private final ExpeditionLog log = new ExpeditionLog();
    @Nullable private final ResourceLocation research;

    public ExpeditionLogModule(@Nullable ResourceLocation research)
    {
        this.research = research;
    }

    @NotNull
    public ExpeditionLog getLog()
    {
        return this.log;
    }

    @Override
    public void serializeNBT(@NotNull final NBTTagCompound compound)
    {
        this.log.serializeNBT(compound);
    }

    @Override
    public void deserializeNBT(@NotNull final NBTTagCompound compound)
    {
        final NBTTagCompound log = compound.contains(TAG_LOG) ? compound.getCompoundTag(TAG_LOG) : compound;
        this.log.deserializeNBT(log);
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        final boolean unlocked = research == null || getBuilding().getColony().getResearchManager().getResearchEffects().getEffectStrength(research) > 0;

        buf.writeBoolean(unlocked);
        if (unlocked)
        {
            this.log.serialize(buf);
        }
    }

}



