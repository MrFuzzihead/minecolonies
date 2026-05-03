package com.minecolonies.api.colony.managers.interfaces.views;

import com.minecolonies.api.colony.buildingextensions.IBuildingExtension;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.buildings.workerbuildings.ITownHallView;
import com.minecolonies.api.colony.managers.interfaces.ICommonRegisteredStructureManager;
import com.minecolonies.api.network.IMessage;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IRegisteredStructureManagerView extends ICommonRegisteredStructureManager<IBuildingView, ITownHallView>
{
    /**
     * Remove a building from the ColonyView.
     *
     * @param buildingId location of the building.
     * @return null == no response.
     */
    @Nullable IMessage handleColonyViewRemoveBuildingMessage(int[] buildingId);

    /**
     * Update a ColonyView's buildings given a network data ColonyView update packet. This uses a full-replacement - buildings do not get updated and are instead overwritten.
     *
     * @param buildingId location of the building.
     * @param buf        buffer containing ColonyBuilding information.
     * @return null == no response.
     */
    @Nullable IMessage handleColonyBuildingViewMessage(int[] buildingId, @NotNull PacketBuffer buf);

    void handleColonyBuildingExtensionViewUpdateMessage(Set<IBuildingExtension> extensions);

    void deserializeFromView(boolean isNewSubscription, @NotNull PacketBuffer buf);
}


