package com.minecolonies.api.entity.ai.workers.util;

import com.minecolonies.api.equipment.ModEquipmentTypes;
import com.minecolonies.api.util.Tuple;
// [1.7.10] world.entity removed

import java.util.ArrayList;
import java.util.List;

public final class GuardGearBuilder
{
    /**
     * Private constructor to hide implicit one.
     */
    private GuardGearBuilder()
    {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * Build the gear for a certain armor World and World range.
     *
     * @param minArmorLevel      the min armor World.
     * @param maxArmorLevel      the max armor World.
     * @param levelRange         the World range of the guard.
     * @param buildingLevelRange the building World range.
     * @return the list of items.
     */
    public static List<GuardGear> buildGearForLevel(
      final int minArmorLevel,
      final int maxArmorLevel,
      final Tuple<Integer, Integer> levelRange,
      final Tuple<Integer, Integer> buildingLevelRange)
    {
        final List<GuardGear> armorList = new ArrayList<>();
        armorList.add(new GuardGear(ModEquipmentTypes.boots.get(), 0 /* EquipmentSlot.FEET */, minArmorLevel, maxArmorLevel, levelRange, buildingLevelRange));
        armorList.add(new GuardGear(ModEquipmentTypes.chestplate.get(), 1 /* EquipmentSlot.CHEST */, minArmorLevel, maxArmorLevel, levelRange, buildingLevelRange));
        armorList.add(new GuardGear(ModEquipmentTypes.helmet.get(), 2 /* EquipmentSlot.HEAD */, minArmorLevel, maxArmorLevel, levelRange, buildingLevelRange));
        armorList.add(new GuardGear(ModEquipmentTypes.leggings.get(), 3 /* EquipmentSlot.LEGS */, minArmorLevel, maxArmorLevel, levelRange, buildingLevelRange));
        return armorList;
    }
}



