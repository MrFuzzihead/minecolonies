package com.minecolonies.core.colony;
// [1.7.10] Style removed — no equivalent
// [1.7.10] Animal removed — EntityAnimal used if needed
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
// [1.7.10] BlockState (1.21) removed — net.minecraft.block.state.BlockState (1.7.10) used
// [1.7.10] Player removed — EntityPlayer used below
// [1.7.10] AABB removed — AxisAlignedBB used
// [1.7.10] RandomSource removed — java.util.Random used
// [1.7.10] BoneMealItem removed — not used

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.ldtteam.structurize.util.BlockUtils;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.*;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.modules.ISettingsModule;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.connections.IColonyConnectionManager;
import com.minecolonies.api.colony.managers.interfaces.*;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.permissions.Rank;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.workorders.IWorkManager;
import com.minecolonies.api.compatibility.newstruct.BlueprintMapping;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickingTransition;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.eventbus.events.colony.permissions.PlayerEnteringModEvent;
import com.minecolonies.api.eventbus.events.colony.permissions.PlayerLeavingModEvent;
import com.minecolonies.api.quests.IQuestManager;
import com.minecolonies.api.research.IResearchManager;
import com.minecolonies.api.util.*;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.NbtTagConstants;
import com.minecolonies.api.util.constant.Suppression;
import com.minecolonies.core.Network;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.SettingsModule;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingTownHall;
import com.minecolonies.core.colony.events.raid.RaidManager;
import com.minecolonies.core.colony.managers.*;
import com.minecolonies.core.colony.permissions.ColonyPermissionEventHandler;
import com.minecolonies.core.colony.permissions.Permissions;
import com.minecolonies.core.colony.pvp.AttackingPlayer;
import com.minecolonies.core.colony.requestsystem.management.manager.StandardRequestManager;
import com.minecolonies.core.colony.workorders.WorkManager;
import com.minecolonies.core.datalistener.CitizenNameListener;
import com.minecolonies.core.network.messages.client.colony.ColonyViewRemoveWorkOrderMessage;
import com.minecolonies.core.quests.QuestManager;
import net.minecraft.util.EnumChatFormatting;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] BuiltInRegistries removed
// [1.7.10] Registries removed
import net.minecraft.nbt.*;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] ServerChunkCache not in 1.7.10
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] DyeColor not in 1.7.10
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.block.Block;
// [1.7.10] block.entity removed
// [1.7.10] block.entity removed
// [1.7.10] int -> int metadata
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.gameevent.TickEvent;
// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.minecolonies.api.colony.ColonyState.*;
import static com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickRateConstants.MAX_TICKRATE;
import static com.minecolonies.api.research.util.ResearchConstants.SHIELD_USAGE;
import static com.minecolonies.api.util.constant.ColonyConstants.*;
import static com.minecolonies.api.util.constant.Constants.DEFAULT_STYLE;
import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.TranslationConstants.*;
import static com.minecolonies.core.MineColonies.getConfig;

/**
 * This class describes a colony and contains all the data and methods for manipulating a Colony.
 */
@SuppressWarnings({Suppression.BIG_CLASS, Suppression.SPLIT_CLASS})
public class Colony implements IColony
{
    /**
     * The default style for the building.
     */
    private String pack = DEFAULT_STYLE;

    /**
     * Id of the colony.
     */
    private final int id;

    /**
     * Dimension of the colony.
     */
    private int /* ResourceKey */ dimensionId;

    /**
     * List of loaded chunks for the colony.
     */
    private final ConcurrentHashMap<Long, Long> loadedChunks = new ConcurrentHashMap<>();

    /**
     * List of loaded chunks for the colony.
     */
    public Set<Long> ticketedChunks = new HashSet<>();

    private boolean ticketedChunksDirty = true;

    /**
     * List of chunks that have to be be force loaded.
     */
    private final Set<Long> pendingChunks = new HashSet<>();

    /**
     * List of chunks pending for unloading, which have their tickets removed
     */
    private final Set<Long> pendingToUnloadChunks = new HashSet<>();

    /**
     * List of waypoints of the colony.
     */
    private final Map<int[], Integer> wayPoints = new HashMap<>();

    /**
     * Work Manager of the colony (Request System).
     */
    private final WorkManager workManager = new WorkManager(this);

    /**
     * Building manager of the colony.
     */
    private final IRegisteredStructureManager buildingManager = new RegisteredStructureManager(this);

    /**
     * Grave manager of the colony.
     */
    private final IGraveManager graveManager = new GraveManager(this);

    /**
     * Citizen manager of the colony.
     */
    private final ICitizenManager citizenManager = new CitizenManager(this);

    /**
     * Citizen manager of the colony.
     */
    private final IVisitorManager visitorManager = new VisitorManager(this);

    /**
     * Animal manager of the colony.
     */
    private final IAnimalManager animalManager = new AnimalManager(this);

    /**
     * Barbarian manager of the colony.
     */
    private final IRaiderManager raidManager = new RaidManager(this);

    /**
     * Event manager of the colony.
     */
    private final IEventManager eventManager = new EventManager(this);

    /**
     * Reproduction manager of the colony.
     */
    private final IReproductionManager reproductionManager = new ReproductionManager(this);

    /**
     * Event description manager of the colony.
     */
    private final IEventDescriptionManager eventDescManager = new EventDescriptionManager(this);

    /**
     * The colony package manager.
     */
    private final IColonyPackageManager packageManager = new ColonyPackageManager(this);

    /**
     * Event manager of the colony.
     */
    private final IStatisticsManager statisticManager = new StatisticsManager();

    /**
     * Quest manager of the colony
     */
    private IQuestManager questManager;

    /**
     * Traveling manager of the colony.
     */
    private final TravellingManager travellingManager = new TravellingManager(this);

    /**
     * Connection manager of the colony.
     */
    private final ColonyConnectionManager connectionManager = new ColonyConnectionManager(this);

    /**
     * The Positions which players can freely interact.
     */
    private ImmutableSet<int[]> freePositions = ImmutableSet.of();

    /**
     * The Blocks which players can freely interact with.
     */
    private ImmutableSet<Block> freeBlocks = ImmutableSet.of();

    /**
     * Colony permission event handler.
     */
    private ColonyPermissionEventHandler eventHandler;

    /**
     * Whether or not this colony may be auto-deleted.
     */
    private boolean canColonyBeAutoDeleted = true;

    /**
     * Variable to determine if its currently day or night.
     */
    private boolean isDay = true;

    /**
     * The world the colony currently runs on.
     */
    @Nullable
    private World world = null;

    /**
     * The name of the colony.
     */
    private String name = "ERROR(Wasn't placed by player)";

    /**
     * The center of the colony.
     */
    private int[] center;

