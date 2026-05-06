package com.minecolonies.core.colony.buildings;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.modules.*;
import com.minecolonies.api.colony.buildings.modules.settings.ISetting;
import com.minecolonies.api.colony.buildings.modules.settings.ISettingKey;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.interactionhandling.ChatPriority;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.data.IRequestSystemBuildingDataStore;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.request.IRequest;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requestable.IDeliverable;
import com.minecolonies.api.colony.requestsystem.requestable.IRequestable;
import com.minecolonies.api.colony.requestsystem.requestable.deliveryman.Pickup;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.resolver.IRequestResolver;
import com.minecolonies.api.colony.requestsystem.resolver.player.IPlayerRequestResolver;
import com.minecolonies.api.colony.requestsystem.resolver.retrying.IRetryingRequestResolver;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.colony.workorders.IWorkOrder;
import com.minecolonies.api.colony.workorders.WorkOrderType;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.api.tileentities.MinecoloniesTileEntities;
import com.minecolonies.api.util.*;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.TypeConstants;
import com.minecolonies.api.util.constant.translation.RequestSystemTranslationConstants;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.colony.buildings.modules.AbstractAssignedCitizenModule;
import com.minecolonies.core.colony.buildings.modules.settings.BoolSetting;
import com.minecolonies.core.colony.buildings.modules.settings.SettingKey;
import com.minecolonies.core.colony.interactionhandling.RequestBasedInteraction;
import com.minecolonies.core.colony.jobs.AbstractJobCrafter;
import com.minecolonies.core.colony.requestsystem.management.IStandardRequestManager;
import com.minecolonies.core.colony.requestsystem.requesters.BuildingBasedRequester;
import com.minecolonies.core.colony.requestsystem.requests.StandardRequests;
import com.minecolonies.core.colony.workorders.WorkOrderBuilding;
import com.minecolonies.core.entity.ai.workers.service.EntityAIWorkDeliveryman;
import com.minecolonies.core.entity.ai.workers.util.ConstructionTapeHelper;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import com.minecolonies.core.util.ChunkDataHelper;
import com.minecolonies.core.util.SchemAnalyzerUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
import com.minecolonies.api.util.Tuple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] World.block.AirBlock removed
import net.minecraft.block.Block;
// [1.7.10] block.entity removed
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
// [1.7.10] capabilities removed
// [1.7.10] capabilities removed
// [1.7.10] LazyOptional removed
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.BuildingConstants.CONST_DEFAULT_MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.BuildingConstants.NO_WORK_ORDER;
import static com.minecolonies.api.util.constant.Constants.MOD_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;
import static com.minecolonies.api.util.constant.Suppression.GENERIC_WILDCARD;
import static com.minecolonies.api.util.constant.Suppression.UNCHECKED;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Base building class, has all the foundation for what a building stores and does.
 * <p>
 * We suppress the warning which warns you about referencing child classes in the parent because that's how we register the instances of the childClasses to their views and
 * blocks.
 */
@SuppressWarnings({"squid:S2390", "PMD.ExcessiveClassLength"})
public abstract class AbstractBuilding extends AbstractBuildingContainer
{
    /**
     * Breeding setting.
     */
    public static final ISettingKey<BoolSetting> BREEDING = new SettingKey<>(BoolSetting.class, new ResourceLocation(MOD_ID, "breeding"));

    public static final ISettingKey<BoolSetting> USE_SHEARS = new SettingKey<>(BoolSetting.class, new ResourceLocation(Constants.MOD_ID, "useshears"));

    /**
     * Best possible standing pos score.
     */
    private static final int BEST_STANDING_SCORE = 10;

    /**
     * The data store id for request system related data.
     */
    private IToken<?> rsDataStoreToken;

    /**
     * The ID of the building. Needed in the request system to identify it.
     */
    private IRequester requester;

    /**
     * If the building has been built already.
     */
    private boolean isBuilt = false;

    /**
     * The custom name of the building, empty by default.
     */
    private String customName = "";

    /**
     * Whether a guard building is near
     */
    private boolean guardBuildingNear = false;

    /**
     * Whether we need to recheck if a guard building is near
     */
    private boolean recheckGuardBuildingNear = true;

    /**
     * Made to check if the building has to update the server/client.
     */
    private boolean dirty = false;

    /**
     * Set of building modules this building has.
     */
    protected List<IBuildingModule>                  modules    = new ArrayList<>();
    protected Int2ObjectOpenHashMap<IBuildingModule> modulesMap = new Int2ObjectOpenHashMap<>();

    /**
     * Day the next pickup should happen.
     */
    public int pickUpDay = -1;

    /**
     * Cached position for citizen standing position next to hut block.
     */
    private int[] cachedStandingPosition;

    /**
     * Prestige value of this building (dominated by schematic complexity).
     */
    private int prestige;

    /**
     * Constructor for a AbstractBuilding.
     *
     * @param colony Colony the building belongs to.
     * @param pos    Location of the building (it's Hut Block).
     */
    protected AbstractBuilding(@NotNull final IColony colony, final int[] pos)
    {
        super(pos, colony);

        this.requester = StandardFactoryController.getInstance().getNewInstance(TypeToken.of(BuildingBasedRequester.class), this);
        setupRsDataStore();
    }

    @Override
    @NotNull
    public List<IBuildingModule> getModules()
    {
        return modules;
    }

    @Override
    @NotNull
    public Class<IBuildingModule> getClassType()
    {
        return IBuildingModule.class;
    }

    @Override
    public boolean hasModule(final BuildingEntry.ModuleProducer<?, ?> producer)
    {
        return modulesMap.containsKey(producer.getRuntimeID());
    }

    @Override
    public <M extends IBuildingModule, V extends IBuildingModuleView> M getModule(final BuildingEntry.ModuleProducer<M,V> producer)
    {
        return (M) modulesMap.get(producer.getRuntimeID());
    }

    @Override
    public IBuildingModule getModule(final int id)
    {
        return modulesMap.get(id);
    }

    @Override
    public void registerModule(@NotNull final IBuildingModule module)
    {
        if (modulesMap.containsKey(module.getProducer().getRuntimeID()))
        {
            Log.getLogger().error("Trying to register same module twice!"+module.getProducer().key, new RuntimeException());
            return;
        }
        modulesMap.put(module.getProducer().getRuntimeID(),module);
        this.modules.add(module);
    }

    /**
     * Getter for the custom name of a building.
     *
     * @return the custom name.
     */
    @Override
    @NotNull
    public String getCustomName()
    {
        return this.customName;
    }

    /**
     * Getter for the custom name of a building.
     * Returns either the custom name (if any) or the translation key.
     *
     * @return the custom name or the schematic name.
     */
    @Override
    @NotNull
    public String getBuildingDisplayName()
    {
        if (customName.isEmpty())
        {
            return getBuildingType().getTranslationKey();
        }
        return this.customName;
    }

    /**
     * Executed when a new day start.
     */
    @Override
    public void onWakeUp()
    {
        getModulesByType(IBuildingEventsModule.class).forEach(IBuildingEventsModule::onWakeUp);
    }

    /**
     * Executed every time when citizen finish inventory cleanup called after citizen got paused. Use for cleaning a state only.
     */
    @Override
    public void onCleanUp(final ICitizenData citizen)
    {
        /*
         * Buildings override this if required.
         */
    }

    /**
     * Executed when RestartCitizenMessage is called and worker is paused. Use for reseting, onCleanUp is called before this
     */
    @Override
    public void onRestart(final ICitizenData citizen)
    {
        // Unpause citizen
        citizen.setPaused(false);
        /*
         * Buildings override this if required.
         */
    }

    @Override
    public void onPlayerEnterBuilding(final EntityPlayer EntityPlayer)
    {
        getModulesByType(IBuildingEventsModule.class).forEach(module -> module.onPlayerEnterBuilding(EntityPlayer));
    }

    @Override
    public void onPlayerEnterNearby(final EntityPlayer EntityPlayer)
    {
        if (getBuildingLevel() == 0 || getSchematicName() == null || getSchematicName().isEmpty())
        {
            return;
        }

        if (isInBuilding(new int[]{(int)EntityPlayer.posX, (int)EntityPlayer.posY, (int)EntityPlayer.posZ}))
        {
            onPlayerEnterBuilding(EntityPlayer);
        }
    }

    /**
     * On setting down the building.
     */
    @Override
    public void onPlacement()
    {
        if (getBuildingLevel() == 0 && !hasParent())
        {
            ChunkDataHelper.claimBuildingChunks(colony, true, getPosition()[0], getPosition()[1], getPosition()[2], getClaimRadius(getBuildingLevel()), getCorners());
        }
    }

