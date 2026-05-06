package com.minecolonies.core.colony.buildings.modules.settings;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

// [1.7.10] blockui replaced by ModularUI2
import com.ldtteam.structurize.client.gui.WindowSelectRes;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.modules.ICommonSettingsModule;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
// [1.7.10] EntityBlock, FallingBlock removed - no equivalent in 1.7.10
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
// [1.7.10] World.material removed
// [1.7.10] World.material removed
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.constant.WindowConstants.SWITCH;

/**
 * Stores a solid block setting.
 */
public class BlockSetting implements ISetting<ItemBlock>
{
    /**
     * Default value of the setting.
     */
    private final ItemBlock defaultValue;

    /**
     * The value of the setting.
     */
    private ItemBlock value;

    /**
     * Create a new boolean setting.
     *
     * @param init the initial value.
     */
    public BlockSetting(final ItemBlock init)
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
    public BlockSetting(final ItemBlock value, final ItemBlock def)
    {
        this.value = value;
        this.defaultValue = def;
    }

    /**
     * Get the setting value.
     *
     * @return the set value.
     */
    public ItemBlock getValue()
    {
        return value;
    }

    /**
     * Get the default value.
     *
     * @return the default value.
     */
    public ItemBlock getDefault()
    {
        return defaultValue;
    }

    /**
     * Set a new block value.
     *
     * @param value the item block to set.
     */
    public void setValue(final ItemBlock value)
    {
        this.value = value;
    }

    @Override
    public ResourceLocation getLayoutItem()
    {
        return new ResourceLocation("minecolonies:gui/layouthuts/layoutblocksetting.xml");
    }

    @Override
    public void setupHandler(
      final ISettingKey<?> key,
      final Object pane,
      final ICommonSettingsModule settingsModuleView,
      final IBuildingView building,
      final Object /* BOWindow: todo ModularUI2 */ window)
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
        if (setting instanceof final BlockSetting other)
        {
            setValue(other.getValue());
        }
    }
}