    /**
     * The colony permission object.
     */
    @NotNull
    private Permissions permissions;

    /**
     * The request manager assigned to the colony.
     */
    private IRequestManager requestManager;

    /**
     * The request manager assigned to the colony.
     */
    private IResearchManager researchManager;

    /**
     * The NBTTag compound of the colony itself.
     */
    private NBTTagCompound colonyTag;

    /**
     * List of players visiting the colony.
     */
    private final List<EntityPlayer> visitingPlayers = new ArrayList<>();

    /**
     * List of players attacking the colony.
     */
    private final List<AttackingPlayer> attackingPlayers = new ArrayList<>();

    /**
     * The colonies state machine
     */
    private ITickRateStateMachine<ColonyState> colonyStateMachine = null;

    /**
     * If the colony is dirty.
     */
    private boolean isDirty = true;

    /**
     * The colony team color.
     */
    private EnumChatFormatting colonyTeamColor = EnumChatFormatting.WHITE;

    /**
     * The colony flag, as a list of patterns.
     */
    private NBTTagList colonyFlag = new BannerPattern.Builder()
        .addPattern(BannerPatterns.BASE, DyeColor.WHITE)
        .toListTag();

    /**
     * The last time the mercenaries were used.
     */
    private long mercenaryLastUse = 0;

    /**
     * The amount of additional child time gathered when the colony is not loaded.
     */
    private int additionalChildTime = 0;

    /**
     * The maximum amount of additional child time to be stored when the colony is not loaded.
     */
    private static final int maxAdditionalChildTime = 70000;

    /**
     * Boolean whether the colony has childs.
     */
    private boolean hasChilds = false;

    /**
     * Last time the server was online.
     */
    public long lastOnlineTime = 0;

    /**
     * The force chunk load timer.
     */
    private int forceLoadTimer = 0;

    /**
     * The texture set of the colony.
     */
    private String textureStyle = "default";

    /**
     * The colony name style.
     */
    private String nameStyle = "default";

    /**
     * Current day of the colony.
     */
    private int day = 0;

    /**
     * Colony World settings manager.
     */
    private final SettingsModule settingsModule = (SettingsModule) BuildingEntry.produceModuleWithoutBuilding(BuildingModules.TOWNHALL_SETTINGS.key);

    /**
     * Constructor for a newly created Colony.
     *
     * @param id The id of the colony to create.
     * @param w  The world the colony exists in.
     * @param c  The center of the colony (location of Town Hall).
     */
    @SuppressWarnings("squid:S2637")
    Colony(final int id, @Nullable final World w, final int[] c)
    {
        this(id, w);
        center = c;
        this.permissions = new Permissions(this);
        requestManager = new StandardRequestManager(this);
        researchManager = new ResearchManager(this);
        questManager = new QuestManager(this);
    }

