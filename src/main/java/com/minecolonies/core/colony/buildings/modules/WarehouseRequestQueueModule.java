package com.minecolonies.core.colony.buildings.modules;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_REQUEST;

/**
 * The class of the citizen hut.
 */
public class WarehouseRequestQueueModule extends AbstractBuildingModule implements IPersistentModule
{
    /**
     * List of all beds.
     */
    @NotNull
    private final List<IToken<?>> requestList = new ArrayList<>();

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        final NBTTagList requestTagList = compound.getTagList(TAG_REQUEST, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < requestTagList.size(); ++i)
        {
            requestList.add(StandardFactoryController.getInstance().deserialize(requestTagList.getCompoundTagAt(i)));
        }
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        if (!requestList.isEmpty())
        {
            @NotNull final NBTTagList requestTagList = new NBTTagList();
            for (@NotNull final IToken<?> token : requestList)
            {
                requestTagList.add(StandardFactoryController.getInstance().serialize(token));
            }
            compound.setTag(TAG_REQUEST, requestTagList);
        }
    }

    @Override
    public void serializeToView(final PacketBuffer buf)
    {
        super.serializeToView(buf);
        buf.writeInt(requestList.size());
        for (final IToken<?> reqId : requestList)
        {
            StandardFactoryController.getInstance().serialize(buf, reqId);
        }
    }

    /**
     * Add request to warehouse queue.
     * @param requestToken request to add.
     */
    public void addRequest(IToken<?> requestToken)
    {
        requestList.add(requestToken);
        markDirty();
    }

    /**
     * Get a mutable version of the request list.
     * @return the mutable request list.
     */
    public List<IToken<?>> getMutableRequestList()
    {
        return requestList;
    }
}




