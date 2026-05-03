package com.minecolonies.core.colony.buildings.moduleviews;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModuleView;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.modules.building.SettingsModuleWindow;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import com.minecolonies.core.network.messages.server.colony.building.TriggerSettingMessage;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Module containing all settings (client side).
 */
public class SettingsModuleView extends AbstractBuildingModuleView implements ISettingsModuleView
{
    /**
     * Map of setting id (string) to generic setting.
     */
    final Map<ISettingKey<?>, ISetting<?>> settings = new LinkedHashMap<>();

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        final Map<ISettingKey<?>, ISetting> tempSettings = new LinkedHashMap<>();
        final int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            final ResourceLocation key = buf.readResourceLocation();
            final ISetting setting = StandardFactoryController.getInstance().deserialize(buf);
            if (setting != null)
            {
                final SettingKey<?> settingsKey = new SettingKey<>(setting.getClass(), key);
                tempSettings.put(settingsKey, setting);
                settings.putIfAbsent(settingsKey, setting);
            }
        }

        for (final Map.Entry<ISettingKey<?>, ISetting<?>> entry : new ArrayList<>(settings.entrySet()))
        {
            final ISetting syncSetting = tempSettings.get(entry.getKey());
            if (syncSetting == null)
            {
                settings.remove(entry.getKey());
            }
            else if (entry.getValue() != syncSetting)
            {
                entry.getValue().updateSetting(syncSetting);
                entry.getValue().copyValue(syncSetting);
            }
        }
    }

    /**
     * Get the full settings map.
     *
     * @return the list of string key and ISetting value.
     */
    public List<ISettingKey<? extends ISetting<?>>> getSettingsToShow()
    {
        final List<ISettingKey<? extends ISetting<?>>> filteredSettings = new ArrayList<>();
        for (Map.Entry<ISettingKey<?>, ISetting<?>> setting : settings.entrySet())
        {
            if (setting.getValue().isActive(this) || !setting.getValue().shouldHideWhenInactive())
            {
                filteredSettings.add((ISettingKey<? extends ISetting<?>>) setting.getKey());
            }
        }
        return filteredSettings;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends ISetting<?>> T getSetting(final ISettingKey<T> key)
    {
        return (T) settings.getOrDefault(key, null);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Object /* BOWindow: todo ModularUI2 */ getWindow()
    {
        return new SettingsModuleWindow(this);
    }

    @Override
    public ResourceLocation getIconResourceLocation()
    {
        return new ResourceLocation(Constants.MOD_ID, "textures/gui/modules/settings.png");
    }

    @Override
    public String getDesc()
    {
        return String.translatable("com.minecolonies.coremod.gui.workerhuts.settings");
    }

    @Override
    public void trigger(final ISettingKey<?> key)
    {
        final ISetting setting = settings.get(key);
        if (setting.isActive(this))
        {
            setting.trigger();
            Network.getNetwork().sendToServer(new TriggerSettingMessage(getColony(), key, setting, getProducer().getRuntimeID(), buildingView == null ? new int[]{0,0,0} : buildingView.getPosition()));
        }
    }

    @Override
    public void updateSetting(final ISettingKey<?> settingKey, final ISetting<?> value, final EntityPlayerMP sender)
    {
        if (settings.containsKey(settingKey))
        {
            settings.put(settingKey, value);
        }
    }
}




