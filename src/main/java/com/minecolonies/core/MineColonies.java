package com.minecolonies.core;

// [1.7.10 BACKPORT] Structurize imports may differ depending on the 1.7.10-compatible jar.
// TODO: verify package names once the Structurize 1.7.10 jar API is confirmed.
import com.ldtteam.structurize.storage.SurvivalBlueprintHandlers;
import com.ldtteam.structurize.util.LanguageHandler;
import com.minecolonies.api.MinecoloniesAPIProxy;
// [1.7.10 BACKPORT] AdvancementTriggers do not exist in 1.7.10 — commented out.
// import com.minecolonies.api.advancements.AdvancementTriggers;
import com.minecolonies.api.configuration.Configuration;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.apiimp.CommonMinecoloniesAPIImpl;
import com.minecolonies.apiimp.initializer.*;
import com.minecolonies.core.colony.requestsystem.init.RequestSystemInitializer;
import com.minecolonies.core.colony.requestsystem.init.StandardFactoryControllerInitializer;
import com.minecolonies.core.event.EventHandler;
import com.minecolonies.core.event.FMLEventHandler;
import com.minecolonies.core.placementhandlers.PlacementHandlerInitializer;
import com.minecolonies.core.placementhandlers.main.SuppliesHandler;
import com.minecolonies.core.placementhandlers.main.SurvivalHandler;

// [1.7.10 BACKPORT] 1.7.10 FML / Forge imports
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

// [1.7.10 BACKPORT] Removed 1.21-only imports:
//   com.ldtteam.structurize.util.TagManager             — may not exist in 1.7.10 Structurize
//   com.minecolonies.api.advancements.AdvancementTriggers — Advancements don't exist in 1.7.10
//   com.minecolonies.api.colony.IChunkmanagerCapability  — replaced by ChunkAPI
//   com.minecolonies.api.colony.IColonyTagCapability     — replaced by ChunkAPI
//   com.minecolonies.api.crafting.CountedIngredient      — Recipe serializers don't exist in 1.7.10
//   com.minecolonies.api.creativetab.ModCreativeTabs     — ported separately (DeferredRegister removed)
//   com.minecolonies.api.enchants.ModEnchants            — ported separately
//   com.minecolonies.api.entity.ModEntities              — ported separately
//   com.minecolonies.api.entity.citizen.*                — ported separately
//   com.minecolonies.api.entity.mobs.*                   — ported separately
//   com.minecolonies.api.items.ModBannerPatterns         — Banner patterns don't exist in 1.7.10
//   com.minecolonies.api.items.ModTags                   — Tags don't exist in 1.7.10
//   com.minecolonies.api.equipment.ModEquipmentTypes     — ported separately
//   com.minecolonies.api.loot.ModLootConditions          — Loot tables don't exist in 1.7.10
//   com.minecolonies.api.sounds.ModSoundEvents           — ported separately
//   com.minecolonies.api.util.constant.SchematicTagConstants — TODO: Structurize compat
//   com.minecolonies.apiimp.ClientMinecoloniesAPIImpl    — moved to client proxy
//   com.minecolonies.core.blocks.BlockDecorationController — NBTBase registration removed
//   com.minecolonies.core.blocks.BlockPlantationField    — NBTBase registration removed
//   com.minecolonies.core.blocks.huts.*                  — NBTBase registration removed
//   com.minecolonies.core.colony.IColonyManagerCapability — replaced by WorldSavedData
//   com.minecolonies.core.commands.arguments.ModArgumentTypes — no 1.7.10 equivalent
//   com.minecolonies.core.entity.mobs.EntityMercenary   — ported separately
//   com.minecolonies.core.event.*                        — reduced set
//   com.minecolonies.core.loot.SupplyLoot               — Loot doesn't exist in 1.7.10
//   com.minecolonies.core.recipes.*                      — ported separately
//   com.minecolonies.core.structures.MineColoniesStructures — Jigsaw structures don't exist in 1.7.10
//   net.minecraft.world.entity.animal.horse.Horse        — ported separately
//   net.minecraftforge.api.distmarker.*                  — replaced by @SideOnly / sided proxies
//   net.minecraftforge.common.MinecraftForge             — still exists in 1.7.10
//   net.minecraftforge.common.capabilities.*             — replaced by ChunkAPI / WorldSavedData
//   net.minecraftforge.common.crafting.CraftingHelper    — replaced by GameRegistry.addRecipe()
//   net.minecraftforge.event.TagsUpdatedEvent            — Tags don't exist in 1.7.10
//   net.minecraftforge.event.entity.EntityAttributeCreationEvent — not in 1.7.10
//   net.minecraftforge.eventbus.api.*                    — replaced by cpw.mods.fml.common.event.*
//   net.minecraftforge.fml.DistExecutor                  — replaced by @SideOnly / proxy
//   net.minecraftforge.fml.ModList                       — replaced by Loader.isModLoaded()
//   net.minecraftforge.fml.common.Mod                    — still exists but different API
//   net.minecraftforge.fml.event.lifecycle.*             — replaced by cpw.mods.fml.common.event.*
//   net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext — replaced by direct @Mod.EventHandler
//   net.minecraftforge.registries.*                      — replaced by GameRegistry

