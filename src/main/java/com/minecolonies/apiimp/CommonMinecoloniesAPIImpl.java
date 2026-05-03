package com.minecolonies.apiimp;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.client.render.modeltype.registry.IModelTypeRegistry;
import com.minecolonies.api.colony.ICitizenDataManager;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.registry.IBuildingDataManager;
import com.minecolonies.api.colony.colonyEvents.registry.ColonyEventDescriptionTypeRegistryEntry;
import com.minecolonies.api.colony.colonyEvents.registry.ColonyEventTypeRegistryEntry;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import com.minecolonies.api.colony.guardtype.GuardType;
import com.minecolonies.api.colony.guardtype.registry.IGuardTypeDataManager;
import com.minecolonies.api.colony.guardtype.registry.ModGuardTypes;
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
import com.minecolonies.api.eventbus.DefaultEventBus;
import com.minecolonies.api.eventbus.EventBus;
import com.minecolonies.api.quests.registries.QuestRegistries;
import com.minecolonies.api.registry.SimpleRegistry;
import com.minecolonies.api.research.IGlobalResearchTree;
import com.minecolonies.api.research.ModResearchCosts.ResearchCostEntry;
import com.minecolonies.api.research.ModResearchEffects;
import com.minecolonies.api.research.ModResearchRequirements;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.colony.CitizenDataManager;
import com.minecolonies.core.colony.ColonyManager;
import com.minecolonies.core.colony.buildings.registry.BuildingDataManager;
import com.minecolonies.core.colony.interactionhandling.registry.InteractionResponseHandlerManager;
import com.minecolonies.core.colony.jobs.registry.JobDataManager;
import com.minecolonies.core.entity.mobs.registry.MobAIRegistry;
import com.minecolonies.core.entity.pathfinding.registry.PathNavigateRegistry;
import com.minecolonies.core.research.GlobalResearchTree;
import com.minecolonies.core.util.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
// [1.7.10 BACKPORT] IForgeRegistry, NewRegistryEvent and RegistryBuilder removed.
//   All mod-internal registries now use SimpleRegistry<T>.
import org.jetbrains.annotations.NotNull;

