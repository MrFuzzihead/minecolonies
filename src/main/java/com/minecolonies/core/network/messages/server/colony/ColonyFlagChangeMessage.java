package com.minecolonies.core.network.messages.server.colony;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.eventbus.events.colony.ColonyFlagChangedModEvent;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_BANNER_PATTERNS;

/**
 * Message to update the colony flag once set in the {@link com.minecolonies.core.client.gui.WindowBannerPicker}.
 */
public class ColonyFlagChangeMessage extends AbstractColonyServerMessage
{
    /**
     * The chosen list of patterns from the window
     */
    private NBTTagList patterns;

    /**
     * Default constructor
     **/
    public ColonyFlagChangeMessage() {super();}

    /**
     * Spawn a new change message
     *
     * @param colony      the colony the player changed the banner in
     * @param patternList the list of patterns they set in the banner picker
     */
    public ColonyFlagChangeMessage(IColony colony, NBTTagList patternList)
    {
        super(colony);

        this.patterns = patternList;
    }

    @Override
    protected void onExecute(MessageContext ctx, boolean isLogicalServer, IColony colony)
    {
        colony.setColonyFlag(patterns);
        IMinecoloniesAPI.getInstance().getEventBus().post(new ColonyFlagChangedModEvent(colony));
    }

    @Override
    protected void toBytesOverride(PacketBuffer buf)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.put(TAG_BANNER_PATTERNS, this.patterns);
        buf.writeNbt(nbt);
    }

    @Override
    protected void fromBytesOverride(PacketBuffer buf)
    {
        NBTTagCompound nbt = buf.readNbt();
        if (nbt != null)
        {
            this.patterns = nbt.getList(TAG_BANNER_PATTERNS, Constants.TAG_COMPOUND);
        }
    }
}



