package com.minecolonies.core.client.gui.containers;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.crafting.ModCraftingTypes;
import com.minecolonies.api.inventory.container.ContainerCraftingBrewingstand;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.buildings.moduleviews.CraftingModuleView;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
import com.minecolonies.core.network.messages.server.colony.building.worker.AddRemoveRecipeMessage;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.api.util.constant.TranslationConstants.WARNING_MAXIMUM_NUMBER_RECIPES;
import static com.minecolonies.api.util.constant.translation.BaseGameTranslationConstants.BASE_GUI_DONE;

/**
 * BrewingStand crafting gui.
 */
public class WindowBrewingstandCrafting extends AbstractContainerScreen<ContainerCraftingBrewingstand>
{
    private static final ResourceLocation BREWING_STAND_LOCATION = new ResourceLocation("textures/gui/container/brewing_stand.png");

    /**
     * X offset of the button.
     */
    private static final int BUTTON_X_OFFSET = 1;

    /**
     * Y offset of the button.
     */
    private static final int BUTTON_Y_POS = 170;

    /**
     * Button width.
     */
    private static final int BUTTON_WIDTH = 150;

    /**
     * Button height.
     */
    private static final int BUTTON_HEIGHT = 20;

    /**
     * The building the window belongs to.
     */
    private final ContainerCraftingBrewingstand container;

    /**
     * The building assigned to this.
     */
    private final AbstractBuildingView building;

    /**
     * The module this crafting window is for.
     */
    private final CraftingModuleView module;

    /**
     * Create a crafting gui window.
     *
     * @param container       the container.
     * @param playerInventory the player inv.
     * @param iTextComponent  the display text String.
     */
    public WindowBrewingstandCrafting(final ContainerCraftingBrewingstand container, final Inventory playerInventory, final String iTextComponent)
    {
        super(container, playerInventory, iTextComponent);
        this.container = container;
        this.building = (AbstractBuildingView) IColonyManager.getInstance().getBuildingView(playerInventory.player.World.dimension(), container.getPos());
        this.module = (CraftingModuleView) building.getModuleView(container.getModuleId());
    }

    @NotNull
    public AbstractBuildingView getBuildingView()
    {
        return building;
    }

    @Override
    protected void init()
    {
        super.init();
        final String buttonDisplay = String.translatable(module.canLearn(ModCraftingTypes.BREWING.get()) ? BASE_GUI_DONE : WARNING_MAXIMUM_NUMBER_RECIPES);
        /*
         * The button to click done after finishing the recipe.
         */
        final Button doneButton = new Button.Builder(buttonDisplay, new WindowBrewingstandCrafting.OnButtonPress()).pos(leftPos + BUTTON_X_OFFSET, topPos + BUTTON_Y_POS).size(BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(doneButton);
        if (!module.canLearn(ModCraftingTypes.BREWING.get()))
        {
            doneButton.active = false;
        }
    }

    public class OnButtonPress implements Button.OnPress
    {
        @Override
        public void onPress(@NotNull final Button button)
        {
            if (module.canLearn(ModCraftingTypes.BREWING.get()))
            {
                final List<ItemStorage> input = new ArrayList<>();
                input.add(new ItemStorage(container.slots.get(0).getItem()));
                input.add(new ItemStorage(container.slots.get(1).getItem()));
                input.add(new ItemStorage(container.slots.get(2).getItem()));
                input.add(new ItemStorage(container.slots.get(3).getItem()));

                final ItemStack
                  primaryOutput = net.minecraftforge.common.brewing.BrewingRecipeRegistry.getOutput(container.slots.get(3).getItem(), container.slots.get(0).getItem()).copy();
                primaryOutput.setCount(3);

                if (!ItemStackUtils.isEmpty(primaryOutput))
                {
                    Network.getNetwork().sendToServer(new AddRemoveRecipeMessage(building, input, 1, primaryOutput, false, Blocks.BREWING_STAND, module.getProducer().getRuntimeID()));
                }
            }
        }
    }

    @Override
    public void render(@NotNull final GuiGraphics stack, int x, int y, float z)
    {
        this.renderBackground(stack);
        super.render(stack, x, y, z);
        this.renderTooltip(stack, x, y);
    }

    protected void renderBg(@NotNull final GuiGraphics stack, final float partialTicks, final int mouseX, final int mouseY)
    {
        stack.blit(BREWING_STAND_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int l = Mth.clamp((20 - 1) / 20, 0, 18);
        if (l > 0) {
            stack.blit(BREWING_STAND_LOCATION, mouseX + 60, mouseY + 44, 176, 29, l, 4);
        }
    }
}




