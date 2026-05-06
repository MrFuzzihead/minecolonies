package com.minecolonies.core.placementhandlers.main;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.entity.player.Player;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.StructurePlacementUtils;
import com.ldtteam.structurize.storage.ISurvivalBlueprintHandler;
import com.ldtteam.structurize.storage.StructurePacks;
import com.ldtteam.structurize.util.PlacementSettings;
import com.ldtteam.structurize.util.RotationMirror;
import com.minecolonies.api.advancements.AdvancementTriggers;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.SoundUtils;
import com.minecolonies.core.MineColonies;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Stats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.Mirror;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
// [1.7.10] items shim in com.minecolonies.api.shim

import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.Constants.*;
import static com.minecolonies.api.util.constant.TranslationConstants.*;
import static com.minecolonies.api.util.constant.translation.ProgressTranslationConstants.PROGRESS_SUPPLY_CHEST_PLACED;

public class SuppliesHandler implements ISurvivalBlueprintHandler
{
    public static final String ID = MOD_ID + ":supplies";

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public String getDisplayName()
    {
        // this should never actually be visible
        return String.translatable("com.minecolonies.coremod.supplies.placement");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canHandle(final Blueprint blueprint, final ClientLevel clientLevel, final Player player, final int[] blockPos, final PlacementSettings placementSettings)
    {
        return false;
    }

    @Override
    public void handle(
            final Blueprint blueprint,
            final String packName,
            final String blueprintPath,
            final boolean clientPack,
            final World world,
            final Player playerArg,
            final int[] blockPos,
            final PlacementSettings placementSettings)
    {
        if (clientPack || !StructurePacks.hasPack(packName))
        {
            MessageUtils.format(NO_CUSTOM_CAMPS).sendTo(playerArg);
            SoundUtils.playErrorSound(playerArg, playerArg.blockPosition());
            return;
        }

        final EntityPlayerMP player = (EntityPlayerMP) playerArg;

        blueprint.setRotationMirror(RotationMirror.of(placementSettings.rotation, placementSettings.mirror == Mirror.NONE ? Mirror.NONE : Mirror.FRONT_BACK), world);

        if (player.getStats().getValue(Stats.ITEM_USED.get(ModItems.supplyChest)) > 0 && !MineColonies.getConfig().getServer().allowInfiniteSupplyChests.get()
                && !isFreeInstantPlacementMH(player) && !player.isCreative())
        {
            MessageUtils.format(WARNING_SUPPLY_CHEST_ALREADY_PLACED).sendTo(player);
            SoundUtils.playErrorSound(player, player.blockPosition());
            return;
        }

        Predicate<ItemStack> searchPredicate = stack -> !stack.isEmpty();
        if (blueprintPath.contains("supplyship"))
        {
            searchPredicate = searchPredicate.and(stack -> ItemStackUtils.compareItemStacksIgnoreStackSize(stack, new ItemStack(ModItems.supplyChest), true, false));
        }
        if (blueprintPath.contains("supplycamp"))
        {
            searchPredicate = searchPredicate.and(stack -> ItemStackUtils.compareItemStacksIgnoreStackSize(stack, new ItemStack(ModItems.supplyCamp), true, false));
        }

        if (isFreeInstantPlacementMH(player))
        {
            searchPredicate =
                    searchPredicate.and(
                            stack -> stack.hasTag() && stack.getTag().get(PLACEMENT_NBT) != null && stack.getTag().getString(PLACEMENT_NBT).equals(INSTANT_PLACEMENT));
        }

        final int slot = InventoryUtils.findFirstSlotInItemHandlerNotEmptyWith(new InvWrapper(player.getInventory()), searchPredicate);

        if (slot != -1 && !ItemStackUtils.isEmpty(player.getInventory().removeItemNoUpdate(slot)))
        {
            if (player.getStats().getValue(Stats.ITEM_USED.get(ModItems.supplyChest)) < 1)
            {
                MessageUtils.format(PROGRESS_SUPPLY_CHEST_PLACED).sendTo(player);
                player.awardStat(Stats.ITEM_USED.get(ModItems.supplyChest), 1);
                AdvancementTriggers.PLACE_SUPPLY.trigger(player);
            }

            SoundUtils.playSuccessSound(player, player.blockPosition());

            StructurePlacementUtils.loadAndPlaceStructureWithRotation(player.World, blueprint,
                    blockPos, placementSettings.getRotation(), placementSettings.getMirror() != Mirror.NONE ? Mirror.FRONT_BACK : Mirror.NONE, true, player);
        }
        else
        {
            MessageUtils.format(WARNING_REMOVING_SUPPLY_CHEST).sendTo(player);
        }
    }

    /**
     * Whether the itemstack used allows a free placement.
     *
     * @param playerEntity the player to check
     * @return whether the itemstack used allows a free placement.
     */
    private boolean isFreeInstantPlacementMH(EntityPlayerMP playerEntity)
    {
        final ItemStack mhItem = playerEntity.getMainHandItem();
        return !ItemStackUtils.isEmpty(mhItem) && mhItem.getTag() != null && mhItem.getTag().getString(PLACEMENT_NBT).equals(INSTANT_PLACEMENT);
    }
}






