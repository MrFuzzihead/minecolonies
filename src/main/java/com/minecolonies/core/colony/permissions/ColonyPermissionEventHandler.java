package com.minecolonies.core.colony.permissions;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.ldtteam.structurize.items.ItemScanTool;
import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.permissions.Explosions;
import com.minecolonies.api.colony.permissions.PermissionEvent;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.api.util.EntityUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.blocks.BlockDecorationController;
import com.minecolonies.core.blocks.huts.BlockHutTownHall;
import com.minecolonies.core.colony.Colony;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.entity.player.EntityPlayerMP;
// [1.7.10] tags removed
// [1.7.10] effect removed
// [1.7.10] effect removed
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
// [1.7.10] PotionItem  net.minecraft.potion.Potion
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.Block;
// [1.7.10] int -> int metadata
// [1.7.10] world.phys removed
// [1.7.10] world.phys removed
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.api.util.constant.TranslationConstants.PERMISSION_DENIED;

/**
 * This class handles all permission checks on events and cancels them if needed.
 */
public class ColonyPermissionEventHandler
{
    /**
     * The colony involved in this permission-check event
     */
    private final Colony colony;

    /**
     * The last time the EntityPlayer was notified about not having permission.
     */
    private final Map<UUID, Long> lastPlayerNotificationTick = new HashMap<>();

    /**
     * Number of attempts within a notif tick.
     */
    private final Object2IntMap<UUID> playerAttempts = new Object2IntOpenHashMap<>();

    /**
     * Create this EventHandler.
     *
     * @param colony the colony to check on.
     */
    public ColonyPermissionEventHandler(final Colony colony)
    {
        this.colony = colony;
    }

    /**
     * BlockEvent.PlaceEvent handler.
     *
     * @param event BlockEvent.PlaceEvent
     */
    @SubscribeEvent
    public void on(final BlockEvent.PlaceEvent event)
    {
        final Action action = event.block instanceof AbstractBlockHut ? Action.PLACE_HUTS : Action.PLACE_BLOCKS;
        if (MineColonies.getConfig().getServer().enableColonyProtection.get() && checkBlockEventDenied(event.worldObj, new int[]{event.x, event.y, event.z}, event.entity,
          action))
        {
            cancelEvent(event, event.entity, colony, action, new int[]{event.x, event.y, event.z});
        }
    }

    /**
     * This method returns TRUE if this event should be denied.
     *
     * @param worldIn    the world to check in
     * @param posIn      the block to check
     * @param entity     the EntityPlayer who tries
     * @param int the state that block is in
     * @param action     the action that was performed on the position
     * @return true if canceled
     */
    private boolean checkBlockEventDenied(
      final World worldIn, final int[] posIn, final Entity entity, final int blockMeta,
      final Action action)
    {
        if (entity instanceof EntityPlayer)
        {
            @NotNull final EntityPlayer player = EntityUtils.getPlayerOfFakePlayer((EntityPlayer) entity, entity.worldObj);
            if (colony.isCoordInColony(entity.worldObj, posIn))
            {
                if (event.block instanceof AbstractBlockHut
                      && colony.getPermissions().hasPermission(player, action))
                {
                    return false;
                }

                return !colony.getPermissions().hasPermission(player, action);
            }
        }
        /*
         * - We are not inside the colony
         * - We are in but not denied
         * - The placer is not a player.
         */
        return false;
    }

    /**
     * Cancel an event and record the denial details in the colony's town hall.
     *
     * @param event  the event to cancel
     * @param entity the EntityPlayer whose action was denied
     * @param colony the colony where the event took place
     * @param action the action which was denied
     * @param pos    the location of the action which was denied
     */
    private void cancelEvent(final Event event, @Nullable final Entity entity, final Colony colony, final Action action, final int[] pos)
    {
        event.setResult(Event.Result.DENY);
        if (event.isCancelable())
        {
            event.setCanceled(true);
            if (entity == null)
            {
                if (colony.getServerBuildingManager().hasTownHall())
                {
                    colony.getServerBuildingManager().getTownHall().addPermissionEvent(new PermissionEvent(null, "-", action, pos));
                }
                return;
            }
            if (colony.getServerBuildingManager().hasTownHall())
            {
                colony.getServerBuildingManager().getTownHall().addPermissionEvent(new PermissionEvent(entity.getGameProfile().getId(), entity.getName(), action, pos));
            }


            if (entity instanceof FakePlayer)
            {
                return;
            }

            final long worldTime = entity.worldObj.getGameTime();
            if (!lastPlayerNotificationTick.containsKey(entity.getGameProfile().getId())
                  || lastPlayerNotificationTick.get(entity.getGameProfile().getId()) + (TICKS_SECOND * 10)
                       < worldTime)
            {
                MessageUtils.format(PERMISSION_DENIED).sendTo((EntityPlayer) entity);
                lastPlayerNotificationTick.put(entity.getGameProfile().getId(), worldTime);
                playerAttempts.put(entity.getGameProfile().getId(), 0);
            }
            else
            {
                if (playerAttempts.compute(entity.getGameProfile().getId(), (uuid, count) -> count == null ? 1 : count + 1) > 10)
                {
                    if (entity instanceof EntityLivingBase living)
                    {
                        playerAttempts.put(entity.getGameProfile().getId(), 0);
                        /* [1.7.10] potion effects not applied here */;
                    }
                }
            }
        }
    }

