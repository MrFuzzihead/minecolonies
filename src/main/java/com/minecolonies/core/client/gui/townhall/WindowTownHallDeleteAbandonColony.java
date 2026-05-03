package com.minecolonies.core.client.gui.townhall;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.MouseEventCallback;
import com.ldtteam.blockui.controls.BOGuiGraphics;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonHandler;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Color;
import com.ldtteam.blockui.controls.DropDownList;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.TextField;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.Box;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.AbstractWindowSkeleton;
import com.minecolonies.core.network.messages.server.GetColonyInfoMessage;
import com.minecolonies.core.network.messages.server.PickupBlockMessage;
import com.minecolonies.core.network.messages.server.colony.ColonyAbandonOwnMessage;
import com.minecolonies.core.network.messages.server.colony.ColonyDeleteOwnMessage;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
// [1.7.10] sounds removed

import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * UI to delete or abandon an old colony.
 */
public class WindowTownHallDeleteAbandonColony extends AbstractWindowSkeleton
{
    /**
     * String constants.
     */
    private static final String DELETE_PROCEED = "com.minecolonies.core.gui.colony.delete.proceed";
    private static final String ABANDON_PROCEED = "com.minecolonies.core.gui.colony.abandon.proceed";
    private static final String DELETE_WARNING =    "com.minecolonies.core.gui.colony.delete.warning";
    private static final String ABANDON_WARNING =    "com.minecolonies.core.gui.colony.abandon.warning";

    /**
     * Townhall position
     */
    private final int[] pos;

    public WindowTownHallDeleteAbandonColony(final int[] pos, final String oldColonyName, final int[] oldColonyPos)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/townhall/windowdeleteabandoncolony.xml"));
        this.pos = pos;
        mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));

        registerButton(BUTTON_CANCEL, this::close);
        registerButton(BUTTON_PICKUP_BUILDING, this::pickup);
        registerButton(BUTTON_DELETE, this::deleteColony);
        registerButton(BUTTON_ABANDON, this::abandonColony);
        registerButton("cancelaction", this::cancel);

        registerButton(BUTTON_CONFIRM_DELETE, this::confirmDeleteColony);
        registerButton(BUTTON_CONFIRM_ABANDON, this::confirmAbandonColony);

        if (MineColonies.getConfig().getServer().allowInfiniteColonies.get())
        {
            this.findPaneOfTypeByID("abandon", ButtonImage.class).show();
            this.findPaneOfTypeByID("warningtext", Text.class).setText(String.translatable(ABANDON_WARNING, oldColonyPos.getX(), oldColonyPos.getY(), oldColonyPos.getZ(), String.literal(oldColonyName).withStyle(ChatFormatting.DARK_RED)));
        }
        else
        {
            this.findPaneOfTypeByID("abandon", ButtonImage.class).hide();
            this.findPaneOfTypeByID("warningtext", Text.class).setText(String.translatable(DELETE_WARNING, oldColonyPos.getX(), oldColonyPos.getY(), oldColonyPos.getZ(), String.literal(oldColonyName).withStyle(ChatFormatting.DARK_RED)));
        }
    }

    private void confirmAbandonColony()
    {
        Network.getNetwork().sendToServer(new ColonyAbandonOwnMessage());
        Network.getNetwork().sendToServer(new GetColonyInfoMessage(pos));
        close();
    }

    private void confirmDeleteColony()
    {
        Network.getNetwork().sendToServer(new ColonyDeleteOwnMessage());
        Network.getNetwork().sendToServer(new GetColonyInfoMessage(pos));
        close();
    }

    private void abandonColony()
    {
        this.findPaneOfTypeByID("dialog", Image.class).show();
        final Text confirmText = this.findPaneOfTypeByID("confirmtext", Text.class);
        confirmText.show();
        confirmText.setText(String.translatable(ABANDON_PROCEED));
        this.findPaneOfTypeByID("confirmdelete", ButtonImage.class).hide();
        this.findPaneOfTypeByID("confirmabandon", ButtonImage.class).show();
        this.findPaneOfTypeByID("cancelaction", ButtonImage.class).show();
    }

    private void deleteColony()
    {
        this.findPaneOfTypeByID("dialog", Image.class).show();
        final Text confirmText = this.findPaneOfTypeByID("confirmtext", Text.class);
        confirmText.show();
        confirmText.setText(String.translatable(DELETE_PROCEED));
        this.findPaneOfTypeByID("confirmdelete", ButtonImage.class).show();
        this.findPaneOfTypeByID("confirmabandon", ButtonImage.class).hide();
        this.findPaneOfTypeByID("cancelaction", ButtonImage.class).show();
    }

    private void cancel()
    {
        this.findPaneOfTypeByID("dialog", Image.class).hide();
        this.findPaneOfTypeByID("confirmtext", Text.class).hide();
        this.findPaneOfTypeByID("confirmdelete", ButtonImage.class).hide();
        this.findPaneOfTypeByID("confirmabandon", ButtonImage.class).hide();
        this.findPaneOfTypeByID("cancelaction", ButtonImage.class).hide();
    }

    /**
     * When the pickup building button was clicked.
     */
    private void pickup()
    {
        Network.getNetwork().sendToServer(new PickupBlockMessage(pos));
        close();
    }
}



