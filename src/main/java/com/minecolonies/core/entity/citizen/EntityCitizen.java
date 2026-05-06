package com.minecolonies.core.entity.citizen;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.ChunkPos;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.*;
import com.minecolonies.api.colony.buildings.IGuardBuilding;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.permissions.IPermissions;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.compatibility.Compatibility;
import com.minecolonies.api.entity.CustomGoalSelector;
import com.minecolonies.api.entity.ai.combat.threat.IThreatTableEntity;
import com.minecolonies.api.entity.ai.combat.threat.ThreatTable;
import com.minecolonies.api.entity.ai.statemachine.AIOneTimeEventTarget;
import com.minecolonies.api.entity.ai.statemachine.states.CitizenAIState;
import com.minecolonies.api.entity.ai.statemachine.states.EntityState;
import com.minecolonies.api.entity.ai.statemachine.states.IState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickingTransition;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.entity.citizen.VisibleCitizenStatus;
import com.minecolonies.api.entity.citizen.citizenhandlers.*;
import com.minecolonies.api.entity.citizen.happiness.ExpirationBasedHappinessModifier;
import com.minecolonies.api.entity.citizen.happiness.StaticHappinessSupplier;
import com.minecolonies.api.eventbus.events.colony.citizens.CitizenDiedModEvent;
import com.minecolonies.api.eventbus.events.colony.citizens.CitizenRemovedModEvent;
import com.minecolonies.api.inventory.InventoryCitizen;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.sounds.EventType;
import com.minecolonies.api.util.*;
import com.minecolonies.api.util.MessageUtils.MessagePriority;
import com.minecolonies.api.util.constant.HappinessConstants;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.api.util.constant.TypeConstants;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.WindowInteraction;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.buildings.modules.WorkerBuildingModule;
import com.minecolonies.core.colony.eventhooks.citizenEvents.CitizenDiedEvent;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.colony.jobs.JobKnight;
import com.minecolonies.core.colony.jobs.JobNetherWorker;
import com.minecolonies.core.colony.jobs.JobRanger;
import com.minecolonies.core.datalistener.DiseasesListener;
import com.minecolonies.core.debug.DebugPlayerManager;
import com.minecolonies.core.entity.ai.minimal.*;
import com.minecolonies.core.entity.ai.workers.AbstractEntityAIBasic;
import com.minecolonies.core.entity.ai.workers.CitizenAI;
import com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard;
import com.minecolonies.core.entity.citizen.citizenhandlers.*;
import com.minecolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
import com.minecolonies.core.entity.pathfinding.navigation.MovementHandler;
import com.minecolonies.core.event.EventHandler;
import com.minecolonies.core.event.TextureReloadListener;
import com.minecolonies.core.network.messages.client.ItemParticleEffectMessage;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;
import com.minecolonies.core.network.messages.client.colony.ColonyViewCitizenViewMessage;
import com.minecolonies.core.network.messages.client.colony.PlaySoundForCitizenMessage;
import com.minecolonies.core.network.messages.server.colony.OpenInventoryMessage;
import com.minecolonies.core.util.TeleportHelper;
import com.minecolonies.core.util.citizenutils.CitizenItemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minecolonies.api.research.util.ResearchConstants.*;
import static com.minecolonies.api.util.ItemStackUtils.ISFOOD;
import static com.minecolonies.api.util.constant.CitizenConstants.*;
import static com.minecolonies.api.util.constant.Constants.*;
import static com.minecolonies.api.util.constant.HappinessConstants.DAMAGE;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_CITIZEN;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_ID;
import static com.minecolonies.api.util.constant.StatisticsConstants.DEATH;
import static com.minecolonies.api.util.constant.Suppression.INCREMENT_AND_DECREMENT_OPERATORS_SHOULD_NOT_BE_USED_IN_A_METHOD_CALL_OR_MIXED_WITH_OTHER_OPERATORS_IN_AN_EXPRESSION;
import static com.minecolonies.api.util.constant.TranslationConstants.*;
import static com.minecolonies.core.entity.ai.minimal.EntityAIInteractToggleAble.*;