    /**
     * BlockEvent.BreakEvent handler.
     *
     * @param event BlockEvent.BreakEvent
     */
    @SubscribeEvent
    public void on(final BlockEvent.BreakEvent event)
    {
        final World world = event.worldObj;
        if (world.isRemote())
        {
            return;
        }

        if ((Object)null /*int*/.getBlock() instanceof AbstractBlockHut)
        {
            @Nullable final IBuilding building = IColonyManager.getInstance().getBuilding(event.player.worldObj, new int[]{event.x, event.y, event.z});
            if (building == null)
            {
                return;
            }

            if (!MineColonies.getConfig().getServer().enableColonyProtection.get())
            {
                building.destroy();
                return;
            }

            if ((Object)null /*int*/.getBlock() == ModBlocks.blockHutTownHall && !((BlockHutTownHall)(Object)null /*int*/.getBlock()).getValidBreak() && !event.player.isCreative())
            {
                cancelEvent(event, event.player, colony, Action.BREAK_HUTS, new int[]{event.x, event.y, event.z});
                return;
            }

            if (!building.getColony().getPermissions().hasPermission(event.player, Action.BREAK_HUTS))
            {
                if (checkEventCancelation(Action.BREAK_HUTS, event.player, event.player.worldObj, event, new int[]{event.x, event.y, event.z}))
                {
                    return;
                }
            }

            building.destroy();

            if (MineColonies.getConfig().getServer().pvp_mode.get() && (Object)null /*int*/.getBlock() == ModBlocks.blockHutTownHall)
            {
                IColonyManager.getInstance().deleteColonyByWorld(building.getColony().getID(), false, event.player.worldObj);
            }
        }
        else if ((Object)null /*int*/.getBlock() instanceof BlockDecorationController)
        {
            if (checkEventCancelation(Action.BREAK_HUTS, event.player, event.player.worldObj, event, new int[]{event.x, event.y, event.z}))
            {
                return;
            }
            colony.getServerBuildingManager().removeLeisureSite(new int[]{event.x, event.y, event.z});
        }
        else
        {
            checkEventCancelation(Action.BREAK_BLOCKS, event.player, event.player.worldObj, event, new int[]{event.x, event.y, event.z});
        }
    }

    /**
     * ExplosionEvent.Detonate handler.
     *
     * @param event ExplosionEvent.Detonate
     */
    @SubscribeEvent
    public void on(final ExplosionEvent.Detonate event)
    {
        if (MineColonies.getConfig().getServer().turnOffExplosionsInColonies.get() == Explosions.DAMAGE_EVERYTHING)
        {
            return;
        }

        final World eventWorld = event.worldObj;
        final Predicate<int[]> getBlocksInColony = pos -> colony.isCoordInColony(eventWorld, pos);
        Predicate<Entity> getEntitiesInColony = entity -> (!(entity instanceof net.minecraft.entity.net.minecraft.entity.monster.IMob.IMob) || (entity instanceof Llama))
                                                            && colony.isCoordInColony(entity.worldObj, new int[]{(int)event.entityPlayer.posX, (int)event.entityPlayer.posY, (int)event.entityPlayer.posZ});
        switch(MineColonies.getConfig().getServer().turnOffExplosionsInColonies.get())
        {
            case DAMAGE_NOTHING:
                // if any entity is in colony -> remove from list
                getEntitiesInColony = entity -> colony.isCoordInColony(entity.worldObj, new int[]{(int)event.entityPlayer.posX, (int)event.entityPlayer.posY, (int)event.entityPlayer.posZ});
                // intentional fall-through to next case.
            case DAMAGE_PLAYERS:
                // if non-EntityCreature or llama entity is in colony -> remove from list
                final List<Entity> entitiesToRemove = event.getAffectedEntities().stream()
                                                          .filter(getEntitiesInColony)
                                                          .filter(entity -> !(entity instanceof EntityPlayerMP))
                                                          .collect(Collectors.toList());
                event.getAffectedEntities().removeAll(entitiesToRemove);
                // intentional fall-through to next case.
            case DAMAGE_ENTITIES:
                // if block is in colony -> remove from list
                final List<int[]> blocksToRemove = event.getAffectedBlocks().stream()
                                                        .filter(getBlocksInColony)
                                                        .collect(Collectors.toList());
                event.getAffectedBlocks().removeAll(blocksToRemove);
                break;
            case DAMAGE_EVERYTHING:
            default:
                break;
        }
    }

