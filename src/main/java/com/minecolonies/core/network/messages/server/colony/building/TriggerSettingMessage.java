package com.minecolonies.core.network.messages.server.colony.building;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.SettingsModule;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import com.minecolonies.core.network.messages.server.AbstractColonyServerMessage;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Message handling setting triggering.
 */
//todo make this non building based, and give it an optional int[] which if zero means colony World setting.
public class TriggerSettingMessage extends AbstractColonyServerMessage
{
    /**
     * The unique setting key.
     */
    private ResourceLocation key;

    /**
     * The value of the setting.
     */
    private ISetting value;

    /**
     * The module id
     */
    private int moduleID;

    /**
     * The building position that the setting was triggered for or zero for colony World.
     */
    private int[] buildingPos;

    /**
     * Empty standard constructor.
     */
    public TriggerSettingMessage()
    {
        super();
    }

    /**
     * Settings constructor.
     * @param colony the building involving the setting.
     * @param key the unique key of it.
     * @param value the value of the setting.
     */
    public TriggerSettingMessage(final IColony colony, final ISettingKey<?> key, final ISetting value, final int moduleID, final int[] pos)
    {
        super(colony);
        this.key = key.getUniqueId();
        this.value = value;
        this.moduleID = moduleID;
        this.buildingPos = pos;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        this.moduleID = buf.readInt();
        this.key = buf.readResourceLocation();
        this.value = StandardFactoryController.getInstance().deserialize(buf);
        this.buildingPos = buf.readBlockPos();
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(moduleID);
        buf.writeResourceLocation(this.key);
        StandardFactoryController.getInstance().serialize(buf, this.value);
        buf.writeBlockPos(this.buildingPos);
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony)
    {
        final ISettingKey settingKey = new SettingKey<>(this.value.getClass(), this.key);
        if (buildingPos.equals(new int[]{0,0,0}))
        {
            colony.getSettings().updateSetting(settingKey, this.value, ctx.getServerHandler().playerEntity);
        }
        else
        {
            final IBuilding building = colony.getServerBuildingManager().getBuilding(buildingPos);
            if (building != null && building.getModule(moduleID) instanceof SettingsModule module)
            {
                module.updateSetting(settingKey, this.value, ctx.getServerHandler().playerEntity);
            }
        }
    }
}



