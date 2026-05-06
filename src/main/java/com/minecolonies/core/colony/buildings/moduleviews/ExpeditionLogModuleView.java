package com.minecolonies.core.colony.buildings.moduleviews;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;


// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.ExpeditionLogModuleWindow;
import com.minecolonies.core.colony.buildings.modules.expedition.ExpeditionLog;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * Building module view to display an expedition log
 */
public class ExpeditionLogModuleView extends AbstractBuildingModuleView
{
    private boolean updated;
    private boolean unlocked;
    private ExpeditionLog log = new ExpeditionLog();

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        this.unlocked = buf.readBoolean();
        if (this.unlocked)
        {
            this.log.deserialize(buf);
        }
        this.updated = true;
    }

    public boolean checkAndResetUpdated()
    {
        final boolean wasUpdated = this.updated;
        this.updated = false;
        return wasUpdated;
    }

    public ExpeditionLog getLog()
    {
        return this.log;
    }

    @Override
    public boolean isPageVisible()
    {
        return this.unlocked;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new ExpeditionLogModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/sword.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.gui.workerhuts.expeditionlog");
    }
}



