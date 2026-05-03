package com.minecolonies.api.configuration;

import com.minecolonies.api.colony.permissions.Explosions;
import com.minecolonies.api.util.constant.CitizenConstants;
// [1.7.10 BACKPORT] Forge 1.7.10 config file
import net.minecraftforge.common.config.Configuration;

import java.util.List;

import static com.minecolonies.api.util.constant.Constants.*;

// [1.7.10 BACKPORT] Removed: net.minecraftforge.common.ForgeConfigSpec
// All ForgeConfigSpec.BooleanValue / IntValue / DoubleValue / EnumValue / ConfigValue<List>
// fields are now typed as AbstractConfiguration.BooleanValue / IntValue / DoubleValue /
// EnumValue / StringListValue — thin wrappers providing the same .get() interface.

/**
 * Mod server configuration. Loaded serverside, synced on connection.
 *
 * <p><b>1.7.10 Backport:</b> Constructor now takes a {@code Configuration} (Forge 1.7.10
 * config file object) instead of a {@code ForgeConfigSpec.Builder}. All field types are
 * replaced by {@link AbstractConfiguration} inner wrappers that expose the same {@code .get()}
 * method, so no call-sites in the rest of the codebase need changing.</p>
 */
public class ServerConfiguration extends AbstractConfiguration
{
    // [1.7.10 BACKPORT] Field types changed:
    //   ForgeConfigSpec.BooleanValue → AbstractConfiguration.BooleanValue
    //   ForgeConfigSpec.IntValue     → AbstractConfiguration.IntValue
    //   ForgeConfigSpec.DoubleValue  → AbstractConfiguration.DoubleValue
    //   ForgeConfigSpec.EnumValue<V> → AbstractConfiguration.EnumValue<V>
    //   ForgeConfigSpec.ConfigValue<List<? extends String>> → AbstractConfiguration.StringListValue

    public final BooleanValue     initialCitizenAmountEnabled; // kept for compat; see IntValue below
    public final IntValue         initialCitizenAmount;
    public final BooleanValue     allowInfiniteSupplyChests;
    public final BooleanValue     allowInfiniteColonies;
    public final BooleanValue     allowOtherDimColonies;
    public final IntValue         maxCitizenPerColony;
    public final BooleanValue     enableInDevelopmentFeatures;
    public final BooleanValue     alwaysRenderNameTag;
    public final BooleanValue     workersAlwaysWorkInRain;
    public final IntValue         luckyBlockChance;
    public final IntValue         minThLevelToTeleport;
    public final DoubleValue      foodModifier;
    public final IntValue         diseaseModifier;
    public final BooleanValue     forceLoadColony;
    public final IntValue         loadtime;
    public final IntValue         colonyLoadStrictness;
    public final IntValue         maxTreeSize;
    public final BooleanValue     noSupplyPlacementRestrictions;
    public final BooleanValue     skyRaiders;

    public final BooleanValue           researchCreativeCompletion;
    public final BooleanValue           researchDebugLog;
    public final StringListValue        researchResetCost;

    public final BooleanValue     canPlayerUseRTPCommand;
    public final BooleanValue     canPlayerUseColonyTPCommand;
    public final BooleanValue     canPlayerUseAllyTHTeleport;
    public final BooleanValue     canPlayerUseHomeTPCommand;
    public final BooleanValue     canPlayerUseShowColonyInfoCommand;
    public final BooleanValue     canPlayerUseKillCitizensCommand;
    public final BooleanValue     canPlayerUseModifyCitizensCommand;
    public final BooleanValue     canPlayerUseAddOfficerCommand;
    public final BooleanValue     canPlayerUseDeleteColonyCommand;
    public final BooleanValue     canPlayerUseResetCommand;

    public final IntValue         maxColonySize;
    public final IntValue         minColonyDistance;
    public final IntValue         initialColonySize;
    public final IntValue         maxDistanceFromWorldSpawn;
    public final IntValue         minDistanceFromWorldSpawn;

