package com.minecolonies.api.entity.mobs;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.mobs.barbarians.IChiefBarbarianEntity;
import com.minecolonies.api.entity.mobs.barbarians.IMeleeBarbarianEntity;
import com.minecolonies.api.entity.mobs.egyptians.IPharaoEntity;
import com.minecolonies.api.entity.mobs.pirates.ICaptainPirateEntity;
import com.minecolonies.api.entity.mobs.pirates.IPirateEntity;
import com.minecolonies.api.entity.mobs.vikings.IMeleeNorsemenEntity;
import com.minecolonies.api.entity.mobs.vikings.INorsemenChiefEntity;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.api.util.CompatibilityUtils;
import com.minecolonies.api.util.constant.Constants;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.MathHelper;
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.World;
// [1.7.10] registries removed
// [1.7.10] registries removed
// [1.7.10] registries removed

import java.util.List;
import java.util.Random;

import static com.minecolonies.core.colony.events.raid.RaiderConstants.*;

/**
 * Util class for raider mobs/spawning
 */
public final class RaiderMobUtils
{
    // [1.7.10] DeferredRegister not available

    /**
     * EntityCreature attribute, used for custom attack damage
     */
    public final static Object MOB_ATTACK_DAMAGE = null; // [1.7.10] attribute registry stubbed

    /**
     * Damage increased by 1 for every 200 raid World difficulty
     */
    public static int DAMAGE_PER_X_RAID_LEVEL = 400;

    /**
     * Max damage from raidlevels
     */
    public static int MAX_RAID_LEVEL_DAMAGE = 3;

    private RaiderMobUtils()
    {
        throw new IllegalStateException("Tried to initialize: MobSpawnUtils but this is a Utility class.");
    }

    /**
     * Set EntityCreature attributes.
     *
     * @param EntityCreature    The EntityCreature to set the attributes on.
     * @param colony The colony that the EntityCreature is attacking.
     */
    public static void setMobAttributes(final AbstractEntityMinecoloniesRaider EntityCreature, final IColony colony)
    {
        final double difficultyModifier = colony.getRaiderManager().getRaidDifficultyModifier();
        EntityCreature.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(FOLLOW_RANGE * 2);
        EntityCreature.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(difficultyModifier < 2.4 ? MOVEMENT_SPEED : MOVEMENT_SPEED * 1.2);
        final int raidLevel = colony.getRaiderManager().getColonyRaidLevel();

        // Base damage
        final double attackDamage =
          ATTACK_DAMAGE +
            difficultyModifier *
              Math.min(raidLevel / DAMAGE_PER_X_RAID_LEVEL, MAX_RAID_LEVEL_DAMAGE);

        // Base health
        final double baseHealth = getHealthBasedOnRaidLevel(raidLevel) * difficultyModifier;

        EntityCreature.initStatsFor(baseHealth, difficultyModifier, attackDamage);
    }

    /**
     * Sets the entity's health based on the raidLevel
     *
     * @param raidLevel the raid World.
     * @return returns the health in the form of a double
     */
    public static double getHealthBasedOnRaidLevel(final int raidLevel)
    {
        return Math.max(BARBARIAN_BASE_HEALTH, (BARBARIAN_BASE_HEALTH + raidLevel * BARBARIAN_HEALTH_MULTIPLIER));
    }

    /**
     * Sets up and spawns the Barbarian entities of choice
     *
     * @param entityToSpawn  The entity which should be spawned
     * @param numberOfSpawns The number of times the entity should be spawned
     * @param spawnLocation  the location at which to spawn the entity
     * @param world          the world in which the colony and entity are
     * @param colony         the colony to spawn them close to.
     * @param eventID        the event id.
     */
    public static void spawn(
      final Class<?> entityToSpawn,
      final int numberOfSpawns,
      final int[] spawnLocation,
      final World world,
      final IColony colony,
      final int eventID)
    {
        if (spawnLocation != null && entityToSpawn != null && world != null && numberOfSpawns > 0)
        {
            int spawnDeviationX = 0;
            int spawnDeviationZ = 0;

            for (int i = 0; i < numberOfSpawns; i++)
            {
                final AbstractEntityMinecoloniesRaider entity = (AbstractEntityMinecoloniesRaider) entityToSpawn.create(world);

                if (entity != null)
                {
                    int[] spawnpos = BlockPosUtil.findAround(world, spawnLocation.offset(spawnDeviationX, 0, spawnDeviationZ), 5, 5, BlockPosUtil.SOLID_AIR_POS_SELECTOR);
                    if (spawnpos == null)
                    {
                        spawnpos = spawnLocation.above();
                    }

                    entity.absMoveTo(spawnpos.getX(), spawnpos.getY(), spawnpos.getZ(), (float) Mth.wrapDegrees(world.random.nextDouble() * WHOLE_CIRCLE), 0.0F);
                    CompatibilityUtils.addEntity(world, entity);
                    entity.setColony(colony);
                    entity.setEventID(eventID);
                    entity.registerWithColony();
                    spawnDeviationZ += 1;

                    if (spawnDeviationZ > 5)
                    {
                        spawnDeviationZ = 0;
                        spawnDeviationX += 1;
                    }
                }
            }
        }
    }

    /**
     * Set the equipment of a certain EntityCreature.
     *
     * @param EntityCreature the equipment to set up.
     */
    public static void setEquipment(final AbstractEntityMinecoloniesMonster EntityCreature)
    {
        if (EntityCreature instanceof IMeleeBarbarianEntity || EntityCreature instanceof IMeleeNorsemenEntity || EntityCreature instanceof INorsemenChiefEntity)
        {
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(Items.STONE_AXE));
        }
        else if (EntityCreature instanceof IPharaoEntity)
        {
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pharaoscepter));
        }
        else if (EntityCreature instanceof IArcherMobEntity)
        {
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(Items.BOW));
        }
        else if (EntityCreature instanceof ISpearmanMobEntity)
        {
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.spear));
        }
        else if (EntityCreature instanceof IChiefBarbarianEntity)
        {
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.chiefSword));
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(Items.CHAINMAIL_HELMET));
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(Items.CHAINMAIL_LEGGINGS));
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(Items.CHAINMAIL_BOOTS));
        }
        else if (EntityCreature instanceof IPirateEntity)
        {
            EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.scimitar));
            if (EntityCreature instanceof ICaptainPirateEntity)
            {
                if (new Random().nextBoolean())
                {
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateHelmet_1));
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateChest_1));
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateLegs_1));
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateBoots_1));
                }
                else
                {
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateHelmet_2));
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateChest_2));
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateLegs_2));
                    EntityCreature.setItemSlot(null /* EquipmentSlot. */, new ItemStack(ModItems.pirateBoots_2));
                }
            }
        }
    }

    /**
     * Returns the barbarians close to an entity.
     *
     * @param entity             The entity to test against
     * @param distanceFromEntity The distance to check for
     * @return the barbarians (if any) that is nearest
     */
    public static List<AbstractEntityMinecoloniesRaider> getBarbariansCloseToEntity(final Entity entity, final double distanceFromEntity)
    {
        return CompatibilityUtils.getWorldFromEntity(entity).getEntitiesOfClass(
          AbstractEntityMinecoloniesRaider.class,
          entity.getBoundingBox().expandTowards(
            distanceFromEntity,
            3.0D,
            distanceFromEntity),
          Entity::isAlive);
    }
}