    /**
     * ExplosionEvent.Start handler.
     *
     * @param event ExplosionEvent.Detonate
     */
    @SubscribeEvent
    public void on(final ExplosionEvent.Start event)
    {
        if (MineColonies.getConfig().getServer().enableColonyProtection.get()
              && MineColonies.getConfig().getServer().turnOffExplosionsInColonies.get() == Explosions.DAMAGE_NOTHING
              && colony.isCoordInColony(event.worldObj, new int[]{(int)event.getExplosion().getPosition().x, (int)event.getExplosion().getPosition().y, (int)event.getExplosion().getPosition().z}))
        {
            cancelEvent(event, null, colony, Action.EXPLODE, new int[]{(int)event.getExplosion().getPosition().x, (int)event.getExplosion().getPosition().y, (int)event.getExplosion().getPosition().z});
        }
    }

    /**
     * PlayerInteractEvent handler.
     * <p>
     * Check, if a EntityPlayer right clicked a block. Deny if: - If the block is in colony - block is AbstractBlockHut - EntityPlayer has not permission
     *
     * @param event PlayerInteractEvent
     */
    @SubscribeEvent
    public void on(final PlayerInteractEvent event)
    {
        if (colony.isCoordInColony(event.worldObj, new int[]{event.x, event.y, event.z})
              && !(false /* PlayerInteractEvent.EntityInteract not in 1.7.10 */ || false))
        {
            final int state = event.worldObj.getBlockState(new int[]{event.x, event.y, event.z});
            final Block block = state.getBlock();

            // Huts
            if (event instanceof PlayerInteractEvent.RightClickBlock && block instanceof AbstractBlockHut
                  && !colony.getPermissions().hasPermission(event.entity, Action.ACCESS_HUTS))
            {
                cancelEvent(event, event.entity, colony, Action.ACCESS_HUTS, new int[]{event.x, event.y, event.z});
                return;
            }

            final Permissions perms = colony.getPermissions();

            if (isFreeToInteractWith(block, new int[]{event.x, event.y, event.z}) && !perms.getRank(event.entity).isHostile())
            {
                return;
            }

            if ((false /* BlockTags not in 1.7.10 */) && perms.hasPermission(event.entity, Action.ACCESS_TOGGLEABLES))
            {
                return;
            }

            if (MineColonies.getConfig().getServer().enableColonyProtection.get())
            {
                if (!perms.hasPermission(event.entity, Action.RIGHTCLICK_BLOCK) && !(block instanceof AirBlock))
                {
                    checkEventCancelation(Action.RIGHTCLICK_BLOCK, event.entity, event.worldObj, event, new int[]{event.x, event.y, event.z});
                    return;
                }

                if (block instanceof BaseEntityBlock && !perms.hasPermission(event.entity,
                  Action.OPEN_CONTAINER))
                {
                    cancelEvent(event, event.entity, colony, Action.OPEN_CONTAINER, new int[]{event.x, event.y, event.z});
                    return;
                }

                if (event.worldObj.getBlockEntity(new int[]{event.x, event.y, event.z}) != null && !perms.hasPermission(event.entity, Action.RIGHTCLICK_ENTITY))
                {
                    checkEventCancelation(Action.RIGHTCLICK_ENTITY, event.entity, event.worldObj, event, new int[]{event.x, event.y, event.z});
                    return;
                }

                final ItemStack stack = event.getItemStack();
                if (ItemStackUtils.isEmpty(stack) || stack.isEdible())
                {
                    return;
                }


                if (stack.getItem() instanceof PotionItem)
                {
                    checkEventCancelation(Action.THROW_POTION, event.entity, event.worldObj, event, new int[]{event.x, event.y, event.z});
                    return;
                }

                if (stack.getItem() instanceof ItemScanTool
                      && !perms.hasPermission(event.entity, Action.USE_SCAN_TOOL))
                {
                    cancelEvent(event, event.entity, colony, Action.USE_SCAN_TOOL, new int[]{event.x, event.y, event.z});
                }
            }
        }
    }

