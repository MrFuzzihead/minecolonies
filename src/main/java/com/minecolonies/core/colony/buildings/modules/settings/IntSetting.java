package com.minecolonies.core.colony.buildings.modules.settings;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.ICommonSettingsModule;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Stores an integer setting.
 */
public class IntSetting implements ISetting<Integer>
{
    /**
     * Default value of the setting.
     */
    private final int defaultValue;

    /**
     * The value of the setting.
     */
    private int value;

    /**
     * Create a new boolean setting.
     * @param init the initial value.
     */
    public IntSetting(final int init)
    {
        this.value = init;
        this.defaultValue = init;
    }

    /**
     * Create a new int setting.
     * @param value the value.
     * @param def the default value.
     */
    public IntSetting(final int value, final int def)
    {
        this.value = value;
        this.defaultValue = def;
    }

    /**
     * Get the setting value.
     * @return the set value.
     */
    public Integer getValue()
    {
        return value;
    }

    /**
     * Get the default value.
     * @return the default value.
     */
    public int getDefault()
    {
        return defaultValue;
    }

    @Override
    public ResourceLocation getLayoutItem()
    {
        return new ResourceLocation("minecolonies:gui/layouthuts/layoutintsetting.xml");
    }

    @Override
    public void setupHandler(
      final ISettingKey<?> key,
      final Object pane,
      final ICommonSettingsModule settingsModuleView,
      final IBuildingView building, final Object /* BOWindow: todo ModularUI2 */ window)
    {
        // [1.7.10] todo: ModularUI2 port
    }

    @Override
    public void render(
      final ISettingKey<?> key,
      final Object pane,
      final ICommonSettingsModule settingsModuleView,
      final IBuildingView building,
      final Object /* BOWindow: todo ModularUI2 */ window)
    {
        // [1.7.10] todo: ModularUI2 port
    }

    @Override
    public void copyValue(final ISetting<?> setting)
    {
        if (setting instanceof final IntSetting other)
        {
            setValue(other.getValue());
        }
    }

    /**
     * Set a new int value.
     * @param value the int to set.
     */
    public void setValue(final int value)
    {
        this.value = value;
    }
}



