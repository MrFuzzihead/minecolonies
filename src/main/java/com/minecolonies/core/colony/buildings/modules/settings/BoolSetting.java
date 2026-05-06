package com.minecolonies.core.colony.buildings.modules.settings;
// [1.7.10] replaced OnlyIn/Dist with SideOnly/Side
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.ldtteam.blockui.controls.ButtonImage;
import com.minecolonies.api.colony.buildings.modules.ICommonSettingsModule;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import static com.minecolonies.api.util.constant.WindowConstants.OFF;
import static com.minecolonies.api.util.constant.WindowConstants.ON;

/**
 * Stores a boolean setting.
 */
public class BoolSetting implements ISetting<Boolean>
{
    /**
     * Default value of the setting.
     */
    private final boolean defaultValue;

    /**
     * The value of the setting.
     */
    private boolean value;

    /**
     * Create a new boolean setting.
     *
     * @param init the initial value.
     */
    public BoolSetting(final boolean init)
    {
        this.value = init;
        this.defaultValue = init;
    }

    /**
     * Create a new boolean setting.
     *
     * @param value the value.
     * @param def   the default value.
     */
    public BoolSetting(final boolean value, final boolean def)
    {
        this.value = value;
        this.defaultValue = def;
    }

    /**
     * Get the setting value.
     *
     * @return the set value.
     */
    public Boolean getValue()
    {
        return value;
    }

    /**
     * Get the default value.
     *
     * @return the default value.
     */
    public boolean getDefault()
    {
        return defaultValue;
    }

    @Override
    public ResourceLocation getLayoutItem()
    {
        return new ResourceLocation("minecolonies:gui/layouthuts/layoutboolsetting.xml");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setupHandler(
      final ISettingKey<?> key,
      final Object rowPane,
      final ICommonSettingsModule settingsModuleView,
      final IBuildingView building, final Object window)
    {
        ((com.ldtteam.blockui.Pane) rowPane).findPaneOfTypeByID("trigger", ButtonImage.class).setHandler(button -> settingsModuleView.trigger(key));
    }

    @Override
    public void render(
      final ISettingKey<?> key,
      final Object rowPane,
      final ICommonSettingsModule settingsModuleView,
      final IBuildingView building,
      final Object window)
    {
        ButtonImage triggerButton = ((com.ldtteam.blockui.Pane) rowPane).findPaneOfTypeByID("trigger", ButtonImage.class);
        triggerButton.setEnabled(isActive((ISettingsModuleView) settingsModuleView));
        triggerButton.setText(value ? ON : OFF); // [1.7.10] use key directly
        setHoverPane(key, triggerButton, settingsModuleView);
    }

    @Override
    public void trigger()
    {
        this.value = !this.value;
    }

    @Override
    public void copyValue(final ISetting<?> setting)
    {
        if (setting instanceof final BoolSetting other)
        {
            this.value = other.value;
        }
    }
}