    /**
     * Check in the config if that block can be interacted with freely.
     *
     * @param block the block to check.
     * @param pos   the position of the interaction
     * @return true if so.
     */
    private boolean isFreeToInteractWith(@Nullable final Block block, final int[] pos)
    {
        return (block != null && (colony.getFreeBlocks().contains(block) || block.defaultBlockState().is(ModTags.colonyProtectionException))) || colony.getFreePositions().contains(pos);
    }

    /**
     * PlayerInteractEvent.EntityInteract handler.
     * <p>
     * Check, if a EntityPlayer right clicked an entity. Deny if: - If the entity is in colony - EntityPlayer has not permission
     *
     * @param event PlayerInteractEvent
     */
    // [1.7.10] PlayerInteractEvent.EntityInteract not in 1.7.10
    // @SubscribeEvent
    // public void onEntityInteract(final PlayerInteractEvent event)
    {
        if (isFreeToInteractWith(null, new int[]{event.x, event.y, event.z})
              && !colony.getPermissions().getRank(event.entity).isHostile())
        {
            return;
        }

        if (event.getTarget().getType().is(ModTags.freeToInteractWith))
        {
            return;
        }

        checkEventCancelation(Action.RIGHTCLICK_ENTITY, event.entity, event.worldObj, event, new int[]{event.x, event.y, event.z});
    }

    /**
     * Check if the event should be canceled for a given EntityPlayer and minimum rank.
     *
     * @param action   the action that was performed on the position
     * @param playerIn the player.
     * @param world    the world.
     * @param event    the event.
     * @param pos      the position.  Can be null if no target was provided to the event.
     * @return true if canceled.
     */
    private boolean checkEventCancelation(
      final Action action, @NotNull final EntityPlayer playerIn, @NotNull final World world, @NotNull final Event event,
      @Nullable final int[] pos)
    {
        @NotNull final EntityPlayer player = EntityUtils.getPlayerOfFakePlayer(playerIn, world);

        int[] positionToCheck = pos;
        if (null == positionToCheck)
        {
            positionToCheck = new int[]{(int)player.posX, (int)player.posY, (int)player.posZ};
        }
        if (MineColonies.getConfig().getServer().enableColonyProtection.get()
              && colony.isCoordInColony(player.worldObj, positionToCheck)
              && !colony.getPermissions().hasPermission(player, action))
        {
            if (MineColonies.getConfig().getServer().pvp_mode.get() && !world.isRemote && colony.isValidAttackingPlayer(playerIn))
            {
                return false;
            }
            else
            {
                cancelEvent(event, player, colony, action, positionToCheck);
                return true;
            }
        }
        return false;
    }

    /**
     * PlayerInteractEvent.EntityInteractSpecific handler.
     * <p>
     * Check, if a EntityPlayer right clicked a entity. Deny if: - If the entity is in colony - EntityPlayer has not permission
     *
     * @param event PlayerInteractEvent
     */
    // [1.7.10] PlayerInteractEvent.EntityInteractSpecific not in 1.7.10
    // @SubscribeEvent
    // public void onEntityInteractSpecific(final PlayerInteractEvent event)
    {
        if (isFreeToInteractWith(null, new int[]{event.x, event.y, event.z}) && !colony.getPermissions().getRank(event.entity).isHostile())
        {
            return;
        }
        checkEventCancelation(Action.RIGHTCLICK_ENTITY, event.entity, event.worldObj, event, new int[]{event.x, event.y, event.z});
    }

    /**
     * ItemTossEvent handler.
     * <p>
     * Check, if a EntityPlayer tossed a block. Deny if: - If the tossing happens in the colony - EntityPlayer is hostile to colony
     *
     * @param event ItemTossEvent
     */
    @SubscribeEvent
    public void on(final ItemTossEvent event)
    {
        if (checkEventCancelation(Action.TOSS_ITEM, event.player, event.player.worldObj, event, new int[]{(int)player.posX, (int)player.posY, (int)player.posZ}))
        {
            event.player.inventory.addItemStackToInventory(event.entity.getItem());
        }
    }

