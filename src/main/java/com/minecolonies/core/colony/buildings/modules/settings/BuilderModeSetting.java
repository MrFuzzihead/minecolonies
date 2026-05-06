package com.minecolonies.core.colony.buildings.modules.settings;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.placement.StructureIterators;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingsModuleView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBuilder;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static com.minecolonies.api.research.util.ResearchConstants.BUILDER_MODE;

/**
 * Stores the builder mode setting.
 */
public class BuilderModeSetting extends StringSetting
{
    /**
     * Reason display constants.
     */
    public static final String NEEDS_RESEARCH_REASON  = "com.minecolonies.coremod.settings.reason.needsresearch";
    public static final String BUILDER_MODES_RESEARCH = "com.minecolonies.research.technology.buildermodes.name";

    /**
     * Create the builder mode setting.
     */
    public BuilderModeSetting()
    {
        // [1.7.10] StructureIterators.getKeySet() not available; use empty list stub
        super(java.util.Collections.emptyList(), 0);
        set(Structurize.getConfig().getServer().iteratorType); // [1.7.10] iteratorType is a String field
    }

    /**
     * Create the builder mode setting.
     *
     * @param value the list of possible settings.
     * @param curr  the current setting.
     */
    public BuilderModeSetting(final List<String> value, final int curr)
    {
        // [1.7.10] StructureIterators.getKeySet() not available; use empty list stub
        super(java.util.Collections.emptyList(), 0);
        set(value.isEmpty() ? Structurize.getConfig().getServer().iteratorType : value.get(curr));
    }

    @NotNull
    public static String getActualValue(@NotNull final IBuilding building)
    {
        return building.getSettingValueOrDefault(BuildingBuilder.BUILDING_MODE, Structurize.getConfig().getServer().iteratorType); // [1.7.10] iteratorType is String
    }

    @Override
    protected String getDisplayText()
    {
        // [1.7.10] String.translatable → StatCollector.translateToLocal
        return net.minecraft.util.StatCollector.translateToLocal("com.ldtteam.structurize.iterators." + getSettings().get(getCurrentIndex()));
    }

    @Override
    public String getToolTipText()
    {
        return net.minecraft.util.StatCollector.translateToLocal("com.ldtteam.structurize.iterators." + getSettings().get(getCurrentIndex()) + ".tooltip");
    }

    @Override
    public boolean isActive(final ISettingsModule module)
    {
        return module.getBuilding().getColony().getResearchManager().getResearchEffects().getEffectStrength(BUILDER_MODE) > 0;
    }

    @Override
    public boolean isActive(final ISettingsModuleView module)
    {
        return module.getColony().getResearchManager().getResearchEffects().getEffectStrength(BUILDER_MODE) > 0;
    }

    @Override
    public @Nullable String getInactiveReason()
    {
        return net.minecraft.util.StatCollector.translateToLocalFormatted(NEEDS_RESEARCH_REASON,
          new Object[]{net.minecraft.util.StatCollector.translateToLocal(BUILDER_MODES_RESEARCH)}); // [1.7.10] String.translatable→StatCollector
    }
}
