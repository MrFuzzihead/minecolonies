package com.minecolonies.core.entity.mobs;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyRelated;
import com.minecolonies.api.entity.CustomGoalSelector;
import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.entity.ai.statemachine.states.IState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickRateStateMachine;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.TickingTransition;
import com.minecolonies.api.entity.other.AbstractFastMinecoloniesEntity;
import com.minecolonies.api.sounds.MercenarySounds;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.core.entity.ai.minimal.EntityAIInteractToggleAble;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.pathfinding.navigation.AbstractAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.navigation.EntityNavigationUtils;
import com.minecolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.proxy.GeneralEntityWalkToProxy;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
// removed: import net.minecraft.init.Enchantments; -- 1.7.10 uses Enchantment.X directly
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.minecolonies.api.util.constant.CitizenConstants.BASE_PATHFINDING_RANGE;
import static com.minecolonies.api.util.constant.Constants.TICKS_FOURTY_MIN;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_COLONY_ID;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_TIME;
import static com.minecolonies.api.util.constant.TranslationConstants.MESSAGE_INFO_COLONY_MERCENARY_STEAL_CITIZEN;
import static com.minecolonies.core.colony.events.raid.RaiderConstants.FOLLOW_RANGE;
import static com.minecolonies.core.entity.ai.minimal.EntityAIInteractToggleAble.*;