/**
 * MineColonies mod entry point.
 *
 * <p><b>1.7.10 Backport Notes:</b>
 * <ul>
 *   <li>The 1.21 constructor-time {@code DeferredRegister.register()} pattern is replaced
 *       by explicit calls inside {@code preInit()} / {@code init()} / {@code postInit()}.</li>
 *   <li>Capabilities are replaced by ChunkAPI (chunk data) and {@code WorldSavedData}
 *       (world-World data). See {@code ColonyManagerWorldSavedData}.</li>
 *   <li>Client-only code is isolated in {@code ClientProxy} rather than using
 *       {@code DistExecutor} / {@code @OnlyIn(Dist.CLIENT)}.</li>
 *   <li>The FML event bus uses {@code @Mod.EventHandler} on methods in this class, and
 *       {@code MinecraftForge.EVENT_BUS.register()} for Forge game events.</li>
 *   <li>Advancements, loot tables, tags, jigsaw structures, banner patterns, data generators,
 *       and command argument types are all commented out — they have no 1.7.10 equivalent.</li>
 * </ul>
 */
@Mod(modid = Constants.MOD_ID,
     name = "MineColonies",
     version = com.minecolonies.Tags.VERSION,
     // TODO: [1.7.10 BACKPORT] Update dependencies string to reference 1.7.10-compatible versions.
     dependencies = "required-after:structurize;required-after:endlessids;required-after:chunkapi")
public class MineColonies
{
    /**
     * Static mod instance, populated by FML via {@code @Mod.Instance}.
     */
    @Mod.Instance(Constants.MOD_ID)
    public static MineColonies instance;

    // [1.7.10 BACKPORT] In 1.21, capabilities were declared as static Capability<T> fields:
    //   public static final Capability<IChunkmanagerCapability> CHUNK_STORAGE_UPDATE_CAP = ...
    //   public static final Capability<IColonyManagerCapability> COLONY_MANAGER_CAP = ...
    // These are replaced by:
    //   - ChunkAPI IChunkDataHandler for per-chunk data (IChunkmanagerCapability, IColonyTagCapability)
    //   - ColonyManagerWorldSavedData (a WorldSavedData subclass) for world-World data
    // TODO: [1.7.10 BACKPORT] Create ColonyManagerWorldSavedData and ChunkAPI handler classes.

    /**
     * The config instance — loaded during {@link #preInit(FMLPreInitializationEvent)}.
     */
    private static Configuration config;

    // [1.7.10 BACKPORT] Sided proxy replaces DistExecutor / Dist.CLIENT / Dist.DEDICATED_SERVER.
    // The server proxy is CommonMinecoloniesAPIImpl; the client proxy is ClientMinecoloniesAPIImpl.
    // TODO: [1.7.10 BACKPORT] Uncomment and configure the @SidedProxy once proxy classes compile.
    // @SidedProxy(
    //     clientSide = "com.minecolonies.apiimp.ClientMinecoloniesAPIImpl",
    //     serverSide = "com.minecolonies.apiimp.CommonMinecoloniesAPIImpl")
    // public static IMinecoloniesAPIImpl proxy;

