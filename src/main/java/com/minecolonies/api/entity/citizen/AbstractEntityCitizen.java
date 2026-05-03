package com.minecolonies.api.entity.citizen;

import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.client.render.modeltype.registry.IModelTypeRegistry;
import com.minecolonies.api.client.render.modeltype.IModelType;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.entity.ai.statemachine.states.EntityState;
import com.minecolonies.api.entity.ai.statemachine.states.IState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickRateStateMachine;
import com.minecolonies.api.entity.citizen.citizenhandlers.*;
import com.minecolonies.api.entity.other.MinecoloniesMinecart;
import com.minecolonies.api.entity.pathfinding.registry.IPathNavigateRegistry;
import com.minecolonies.api.inventory.InventoryCitizen;
import com.minecolonies.api.sounds.EventType;
import com.minecolonies.api.util.CompatibilityUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.api.util.constant.ColonyConstants;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.navigation.PathingStuckHandler;
// [1.7.10] items shim in com.minecolonies.api.shim

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.CitizenConstants.*;

/**
 * The abstract citizen entity.
 * Ported from 1.21 to 1.7.10.
 * DataWatcher replaces SynchedEntityData/int (EntityDataAccessor removed).
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public abstract class AbstractEntityCitizen extends AbstractCivilianEntity
{
    public static final int ENTITY_AI_TICKRATE = 5;

    private static final double CITIZEN_SWIM_BONUS = 2.0;

    // DataWatcher watch IDs (17–30)
    public static final int DW_TEXTURE       = 17;
    public static final int DW_LEVEL         = 18;
    public static final int DW_IS_FEMALE     = 19;
    public static final int DW_COLONY_ID     = 20;
    public static final int DW_CITIZEN_ID    = 21;
    public static final int DW_MODEL         = 22;
    public static final int DW_RENDER_META   = 23;
    public static final int DW_IS_ASLEEP     = 24;
    public static final int DW_IS_CHILD      = 25;
    public static final int DW_BED_X         = 26;
    public static final int DW_BED_Y         = 27;
    public static final int DW_BED_Z         = 28;
    public static final int DW_STYLE         = 29;
    public static final int DW_JOB           = 30;

    /** The default model. */
    private ResourceLocation modelId = ModModelTypes.SETTLER_ID;

    /** The texture id. */
    private int textureId;

    /** Additional render data. */
    private String renderMetadata = "";

    /** Whether the texture needs to be recomputed. */
    private boolean textureDirty = true;

    /** Computed texture. */
    private ResourceLocation texture;

    /** The gender, true if female. */
    private boolean female;

    private AbstractAdvancedPathNavigate pathNavigate;

    /** Counts entity collisions */
    private int collisionCounter = ColonyConstants.rand.nextInt(100);

    private static final int COLL_THRESHOLD = 100;

    private boolean isEquipmentDirty = true;

    /** The AI for citizens, controlling different global states */
    protected ITickRateStateMachine<IState> entityStateController = new TickRateStateMachine<>(EntityState.INIT,
      e -> Log.getLogger().warn("Citizen state controller exception", e), ENTITY_AI_TICKRATE);

    /**
     * Constructor for a new citizen typed entity.
     *
     * @param world the world.
     */
    public AbstractEntityCitizen(final World world)
    {
        super(world);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(DW_TEXTURE, 0);
        dataWatcher.addObject(DW_LEVEL, 0);
        dataWatcher.addObject(DW_IS_FEMALE, 0);
        dataWatcher.addObject(DW_COLONY_ID, 0);
        dataWatcher.addObject(DW_CITIZEN_ID, 0);
        dataWatcher.addObject(DW_MODEL, ModModelTypes.SETTLER_ID.toString());
        dataWatcher.addObject(DW_RENDER_META, "");
        dataWatcher.addObject(DW_IS_ASLEEP, (byte) 0);
        dataWatcher.addObject(DW_IS_CHILD, (byte) 0);
        dataWatcher.addObject(DW_BED_X, 0);
        dataWatcher.addObject(DW_BED_Y, 0);
        dataWatcher.addObject(DW_BED_Z, 0);
        dataWatcher.addObject(DW_STYLE, "default");
        dataWatcher.addObject(DW_JOB, "");
    }

    /**
     * Get the default attributes with their values.
     * In 1.7.10 these are set in entity constructor via registerAttribute/setBaseValue.
     */
    protected void setupBaseAttributes()
    {
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(BASE_MAX_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BASE_MOVEMENT_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(BASE_PATHFINDING_RANGE);
    }

    public int getTicksExisted()
    {
        return ticksExisted;
    }

    @Override
    public boolean canPickUpLoot()
    {
        return false;
    }

    /**
     * Calculate adjusted damage (1.7.10 approximation).
     */
    public float calculateDamageAfterAbsorbs(final net.minecraft.util.DamageSource source, final float damage)
    {
        float newDamage = ISpecialArmor.ArmorProperties.applyArmor(this, getCurrentArmor(), source, damage);
        // no magic absorb equivalent in 1.7.10 base, return as is
        return newDamage;
    }

    /**
     * Right-click interaction on citizen.
     *
     * @param player the interacting player.
     * @return true if handled.
     */
    @Override
    public boolean interact(final EntityPlayer player)
    {
        if (!worldObj.isRemote)
        {
            if (getCitizenData() != null && getCitizenData().isIdleAtJob())
            {
                SoundUtils.playSoundAtCitizenWith(worldObj, (int) posX, (int) posY, (int) posZ, EventType.MISSING_EQUIPMENT, getCitizenData(), 100);
            }
            else
            {
                SoundUtils.playSoundAtCitizenWith(worldObj, (int) posX, (int) posY, (int) posZ, EventType.INTERACTION, getCitizenData(), 100);
            }
        }
        return super.interact(player);
    }

    /**
     * Sets the textures of all citizens and distinguishes between male and female.
     */
    public void setTexture()
    {
        if (!worldObj.isRemote)
        {
            return;
        }

        final IModelType modelType = IModelTypeRegistry.getInstance().getModelType(getModelType());
        if (modelType == null)
        {
            Log.getLogger().error("Null model type for: " + getModelType() + " of: " + this);
            textureDirty = false;
            return;
        }

        texture = modelType.getTexture(this);
        textureDirty = false;
    }

    /**
     * Get the citizen data view.
     *
     * @return the view.
     */
    public abstract ICitizenDataView getCitizenDataView();

    /**
     * Getter of the resource location of the texture.
     *
     * @return location of the texture.
     */
    @NotNull
    public ResourceLocation getTexture()
    {
        if (texture == null || textureDirty)
        {
            setTexture();
        }
        return texture;
    }

    /**
     * Set the texture dirty.
     */
    public void setTextureDirty()
    {
        this.textureDirty = true;
    }

    /**
     * Get the model assigned to the citizen.
     *
     * @return the model.
     */
    public ResourceLocation getModelType()
    {
        return modelId;
    }

    /**
     * Getter which checks if the citizen is female.
     *
     * @return true if female.
     */
    public boolean isFemale()
    {
        return female;
    }

    /**
     * Set the gender.
     *
     * @param female true if female, false if male.
     */
    public void setFemale(final boolean female)
    {
        this.female = female;
    }

    @NotNull
    public AbstractAdvancedPathNavigate getAdvancedNavigator()
    {
        if (this.pathNavigate == null)
        {
            this.pathNavigate = IPathNavigateRegistry.getInstance().getNavigateFor(this);
            this.pathNavigate.setCanFloat(true);
            this.pathNavigate.setSwimSpeedFactor(CITIZEN_SWIM_BONUS);
            this.pathNavigate.getPathingOptions().setEnterDoors(true);
            this.pathNavigate.getPathingOptions().setCanOpenDoors(true);
            this.pathNavigate.setStuckHandler(PathingStuckHandler.createStuckHandler().withTeleportOnFullStuck().withTeleportSteps(5));
        }
        return pathNavigate;
    }

    @Override
    public void applyEntityCollision(final Entity entityIn)
    {
        if ((collisionCounter += 2) > COLL_THRESHOLD)
        {
            if (collisionCounter > COLL_THRESHOLD * 2)
            {
                collisionCounter = 0;
            }
            return;
        }

        if (ridingEntity instanceof MinecoloniesMinecart)
        {
            return;
        }
        super.applyEntityCollision(entityIn);
    }

    @Override
    public void onPlayerCollide(final EntityPlayer player)
    {
        if (getCitizenData() == null)
        {
            super.onPlayerCollide(player);
            return;
        }

        final IJob<?> job = getCitizenData().getJob();
        if (job == null || !job.isGuard())
        {
            super.onPlayerCollide(player);
        }
        else
        {
            // guards push the player out of their way
            player.applyEntityCollision(this);
        }
    }

    @Override
    public boolean canBePushed()
    {
        if (ridingEntity instanceof MinecoloniesMinecart)
        {
            return false;
        }
        return super.canBePushed();
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (ticksExisted % ENTITY_AI_TICKRATE == 0)
        {
            entityStateController.tick();
        }
        if (collisionCounter > 0)
        {
            collisionCounter--;
        }
    }

    /**
     * Set the rotation of the citizen.
     *
     * @param yaw   the rotation yaw.
     * @param pitch the rotation pitch.
     */
    public void setOwnRotation(final float yaw, final float pitch)
    {
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
    }

    /**
     * Set the model id.
     *
     * @param model the model.
     */
    public void setModelId(final ResourceLocation model)
    {
        this.modelId = model;
    }

    /**
     * Set the render meta data.
     *
     * @param renderMetadata the metadata to set.
     */
    public void setRenderMetadata(final String renderMetadata)
    {
        if (renderMetadata.equals(getRenderMetadata()))
        {
            return;
        }
        this.renderMetadata = renderMetadata;
        dataWatcher.updateObject(DW_RENDER_META, getRenderMetadata());
    }

    /**
     * Getter for the texture id.
     *
     * @return the texture id.
     */
    public int getTextureId()
    {
        return this.textureId;
    }

    /**
     * Set the texture id.
     *
     * @param textureId the id of the texture.
     */
    public void setTextureId(final int textureId)
    {
        this.textureId = textureId;
        dataWatcher.updateObject(DW_TEXTURE, textureId);
    }

    /**
     * Getter for the render metadata.
     *
     * @return the meta data.
     */
    public String getRenderMetadata()
    {
        return renderMetadata;
    }

    public int getOffsetTicks()
    {
        return this.ticksExisted + OFFSET_TICK_MULTIPLIER * this.getEntityId();
    }

    /**
     * Check if recently hit.
     *
     * @return the count of how often.
     */
    public int getRecentlyHit()
    {
        return recentlyHit;
    }

    /**
     * Get the ILocation of the citizen.
     *
     * @return an ILocation object which contains the dimension and is unique.
     */
    public abstract ILocation getLocation();

    /**
     * Getter for the citizendata.
     *
     * @return the data.
     */
    public abstract ICitizenData getCitizenData();

    /**
     * Return this citizens inventory.
     *
     * @return the inventory this citizen has.
     */
    @NotNull
    public abstract InventoryCitizen getInventoryCitizen();

    @NotNull
    public abstract net.minecraftforge.items.IItemHandler getItemHandlerCitizen();

    /**
     * Sets whether this entity is a child.
     *
     * @param isChild boolean
     */
    public abstract void setIsChild(boolean isChild);

    /**
     * Play move away sound when running from an entity.
     */
    public abstract void playMoveAwaySound();

    /**
     * Decrease the saturation of the citizen for 1 action.
     */
    public abstract void decreaseSaturationForAction();

    /**
     * Decrease the saturation of the citizen for 1 continuous action.
     */
    public abstract void decreaseSaturationForContinuousAction();

    public abstract ICitizenExperienceHandler getCitizenExperienceHandler();

    public abstract ICitizenInventoryHandler getCitizenInventoryHandler();

    public abstract void setCitizenInventoryHandler(ICitizenInventoryHandler citizenInventoryHandler);

    public abstract ICitizenColonyHandler getCitizenColonyHandler();

    public abstract void setCitizenColonyHandler(ICitizenColonyHandler citizenColonyHandler);

    public abstract ICitizenJobHandler getCitizenJobHandler();

    public abstract ICitizenSleepHandler getCitizenSleepHandler();

    public abstract float getRotationYaw();

    public abstract float getRotationPitch();

    public abstract boolean isDead();

    public abstract void setCitizenSleepHandler(ICitizenSleepHandler citizenSleepHandler);

    public abstract void setCitizenJobHandler(ICitizenJobHandler citizenJobHandler);

    public abstract void setCitizenExperienceHandler(ICitizenExperienceHandler citizenExperienceHandler);

    /**
     * Calls a guard for help against an attacker.
     *
     * @param attacker       the attacking entity
     * @param guardHelpRange the squaredistance in which we search for nearby guards
     */
    public abstract void callForHelp(final Entity attacker, final int guardHelpRange);

    /**
     * Mark the equipment as dirty.
     */
    public void markEquipmentDirty()
    {
        this.isEquipmentDirty = true;
    }

    @Override
    public boolean isPushedByWater()
    {
        return false;
    }

    /**
     * Get the entities state controller.
     *
     * @return the state machine.
     */
    public ITickRateStateMachine<IState> getEntityStateController()
    {
        return entityStateController;
    }

    @Override
    public boolean isSleeping()
    {
        return getCitizenSleepHandler().isAsleep();
    }

    @Override
    public int getTeamId()
    {
        if (getCitizenColonyHandler().getColony() == null)
        {
            return -1;
        }
        return getCitizenColonyHandler().getColonyId();
    }

    @Override
    public String getCommandSenderName()
    {
        if (getCitizenColonyHandler().getColony() == null)
        {
            return super.getCommandSenderName();
        }
        return super.getCommandSenderName();
    }

    /**
     * Check if the citizen is active (has data, alive).
     *
     * @return true if active.
     */
    public boolean isActive()
    {
        return getCitizenData() != null && isEntityAlive();
    }
}



