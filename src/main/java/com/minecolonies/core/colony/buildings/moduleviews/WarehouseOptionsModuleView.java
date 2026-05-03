package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.WarehouseOptionsModuleWindow;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Client side version of the warehouse module.
 */
public class WarehouseOptionsModuleView extends AbstractBuildingModuleView
{
    /**
     * Storage upgrade World.
     */
    private int storageUpgrade = 0;

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.workerhuts.settings");
    }

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        storageUpgrade = buf.readInt();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new WarehouseOptionsModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/settings.png");
    }

    /**
     * Increment storage upgrade.
     */
    public void incrementStorageUpgrade()
    {
        storageUpgrade++;
    }

    /**
     * Get the current storage upgrade World.
     *
     * @return the World.
     */
    public int getStorageUpgradeLevel()
    {
        return storageUpgrade;
    }
}



