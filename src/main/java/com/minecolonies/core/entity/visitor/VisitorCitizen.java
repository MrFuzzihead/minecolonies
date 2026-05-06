package com.minecolonies.core.entity.visitor;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.*;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.citizenhandlers.*;
import com.minecolonies.api.inventory.InventoryCitizen;
import com.minecolonies.api.util.*;
import com.minecolonies.api.util.MessageUtils.MessagePriority;
import com.minecolonies.api.util.constant.TypeConstants;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.WindowInteraction;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.TavernBuildingModule;
import com.minecolonies.core.entity.ai.minimal.EntityAIInteractToggleAble;
import com.minecolonies.core.entity.ai.minimal.LookAtEntityGoal;
import com.minecolonies.core.entity.ai.minimal.LookAtEntityInteractGoal;
import com.minecolonies.core.entity.ai.visitor.EntityAIVisitor;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenExperienceHandler;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenInventoryHandler;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenJobHandler;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenSleepHandler;
import com.minecolonies.core.network.messages.server.colony.OpenInventoryMessage;
import com.minecolonies.core.util.citizenutils.CitizenItemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.util.ItemStackUtils.ISFOOD;
import static com.minecolonies.api.util.constant.CitizenConstants.SATURATION_DECREASE_FACTOR;
import static com.minecolonies.api.util.constant.CitizenConstants.TICKS_20;
import static com.minecolonies.api.util.constant.Constants.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_CITIZEN;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_ID;
import static com.minecolonies.api.util.constant.TranslationConstants.MESSAGE_INFO_COLONY_VISITOR_DIED;
import static com.minecolonies.api.util.constant.TranslationConstants.MESSAGE_INTERACTION_VISITOR_FOOD;
import static com.minecolonies.core.entity.ai.minimal.EntityAIInteractToggleAble.*;

/**
 * Visitor citizen entity
 */
public class VisitorCitizen extends AbstractEntityCitizen
{
    private ICitizenExperienceHandler citizenExperienceHandler;
    private int          citizenId = 0;
    @Nullable
    private ICitizenData citizenData;
    private ICitizenInventoryHandler citizenInventoryHandler;
    private ICitizenColonyHandler    citizenColonyHandler;
    private ICitizenJobHandler       citizenJobHandler;
    private ICitizenSleepHandler     citizenSleepHandler;
    private ICitizenDataView         citizenDataView;
    private ILocation                location = null;

    /**
     * Constructor for a new visitor citizen typed entity.
     *
     * @param world the world.
     */
    public VisitorCitizen(final World world)
    {
        super(world);
        this.citizenInventoryHandler  = new CitizenInventoryHandler(this);
        this.citizenColonyHandler     = new VisitorColonyHandler(this);
        this.citizenJobHandler        = new CitizenJobHandler(this);
        this.citizenSleepHandler      = new CitizenSleepHandler(this);
        this.citizenExperienceHandler = new CitizenExperienceHandler(this);
        // [1.7.10] MovementHandler not yet ported
        // this.moveControl = new MovementHandler(this);
        this.setAlwaysRenderNameTag(MineColonies.getConfig().getServer().alwaysRenderNameTag.get());
        initTasks();
    }

