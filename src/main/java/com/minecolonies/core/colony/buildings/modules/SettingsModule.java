package com.minecolonies.core.colony.buildings.modules;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.buildings.modules.AbstractBuildingModule;
import com.minecolonies.api.colony.buildings.modules.IPersistentModule;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import net.minecraft.nbt.NBTBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Module containing all settings.
 */
public class SettingsModule extends AbstractBuildingModule implements IPersistentModule, ISettingsModule
{
    /**
     * Map of setting id (string) to generic setting.
     */
    final Map<ISettingKey<?>, ISetting<?>> settings = new LinkedHashMap<>();

    @Override
    public <T extends ISetting<?>> T getSetting(final ISettingKey<T> key)
    {
        return (T) settings.get(key);
    }

    @Override
    @NotNull
    public <T extends ISetting<?>> Optional<T> getOptionalSetting(final ISettingKey<T> key)
    {
        final T setting = getSetting(key);
        return setting == null || !setting.isActive(this) ? Optional.empty() : Optional.of(setting);
    }

    @Override
    public ISettingsModule with(final ISettingKey<?> key, final ISetting<?> setting)
    {
        settings.put(key, setting);
        return this;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        final NBTTagCompound settingsCompound = compound.contains("settings") ? compound.getCompoundTag("settings") : compound;
        final NBTTagList list = settingsCompound.getTagList("settingslist", NBTBase.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            final NBTTagCompound entryCompound = list.getCompoundTagAt(i);
            final ResourceLocation key = new ResourceLocation(entryCompound.getString("key"));
            try
            {
                final ISetting setting = StandardFactoryController.getInstance().deserialize(entryCompound.getCompoundTag("value"));
                final ISettingKey<?> settingsKey = new SettingKey<>(setting.getClass(), key);
                if (settings.containsKey(settingsKey))
                {
                    setting.updateSetting(settings.get(settingsKey));
                    settings.put(settingsKey, setting);
                }
            }
            catch (final IllegalArgumentException ex)
            {
                Log.getLogger().warn("Detected Removed Setting");
            }
        }
    }

    @Override
    public void serializeNBT(final NBTTagCompound compound)
    {
        final NBTTagList list = new NBTTagList();
        for (final Map.Entry<ISettingKey<?>, ISetting<?>> setting : settings.entrySet())
        {
            final NBTTagCompound entryCompound = new NBTTagCompound();
            entryCompound.putString("key", setting.getKey().getUniqueId().toString());
            entrycompound.setTag("value", StandardFactoryController.getInstance().serialize(setting.getValue()));
            list.add(entryCompound);
        }
        compound.setTag("settingslist", list);
    }

    @Override
    public void serializeToView(final PacketBuffer buf)
    {
        buf.writeInt(settings.size());
        for (final Map.Entry<ISettingKey<?>, ISetting<?>> setting : settings.entrySet())
        {
            buf.writeResourceLocation(setting.getKey().getUniqueId());
            StandardFactoryController.getInstance().serialize(buf, setting.getValue());
        }
    }

    @Override
    public void updateSetting(final ISettingKey<?> settingKey, final ISetting<?> value, final EntityPlayerMP sender)
    {
        if (settings.containsKey(settingKey))
        {
            settings.put(settingKey, value);
            value.onUpdate(building, sender);
        }
    }

    @Override
    public <S, T extends ISetting<S>> S getSettingValueOrDefault(final ISettingKey<T> key, final S def)
    {
        final T setting = getSetting(key);
        return setting == null ? def : setting.getValue();
    }
}