    /**
     * Checks if a block matches the current object.
     *
     * @param block Block you want to know whether it matches this class or not.
     * @return True if the block matches this class, otherwise false.
     */
    @Override
    public boolean isMatchingBlock(@NotNull final Block block)
    {
        return this.getBuildingType().getBuildingBlock() == block;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound compound)
    {
        super.deserializeNBT(compound);
        loadRequestSystemFromNBT(compound);
        if (compound.hasKey(TAG_IS_BUILT))
        {
            isBuilt = compound.getBoolean(TAG_IS_BUILT);
        }
        else if (getBuildingLevel() > 0)
        {
            isBuilt = true;
        }
        if (compound.hasKey(TAG_CUSTOM_NAME))
        {
            this.customName = compound.getString(TAG_CUSTOM_NAME);
        }
        if (compound.hasKey(TAG_PRESTIGE))
        {
            this.prestige = compound.getInteger(TAG_PRESTIGE);
        }

        if (compound.hasKey(TAG_BUILDING_MODULES))
        {
            for (IPersistentModule module : getModulesByType(IPersistentModule.class))
            {
                if (compound.getCompoundTag(TAG_BUILDING_MODULES).hasKey(module.getProducer().key))
                {
                    module.deserializeNBT(compound.getCompoundTag(TAG_BUILDING_MODULES).getCompoundTag(module.getProducer().key));
                }
                else
                {
                    module.deserializeNBT(compound);
                }
            }
        }
        else
        {
            getModulesByType(IPersistentModule.class).forEach(module -> module.deserializeNBT(compound));
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compound = super.serializeNBT();
        final NBTTagList list = new NBTTagList();
        for (final IRequestResolver<?> requestResolver : getResolvers())
        {
            list.appendTag(StandardFactoryController.getInstance().serialize(requestResolver.getId()));
        }
        compound.setTag(TAG_RESOLVER, list);
        compound.setString(TAG_BUILDING_TYPE, this.getBuildingType().getRegistryName().toString());
        writeRequestSystemToNBT(compound);
        compound.setBoolean(TAG_IS_BUILT, isBuilt);
        compound.setString(TAG_CUSTOM_NAME, customName);
        compound.setInteger(TAG_PRESTIGE, prestige);

        NBTTagCompound modules = new NBTTagCompound();
        for (IPersistentModule module : getModulesByType(IPersistentModule.class))
        {
            final NBTTagCompound NBTBase = new NBTTagCompound();
            module.serializeNBT(NBTBase);
            modules.setTag(module.getProducer().key, NBTBase);
        }

        compound.setTag(TAG_BUILDING_MODULES, modules);
        return compound;
    }

    /**
     * Destroys the block. Calls {@link #onDestroyed()}.
     */
    @Override
    public void destroy()
    {
        onDestroyed();
        colony.getServerBuildingManager().removeBuilding(this, colony.getPackageManager().getCloseSubscribers());
        colony.getRequestManager().getDataStoreManager().remove(this.rsDataStoreToken);

        for (final int[] childpos : getChildren())
        {
            final IBuilding building = colony.getServerBuildingManager().getBuilding(childpos);
            if (building != null)
            {
                building.destroy();
            }
        }
    }

    @Override
    public void onDestroyed()
    {
        final AbstractTileEntityColonyBuilding tileEntityNew = this.getTileEntity();
        final World world = colony.getWorld();
        final Block block = world.getBlock(this.getPosition()[0], this.getPosition()[1], this.getPosition()[2]);

        if (tileEntityNew != null)
        {
            world.notifyBlocksOfNeighborChange(this.getPosition()[0], this.getPosition()[1], this.getPosition()[2], block);
        }

        ChunkDataHelper.claimBuildingChunks(colony, false, this.getID()[0], this.getID()[1], this.getID()[2], getClaimRadius(getBuildingLevel()), getCorners());
        ConstructionTapeHelper.removeConstructionTape(getCorners(), world);

        getModulesByType(IBuildingEventsModule.class).forEach(IBuildingEventsModule::onDestroyed);
    }

    /**
     * Adds work orders to the {@link Colony#getWorkManager()}.
     *
     * @param type    what to do with the work order
     * @param builder the assigned builder.
     */
    protected void requestWorkOrder(WorkOrderType type, final int[] builder)
    {
        for (@NotNull final WorkOrderBuilding o : colony.getWorkManager().getWorkOrdersOfType(WorkOrderBuilding.class))
        {
            if (o.getLocation().equals(getID()))
            {
                return;
            }
        }

        WorkOrderBuilding workOrder = WorkOrderBuilding.create(type, this);
        if (type == WorkOrderType.REMOVE && !canDeconstruct())
        {
            MessageUtils.format(BUILDER_CANNOT_DECONSTRUCT).sendTo(colony).forAllPlayers();
            return;
        }


        if (type != WorkOrderType.REMOVE &&
              !canBeBuiltByBuilder(workOrder.getTargetLevel()) &&
              !workOrder.canBeResolved(colony, workOrder.getTargetLevel()))
        {
            MessageUtils.format(BUILDER_NECESSARY, Integer.toString(workOrder.getTargetLevel())).sendTo(colony).forAllPlayers();
            return;
        }

        if (workOrder.tooFarFromAnyBuilder(colony, workOrder.getTargetLevel()) &&
              builder.equals(new int[]{0,0,0}))
        {
            MessageUtils.format(BUILDER_TOO_FAR_AWAY).sendTo(colony).forAllPlayers();
            return;
        }

        final int min = 0; // [1.7.10] world.getMinBuildHeight() doesn't exist; 0 is the minimum in 1.7.10
        final int max = 256; // [1.7.10] world.getMaxBuildHeight() doesn't exist; 256 is the max in 1.7.10
        if (getCorners().getA()[1] >= max || getCorners().getB()[1] >= max)
        {
            MessageUtils.format(BUILDER_BUILDING_TOO_HIGH, max).sendTo(colony).forAllPlayers();
            return;
        }
        else if (getPosition()[1] <= min)
        {
            MessageUtils.format(BUILDER_BUILDING_TOO_LOW, min).sendTo(colony).forAllPlayers();
            return;
        }

        if (!builder.equals(new int[]{0,0,0}))
        {
            final IBuilding building = colony.getServerBuildingManager().getBuilding(builder);
            if (building instanceof AbstractBuildingStructureBuilder &&
                  (building.getBuildingLevel() >= workOrder.getTargetLevel() || canBeBuiltByBuilder(workOrder.getTargetLevel())))
            {
                workOrder.setClaimedBy(builder);
            }
            else
            {
                MessageUtils.format(BUILDER_NECESSARY, Integer.toString(workOrder.getTargetLevel())).sendTo(colony).forAllPlayers();
                return;
            }
        }

        colony.getWorkManager().addWorkOrder(workOrder, false);
        workOrder.loadBlueprint(colony.getWorld(), b -> {});

        if (workOrder.getID() != 0)
        {
            MessageUtils.format(WORK_ORDER_CREATED,
                workOrder.getDisplayName(),
                colony.getName(),
                workOrder.getLocation()[0],
                workOrder.getLocation()[1],
                workOrder.getLocation()[2])
              .sendTo(colony).forAllPlayers();
        }
        markDirty();
    }

    /**
     * Check if this particular building can be deconstructed.
     *
     * @return true if so.
     */
    public boolean canDeconstruct()
    {
        return true;
    }

    /**
     * Method to define if a builder can build this although the builder is not World 1 yet.
     *
     * @return true if so.
     */
    @Override
    public boolean canBeBuiltByBuilder(final int newLevel)
    {
        return false;
    }

    @Override
    public final void markDirty()
    {
        dirty = true;
        if (colony != null)
        {
            colony.getServerBuildingManager().markBuildingsDirty();
        }
    }

    @Override
    public final boolean isDirty()
    {
        for (final IBuildingModule module : modules)
        {
            if (module.checkDirty())
            {
                return true;
            }
        }
        return dirty;
    }

    @Override
    public final void clearDirty()
    {
        dirty = false;
        for (final IBuildingModule module : modules)
        {
            module.clearDirty();
        }
    }

    @Override
    public void processOfflineTime(final long time)
    {
        // Do nothing.
    }

    /**
     * Checks if this building have a work order.
     *
     * @return true if the building is building, upgrading or repairing.
     */
    @Override
    public boolean isPendingConstruction()
    {
        return getCurrentWorkOrderLevel() != NO_WORK_ORDER;
    }

    /**
     * Get the current World of the work order.
     *
     * @return NO_WORK_ORDER if not current work otherwise the World requested.
     */
    private int getCurrentWorkOrderLevel()
    {
        return colony.getWorkManager().getWorkOrdersOfType(WorkOrderBuilding.class)
          .stream()
          .filter(f -> f.getLocation().equals(getID()))
          .map(IWorkOrder::getTargetLevel)
          .findFirst()
          .orElse(NO_WORK_ORDER);
    }

    /**
     * Remove the work order for the building.
     * <p>
     * Remove either the upgrade or repair work order
     */
    @Override
    public void removeWorkOrder()
    {
        for (@NotNull final WorkOrderBuilding o : colony.getWorkManager().getWorkOrdersOfType(WorkOrderBuilding.class))
        {
            if (o.getLocation().equals(getID()))
            {
                colony.getWorkManager().removeWorkOrder(o.getID());
                markDirty();

                final int[] buildingPos = o.getClaimedBy();
                final IBuilding building = colony.getServerBuildingManager().getBuilding(buildingPos);
                if (building != null)
                {
                    for (final AbstractAssignedCitizenModule module : building.getModulesByType(AbstractAssignedCitizenModule.class))
                    {
                        for (final ICitizenData citizen : module.getAssignedCitizen())
                        {
                            building.cancelAllRequestsOfCitizenOrBuilding(citizen);
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    public Set<ICitizenData> getAllAssignedCitizen()
    {
        final Set<ICitizenData> citizens = new HashSet<>();
        for (final IAssignsCitizen module : getModulesByType(IAssignsCitizen.class))
        {
            citizens.addAll(module.getAssignedCitizen());
        }
        return citizens;
    }

    /**
     * Method to calculate the radius to be claimed by this building depending on the World.
     *
     * @param newLevel the new World of the building.
     * @return the radius.
     */
    @Override
    public int getClaimRadius(final int newLevel)
    {
        switch (newLevel)
        {
            case 1:
            case 2:
            case 3:
                return 1;
            case 4:
            case 5:
                return 2;
            default:
                return 0;
        }
    }

    /**
     * Serializes to view.
     *
     * @param buf PacketBuffer to write to.
     */
    @Override
    public void serializeToView(@NotNull final PacketBuffer buf, final boolean fullSync)
    {
        try {
        buf.writeStringToBuffer(this.getBuildingType().getRegistryName().toString());
        buf.writeInt(getBuildingLevel());
        buf.writeInt(getMaxBuildingLevel());
        buf.writeInt(getPickUpPriority());
        buf.writeInt(getCurrentWorkOrderLevel());
        buf.writeStringToBuffer(getStructurePack());
        buf.writeStringToBuffer(getBlueprintPath());
        final int[] parent = getParent();
        buf.writeInt(parent[0]); buf.writeInt(parent[1]); buf.writeInt(parent[2]);
        buf.writeStringToBuffer(this.customName);
        buf.writeInt(this.prestige);

        buf.writeInt(getRotation());
        buf.writeBoolean(isMirrored());
        buf.writeInt(getClaimRadius(getBuildingLevel()));

        final NBTTagCompound requestSystemCompound = new NBTTagCompound();
        writeRequestSystemToNBT(requestSystemCompound);

        final ImmutableCollection<IRequestResolver<?>> resolvers = getResolvers();
        buf.writeInt(resolvers.size());
        for (final IRequestResolver<?> resolver : resolvers)
        {
            buf.writeNBTTagCompoundToBuffer(StandardFactoryController.getInstance().serialize(resolver.getId()));
        }
        buf.writeNBTTagCompoundToBuffer(StandardFactoryController.getInstance().serialize(getId()));
        buf.writeInt(containerList.size());
        for (int[] blockPos : containerList)
        {
            buf.writeInt(blockPos[0]); buf.writeInt(blockPos[1]); buf.writeInt(blockPos[2]);
        }
        buf.writeNBTTagCompoundToBuffer(requestSystemCompound);

        buf.writeBoolean(isDeconstructed());
        buf.writeBoolean(canAssignCitizens());

        final List<IBuildingModule> syncedModules = new ArrayList<>();
        for(final IBuildingModule module:modules)
        {
            if (module.getProducer().hasView())
            {
                syncedModules.add(module);
            }
        }

        buf.writeInt(syncedModules.size());
        for (final IBuildingModule module: syncedModules)
        {
            buf.writeInt(module.getProducer().getRuntimeID());
            module.serializeToView(buf, fullSync);
        }
        } catch (java.io.IOException e) { throw new RuntimeException(e); }
    }

    /**
     * Regularly tick this building and check if we  got the minimum stock(like once a minute is still fine) - If not: Check if there is a request for this already. -- If not:
     * Create a request. - If so: Check if there is a request for this still. -- If so: cancel it.
     */
    @Override
    public void onColonyTick(final IColony colony)
    {
        getModulesByType(ITickingModule.class).forEach(module -> module.onColonyTick(colony));
        super.onColonyTick(colony);
    }

    /**
     * Set the custom building name of the building.
     *
     * @param name the name to set.
     */
    @Override
    public void setCustomBuildingName(final String name)
    {
        this.customName = name;
        this.markDirty();

        this.colony.getWorkManager().getWorkOrders().values().stream()
          .filter(f -> f instanceof WorkOrderBuilding)
          .map(m -> (WorkOrderBuilding) m)
          .filter(f -> f.getLocation().equals(this.getID()) || this.getChildren().contains(f.getLocation()))
          .forEach(f -> {
              IBuilding building = this.colony.getServerBuildingManager().getBuilding(f.getLocation());
              if (building != null)
              {
                  f.setCustomName(building);
                  this.colony.getWorkManager().setDirty(true);
              }
          });
    }

    /**
     * Check if the building should be gathered by the dman.
     *
     * @return true if so.
     */
    @Override
    public boolean canBeGathered()
    {
        return true;
    }

    /**
     * Requests an upgrade for the current building.
     *
     * @param EntityPlayer  the requesting EntityPlayer.
     * @param builder the assigned builder.
     */
    @Override
    public void requestUpgrade(final EntityPlayer EntityPlayer, final int[] builder)
    {
        final ResourceLocation hutResearch = colony.getResearchManager().getResearchEffectIdFrom(this.getBuildingType().getBuildingBlock());

        if (MinecoloniesAPIProxy.getInstance().getGlobalResearchTree().hasResearchEffect(hutResearch) &&
              colony.getResearchManager().getResearchEffects().getEffectStrength(hutResearch) < 1)
        {
            MessageUtils.format(WARNING_BUILDING_REQUIRES_RESEARCH_UNLOCK).sendTo(EntityPlayer);
            return;
        }
        if (MinecoloniesAPIProxy.getInstance().getGlobalResearchTree().hasResearchEffect(hutResearch) &&
              (colony.getResearchManager().getResearchEffects().getEffectStrength(hutResearch) <= getBuildingLevel()))
        {
            MessageUtils.format(WARNING_BUILDING_REQUIRES_RESEARCH_UPGRADE).sendTo(EntityPlayer);
            return;
        }

        final IBuilding parentBuilding = colony.getServerBuildingManager().getBuilding(getParent());

        if (getBuildingLevel() == 0 && (parentBuilding == null || parentBuilding.getBuildingLevel() > 0))
        {
            requestWorkOrder(WorkOrderType.BUILD, builder);
        }
        else if (getBuildingLevel() < getMaxBuildingLevel() && (parentBuilding == null || getBuildingLevel() < parentBuilding.getBuildingLevel() || parentBuilding.getBuildingLevel() >= parentBuilding.getMaxBuildingLevel()))
        {
            requestWorkOrder(WorkOrderType.UPGRADE, builder);
        }
        else
        {
            MessageUtils.format(WARNING_NO_UPGRADE).sendTo(EntityPlayer);
        }
    }

    @Override
    public void requestRemoval(final EntityPlayer EntityPlayer, final int[] builder)
    {
        if (this.isDeconstructed())
        {
            pickUp(EntityPlayer);
        }
        else
        {
            requestWorkOrder(WorkOrderType.REMOVE, builder);
        }
    }

    @Override
    public void pickUp(final EntityPlayer EntityPlayer)
    {
        if (hasParent())
        {
            MessageUtils.format(WARNING_BUILDING_PICKUP_DENIED).sendTo(EntityPlayer);
            return;
        }

        final ItemStack stack = new ItemStack(colony.getWorld().getBlock(getPosition()[0], getPosition()[1], getPosition()[2]), 1);
        final NBTTagCompound compoundNBT = new NBTTagCompound();
        compoundNBT.setInteger(TAG_COLONY_ID, this.getColony().getID());
        compoundNBT.setInteger(TAG_OTHER_LEVEL, this.getBuildingLevel());
        stack.setTagCompound(compoundNBT);
        if (InventoryUtils.addItemStackToProvider((net.minecraft.inventory.IInventory)EntityPlayer, stack))
        {
            this.destroy();
            colony.getWorld().setBlockToAir(this.getPosition()[0], this.getPosition()[1], this.getPosition()[2]);
        }
        else
        {
            MessageUtils.format(WARNING_BUILDING_PICKUP_PLAYER_INVENTORY_FULL).sendTo(EntityPlayer);
        }
    }

    /**
     * Requests a repair for the current building.
     *
     * @param builder the assigned builder.
     */
    @Override
    public void requestRepair(final int[] builder)
    {
        if (getBuildingLevel() > 0)
        {
            requestWorkOrder(WorkOrderType.REPAIR, builder);
        }
    }

    /**
     * Check if the building was built already.
     *
     * @return true if so.
     */
    @Override
    public boolean isBuilt()
    {
        return isBuilt;
    }

    /**
     * Deconstruct the building on destroyed.
     */
    @Override
    public void deconstruct()
    {
        final Tuple<int[], int[]> tuple = getCorners();
        for (int x = tuple.getA()[0]; x < tuple.getB()[0]; x++)
        {
            for (int z = tuple.getA()[2]; z < tuple.getB()[2]; z++)
            {
                for (int y = tuple.getA()[1]; y < tuple.getB()[1]; y++)
                {
                    colony.getWorld().setBlockToAir(x, y, z);
                }
            }
        }
    }

    @Override
    public AbstractTileEntityColonyBuilding getTileEntity()
    {
        if (tileEntity != null && tileEntity.isInvalid())
        {
            tileEntity = null;
        }

        if ((tileEntity == null)
              && colony != null
              && colony.getWorld() != null
              && getPosition() != null
              && WorldUtil.isBlockLoaded(colony.getWorld(), getPosition()[0], getPosition()[2])
              && !(colony.getWorld().getBlock(getPosition()[0], getPosition()[1], getPosition()[2]).isAir(colony.getWorld(), getPosition()[0], getPosition()[1], getPosition()[2]))
              && colony.getWorld().getBlock(getPosition()[0], getPosition()[1], getPosition()[2]) instanceof AbstractColonyBlock)
        {
            final net.minecraft.tileentity.TileEntity te = colony.getWorld().getTileEntity(getPosition()[0], getPosition()[1], getPosition()[2]);
            if (te instanceof TileEntityColonyBuilding)
            {
                tileEntity = (TileEntityColonyBuilding) te;
                if (tileEntity.getBuilding() == null)
                {
                    tileEntity.setColony(colony);
                    tileEntity.setBuilding(this);
                }
            }
            else
            {
                Log.getLogger().error("Somehow the wrong TileEntity is at the location where the building should be!", new Exception());
                Log.getLogger().error("Trying to restore order!");

                // [1.7.10] Cannot easily create new TileEntity and setBlockEntity; skip restoration
            }
        }

        return tileEntity;
    }

    @Override
    public void onUpgradeComplete(@Nullable final Blueprint blueprint, final int newLevel)
    {
        cachedRotation = -1;
        if (!hasParent())
        {
            ChunkDataHelper.claimBuildingChunks(colony, true, this.getID()[0], this.getID()[1], this.getID()[2], this.getClaimRadius(newLevel), getCorners());
        }
        recheckGuardBuildingNear = true;

        ConstructionTapeHelper.removeConstructionTape(getCorners(), colony.getWorld());
        calculateCorners();
        this.isBuilt = true;

        final List<IToken<?>> playerRequests = colony.getRequestManager().getPlayerResolver().getAllAssignedRequests();
        final List<IToken<?>> retryingRequests = colony.getRequestManager().getRetryingRequestResolver().getAllAssignedRequests();

        for (final Collection<IToken<?>> requestList : new ArrayList<>(getOpenRequestsByCitizen().values()))
        {
            for (final IToken<?> requestToken : new ArrayList<>(requestList))
            {
                final IRequest<?> request = colony.getRequestManager().getRequestForToken(requestToken);
                if (request instanceof StandardRequests.ToolRequest && isRequestStuck(request, playerRequests, retryingRequests))
                {
                    colony.getRequestManager().updateRequestState(requestToken, RequestState.CANCELLED);
                }
            }
        }

        getModulesByType(IBuildingEventsModule.class).forEach(module -> module.onUpgradeComplete(newLevel));
        colony.getResearchManager().checkAutoStartResearch();
        colony.getServerBuildingManager().onBuildingUpgradeComplete(this, newLevel);
        cachedStandingPosition = null;

        if (blueprint != null)
        {
           calculatePrestige(blueprint);
        }
    }

    @Override
    public void calculatePrestige(final Blueprint blueprint)
    {
        if (getBuildingLevel() > 0)
        {
            final int new_prestige = SchemAnalyzerUtil.analyzeSchematic(blueprint).costScore;
            if (new_prestige != prestige)
            {
                prestige = new_prestige;
                markDirty();
            }
        }
        colony.getServerBuildingManager().clearPendingPrestigeCalc(this);
    }

    @Override
    public void calculateCorners()
    {
        // [1.7.10] StructurePacks/Blueprint/IBlueprintDataProviderBE not available in 1.7.10
        // Use position as both corners as a stub - real corners unknown without blueprint data
        setCorners(getPosition(), getPosition());
    }

    @Override
    public boolean isGuardBuildingNear()
    {
        if (recheckGuardBuildingNear)
        {
            guardBuildingNear = colony.getServerBuildingManager().hasGuardBuildingNear(this);
            recheckGuardBuildingNear = false;
        }
        return guardBuildingNear;
    }

    @Override
    public void resetGuardBuildingNear()
    {
        this.recheckGuardBuildingNear = true;
    }

    //------------------------- Starting Required Tools/Item handling -------------------------//

    @Override
    public int buildingRequiresCertainAmountOfItem(final ItemStack stack, final List<ItemStorage> localAlreadyKept, final boolean inventory, final JobEntry jobEntry)
    {
        for (final Map.Entry<Predicate<ItemStack>, Tuple<Integer, Boolean>> entry : getRequiredItemsAndAmount().entrySet())
        {
            if (inventory && !entry.getValue().getB())
            {
                continue;
            }

            if (entry.getKey().test(stack))
            {
                final ItemStorage kept = ItemStorage.getItemStackOfListMatchingPredicate(localAlreadyKept, entry.getKey());
                final int toKeep = entry.getValue().getA();
                int rest = stack.stackSize - toKeep;
                if (kept != null)
                {
                    if (kept.getAmount() >= toKeep && !ItemStackUtils.isBetterEquipment(stack, kept.getItemStack()))
                    {
                        return stack.stackSize;
                    }

                    rest = kept.getAmount() + stack.stackSize - toKeep;

                    localAlreadyKept.remove(kept);
                    kept.setAmount(kept.getAmount() + ItemStackUtils.getSize(stack) - Math.max(0, rest));
                    localAlreadyKept.add(kept);
                }
                else
                {
                    final ItemStorage newStorage = new ItemStorage(stack);
                    newStorage.setAmount(ItemStackUtils.getSize(stack) - Math.max(0, rest));
                    localAlreadyKept.add(newStorage);
                }

                if (rest <= 0)
                {
                    return 0;
                }

                return Math.min(rest, ItemStackUtils.getSize(stack));
            }
        }
        return stack.stackSize;
    }

    /**
     * Override this method if you want to keep an amount of items in inventory. When the inventory is full, everything get's dumped into the building chest. But you can use this
     * method to hold some stacks back.
     *
     * @return a list of objects which should be kept.
     */
    @Override
    public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
    {
        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> toKeep = new HashMap<>(keepX);

        final Map<ItemStorage, Tuple<Integer, Boolean>> requiredItems = new HashMap<>();
        final Collection<IRequestResolver<?>> resolvers = getResolvers();
        for (final IRequestResolver<?> resolver : resolvers)
        {
            final IStandardRequestManager requestManager = (IStandardRequestManager) colony.getRequestManager();
            final List<IRequest<? extends IDeliverable>> deliverableRequests =
              requestManager.getRequestHandler().getRequestsMadeByRequester(resolver)
                .stream()
                .filter(iRequest -> iRequest.getRequest() instanceof IDeliverable)
                .map(iRequest -> (IRequest<? extends IDeliverable>) iRequest)
                .collect(Collectors.toList());
            for (IRequest<? extends IDeliverable> request : deliverableRequests)
            {
                for (ItemStack item : request.getDeliveries())
                {
                    final ItemStorage output = new ItemStorage(item);
                    int amount = output.getAmount();
                    if (requiredItems.containsKey(output))
                    {
                        amount += requiredItems.get(output).getA();
                    }
                    requiredItems.put(output, new Tuple<>(amount, false));
                }
            }
        }
        toKeep.putAll(requiredItems.entrySet().stream()
          .collect(Collectors.toMap(key -> (stack -> ItemStackUtils.compareItemStacksIgnoreStackSize(stack, key.getKey().getItemStack())), Map.Entry::getValue)));

        if (keepFood())
        {
            toKeep.put(stack -> FoodUtils.canEat(stack, null, this), new Tuple<>(getBuildingLevel() * 2, true));
        }
        for (final IHasRequiredItemsModule module : getModulesByType(IHasRequiredItemsModule.class))
        {
            toKeep.putAll(module.getRequiredItemsAndAmount());
        }

        getModulesByType(IAltersRequiredItems.class).forEach(module -> module.alterItemsToBeKept((stack, qty, inv) -> toKeep.put(stack, new Tuple<>(qty, inv))));
        return toKeep;
    }

    @Override
    public int getPrestige()
    {
        return prestige;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public List<net.minecraftforge.items.IItemHandler> getHandlers()
    {
        if (this.getAllAssignedCitizen().isEmpty() || colony == null || colony.getWorld() == null)
        {
            return Collections.emptyList();
        }

        final Set<net.minecraftforge.items.IItemHandler> handlers = new HashSet<>();
        for (final ICitizenData workerEntity : this.getAllAssignedCitizen())
        {
            handlers.add(workerEntity.getInventory());
        }

        // [1.7.10] Capabilities/BlockEntity not available; skip block entity handler
        return ImmutableList.copyOf(handlers);
    }

    @Override
    public <T extends ISetting<S>, S> T getSetting(@NotNull final ISettingKey<T> key)
    {
        return getFirstModuleOccurance(ISettingsModule.class).getSetting(key);
    }

    @Override
    public <T extends ISetting<S>, S> S getSettingValueOrDefault(@NotNull final ISettingKey<T> key, @NotNull final S def)
    {
        return getFirstModuleOccurance(ISettingsModule.class).getSettingValueOrDefault(key, def);
    }


    /**
     * Get the right module for the recipe.
     *
     * @param token the recipe trying to be fulfilled.
     * @return the matching module.
     */
    public ICraftingBuildingModule getCraftingModuleForRecipe(final IToken<?> token)
    {
        for (final ICraftingBuildingModule module : getModulesByType(ICraftingBuildingModule.class))
        {
            if (module.holdsRecipe(token))
            {
                return module;
            }
        }
        return null;
    }

    /**
     * If the worker should keep some food in the inventory/building.
     *
     * @return true if so.
     */
    protected boolean keepFood()
    {
        return true;
    }

    /**
     * Try to transfer a stack to one of the inventories of the building and force the transfer.
     *
     * @param stack the stack to transfer.
     * @param world the world to do it in.
     * @return the itemStack which has been replaced or the itemStack which could not be transfered
     */
    @Override
    @Nullable
    public ItemStack forceTransferStack(final ItemStack stack, final World world)
    {
        if (getTileEntity() == null)
        {
            for (final int[] pos : containerList)
            {
                final net.minecraft.tileentity.TileEntity tempTileEntity = world.getTileEntity(pos[0], pos[1], pos[2]);
                if (tempTileEntity instanceof net.minecraft.tileentity.TileEntityChest && !InventoryUtils.isProviderFull((net.minecraft.inventory.IInventory) tempTileEntity))
                {
                    return forceItemStackToProvider((net.minecraft.inventory.IInventory) tempTileEntity, stack);
                }
            }
        }
        else
        {
            return forceItemStackToProvider((net.minecraft.inventory.IInventory) getTileEntity(), stack);
        }
        return stack;
    }

    @Nullable
    private ItemStack forceItemStackToProvider(@NotNull final net.minecraft.inventory.IInventory provider, @NotNull final ItemStack itemStack)
    {
        final List<ItemStorage> localAlreadyKept = new ArrayList<>();
        return InventoryUtils.forceItemStackToProvider(provider,
          itemStack,
          (ItemStack stack) -> EntityAIWorkDeliveryman.workerRequiresItem(this, stack, localAlreadyKept) != stack.stackSize);
    }

    //------------------------- Ending Required Tools/Item handling -------------------------//

    //------------------------- !START! RequestSystem handling for minecolonies buildings -------------------------//

    @Override
    public boolean isItemStackInRequest(@Nullable final ItemStack stack)
    {
        if (ItemStackUtils.isEmpty(stack))
        {
            return false;
        }

        for (final AbstractAssignedCitizenModule module : getModulesByType(AbstractAssignedCitizenModule.class))
        {
            for (final ICitizenData citizen : module.getAssignedCitizen())
            {
                for (final IRequest<?> request : getOpenRequests(citizen.getId()))
                {
                    for (final ItemStack deliveryStack : request.getDeliveries())
                    {
                        if (ItemStackUtils.compareItemStacksIgnoreStackSize(deliveryStack, stack, false, true))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected void writeRequestSystemToNBT(final NBTTagCompound compound)
    {
        compound.setTag(TAG_REQUESTOR_ID, StandardFactoryController.getInstance().serialize(requester));
        compound.setTag(TAG_RS_BUILDING_DATASTORE, StandardFactoryController.getInstance().serialize(rsDataStoreToken));
    }

    protected void setupRsDataStore()
    {
        this.rsDataStoreToken = colony.getRequestManager()
          .getDataStoreManager()
          .get(
            StandardFactoryController.getInstance().getNewInstance(TypeConstants.ITOKEN),
            TypeConstants.REQUEST_SYSTEM_BUILDING_DATA_STORE
          )
          .getId();
    }

    private void loadRequestSystemFromNBT(final NBTTagCompound compound)
    {
        if (compound.hasKey(TAG_REQUESTOR_ID))
        {
            this.requester = StandardFactoryController.getInstance().deserialize(compound.getCompoundTag(TAG_REQUESTOR_ID));
        }
        else
        {
            this.requester = StandardFactoryController.getInstance().getNewInstance(TypeToken.of(BuildingBasedRequester.class), this);
        }

        if (compound.hasKey(TAG_RS_BUILDING_DATASTORE))
        {
            this.rsDataStoreToken = StandardFactoryController.getInstance().deserialize(compound.getCompoundTag(TAG_RS_BUILDING_DATASTORE));
        }
        else
        {
            setupRsDataStore();
        }
    }

    private IRequestSystemBuildingDataStore getDataStore()
    {
        return colony.getRequestManager().getDataStoreManager().get(rsDataStoreToken, TypeConstants.REQUEST_SYSTEM_BUILDING_DATA_STORE);
    }

    @Override
    public Map<TypeToken<?>, Collection<IToken<?>>> getOpenRequestsByRequestableType()
    {
        return getDataStore().getOpenRequestsByRequestableType();
    }

    protected Map<Integer, Collection<IToken<?>>> getOpenRequestsByCitizen()
    {
        return getDataStore().getOpenRequestsByCitizen();
    }

    private Map<Integer, Collection<IToken<?>>> getCompletedRequestsByCitizen()
    {
        return getDataStore().getCompletedRequestsByCitizen();
    }

    private Map<IToken<?>, Integer> getCitizensByRequest()
    {
        return getDataStore().getCitizensByRequest();
    }

    /**
     * Create a request for a citizen.
     *
     * @param citizenData the data of the citizen.
     * @param requested   the request to create.
     * @param async       if async or not.
     * @param <R>         the type of the request.
     * @return the Token of the request.
     */
    @Override
    public <R extends IRequestable> IToken<?> createRequest(@NotNull final ICitizenData citizenData, @NotNull final R requested, final boolean async)
    {
        final IToken<?> requestToken = colony.getRequestManager().createRequest(requester, requested);
        final IRequest<?> request = colony.getRequestManager().getRequestForToken(requestToken);

        addRequestToMaps(citizenData.getId(), requestToken, TypeToken.of(requested.getClass()));

        colony.getRequestManager().assignRequest(requestToken);

        if (async)
        {
            citizenData.getJob().getAsyncRequests().add(requestToken);
            citizenData.triggerInteraction(new RequestBasedInteraction(RequestSystemTranslationConstants.REQUEST_RESOLVER_ASYNC,
                ChatPriority.PENDING, RequestSystemTranslationConstants.REQUEST_RESOLVER_ASYNC, request.getId()));
        }
        else
        {
            citizenData.triggerInteraction(new RequestBasedInteraction(RequestSystemTranslationConstants.REQUEST_RESOLVER_NORMAL,
                ChatPriority.BLOCKING, RequestSystemTranslationConstants.REQUEST_RESOLVER_NORMAL, request.getId()));
        }

        markDirty();

        return requestToken;
    }

    /**
     * Create a request for the building.
     *
     * @param requested the request to create.
     * @param async     if async or not.
     * @param <R>       the type of the request.
     * @return the Token of the request.
     */
    @Override
    public <R extends IRequestable> IToken<?> createRequest(@NotNull final R requested, final boolean async)
    {
        final IToken<?> requestToken = colony.getRequestManager().createRequest(requester, requested);
        addRequestToMaps(-1, requestToken, TypeToken.of(requested.getClass()));

        colony.getRequestManager().assignRequest(requestToken);

        markDirty();

        return requestToken;
    }

    @Override
    public void registerBlockPosition(@NotNull final net.minecraft.block.state.BlockState blockState, @NotNull final int[] pos, @NotNull final World world)
    {
        super.registerBlockPosition(blockState, pos, world);
        getModulesByType(IModuleWithExternalBlocks.class).forEach(module -> module.onBlockPlacedInBuilding(blockState.meta, pos, world));
    }

    /**
     * Internal method used to register a new Request to the request maps. Helper method.
     *
     * @param citizenId    The id of the citizen.
     * @param requestToken The {@link IToken} that is used to represent the request.
     * @param requested    The class of the type that has been requested eg. {@code ItemStack.class}
     */
    private void addRequestToMaps(final int citizenId, @NotNull final IToken<?> requestToken, @NotNull final TypeToken<?> requested)
    {
        if (!getOpenRequestsByRequestableType().containsKey(requested))
        {
            getOpenRequestsByRequestableType().put(requested, new ArrayList<>());
        }
        getOpenRequestsByRequestableType().get(requested).add(requestToken);

        getCitizensByRequest().put(requestToken, citizenId);

        if (!getOpenRequestsByCitizen().containsKey(citizenId))
        {
            getOpenRequestsByCitizen().put(citizenId, new ArrayList<>());
        }
        getOpenRequestsByCitizen().get(citizenId).add(requestToken);
    }

    @Override
    public boolean hasWorkerOpenRequests(final int citizenId)
    {
        return getOpenRequestsByCitizen().containsKey(citizenId) && !getOpenRequestsByCitizen().get(citizenId).isEmpty();
    }

    @Override
    public Collection<IRequest<?>> getOpenRequests(final int citizenId)
    {
        if (!getOpenRequestsByCitizen().containsKey(citizenId))
        {
            return ImmutableList.of();
        }

        final Collection<IToken<?>> tokens = getOpenRequestsByCitizen().get(citizenId);
        final List<IRequest<?>> requests = new ArrayList<>(tokens.size());

        for (final IToken<?> token : tokens)
        {
            final IRequest<?> request = colony.getRequestManager().getRequestForToken(token);
            if (request != null)
            {
                requests.add(request);
            }
        }

        return Collections.unmodifiableList(requests);
    }

    @Override
    public boolean hasWorkerOpenRequestsFiltered(final int citizenId, @NotNull final Predicate<IRequest<?>> selectionPredicate)
    {
        for (final IRequest<?> req : getOpenRequests(citizenId))
        {
            if (selectionPredicate.test(req))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasOpenSyncRequest(@NotNull final ICitizenData citizen)
    {
        if (!hasWorkerOpenRequests(citizen.getId()))
        {
            return false;
        }

        for (final IToken<?> token : getOpenRequestsByCitizen().get(citizen.getId()))
        {
            if (!citizen.isRequestAsync(token) && colony.getRequestManager().getRequestForToken(token) != null)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public <R> boolean hasWorkerOpenRequestsOfType(final int citizenId, final TypeToken<R> requestType)
    {
        return !getOpenRequestsOfType(citizenId, requestType).isEmpty();
    }

    @Override
    @SuppressWarnings({GENERIC_WILDCARD, UNCHECKED})
    public <R> ImmutableList<IRequest<? extends R>> getOpenRequestsOfType(
      final int citizenId,
      final TypeToken<R> requestType)
    {
        return ImmutableList.copyOf(getOpenRequests(citizenId).stream()
          .filter(request -> requestType.isAssignableFrom(request.getType()))
          .map(request -> (IRequest<? extends R>) request)
          .iterator());
    }

    @Override
    public boolean createPickupRequest(final int pickUpPrio)
    {
        int daysToPickup = 10 - pickUpPrio;
        if (pickUpDay == -1 || pickUpDay > colony.getDay() + daysToPickup)
        {
            pickUpDay = colony.getDay() + daysToPickup;
        }

        if (colony.getDay() < pickUpDay)
        {
            return false;
        }

        pickUpDay = -1;

        final List<IToken<?>> reqs = new ArrayList<>(getOpenRequestsByRequestableType().getOrDefault(TypeConstants.PICKUP, Collections.emptyList()));
        if (!reqs.isEmpty())
        {
            for (final IToken<?> req : reqs)
            {
                final IRequest<?> request = colony.getRequestManager().getRequestForToken(req);
                if (request != null && request.getState() == RequestState.IN_PROGRESS)
                {
                    final IRequestResolver<?> resolver = colony.getRequestManager().getResolverForRequest(req);
                    if (resolver instanceof IPlayerRequestResolver || resolver instanceof IRetryingRequestResolver)
                    {
                        colony.getRequestManager().reassignRequest(req, Collections.emptyList());
                    }
                }
            }
            return false;
        }

        createRequest(new Pickup(pickUpPrio), true);
        return true;
    }

    @Override
    public boolean hasCitizenCompletedRequests(@NotNull final ICitizenData data)
    {
        return getCompletedRequestsByCitizen().containsKey(data.getId()) && !getCompletedRequestsByCitizen().get(data.getId()).isEmpty();
    }

    @Override
    public boolean hasCitizenCompletedRequestsToPickup(@NotNull final ICitizenData data)
    {
        if (!getCompletedRequestsByCitizen().containsKey(data.getId()))
        {
            return false;
        }

        for (IToken<?> token : getCompletedRequestsByCitizen().get(data.getId()))
        {
            if (!data.isRequestAsync(token))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<IRequest<?>> getCompletedRequestsOfCitizenOrBuilding(@Nullable final ICitizenData data)
    {
        final int citizenId = data == null ? -1 : data.getId();
        final Collection<IToken<?>> tokens = getCompletedRequestsByCitizen().get(citizenId);
        if (tokens == null || tokens.isEmpty())
        {
            return ImmutableList.of();
        }

        final List<IRequest<?>> requests = new ArrayList<>(tokens.size());

        for (final IToken<?> token : tokens)
        {
            final IRequest<?> request = colony.getRequestManager().getRequestForToken(token);
            if (request != null)
            {
                requests.add(request);
            }
            else
            {
                getCompletedRequestsByCitizen().get(citizenId).remove(token);
                if (getCompletedRequestsByCitizen().get(citizenId).isEmpty())
                {
                    getCompletedRequestsByCitizen().remove(citizenId);
                }
            }
        }

        return Collections.unmodifiableList(requests);
    }

    @Override
    @SuppressWarnings({GENERIC_WILDCARD, UNCHECKED})
    public <R> ImmutableList<IRequest<? extends R>> getCompletedRequestsOfType(@NotNull final ICitizenData citizenData, final TypeToken<R> requestType)
    {
        return ImmutableList.copyOf(getCompletedRequestsOfCitizenOrBuilding(citizenData).stream()
          .filter(request -> requestType.isAssignableFrom(request.getType()))
          .map(request -> (IRequest<? extends R>) request)
          .iterator());
    }

    @Override
    @SuppressWarnings({GENERIC_WILDCARD, UNCHECKED})
    public <R> ImmutableList<IRequest<? extends R>> getCompletedRequestsOfTypeFiltered(
      @NotNull final ICitizenData citizenData,
      final TypeToken<R> requestType,
      final Predicate<IRequest<? extends R>> filter)
    {
        return ImmutableList.copyOf(getCompletedRequestsOfCitizenOrBuilding(citizenData).stream()
          .filter(request -> requestType.isAssignableFrom(request.getType()))
          .map(request -> (IRequest<? extends R>) request)
          .filter(filter)
          .iterator());
    }

    @Override
    public void markRequestAsAccepted(@NotNull final ICitizenData data, @NotNull final IToken<?> token)
    {
        if (!getCompletedRequestsByCitizen().containsKey(data.getId()) || !getCompletedRequestsByCitizen().get(data.getId()).contains(token))
        {
            throw new IllegalArgumentException("The given token " + token + " is not known as a completed request waiting for acceptance by the citizen.");
        }

        getCompletedRequestsByCitizen().get(data.getId()).remove(token);
        if (getCompletedRequestsByCitizen().get(data.getId()).isEmpty())
        {
            getCompletedRequestsByCitizen().remove(data.getId());
        }

        colony.getRequestManager().updateRequestState(token, RequestState.RECEIVED);
        markDirty();
    }

    @Override
    public void cancelAllRequestsOfCitizenOrBuilding(@Nullable final ICitizenData data)
    {
        final int citizenId = data == null ? -1 : data.getId();
        getOpenRequests(citizenId).forEach(request ->
        {
            try
            {
                colony.getRequestManager().updateRequestState(request.getId(), RequestState.CANCELLED);

                if (getOpenRequestsByRequestableType().containsKey(TypeToken.of(request.getRequest().getClass())))
                {
                    getOpenRequestsByRequestableType().get(TypeToken.of(request.getRequest().getClass())).remove(request.getId());
                    if (getOpenRequestsByRequestableType().get(TypeToken.of(request.getRequest().getClass())).isEmpty())
                    {
                        getOpenRequestsByRequestableType().remove(TypeToken.of(request.getRequest().getClass()));
                    }
                }

                getCitizensByRequest().remove(request.getId());
            }
            catch (Exception ex)
            {
                Log.getLogger().error("Tried cancelling request of citizen but errored: " + (data == null ? -1 : data.getName()), ex);
            }
        });

        getCompletedRequestsOfCitizenOrBuilding(data).forEach(request -> colony.getRequestManager().updateRequestState(request.getId(), RequestState.RECEIVED));

        getOpenRequestsByCitizen().remove(citizenId);

        getCompletedRequestsByCitizen().remove(citizenId);

        markDirty();
    }

    /**
     * Overrule the next open request with a give stack.
     * <p>
     * We squid:s135 which takes care that there are not too many continue statements in a loop since it makes sense here out of performance reasons.
     *
     * @param stack the stack.
     */
    @Override
    @SuppressWarnings("squid:S135")
    public void overruleNextOpenRequestWithStack(@NotNull final ItemStack stack)
    {
        if (ItemStackUtils.isEmpty(stack))
        {
            return;
        }

        final Collection<IRequestResolver<?>> resolvers = getResolvers();
        final List<IToken<?>> playerRequests = colony.getRequestManager().getPlayerResolver().getAllAssignedRequests();
        final List<IToken<?>> retryingRequests = colony.getRequestManager().getRetryingRequestResolver().getAllAssignedRequests();


        for (final IRequestResolver<?> resolver : resolvers)
        {
            final IStandardRequestManager requestManager = (IStandardRequestManager) colony.getRequestManager();

            final List<IRequest<? extends IDeliverable>> deliverableRequests =
              requestManager.getRequestHandler().getRequestsMadeByRequester(resolver)
                .stream()
                .filter(iRequest -> iRequest.getRequest() instanceof IDeliverable)
                .map(iRequest -> (IRequest<? extends IDeliverable>) iRequest)
                .collect(Collectors.toList());

            final IRequest<? extends IDeliverable> target = getFirstOverullingRequestFromInputList(deliverableRequests, stack);

            if (target == null || !isRequestStuck(target, playerRequests, retryingRequests))
            {
                continue;
            }

            colony.getRequestManager().overruleRequest(target.getId(), stack.copy());
            return;
        }

        final Set<Integer> citizenIdsWithRequests = getOpenRequestsByCitizen().keySet();
        for (final int citizenId : citizenIdsWithRequests)
        {
            final ICitizenData data = colony.getCitizenManager().getCivilian(citizenId);
            if (data == null && citizenId != -1)
            {
                continue;
            }

            final IRequest<? extends IDeliverable> target = getFirstOverullingRequestFromInputList(getOpenRequestsOfType(citizenId, TypeConstants.DELIVERABLE), stack);

            if (target == null || !isRequestStuck(target, playerRequests, retryingRequests))
            {
                continue;
            }

            colony.getRequestManager().overruleRequest(target.getId(), stack.copy());
            return;
        }
    }

    /**
     * Check if the request or one of the child requests is stuck in the retrying resolver.
     *
     * @param target the request to check.
     * @return true if stuck.
     */
    private boolean isRequestStuck(final IRequest<?> target, final List<IToken<?>> playerResolverRequests, final List<IToken<?>> retryingRequests)
    {
        if (playerResolverRequests.contains(target.getId())
              || retryingRequests.contains(target.getId()))
        {
            return true;
        }

        for (final IToken<?> child : target.getChildren())
        {
            final IRequest<?> request = colony.getRequestManager().getRequestForToken(child);
            if (request != null && isRequestStuck(request, playerResolverRequests, retryingRequests))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings({GENERIC_WILDCARD, UNCHECKED})
    public <R> ImmutableList<IRequest<? extends R>> getOpenRequestsOfTypeFiltered(
      @NotNull final ICitizenData citizenData,
      final TypeToken<R> requestType,
      final Predicate<IRequest<? extends R>> filter)
    {
        return ImmutableList.copyOf(getOpenRequests(citizenData.getId()).stream()
          .filter(request -> requestType.isAssignableFrom(request.getType()))
          .map(request -> (IRequest<? extends R>) request)
          .filter(filter)
          .iterator());
    }

    @Override
    public boolean overruleNextOpenRequestOfCitizenWithStack(@NotNull final ICitizenData citizenData, @NotNull final ItemStack stack)
    {
        if (ItemStackUtils.isEmpty(stack))
        {
            return false;
        }

        final IRequest<? extends IDeliverable> target = getFirstOverullingRequestFromInputList(getOpenRequestsOfType(citizenData.getId(), TypeConstants.DELIVERABLE), stack);

        if (target == null)
        {
            if (citizenData.getJob() instanceof AbstractJobCrafter)
            {
                final AbstractJobCrafter<?, ?> crafterJob = citizenData.getJob(AbstractJobCrafter.class);

                if (!crafterJob.getAssignedTasks().isEmpty())
                {
                    final List<IToken<?>> assignedTasks = crafterJob.getAssignedTasks();
                    final IRequest<? extends IDeliverable> deliverableChildRequest = assignedTasks
                      .stream()
                      .map(colony.getRequestManager()::getRequestForToken)
                      .map(IRequest::getChildren)
                      .flatMap(Collection::stream)
                      .map(colony.getRequestManager()::getRequestForToken)
                      .filter(iRequest -> iRequest.getRequest() instanceof IDeliverable)
                      .filter(iRequest -> ((IRequest<? extends IDeliverable>) iRequest).getRequest()
                        .matches(stack))
                      .findFirst()
                      .map(iRequest -> (IRequest<? extends IDeliverable>) iRequest)
                      .orElse(null);

                    if (deliverableChildRequest != null)
                    {
                        deliverableChildRequest.overrideCurrentDeliveries(ImmutableList.of(stack));
                        colony.getRequestManager().overruleRequest(deliverableChildRequest.getId(), stack.copy());
                        return true;
                    }
                }
            }

            return false;
        }

        try
        {
            target.overrideCurrentDeliveries(ImmutableList.of(stack));
            colony.getRequestManager().overruleRequest(target.getId(), stack.copy());
        }
        catch (final Exception ex)
        {
            Log.getLogger().error("Error during overruling", ex);
            Log.getLogger().error(target.getId().toString() + " " + target.getState().name() + " " + target.getShortDisplayString().toString());
            return false;
        }

        return true;
    }

    private IRequest<? extends IDeliverable> getFirstOverullingRequestFromInputList(
      @NotNull final Collection<IRequest<? extends IDeliverable>> queue,
      @NotNull final ItemStack stack)
    {
        if (queue.isEmpty())
        {
            return null;
        }

        List<IToken<?>> validRequesterTokens = Lists.newArrayList();
        validRequesterTokens.add(this.getId());
        this.getResolvers().forEach(iRequestResolver -> validRequesterTokens.add(iRequestResolver.getId()));

        return queue
          .stream()
          .filter(request -> request.getState() == RequestState.IN_PROGRESS && validRequesterTokens.contains(request.getRequester().getId()) && request.getRequest()
            .matches(stack))
          .findFirst()
          .orElse(null);
    }

    /*
    private Collection<IRequest<? extends IDeliverable>> flattenDeliverableChildRequests(@NotNull final IRequest<? extends IDeliverable> request)
    {
        if (!request.hasChildren())
        {
            return ImmutableList.of();
        }

        return request.getChildren()
                 .stream()
                 .map(colony.getRequestManager()::getRequestForToken)
                 .filter(Objects::nonNull)
                 .filter(request1 -> request1.getRequest() instanceof IDeliverable)
                 .map(request1 -> (IRequest<? extends IDeliverable>) request1)
                 .collect(Collectors.toList());
    }
    */

    @Override
    public IToken<?> getId()
    {
        return requester.getId();
    }

    @Override
    public final ImmutableCollection<IRequestResolver<?>> getResolvers()
    {
        final IStandardRequestManager requestManager = (IStandardRequestManager) colony.getRequestManager();
        if (!requestManager.getProviderHandler().getRegisteredResolvers(this).isEmpty())
        {
            List<IRequestResolver<? extends IRequestable>> list = new ArrayList<>();
            for (Iterator<IToken<?>> iterator = requestManager.getProviderHandler().getRegisteredResolvers(this).iterator(); iterator.hasNext(); )
            {
                final IToken<?> token = iterator.next();
                try
                {
                    IRequestResolver<? extends IRequestable> resolver = requestManager.getResolverHandler().getResolver(token);
                    list.add(resolver);
                }
                catch (Exception e)
                {
                    iterator.remove();
                }
            }
            return ImmutableList.copyOf(list);
        }

        return createResolvers();
    }

    @Override
    public ImmutableCollection<IRequestResolver<?>> createResolvers()
    {
        final ImmutableList.Builder<IRequestResolver<?>> builder = ImmutableList.builder();
        for (final ICreatesResolversModule module : getModulesByType(ICreatesResolversModule.class))
        {
            builder.addAll(module.createResolvers());
        }
        return builder.build();
    }

    @Override
    public IRequester getRequester()
    {
        return requester;
    }

    @NotNull
    @Override
    public ILocation getLocation()
    {
        return getRequester().getLocation();
    }

    @Override
    public void onRequestedRequestComplete(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request)
    {
        if (!getCitizensByRequest().containsKey(request.getId()))
        {
            return;
        }

        final int citizenThatRequested = getCitizensByRequest().remove(request.getId());

        if (getOpenRequestsByCitizen().containsKey(citizenThatRequested))
        {
            getOpenRequestsByCitizen().get(citizenThatRequested).remove(request.getId());
            if (getOpenRequestsByCitizen().get(citizenThatRequested).isEmpty())
            {
                getOpenRequestsByCitizen().remove(citizenThatRequested);
            }
        }

        getOpenRequestsByRequestableType().get(TypeToken.of(request.getRequest().getClass())).remove(request.getId());

        if (getOpenRequestsByRequestableType().get(TypeToken.of(request.getRequest().getClass())).isEmpty())
        {
            getOpenRequestsByRequestableType().remove(TypeToken.of(request.getRequest().getClass()));
        }

        if (citizenThatRequested >= 0)
        {
            getCompletedRequestsByCitizen().computeIfAbsent(citizenThatRequested, ArrayList::new).add(request.getId());
        }
        else
        {
            colony.getRequestManager().updateRequestState(request.getId(), RequestState.RECEIVED);
        }

        //Check if the citizen did not die.
        if (colony.getCitizenManager().getCivilian(citizenThatRequested) != null)
        {
            colony.getCitizenManager().getCivilian(citizenThatRequested).onRequestCompleted(request.getId());
        }
        markDirty();
    }

    @Override
    public void onRequestedRequestCancelled(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request)
    {
        final int citizenThatRequested = getCitizensByRequest().remove(request.getId());
        final Map<Integer, Collection<IToken<?>>> openRequestsByCitizen = getOpenRequestsByCitizen();
        final Collection<IToken<?>> byCitizenList = openRequestsByCitizen.get(citizenThatRequested);
        if (byCitizenList != null)
        {
            byCitizenList.remove(request.getId());
            if (byCitizenList.isEmpty())
            {
                openRequestsByCitizen.remove(citizenThatRequested);
            }
        }

        if (getOpenRequestsByRequestableType().containsKey(TypeToken.of(request.getRequest().getClass())))
        {
            getOpenRequestsByRequestableType().get(TypeToken.of(request.getRequest().getClass())).remove(request.getId());
            if (getOpenRequestsByRequestableType().get(TypeToken.of(request.getRequest().getClass())).isEmpty())
            {
                getOpenRequestsByRequestableType().remove(TypeToken.of(request.getRequest().getClass()));
            }
        }

        //Check if the citizen did not die.
        if (colony.getCitizenManager().getCivilian(citizenThatRequested) != null)
        {
            colony.getCitizenManager().getCivilian(citizenThatRequested).onRequestCancelled(request.getId());
        }
        markDirty();
    }

    @NotNull
    @Override
    public String getRequesterDisplayName(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request)
    {
        if (!getCitizensByRequest().containsKey(request.getId()))
        {
            return "<UNKNOWN>";
        }

        final int citizenId = getCitizensByRequest().get(request.getId());
        if (citizenId == -1)
        {
            return getBuildingDisplayName();
        }

        final ICitizenData citizenData = colony.getCitizenManager().getCivilian(citizenId);
        if (citizenData.getJob() == null)
        {
            return citizenData.getName();
        }

        final String jobName = citizenData.getJob().getJobRegistryEntry().getTranslationKey().toLowerCase();
        return jobName + " " + citizenData.getName();
    }

    @Override
    public Optional<ICitizenData> getCitizenForRequest(@NotNull final IToken<?> token)
    {
        if (!getCitizensByRequest().containsKey(token) || colony == null)
        {
            return Optional.empty();
        }

        final int citizenID = getCitizensByRequest().get(token);
        if (citizenID == -1 || colony.getCitizenManager().getCivilian(citizenID) == null)
        {
            return Optional.empty();
        }

        return Optional.of(colony.getCitizenManager().getCivilian(citizenID));
    }

    @Override
    public Map<ItemStorage, Integer> reservedStacksExcluding(@NotNull final IRequest<? extends IDeliverable> excluded)
    {
        final Map<ItemStorage, Integer> map = new HashMap<>();
        for (final IHasRequiredItemsModule module : getModulesByType(IHasRequiredItemsModule.class))
        {
            for (final Map.Entry<ItemStorage, Integer> content : module.reservedStacksExcluding(excluded).entrySet())
            {
                final int current = map.getOrDefault(content.getKey(), 0);
                map.put(content.getKey(), current + content.getValue());
            }
        }
        return map;
    }

    /**
     * Get a list of open requests from the building that are filtered by a predicate for citizen id or building (-1).
     * @param citizenId the citizen id to include.
     * @param selectionPredicate the selection predicate.
     * @return the list.
     */
    public List<IRequest<?>> getOpenRequestsOfCitizenOrBuilding(final int citizenId, final Predicate<IRequest<?>> selectionPredicate)
    {
        final List<IRequest<?>> requests = new ArrayList<>();
        for (final IRequest<?> req : getOpenRequests(-1))
        {
            if (selectionPredicate.test(req))
            {
                requests.add(req);
            }
        }

        for (final IRequest<?> req : getOpenRequests(citizenId))
        {
            if (selectionPredicate.test(req))
            {
                requests.add(req);
            }
        }

        return requests;
    }

    /**
     * Get a list of closed requests from the building that are filtered by a predicate for citizen or building (null citizen).
     * @param citizenData the citizen to include.
     * @param selectionPredicate the selection predicate.
     * @return the list.
     */
    public List<IRequest<?>> getCompletedRequestsOfCitizenOrBuilding(@Nullable final ICitizenData citizenData, final Predicate<IRequest<?>> selectionPredicate)
    {
        final List<IRequest<?>> requests = new ArrayList<>();
        for (final IRequest<?> req : getCompletedRequestsOfCitizenOrBuilding(citizenData))
        {
            if (selectionPredicate.test(req))
            {
                requests.add(req);
            }
        }

        for (final IRequest<?> req : getCompletedRequestsOfCitizenOrBuilding(citizenData))
        {
            if (selectionPredicate.test(req))
            {
                requests.add(req);
            }
        }

        return requests;
    }

    /**
     * Move request from building (-1) to citizen and mark synchronous.
     * @param citizenData the citizen to move it to.
     * @param request the request to move.
     */
    public void moveToSyncCitizen(final ICitizenData citizenData, final IRequest<?> request)
    {
        getDataStore().moveToSyncCitizen(citizenData, request);
    }

    //------------------------- !END! RequestSystem handling for minecolonies buildings -------------------------//
}











