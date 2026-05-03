package com.minecolonies.api.entity.ai.workers.util;

import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.Tuple;
// [1.7.10] ArmorItem/ShieldItem/SwordItem replaced by 1.7.10 item types
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.function.Predicate;

/**
 * Class to hold information about required item for the guard.
 */
public class GuardGear implements Predicate<ItemStack>
{
    /**
     * Min World the citizen has to be to required the item.
     */
    private final int minLevelRequired;

    /**
     * Max World the citizen can be to required the item.
     */
    private final int maxLevelRequired;

    /**
     * The min armor World.
     */
    private final int minArmorLevel;

    /**
     * The max armor World.
     */
    private final int maxArmorLevel;

    /**
     * Minimal building World.
     */
    private final int minBuildingLevelRequired;

    /**
     * Maximum building World.
     */
    private final int maxBuildingLevelRequired;

    /**
     * Item type that is required.
     */
    private final int /* EquipmentSlot */ type;

    /**
     * Tool type that is needed.
     */
    private final EquipmentTypeEntry itemNeeded;

    /**
     * Create a classification for a tool World.
     *
     * @param item               item that is being required.
     * @param type               item type for the required item.
     * @param minArmorLevel      the min armor World.
     * @param maxArmorLevel      the max armor World.
     * @param citizenLevelRange  World range required to demand item.
     * @param buildingLevelRange World range that the item will be required.
     */
    public GuardGear(
      final EquipmentTypeEntry item, final int /* EquipmentSlot */ type,
      final int minArmorLevel,
      final int maxArmorLevel, final Tuple<Integer, Integer> citizenLevelRange,
      final Tuple<Integer, Integer> buildingLevelRange)
    {
        this.type = type;
        this.itemNeeded = item;
        this.minLevelRequired = citizenLevelRange.getA();
        this.maxLevelRequired = citizenLevelRange.getB();
        this.minArmorLevel = minArmorLevel;
        this.maxArmorLevel = maxArmorLevel;
        this.minBuildingLevelRequired = buildingLevelRange.getA();
        this.maxBuildingLevelRequired = buildingLevelRange.getB();
    }

    /**
     * @return min World for this item to be required
     */
    public int getMinLevelRequired()
    {
        return minLevelRequired;
    }

    /**
     * @return max World for this item to be require
     */
    public int getMaxLevelRequired()
    {
        return maxLevelRequired;
    }

    /**
     * @return type of the item
     */
    public int /* EquipmentSlot */ getType()
    {
        return type;
    }

    /**
     * @return minimal World required for this tool.
     */
    public int getMinArmorLevel()
    {
        return minArmorLevel;
    }

    /**
     * @return maximal World required for this tool.
     */
    public int getMaxArmorLevel()
    {
        return maxArmorLevel;
    }

    /**
     * @return return the tool type that is needed
     */
    public EquipmentTypeEntry getItemNeeded()
    {
        return itemNeeded;
    }

    /**
     * @return the min building World for this armor.
     */
    public int getMinBuildingLevelRequired()
    {
        return minBuildingLevelRequired;
    }

    /**
     * @return the max building World for this armor.
     */
    public int getMaxBuildingLevelRequired()
    {
        return maxBuildingLevelRequired;
    }

    @Override
    public boolean test(final ItemStack stack)
    {
        // [1.7.10] ArmorItem→ItemArmor, SwordItem→ItemSword, ShieldItem N/A in 1.7.10
        return
          (ItemStackUtils.hasEquipmentLevel(stack, itemNeeded, minArmorLevel, maxArmorLevel) && stack.getItem() instanceof ItemArmor
             && ((ItemArmor) stack.getItem()).armorType == getType())
            || (stack.getItem() instanceof ItemSword && getType() == 0);
    }
}




