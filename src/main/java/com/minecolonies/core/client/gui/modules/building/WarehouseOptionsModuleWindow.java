package com.minecolonies.core.client.gui.modules.building;

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
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.AbstractModuleWindow;
import com.minecolonies.core.colony.buildings.moduleviews.WarehouseOptionsModuleView;
import com.minecolonies.core.colony.buildings.utils.BuildingBuilderResource;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingWareHouse;
import com.minecolonies.core.network.messages.server.colony.building.MarkBuildingDirtyMessage;
import com.minecolonies.core.network.messages.server.colony.building.warehouse.SortBuildingMessage;
import com.minecolonies.core.network.messages.server.colony.building.warehouse.UpgradeWarehouseMessage;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
// [1.7.10] items shim in com.minecolonies.api.shim

import static com.minecolonies.api.util.constant.TranslationConstants.LABEL_X_OF_Z;
import static com.minecolonies.api.util.constant.TranslationConstants.WAREHOUSE_SORTED;
import static com.minecolonies.api.util.constant.WindowConstants.*;
import static com.minecolonies.core.client.gui.modules.building.WindowBuilderResModule.*;

/**
 * Object (BOWindow: todo ModularUI2 removed) for the warehouse options.
 */
public class WarehouseOptionsModuleWindow extends AbstractModuleWindow<WarehouseOptionsModuleView>
{
    /**
     * Required building World for sorting.
     */
    private static final int BUILDING_LEVEL_FOR_SORTING = 3;

    /**
     * Warehouse constants
     */
    private static final String SORT_WAREHOUSE_BUTTON = "sort";

    /**
     * If further upgrades should be locked.
     */
    private boolean lockUpgrade = false;

    /**
     * Constructor for window warehouse hut.
     * @param module the module belonging to it.
     */
    public WarehouseOptionsModuleWindow(final WarehouseOptionsModuleView module)
    {
        super(module, new ResourceLocation(Constants.MOD_ID, "gui/layouthuts/layoutwarehouseoptions.xml"));
        registerButton(RESOURCE_ADD, this::transferItems);
        registerButton(SORT_WAREHOUSE_BUTTON, this::sortWarehouse);
    }

    @Override
    public void onOpened()
    {
        if (buildingView.getBuildingLevel() < BUILDING_LEVEL_FOR_SORTING)
        {
            final ButtonImage sortButton = findPaneOfTypeByID(SORT_WAREHOUSE_BUTTON, ButtonImage.class);
            PaneBuilders.tooltipBuilder()
                .append(String.translatable("com.minecolonies.coremod.gui.warehouse.sort.disabled.1", BUILDING_LEVEL_FOR_SORTING))
                .appendNL(String.translatable("com.minecolonies.coremod.gui.warehouse.sort.disabled.2", BUILDING_LEVEL_FOR_SORTING))
                .hoverPane(sortButton)
                .build();
            sortButton.disable();
        }

        super.onOpened();

        updateResourcePane();
        //Make sure we have a fresh view
        Network.getNetwork().sendToServer(new MarkBuildingDirtyMessage(this.buildingView));
    }

