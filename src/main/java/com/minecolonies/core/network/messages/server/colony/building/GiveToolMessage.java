package com.minecolonies.core.network.messages.server.colony.building;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_POS;

/**
 * Message to set the beekeeper scepter in the player inventory.
 */
public class GiveToolMessage extends AbstractBuildingServerMessage<AbstractBuilding>
{
    /**
     * The item to give.
     */
    private Item item;

    /**
     * Empty standard constructor.
     */
    public GiveToolMessage()
    {
        super();
    }

    /**
     * Create a new tool message.
     * @param building the building it's created from.
     * @param item the item to give.
     */
    public GiveToolMessage(final IBuildingView building, final Item item)
    {
        super(building);
        this.item = item;
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {
        buf.writeItem(new ItemStack(item, 1));
    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {
        item = buf.readItem().getItem();
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final AbstractBuilding building)
    {
        final Player player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        final ItemStack scepter = InventoryUtils.getOrCreateItemAndPutToHotbarAndSelectOrDrop(item, player, item::getDefaultInstance, true);
        final NBTTagCompound compound = scepter.getOrCreateTag();
        BlockPosUtil.write(compound, TAG_POS, building.getID());
        compound.putInt(TAG_ID, colony.getID());

        player.getInventory().setChanged();
    }
}



