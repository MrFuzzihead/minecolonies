package com.minecolonies.api.equipment;

// [1.7.10 BACKPORT] DeferredRegister/ToolActions/RegistryObject replaced with 1.7.10 equivalents.
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.compatibility.Compatibility;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import com.minecolonies.api.registry.RegistryObject;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.translation.ToolTranslationConstants;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

/**
 * Class used for storing and registering any EquipmentTypes.
 */
public class ModEquipmentTypes
{
    public static final RegistryObject<EquipmentTypeEntry> none;
    public static final RegistryObject<EquipmentTypeEntry> pickaxe;
    public static final RegistryObject<EquipmentTypeEntry> shovel;
    public static final RegistryObject<EquipmentTypeEntry> axe;
    public static final RegistryObject<EquipmentTypeEntry> hoe;
    public static final RegistryObject<EquipmentTypeEntry> sword;
    public static final RegistryObject<EquipmentTypeEntry> bow;
    public static final RegistryObject<EquipmentTypeEntry> fishing_rod;
    public static final RegistryObject<EquipmentTypeEntry> shears;
    public static final RegistryObject<EquipmentTypeEntry> shield;
    public static final RegistryObject<EquipmentTypeEntry> helmet;
    public static final RegistryObject<EquipmentTypeEntry> leggings;
    public static final RegistryObject<EquipmentTypeEntry> chestplate;
    public static final RegistryObject<EquipmentTypeEntry> boots;
    public static final RegistryObject<EquipmentTypeEntry> flint_and_steel;
    public static final RegistryObject<EquipmentTypeEntry> lead;

    static
    {
        none = build("none", ToolTranslationConstants.TOOL_TYPE_NONE,
          (itemStack, equipmentType) -> true,
          (itemStack, equipmentType) -> -1);

        pickaxe = build("pickaxe", ToolTranslationConstants.TOOL_TYPE_PICKAXE,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemPickaxe || Compatibility.isTinkersTool(itemStack, equipmentType),
          ModEquipmentTypes::vanillaToolLevel);

        shovel = build("shovel", ToolTranslationConstants.TOOL_TYPE_SHOVEL,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemSpade || Compatibility.isTinkersTool(itemStack, equipmentType),
          ModEquipmentTypes::vanillaToolLevel);

        axe = build("axe", ToolTranslationConstants.TOOL_TYPE_AXE,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemAxe || Compatibility.isTinkersTool(itemStack, equipmentType),
          ModEquipmentTypes::vanillaToolLevel);

        hoe = build("hoe", ToolTranslationConstants.TOOL_TYPE_HOE,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemHoe || Compatibility.isTinkersTool(itemStack, equipmentType),
          ModEquipmentTypes::vanillaToolLevel);

        sword = build("sword", ToolTranslationConstants.TOOL_TYPE_SWORD,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemSword || Compatibility.isTinkersWeapon(itemStack),
          (itemStack, equipmentType) -> {
              if (Compatibility.isTinkersWeapon(itemStack)) return Compatibility.getToolLevel(itemStack);
              else if (itemStack.getItem() instanceof ItemSword) return vanillaToolLevel(itemStack, equipmentType);
              return -1;
          });

        bow = build("bow", ToolTranslationConstants.TOOL_TYPE_BOW,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemBow,
          (itemStack, equipmentType) -> durabilityBasedLevel(itemStack, Items.bow.getMaxDamage()));

        fishing_rod = build("rod", ToolTranslationConstants.TOOL_TYPE_FISHING_ROD,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemFishingRod,
          (itemStack, equipmentType) -> durabilityBasedLevel(itemStack, Items.fishing_rod.getMaxDamage()));

        shears = build("shears", ToolTranslationConstants.TOOL_TYPE_SHEARS,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemShears,
          (itemStack, equipmentType) -> durabilityBasedLevel(itemStack, Items.shears.getMaxDamage()));

        // [1.7.10] Shield does not exist in 1.7.10
        shield = build("shield", ToolTranslationConstants.TOOL_TYPE_SHIELD,
          (itemStack, equipmentType) -> false,
          (itemStack, equipmentType) -> -1);

        helmet = build("helmet", ToolTranslationConstants.TOOL_TYPE_HELMET,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemArmor && ((ItemArmor) itemStack.getItem()).armorType == 0,
          (itemStack, equipmentType) -> ItemStackUtils.getArmorLevel(itemStack));

        chestplate = build("chestplate", ToolTranslationConstants.TOOL_TYPE_CHEST_PLATE,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemArmor && ((ItemArmor) itemStack.getItem()).armorType == 1,
          (itemStack, equipmentType) -> ItemStackUtils.getArmorLevel(itemStack));

        leggings = build("leggings", ToolTranslationConstants.TOOL_TYPE_LEGGINGS,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemArmor && ((ItemArmor) itemStack.getItem()).armorType == 2,
          (itemStack, equipmentType) -> ItemStackUtils.getArmorLevel(itemStack));

        boots = build("boots", ToolTranslationConstants.TOOL_TYPE_BOOTS,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemArmor && ((ItemArmor) itemStack.getItem()).armorType == 3,
          (itemStack, equipmentType) -> ItemStackUtils.getArmorLevel(itemStack));

        flint_and_steel = build("flintandsteel", ToolTranslationConstants.TOOL_TYPE_LIGHTER,
          (itemStack, equipmentType) -> itemStack.getItem() instanceof ItemFlintAndSteel,
          (itemStack, equipmentType) -> durabilityBasedLevel(itemStack, Items.flint_and_steel.getMaxDamage()));

        // [1.7.10] Lead/LeadItem does not exist as an item class; check by item reference
        lead = build("lead", ToolTranslationConstants.TOOL_TYPE_LEAD,
          (itemStack, equipmentType) -> itemStack.getItem() == Items.lead,
          (itemStack, equipmentType) -> -1);

        // populate lookup map
        for (final RegistryObject<EquipmentTypeEntry> ro : new RegistryObject[] {none, pickaxe, shovel, axe, hoe, sword, bow, fishing_rod, shears, shield, helmet, leggings, chestplate, boots, flint_and_steel, lead})
        {
            REGISTRY_MAP.put(ro.get().getRegistryName(), ro.get());
        }
    }