    /**
     * Update one row pad with its resource informations.
     */
    private void updateResourcePane()
    {
        final BuildingBuilderResource resource = new BuildingBuilderResource(new ItemStack(Blocks.EMERALD_BLOCK, 1), 1);

        final int amountToSet;
        final Inventory inventory = this.mc.player.getInventory();
        final boolean isCreative = this.mc.player.isCreative();
        if (isCreative)
        {
            amountToSet = resource.getAmount();
        }
        else
        {
            amountToSet = InventoryUtils.getItemCountInItemHandler(new InvWrapper(inventory), resource.getItem());
        }
        resource.setPlayerAmount(amountToSet);

        final Text resourceLabel = findPaneOfTypeByID(RESOURCE_NAME, Text.class);
        final Text resourceMissingLabel = findPaneOfTypeByID(RESOURCE_MISSING, Text.class);
        final Text neededLabel = findPaneOfTypeByID(RESOURCE_AVAILABLE_NEEDED, Text.class);
        final Button addButton = findPaneOfTypeByID(RESOURCE_ADD, Button.class);

        BuildingBuilderResource.RessourceAvailability availability = resource.getAvailabilityStatus();

        if (moduleView.getStorageUpgradeLevel() >= BuildingWareHouse.MAX_STORAGE_UPGRADE || buildingView.getBuildingLevel() < buildingView.getBuildingMaxLevel() || lockUpgrade)
        {
            availability = BuildingBuilderResource.RessourceAvailability.NOT_NEEDED;
        }

        findPaneOfTypeByID(UPGRADE_PROGRESS_LABEL, Text.class).setText(String.translatable(LABEL_X_OF_Z,
            moduleView.getStorageUpgradeLevel(),
            BuildingWareHouse.MAX_STORAGE_UPGRADE));

        switch (availability)
        {
            case DONT_HAVE:
                addButton.disable();
                resourceLabel.setColors(RED);
                resourceMissingLabel.setColors(RED);
                neededLabel.setColors(RED);
                break;
            case NEED_MORE:
                addButton.enable();
                resourceLabel.setColors(RED);
                resourceMissingLabel.setColors(RED);
                neededLabel.setColors(RED);
                break;
            case HAVE_ENOUGH:
                addButton.enable();
                resourceLabel.setColors(DARKGREEN);
                resourceMissingLabel.setColors(DARKGREEN);
                neededLabel.setColors(DARKGREEN);
                break;
            case NOT_NEEDED:
            default:
                addButton.disable();
                resourceLabel.setColors(BLACK);
                resourceMissingLabel.setColors(BLACK);
                neededLabel.setColors(BLACK);
                if (buildingView.getBuildingLevel() < buildingView.getBuildingMaxLevel())
                {
                    resourceLabel.hide();
                    resourceMissingLabel.hide();
                    neededLabel.hide();
                    addButton.setText(String.literal("X").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
                    PaneBuilders.tooltipBuilder()
                        .append(String.translatable("com.minecolonies.coremod.gui.warehouse.upgrade.disabled.1", buildingView.getBuildingMaxLevel()))
                        .appendNL(String.translatable("com.minecolonies.coremod.gui.warehouse.upgrade.disabled.2", buildingView.getBuildingMaxLevel()))
                        .hoverPane(addButton)
                        .build();
                }
                break;
        }

        resourceLabel.setText(String.literal(resource.getName()));
        final int missing = resource.getMissingFromPlayer();
        if (missing < 0)
        {
            resourceMissingLabel.setText(String.literal(Integer.toString(missing)));
        }
        else
        {
            resourceMissingLabel.clearText();
        }

        neededLabel.setText(String.literal(resource.getAvailable() + " / " + resource.getAmount()));
        findPaneOfTypeByID(RESOURCE_QUANTITY_MISSING, Text.class).setText(String.literal(Integer.toString(resource.getAmount() - resource.getAvailable())));

        if(buildingView.getBuildingLevel() >= buildingView.getBuildingMaxLevel())
        {
            final ItemStack resourceStackOfOne = new ItemStack(resource.getItem(), 1);
            resourceStackOfOne.setTag(resource.getItemStack().getTag());
            findPaneOfTypeByID(RESOURCE_ICON, ItemIcon.class).setItem(resourceStackOfOne);
        }
    }

    /**
     * On Button click transfer Items.
     */
    private void transferItems()
    {
        Network.getNetwork().sendToServer(new UpgradeWarehouseMessage(this.buildingView));
        moduleView.incrementStorageUpgrade();
        lockUpgrade = true;
        this.updateResourcePane();
    }

    /**
     * On button click for warehouse sorting.
     */
    private void sortWarehouse()
    {
        if (buildingView.getBuildingLevel() >= BUILDING_LEVEL_FOR_SORTING)
        {
            Network.getNetwork().sendToServer(new SortBuildingMessage(this.buildingView));
            MessageUtils.format(WAREHOUSE_SORTED).sendTo(Minecraft.getInstance().player);
        }
    }
}