    // -----------------------------------------------------------------------
    // FML Lifecycle — preInit
    // -----------------------------------------------------------------------

    /**
     * Called during {@code FMLPreInitializationEvent} — registers blocks, items, tile entities,
     * entities, sounds, and other pre-init resources.
     *
     * <p>In 1.21, most of this was done via {@code DeferredRegister} inside the
     * constructor. In 1.7.10 we register everything explicitly here.</p>
     *
     * @param event the FML pre-init event.
     */
    @Mod.EventHandler
    public void preInit(@NotNull final FMLPreInitializationEvent event)
    {
        // Load configuration file.
        // TODO: [1.7.10 BACKPORT] Port Configuration to use Forge's net.minecraftforge.common.config.Configuration.
        config = new Configuration(event);

        // Language / localisation.
        LanguageHandler.loadLangPath("assets/minecolonies/lang/%s.json");

        // Register Forge and FML event handlers.
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new FMLEventHandler());
        // TODO: [1.7.10 BACKPORT] Register client-side event handlers via sided proxy.
        // proxy.registerClientEventHandlers();

        // Blocks and their ItemBlocks.
        ModBlocksInitializer.init();

        // Items.
        // TODO: [1.7.10 BACKPORT] Port ModItemsInitializer to GameRegistry.registerItem().
        // ModItemsInitializer.init();

        // TileEntities.
        // TODO: [1.7.10 BACKPORT] Port TileEntityInitializer to GameRegistry.registerTileEntity().
        // TileEntityInitializer.init();

        // Entities.
        // TODO: [1.7.10 BACKPORT] Port EntityInitializer to EntityRegistry.registerModEntity().
        // EntityInitializer.init();

        // Equipment types (custom registry).
        // TODO: [1.7.10 BACKPORT] Port ModEquipmentTypes — DeferredRegister removed.
        // ModEquipmentTypesInitializer.init();

        // Enchantments.
        // TODO: [1.7.10 BACKPORT] Port ModEnchants — register via EnchantmentManager or GameRegistry.
        // ModEnchantInitializer.init();

        // Sounds.
        // TODO: [1.7.10 BACKPORT] Port ModSoundEvents — register via ResourceLocation; no SoundEvent registry in 1.7.10.
        // ModSoundEvents.init();

        // Creative tab.
        // TODO: [1.7.10 BACKPORT] Port ModCreativeTabs — instantiate CreativeTabs directly.
        // ModCreativeTabs.init();

        // ChunkAPI registration for colony chunk data.
        // TODO: [1.7.10 BACKPORT] Register IChunkDataHandler via ChunkAPI.
        // ChunkAPI.registerDataHandler(new ColonyChunkDataHandler());

        // Structurize blueprint handlers.
        SurvivalBlueprintHandlers.registerHandler(new SurvivalHandler());
        SurvivalBlueprintHandlers.registerHandler(new SuppliesHandler());

        // Request system factory controllers.
        StandardFactoryControllerInitializer.onPreInit();

        // API instance — server-side (common) implementation.
        // TODO: [1.7.10 BACKPORT] Replace with sided proxy.
        MinecoloniesAPIProxy.getInstance().setApiInstance(new CommonMinecoloniesAPIImpl());

