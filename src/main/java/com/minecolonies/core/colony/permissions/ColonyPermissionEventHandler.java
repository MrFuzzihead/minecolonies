package com.minecolonies.core.colony.permissions;

// [1.7.10] removed bad 1.21 imports
// import net.minecraft.util.Direction; (not needed)
// import net.minecraft.world.level.block.state.BlockState; (1.21 only)
// import net.minecraft.world.entity.player.Player; (use EntityPlayer)
// import net.minecraft.world.phys.AABB; (not needed)
// import net.minecraft.util.RandomSource; (not needed)
// import net.minecraft.world.item.BoneMealItem; (not needed)

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
        // [1.7.10] PlaceEvent: player field (not entity); placedBlock field; world (not worldObj)
        final Action action = event.placedBlock instanceof AbstractBlockHut ? Action.PLACE_HUTS : Action.PLACE_BLOCKS;
        if (MineColonies.getConfig().getServer().enableColonyProtection.get() && checkBlockEventDenied(event.world, new int[]{event.x, event.y, event.z}, event.player, event.blockMetadata, action))
        {
            cancelEvent(event, event.player, colony, action, new int[]{event.x, event.y, event.z});
        }
    }

    /**
     * This method returns TRUE if this event should be denied.
     *
     * @param worldIn    the world to check in
     * @param posIn      the block to check
     * @param entity     the EntityPlayer who tries
     * @param blockMeta the metadata of the block being interacted with
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
                if (action == Action.PLACE_HUTS // [1.7.10] was event.block instanceof AbstractBlockHut - use action type
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
     * Record a permission denial (without cancelling the event - callers must cancel inline).
     * Also sends permission-denied notification to the player.
     *
     * @param entity the Entity whose action was denied
     * @param colony the colony where the event took place
     * @param action the action which was denied
     * @param pos    the location of the action which was denied
     */
    private void recordDenial(@Nullable final Entity entity, final Colony colony, final Action action, final int[] pos)
    {
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
            // [1.7.10] getGameProfile() is on EntityPlayer, not Entity — entity here is always a player
            final EntityPlayer ep = (EntityPlayer) entity;
            colony.getServerBuildingManager().getTownHall().addPermissionEvent(new PermissionEvent(ep.getGameProfile().getId(), ep.getCommandSenderName(), action, pos));
        }

        if (entity instanceof FakePlayer)
        {
            return;
        }

        // [1.7.10] getGameProfile() is on EntityPlayer
        final EntityPlayer notifyPlayer = (EntityPlayer) entity;
        final long worldTime = entity.worldObj.getTotalWorldTime();
        if (!lastPlayerNotificationTick.containsKey(notifyPlayer.getGameProfile().getId())
              || lastPlayerNotificationTick.get(notifyPlayer.getGameProfile().getId()) + (TICKS_SECOND * 10)
                   < worldTime)
        {
            MessageUtils.format(PERMISSION_DENIED).sendTo(notifyPlayer);
            lastPlayerNotificationTick.put(notifyPlayer.getGameProfile().getId(), worldTime);
            playerAttempts.put(notifyPlayer.getGameProfile().getId(), 0);
        }
        else
        {
            if (playerAttempts.compute(notifyPlayer.getGameProfile().getId(), (uuid, count) -> count == null ? 1 : count + 1) > 10)
            {
                // [1.7.10] potion effects for persistent offenders stubbed
                playerAttempts.put(notifyPlayer.getGameProfile().getId(), 0);
            }
        }
    }

    /**
     * Cancel an event and record the denial. Helper that handles the cpw/forge Event type difference.
     *
     * @param event  the event to cancel (as Object to bypass type system)
     * @param entity the Entity whose action was denied
     * @param colony the colony where the event took place
     * @param action the action which was denied
     * @param pos    the location of the action which was denied
     */
    private void cancelEvent(final cpw.mods.fml.common.eventhandler.Event event, @Nullable final Entity entity, final Colony colony, final Action action, final int[] pos)
    {
        event.setResult(cpw.mods.fml.common.eventhandler.Event.Result.DENY);
        if (event.isCancelable())
        {
            event.setCanceled(true);
        }
        recordDenial(entity, colony, action, pos);
    }

    /**
     * BlockEvent.BreakEvent handler.
     *
     * @param event BlockEvent.BreakEvent
     */
    @SubscribeEvent
    public void on(final BlockEvent.BreakEvent event)
    {
        // [1.7.10] BreakEvent: getPlayer() method; world field (not worldObj); isRemote field (not method)
        final World world = event.world;
        if (world.isRemote)
        {
            return;
        }

        final EntityPlayer player = event.getPlayer();

        if (event.block instanceof AbstractBlockHut)
        {
            @Nullable final IBuilding building = IColonyManager.getInstance().getBuilding(player.worldObj, new int[]{event.x, event.y, event.z});
            if (building == null)
            {
                return;
            }

            if (!MineColonies.getConfig().getServer().enableColonyProtection.get())
            {
                building.destroy();
                return;
            }

            if (event.block == ModBlocks.blockHutTownHall && !((BlockHutTownHall) event.block).getValidBreak() && !player.capabilities.isCreativeMode)
            {
                cancelEvent(event, player, colony, Action.BREAK_HUTS, new int[]{event.x, event.y, event.z});
                return;
            }

            if (!building.getColony().getPermissions().hasPermission(player, Action.BREAK_HUTS))
            {
                if (checkEventCancelation(Action.BREAK_HUTS, player, player.worldObj, event, new int[]{event.x, event.y, event.z}))
                {
                    return;
                }
            }

            building.destroy();

            if (MineColonies.getConfig().getServer().pvp_mode.get() && event.block == ModBlocks.blockHutTownHall)
            {
                IColonyManager.getInstance().deleteColonyByWorld(building.getColony().getID(), false, player.worldObj);
            }
        }
        else if (event.block instanceof BlockDecorationController)
        {
            if (checkEventCancelation(Action.BREAK_HUTS, player, player.worldObj, event, new int[]{event.x, event.y, event.z}))
            {
                return;
            }
            colony.getServerBuildingManager().removeLeisureSite(new int[]{event.x, event.y, event.z});
        }
        else
        {
            checkEventCancelation(Action.BREAK_BLOCKS, player, player.worldObj, event, new int[]{event.x, event.y, event.z});
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

        final World eventWorld = event.world;
        final Predicate<net.minecraft.world.ChunkPosition> getBlocksInColony = pos -> colony.isCoordInColony(eventWorld, new int[]{pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ}); // [1.7.10] ChunkPosition
        Predicate<Entity> getEntitiesInColony = entity -> (!(entity instanceof net.minecraft.entity.monster.IMob))
                                                            && colony.isCoordInColony(entity.worldObj, new int[]{(int)entity.posX, (int)entity.posY, (int)entity.posZ});
        switch(MineColonies.getConfig().getServer().turnOffExplosionsInColonies.get())
        {
            case DAMAGE_NOTHING:
                // if any entity is in colony -> remove from list
                getEntitiesInColony = entity -> colony.isCoordInColony(entity.worldObj, new int[]{(int)entity.posX, (int)entity.posY, (int)entity.posZ});
                // intentional fall-through to next case.
            case DAMAGE_PLAYERS:
                // if non-EntityCreature entity is in colony -> remove from list
                final List<Entity> entitiesToRemove = event.getAffectedEntities().stream()
                                                          .filter(getEntitiesInColony)
                                                          .filter(entity -> !(entity instanceof EntityPlayerMP))
                                                          .collect(Collectors.toList());
                event.getAffectedEntities().removeAll(entitiesToRemove);
                // intentional fall-through to next case.
            case DAMAGE_ENTITIES:
                // [1.7.10] getAffectedBlocks returns List<ChunkPosition>
                final List<net.minecraft.world.ChunkPosition> blocksToRemove = event.getAffectedBlocks().stream()
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
              && colony.isCoordInColony(event.world, new int[]{(int)event.explosion.explosionX, (int)event.explosion.explosionY, (int)event.explosion.explosionZ})) // [1.7.10] event.world, explosionX/Y/Z
        {
            cancelEvent(event, null, colony, Action.EXPLODE, new int[]{(int)event.explosion.explosionX, (int)event.explosion.explosionY, (int)event.explosion.explosionZ});
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
        // [1.7.10] PlayerInteractEvent: world field (not worldObj), entityPlayer field
        if (colony.isCoordInColony(event.world, new int[]{event.x, event.y, event.z}))
        {
            final Block block = event.world.getBlock(event.x, event.y, event.z);

            // Huts
            if (block instanceof AbstractBlockHut
                  && !colony.getPermissions().hasPermission(event.entityPlayer, Action.ACCESS_HUTS))
            {
                cancelEvent(event, event.entityPlayer, colony, Action.ACCESS_HUTS, new int[]{event.x, event.y, event.z});
                return;
            }

            final Permissions perms = colony.getPermissions();

            if (isFreeToInteractWith(block, new int[]{event.x, event.y, event.z}) && !perms.getRank(event.entityPlayer).isHostile())
            {
                return;
            }

            if (MineColonies.getConfig().getServer().enableColonyProtection.get())
            {
                if (!perms.hasPermission(event.entityPlayer, Action.RIGHTCLICK_BLOCK) && !(block instanceof net.minecraft.block.BlockAir))
                {
                    checkEventCancelation(Action.RIGHTCLICK_BLOCK, event.entityPlayer, event.world, event, new int[]{event.x, event.y, event.z});
                    return;
                }

                if (event.world.getTileEntity(event.x, event.y, event.z) != null && !perms.hasPermission(event.entityPlayer, Action.OPEN_CONTAINER))
                {
                    cancelEvent(event, event.entityPlayer, colony, Action.OPEN_CONTAINER, new int[]{event.x, event.y, event.z});
                    return;
                }

                if (event.world.getTileEntity(event.x, event.y, event.z) != null && !perms.hasPermission(event.entityPlayer, Action.RIGHTCLICK_ENTITY))
                {
                    checkEventCancelation(Action.RIGHTCLICK_ENTITY, event.entityPlayer, event.world, event, new int[]{event.x, event.y, event.z});
                    return;
                }

                // [1.7.10] getItemStack() not available; use getHeldItem()
                final ItemStack stack = event.entityPlayer.getHeldItem();
                if (ItemStackUtils.isEmpty(stack))
                {
                    return;
                }

                if (stack.getItem() instanceof net.minecraft.item.ItemPotion)
                {
                    checkEventCancelation(Action.THROW_POTION, event.entityPlayer, event.world, event, new int[]{event.x, event.y, event.z});
                    return;
                }

                if (stack.getItem() instanceof ItemScanTool
                      && !perms.hasPermission(event.entityPlayer, Action.USE_SCAN_TOOL))
                {
                    cancelEvent(event, event.entityPlayer, colony, Action.USE_SCAN_TOOL, new int[]{event.x, event.y, event.z});
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
        // [1.7.10] block.defaultBlockState().is(ModTags.colonyProtectionException) not in 1.7.10 — ModTags check omitted; use free-blocks list only
        return (block != null && colony.getFreeBlocks().contains(block)) || colony.getFreePositions().contains(pos);
    }

    /**
     * PlayerInteractEvent.EntityInteract handler.
     * <p>
     * Check, if a EntityPlayer right clicked an entity. Deny if: - If the entity is in colony - EntityPlayer has not permission
     *
     * @param event PlayerInteractEvent
     */
    // [1.7.10] PlayerInteractEvent.EntityInteract not in 1.7.10 — orphaned block removed

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
      final Action action, @NotNull final EntityPlayer playerIn, @NotNull final World world, @NotNull final cpw.mods.fml.common.eventhandler.Event event, // [1.7.10] use fqn Event
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
    // [1.7.10] PlayerInteractEvent.EntityInteractSpecific not in 1.7.10 — orphaned block removed

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
        if (checkEventCancelation(Action.TOSS_ITEM, event.player, event.player.worldObj, event, new int[]{(int)event.player.posX, (int)event.player.posY, (int)event.player.posZ}))
        {
            event.player.inventory.addItemStackToInventory(((net.minecraft.entity.item.EntityItem) event.entity).getEntityItem());
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

        // [1.7.10] EntityItemPickupEvent: player is entityPlayer (inherited from PlayerEvent)
        checkEventCancelation(Action.PICKUP_ITEM, event.entityPlayer, event.entityPlayer.worldObj, event, new int[]{(int)event.entityPlayer.posX, (int)event.entityPlayer.posY, (int)event.entityPlayer.posZ});
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
        // [1.7.10] FillBucketEvent target is MovingObjectPosition; player is entityPlayer
        @Nullable int[] targetBlockPos = null;
        if (event.target != null && event.target.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK)
        {
            targetBlockPos = new int[]{event.target.blockX, event.target.blockY, event.target.blockZ};
        }
        checkEventCancelation(Action.FILL_BUCKET, event.entityPlayer, event.entityPlayer.worldObj, event, targetBlockPos);
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
        // [1.7.10] ArrowLooseEvent: player is entityPlayer
        checkEventCancelation(Action.SHOOT_ARROW, event.entityPlayer, event.entityPlayer.worldObj, event, new int[]{(int)event.entityPlayer.posX, (int)event.entityPlayer.posY, (int)event.entityPlayer.posZ});
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
        // [1.7.10] LivingHurtEvent: entityLiving (not entity), source field (not getSource()), ammount field
        if (event.entityLiving instanceof EntityPlayerMP
              && event.source.getEntity() instanceof EntityCitizen
              && ((EntityCitizen) event.source.getEntity()).getCitizenColonyHandler().getColonyId() == colony.getID()
              && colony.getRaiderManager().isRaided()
              && !colony.getPermissions().getRank((EntityPlayer) event.entityLiving).isHostile())
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
        // [1.7.10] AttackEntityEvent: target field (not getTarget()), player is entityPlayer
        if (event.target instanceof net.minecraft.entity.monster.IMob)
        {
            return;
        }

        @NotNull final EntityPlayer player = EntityUtils.getPlayerOfFakePlayer(event.entityPlayer, event.entityPlayer.worldObj);

        if (MineColonies.getConfig().getServer().enableColonyProtection.get()
              && colony.isCoordInColony(player.worldObj, new int[]{(int)player.posX, (int)player.posY, (int)player.posZ}))
        {
            final Permissions perms = colony.getPermissions();
            if (event.target instanceof EntityCitizen)
            {
                final AbstractEntityCitizen citizen = (AbstractEntityCitizen) event.target;
                if (citizen.getCitizenJobHandler().getColonyJob() instanceof AbstractJobGuard && perms.getRank(event.entityPlayer).isHostile())
                {
                    return;
                }

                if (perms.hasPermission(event.entityPlayer, Action.ATTACK_CITIZEN))
                {
                    return;
                }

                cancelEvent(event, event.entityPlayer, colony, Action.ATTACK_CITIZEN,
                  new int[]{(int)event.target.posX, (int)event.target.posY, (int)event.target.posZ});
                return;
            }

            if (!(event.target instanceof net.minecraft.entity.monster.IMob) && !perms.hasPermission(event.entityPlayer, Action.ATTACK_ENTITY))
            {
                cancelEvent(event, event.entityPlayer, colony, Action.ATTACK_ENTITY,
                  new int[]{(int)event.target.posX, (int)event.target.posY, (int)event.target.posZ});
            }
        }
    }
}






