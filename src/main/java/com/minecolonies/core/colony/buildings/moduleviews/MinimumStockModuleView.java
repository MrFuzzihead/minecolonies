package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.colony.buildings.modules.IMinimumStockModuleView;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.MinimumStockModuleWindow;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Client side representation of the minimum stock module.
 */
public class MinimumStockModuleView extends AbstractBuildingModuleView implements IMinimumStockModuleView
{
    /**
     * The minimum stock.
     */
    private final List<Tuple<ItemStorage, Integer>> minimumStock = new ArrayList<>();

    /**
     * If the stock limit was reached.
     */
    private boolean reachedLimit = false;

    /**
     * Read this view from a {@link PacketBuffer}.
     *
     * @param buf The buffer to read this view from.
     */
    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        minimumStock.clear();
        final int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            minimumStock.add(new Tuple<>(new ItemStorage(buf.readItem()), buf.readInt()));
        }
        reachedLimit = buf.readBoolean();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new MinimumStockModuleWindow(this);
    }

    @Override
    public List<Tuple<ItemStorage, Integer>> getStock()
    {
        return minimumStock;
    }

    @Override
    public boolean hasReachedLimit()
    {
        return reachedLimit;
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/stock.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.warehouse.stock");
    }
}



