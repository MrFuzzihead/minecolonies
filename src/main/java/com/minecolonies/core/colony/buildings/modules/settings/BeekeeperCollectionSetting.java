package com.minecolonies.core.colony.buildings.modules.settings;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.buildings.modules.ICommonSettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBeekeeper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import static com.minecolonies.api.research.util.ResearchConstants.BEEKEEP_2;

/**
 * Stores the beekeeper collection setting.
 */
public class BeekeeperCollectionSetting extends StringSetting
{
    /**
     * Cached research.
     */
    private boolean hasResearch;

    /**
     * Create a new string list setting.
     *
     * @param settings the overall list of settings.
     */
    public BeekeeperCollectionSetting(final String... settings)
    {
        super(settings);
    }

    /**
     * Create a new string list setting.
     *
     * @param settings     the overall list of settings.
     * @param currentIndex the current selected index.
     */
    public BeekeeperCollectionSetting(final List<String> settings, final int currentIndex)
    {
        super(settings, currentIndex);
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
        hasResearch = building.getColony().getResearchManager().getResearchEffects().getEffectStrength(BEEKEEP_2) > 0;
    }

    @Override
    public boolean isIndexAllowed(final int index)
    {
        return super.isIndexAllowed(index) && (hasResearch || !getSettings().get(index).equals(BuildingBeekeeper.BOTH));
    }
}



