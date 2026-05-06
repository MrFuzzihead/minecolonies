package com.minecolonies.core.client.gui.modules.building;
import net.minecraft.world.entity.player.Player;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.MouseEventCallback;
import com.ldtteam.blockui.controls.BOGuiGraphics;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonHandler;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.Box;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.ldtteam.structurize.client.gui.WindowSelectRes;
import com.minecolonies.api.colony.buildings.modules.IMinimumStockModuleView;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.network.messages.server.colony.building.AddMinimumStockToBuildingModuleMessage;
import com.minecolonies.core.network.messages.server.colony.building.RemoveMinimumStockFromBuildingModuleMessage;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * Cook window class. Specifies the extras the composter has for its list.
 */
public class MinimumStockModuleWindow extends AbstractModuleWindow<IMinimumStockModuleView>
{
    /**
     * Limit reached label.
     */
    private static final String LABEL_LIMIT_REACHED = "com.minecolonies.coremod.gui.warehouse.limitreached";

    /**
     * Resource scrolling list.
     */
    private final ScrollingList resourceList;

    /**
     * Constructor for the minimum stock window view.
     *
     * @param moduleView the module view.
     */
    public MinimumStockModuleWindow(final IMinimumStockModuleView moduleView)
    {
        this(moduleView, new ResourceLocation(Constants.MOD_ID, "gui/layouthuts/layoutminimumstock.xml"));
    }

    /**
     * Constructor for the minimum stock window view.
     *
     * @param moduleView the module view.
     * @param layoutId   the layout to use for rendering this window.
     */
    public MinimumStockModuleWindow(final IMinimumStockModuleView moduleView, final ResourceLocation layoutId)
    {
        super(moduleView, layoutId);

        resourceList = this.window.findPaneOfTypeByID("resourcesstock", ScrollingList.class);

        registerButton(STOCK_ADD, this::addStock);
        if (moduleView.hasReachedLimit())
        {
            final ButtonImage button = findPaneOfTypeByID(STOCK_ADD, ButtonImage.class);
            button.setText(String.translatable(LABEL_LIMIT_REACHED));
            button.setImage(new ResourceLocation(Constants.MOD_ID, "textures/gui/builderhut/builder_button_medium_disabled.png"), false);
        }

        registerButton(STOCK_REMOVE, this::removeStock);
    }

    /**
     * Remove the stock.
     *
     * @param button the button.
     */
    private void removeStock(final Button button)
    {
        final int row = resourceList.getListElementIndexByPane(button);
        final Tuple<ItemStorage, Integer> tuple = moduleView.getStock().get(row);
        moduleView.getStock().remove(row);
        Network.getNetwork().sendToServer(new RemoveMinimumStockFromBuildingModuleMessage(buildingView, tuple.getA().getItemStack(), moduleView.getProducer().getRuntimeID()));
        updateStockList();
    }

    /**
     * Add the stock.
     */
    private void addStock()
    {
        if (!moduleView.hasReachedLimit())
        {
            new WindowSelectRes(this,
                String.empty(),
                null,
                ItemStackUtils.allItemsPlusInventory(mc.player),
                (stack, qty) -> Network.getNetwork().sendToServer(new AddMinimumStockToBuildingModuleMessage(buildingView, stack, qty)),
                true,
                String.translatable("com.minecolonies.coremod.gui.scan.select.stack")).open();
        }
    }

    @Override
    public void onOpened()
    {
        super.onOpened();
        updateStockList();
    }

    /**
     * Updates the resource list in the GUI with the info we need.
     */
    private void updateStockList()
    {
        resourceList.enable();
        resourceList.show();

        //Creates a dataProvider for the unemployed resourceList.
        resourceList.setDataProvider(new ScrollingList.DataProvider()
        {
            /**
             * The number of rows of the list.
             * @return the number.
             */
            @Override
            public int getElementCount()
            {
                return moduleView.getStock().size();
            }

            /**
             * Inserts the elements into each row.
             * @param index the index of the row/list element.
             * @param rowPane the parent Pane for the row, containing the elements to update.
             */
            @Override
            public void updateElement(final int index, @NotNull final Pane rowPane)
            {
                final ItemStack resource = moduleView.getStock().get(index).getA().getItemStack().copy();
                resource.setCount(resource.getMaxStackSize());

                rowPane.findPaneOfTypeByID(RESOURCE_NAME, Text.class).setText(resource.getHoverName());
                rowPane.findPaneOfTypeByID(QUANTITY_LABEL, Text.class).setText(String.literal(String.valueOf(moduleView.getStock().get(index).getB())));
                rowPane.findPaneOfTypeByID(RESOURCE_ICON, ItemIcon.class).setItem(resource);
            }
        });
    }
}



