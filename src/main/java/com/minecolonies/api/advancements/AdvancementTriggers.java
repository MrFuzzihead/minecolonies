package com.minecolonies.api.advancements;

import com.minecolonies.api.advancements.all_towers.*;
import com.minecolonies.api.advancements.army_population.*;
import com.minecolonies.api.advancements.building_add_recipe.*;
import com.minecolonies.api.advancements.citizen_bury.*;
import com.minecolonies.api.advancements.citizen_eat_food.*;
import com.minecolonies.api.advancements.citizen_resurrect.*;
import com.minecolonies.api.advancements.click_gui_button.*;
import com.minecolonies.api.advancements.colony_population.*;
import com.minecolonies.api.advancements.complete_build_request.*;
import com.minecolonies.api.advancements.create_build_request.*;
import com.minecolonies.api.advancements.deep_mine.*;
import com.minecolonies.api.advancements.max_fields.*;
import com.minecolonies.api.advancements.open_gui_window.*;
import com.minecolonies.api.advancements.place_structure.*;
import com.minecolonies.api.advancements.place_supply.*;
import com.minecolonies.api.advancements.undertaker_totem.*;

// [1.7.10 BACKPORT] Advancements do not exist in Minecraft 1.7.10.
// This class is stubbed out. All trigger instances are no-op stubs;
// preInit() is a no-op (CriteriaTriggers.register() does not exist in 1.7.10).
//
public class AdvancementTriggers
{
    public static final PlaceSupplyTrigger          PLACE_SUPPLY           = new PlaceSupplyTrigger();
    public static final PlaceStructureTrigger       PLACE_STRUCTURE        = new PlaceStructureTrigger();
    public static final CreateBuildRequestTrigger   CREATE_BUILD_REQUEST   = new CreateBuildRequestTrigger();
    public static final OpenGuiWindowTrigger        OPEN_GUI_WINDOW        = new OpenGuiWindowTrigger();
    public static final ClickGuiButtonTrigger       CLICK_GUI_BUTTON       = new ClickGuiButtonTrigger();
    public static final CitizenEatFoodTrigger       CITIZEN_EAT_FOOD       = new CitizenEatFoodTrigger();
    public static final BuildingAddRecipeTrigger    BUILDING_ADD_RECIPE    = new BuildingAddRecipeTrigger();
    public static final CompleteBuildRequestTrigger COMPLETE_BUILD_REQUEST = new CompleteBuildRequestTrigger();
    public static final ColonyPopulationTrigger     COLONY_POPULATION      = new ColonyPopulationTrigger();
    public static final ArmyPopulationTrigger       ARMY_POPULATION        = new ArmyPopulationTrigger();
    public static final MaxFieldsTrigger            MAX_FIELDS             = new MaxFieldsTrigger();
    public static final DeepMineTrigger             DEEP_MINE              = new DeepMineTrigger();
    public static final AllTowersTrigger            ALL_TOWERS             = new AllTowersTrigger();
    public static final CitizenBuryTrigger          CITIZEN_BURY           = new CitizenBuryTrigger();
    public static final CitizenResurrectTrigger     CITIZEN_RESURRECT      = new CitizenResurrectTrigger();
    public static final UndertakerTotemTrigger      UNDERTAKER_TOTEM       = new UndertakerTotemTrigger();

    /**
     * No-op in 1.7.10 — CriteriaTriggers.register() does not exist.
     */
    public static void preInit()
    {
        // [1.7.10 BACKPORT] In 1.21 this called CriteriaTriggers.register() for each trigger.
        // Nothing to do here; advancements are not supported in 1.7.10.
    }
}
