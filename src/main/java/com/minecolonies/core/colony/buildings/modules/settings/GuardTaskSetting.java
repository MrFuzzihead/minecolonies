package com.minecolonies.core.colony.buildings.modules.settings;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.modules.ICommonSettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

import static com.minecolonies.core.colony.buildings.AbstractBuildingGuards.PATROL_MODE;
import static com.minecolonies.core.colony.buildings.modules.settings.GuardPatrolModeSetting.MANUAL;

/**
 * Stores a guard task setting.
 */
public class GuardTaskSetting extends StringSettingWithDesc
{
    /**
     * Different setting possibilities.
     */
    public static final String PATROL      = "com.minecolonies.core.guard.setting.patrol";
    public static final String GUARD       = "com.minecolonies.core.guard.setting.guard";
    public static final String FOLLOW      = "com.minecolonies.core.guard.setting.follow";
    public static final String PATROL_MINE = "com.minecolonies.core.guard.setting.patrol_mine";

    /**
     * Different trigger button widths.
     */
    private static final int SET_POS_BUTTON_WIDTH = 60;
    private static final int HELP_BUTTON_WIDTH    = 125;

    /**
     * Create a new guard task list setting.
     */
    public GuardTaskSetting()
    {
        super(PATROL, GUARD, FOLLOW, PATROL_MINE);
    }

    /**
     * Create a new guard task list setting.
     */
    public GuardTaskSetting(final String...list)
    {
        super(list);
    }

    /**
     * Create a new string list setting.
     * @param settings the overall list of settings.
     * @param currentIndex the current selected index.
     */
    public GuardTaskSetting(final List<String> settings, final int currentIndex)
    {
        super(settings, currentIndex);
    }

    @Override
    public ResourceLocation getLayoutItem()
    {
        return new ResourceLocation("minecolonies:gui/layouthuts/layoutguardtasksetting.xml");
    }

    @Override
    public void onUpdate(final IBuilding building, final EntityPlayerMP sender)
    {
        if (building instanceof AbstractBuildingGuards guardBuilding && getValue().equals(FOLLOW))
        {
            guardBuilding.setPlayerToFollow(sender);
        }
    }

    @Override
    public void setupHandler(final ISettingKey<?> key, final Object pane, final ICommonSettingsModule settingsModuleView, final IBuildingView building, final Object /* BOWindow: todo ModularUI2 */ window)
    {
        // [1.7.10] todo: ModularUI2 port
        super.setupHandler(key, pane, settingsModuleView, building, window);
    }

    @Override
    public void render(final ISettingKey<?> key, final Object pane, final ICommonSettingsModule settingsModuleView, final IBuildingView building, final Object /* BOWindow: todo ModularUI2 */ window)
    {
        // [1.7.10] todo: ModularUI2 port
        super.render(key, pane, settingsModuleView, building, window);
    }

    @Override
    protected int getButtonWidth(final ISettingsModuleView settingsModuleView)
    {
        return switch (getValue())
        {
            case PATROL ->
            {
                final String patrolMode = settingsModuleView.getSetting(PATROL_MODE).getValue();
                yield patrolMode.equals(MANUAL) ? SET_POS_BUTTON_WIDTH : MAX_BUTTON_WIDTH;
            }
            case GUARD -> SET_POS_BUTTON_WIDTH;
            case PATROL_MINE -> HELP_BUTTON_WIDTH;
            default -> MAX_BUTTON_WIDTH;
        };
    }

    /**
     * Set the correct text on the patrol mine help button.
     *
     * @param button   the button instance.
     * @param building the building.
     */
    // [1.7.10] todo: ModularUI2 port - setPatrolMineHelpLabel removed (uses ButtonImage)
}




