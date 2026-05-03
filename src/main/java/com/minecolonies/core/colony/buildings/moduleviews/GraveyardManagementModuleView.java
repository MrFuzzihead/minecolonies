package com.minecolonies.core.colony.buildings.moduleviews;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.client.gui.modules.building.GraveyardManagementWindow;
import com.minecolonies.core.tileentities.TileEntityGrave;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
// [1.7.10] block.entity removed
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraveyardManagementModuleView extends AbstractBuildingModuleView
{
    /**
     * Contains a view object of all the graves in the colony.
     */
    @NotNull
    private List<int[]> graves = new ArrayList<>();

    /**
     * Contains a view object of all the restingCitizen in the colony.
     */
    @NotNull
    private List<String> restingCitizen = new ArrayList<>();

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        graves = new ArrayList<>();
        final int size = buf.readInt();
        for (int i = 1; i <= size; i++)
        {
            @NotNull final int[] pos = buf.readBlockPos();
            graves.add(pos);
        }

        restingCitizen = new ArrayList<>();
        final int sizeRIP = buf.readInt();
        for (int i = 1; i <= sizeRIP; i++)
        {
            restingCitizen.add(buf.readUtf());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new GraveyardManagementWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/grave.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.gui.workerhuts.enchanter.workers");
    }

    /**
     * Getter of the graves list.
     *
     * @return an unmodifiable List.
     */
    @NotNull
    public List<int[]> getGraves()
    {
        return graves;
    }

    /**
     * Clean the list of graves if a grave is missing from the world.
     */
    public void cleanGraves()
    {
        for (final int[] grave : new ArrayList<>(graves))
        {
            final BlockEntity entity = buildingView.getColony().getWorld().getBlockEntity(grave);
            if (!(entity instanceof TileEntityGrave))
            {
                graves.remove(grave);
            }
        }
    }

    /**
     * Getter of the restingCitizen list.
     *
     * @return an unmodifiable List.
     */
    @NotNull
    public List<String> getRestingCitizen()
    {
        return Collections.unmodifiableList(restingCitizen);
    }
}




