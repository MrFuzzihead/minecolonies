package com.minecolonies.core.network.messages.server;

import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.MessageUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_BANNER_RALLY_GUARDS_GUI_ERROR;
import static com.minecolonies.core.items.ItemBannerRallyGuards.toggleBanner;

/**
 * Toggles a rallying banner
 */
public class ToggleBannerRallyGuardsMessage implements IMessage
{
    /**
     * The banner to be toggled.
     */
    private ItemStack banner;

    /**
     * Empty constructor used when registering the message
     */
    public ToggleBannerRallyGuardsMessage()
    {
        super();
    }

    /**
     * Toggle the banner
     *
     * @param banner The banner to be toggled.
     */
    public ToggleBannerRallyGuardsMessage(final ItemStack banner)
    {
        super();
        this.banner = banner;
    }

    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        banner = buf.readItem();
    }

    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeItem(banner);
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.TRUE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(player.getInventory()),
          (itemStack -> ItemStackUtils.compareItemStacksIgnoreStackSize(itemStack, banner)));

        if (slot == -1)
        {
            MessageUtils.format(COM_MINECOLONIES_BANNER_RALLY_GUARDS_GUI_ERROR).sendTo(player);
            return;
        }

        toggleBanner(player.getInventory().getItem(slot), player);
    }
}



