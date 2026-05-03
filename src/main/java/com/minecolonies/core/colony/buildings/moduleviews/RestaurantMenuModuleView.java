package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.translation.RequestSystemTranslationConstants;
import com.minecolonies.core.client.gui.modules.building.RestaurantMenuModuleWindow;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.minecolonies.core.colony.buildings.modules.RestaurantMenuModule.STOCK_PER_LEVEL;
import static com.minecolonies.core.colony.buildings.workerbuildings.BuildingCook.FOOD_EXCLUSION_LIST;

/**
 * Client side version of food menu.
 */
public class RestaurantMenuModuleView extends AbstractBuildingModuleView
{
    /**
     * The menu.
     */
    private final List<ItemStorage> menu = new ArrayList<>();

    @Override
    public void deserialize(final @NotNull PacketBuffer buf)
    {
        menu.clear();
        final int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            menu.add(new ItemStorage(buf.readItem()));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new RestaurantMenuModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/" + FOOD_EXCLUSION_LIST + ".png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_FOOD);
    }

    /**
     * Get the menu for the restaurant.
     * @return the menu.
     */
    public List<ItemStorage> getMenu()
    {
        return menu;
    }

    public boolean hasReachedLimit()
    {
        return menu.size() >= buildingView.getBuildingLevel() * STOCK_PER_LEVEL;
    }
}