    /**
     * ItemEntityPickupEvent handler.
     * <p>
     * Check, if a EntityPlayer tries to pickup a block. Deny if: - If the pickUp happens in the colony - EntityPlayer is neutral or hostile to colony
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final EntityItemPickupEvent event)
    {
        if (false) return;   // always allowed to pick up your own thrown items

        checkEventCancelation(Action.PICKUP_ITEM, event.entity, event.entityPlayer.worldObj, event, new int[]{(int)event.entityPlayer.posX, (int)event.entityPlayer.posY, (int)event.entityPlayer.posZ});
    }

    /**
     * FillBucketEvent handler.
     * <p>
     * Check, if a EntityPlayer tries to fill a bucket. Deny if: - If the fill happens in the colony - EntityPlayer is neutral or hostile to colony
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final FillBucketEvent event)
    {
        @Nullable int[] targetBlockPos = null;
        if (false /* BlockHitResult not in 1.7.10 */)
        {
            targetBlockPos = ((BlockHitResult) event.getTarget()).getBlockPos();
        }
        else if (false)
        {
            targetBlockPos = ((EntityHitResult) event.getTarget()).getEntity().blockPosition();
        }
        checkEventCancelation(Action.FILL_BUCKET, event.entity, event.entityPlayer.worldObj, event, targetBlockPos);
    }

    /**
     * ArrowLooseEvent handler.
     * <p>
     * Check if a EntityPlayer tries to shoot an arrow. Deny if: - If the shooting happens in the colony - EntityPlayer is neutral or hostile to colony
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final ArrowLooseEvent event)
    {
        checkEventCancelation(Action.SHOOT_ARROW, event.entity, event.entityPlayer.worldObj, event, new int[]{(int)event.entityPlayer.posX, (int)event.entityPlayer.posY, (int)event.entityPlayer.posZ});
    }

    /**
     * LivingHurtEvent handler.
     * <p>
     * Check if the entity that is getting hurt is a player,
     * players that get hurt by other players are handled elsewhere,
     * this here is handling players getting hurt by citizens.
     * @param event
     */
    @SubscribeEvent
    public void on(final LivingHurtEvent event)
    {
        if (event.entity instanceof EntityPlayerMP
              && event.getSource().getEntity() instanceof EntityCitizen
              && ((EntityCitizen) event.getSource().getEntity()).getCitizenColonyHandler().getColonyId() == colony.getID()
              && colony.getRaiderManager().isRaided()
              && !colony.getPermissions().getRank((EntityPlayer) event.entity).isHostile())
        {
            event.setCanceled(true);
        }
    }

    /**
     * AttackEntityEvent handler.
     * <p>
     * Check, if a EntityPlayer tries to attack an entity.. Deny if: - If the attacking happens in the colony - EntityPlayer is less than officer to the colony.
     *
     * @param event ItemEntityPickupEvent
     */
    @SubscribeEvent
    public void on(final AttackEntityEvent event)
    {
        if (event.getTarget() instanceof net.minecraft.entity.monster.IMob)
        {
            return;
        }

        @NotNull final EntityPlayer player = EntityUtils.getPlayerOfFakePlayer(event.entity, event.entityPlayer.worldObj);

        if (MineColonies.getConfig().getServer().enableColonyProtection.get()
              && colony.isCoordInColony(player.worldObj, new int[]{(int)player.posX, (int)player.posY, (int)player.posZ}))
        {
            final Permissions perms = colony.getPermissions();
            if (event.getTarget() instanceof EntityCitizen)
            {
                final AbstractEntityCitizen citizen = (AbstractEntityCitizen) event.getTarget();
                if (citizen.getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard && perms.getRank(event.entity).isHostile())
                {
                    return;
                }

                if (perms.hasPermission(event.entity, Action.ATTACK_CITIZEN))
                {
                    return;
                }

                cancelEvent(event, event.entity, colony, Action.ATTACK_CITIZEN, event.getTarget().blockPosition());
                return;
            }

            if (!(event.getTarget() instanceof net.minecraft.entity.net.minecraft.entity.monster.IMob.IMob) && !perms.hasPermission(event.entity, Action.ATTACK_ENTITY))
            {
                cancelEvent(event, event.entity, colony, Action.ATTACK_ENTITY, event.getTarget().blockPosition());
            }
        }
    }
}