    private static final java.util.Map<ResourceLocation, EquipmentTypeEntry> REGISTRY_MAP = new java.util.HashMap<>();

    /**
     * Look up an EquipmentTypeEntry by its ResourceLocation.
     *
     * @param location the registry location.
     * @return the entry, or null if not found.
     */
    public static EquipmentTypeEntry lookup(final ResourceLocation location)
    {
        return REGISTRY_MAP.get(location);
    }

    private static RegistryObject<EquipmentTypeEntry> build(
      final String id,
      final String displayName,
      final java.util.function.BiPredicate<ItemStack, EquipmentTypeEntry> isEquipment,
      final java.util.function.BiFunction<ItemStack, EquipmentTypeEntry, Integer> equipmentLevel)
    {
        final EquipmentTypeEntry entry = new EquipmentTypeEntry.Builder()
          .setRegistryName(new ResourceLocation(Constants.MOD_ID, id))
          .setDisplayName(displayName)
          .setIsEquipment(isEquipment)
          .setEquipmentLevel(equipmentLevel)
          .build();
        return RegistryObject.of(entry);
    }

    /**
     * Get all registered equipment type entries.
     *
     * @return all entries.
     */
    public static java.util.Collection<EquipmentTypeEntry> getAllEntries()
    {
        return REGISTRY_MAP.values();
    }
    public static int vanillaToolLevel(final ItemStack itemStack, final EquipmentTypeEntry equipmentType)
    {
        if (Compatibility.isTinkersTool(itemStack, equipmentType))
        {
            return Compatibility.getToolLevel(itemStack);
        }
        else if (itemStack.getItem() instanceof ItemTool)
        {
            // In 1.7.10 tool materials have levels 0-4 matching Wood=0,Stone=1,Iron=2,Diamond=3,Gold=0
            final ItemTool tool = (ItemTool) itemStack.getItem();
            return tool.getToolMaterial().getHarvestLevel();
        }
        return -1;
    }

    /**
     * Get the durability based item World.
     */
    public static int durabilityBasedLevel(final ItemStack itemStack, final int vanillaItemDurability)
    {
        if (itemStack.getItem().isDamageable())
        {
            return 5; // not damageable
        }
        return Math.min(itemStack.getItem().getMaxDamage() / Math.max(vanillaItemDurability, 1), 5);
    }
}