    /**
     * Base constructor.
     *
     * @param id    The current id for the colony.
     * @param world The world the colony exists in.
     */
    protected Colony(final int id, @Nullable final World world)
    {
        questManager = new QuestManager(this);
        this.id = id;
        if (world != null)
        {
            this.dimensionId = world.dimension();
            onWorldLoad(world);
        }
        this.permissions = new Permissions(this);
        researchManager = new ResearchManager(this);
        colonyStateMachine = new TickRateStateMachine<>(INACTIVE, e ->
        {
            Log.getLogger().warn("Exception triggered in colony:{} in dimension:{} history:{}", getID(), getDimension().location(), colonyStateMachine.getHistory(), e);
            colonyStateMachine.setCurrentDelay(20 * 60 * 5);
        });
        colonyStateMachine.setHistoryEnabled(true, 10);

        colonyStateMachine.addTransition(new TickingTransition<>(INACTIVE, () -> true, this::updateState, UPDATE_STATE_INTERVAL));
        colonyStateMachine.addTransition(new TickingTransition<>(UNLOADED, () -> true, this::updateState, UPDATE_STATE_INTERVAL));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, () -> true, this::updateState, UPDATE_STATE_INTERVAL));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, () -> {
            citizenManager.tickCitizenData(TICKS_SECOND * 3);
            return false;
        }, () -> ACTIVE, TICKS_SECOND * 3));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, () -> {
            animalManager.tickAnimalData(TICKS_SECOND * 3);
            return false;
        }, () -> ACTIVE, TICKS_SECOND * 3));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, this::updateSubscribers, () -> ACTIVE, UPDATE_SUBSCRIBERS_INTERVAL));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, this::tickRequests, () -> ACTIVE, UPDATE_RS_INTERVAL));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, this::tickTravellers, () -> ACTIVE, UPDATE_TRAVELING_INTERVAL));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, this::checkDayTime, () -> ACTIVE, UPDATE_DAYTIME_INTERVAL));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, this::updateWayPoints, () -> ACTIVE, CHECK_WAYPOINT_EVERY));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, this::worldTickSlow, () -> ACTIVE, MAX_TICKRATE));
        colonyStateMachine.addTransition(new TickingTransition<>(ACTIVE, this::tickWorkManager, () -> ACTIVE, 20));
        colonyStateMachine.addTransition(new TickingTransition<>(UNLOADED, this::worldTickUnloaded, () -> UNLOADED, MAX_TICKRATE));
    }

    /**
     * Updates the state the colony is in.
     *
     * @return the new colony state.
     */
    private ColonyState updateState()
    {
        if (world == null)
        {
            return INACTIVE;
        }
        packageManager.updateAwayTime();

        if (!packageManager.getCloseSubscribers().isEmpty() || (loadedChunks.size() > 40 && !packageManager.getImportantColonyPlayers().isEmpty()))
        {
            isDirty = true;
            return ACTIVE;
        }

        if (!packageManager.getImportantColonyPlayers().isEmpty() || forceLoadTimer > 0)
        {
            isDirty = true;
            return UNLOADED;
        }

        return INACTIVE;
    }

    /**
     * Updates the existing subscribers
     *
     * @return false
     */
    private boolean updateSubscribers()
    {
        packageManager.updateSubscribers();
        return false;
    }

    /**
     * Ticks the request manager.
     *
     * @return false
     */
    private boolean tickRequests()
    {
        if (getRequestManager() != null)
        {
            getRequestManager().tick();
        }
        return false;
    }

    /**
     * Ticks the travelling manager.
     *
     * @return false
     */
    private boolean tickTravellers()
    {
        if (getTravellingManager() != null)
        {
            return !getTravellingManager().onTick();
        }
        return false;
    }

    /**
     * Called every 500 ticks, for slower updates.
     *
     * @return false
     */
    private boolean worldTickSlow()
    {
        buildingManager.cleanUpBuildings(this);
        citizenManager.onColonyTick(this);
        visitorManager.onColonyTick(this);
        animalManager.onColonyTick(this);
        updateAttackingPlayers();
        eventManager.onColonyTick(this);
        buildingManager.onColonyTick(this);
        graveManager.onColonyTick(this);
        reproductionManager.onColonyTick(this);
        questManager.onColonyTick();

        final long currTime = System.currentTimeMillis();
        if (lastOnlineTime != 0)
        {
            final long pastTime = currTime - lastOnlineTime;
            if (pastTime > ONE_HOUR_IN_MILLIS)
            {
                for (final IBuilding building : buildingManager.getBuildings().values())
                {
                    building.processOfflineTime(pastTime / 1000);
                }
            }
        }
        lastOnlineTime = currTime;

        updateChildTime();
        updateChunkLoadTimer();
        return false;
    }

    /**
     * Tick the work Manager.
     */
    private boolean tickWorkManager()
    {
        workManager.onColonyTick(this);
        return false;
    }

    /**
     * Check if we can unload the colony now.
     * Update chunk unload timer and releases chunks when it hits 0.
     */
    private void updateChunkLoadTimer()
    {
        if (getConfig().getServer().forceLoadColony.get())
        {
            for (final EntityPlayerMP sub : getPackageManager().getCloseSubscribers())
            {
                if (getPermissions().getRank(sub).isColonyManager())
                {
                    this.forceLoadTimer = getConfig().getServer().loadtime.get() * 20 * 60;
                    pendingChunks.addAll(pendingToUnloadChunks);
                    for (final long pending : pendingChunks)
                    {
                        checkChunkAndRegisterTicket(pending, world.getChunk(ChunkCoordIntPair.getX(pending), ChunkCoordIntPair.getZ(pending)));
                    }

                    pendingToUnloadChunks.clear();
                    pendingChunks.clear();
                    return;
                }
            }

            if (this.forceLoadTimer > 0)
            {
                this.forceLoadTimer -= MAX_TICKRATE;
                if (this.forceLoadTimer <= 0)
                {
                    for (final long chunkCoord : this.ticketedChunks)
                    {
                        final int chunkX = ChunkCoordIntPair.getX(ChunkCoordIntPair);
                        final int chunkZ = ChunkCoordIntPair.getZ(ChunkCoordIntPair);
                        if (world instanceof ServerLevel)
                        {
                            final ChunkCoordIntPair pos = new ChunkCoordIntPair(chunkX, chunkZ);
                            ((ServerChunkCache) world.getChunkSource()).removeRegionTicket(KEEP_LOADED_TYPE, pos, 2, pos);
                            pendingToUnloadChunks.add(ChunkCoordIntPair);
                        }
                    }
                    ticketedChunks.clear();
                    ticketedChunksDirty = true;
                }
            }
        }
    }

    /**
     * Checks the chunk and registers a ticket for it if needed
     *
     * @param ChunkCoordIntPair chunk position to check
     */
    private void checkChunkAndRegisterTicket(final long chunkCoord, final Chunk chunk)
    {
        if (forceLoadTimer > 0 && world instanceof ServerLevel)
        {
            if (!ticketedChunks.contains(ChunkCoordIntPair) && buildingManager.keepChunkColonyLoaded(chunk))
            {
                ticketedChunks.add(ChunkCoordIntPair);
                ticketedChunksDirty = true;
                ((ServerChunkCache) world.getChunkSource()).addRegionTicket(KEEP_LOADED_TYPE, chunk.getPos(), 2, chunk.getPos(), true);
            }
        }
    }

    /**
     * Called every 500 ticks, for slower updates. Only ticked when the colony is not loaded.
     *
     * @return false
     */
    private boolean worldTickUnloaded()
    {
        updateChildTime();
        updateChunkLoadTimer();
        return false;
    }

    /**
     * Adds 500 additional ticks to the child growth.
     */
    private void updateChildTime()
    {
        if (hasChilds && additionalChildTime < maxAdditionalChildTime)
        {
            additionalChildTime += MAX_TICKRATE;
        }
        else
        {
            additionalChildTime = 0;
        }
    }

    /**
     * Updates the day and night detection.
     *
     * @return false
     */
    private boolean checkDayTime()
    {
        if (isDay && !WorldUtil.isDayTime(world))
        {
            isDay = false;
            eventManager.onNightFall();
            raidManager.onNightFall();
            if (!packageManager.getCloseSubscribers().isEmpty())
            {
                citizenManager.checkCitizensForHappiness();
            }

            citizenManager.updateCitizenSleep(false);
            eventDescManager.computeNews();
        }
        else if (!isDay && WorldUtil.isDayTime(world))
        {
            isDay = true;
            day++;
            citizenManager.onWakeUp();
        }
        return false;
    }

    /**
     * Updates the pvping playeres.
     */
    public void updateAttackingPlayers()
    {
        final List<EntityPlayer> visitors = new ArrayList<>(visitingPlayers);

        //Clean up visiting player.
        for (final EntityPlayer player : visitors)
        {
            if (!packageManager.getCloseSubscribers().contains(player))
            {
                visitingPlayers.remove(player);
                attackingPlayers.remove(new AttackingPlayer(player));
            }
        }

        for (final AttackingPlayer player : attackingPlayers)
        {
            if (!player.getGuards().isEmpty())
            {
                player.refreshList(this);
                if (player.getGuards().isEmpty())
                {
                    MessageUtils.format(COLONY_DEFENDED_SUCCESS_MESSAGE, player.getPlayer().getName()).sendTo(this).forManagers();
                }
            }
        }
    }

    /**
     * Set up the colony color for team handling for pvp.
     *
     * @param colonyColor the colony color.
     */
    public void setColonyColor(final EnumChatFormatting colonyColor)
    {
        if (this.world != null)
        {
            this.colonyTeamColor = colonyColor;
        }
        this.markDirty();
    }

    /**
     * Set up the colony flag patterns for use in decorations etc
     *
     * @param colonyFlag the list of pattern-color pairs
     */
    @Override
    public void setColonyFlag(NBTTagList colonyFlag)
    {
        this.colonyFlag = colonyFlag;
        if (researchManager.getResearchEffects().getEffectStrength(SHIELD_USAGE) > 0)
        {
            citizenManager.onFlagChange();
        }
        markDirty();
    }

    /**
     * Load a saved colony.
     *
     * @param compound The NBT compound containing the colony's data.
     * @param world    the world to load it for.
     * @return loaded colony.
     */
    @Nullable
    public static Colony loadColony(@NotNull final NBTTagCompound compound, @Nullable final World world)
    {
        try
        {
            final int id = compound.getInt(TAG_ID);
            @NotNull final Colony c = new Colony(id, world);
            c.name = compound.getString(TAG_NAME);
            c.center = BlockPosUtil.read(compound, TAG_CENTER);
            c.dimensionId = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString(TAG_DIMENSION)));

            c.setRequestManager();
            c.read(compound);

            return c;
        }
        catch (final Exception e)
        {
            Log.getLogger().warn("Something went wrong loading a colony, please report this to the administrators", e);
        }
        return null;
    }

    /**
     * Sets the request manager on colony load.
     */
    private void setRequestManager()
    {
        requestManager = new StandardRequestManager(this);
    }

    /**
     * Read colony from saved data.
     *
     * @param compound compound to read from.
     */
    public void read(@NotNull final NBTTagCompound compound)
    {
        dimensionId = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString(TAG_DIMENSION)));

        mercenaryLastUse = compound.getLong(TAG_MERCENARY_TIME);
        additionalChildTime = compound.getInt(TAG_CHILD_TIME);

        // Permissions
        permissions.loadPermissions(compound);

        citizenManager.read(compound.getCompoundTag(TAG_CITIZEN_MANAGER));
        visitorManager.read(compound);
        animalManager.read(compound.getCompoundTag(TAG_ANIMAL_MANAGER));
        buildingManager.read(compound.getCompoundTag(TAG_BUILDING_MANAGER));

        // Recalculate max after citizens and buildings are loaded.
        citizenManager.afterBuildingLoad();

        graveManager.read(compound.getCompoundTag(TAG_GRAVE_MANAGER));

        eventManager.readFromNBT(compound);
        statisticManager.readFromNBT(compound);

        questManager.deserializeNBT(compound.getCompoundTag(TAG_QUEST_MANAGER));
        eventDescManager.deserializeNBT(compound.getCompoundTag(NbtTagConstants.TAG_EVENT_DESC_MANAGER));

        if (compound.hasKey(TAG_RESEARCH))
        {
            researchManager.readFromNBT(compound.getCompoundTag(TAG_RESEARCH));
            // now that buildings, colonists, and research are loaded, check for new autoStartResearch.
            // this is mostly for backwards compatibility with older saves, so players do not have to manually start newly added autostart researches that they've unlocked before the update.
            researchManager.checkAutoStartResearch();
        }

        //  Workload
        workManager.read(compound.getCompoundTag(TAG_WORK));

        wayPoints.clear();
        // Waypoints
        final NBTTagList wayPointTagList = compound.getTagList(TAG_WAYPOINT, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < wayPointTagList.size(); ++i)
        {
            final NBTTagCompound blockAtPos = wayPointTagList.getCompoundTagAt(i);
            final int[] pos = BlockPosUtil.read(blockAtPos, TAG_WAYPOINT);
            final int blockId = blockAtPos.getInteger(TAG_BLOCK); // [1.7.10] NbtUtils.readBlockState -> getInteger block ID
            wayPoints.put(pos, blockId);
        }

        // Free blocks
        final Set<Block> tempFreeBlocks = new HashSet<>();
        final NBTTagList freeBlockTagList = compound.getTagList(TAG_FREE_BLOCKS, NBTBase.TAG_STRING);
        for (int i = 0; i < freeBlockTagList.size(); ++i)
        {
            tempFreeBlocks.add(Block.getBlockFromName(freeBlockTagList.getString(i))); // [1.7.10] ForgeRegistries.BLOCKS.getValue -> Block.getBlockFromName
        }
        freeBlocks = ImmutableSet.copyOf(tempFreeBlocks);

        final Set<int[]> tempFreePositions = new HashSet<>();
        // Free positions
        final NBTTagList freePositionTagList = compound.getTagList(TAG_FREE_POSITIONS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < freePositionTagList.size(); ++i)
        {
            final NBTTagCompound blockTag = freePositionTagList.getCompoundTagAt(i);
            final int[] block = BlockPosUtil.read(blockTag, TAG_FREE_POSITIONS);
            tempFreePositions.add(block);
        }
        freePositions = ImmutableSet.copyOf(tempFreePositions);

        packageManager.setLastContactInHours(compound.getInt(TAG_ABANDONED));

        if (compound.hasKey(TAG_STYLE))
        {
            this.pack = BlueprintMapping.getStyleMapping(compound.getString(TAG_STYLE));
        }
        else
        {
            this.pack = compound.getString(TAG_PACK);
        }

        raidManager.read(compound);

        if (compound.hasKey(TAG_AUTO_DELETE))
        {
            this.canColonyBeAutoDeleted = compound.getBoolean(TAG_AUTO_DELETE);
        }
        else
        {
            this.canColonyBeAutoDeleted = true;
        }

        if (compound.hasKey(TAG_TEAM_COLOR))
        {
            // This read can occur before the world is non-null, due to Minecraft's order of operations for capabilities.
            // As a result, setColonyColor proper must wait until onWorldLoad fires.
            this.colonyTeamColor = EnumChatFormatting.values()[compound.getInt(TAG_TEAM_COLOR)];
        }

        if (compound.hasKey(TAG_FLAG_PATTERNS))
        {
            this.setColonyFlag(compound.getTagList(TAG_FLAG_PATTERNS, Constants.TAG_COMPOUND));
        }

        this.requestManager.reset();
        if (compound.hasKey(TAG_REQUESTMANAGER))
        {
            this.requestManager.deserializeNBT(compound.getCompoundTag(TAG_REQUESTMANAGER));
        }
        this.lastOnlineTime = compound.getLong(TAG_LAST_ONLINE);
        if (compound.hasKey(TAG_COL_TEXT))
        {
            this.textureStyle = compound.getString(TAG_COL_TEXT);
        }
        if (compound.hasKey(TAG_COL_NAME_STYLE))
        {
            this.nameStyle = compound.getString(TAG_COL_NAME_STYLE);
        }

        if (compound.hasKey(BuildingModules.TOWNHALL_SETTINGS.key) && settingsModule != null)
        {
            settingsModule.deserializeNBT(compound.getCompoundTag(BuildingModules.TOWNHALL_SETTINGS.key));
        }

        this.day = compound.getInt(COLONY_DAY);
        this.colonyTag = compound;

        if (compound.hasKey(NbtTagConstants.TAG_TRAVELLING_DATA))
        {
            this.travellingManager.deserializeNBT(compound.getCompoundTag(NbtTagConstants.TAG_TRAVELLING_DATA));
        }

        if (compound.hasKey(NbtTagConstants.TAG_CONNECTION_MANAGER))
        {
            this.connectionManager.deserializeNBT(compound.getCompoundTag(NbtTagConstants.TAG_CONNECTION_MANAGER));
        }
    }

    /**
     * Get the event handler assigned to the colony.
     *
     * @return the ColonyPermissionEventHandler.
     */
    public ColonyPermissionEventHandler getEventHandler()
    {
        return eventHandler;
    }

    /**
     * Write colony to save data.
     *
     * @param compound compound to write to.
     */
    public NBTTagCompound write(@NotNull final NBTTagCompound compound)
    {
        compound.setInteger(DATA_VERSION_TAG, DATA_VERSION);

        //  Core attributes
        compound.setInteger(TAG_ID, id);
        compound.setString(TAG_DIMENSION, dimensionId.location().toString());

        //  Basic data
        compound.setString(TAG_NAME, name);
        BlockPosUtil.write(compound, TAG_CENTER, center);

        compound.setLong(TAG_MERCENARY_TIME, mercenaryLastUse);

        compound.setInteger(TAG_CHILD_TIME, additionalChildTime);

        // Permissions
        permissions.savePermissions(compound);

        final NBTTagCompound buildingCompound = new NBTTagCompound();
        buildingManager.write(buildingCompound);
        compound.setTag(TAG_BUILDING_MANAGER, buildingCompound);

        final NBTTagCompound citizenCompound = new NBTTagCompound();
        citizenManager.write(citizenCompound);
        compound.setTag(TAG_CITIZEN_MANAGER, citizenCompound);

        visitorManager.write(compound);

        final NBTTagCompound animalCompound = new NBTTagCompound();
        animalManager.write(animalCompound);
        compound.setTag(TAG_ANIMAL_MANAGER, animalCompound);

        final NBTTagCompound graveCompound = new NBTTagCompound();
        graveManager.write(graveCompound);
        compound.setTag(TAG_GRAVE_MANAGER, graveCompound);

        //  Workload
        @NotNull final NBTTagCompound workManagerCompound = new NBTTagCompound();
        workManager.write(workManagerCompound);
        compound.setTag(TAG_WORK, workManagerCompound);

        eventManager.writeToNBT(compound);
        statisticManager.writeToNBT(compound);

        compound.setTag(TAG_QUEST_MANAGER, questManager.serializeNBT());
        compound.setTag(NbtTagConstants.TAG_EVENT_DESC_MANAGER, eventDescManager.serializeNBT());
        raidManager.write(compound);

        @NotNull final NBTTagCompound researchManagerCompound = new NBTTagCompound();
        researchManager.writeToNBT(researchManagerCompound);
        compound.setTag(TAG_RESEARCH, researchManagerCompound);

        // Waypoints
        @NotNull final NBTTagList wayPointTagList = new NBTTagList();
        for (@NotNull final Map.Entry<int[], Integer> entry : wayPoints.entrySet()) // [1.7.10] int -> Integer
        {
            @NotNull final NBTTagCompound wayPointCompound = new NBTTagCompound();
            BlockPosUtil.write(wayPointCompound, TAG_WAYPOINT, entry.getKey());
            wayPointCompound.setInteger(TAG_BLOCK, entry.getValue()); // [1.7.10] NbtUtils.writeBlockState -> setInteger block ID
            wayPointTagList.add(wayPointCompound);
        }
        compound.setTag(TAG_WAYPOINT, wayPointTagList);

        // Free blocks
        @NotNull final NBTTagList freeBlocksTagList = new NBTTagList();
        for (@NotNull final Block block : freeBlocks)
        {
            freeBlocksTagList.appendTag(new NBTTagString(Block.blockRegistry.getNameForObject(block).toString())); // [1.7.10] ForgeRegistries.BLOCKS.getKey -> Block.blockRegistry.getNameForObject; NBTTagString.valueOf -> new NBTTagString
        }
        compound.setTag(TAG_FREE_BLOCKS, freeBlocksTagList);

        // Free positions
        @NotNull final NBTTagList freePositionsTagList = new NBTTagList();
        for (@NotNull final int[] pos : freePositions)
        {
            @NotNull final NBTTagCompound wayPointCompound = new NBTTagCompound();
            BlockPosUtil.write(wayPointCompound, TAG_FREE_POSITIONS, pos);
            freePositionsTagList.add(wayPointCompound);
        }
        compound.setTag(TAG_FREE_POSITIONS, freePositionsTagList);

        compound.setInteger(TAG_ABANDONED, packageManager.getLastContactInHours());
        compound.setTag(TAG_REQUESTMANAGER, getRequestManager().serializeNBT());
        compound.setString(TAG_PACK, pack);
        compound.setBoolean(TAG_AUTO_DELETE, canColonyBeAutoDeleted);
        compound.setInteger(TAG_TEAM_COLOR, colonyTeamColor.ordinal());
        compound.setTag(TAG_FLAG_PATTERNS, colonyFlag);
        compound.setLong(TAG_LAST_ONLINE, lastOnlineTime);
        compound.setString(TAG_COL_TEXT, textureStyle);
        compound.setString(TAG_COL_NAME_STYLE, nameStyle);
        compound.setInteger(COLONY_DAY, day);

        final NBTTagCompound settings = new NBTTagCompound();
        settingsModule.serializeNBT(settings);
        compound.setTag(BuildingModules.TOWNHALL_SETTINGS.key, settings);

        compound.setTag(TAG_TRAVELLING_DATA, travellingManager.serializeNBT());
        compound.setTag(TAG_CONNECTION_MANAGER, connectionManager.serializeNBT());

        this.colonyTag = compound;

        isDirty = false;
        return compound;
    }

    /**
     * Returns the dimension ID.
     *
     * @return Dimension ID.
     */
    public int /* ResourceKey */ getDimension()
    {
        return dimensionId;
    }

    @Override
    public boolean isRemote()
    {
        return false;
    }

    @Override
    public IResearchManager getResearchManager()
    {
        return this.researchManager;
    }

    /**
     * When the Colony's world is loaded, associate with it.
     *
     * @param w World object.
     */
    @Override
    public void onWorldLoad(@NotNull final World w)
    {
        if (w.dimension() == dimensionId)
        {
            this.world = w;
            // Register a new event handler
            if (eventHandler == null)
            {
                eventHandler = new ColonyPermissionEventHandler(this);
                questManager.onWorldLoad();
                MinecraftForge.EVENT_BUS.register(eventHandler);
            }
            setColonyColor(this.colonyTeamColor);
        }
    }

    /**
     * Unsets the world if the world unloads.
     *
     * @param w World object.
     */
    @Override
    public void onWorldUnload(@NotNull final World w)
    {
        if (w != world)
        {
            /*
             * If the event world is not the colony world ignore. This might happen in interactions with other mods.
             * This should not be a problem for minecolonies as long as we take care to do nothing in that moment.
             */
            return;
        }

        if (eventHandler != null)
        {
            MinecraftForge.EVENT_BUS.unregister(eventHandler);
        }
        world = null;
    }

    @Override
    public void onServerTick(@NotNull final TickEvent.ServerTickEvent event)
    {
    }

    /**
     * Get the Work Manager for the Colony.
     *
     * @return WorkManager for the Colony.
     */
    @Override
    @NotNull
    public IWorkManager getWorkManager()
    {
        return workManager;
    }

    /**
     * Get a copy of the freePositions list.
     *
     * @return the list of free to interact positions.
     */
    public Set<int[]> getFreePositions()
    {
        return freePositions;
    }

    /**
     * Get a copy of the freeBlocks list.
     *
     * @return the list of free to interact blocks.
     */
    public Set<Block> getFreeBlocks()
    {
        return freeBlocks;
    }

    /**
     * Add a new free to interact position.
     *
     * @param pos position to add.
     */
    public void addFreePosition(@NotNull final int[] pos)
    {
        ImmutableSet.Builder<int[]> builder = ImmutableSet.builder();
        builder.addAll(freePositions);
        builder.add(pos);
        freePositions = builder.build();
        markDirty();
    }

    /**
     * Add a new free to interact block.
     *
     * @param block block to add.
     */
    public void addFreeBlock(@NotNull final Block block)
    {
        ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
        builder.addAll(freeBlocks);
        builder.add(block);
        freeBlocks = builder.build();
        markDirty();
    }

    /**
     * Remove a free to interact position.
     *
     * @param pos position to remove.
     */
    public void removeFreePosition(@NotNull final int[] pos)
    {
        ImmutableSet.Builder<int[]> builder = ImmutableSet.builder();
        for (final int[] tempPos : freePositions)
        {
            if (!pos.equals(tempPos))
            {
                builder.add(tempPos);
            }
        }
        freePositions = builder.build();
        markDirty();
    }

    /**
     * Remove a free to interact block.
     *
     * @param block state to remove.
     */
    public void removeFreeBlock(@NotNull final Block block)
    {
        ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
        for (final Block tempBlock : freeBlocks)
        {
            if (block != tempBlock)
            {
                builder.add(tempBlock);
            }
        }
        freeBlocks = builder.build();
        markDirty();
    }

    /**
     * Any per-world-tick logic should be performed here. NOTE: If the Colony's world isn't loaded, it won't have a world tick. Use onServerTick for logic that should _always_
     * run.
     *
     * @param event {@link TickEvent.WorldTickEvent}
     */
    @Override
    public void onWorldTick(@NotNull final TickEvent.WorldTickEvent event)
    {
        if (event.World != getWorld())
        {
            /*
             * If the event world is not the colony world ignore. This might happen in interactions with other mods.
             * This should not be a problem for minecolonies as long as we take care to do nothing in that moment.
             */
            return;
        }

        if (!event.World.isRemote && (event.world.getTotalWorldTime() + id) % 20 == 0)
        {
            connectionManager.tick();
        }

        colonyStateMachine.tick();
    }

    /**
     * Calculate randomly if the colony should update the citizens. By mean they update it at CLEANUP_TICK_INCREMENT.
     *
     * @param world        the world.
     * @param averageTicks the average ticks to upate it.
     * @return a boolean by random.
     */
    public static boolean shallUpdate(final World world, final int averageTicks)
    {
        return world.getTotalWorldTime() % (world.rand.nextInt(averageTicks * 2) + 1) == 0;
    }

    /**
     * Update the waypoints after worldTicks.
     *
     * @return false
     */
    private boolean updateWayPoints()
    {
        if (!wayPoints.isEmpty() && world != null)
        {
            final int randomPos = world.rand.nextInt(wayPoints.size()); // [1.7.10] world.random -> world.rand
            int count = 0;
            for (final Map.Entry<int[], Integer> entry : wayPoints.entrySet()) // [1.7.10] int[] -> int[], int -> Integer
            {
                if (count++ == randomPos)
                {
                    if (WorldUtil.isBlockLoaded(world, entry.getKey()))
                    {
                        final int[] key = entry.getKey();
                        final Block worldBlock = world.getBlock(key[0], key[1], key[2]); // [1.7.10] getBlockState -> getBlock
                        final Block storedBlock = Block.getBlockById(entry.getValue()); // [1.7.10] int block ID -> Block
                        if (
                            ((worldBlock != storedBlock && storedBlock != ModBlocks.blockWayPoint)
                                && worldBlock != ModBlocks.blockConstructionTape)
                                || (world.isAirBlock(key[0], key[1] - 1, key[2]) && !BlockUtils.isAnySolid(storedBlock))) // [1.7.10] isEmptyBlock/below -> isAirBlock
                        {
                            wayPoints.remove(entry.getKey());
                            markDirty();
                        }
                    }
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Returns the center of the colony.
     *
     * @return Chunk Coordinates of the center of the colony.
     */
    @Override
    public int[] getCenter()
    {
        return center;
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the colony. Marks dirty.
     *
     * @param n new name.
     */
    @Override
    public void setName(final String n)
    {
        name = n;
        markDirty();
    }

    @NotNull
    @Override
    public Permissions getPermissions()
    {
        return permissions;
    }

    @Override
    public boolean isCoordInColony(@NotNull final World w, @NotNull final int[] pos)
    {
        if (w.dimension() != this.dimensionId)
        {
            return false;
        }


        final Chunk chunk = w.getChunkAt(pos);
        return ColonyUtils.getOwningColony(chunk) == this.getID();
    }

    @Override
    public long getDistanceSquared(@NotNull final int[] pos)
    {
        return BlockPosUtil.getDistanceSquared2D(center, pos);
    }

    /**
     * Returns the ID of the colony.
     *
     * @return Colony ID.
     */
    @Override
    public int getID()
    {
        return id;
    }

    @Override
    public int getLastContactInHours()
    {
        return packageManager.getLastContactInHours();
    }

    /**
     * Returns the world the colony is in.
     *
     * @return World the colony is in.
     */
    @Nullable
    public World getWorld()
    {
        return world;
    }

    @NotNull
    @Override
    public IRequestManager getRequestManager()
    {
        return requestManager;
    }

    /**
     * Marks the instance dirty.
     */
    public void markDirty()
    {
        packageManager.setDirty();
        isDirty = true;
    }

    @Override
    public boolean canBeAutoDeleted()
    {
        return canColonyBeAutoDeleted;
    }

    @Nullable
    @Override
    public IRequester getRequesterBuildingForPosition(@NotNull final int[] pos)
    {
        return buildingManager.getBuilding(pos);
    }

    @Override
    @NotNull
    public List<EntityPlayer> getMessagePlayerEntities()
    {
        List<EntityPlayer> players = new ArrayList<>();

        for (EntityPlayerMP player : packageManager.getCloseSubscribers())
        {
            if (permissions.hasPermission(player, Action.RECEIVE_MESSAGES))
            {
                players.add(player);
            }
        }

        return players;
    }

    @Override
    @NotNull
    public List<EntityPlayer> getImportantMessageEntityPlayers()
    {
        final Set<Player> playerList = new HashSet<>(getMessagePlayerEntities());

        for (final EntityPlayerMP player : packageManager.getImportantColonyPlayers())
        {
            if (permissions.getRank(player).isColonyManager())
            {
                playerList.add(player);
            }
        }
        return new ArrayList<>(playerList);
    }

    /**
     * Send the message of a removed workOrder to the client.
     *
     * @param orderId the workOrder to remove.
     */
    public void removeWorkOrderInView(final int orderId)
    {
        //  Inform Subscribers of removed workOrder
        for (final EntityPlayerMP player : packageManager.getCloseSubscribers())
        {
            Network.getNetwork().sendToPlayer(new ColonyViewRemoveWorkOrderMessage(this, orderId), player);
        }
    }

    /**
     * Adds a waypoint to the colony.
     *
     * @param point the waypoint to add.
     * @param block the block at the waypoint.
     */
    public void addWayPoint(final int[] point, final int block)
    {
        wayPoints.put(point, block);
        this.markDirty();
    }

    /**
     * Getter for overall happiness.
     *
     * @return the overall happiness.
     */
    @Override
    public double getOverallHappiness()
    {
        if (citizenManager.getCitizens().size() <= 0)
        {
            return 5.5;
        }

        double happinessSum = 0;
        for (final ICitizenData citizen : citizenManager.getCitizens())
        {
            happinessSum += citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony(), citizen);
        }
        return happinessSum / citizenManager.getCitizens().size();
    }

    /**
     * Get all the waypoints of the colony.
     *
     * @return copy of hashmap.
     */
    @Override
    public Map<int[], Integer> getWayPoints()
    {
        return new HashMap<>(wayPoints);
    }

    /**
     * This sets whether or not a colony can be automatically deleted Via command, or an on-tick check.
     *
     * @param canBeDeleted whether the colony is able to be deleted automatically
     */
    public void setCanBeAutoDeleted(final boolean canBeDeleted)
    {
        this.canColonyBeAutoDeleted = canBeDeleted;
        this.markDirty();
    }

    /**
     * Getter for the default style of the colony.
     *
     * @return the style string.
     */
    @Override
    public String getStructurePack()
    {
        return pack;
    }

    /**
     * Setter for the default pack of the colony.
     *
     * @param style the default string.
     */
    @Override
    public void setStructurePack(final String style)
    {
        this.pack = style;
        this.markDirty();
    }

    /**
     * Get the buildingmanager of the colony.
     *
     * @return the buildingManager.
     */
    @Override
    public IRegisteredStructureManager getServerBuildingManager()
    {
        return buildingManager;
    }

    @Override
    public ICommonRegisteredStructureManager getCommonBuildingManager()
    {
        //todo merge with above.
        return buildingManager;
    }

    /**
     * Get the graveManager of the colony.
     *
     * @return the graveManager.
     */
    @Override
    public IGraveManager getGraveManager()
    {
        return graveManager;
    }

    /**
     * Get the citizenManager of the colony.
     *
     * @return the citizenManager.
     */
    @Override
    public ICitizenManager getCitizenManager()
    {
        return citizenManager;
    }

    /**
     * Get the visitor manager of the colony.
     *
     * @return the visitor manager.
     */
    @Override
    public IVisitorManager getVisitorManager()
    {
        return visitorManager;
    }

    /**
     * Get the animal manager of the colony.
     *
     * @return the animal manager.
     */
    @Override
    public IAnimalManager getAnimalManager()
    {
        return animalManager;
    }

    /**
     * Get the barbManager of the colony.
     *
     * @return the barbManager.
     */
    @Override
    public IRaiderManager getRaiderManager()
    {
        return raidManager;
    }

    @Override
    public IEventManager getEventManager()
    {
        return eventManager;
    }

    @Override
    public IStatisticsManager getStatisticsManager()
    {
        return statisticManager;
    }

    @Override
    public IReproductionManager getReproductionManager()
    {
        return reproductionManager;
    }

    @Override
    public IEventDescriptionManager getEventDescriptionManager()
    {
        return eventDescManager;
    }

    /**
     * Get the packagemanager of the colony.
     *
     * @return the manager.
     */
    @Override
    public IColonyPackageManager getPackageManager()
    {
        return packageManager;
    }

    @Override
    public TravellingManager getTravellingManager()
    {
        return travellingManager;
    }

    @Override
    public IColonyConnectionManager getConnectionManager()
    {
        return connectionManager;
    }

    /**
     * Get all visiting players.
     *
     * @return the list.
     */
    public ImmutableList<EntityPlayer> getVisitingPlayers()
    {
        return ImmutableList.copyOf(visitingPlayers);
    }

    @Override
    public void addVisitingPlayer(final EntityPlayer player)
    {
        final Rank rank = getPermissions().getRank(player);
        if (!rank.isColonyManager() && !visitingPlayers.contains(player) && settingsModule.getSetting(BuildingTownHall.ENTER_LEAVE_MESSAGES).getValue())
        {
            visitingPlayers.add(player);
            if (!this.getImportantMessageEntityPlayers().contains(player))
            {
                MessageUtils.format(ENTERING_COLONY_MESSAGE, this.getName()).sendTo(player);
            }

            final PlayerEnteringModEvent notifyPlayerEnteringModEvent = new PlayerEnteringModEvent(this, player);
            IMinecoloniesAPI.getInstance().getEventBus().post(notifyPlayerEnteringModEvent);

            if (notifyPlayerEnteringModEvent.shouldShowNotification())
            {
                MessageUtils.format(ENTERING_COLONY_MESSAGE_NOTIFY, player.getName()).sendTo(this, true).forManagers();
            }
        }
    }

    @Override
    public void removeVisitingPlayer(final EntityPlayer player)
    {
        if (visitingPlayers.contains(player) && settingsModule.getSetting(BuildingTownHall.ENTER_LEAVE_MESSAGES).getValue())
        {
            visitingPlayers.remove(player);
            if (!this.getImportantMessageEntityPlayers().contains(player))
            {
                MessageUtils.format(LEAVING_COLONY_MESSAGE, this.getName()).sendTo(player);
            }

            final PlayerLeavingModEvent notifyPlayerLeavingModEvent = new PlayerLeavingModEvent(this, player);
            IMinecoloniesAPI.getInstance().getEventBus().post(notifyPlayerLeavingModEvent);

            if (notifyPlayerLeavingModEvent.shouldShowNotification())
            {
                MessageUtils.format(LEAVING_COLONY_MESSAGE_NOTIFY, player.getName()).sendTo(this, true).forManagers();
            }
        }
    }

    /**
     * Get the NBT NBTBase of the colony.
     *
     * @return the NBTBase of it.
     */
    @Override
    public NBTTagCompound getColonyTag()
    {
        try
        {
            if (this.colonyTag == null || this.isDirty)
            {
                this.write(new NBTTagCompound());
            }
        }
        catch (final Exception e)
        {
            Log.getLogger().warn("Something went wrong persisting colony: " + id, e);
        }
        return this.colonyTag;
    }

    /**
     * Is EntityPlayer part of a wave trying to invade the colony?
     *
     * @param EntityPlayer the EntityPlayer to check..
     * @return true if so.
     */
    public boolean isValidAttackingPlayer(final EntityPlayer player)
    {
        if (packageManager.getLastContactInHours() > 1)
        {
            return false;
        }

        for (final AttackingPlayer attackingPlayer : attackingPlayers)
        {
            if (attackingPlayer.getPlayer().equals(player))
            {
                return attackingPlayer.isValidAttack(this);
            }
        }
        return false;
    }

    /**
     * Check if attack of guard is valid.
     *
     * @param entity the guard entity.
     * @return true if so.
     */
    public boolean isValidAttackingGuard(final AbstractEntityCitizen entity)
    {
        if (packageManager.getLastContactInHours() > 1)
        {
            return false;
        }

        return AttackingPlayer.isValidAttack(entity, this);
    }

    /**
     * Add a guard to the list of attacking guards.
     *
     * @param IEntityCitizen the citizen to add.
     */
    public void addGuardToAttackers(final AbstractEntityCitizen IEntityCitizen, final EntityPlayer player)
    {
        if (player == null)
        {
            return;
        }

        for (final AttackingPlayer attackingPlayer : attackingPlayers)
        {
            if (attackingPlayer.getPlayer().equals(player))
            {
                if (attackingPlayer.addGuard(IEntityCitizen))
                {
                    MessageUtils.format(COLONY_ATTACK_GUARD_GROUP_SIZE_MESSAGE, attackingPlayer.getPlayer().getName(), attackingPlayer.getGuards().size())
                        .sendTo(this)
                        .forManagers();
                }
                return;
            }
        }

        for (final EntityPlayer visitingPlayer : visitingPlayers)
        {
            if (visitingPlayer.equals(player))
            {
                final AttackingPlayer attackingPlayer = new AttackingPlayer(visitingPlayer);
                attackingPlayer.addGuard(IEntityCitizen);
                attackingPlayers.add(attackingPlayer);
                MessageUtils.format(COLONY_ATTACK_START_MESSAGE, visitingPlayer.getName()).sendTo(this).forManagers();
            }
        }
    }

    /**
     * Check if the colony is currently under attack by another player.
     *
     * @return true if so.
     */
    public boolean isColonyUnderAttack()
    {
        return !attackingPlayers.isEmpty();
    }

    /**
     * Getter for the colony team color.
     *
     * @return the EnumChatFormatting enum color.
     */
    @Override
    public EnumChatFormatting getTeamColonyColor()
    {
        return colonyTeamColor;
    }

    /**
     * Getter for the colony flag patterns
     *
     * @return the list of pattern-color pairs
     */
    @Override
    public NBTTagList getColonyFlag()
    {
        return colonyFlag;
    }

    /**
     * Set the colony to be dirty.
     *
     * @param dirty if dirty.
     */
    public void setDirty(final boolean dirty)
    {
        this.isDirty = dirty;
    }

    /**
     * Save the time when mercenaries are used, to set a cooldown.
     */
    @Override
    public void usedMercenaries()
    {
        mercenaryLastUse = world.getTotalWorldTime();
        markDirty();
    }

    /**
     * Get the last time mercenaries were used.
     */
    @Override
    public long getMercenaryUseTime()
    {
        return mercenaryLastUse;
    }

    @Override
    public boolean useAdditionalChildTime(final int amount)
    {
        if (additionalChildTime < amount)
        {
            return false;
        }
        else
        {
            additionalChildTime -= amount;
            return true;
        }
    }

    @Override
    public void updateHasChilds()
    {
        for (ICitizenData data : this.getCitizenManager().getCitizens())
        {
            if (data.isChild())
            {
                this.hasChilds = true;
                return;
            }
        }
        this.hasChilds = false;
    }

    @Override
    public void addLoadedChunk(final long chunkCoord, final Chunk chunk)
    {
        if (world instanceof ServerLevel
            && getConfig().getServer().forceLoadColony.get())
        {
            if (this.forceLoadTimer > 0)
            {
                checkChunkAndRegisterTicket(ChunkCoordIntPair, chunk);
            }
            else if (buildingManager.keepChunkColonyLoaded(chunk))
            {
                this.pendingChunks.add(ChunkCoordIntPair);
            }
        }
        this.loadedChunks.put(ChunkCoordIntPair, ChunkCoordIntPair);
    }

    @Override
    public void removeLoadedChunk(final long chunkCoord)
    {
        loadedChunks.remove(ChunkCoordIntPair);
        pendingToUnloadChunks.remove(ChunkCoordIntPair);
    }

    @Override
    public int getLoadedChunkCount()
    {
        return loadedChunks.size();
    }

    @Override
    public Set<Long> getLoadedChunks()
    {
        return loadedChunks.keySet();
    }

    @Override
    public ColonyState getState()
    {
        return colonyStateMachine.getState();
    }

    @Override
    public boolean isActive()
    {
        return colonyStateMachine.getState() != INACTIVE;
    }

    @Override
    public boolean isDay()
    {
        return isDay;
    }

    @Override
    public Set<Long> getTicketedChunks()
    {
        return ticketedChunks;
    }

    @Override
    public void setTextureStyle(final String style)
    {
        this.textureStyle = style;
        this.markDirty();
    }

    @Override
    public String getTextureStyleId()
    {
        return this.textureStyle;
    }

    @Override
    public void setNameStyle(final String style)
    {
        this.nameStyle = style;
        this.markDirty();
    }

    @Override
    public String getNameStyle()
    {
        return this.nameStyle;
    }

    @Override
    public CitizenNameFile getCitizenNameFile()
    {
        return CitizenNameListener.nameFileMap.getOrDefault(nameStyle, CitizenNameListener.nameFileMap.get("default"));
    }

    /**
     * Check if we need to update the view's chunk ticket info
     *
     * @return true if dirty.
     */
    public boolean isTicketedChunksDirty()
    {
        return ticketedChunksDirty;
    }

    @Override
    public int getDay()
    {
        return day;
    }

    @Override
    public IQuestManager getQuestManager()
    {
        return questManager;
    }

    @Override
    public ICitizen getCitizen(final int id)
    {
        return citizenManager.getCivilian(id);
    }

    @Override
    public ISettingsModule getSettings()
    {
        return settingsModule;
    }

    /**
     * Sets the dimension ID, use with care!
     *
     * @param dimensionId
     */
    public void setDimensionId(final int /* ResourceKey */ dimensionId)
    {
        this.dimensionId = dimensionId;
    }
}