/**
 * The Class used to represent the citizen entities.
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.ExcessiveClassLength"})
public class EntityCitizen extends AbstractEntityCitizen implements IThreatTableEntity
{
    private static final int    CALL_HELP_CD        = 100;
    private static final float  GUARD_BLOCK_DAMAGE  = 0.5f;
    private static final double MAX_SPEED_FACTOR    = 0.5;
    private static final int    CALL_TO_HELP_AMOUNT = 2;

    private int                       citizenId = 0;
    private ICitizenData              citizenData;
    private ICitizenExperienceHandler citizenExperienceHandler;
    private ICitizenInventoryHandler  citizenInventoryHandler;
    private ICitizenColonyHandler     citizenColonyHandler;
    private ICitizenJobHandler        citizenJobHandler;
    private ICitizenSleepHandler      citizenSleepHandler;
    private final CitizenCombatTracker combatTracker;
    private boolean child = false;
    private int     callForHelpCooldown = 0;
    private float   lastDistanceWalked  = 0;
    private ICitizenDataView citizenDataView;
    private ILocation        location = null;

    /** [1.7.10] ChunkPos replaced by two int fields */
    private int lastChunkX = Integer.MIN_VALUE;
    private int lastChunkZ = Integer.MIN_VALUE;

    private final ThreatTable threatTable         = new ThreatTable<>(this);
    private       int         interactionCooldown = 0;

    private ITickRateStateMachine<IState> citizenAI = new TickRateStateMachine<>(CitizenAIState.IDLE, e -> {}, ENTITY_AI_TICKRATE);

    private int     maxAir = 300;
    private boolean isGlowing;
    private double  cachedActionSaturationDecrease;

    /**
     * Constructor for a new citizen typed entity.
     *
     * @param world the world.
     */
    public EntityCitizen(final World world)
    {
        super(world);
        this.goalSelector   = new CustomGoalSelector(this.tasks);
        this.targetSelector = new CustomGoalSelector(this.targetTasks);
        this.citizenExperienceHandler = new CitizenExperienceHandler(this);
        this.citizenInventoryHandler  = new CitizenInventoryHandler(this);
        this.citizenColonyHandler     = new CitizenColonyHandler(this);
        this.citizenJobHandler        = new CitizenJobHandler(this);
        this.citizenSleepHandler      = new CitizenSleepHandler(this);
        this.combatTracker            = new CitizenCombatTracker(this);
        // [1.7.10] MovementHandler ported separately; stub if not yet available
        // this.moveControl = new MovementHandler(this);
        this.isImmuneToFire = false;
        this.setCustomNameTag("");
        this.setAlwaysRenderNameTag(MineColonies.getConfig().getServer().alwaysRenderNameTag.get());

        entityStateController.addTransition(new TickingTransition<>(EntityState.INIT, () -> true, this::initialize, 40));

        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_CLIENT, () -> {
            citizenColonyHandler.updateColonyClient();
            return false;
        }, () -> null, 1));

        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_CLIENT, this::shouldBeInactive, () -> EntityState.INACTIVE, TICKS_20));
        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_CLIENT, this::refreshCitizenDataView, () -> null, TICKS_20));

        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_SERVER, this::updateHealing, () -> null, HEAL_CITIZENS_AFTER));
        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_SERVER, this::updateVisualData, () -> null, 200));
        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_SERVER, this::onServerUpdateHandlers, () -> null, TICKS_20));
        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_SERVER, this::onTickDecrements, () -> null, 1));
        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_SERVER, this::shouldBeInactive, () -> EntityState.INACTIVE, TICKS_20));
        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_SERVER, () -> {
            citizenAI.tick();
            return false;
        }, () -> null, 1));
        entityStateController.addTransition(new TickingTransition<>(EntityState.ACTIVE_SERVER, this::decreaseIdleSaturation, () -> null, SATURATION_DECREASE_AFTER));
        entityStateController.addTransition(new TickingTransition<>(EntityState.INACTIVE, this::isAlive, () -> EntityState.INIT, 100));
    }

    private boolean shouldBeInactive()
    {
        if (citizenData == null && citizenDataView == null)
        {
            return true;
        }
        return !isAlive();
    }

    private EntityState initialize()
    {
        if (worldObj.isRemote)
        {
            citizenColonyHandler.updateColonyClient();
            if (citizenColonyHandler.getColonyId() != 0 && citizenId != 0)
            {
                final IColonyView colonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), worldObj.provider.dimensionId);
                if (colonyView != null)
                {
                    this.citizenDataView = colonyView.getCitizen(citizenId);
                    if (citizenDataView != null)
                    {
                        initTasks();
                        return EntityState.ACTIVE_CLIENT;
                    }
                }
            }
        }
        else
        {
            citizenColonyHandler.registerWithColony(citizenColonyHandler.getColonyId(), citizenId);
            if (citizenData != null && isAlive() && citizenColonyHandler.getColonyOrRegister() != null)
            {
                initTasks();
                return EntityState.ACTIVE_SERVER;
            }
        }

        return null;
    }

    @SuppressWarnings(INCREMENT_AND_DECREMENT_OPERATORS_SHOULD_NOT_BE_USED_IN_A_METHOD_CALL_OR_MIXED_WITH_OTHER_OPERATORS_IN_AN_EXPRESSION)
    private void initTasks()
    {
        new CitizenAI(this);

        int priority = 0;
        this.goalSelector.addGoal(priority, new EntityAIFloat(this));
        this.goalSelector.addGoal(priority, new EntityAIInteractToggleAble(this, FENCE_TOGGLE, TRAP_TOGGLE, DOOR_TOGGLE));
        this.goalSelector.addGoal(++priority, new LookAtEntityInteractGoal(this, EntityPlayer.class, WATCH_CLOSEST2, 0.2F));
        this.goalSelector.addGoal(++priority, new LookAtEntityInteractGoal(this, EntityCitizen.class, WATCH_CLOSEST2_FAR, WATCH_CLOSEST2_FAR_CHANCE));
        this.goalSelector.addGoal(++priority, new LookAtEntityGoal(this, EntityLivingBase.class, WATCH_CLOSEST));
    }

    @NotNull
    @Override
    public boolean checkAndHandleImportantInteractions(final EntityPlayer player, final ItemStack heldItem)
    {
        final IColonyView iColonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), player.worldObj.provider.dimensionId);
        if (iColonyView != null && !iColonyView.getPermissions().hasPermission(player, Action.ACCESS_HUTS))
        {
            return false;
        }

        if (!ItemStackUtils.isEmpty(heldItem) && heldItem.getItem() == net.minecraft.init.Items.name_tag)
        {
            return super.checkAndHandleImportantInteractions(player, heldItem);
        }

        final boolean handled = directPlayerInteraction(player, heldItem);
        if (handled)
        {
            return true;
        }

        if (worldObj.isRemote && iColonyView != null)
        {
            if (player.isSneaking() && !isInvisible())
            {
                Network.getNetwork().sendToServer(new OpenInventoryMessage(iColonyView, this.getCommandSenderName(), this.getEntityId()));
            }
            else
            {
                final ICitizenDataView dataView = getCitizenDataView();
                if (dataView != null && !isInvisible())
                {
                    new WindowInteraction(dataView).open();
                }
            }
        }

        if (!worldObj.isRemote && getCitizenData() != null)
        {
            citizenData.setInteractedRecently(player.getUniqueID());
            final ColonyViewCitizenViewMessage message = new ColonyViewCitizenViewMessage((Colony) getCitizenData().getColony(), getCitizenData());
            Network.getNetwork().sendToPlayer(message, (EntityPlayerMP) player);

            if (DebugPlayerManager.hasDebugEnabled(player))
            {
                getCitizenAI().setHistoryEnabled(true, 20);
                if (getCitizenJobHandler().getColonyJob() != null)
                {
                    getCitizenJobHandler().getWorkAI().getStateAI().setHistoryEnabled(true, 20);
                }
            }
            else
            {
                if (citizenData.getJob() != null)
                {
                    ((AbstractEntityAIBasic) citizenData.getJob().getWorkerAI()).setDelay(TICKS_SECOND * 3);
                }
                getNavigator().clearPathEntity();
                getLookHelper().setLookPositionWithEntity(player, 10F, 10F);
            }
        }

        return true;
    }

    /**
     * Direct interaction actions with a player.
     * Returns true if interaction was consumed.
     */
    private boolean directPlayerInteraction(final EntityPlayer player, final ItemStack usedStack)
    {
        if (player.isSneaking())
        {
            return false;
        }

        if (MineColonies.getConfig().getServer().enableInDevelopmentFeatures.get() &&
              usedStack != null && usedStack.getItem() instanceof ItemBlock && ((ItemBlock) usedStack.getItem()).field_150939_a instanceof AbstractBlockHut<?>)
        {
            final BuildingEntry entry = ((AbstractBlockHut<?>) ((ItemBlock) usedStack.getItem()).field_150939_a).getBuildingEntry();
            for (final BuildingEntry.ModuleProducer moduleProducer : entry.getModuleProducers())
            {
                if (BuildingEntry.produceModuleWithoutBuilding(moduleProducer.key) instanceof WorkerBuildingModule module)
                {
                    getCitizenJobHandler().setModelDependingOnJob(module.getJobEntry().produceJob(null));
                    return true;
                }
            }
        }

        if (isInteractionItem(usedStack) && interactionCooldown > 0)
        {
            if (!worldObj.isRemote)
            {
                playSound("mob.villager.no", 0.5f, (float) SoundUtils.getRandomPitch(getRNG()));
                MessageUtils.format(WARNING_INTERACTION_CANT_DO_NOW, this.getCitizenData().getName())
                  .withPriority(MessagePriority.DANGER)
                  .sendTo(player);
            }
            return true;
        }

        final boolean isSick = (getCitizenData() != null && getCitizenData().getCitizenDiseaseHandler().isSick()) || (citizenDataView != null
            && citizenDataView.getVisibleStatus() == VisibleCitizenStatus.SICK);

        if (usedStack != null && usedStack.getItem() == net.minecraft.init.Items.golden_apple && isSick)
        {
            usedStack.stackSize--;
            if (!worldObj.isRemote)
            {
                if (getRNG().nextInt(3) == 0)
                {
                    getCitizenData().getCitizenDiseaseHandler().cure();
                    playSound("random.levelup", 1.0f, (float) SoundUtils.getRandomPitch(getRNG()));
                    // [1.7.10] VanillaParticleMessage stub - particles not yet ported
                }
            }
            interactionCooldown = 20 * 60 * 5;
            return true;
        }

        if (usedStack != null && usedStack.getItem() != null && false /* [1.7.10] ModTags.poisonous_food not yet ported */)
        {
            // TODO: port poisonous food tag check
            return false;
        }

        if (isSick)
        {
            return false;
        }

        if (usedStack != null && ISFOOD.test(usedStack) && usedStack.getItem() != net.minecraft.init.Items.golden_apple)
        {
            if (isBaby())
            {
                childFoodInteraction(usedStack, player);
            }
            else
            {
                eatFoodInteraction(usedStack, player);
            }
            return true;
        }

        if (usedStack != null && usedStack.getItem() == net.minecraft.init.Items.book && isBaby())
        {
            usedStack.stackSize--;
            if (!worldObj.isRemote)
            {
                getCitizenData().getCitizenSkillHandler().addXpToSkill(Skill.Intelligence, 50, getCitizenData());
            }
            interactionCooldown = 20 * 60 * 5;
            return true;
        }

        if (usedStack != null && usedStack.getItem() == net.minecraft.init.Items.cactus)
        {
            usedStack.stackSize--;
            if (!worldObj.isRemote)
            {
                MessageUtils.format(MESSAGE_INTERACTION_OUCH, getCitizenData().getName()).sendTo(player);
                EntityNavigationUtils.walkAwayFrom(this, (int)posX, (int)posY, (int)posZ, 5, 1);
                isJumping = true;
            }
            interactionCooldown = 20 * 60 * 5;
            return true;
        }

        if (usedStack != null && usedStack.getItem() == net.minecraft.init.Items.glowstone_dust)
        {
            usedStack.stackSize--;
            if (!worldObj.isRemote)
            {
                // [1.7.10] MobEffects.GLOWING does not exist; skip glowing effect
                // addPotionEffect(new PotionEffect(Potion.???.id, 20 * 60 * 3));
            }
            interactionCooldown = 20 * 60 * 3;
            return true;
        }

        return false;
    }

    public boolean isInteractionItem(final ItemStack stack)
    {
        if (stack == null) return false;
        return ISFOOD.test(stack) || stack.getItem() == net.minecraft.init.Items.book
                 || stack.getItem() == net.minecraft.init.Items.golden_apple
                 || stack.getItem() == net.minecraft.init.Items.cactus
                 || stack.getItem() == net.minecraft.init.Items.glowstone_dust;
        // [1.7.10] ModTags.poisonous_food check omitted
    }

    private void childFoodInteraction(final ItemStack usedStack, final EntityPlayer player)
    {
        if (usedStack.getDisplayName().toLowerCase(java.util.Locale.US).contains("cookie"))
        {
            interactionCooldown = 100;
            if (!worldObj.isRemote)
            {
                // [1.7.10] addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 300));
                playSound("random.eat", 1.5f, (float) SoundUtils.getRandomPitch(getRNG()));
                // [1.7.10] ItemParticleEffectMessage stub
                ItemStackUtils.consumeFood(usedStack, this, player.inventory);
            }
        }
        else
        {
            // [1.7.10] player.inventory.removeItem -> player drops it
            if (!worldObj.isRemote)
            {
                playSound("mob.villager.no", 1.0f, (float) SoundUtils.getRandomPitch(getRNG()));
                MessageUtils.format(MESSAGE_INTERACTION_COOKIE, this.getCitizenData().getName())
                  .withPriority(MessagePriority.DANGER)
                  .sendTo(player);
            }
        }
    }

    private void eatFoodInteraction(final ItemStack usedStack, final EntityPlayer player)
    {
        if (!worldObj.isRemote)
        {
            playSound("random.eat", 1.5f, (float) SoundUtils.getRandomPitch(getRNG()));
            // [1.7.10] ItemParticleEffectMessage stub
            if (citizenData != null)
            {
                citizenData.getCitizenFoodHandler().addLastEaten(usedStack.getItem());
            }
            ItemStackUtils.consumeFood(usedStack, this, player.inventory);
        }
        interactionCooldown = 100;
    }

    @Override
    @NotNull
    public String getScoreboardName()
    {
        return getCommandSenderName() + " (" + getCivilianID() + ")";
    }

    @Override
    public ICitizenDataView getCitizenDataView()
    {
        if (this.citizenDataView == null)
        {
            if (citizenColonyHandler.getColonyId() != 0 && citizenId != 0)
            {
                final IColonyView colonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), worldObj.provider.dimensionId);
                if (colonyView != null)
                {
                    this.citizenDataView = colonyView.getCitizen(citizenId);
                    return this.citizenDataView;
                }
            }
        }
        else
        {
            return this.citizenDataView;
        }
        return null;
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger(TAG_COLONY_ID, citizenColonyHandler.getColonyId());
        if (citizenData != null)
        {
            compound.setInteger(TAG_CITIZEN, citizenData.getId());
        }
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if (compound.hasKey(TAG_COLONY_ID))
        {
            citizenColonyHandler.setColonyId(compound.getInteger(TAG_COLONY_ID));
            if (compound.hasKey(TAG_CITIZEN))
            {
                citizenId = compound.getInteger(TAG_CITIZEN);
            }
        }
        // [1.7.10] setPose(Pose.STANDING) has no equivalent; skipped
    }

    /**
     * [1.7.10] onLivingUpdate replaces aiStep.
     */
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (interactionCooldown > 0)
        {
            interactionCooldown--;
        }
    }

    public boolean refreshCitizenDataView()
    {
        if (citizenColonyHandler.getColonyId() != 0 && citizenId != 0)
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), worldObj.provider.dimensionId);
            if (colonyView != null)
            {
                this.citizenDataView = colonyView.getCitizen(citizenId);
                // [1.7.10] pathing options not yet ported
                // this.getNavigator().getPathingOptions().setCanUseRails(canPathOnRails());
            }
        }
        return false;
    }

    private boolean onTickDecrements()
    {
        decrementCallForHelpCooldown();
        decreaseWalkingSaturation();
        return false;
    }

    private boolean onServerUpdateHandlers()
    {
        citizenExperienceHandler.gatherXp();
        CitizenItemUtils.pickupItems(this);
        citizenData.setLastPosition((int)posX, (int)posY, (int)posZ);
        onLivingSoundUpdate();

        final int chunkX = (int) posX >> 4;
        final int chunkZ = (int) posZ >> 4;
        if (chunkX != lastChunkX || chunkZ != lastChunkZ)
        {
            lastChunkX = chunkX;
            lastChunkZ = chunkZ;
            EventHandler.onEnteringChunkEntity(this, chunkX, chunkZ);
        }

        return false;
    }

    @Override
    public int getMaxAirSupply()
    {
        return maxAir;
    }

    private boolean updateVisualData()
    {
        this.setAlwaysRenderNameTag(MineColonies.getConfig().getServer().alwaysRenderNameTag.get());

        if (!citizenColonyHandler.getColonyOrRegister().getTextureStyleId().equals(getDataWatcher().getWatchableObjectString(DATA_STYLE_ID)))
        {
            getDataWatcher().updateObject(DATA_STYLE_ID, citizenColonyHandler.getColonyOrRegister().getTextureStyleId());
        }
        if (!citizenData.getTextureSuffix().equals(getDataWatcher().getWatchableObjectString(DATA_TEXTURE_SUFFIX_ID)))
        {
            getDataWatcher().updateObject(DATA_TEXTURE_SUFFIX_ID, citizenData.getTextureSuffix());
        }

        return false;
    }

    private boolean updateHealing()
    {
        checkHeal();
        if (citizenData.getSaturation() <= 0)
        {
            if (!isPotionActive(net.minecraft.potion.Potion.moveSlowdown))
            {
                addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.potion.Potion.moveSlowdown.id, TICKS_SECOND * 30));
            }
        }
        else
        {
            removePotionEffect(net.minecraft.potion.Potion.moveSlowdown.id);
        }
        return false;
    }

    private void decrementCallForHelpCooldown()
    {
        if (callForHelpCooldown > 0)
        {
            callForHelpCooldown--;
        }
    }

    public boolean canPathOnRails()
    {
        if (worldObj.isRemote)
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), worldObj.provider.dimensionId);
            if (colonyView != null)
            {
                return colonyView.getResearchManager().getResearchEffects().getEffectStrength(RAILS) > 0;
            }
            return false;
        }
        return getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(RAILS) > 0;
    }

    public boolean canClimbVines()
    {
        if (worldObj.isRemote)
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), worldObj.provider.dimensionId);
            if (colonyView != null)
            {
                return colonyView.getResearchManager().getResearchEffects().getEffectStrength(VINES) > 0;
            }
            return false;
        }
        return getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(VINES) > 0;
    }

    private void decreaseWalkingSaturation()
    {
        if (distanceWalkedModified - lastDistanceWalked > ACTIONS_EACH_BLOCKS_WALKED)
        {
            lastDistanceWalked = distanceWalkedModified;
            decreaseSaturationForContinuousAction();
        }
    }

    private void checkHeal()
    {
        if (getCitizenData() != null && getHealth() < (getCitizenData().getCitizenDiseaseHandler().isSick() ? getMaxHealth() / 3 : getMaxHealth()) && getAITarget() == null)
        {
            final double limitDecrease = getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(SATLIMIT);
            final double citizenSaturation = citizenData.getSaturation();
            final double healAmount;
            if (citizenSaturation >= FULL_SATURATION + limitDecrease)
            {
                healAmount = 2 * (1.0 + getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(REGENERATION));
            }
            else if (citizenSaturation < LOW_SATURATION)
            {
                healAmount = 1 * (citizenSaturation / FULL_SATURATION) / 2.0;
            }
            else
            {
                healAmount = 1 * (1.0 + getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(REGENERATION));
            }
            heal((float) healAmount);
        }
    }

    private void onLivingSoundUpdate()
    {
        if (WorldUtil.isDayTime(worldObj) && !isSilent())
        {
            SoundUtils.playRandomSound(worldObj, (int)posX, (int)posY, (int)posZ, citizenData);
        }
    }

    @Override
    public boolean isBaby()
    {
        return child;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        // DataWatcher entries initialised in AbstractEntityCitizen.entityInit()
        // Colony and citizen ID watchers are set there
    }

    @Override
    public void setRenderMetadata(final String metadata)
    {
        super.setRenderMetadata(metadata);
        if (citizenJobHandler.getColonyJob() != null && MineColonies.getConfig().getServer().enableInDevelopmentFeatures.get())
        {
            setCustomNameTag(citizenData.getName() + "[" + citizenJobHandler.getColonyJob().getNameTagDescription() + "]");
        }
    }

    @Override
    public ILocation getLocation()
    {
        if (location == null)
        {
            location = StandardFactoryController.getInstance().getNewInstance(TypeConstants.ILOCATION, this);
        }
        return location;
    }

    @Override
    public ICitizenData getCitizenData()
    {
        return citizenData;
    }

    @Override
    public ICivilianData getCivilianData()
    {
        return citizenData;
    }

    @Override
    public void setCivilianData(@Nullable final ICivilianData data)
    {
        if (data != null)
        {
            this.citizenData = (ICitizenData) data;
            data.initEntityValues();
        }
    }

    @Override
    @NotNull
    public InventoryCitizen getInventoryCitizen()
    {
        return getCitizenData().getInventory();
    }

    @Override
    @NotNull
    public net.minecraftforge.items.IItemHandler getItemHandlerCitizen()
    {
        return getInventoryCitizen();
    }

    @Override
    public void markDirty(final int time)
    {
        if (citizenData != null)
        {
            citizenData.markDirty(time);
        }
    }

    @Override
    public void setIsChild(final boolean isChild)
    {
        if (isChild && !this.child)
        {
            new EntityAICitizenChild(this);
        }
        else if (!isChild && this.child)
        {
            this.child = isChild;
            getCitizenJobHandler().setModelDependingOnJob(citizenJobHandler.getColonyJob());
        }
        this.child = isChild;
        getDataWatcher().updateObject(DATA_IS_CHILD_ID, (byte)(isChild ? 1 : 0));
        // [1.7.10] refreshDimensions() → recalculate bounding box manually if needed
        markDirty(0);
    }

    @Override
    public void playMoveAwaySound()
    {
        if (citizenJobHandler.getColonyJob() != null)
        {
            SoundUtils.playSoundAtCitizenWith(worldObj, (int)posX, (int)posY, (int)posZ, EventType.DANGER, getCitizenData());
        }
    }

    @Override
    public void decreaseSaturationForAction()
    {
        this.cachedActionSaturationDecrease += BIG_SATURATION_FACTOR;
    }

    @Override
    public void decreaseSaturationForContinuousAction()
    {
        this.cachedActionSaturationDecrease += SATURATION_DECREASE_FACTOR / 4.0;
    }

    @Override
    public int getCivilianID()
    {
        return citizenId;
    }

    @Override
    public void setCitizenId(final int id)
    {
        this.citizenId = id;
    }

    @Override
    public ICitizenExperienceHandler getCitizenExperienceHandler()
    {
        return citizenExperienceHandler;
    }

    @Override
    public ICitizenInventoryHandler getCitizenInventoryHandler()
    {
        return citizenInventoryHandler;
    }

    @Override
    public void setCitizenInventoryHandler(final ICitizenInventoryHandler citizenInventoryHandler)
    {
        this.citizenInventoryHandler = citizenInventoryHandler;
    }

    @Override
    public ICitizenColonyHandler getCitizenColonyHandler()
    {
        return citizenColonyHandler;
    }

    @Override
    public void setCitizenColonyHandler(final ICitizenColonyHandler citizenColonyHandler)
    {
        this.citizenColonyHandler = citizenColonyHandler;
    }

    @Override
    public ICitizenJobHandler getCitizenJobHandler()
    {
        return citizenJobHandler;
    }

    @Override
    public ICitizenSleepHandler getCitizenSleepHandler()
    {
        return citizenSleepHandler;
    }

    public void setVisibleStatusIfNone(final VisibleCitizenStatus status)
    {
        if (getCitizenData().getStatus() == null)
        {
            getCitizenData().setVisibleStatus(status);
        }
    }

    @Override
    public float getRotationYaw()
    {
        return this.rotationYaw;
    }

    @Override
    public float getRotationPitch()
    {
        return this.rotationPitch;
    }

    @Override
    public boolean isDead()
    {
        return this.isDead;
    }

    @Override
    public void setCitizenSleepHandler(final ICitizenSleepHandler citizenSleepHandler)
    {
        this.citizenSleepHandler = citizenSleepHandler;
    }

    @Override
    public void setCitizenJobHandler(final ICitizenJobHandler citizenJobHandler)
    {
        this.citizenJobHandler = citizenJobHandler;
    }

    @Override
    public void setCitizenExperienceHandler(final ICitizenExperienceHandler citizenExperienceHandler)
    {
        this.citizenExperienceHandler = citizenExperienceHandler;
    }

    @Override
    public boolean attackEntityFrom(@NotNull final DamageSource source, final float damage)
    {
        if (handleInWallDamage(source))
        {
            return false;
        }

        final Entity sourceEntity = source.getEntity();
        if (!checkIfValidDamageSource(source, damage))
        {
            return false;
        }

        if (getCitizenJobHandler().getColonyJob() != null && getCitizenJobHandler().getColonyJob().ignoresDamage(source))
        {
            return false;
        }

        if (getCitizenColonyHandler().getColonyOrRegister() == null)
        {
            return super.attackEntityFrom(source, damage);
        }

        return handleDamagePerformed(source, damage, sourceEntity);
    }

    private boolean handleInWallDamage(@NotNull final DamageSource source)
    {
        if (source == DamageSource.inWall)
        {
            TeleportHelper.teleportCitizen(this, worldObj, (int)posX, (int)posY, (int)posZ);
            return true;
        }
        // [1.7.10] Compatibility.getDynamicTreeDamage() check retained if Compat provides it
        return citizenSleepHandler.isAsleep() && source == DamageSource.inWall || this.isEntityInvulnerable();
    }

    private boolean checkIfValidDamageSource(final DamageSource source, final float damage)
    {
        final Entity sourceEntity = source.getEntity();
        if (sourceEntity instanceof EntityCitizen)
        {
            if (((EntityCitizen) sourceEntity).citizenColonyHandler.getColonyId() == citizenColonyHandler.getColonyId())
            {
                return false;
            }

            final IColony attackerColony = ((EntityCitizen) sourceEntity).citizenColonyHandler.getColonyOrRegister();
            if (attackerColony != null && citizenColonyHandler.getColonyOrRegister() != null && MineColonies.getConfig().getServer().pvp_mode.get())
            {
                final IPermissions permission = attackerColony.getPermissions();
                citizenColonyHandler.getColonyOrRegister().getPermissions().addPlayer(permission.getOwner(), permission.getOwnerName(), permission.getRank(permission.HOSTILE_RANK_ID));
            }
        }

        if (sourceEntity instanceof EntityPlayer)
        {
            if (sourceEntity instanceof EntityPlayerMP)
            {
                if (citizenColonyHandler.getColonyOrRegister().getRaiderManager().isRaided())
                {
                    return false;
                }
                if (damage > 1 && !getCitizenColonyHandler().getColonyOrRegister().getPermissions().hasPermission((EntityPlayer) sourceEntity, Action.HURT_CITIZEN))
                {
                    return false;
                }
                if (getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard)
                {
                    return IGuardBuilding.checkIfGuardShouldTakeDamage(this, (EntityPlayer) sourceEntity);
                }
            }
            else
            {
                final IColonyView colonyView = IColonyManager.getInstance().getColonyView(getCitizenColonyHandler().getColonyId(), worldObj.provider.dimensionId);
                return damage <= 1 || colonyView == null || colonyView.getPermissions().hasPermission((EntityPlayer) sourceEntity, Action.HURT_CITIZEN);
            }
        }
        return true;
    }

    @Override
    public float getSpeed()
    {
        return (float) Math.min(MAX_SPEED_FACTOR, super.getSpeed());
    }

    private boolean handleDamagePerformed(@NotNull final DamageSource source, final float damage, final Entity sourceEntity)
    {
        float damageInc = Math.min(damage, (getMaxHealth() * 0.2f));

        if (citizenJobHandler.getColonyJob() instanceof JobNetherWorker && citizenData != null
              && source.damageType != null && source.damageType.equals("nether"))
        {
            damageInc = damage;
        }

        if (!worldObj.isRemote && !this.isInvisible() && source != DamageSource.fall)
        {
            performMoveAway(sourceEntity);
        }
        setLastAttacker(source.getEntity() instanceof EntityLivingBase ? (EntityLivingBase) source.getEntity() : null);

        if (!worldObj.isRemote)
        {
            if (citizenJobHandler.getColonyJob() instanceof AbstractJobGuard && citizenData != null)
            {
                if (citizenJobHandler.getColonyJob() instanceof JobKnight)
                {
                    if (citizenColonyHandler.getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(BLOCK_ATTACKS) > 0)
                    {
                        if (getRNG().nextDouble() < citizenColonyHandler.getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(BLOCK_ATTACKS))
                        {
                            return false;
                        }
                    }
                }

                if (citizenData.getWorkBuilding() instanceof AbstractBuildingGuards && ((AbstractBuildingGuards) citizenData.getWorkBuilding()).shallRetrieveOnLowHealth()
                      && getHealth() < ((int) getMaxHealth() * 0.2D))
                {
                    damageInc *= 1 - citizenColonyHandler.getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(FLEEING_DAMAGE);
                }
            }
        }

        if (!super.attackEntityFrom(source, damageInc))
        {
            return false;
        }

        if (source.getEntity() instanceof EntityLivingBase)
        {
            threatTable.addThreat((EntityLivingBase) source.getEntity(), (int) damageInc);
        }

        if (source.getEntity() instanceof EntityPlayer)
        {
            // [1.7.10] Environmental damage (fire, lava, etc.) handled in compatibility layer
            return true;
        }

        if (!worldObj.isRemote)
        {
            CitizenItemUtils.updateArmorDamage(this, damageInc);
            if (citizenData != null)
            {
                getCitizenData().getCitizenHappinessHandler().addModifier(new ExpirationBasedHappinessModifier(DAMAGE, 2.0, new StaticHappinessSupplier(0.0), 1));
            }
        }

        return true;
    }

    private void performMoveAway(@Nullable final Entity attacker)
    {
        if (!(attacker instanceof EntityLivingBase) &&
              (!(getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard) || getCitizenJobHandler().getColonyJob().canAIBeInterrupted()))
        {
            EntityNavigationUtils.walkAwayFrom(this, (int)posX, (int)posY, (int)posZ, 5, INITIAL_RUN_SPEED_AVOID);
            return;
        }

        if (attacker == null)
        {
            return;
        }

        if (getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard)
        {
            // 30 Blocks range
            callForHelp(attacker, 900);
            return;
        }

        citizenAI.addTransition(new AIOneTimeEventTarget<>(CitizenAIState.FLEE));
        callForHelp(attacker, MAX_GUARD_CALL_RANGE);
        EntityNavigationUtils.walkAwayFrom(this, (int)attacker.posX, (int)attacker.posY, (int)attacker.posZ, 15, INITIAL_RUN_SPEED_AVOID);
    }

    @Override
    public void callForHelp(final Entity attacker, final int guardHelpRange)
    {
        if (!(attacker instanceof EntityLivingBase) || callForHelpCooldown != 0)
        {
            return;
        }

        // Don't call for help when a guard gets woken up
        if (citizenJobHandler.getColonyJob() instanceof AbstractJobGuard && citizenJobHandler.getColonyJob(AbstractJobGuard.class).isAsleep())
        {
            return;
        }

        callForHelpCooldown = CALL_HELP_CD;

        List<AbstractEntityCitizen> possibleGuards = new ArrayList<>();

        for (final ICitizenData entry : getCitizenColonyHandler().getColonyOrRegister().getCitizenManager().getCitizens())
        {
            if (entry.getEntity().isPresent())
            {
                // Checking for guard nearby
                if (entry.getJob() instanceof AbstractJobGuard && entry.getId() != citizenData.getId()
                      && BlockPosUtil.getDistanceSquared2(entry.getEntity().get(), this) < guardHelpRange && entry.getJob().getWorkerAI() != null)
                {
                    final ThreatTable table = ((EntityCitizen) entry.getEntity().get()).getThreatTable();
                    table.addThreat((EntityLivingBase) attacker, 0);
                    if (((AbstractEntityAIGuard<?, ?>) entry.getJob().getWorkerAI()).canHelp((int)attacker.posX, (int)attacker.posY, (int)attacker.posZ))
                    {
                        possibleGuards.add(entry.getEntity().get());
                    }
                }
            }
        }

        Collections.sort(possibleGuards, Comparator.comparingDouble(guard -> guard.getDistanceSqToEntity(this)));

        for (int i = 0; i < possibleGuards.size() && i <= CALL_TO_HELP_AMOUNT; i++)
        {
            ((AbstractEntityAIGuard<?, ?>) possibleGuards.get(i).getCitizenData().getJob().getWorkerAI()).startHelpCitizen((EntityLivingBase) attacker);
        }
    }

    @Override
    protected void applyEntityCollision(final Entity entity)
    {
        if (!citizenSleepHandler.isAsleep())
        {
            super.applyEntityCollision(entity);
        }

        if (!worldObj.isRemote && getCitizenData() != null && entity instanceof AbstractEntityCitizen otherCitizen && otherCitizen.getCitizenData() != null)
        {
            getCitizenData().getCitizenDiseaseHandler().onCollission(otherCitizen.getCitizenData());
        }
    }

    @Override
    public void onCollideWithPlayer(final EntityPlayer player)
    {
        super.onCollideWithPlayer(player);
        if (citizenJobHandler.getColonyJob() != null && citizenJobHandler.getColonyJob().getWorkerAI() instanceof AbstractEntityAIBasic && !citizenJobHandler.getColonyJob()
                                                                                                                                           .isGuard())
        {
            ((AbstractEntityAIBasic) citizenJobHandler.getColonyJob().getWorkerAI()).setDelay(TICKS_SECOND * 3);
        }
    }

    @Override
    public float getScale()
    {
        return this.isBaby() ? 0.62F : 1.0F;
    }

    @Override
    public void onDeath(@NotNull final DamageSource source)
    {
        if (citizenColonyHandler.getColonyOrRegister() != null && getCitizenData() != null)
        {
            citizenColonyHandler.getColonyOrRegister().getRaiderManager().onLostCitizen(getCitizenData());
            citizenExperienceHandler.dropExperience();
            this.setDead();

            if (!(citizenJobHandler.getColonyJob() instanceof AbstractJobGuard))
            {
                citizenColonyHandler.getColonyOrRegister()
                  .getCitizenManager()
                  .injectModifier(new ExpirationBasedHappinessModifier(HappinessConstants.DEATH, 3.0, new StaticHappinessSupplier(0.0), 3));
            }
            triggerDeathAchievement(source, citizenJobHandler.getColonyJob());

            if (!(citizenJobHandler.getColonyJob() instanceof AbstractJobGuard))
            {
                citizenColonyHandler.getColonyOrRegister().getCitizenManager().updateCitizenMourn(citizenData, true);
            }

            getCitizenColonyHandler().getColonyOrRegister().getStatisticsManager().increment(DEATH, getCitizenColonyHandler().getColonyOrRegister().getDay());

            final int[] gravePos;
            if (!isInvisible())
            {
                if (citizenColonyHandler.getColonyOrRegister().isCoordInColony(worldObj, (int)posX, (int)posY, (int)posZ))
                {
                    gravePos = getCitizenColonyHandler().getColonyOrRegister().getGraveManager().createCitizenGrave(worldObj, (int)posX, (int)posY, (int)posZ, citizenData);
                }
                else
                {
                    gravePos = null;
                    InventoryUtils.dropItemHandler(citizenData.getInventory(), worldObj, (int) posX, (int) posY, (int) posZ);
                }
            }
            else
            {
                gravePos = null;
            }

            // [1.7.10] Death message formatting - simplified; full Chat hover events not available
            if (getCitizenColonyHandler().getColonyOrRegister() != null && getCitizenData() != null)
            {
                final String deathMsg = source.getDeathMessage(this).getUnformattedText();
                MessageUtils.format(deathMsg)
                  .withPriority(MessagePriority.DANGER)
                  .sendTo(getCitizenColonyHandler().getColonyOrRegister()).forManagers();
            }

            if (citizenData.getJob() != null)
            {
                citizenData.getJob().onRemoval();
            }
            citizenColonyHandler.getColonyOrRegister().getCitizenManager().removeCivilian(getCitizenData());

            final String deathCause = source.getDeathMessage(this).getUnformattedText().replaceFirst(this.getCommandSenderName(), "Citizen");
            citizenColonyHandler.getColonyOrRegister().getEventDescriptionManager().addEventDescription(
              new CitizenDiedEvent(new int[]{(int)posX, (int)posY, (int)posZ}, citizenData.getName(), deathCause));

            IMinecoloniesAPI.getInstance().getEventBus().post(new CitizenDiedModEvent(citizenData, source));
        }
        super.onDeath(source);
    }

    @Override
    public void setDead()
    {
        super.setDead();
        IMinecoloniesAPI.getInstance().getEventBus().post(new CitizenRemovedModEvent(citizenColonyHandler.getColony(), citizenId, null));
        citizenColonyHandler.onCitizenRemoved();
    }

    private void triggerDeathAchievement(final DamageSource source, final IJob<?> job)
    {
        if (job != null)
        {
            job.triggerDeathAchievement(source, this);
        }
    }

    @Override
    protected void dropEquipment(final boolean recentlyHit, final int lootingModifier)
    {
        for (int i = 0; i < getInventoryCitizen().getSlots(); i++)
        {
            final ItemStack itemstack = getCitizenData().getInventory().getStackInSlot(i);
            if (ItemStackUtils.getSize(itemstack) > 0)
            {
                CitizenItemUtils.entityDropItem(this, itemstack);
            }
        }
    }

    @NotNull
    @Override
    public Iterable<ItemStack> getAllSlots()
    {
        if (citizenData != null)
        {
            return citizenData.getInventory().getIterableArmorAndHandInv();
        }
        else if (citizenDataView != null)
        {
            return citizenDataView.getInventory().getIterableArmorAndHandInv();
        }
        return super.getAllSlots();
    }

    // [1.7.10] getItemBySlot(EquipmentSlot) replaced by getEquipmentInSlot(int) in EntityLivingBase.
    // Override getEquipmentInSlot and getCurrentArmor as needed in rendering.

    @Override
    public int getArmorValue()
    {
        if (citizenJobHandler.getColonyJob() instanceof JobKnight)
        {
            return (int) (super.getArmorValue() * (1 + citizenColonyHandler.getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(MELEE_ARMOR)));
        }
        else if (citizenJobHandler.getColonyJob() instanceof JobRanger)
        {
            return (int) (super.getArmorValue() * (1 + citizenColonyHandler.getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(ARCHER_ARMOR)));
        }
        return super.getArmorValue();
    }

    // [1.7.10] hurtCurrentlyUsedShield / shield mechanics not present; omitted
    // [1.7.10] getCapability / ForgeCapabilities / LazyOptional removed; InventoryCitizen is returned directly

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof EntityCitizen)
        {
            final EntityCitizen citizen = (EntityCitizen) obj;
            return citizen.citizenColonyHandler.getColonyId() == this.citizenColonyHandler.getColonyId() && citizen.citizenId == this.citizenId;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        if (citizenColonyHandler == null)
        {
            return super.hashCode();
        }
        return Objects.hash(citizenId, citizenColonyHandler.getColonyId());
    }

    @Override
    public void setCustomNameTag(@Nullable final String name)
    {
        if (name != null && citizenData != null && citizenColonyHandler.getColonyOrRegister() != null)
        {
            citizenData.setName(name);
        }
        super.setCustomNameTag(name == null ? "" : name);
    }

    @Override
    protected void entityDropItem(final ItemStack stack)
    {
        /* Intentionally left empty — drops handled in dropEquipment */
    }

    // [1.7.10] requiresCustomPersistence() → isNoDespawnRequired()
    @Override
    public boolean isNoDespawnRequired()
    {
        return true;
    }

    // [1.7.10] createMenu (MenuProvider) removed; no container menus in 1.7.10 style here

    @Override
    public void setTexture()
    {
        super.setTexture();
    }

    // [1.7.10] refreshDimensions() with VoxelShapes — not applicable; removed

    @Override
    public void queueSound(@NotNull final String soundName, final int[] pos, final int length, final int repetitions)
    {
        // [1.7.10] Network sound messages not yet fully ported; stub
        // Network.getNetwork().sendToPosition(new PlaySoundForCitizenMessage(...), ...);
    }

    @Override
    public void queueSound(@NotNull final String soundName, final int[] pos, final int length, final int repetitions, final float volume, final float pitch)
    {
        // [1.7.10] stub
    }

    public boolean isActive()
    {
        return worldObj.isRemote ? entityStateController.getState() == EntityState.ACTIVE_CLIENT : entityStateController.getState() == EntityState.ACTIVE_SERVER;
    }

    @Override
    public ThreatTable getThreatTable()
    {
        return threatTable;
    }

    public ITickRateStateMachine<IState> getCitizenAI()
    {
        return citizenAI;
    }

    // [1.7.10] isSuppressingBounce() does not exist; sleeping handled elsewhere

    // [1.7.10] onSyncedDataUpdated replaced by onDataWatcherUpdate in AbstractEntityCitizen

    public boolean isCurrentlyGlowing()
    {
        return isGlowing;
    }

    public void setGlowing(final boolean isGlowing)
    {
        this.isGlowing = isGlowing;
    }

    // [1.7.10] getCombatTracker() → CitizenCombatTracker stored locally
    public CitizenCombatTracker getCombatTracker()
    {
        return combatTracker;
    }

    public void setMaxAir(final int maxAir)
    {
        this.maxAir = maxAir;
    }

    @Override
    public int getTeamId()
    {
        return citizenColonyHandler.getColonyId();
    }

    private boolean decreaseIdleSaturation()
    {
        if (citizenData != null && worldObj != null && !WorldUtil.isNight(worldObj) && !citizenSleepHandler.isAsleep())
        {
            final int buildingLevel = citizenData.getHomeBuilding() == null ? 0 : citizenData.getHomeBuilding().getBuildingLevelEquivalent();
            double decrease = switch (buildingLevel)
            {
                case 1 -> 0.2;
                case 2 -> 0.256;
                case 3 -> 0.32;
                case 4 -> 0.4;
                case 5 -> 0.5;
                default -> 0.1;
            };

            if (citizenData.getJob() != null)
            {
                decrease *= citizenData.getJob().getSaturationFactor();
            }

            if (cachedActionSaturationDecrease != 0)
            {
                decrease += Math.min(decrease / 2.0, cachedActionSaturationDecrease);
                cachedActionSaturationDecrease = 0;
            }

            if (citizenData.isChild())
            {
                decrease = decrease / 2.0;
            }
            citizenData.decreaseSaturation(decrease);
        }
        return false;
    }
}