    private void initTasks()
    {
        int priority = 0;
        this.goalSelector.addGoal(priority, new EntityAIFloat(this));
        this.goalSelector.addGoal(++priority, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(priority, new EntityAIInteractToggleAble(this, FENCE_TOGGLE, TRAP_TOGGLE, DOOR_TOGGLE));
        this.goalSelector.addGoal(++priority, new LookAtEntityInteractGoal(this, EntityPlayer.class, WATCH_CLOSEST2, 0.2F));
        this.goalSelector.addGoal(++priority, new LookAtEntityInteractGoal(this, EntityCitizen.class, WATCH_CLOSEST2_FAR, WATCH_CLOSEST2_FAR_CHANCE));
        this.goalSelector.addGoal(++priority, new LookAtEntityGoal(this, EntityLivingBase.class, WATCH_CLOSEST));
        new EntityAIVisitor(this);
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
    public boolean attackEntityFrom(@NotNull final DamageSource source, final float damage)
    {
        if (!(source.getEntity() instanceof EntityCitizen) && super.attackEntityFrom(source, damage))
        {
            if (source.getEntity() instanceof EntityLivingBase && damage > 1.01f)
            {
                final IBuilding home = getCitizenData().getHomeBuilding();
                if (home != null && home.hasModule(BuildingModules.TAVERN_VISITOR))
                {
                    final TavernBuildingModule module = home.getModule(BuildingModules.TAVERN_VISITOR);
                    for (final Integer id : module.getExternalCitizens())
                    {
                        ICitizenData data = citizenColonyHandler.getColonyOrRegister().getVisitorManager().getCivilian(id);
                        if (data != null && data.getEntity().isPresent() && data.getEntity().get().getAITarget() == null)
                        {
                            data.getEntity().get().setLastAttacker((EntityLivingBase) source.getEntity());
                        }
                    }
                }

                final Entity sourceEntity = source.getEntity();
                if (sourceEntity instanceof EntityPlayer)
                {
                    if (sourceEntity instanceof EntityPlayerMP)
                    {
                        return damage <= 1 || getCitizenColonyHandler().getColonyOrRegister().getPermissions().hasPermission((EntityPlayer) sourceEntity, Action.HURT_VISITOR);
                    }
                    else
                    {
                        final IColonyView colonyView = IColonyManager.getInstance().getColonyView(getCitizenColonyHandler().getColonyId(), worldObj.provider.dimensionId);
                        return damage <= 1 || colonyView == null || colonyView.getPermissions().hasPermission((EntityPlayer) sourceEntity, Action.HURT_VISITOR);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Nullable
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
        if (data instanceof IVisitorData)
        {
            this.citizenData = (IVisitorData) data;
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
    public void setIsChild(final boolean isChild) {}

    @Override
    public void playMoveAwaySound() {}

    @Override
    public void decreaseSaturationForAction()
    {
        if (citizenData != null)
        {
            citizenData.decreaseSaturation(SATURATION_DECREASE_FACTOR);
            citizenData.markDirty(20 * 20);
        }
    }

    @Override
    public void decreaseSaturationForContinuousAction()
    {
        if (citizenData != null)
        {
            citizenData.decreaseSaturation(SATURATION_DECREASE_FACTOR / 100.0);
            citizenData.markDirty(20 * 60 * 2);
        }
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

    @Override
    public float getRotationYaw()
    {
        return rotationYaw;
    }

    @Override
    public float getRotationPitch()
    {
        return rotationPitch;
    }

    @Override
    public boolean isDead()
    {
        return this.isDead;
    }

    @Override
    public void setCitizenSleepHandler(final ICitizenSleepHandler citizenSleepHandler) {}

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
    public void callForHelp(final Entity attacker, final int guardHelpRange) {}

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

        if (directPlayerFoodInteraction(player, heldItem))
        {
            return true;
        }

        if (worldObj.isRemote)
        {
            if (player.isSneaking())
            {
                Network.getNetwork().sendToServer(new OpenInventoryMessage(iColonyView, this.getCommandSenderName(), this.getEntityId()));
            }
            else
            {
                final ICitizenDataView dataView = getCitizenDataView();
                if (dataView != null)
                {
                    new WindowInteraction(dataView).open();
                }
            }
        }
        return true;
    }

    private boolean directPlayerFoodInteraction(final EntityPlayer player, final ItemStack usedStack)
    {
        if (usedStack != null && ISFOOD.test(usedStack))
        {
            if (!worldObj.isRemote)
            {
                playSound("random.eat", 1.5f, (float) SoundUtils.getRandomPitch(getRNG()));
                // [1.7.10] ItemParticleEffectMessage stub
                ItemStackUtils.consumeFood(usedStack, this, player.inventory);
                MessageUtils.forCitizen(this, MESSAGE_INTERACTION_VISITOR_FOOD).sendTo(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public ICitizenDataView getCitizenDataView()
    {
        if (this.citizenDataView == null)
        {
            citizenColonyHandler.updateColonyClient();
            if (citizenColonyHandler.getColonyId() != 0 && citizenId != 0)
            {
                final IColonyView colonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), worldObj.provider.dimensionId);
                if (colonyView != null)
                {
                    this.citizenDataView = colonyView.getVisitor(citizenId);
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
    protected void entityInit()
    {
        super.entityInit();
        // DataWatcher entries initialised in AbstractEntityCitizen.entityInit()
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (recentlyHit > 0)
        {
            markDirty(0);
        }

        if (worldObj.isRemote)
        {
            citizenColonyHandler.updateColonyClient();
            if (citizenColonyHandler.getColonyId() != 0 && citizenId != 0 && getOffsetTicks() % TICKS_20 == 0)
            {
                final IColonyView colonyView = IColonyManager.getInstance().getColonyView(citizenColonyHandler.getColonyId(), worldObj.provider.dimensionId);
                if (colonyView != null)
                {
                    this.citizenDataView = colonyView.getVisitor(citizenId);
                    getDataWatcher().updateObject(DATA_STYLE_ID, colonyView.getTextureStyleId());
                }
            }
        }
        else
        {
            citizenColonyHandler.registerWithColony(citizenColonyHandler.getColonyId(), citizenId);
            if (ticksExisted % 500 == 0)
            {
                this.setAlwaysRenderNameTag(MineColonies.getConfig().getServer().alwaysRenderNameTag.get());
            }
        }
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
    }

    @Override
    public void onDeath(final DamageSource cause)
    {
        super.onDeath(cause);
        if (!worldObj.isRemote)
        {
            IColony colony = getCitizenColonyHandler().getColonyOrRegister();
            if (colony != null && getCitizenData() != null)
            {
                colony.getVisitorManager().removeCivilian(getCitizenData());
                if (getCitizenData().getHomeBuilding() instanceof TavernBuildingModule)
                {
                    TavernBuildingModule tavern = (TavernBuildingModule) getCitizenData().getHomeBuilding();
                    tavern.setNoVisitorTime(worldObj.rand.nextInt(5000) + 30000);
                }

                final String deathLocation = posX + ", " + posY + ", " + posZ;
                MessageUtils.format(MESSAGE_INFO_COLONY_VISITOR_DIED, getCitizenData().getName(), cause.damageType, deathLocation)
                  .withPriority(MessagePriority.DANGER)
                  .sendTo(colony)
                  .forManagers();
            }
        }
    }

    @Override
    public void setDead()
    {
        citizenColonyHandler.onCitizenRemoved();
        super.setDead();
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

    @Override
    public void queueSound(final @NotNull String soundName, final int[] pos, final int length, final int repetitions) {}

    @Override
    public void queueSound(final @NotNull String soundName, final int[] pos, final int length, final int repetitions, final float volume, final float pitch) {}

    @Override
    public int getTeamId()
    {
        return citizenColonyHandler.getColonyId();
    }
}

