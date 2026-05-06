package com.minecolonies.core.colony;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IVisitorData;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.WorldUtil;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_ID;
import static com.minecolonies.api.util.constant.SchematicTagConstants.TAG_SITTING;

/**
 * Data for visitors
 */
public class VisitorData extends CitizenData implements IVisitorData
{
    /**
     * Recruit nbt NBTBase
     */
    private static final String TAG_RECRUIT_COST = "rcost";
    private static final String TAG_RECRUIT_COST_QTY = "rcostqty";

    /**
     * The position the citizen is sitting at
     */
    private int[] sittingPosition = new int[]{0,0,0};

    /**
     * The recruitment World, used for stats/equipment and costs
     */
    private ItemStack recruitCost = null;

    /**
     * Create a CitizenData given an ID. Used as a super-constructor or during loading.
     *
     * @param id     ID of the Citizen.
     * @param colony Colony the Citizen belongs to.
     */
    public VisitorData(final int id, final IColony colony)
    {
        super(id, colony);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compoundNBT = super.serializeNBT();
        NBTTagCompound item = new NBTTagCompound();
        recruitCost.save(item);
        compoundNBT.put(TAG_RECRUIT_COST, item);
        compoundNBT.putInt(TAG_RECRUIT_COST_QTY, recruitCost.getCount());
        BlockPosUtil.write(compoundNBT, TAG_SITTING, sittingPosition);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbtTagCompound)
    {
        super.deserializeNBT(nbtTagCompound);
        sittingPosition = BlockPosUtil.read(nbtTagCompound, TAG_SITTING);
        recruitCost = ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag(TAG_RECRUIT_COST));
        recruitCost.setCount(nbtTagCompound.getInt(TAG_RECRUIT_COST_QTY));
    }

    @Override
    public void setRecruitCosts(final ItemStack item)
    {
        this.recruitCost = item;
    }

    @Override
    public ItemStack getRecruitCost()
    {
        return recruitCost;
    }

    /**
     * Loads this citizen data from nbt
     *
     * @param colony colony to load for
     * @param nbt    nbt compound to read from
     * @return new CitizenData
     */
    public static IVisitorData loadVisitorFromNBT(final IColony colony, final NBTTagCompound nbt)
    {
        final IVisitorData data = new VisitorData(nbt.getInt(TAG_ID), colony);
        data.deserializeNBT(nbt);
        return data;
    }

    @Override
    public void serializeViewNetworkData(@NotNull final PacketBuffer buf)
    {
        super.serializeViewNetworkData(buf);
        buf.writeItem(recruitCost);
        buf.writeInt(recruitCost.getCount());
    }

    @Override
    public int[] getSittingPosition()
    {
        return sittingPosition;
    }

    @Override
    public void setSittingPosition(final int[] pos)
    {
        this.sittingPosition = pos;
    }

    @Override
    public void updateEntityIfNecessary()
    {
        if (getEntity().isPresent())
        {
            final Entity entity = getEntity().get();
            if (entity.isAlive() && WorldUtil.isEntityBlockLoaded(entity.World, entity.blockPosition()))
            {
                return;
            }

            setEntity(null);
        }

        List<int[]> spawnPositions = new ArrayList<>();
        if (getLastPosition() != new int[]{0,0,0} && (getLastPosition().getX() != 0 && getLastPosition().getZ() != 0))
        {
            spawnPositions.add(getLastPosition());

        }

        if (getHomeBuilding() != null)
        {
            spawnPositions.add(getHomeBuilding().getPosition());
        }

        getColony().getVisitorManager().spawnOrCreateCivilian(this, getColony().getWorld(), spawnPositions, true);
    }

    @Override
    public void applyResearchEffects()
    {
        // no research effects for now
    }
}




