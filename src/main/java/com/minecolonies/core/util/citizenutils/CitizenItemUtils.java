package com.minecolonies.core.util.citizenutils;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.*;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.client.BlockParticleEffectMessage;
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Direction -> net.minecraft.util.EnumFacing
// [1.7.10] sounds removed
// [1.7.10] sounds removed
// [1.7.10] int /* InteractionHand */ removed
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.block.Block;
// [1.7.10] BlockState -> int metadata
// [1.7.10] world.phys removed
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minecolonies.api.research.util.ResearchConstants.ARMOR_DURABILITY;
import static com.minecolonies.api.research.util.ResearchConstants.TOOL_DURABILITY;
import static com.minecolonies.api.util.constant.CitizenConstants.*;
import static com.minecolonies.api.util.constant.Constants.DEFAULT_PITCH_MULTIPLIER;
import static com.minecolonies.api.util.constant.Constants.DEFAULT_VOLUME;

/**
 * Handles the citizens interaction with an item with the world.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class CitizenItemUtils
{
    /**
     * Citizen will try to pick up a certain item.
     *
     * @param itemEntity the item he wants to pickup.
     */
    public static void tryPickupItemEntity(@NotNull final AbstractEntityCitizen citizen, @NotNull final ItemEntity itemEntity)
    {
        if (!CompatibilityUtils.getWorldFromCitizen(citizen).isClientSide)
        {
            if (itemEntity.hasPickUpDelay())
            {
                return;
            }

            final ItemStack itemStack = itemEntity.getItem();
            final ItemStack compareStack = itemStack.copy();

            if (citizen.getCitizenJobHandler().getColonyJob() == null || citizen.getCitizenJobHandler().getColonyJob().pickupSuccess(compareStack))
            {
                final ItemStack resultStack = InventoryUtils.addItemStackToItemHandlerWithResult(citizen.getInventoryCitizen(), itemStack);
                final int resultingStackSize = ItemStackUtils.isEmpty(resultStack) ? 0 : ItemStackUtils.getSize(resultStack);

                if (ItemStackUtils.isEmpty(resultStack) || ItemStackUtils.getSize(resultStack) != ItemStackUtils.getSize(compareStack))
                {
                    CompatibilityUtils.getWorldFromCitizen(citizen).playSound(null,
                      citizen.blockPosition(),
                      SoundEvents.ITEM_PICKUP,
                      SoundSource.AMBIENT,
                      (float) DEFAULT_VOLUME,
                      (float) ((citizen.getRandom().nextGaussian() * DEFAULT_PITCH_MULTIPLIER + 1.0D) * 2.0D));
                    citizen.take(itemEntity, ItemStackUtils.getSize(itemStack) - resultingStackSize);

                    final ItemStack overrulingStack = itemStack.copy();
                    overrulingStack.setCount(ItemStackUtils.getSize(itemStack) - resultingStackSize);

                    if (citizen.getCitizenJobHandler().getColonyJob() != null)
                    {
                        citizen.getCitizenJobHandler().getColonyJob().onStackPickUp(overrulingStack);
                    }

                    if (ItemStackUtils.isEmpty(resultStack))
                    {
                        itemEntity.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
            }
            else
            {
                itemEntity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    /**
     * Removes the currently held item.
     */
    public static void removeHeldItem(AbstractEntityCitizen citizen)
    {
        citizen.setItemSlot(null /* EquipmentSlot. */, ItemStackUtils.EMPTY);
    }

    /**
     * Sets the currently held item.
     *
     * @param hand what hand we're setting
     * @param slot from the inventory slot.
     */
    public static void setHeldItem(@NotNull final AbstractEntityCitizen citizen, final int /* InteractionHand */ hand, final int slot)
    {
        citizen.getCitizenData().getInventory().setHeldItem(hand, slot);
        if (hand.equals(0 /* InteractionHand.MAIN_HAND */))
        {
            citizen.setItemSlot(null /* EquipmentSlot. */, citizen.getCitizenData().getInventory().getStackInSlot(slot));
        }
        else if (hand.equals(1 /* InteractionHand.OFF_HAND */))
        {
            citizen.setItemSlot(null /* EquipmentSlot. */, citizen.getCitizenData().getInventory().getStackInSlot(slot));
        }
    }

    /**
     * Sets the currently held for mainHand item.
     *
     * @param slot from the inventory slot.
     */
    public static void setMainHeldItem(@NotNull final AbstractEntityCitizen citizen, final int slot)
    {
        citizen.getCitizenData().getInventory().setHeldItem(0 /* InteractionHand.MAIN_HAND */, slot);
        citizen.setItemSlot(null /* EquipmentSlot. */, citizen.getCitizenData().getInventory().getStackInSlot(slot));
    }

    /**
     * Swing entity arm, create sound and particle effects.
     * <p>
     * Will not break the block.
     *
     * @param int[] Block position.
     */
    public static void hitBlockWithToolInHand(@NotNull final AbstractEntityCitizen citizen, @Nullable final int[] blockPos)
    {
        if (blockPos == null)
        {
            return;
        }
        hitBlockWithToolInHand(citizen, blockPos, false);
    }

    /**
     * Swing entity arm, create sound and particle effects.
     * <p>
     * If breakBlock is true then it will break the block (different sound and particles), and damage the tool in the citizens hand.
     *
     * @param int[]   Block position.
     * @param breakBlock if we want to break this block.
     */

    public static void hitBlockWithToolInHand(@NotNull final AbstractEntityCitizen citizen, @Nullable final int[] blockPos, final boolean breakBlock)
    {
        if (blockPos == null)
        {
            return;
        }

        citizen.getLookControl().setLookAt(blockPos[0], blockPos[1], blockPos[2], FACING_DELTA_YAW, citizen.getMaxHeadXRot());

        citizen.swing(citizen.getUsedItemHand());

        final BlockState blockState = CompatibilityUtils.getWorldFromCitizen(citizen).getBlockState(blockPos);
        final Block block = blockState.getBlock();
        if (breakBlock)
        {
            if (!CompatibilityUtils.getWorldFromCitizen(citizen).isClientSide)
            {
                Network.getNetwork().sendToPosition(
                  new BlockParticleEffectMessage(blockPos, CompatibilityUtils.getWorldFromCitizen(citizen).getBlockState(blockPos), BlockParticleEffectMessage.BREAK_BLOCK),
                  new PacketDistributor.TargetPoint(
                    blockPos[0], blockPos[1], blockPos[2], BLOCK_BREAK_SOUND_RANGE, citizen.World.dimension()));
            }
            CompatibilityUtils.getWorldFromCitizen(citizen).playSound(null,
              blockPos,
              block.getSoundType(blockState, CompatibilityUtils.getWorldFromCitizen(citizen), blockPos, citizen).getBreakSound(),
              SoundSource.BLOCKS,
              (block.getSoundType(blockState, CompatibilityUtils.getWorldFromCitizen(citizen), blockPos, citizen).getVolume() + 1.0F) * 0.5F,
              block.getSoundType(blockState, CompatibilityUtils.getWorldFromCitizen(citizen), blockPos, citizen).getPitch() * 0.8F);
            WorldUtil.removeBlock(CompatibilityUtils.getWorldFromCitizen(citizen), blockPos, false);

            damageItemInHand(citizen, citizen.getUsedItemHand(), 1);
        }
        else
        {
            if (!CompatibilityUtils.getWorldFromCitizen(citizen).isClientSide)
            {
                final int[] vector = new int[]{blockPos[0] - (int)citizen.posX, blockPos[1] - (int)citizen.posY, blockPos[2] - (int)citizen.posZ};
                final Direction facing = BlockPosUtil.directionFromDelta(vector[0], vector[1], vector[2]).getOpposite();

                Network.getNetwork().sendToPosition(
                  new BlockParticleEffectMessage(blockPos, CompatibilityUtils.getWorldFromCitizen(citizen).getBlockState(blockPos), facing.ordinal()),
                  new PacketDistributor.TargetPoint(blockPos[0],
                    blockPos[1], blockPos[2], BLOCK_BREAK_PARTICLE_RANGE, citizen.World.dimension()));
            }
            CompatibilityUtils.getWorldFromCitizen(citizen).playSound(null,
              blockPos,
              block.getSoundType(blockState, CompatibilityUtils.getWorldFromCitizen(citizen), blockPos, citizen).getHitSound(),
              SoundSource.BLOCKS,
              (block.getSoundType(blockState, CompatibilityUtils.getWorldFromCitizen(citizen), blockPos, citizen).getVolume() + 1.0F) * 0.125F,
              block.getSoundType(blockState, CompatibilityUtils.getWorldFromCitizen(citizen), blockPos, citizen).getPitch() * 0.5F);
        }
    }

    /**
     * Damage the current held item.
     *
     * @param damage amount of damage.
     */
    public static void damageItemInHand(@NotNull final AbstractEntityCitizen citizen, final int /* InteractionHand */ hand, final int damage)
    {
        final ItemStack heldItem = citizen.getCitizenData().getInventory().getHeldItem(hand);
        //If we hit with bare hands, ignore
        if (heldItem == null || heldItem.isEmpty())
        {
            return;
        }

        //Check if the effect exists first, to avoid unnecessary calls to random number generator.
        if (citizen.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(TOOL_DURABILITY) > 0)
        {
            if (citizen.getRandom().nextDouble() > (1 / (1 + citizen.getCitizenColonyHandler()
                                                               .getColonyOrRegister()
                                                               .getResearchManager()
                                                               .getResearchEffects()
                                                               .getEffectStrength(TOOL_DURABILITY))))
            {
                return;
            }
        }

        //check if tool breaks
        if (citizen.getCitizenData()
              .getInventory()
              .damageInventoryItem(citizen.getCitizenData().getInventory().getHeldItemSlot(hand), damage, citizen, item -> item.broadcastBreakEvent(hand)))
        {
            if (hand == 0 /* InteractionHand.MAIN_HAND */)
            {
                citizen.setItemSlot(null /* EquipmentSlot. */, ItemStackUtils.EMPTY);
            }
            else
            {
                citizen.setItemSlot(null /* EquipmentSlot. */, ItemStackUtils.EMPTY);
            }
        }
    }

    /**
     * Pick up all items in a range around the citizen.
     */
    public static void pickupItems(AbstractEntityCitizen citizen)
    {
        for (final ItemEntity item : CompatibilityUtils.getWorldFromCitizen(citizen).getEntitiesOfClass(ItemEntity.class,
          new AABB(citizen.blockPosition())
            .expandTowards(2.0F, 1.0F, 2.0F)
            .expandTowards(-2.0F, -1.0F, -2.0F)))
        {
            if (item != null && item.isAlive())
            {
                tryPickupItemEntity(citizen, item);
            }
        }
    }

    /**
     * Swing entity arm, create sound and particle effects.
     * <p>
     * This will break the block (different sound and particles), and damage the tool in the citizens hand.
     *
     * @param int[] Block position.
     */
    public static void breakBlockWithToolInHand(@NotNull final AbstractEntityCitizen citizen, @Nullable final int[] blockPos)
    {
        if (blockPos == null)
        {
            return;
        }
        hitBlockWithToolInHand(citizen, blockPos, true);
    }

    /**
     * Handles the dropping of items from the entity.
     *
     * @param itemstack to drop.
     * @return the dropped item.
     */
    public static ItemEntity entityDropItem(@NotNull final AbstractEntityCitizen citizen, @NotNull final ItemStack itemstack)
    {
        return citizen.spawnAtLocation(itemstack, 0.0F);
    }

    /**
     * Updates the armour damage after being hit.
     *
     * @param damage damage dealt.
     */
    public static void updateArmorDamage(@NotNull final AbstractEntityCitizen citizen, final double damage)
    {
        if (citizen.getCitizenColonyHandler().getColonyOrRegister().getResearchManager().getResearchEffects().getEffectStrength(ARMOR_DURABILITY) > 0)
        {
            if (citizen.getRandom().nextDouble() > (1 / (1 + citizen.getCitizenColonyHandler()
                                                               .getColonyOrRegister()
                                                               .getResearchManager()
                                                               .getResearchEffects()
                                                               .getEffectStrength(ARMOR_DURABILITY))))
            {
                return;
            }
        }

        final int armorDmg = Math.max(1, (int) (damage / 4));
        for (int i = 0; i < 4; i++)
        {
            final int slot = 0 /* EquipmentSlot - TODO port */;
            final ItemStack equipment = citizen.getInventoryCitizen().getArmorInSlot(i);
            equipment.hurtAndBreak(armorDmg, citizen, (s) -> {
                s.broadcastBreakEvent(i);
                citizen.onArmorRemove(equipment, i);
                citizen.getInventoryCitizen().markDirty();
            });
        }
    }

    public static double applyMending(@NotNull final AbstractEntityCitizen citizen, final double xp)
    {
        double localXp = xp;

        for (int slot = 0; slot < 6; slot++) // [1.7.10] 0-3 armor, 4 mainhand, 5 offhand
        {
            if (localXp <= 0)
            {
                break;
            }

            final ItemStack tool;
            if (slot < 4) // [1.7.10] slots 0-3 are armor slots
            {
                tool = citizen.getInventoryCitizen().getArmorInSlot(slot);
            }
            else
            {
                tool = citizen.getInventoryCitizen().getHeldItem(slot == 4 ? 0 : 1); // [1.7.10] main/off hand
            }

            if (!ItemStackUtils.isEmpty(tool) && tool.isDamaged() && tool.isEnchanted() && EnchantmentHelper.getEnchantments(tool).containsKey(Enchantments.MENDING))
            {
                //2 xp to heal 1 dmg
                final double dmgHealed = Math.min(localXp / 2, tool.getDamageValue());
                localXp -= dmgHealed * 2;
                tool.setDamageValue(tool.getDamageValue() - (int) Math.ceil(dmgHealed));
            }
        }

        return localXp;
    }
}