public class CommonMinecoloniesAPIImpl implements IMinecoloniesAPI
{
    private final  IColonyManager                                                      colonyManager          = new ColonyManager();
    private final  ICitizenDataManager                                                 citizenDataManager     = new CitizenDataManager();
    private final  IMobAIRegistry                                                      mobAIRegistry          = new MobAIRegistry();
    private final  IPathNavigateRegistry                                               pathNavigateRegistry   = new PathNavigateRegistry();
    private final  SimpleRegistry<EquipmentTypeEntry>                                  equipmentTypeRegistry  = new SimpleRegistry<>();
    private final  SimpleRegistry<BuildingEntry>                                       buildingRegistry       = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<BuildingExtensionEntry>                              buildingExtensionRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  IBuildingDataManager                                                buildingDataManager    = new BuildingDataManager();
    private final  IJobDataManager                                                     jobDataManager         = new JobDataManager();
    private final  IGuardTypeDataManager                                               guardTypeDataManager   = new com.minecolonies.core.colony.buildings.registry.GuardTypeDataManager();
    private final  SimpleRegistry<JobEntry>                                            jobRegistry            = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<GuardType>                                           guardTypeRegistry      = new SimpleRegistry<>(ModGuardTypes.KNIGHT_ID);
    private final  SimpleRegistry<InteractionResponseHandlerEntry>                     interactionHandlerRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  IInteractionResponseHandlerDataManager                              interactionDataManager = new InteractionResponseHandlerManager();
    private final  SimpleRegistry<ColonyEventTypeRegistryEntry>                        colonyEventRegistry    = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<ColonyEventDescriptionTypeRegistryEntry>             colonyEventDescriptionRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private static IGlobalResearchTree                                                 globalResearchTree     = new GlobalResearchTree();
    private final  SimpleRegistry<ModResearchRequirements.ResearchRequirementEntry>   researchRequirementRegistry = new SimpleRegistry<>();
    private final  SimpleRegistry<ModResearchEffects.ResearchEffectEntry>             researchEffectRegistry = new SimpleRegistry<>();
    private final  SimpleRegistry<ResearchCostEntry>                                  researchCostRegistry   = new SimpleRegistry<>();
    private final  SimpleRegistry<RecipeTypeEntry>                                    recipeTypeEntryRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "classic"));
    private final  SimpleRegistry<CraftingType>                                       craftingTypeRegistry   = new SimpleRegistry<>();
    private final  SimpleRegistry<QuestRegistries.ObjectiveEntry>                     questObjectiveRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<QuestRegistries.RewardEntry>                        questRewardRegistry    = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<QuestRegistries.TriggerEntry>                       questTriggerRegistry   = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<QuestRegistries.DialogueAnswerEntry>                questDialogueAnswerRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<HappinessRegistry.HappinessFactorTypeEntry>         happinessFactorTypeRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));
    private final  SimpleRegistry<HappinessRegistry.HappinessFunctionEntry>           happinessFunctionRegistry = new SimpleRegistry<>(new ResourceLocation(Constants.MOD_ID, "null"));

    private EventBus eventBus = new DefaultEventBus();

    @Override
    @NotNull
    public IColonyManager getColonyManager()
    {
        return colonyManager;
    }

    @Override
    @NotNull
    public ICitizenDataManager getCitizenDataManager()
    {
        return citizenDataManager;
    }

    @Override
    @NotNull
    public IMobAIRegistry getMobAIRegistry()
    {
        return mobAIRegistry;
    }

    @Override
    @NotNull
    public IPathNavigateRegistry getPathNavigateRegistry()
    {
        return pathNavigateRegistry;
    }

    @Override
    @NotNull
    public IBuildingDataManager getBuildingDataManager()
    {
        return buildingDataManager;
    }

    @Override
    @NotNull
    public SimpleRegistry<BuildingEntry> getBuildingRegistry()
    {
        return buildingRegistry;
    }

    @Override
    @NotNull
    public SimpleRegistry<BuildingExtensionEntry> getBuildingExtensionRegistry()
    {
        return buildingExtensionRegistry;
    }

    @Override
    public IJobDataManager getJobDataManager()
    {
        return jobDataManager;
    }

    @Override
    public SimpleRegistry<JobEntry> getJobRegistry()
    {
        return jobRegistry;
    }

    @Override
    public SimpleRegistry<InteractionResponseHandlerEntry> getInteractionResponseHandlerRegistry()
    {
        return interactionHandlerRegistry;
    }

    @Override
    public IGuardTypeDataManager getGuardTypeDataManager()
    {
        return guardTypeDataManager;
    }

    @Override
    public SimpleRegistry<GuardType> getGuardTypeRegistry()
    {
        return guardTypeRegistry;
    }

    @Override
    public IModelTypeRegistry getModelTypeRegistry()
    {
        return null;
    }

    @Override
    public Configuration getConfig()
    {
        return MineColonies.getConfig();
    }

    @Override
    public IFurnaceRecipes getFurnaceRecipes()
    {
        return FurnaceRecipes.getInstance();
    }

    @Override
    public IInteractionResponseHandlerDataManager getInteractionResponseHandlerDataManager()
    {
        return interactionDataManager;
    }

    @Override
    public IGlobalResearchTree getGlobalResearchTree()
    {
        return globalResearchTree;
    }

    @Override
    public SimpleRegistry<ModResearchRequirements.ResearchRequirementEntry> getResearchRequirementRegistry() { return researchRequirementRegistry; }

    @Override
    public SimpleRegistry<ModResearchEffects.ResearchEffectEntry> getResearchEffectRegistry() { return researchEffectRegistry; }

    @Override
    public SimpleRegistry<ResearchCostEntry> getResearchCostRegistry()
    {
        return researchCostRegistry;
    }

    // [1.7.10 BACKPORT] onRegistryNewRegistry(NewRegistryEvent) removed.
    // Registries are final fields and initialised at construction time.

    @Override
    public SimpleRegistry<ColonyEventTypeRegistryEntry> getColonyEventRegistry()
    {
        return colonyEventRegistry;
    }

    @Override
    public SimpleRegistry<ColonyEventDescriptionTypeRegistryEntry> getColonyEventDescriptionRegistry()
    {
        return colonyEventDescriptionRegistry;
    }

    @Override
    public SimpleRegistry<RecipeTypeEntry> getRecipeTypeRegistry()
    {
        return recipeTypeEntryRegistry;
    }

    @Override
    public SimpleRegistry<CraftingType> getCraftingTypeRegistry()
    {
        return craftingTypeRegistry;
    }

    @Override
    public SimpleRegistry<QuestRegistries.RewardEntry> getQuestRewardRegistry()
    {
        return questRewardRegistry;
    }

    @Override
    public SimpleRegistry<QuestRegistries.ObjectiveEntry> getQuestObjectiveRegistry()
    {
        return questObjectiveRegistry;
    }

    @Override
    public SimpleRegistry<QuestRegistries.TriggerEntry> getQuestTriggerRegistry()
    {
        return questTriggerRegistry;
    }

    @Override
    public SimpleRegistry<QuestRegistries.DialogueAnswerEntry> getQuestDialogueAnswerRegistry()
    {
        return questDialogueAnswerRegistry;
    }

    @Override
    public SimpleRegistry<HappinessRegistry.HappinessFactorTypeEntry> getHappinessTypeRegistry()
    {
        return happinessFactorTypeRegistry;
    }

    @Override
    public SimpleRegistry<HappinessRegistry.HappinessFunctionEntry> getHappinessFunctionRegistry()
    {
        return happinessFunctionRegistry;
    }

    @Override
    public SimpleRegistry<EquipmentTypeEntry> getEquipmentTypeRegistry()
    {
        return equipmentTypeRegistry;
    }

    @Override
    public EventBus getEventBus()
    {
        return eventBus;
    }
}