        // [1.7.10 BACKPORT] Commented-out 1.21 registrations with no 1.7.10 equivalent:
        //   ModEquipmentTypes.DEFERRED_REGISTER.register(...)
        //   TileEntityInitializer.BLOCK_ENTITIES.register(...)
        //   ModEnchants.ENCHANTMENTS.register(...)
        //   ModContainerInitializers.CONTAINERS.register(...)
        //   ModBuildingsInitializer.DEFERRED_REGISTER.register(...)
        //   ModBuildingExtensionsInitializer.DEFERRED_REGISTER.register(...)
        //   ModGuardTypesInitializer.DEFERRED_REGISTER.register(...)
        //   ModColonyEventDescriptionTypeInitializer.DEFERRED_REGISTER.register(...)
        //   ModResearchRequirementInitializer.DEFERRED_REGISTER.register(...)
        //   ModRecipeSerializerInitializer.RECIPE_SERIALIZER.register(...)
        //   ModRecipeSerializerInitializer.RECIPE_TYPES.register(...)
        //   ModColonyEventTypeInitializer.DEFERRED_REGISTER.register(...)
        //   ModCraftingTypesInitializer.DEFERRED_REGISTER.register(...)
        //   ModJobsInitializer.DEFERRED_REGISTER.register(...)
        //   ModRecipeTypesInitializer.DEFERRED_REGISTER.register(...)
        //   RaiderMobUtils.ATTRIBUTES.register(...)
        //   ModSoundEvents.SOUND_EVENTS.register(...)
        //   ModInteractionsInitializer.DEFERRED_REGISTER.register(...)
        //   ModResearchEffectInitializer.DEFERRED_REGISTER.register(...)
        //   ModResearchCostInitializer.DEFERRED_REGISTER.register(...)
        //   ModLootConditions.DEFERRED_REGISTER.register(...)     — Loot: no 1.7.10 equivalent
        //   SupplyLoot.GLM.register(...)                          — Loot: no 1.7.10 equivalent
        //   ModBannerPatterns.BANNER_PATTERNS.register(...)       — Banner patterns: no 1.7.10 equivalent
        //   ModArgumentTypes.ARGUMENT_TYPES.register(...)         — Commands: different in 1.7.10
        //   ModQuestInitializer.DEFERRED_REGISTER_*.register(...) — Quests: needs port
        //   ModHappinessFactorTypeInitializer.*.register(...)     — Needs port
        //   ModCreativeTabs.TAB_REG.register(...)                 — Different in 1.7.10
        //   Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(DataPackSyncEventHandler.*)
        //   Mod.EventBusSubscriber.Bus.MOD.bus().get().addListener(GatherDataHandler::*)
        //   Mod.EventBusSubscriber.Bus.MOD.bus().get().register(ClientRegistryHandler.class)
        //   MineColoniesStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus)  — Jigsaw
        //   TagManager.registerGlobalTagOption(*)                                      — Tags
        //   TagManager.registerSpecificTagOption(*)                                    — Tags