    public final BooleanValue     enableColonyRaids;
    public final IntValue         raidDifficulty;
    public final IntValue         maxRaiders;
    public final BooleanValue     raidersbreakblocks;
    public final IntValue         averageNumberOfNightsBetweenRaids;
    public final IntValue         minimumNumberOfNightsBetweenRaids;
    public final BooleanValue     raidersbreakdoors;
    public final BooleanValue     mobAttackCitizens;
    public final DoubleValue      guardDamageMultiplier;
    public final DoubleValue      guardHealthMult;
    public final BooleanValue     pvp_mode;

    public final BooleanValue           enableColonyProtection;
    public final EnumValue<Explosions>  turnOffExplosionsInColonies;
    public final IntValue               permissionEventMinBypassPermLevel;

    public final BooleanValue     auditCraftingTags;
    public final BooleanValue     debugInventories;
    public final BooleanValue     blueprintBuildMode;

    public final IntValue         pathfindingDebugVerbosity;
    public final IntValue         pathfindingMaxThreadCount;
    public final IntValue         minimumRailsToPath;

    public final BooleanValue     creativeResolve;

    /**
     * Loads server configuration from the provided Forge 1.7.10 {@link Configuration} file.
     *
     * @param config the config file object (obtained from {@code FMLPreInitializationEvent}).
     */
    protected ServerConfiguration(final Configuration config)
    {
        createCategory(config, "gameplay");

        initialCitizenAmountEnabled   = defineBoolean(config, "initialcitizenamountenabled", true);
        initialCitizenAmount          = defineInteger(config, "initialcitizenamount", 4, 1, 10);
        allowInfiniteSupplyChests     = defineBoolean(config, "allowinfinitesupplychests", false);
        allowInfiniteColonies         = defineBoolean(config, "allowinfinitecolonies", false);
        allowOtherDimColonies         = defineBoolean(config, "allowotherdimcolonies", true);
        maxCitizenPerColony           = defineInteger(config, "maxcitizenpercolony", 250, 25, CitizenConstants.CITIZEN_LIMIT_MAX);
        enableInDevelopmentFeatures   = defineBoolean(config, "enableindevelopmentfeatures", false);
        alwaysRenderNameTag           = defineBoolean(config, "alwaysrendernametag", true);
        workersAlwaysWorkInRain       = defineBoolean(config, "workersalwaysworkinrain", false);
        luckyBlockChance              = defineInteger(config, "luckyblockchance", 1, 0, 100);
        minThLevelToTeleport          = defineInteger(config, "minthleveltoteleport", 3, 0, 5);
        foodModifier                  = defineDouble(config,  "foodmodifier", 1.0, 0.1, 100);
        diseaseModifier               = defineInteger(config, "diseasemodifier", 5, 1, 100);
        forceLoadColony               = defineBoolean(config, "forceloadcolony", true);
        loadtime                      = defineInteger(config, "loadtime", 10, 1, 1440);
        colonyLoadStrictness          = defineInteger(config, "colonyloadstrictness", 3, 1, 15);
        maxTreeSize                   = defineInteger(config, "maxtreesize", 400, 1, 1000);
        noSupplyPlacementRestrictions = defineBoolean(config, "nosupplyplacementrestrictions", false);
        skyRaiders                    = defineBoolean(config, "skyraiders", false);

        swapToCategory(config, "research");
        researchCreativeCompletion = defineBoolean(config, "researchcreativecompletion", true);
        researchDebugLog           = defineBoolean(config, "researchdebuglog", false);
        researchResetCost          = defineList(config, "researchresetcost", List.of("minecolonies:ancienttome:1"), s -> s instanceof String);

        swapToCategory(config, "commands");
        canPlayerUseRTPCommand              = defineBoolean(config, "canplayerusertpcommand", false);
        canPlayerUseColonyTPCommand         = defineBoolean(config, "canplayerusecolonytpcommand", false);
        canPlayerUseAllyTHTeleport          = defineBoolean(config, "canplayeruseallytownhallteleport", true);
        canPlayerUseHomeTPCommand           = defineBoolean(config, "canplayerusehometpcommand", false);
        canPlayerUseShowColonyInfoCommand   = defineBoolean(config, "canplayeruseshowcolonyinfocommand", true);
        canPlayerUseKillCitizensCommand     = defineBoolean(config, "canplayerusekillcitizenscommand", false);
        canPlayerUseModifyCitizensCommand   = defineBoolean(config, "canplayerusemodifycitizenscommand", false);
        canPlayerUseAddOfficerCommand       = defineBoolean(config, "canplayeruseaddofficercommand", true);
        canPlayerUseDeleteColonyCommand     = defineBoolean(config, "canplayerusedeletecolonycommand", false);
        canPlayerUseResetCommand            = defineBoolean(config, "canplayeruseresetcommand", false);

        swapToCategory(config, "claims");
        maxColonySize              = defineInteger(config, "maxColonySize", 20, 1, 250);
        minColonyDistance          = defineInteger(config, "minColonyDistance", 8, 1, 200);
        initialColonySize          = defineInteger(config, "initialColonySize", 4, 1, 15);
        maxDistanceFromWorldSpawn  = defineInteger(config, "maxdistancefromworldspawn", 30000, 1000, Integer.MAX_VALUE);
        minDistanceFromWorldSpawn  = defineInteger(config, "mindistancefromworldspawn", 0, 0, 1000);

        swapToCategory(config, "combat");
        enableColonyRaids                      = defineBoolean(config, "dobarbariansspawn", true);
        raidDifficulty                         = defineInteger(config, "barbarianhordedifficulty", DEFAULT_BARBARIAN_DIFFICULTY, MIN_BARBARIAN_DIFFICULTY, MAX_BARBARIAN_DIFFICULTY);
        maxRaiders                             = defineInteger(config, "maxBarbarianSize", 80, MIN_BARBARIAN_HORDE_SIZE, MAX_BARBARIAN_HORDE_SIZE);
        raidersbreakblocks                     = defineBoolean(config, "dobarbariansbreakthroughwalls", true);
        averageNumberOfNightsBetweenRaids      = defineInteger(config, "averagenumberofnightsbetweenraids", 14, 1, 50);
        minimumNumberOfNightsBetweenRaids      = defineInteger(config, "minimumnumberofnightsbetweenraids", 10, 1, 30);
        mobAttackCitizens                      = defineBoolean(config, "mobattackcitizens", true);
        raidersbreakdoors                      = defineBoolean(config, "shouldraiderbreakdoors", true);
        guardDamageMultiplier                  = defineDouble(config, "guardDamageMultiplier", 1.0, 0.1, 15.0);
        guardHealthMult                        = defineDouble(config, "guardhealthmult", 1.0, 0.1, 5.0);
        pvp_mode                               = defineBoolean(config, "pvp_mode", false);

        swapToCategory(config, "permissions");
        enableColonyProtection            = defineBoolean(config, "enablecolonyprotection", true);
        turnOffExplosionsInColonies       = defineEnum(config, "turnoffexplosionsincolonies", Explosions.DAMAGE_ENTITIES);
        permissionEventMinBypassPermLevel = defineInteger(config, "permissioneventbypassminpermlevel", 2, 0, 4);

        swapToCategory(config, "compatibility");
        auditCraftingTags  = defineBoolean(config, "auditcraftingtags", false);
        debugInventories   = defineBoolean(config, "debuginventories", false);
        blueprintBuildMode = defineBoolean(config, "blueprintbuildmode", false);

        swapToCategory(config, "pathfinding");
        pathfindingDebugVerbosity = defineInteger(config, "pathfindingdebugverbosity", 0, 0, 10);
        minimumRailsToPath        = defineInteger(config, "minimumrailstopath", 8, 5, 100);
        pathfindingMaxThreadCount = defineInteger(config, "pathfindingmaxthreadcount", 1, 1, 10);

        swapToCategory(config, "requestSystem");
        creativeResolve = defineBoolean(config, "creativeresolve", false);

        finishCategory(config);
    }
}
