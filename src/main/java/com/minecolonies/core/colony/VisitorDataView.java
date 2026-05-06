package com.minecolonies.core.colony;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.IVisitorViewData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.Util;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * View data for visitors
 */
public class VisitorDataView extends CitizenDataView implements IVisitorViewData
{
    /**
     * The recruitment costs
     */
    private ItemStack recruitmentCosts;

    /**
     * Cached player info for custom texture.
     */
    private volatile ResourceLocation cachedTexture;

    /**
     * Create a CitizenData given an ID. Used as a super-constructor or during loading.
     *
     * @param id     ID of the Citizen.
     * @param colony Colony the Citizen belongs to.
     */
    public VisitorDataView(final int id, final IColonyView colony)
    {
        super(id, colony);
    }

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        super.deserialize(buf);
        recruitmentCosts = buf.readItem();
        recruitmentCosts.setCount(buf.readInt());
    }

    @Override
    public ItemStack getRecruitCost()
    {
        return recruitmentCosts;
    }

    @Override
    public ResourceLocation getCustomTexture()
    {
        if (textureUUID == null)
        {
            return null;
        }
        if (cachedTexture == null)
        {
            cachedTexture = DefaultPlayerSkin.getDefaultSkin(textureUUID);
            Util.backgroundExecutor().execute(() ->
            {
                Minecraft minecraft = Minecraft.getInstance();
                final GameProfile profile = new GameProfile(textureUUID, "mcoltexturequery");
                minecraft.getMinecraftSessionService().fillProfileProperties(profile, true);
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(profile);
                if (!map.isEmpty())
                {
                    cachedTexture = minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                }
            });
        }
        return cachedTexture;
    }
}