        logIncompatibilities();
    }

    // -----------------------------------------------------------------------
    // FML Lifecycle — init
    // -----------------------------------------------------------------------

    /**
     * Called during {@code FMLInitializationEvent} — registers network messages, recipes,
     * and other init-phase resources.
     *
     * <p>In 1.21, this mapped to {@code FMLCommonSetupEvent}.</p>
     *
     * @param event the FML init event.
     */
    @Mod.EventHandler
    public void init(@NotNull final FMLInitializationEvent event)
    {
        // Network messages.
        Network.getNetwork().registerCommonMessages();

        // Interaction validators.
        // TODO: [1.7.10 BACKPORT] Port InteractionValidatorInitializer.
        // InteractionValidatorInitializer.init();

        // Recipes.
        // TODO: [1.7.10 BACKPORT] Port custom ingredient serializers to GameRegistry.addRecipe().
        // CraftingHelper.register(CountedIngredient.ID, CountedIngredient.Serializer.getInstance());
        // CraftingHelper.register(FoodIngredient.ID, FoodIngredient.Serializer.getInstance());
        // CraftingHelper.register(PlantIngredient.ID, PlantIngredient.Serializer.getInstance());

        // Loot conditions — no equivalent in 1.7.10; commented out.
        // ModLootConditions.init();

        // Tags — no equivalent in 1.7.10; commented out.
        // ModTags.init();

        // [1.7.10 BACKPORT] AdvancementTriggers.preInit() removed — Advancements don't exist.
        // AdvancementTriggers.preInit();
    }

    // -----------------------------------------------------------------------
    // FML Lifecycle — postInit
    // -----------------------------------------------------------------------

    /**
     * Called during {@code FMLPostInitializationEvent} — runs final initialisation
     * after all mods have completed their init phase.
     *
     * <p>In 1.21, this mapped to {@code FMLLoadCompleteEvent}.</p>
     *
     * @param event the FML post-init event.
     */
    @Mod.EventHandler
    public void postInit(@NotNull final FMLPostInitializationEvent event)
    {
        PlacementHandlerInitializer.initHandlers();
        RequestSystemInitializer.onPostInit();
    }

    // -----------------------------------------------------------------------
    // FML Lifecycle — serverStarting
    // -----------------------------------------------------------------------

    /**
     * Called when the server is starting — registers commands.
     *
     * @param event the FML server-starting event.
     */
    @Mod.EventHandler
    public void serverStarting(@NotNull final FMLServerStartingEvent event)
    {
        // TODO: [1.7.10 BACKPORT] Port commands from Brigadier (1.21) to
        //   ICommand / ServerCommandManager (1.7.10).
        // event.registerServerCommand(new CommandMineColonies());
    }

    // -----------------------------------------------------------------------
    // Capabilities — replaced by ChunkAPI / WorldSavedData
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 capabilities were registered in RegisterCapabilitiesEvent:
    //   @SubscribeEvent
    //   public static void registerCaps(RegisterCapabilitiesEvent event) {
    //       event.register(IColonyTagCapability.class);
    //       event.register(IChunkmanagerCapability.class);
    //       event.register(IColonyManagerCapability.class);
    //   }
    // Replacements:
    //   IColonyTagCapability     → ChunkAPI IChunkDataHandler (see ColonyChunkDataHandler)
    //   IChunkmanagerCapability  → ChunkAPI IChunkDataHandler (see ColonyChunkDataHandler)
    //   IColonyManagerCapability → ColonyManagerWorldSavedData extends WorldSavedData

    // -----------------------------------------------------------------------
    // Entity Attributes — removed
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21, entity attributes were registered via EntityAttributeCreationEvent:
    //   @SubscribeEvent
    //   public static void createEntityAttribute(EntityAttributeCreationEvent event) {
    //       event.put(ModEntities.CITIZEN, AbstractEntityCitizen.getDefaultAttributes().build());
    //       ... (many more)
    //   }
    // In 1.7.10, entity attributes are set in the entity class constructor via
    //   getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
    // etc. No separate event is needed.

    // -----------------------------------------------------------------------
    // Registry Events — removed
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] NewRegistryEvent is 1.21-only; custom registries use plain Maps or
    //   GameData in 1.7.10. Removed:
    //   @SubscribeEvent
    //   public static void registerNewRegistries(NewRegistryEvent event) {
    //       MinecoloniesAPIProxy.getInstance().onRegistryNewRegistry(event);
    //   }

    // [1.7.10 BACKPORT] RegisterEvent for recipe serializers is 1.21-only; removed:
    //   @SubscribeEvent
    //   public static void registerRecipeSerializers(RegisterEvent event) { ... }

    // -----------------------------------------------------------------------
    // Config accessor
    // -----------------------------------------------------------------------

    /**
     * Returns the active configuration instance.
     *
     * @return the {@link Configuration}.
     */
    public static Configuration getConfig()
    {
        return config;
    }

    // -----------------------------------------------------------------------
    // Incompatibility warnings
    // -----------------------------------------------------------------------

    /**
     * Logs a warning if known incompatible mods are detected.
     */
    private void logIncompatibilities()
    {
        // [1.7.10 BACKPORT] ModList.get().getModContainerById() is 1.21-only.
        //   In 1.7.10, use Loader.isModLoaded("minecolonies_tweaks").
        if (cpw.mods.fml.common.Loader.isModLoaded("minecolonies_tweaks"))
        {
            Log.getLogger().warn("|===================================================================================|");
            Log.getLogger().warn("|                                                                                   |");
            Log.getLogger().warn("| Minecolonies detected: 'Tweaks/Compatibility addon for Minecolonies'.            |");
            Log.getLogger().warn("| Please report issues to that addon's authors, not the MineColonies team.         |");
            Log.getLogger().warn("|                                                                                   |");
            Log.getLogger().warn("|===================================================================================|");
        }
    }
}

