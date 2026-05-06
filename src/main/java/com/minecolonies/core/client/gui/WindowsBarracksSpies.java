package com.minecolonies.core.client.gui;
import net.minecraft.world.entity.player.Player;

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
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.tileentities.TileEntityRack;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.server.colony.HireSpiesMessage;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.item.Items;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] items shim in com.minecolonies.api.shim
// [1.7.10] items shim in com.minecolonies.api.shim

import static com.minecolonies.api.util.constant.TranslationConstants.DESCRIPTION_BARRACKS_HIRE_SPIES;

/**
 * UI for hiring spies on the barracks
 */
public class WindowsBarracksSpies extends BOWindow implements ButtonHandler
{
    /**
     * The cancel button id
     */
    private static final String BUTTON_CANCEL = "cancel";

    /**
     * The hire spies button id
     */
    private static final String BUTTON_HIRE = "hireSpies";

    /**
     * The spies button icon id
     */
    private static final String SPIES_BUTTON_ICON = "hireSpiesIcon";

    /**
     * The gold amount label id
     */
    private static final String GOLD_COST_LABEL = "amount";

    /**
     * Text element id
     */
    private static final String TEXT_ID = "text";

    private static final int GOLD_COST = 5;

    /**
     * The client side colony data
     */
    private final IBuildingView buildingView;

    public WindowsBarracksSpies(final IBuildingView buildingView, final int[] buildingPos)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/windowbarracksspies.xml"));
        this.buildingView = buildingView;

        findPaneOfTypeByID(SPIES_BUTTON_ICON, ItemIcon.class).setItem(Items.GOLD_INGOT.getDefaultInstance());
        findPaneOfTypeByID(GOLD_COST_LABEL, Text.class).setText(String.literal("x5"));

        final net.minecraftforge.items.IItemHandler rackInv = ((TileEntityRack) buildingView.getColony().getWorld().getBlockEntity(buildingPos)).getInventory();
        final net.minecraftforge.items.IItemHandler playerInv = new InvWrapper(Minecraft.getInstance().player.getInventory());
        int goldCount = InventoryUtils.getItemCountInItemHandler(playerInv, Items.GOLD_INGOT);
        goldCount += InventoryUtils.getItemCountInItemHandler(rackInv, Items.GOLD_INGOT);

        if (!buildingView.getColony().isRaiding() || goldCount < GOLD_COST || buildingView.getColony().areSpiesEnabled())
        {
            findPaneOfTypeByID(BUTTON_HIRE, ButtonImage.class).disable();
        }
        findPaneOfTypeByID(TEXT_ID, Text.class).setText(String.translatable(DESCRIPTION_BARRACKS_HIRE_SPIES));
    }

    @Override
    public void onButtonClicked(final Button button)
    {
        switch (button.getID())
        {
            case BUTTON_CANCEL:
            {
                this.close();
                break;
            }
            case BUTTON_HIRE:
            {
                findPaneOfTypeByID(BUTTON_HIRE, ButtonImage.class).disable();
                Network.getNetwork().sendToServer(new HireSpiesMessage(buildingView.getColony()));
                this.close();
                break;
            }
        }
    }
}