/**
 * Class for Mercenary entities, which can be spawned to protect the colony.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class EntityMercenary extends AbstractFastMinecoloniesEntity implements IColonyRelated
{
    private static final int    SLAP_INTERVAL = 100;
    private static final String ENTITY_NAME   = "Mercenary";

    private IColony                      colony;
    private AbstractAdvancedPathNavigate newNavigator;
    private GeneralEntityWalkToProxy     proxy;
    private int                          slapTimer = 0;

    private final Random rand = new Random();

    private long                          worldTimeAtSpawn = 0;
    private boolean                       isLeader         = false;
    private List<EntityMercenary>         soldiers         = new ArrayList<>();
    private int                           spawnEventTime   = 0;
    private boolean                       doSpawnEvent     = false;
    private ITickRateStateMachine<IState> stateMachine;
    private int                           colonyId;

    /**
     * Constructor method for Mercenaries.
     *
     * @param world the world.
     */
    public EntityMercenary(final World world)
    {
        super(world);

        this.goalSelector   = new CustomGoalSelector(this.tasks);
        this.targetSelector = new CustomGoalSelector(this.targetTasks);
        this.goalSelector.addGoal(0, new EntityAIFloat(this));
        this.goalSelector.addGoal(1, new EntityMercenaryAI(this));
        this.goalSelector.addGoal(4, new EntityAIInteractToggleAble(this, FENCE_TOGGLE, TRAP_TOGGLE, DOOR_TOGGLE));
        // [1.7.10] NearestAttackableTargetGoal → EntityAINearestAttackableTarget; Llama doesn't exist in 1.7.10
        this.targetSelector.addGoal(5, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, false,
          false, e -> e instanceof IMob));

        setAlwaysRenderNameTag(true);

        // Equipment setup
        final ItemStack mainhand = new ItemStack(Items.golden_sword, 1, 0);
        mainhand.addEnchantment(Enchantment.fireAspect, 1);
        this.setCurrentItemOrArmor(0, mainhand);

        final ItemStack helmet = new ItemStack(Items.diamond_helmet, 1, 0);
        helmet.addEnchantment(Enchantment.protection, 4);
        this.setCurrentItemOrArmor(4, helmet);

        final ItemStack chest = new ItemStack(Items.golden_chestplate, 1, 0);
        chest.addEnchantment(Enchantment.protection, 4);
        this.setCurrentItemOrArmor(3, chest);

        final ItemStack legs = new ItemStack(Items.chainmail_leggings, 1, 0);
        this.setCurrentItemOrArmor(2, legs);

        final ItemStack boots = new ItemStack(Items.chainmail_boots, 1, 0);
        this.setCurrentItemOrArmor(1, boots);

        // [1.7.10] Attributes set in attribute map
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.followRange).setBaseValue(FOLLOW_RANGE);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.maxHealth).setBaseValue(60);
        this.setHealth(this.getMaxHealth());

        stateMachine = new TickRateStateMachine<>(EntityMercenaryAI.State.INIT, this::handleStateException);
        stateMachine.addTransition(new TickingTransition<>(EntityMercenaryAI.State.INIT, this::isInitialized, () -> EntityMercenaryAI.State.SPAWN_EVENT, 20));
        stateMachine.addTransition(new TickingTransition<>(EntityMercenaryAI.State.SPAWN_EVENT, this::spawnEvent, () -> EntityMercenaryAI.State.ALIVE, 30));
        stateMachine.addTransition(new TickingTransition<>(EntityMercenaryAI.State.ALIVE, this::shouldDespawn, () -> EntityMercenaryAI.State.DEAD, 100));
        stateMachine.addTransition(new TickingTransition<>(EntityMercenaryAI.State.DEAD, () -> true, this::getState, 500));
    }

    private void handleStateException(final RuntimeException e)
    {
        Log.getLogger().warn("Mercenary entity threw an exception:", e);
    }

    private boolean shouldDespawn()
    {
        if (worldObj == null || worldObj.getTotalWorldTime() - worldTimeAtSpawn > TICKS_FOURTY_MIN || colony == null || this.isInvisible())
        {
            this.setDead();
            return true;
        }
        return false;
    }

    private boolean isInitialized()
    {
        if (worldTimeAtSpawn == 0)
        {
            worldTimeAtSpawn = worldObj.getTotalWorldTime();
        }
        return worldObj != null && colony != null && isAlive() && !isInvisible();
    }

    private boolean spawnEvent()
    {
        if (spawnEventTime > 0)
        {
            spawnEventTime--;
        }

        if (!doSpawnEvent || spawnEventTime == 0)
        {
            return true;
        }

        if (!isLeader)
        {
            return false;
        }

        if (!getNavigator().noPath())
        {
            return false;
        }

        final EntityMercenary first = soldiers.get(0);
        final EntityMercenary last  = soldiers.get(soldiers.size() - 1);
        final int[] firstPos = {(int) first.posX, (int) first.posY, (int) first.posZ + 1};
        final int[] lastPos  = {(int) last.posX, (int) last.posY, (int) last.posZ + 1};

        playSound(MercenarySounds.mercenaryCelebrate, 2.0f, 1.0f);
        if ((int) posX == firstPos[0] && (int) posZ == firstPos[2])
        {
            EntityNavigationUtils.walkToPos(this, lastPos[0], lastPos[1], lastPos[2], 2, true, 0.5);
        }
        else
        {
            EntityNavigationUtils.walkToPos(this, firstPos[0], firstPos[1], firstPos[2], 2, true, 0.5);
        }

        return false;
    }

    public void setDoSpawnEvent()
    {
        doSpawnEvent = true;
        spawnEventTime = 15;
    }

    public void setLeader(final List<EntityMercenary> soldiers)
    {
        this.soldiers = soldiers;
        isLeader = true;
        doSpawnEvent = true;
        spawnEventTime = 17;
    }

    public IState getState()
    {
        return stateMachine.getState();
    }

    @Override
    protected void playStepSound(final int x, final int y, final int z, final Block block)
    {
        this.playSound(MercenarySounds.mercenaryStep, 0.45F, 1.0F);
    }

    @Override
    protected String getHurtSound()
    {
        return MercenarySounds.mercenaryHurt;
    }

    @Override
    protected String getDeathSound()
    {
        return MercenarySounds.mercenaryDie;
    }

    @Nullable
    @Override
    protected String getLivingSound()
    {
        return MercenarySounds.mercenarySay;
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound compound)
    {
        compound.setLong(TAG_TIME, worldTimeAtSpawn);
        compound.setInteger(TAG_COLONY_ID, this.colony == null ? 0 : colony.getID());
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound compound)
    {
        worldTimeAtSpawn = compound.getLong(TAG_TIME);
        if (compound.hasKey(TAG_COLONY_ID))
        {
            colonyId = compound.getInteger(TAG_COLONY_ID);
            if (colonyId != 0)
            {
                setColony(IColonyManager.getInstance().getColonyByWorld(colonyId, worldObj));
            }
        }
        super.readEntityFromNBT(compound);
    }

    @Override
    public String getCommandSenderName()
    {
        return ENTITY_NAME;
    }

    @Override
    public void registerWithColony()
    {
        // Does not need to register.
    }

    @Override
    public IColony getColony()
    {
        return colony;
    }

    public void setColony(final IColony colony)
    {
        if (colony != null)
        {
            this.colony = colony;
            this.registerWithColony();
        }
    }

    @Override
    public boolean attackEntityFrom(final DamageSource source, final float damage)
    {
        if (source.getEntity() instanceof EntityLivingBase)
        {
            this.setAttackTarget((EntityLivingBase) source.getEntity());
        }
        return super.attackEntityFrom(source, damage);
    }

    @Override
    protected void applyEntityCollision(final Entity entityIn)
    {
        if (slapTimer == 0 && entityIn instanceof EntityPlayer)
        {
            slapTimer = SLAP_INTERVAL;
            // [1.7.10] custom SLAP damage source; use generic damage
            entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0f);
            // [1.7.10] swing offhand not available; skip swing
        }

        if (slapTimer == 0 && entityIn instanceof EntityCitizen && colony != null && ((EntityCitizen) entityIn).isActive())
        {
            slapTimer = SLAP_INTERVAL;
            final net.minecraftforge.items.IItemHandler handler = ((EntityCitizen) entityIn).getItemHandlerCitizen();
            final ItemStack stack = handler.extractItem(rand.nextInt(handler.getSlots()), 5, false);
            if (!ItemStackUtils.isEmpty(stack))
            {
                MessageUtils.format(MESSAGE_INFO_COLONY_MERCENARY_STEAL_CITIZEN, entityIn.getCommandSenderName(), stack.getDisplayName()).sendTo(colony).forAllPlayers();
            }
        }
    }

    public GeneralEntityWalkToProxy getProxy()
    {
        if (proxy == null)
        {
            proxy = new GeneralEntityWalkToProxy(this);
        }
        return proxy;
    }

    @NotNull
    @Override
    public AbstractAdvancedPathNavigate getAdvancedNavigator()
    {
        if (this.newNavigator == null)
        {
            this.newNavigator = new MinecoloniesAdvancedPathNavigate(this, worldObj);
            this.navigator = newNavigator;
            this.newNavigator.setCanSwim(true);
            this.newNavigator.setEnterDoors(true);
        }
        return newNavigator;
    }

    @Override
    public void onLivingUpdate()
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            stateMachine.tick();
        }
        if (slapTimer > 0)
        {
            slapTimer--;
        }
        // [1.7.10] updateSwingTime() → swing timer is handled by updateArmSwingProgress()
        super.onLivingUpdate();
    }

    // [1.7.10] requiresCustomPersistence() → isNoDespawnRequired()
    @Override
    public boolean isNoDespawnRequired()
    {
        return true;
    }

    /**
     * Spawns mercenaries in the given colony.
     *
     * @param colony given colony
     */
    public static void spawnMercenariesInColony(@NotNull final IColony colony)
    {
        final World world = colony.getWorld();

        if (colony.getMercenaryUseTime() != 0 && world.getTotalWorldTime() - colony.getMercenaryUseTime() < TICKS_FOURTY_MIN)
        {
            return;
        }

        colony.usedMercenaries();

        int amountOfMercenaries = colony.getCitizenManager().getCurrentCitizenCount();
        amountOfMercenaries = amountOfMercenaries / 10;
        amountOfMercenaries += 3;

        final int[] spawn = EntityMercenary.findMercenarySpawnPos(colony, amountOfMercenaries);

        final List<EntityMercenary> soldiers = new ArrayList<>();
        for (int i = 0; i < amountOfMercenaries; i++)
        {
            final EntityMercenary merc = new EntityMercenary(world);
            merc.setColony(colony);
            merc.setPosition(spawn[0] + i, spawn[1], spawn[2]);
            merc.setDoSpawnEvent();
            soldiers.add(merc);
            world.spawnEntityInWorld(merc);
        }

        // spawn leader for the event.
        final EntityMercenary merc = new EntityMercenary(world);
        merc.setColony(colony);
        merc.setPosition(spawn[0], spawn[1], spawn[2] + 1);
        merc.setLeader(soldiers);
        world.spawnEntityInWorld(merc);
    }

    private static int[] findMercenarySpawnPos(final IColony colony, final int amountOfMercenaries)
    {
        final Tuple<int[], int[]> buildingArea = colony.getServerBuildingManager().getTownHall().getCorners();
        final int[] a = buildingArea.getA();
        final int[] b = buildingArea.getB();
        int spawnX = (b[0] + a[0]) / 2;
        int spawnZ = a[2];
        int height = colony.getWorld().getHeightValue(spawnX, spawnZ);
        if (height > b[1])
        {
            height = a[1] + 1;
        }
        int[] spawn = {spawnX, height, spawnZ};

        for (int i = -3; i < 4; i++)
        {
            if (isValidSpawnForMercenaries(colony.getWorld(), new int[]{spawn[0], spawn[1], spawn[2] + i}, amountOfMercenaries))
            {
                spawn = new int[]{spawn[0], spawn[1], spawn[2] + i};
                break;
            }
        }

        return spawn;
    }

    private static boolean isValidSpawnForMercenaries(final World world, final int[] spawn, final int amountOfMercenaries)
    {
        for (int i = 0; i < amountOfMercenaries; i++)
        {
            if (!world.isAirBlock(spawn[0] + i, spawn[1] + 1, spawn[2]) || !world.isAirBlock(spawn[0] + i, spawn[1] + 1, spawn[2] + 1))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getTeamId()
    {
        return colonyId;
    }
}









