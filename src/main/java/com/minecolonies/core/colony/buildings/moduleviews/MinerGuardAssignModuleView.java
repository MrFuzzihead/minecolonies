package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.WindowMineGuardModule;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Miner guard assignment module.
 */
public class MinerGuardAssignModuleView extends AbstractBuildingModuleView
{
    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new WindowMineGuardModule(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/sword.png");
    }
    
    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.miner.guardassign");
    }
}



