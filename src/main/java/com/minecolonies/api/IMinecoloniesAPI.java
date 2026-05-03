package com.minecolonies.api;

import com.minecolonies.api.client.render.modeltype.registry.IModelTypeRegistry;
import com.minecolonies.api.colony.ICitizenDataManager;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.registry.IBuildingDataManager;
import com.minecolonies.api.colony.colonyEvents.registry.ColonyEventDescriptionTypeRegistryEntry;
import com.minecolonies.api.colony.colonyEvents.registry.ColonyEventTypeRegistryEntry;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import com.minecolonies.api.colony.guardtype.GuardType;
import com.minecolonies.api.colony.guardtype.registry.IGuardTypeDataManager;
import com.minecolonies.api.colony.interactionhandling.registry.IInteractionResponseHandlerDataManager;
import com.minecolonies.api.colony.interactionhandling.registry.InteractionResponseHandlerEntry;
import com.minecolonies.api.colony.jobs.registry.IJobDataManager;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.compatibility.IFurnaceRecipes;
import com.minecolonies.api.configuration.Configuration;
import com.minecolonies.api.crafting.registry.CraftingType;
import com.minecolonies.api.crafting.registry.RecipeTypeEntry;
import com.minecolonies.api.entity.mobs.registry.IMobAIRegistry;
import com.minecolonies.api.entity.citizen.happiness.HappinessRegistry;
import com.minecolonies.api.entity.pathfinding.registry.IPathNavigateRegistry;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.eventbus.EventBus;
import com.minecolonies.api.quests.registries.QuestRegistries;
import com.minecolonies.api.registry.SimpleRegistry;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.ModResearchCosts.ResearchCostEntry;
import com.minecolonies.api.research.ModResearchEffects;
import com.minecolonies.api.research.ModResearchRequirements;

public interface IMinecoloniesAPI
{

    static IMinecoloniesAPI getInstance()
    {
        return MinecoloniesAPIProxy.getInstance();
    }

    IColonyManager getColonyManager();

    ICitizenDataManager getCitizenDataManager();

    IMobAIRegistry getMobAIRegistry();

    IPathNavigateRegistry getPathNavigateRegistry();

    IBuildingDataManager getBuildingDataManager();

    SimpleRegistry<BuildingEntry> getBuildingRegistry();

    SimpleRegistry<BuildingExtensionEntry> getBuildingExtensionRegistry();

    IJobDataManager getJobDataManager();

    SimpleRegistry<JobEntry> getJobRegistry();

    SimpleRegistry<InteractionResponseHandlerEntry> getInteractionResponseHandlerRegistry();

    IGuardTypeDataManager getGuardTypeDataManager();

    SimpleRegistry<GuardType> getGuardTypeRegistry();

    IModelTypeRegistry getModelTypeRegistry();

    Configuration getConfig();

    IFurnaceRecipes getFurnaceRecipes();

    IInteractionResponseHandlerDataManager getInteractionResponseHandlerDataManager();

    IGlobalResearchTree getGlobalResearchTree();

    SimpleRegistry<ModResearchRequirements.ResearchRequirementEntry> getResearchRequirementRegistry();

    SimpleRegistry<ModResearchEffects.ResearchEffectEntry> getResearchEffectRegistry();

    SimpleRegistry<ResearchCostEntry> getResearchCostRegistry();

    SimpleRegistry<ColonyEventTypeRegistryEntry> getColonyEventRegistry();

    SimpleRegistry<ColonyEventDescriptionTypeRegistryEntry> getColonyEventDescriptionRegistry();

    SimpleRegistry<RecipeTypeEntry> getRecipeTypeRegistry();

    SimpleRegistry<CraftingType> getCraftingTypeRegistry();

    SimpleRegistry<QuestRegistries.RewardEntry> getQuestRewardRegistry();

    SimpleRegistry<QuestRegistries.ObjectiveEntry> getQuestObjectiveRegistry();

    SimpleRegistry<QuestRegistries.TriggerEntry> getQuestTriggerRegistry();

    SimpleRegistry<QuestRegistries.DialogueAnswerEntry> getQuestDialogueAnswerRegistry();

    SimpleRegistry<HappinessRegistry.HappinessFactorTypeEntry> getHappinessTypeRegistry();

    SimpleRegistry<HappinessRegistry.HappinessFunctionEntry> getHappinessFunctionRegistry();

    SimpleRegistry<EquipmentTypeEntry> getEquipmentTypeRegistry();

    EventBus getEventBus();
}
