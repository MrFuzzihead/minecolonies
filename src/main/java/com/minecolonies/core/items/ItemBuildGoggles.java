package com.minecolonies.core.items;
import net.minecraft.item.ItemStack;

import com.minecolonies.core.client.render.worldevent.ColonyBlueprintRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.Util;
import net.minecraft.util.IChatComponent;
// [1.7.10] sounds removed
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

public class ItemBuildGoggles extends ArmorItem
{
    public static final ArmorMaterial GOGGLES = new MineColoniesArmorMaterial("minecolonies:build_goggles", 2, Util.make(new EnumMap<>(Type.class), map -> {
        map.put(Type.BOOTS, 0);
        map.put(Type.LEGGINGS, 0);
        map.put(Type.CHESTPLATE, 0);
        map.put(Type.HELMET, 0);
    }), 20, SoundEvents.ARMOR_EQUIP_LEATHER, 0F, 0.0F, () -> Ingredient.EMPTY);

    /**
     * Constructor
     *
     * @param name            the name.
     * @param properties      the item properties.
     */
    public ItemBuildGoggles(
            @NotNull final String name,
            final Item.Properties properties)
    {
        super(GOGGLES, Type.HELMET, properties.setNoRepair().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(@NotNull final ItemStack stack,
                                @Nullable final World world,
                                @NotNull final List<String> components,
                                @NotNull final TooltipFlag flags)
    {
        super.appendHoverText(stack, world, components, flags);

        // [1.7.10] Component.translatable replaced with plain string
        components.add("\"" + "item.minecolonies.build_goggles.lore" + "\"");
        components.add(ColonyBlueprintRenderer.willRenderBlueprints()
                ? "item.minecolonies.build_goggles.enabled" : "item.minecolonies.build_goggles.disabled");
    }
}



