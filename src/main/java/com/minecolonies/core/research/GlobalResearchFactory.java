package com.minecolonies.core.research;
import net.minecraft.network.chat.contents.TranslatableContents;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.requestsystem.factory.FactoryVoidInput;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.research.*;
import com.minecolonies.api.research.IResearchEffect;
import com.minecolonies.api.research.factories.IGlobalResearchFactory;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.api.util.constant.SerializationIdentifierConstants;
import com.minecolonies.api.util.constant.TypeConstants;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.minecolonies.api.research.util.ResearchConstants.*;

/**
 * Factory implementation taking care of creating new instances, serializing and deserializing GlobalResearch.
 */
public class GlobalResearchFactory implements IGlobalResearchFactory
{
    @NotNull
    @Override
    public TypeToken<GlobalResearch> getFactoryOutputType()
    {
        return TypeToken.of(GlobalResearch.class);
    }

    @NotNull
    @Override
    public TypeToken<FactoryVoidInput> getFactoryInputType()
    {
        return TypeConstants.FACTORYVOIDINPUT;
    }

    @NotNull
    @Override
    public IGlobalResearch getNewInstance(
        final ResourceLocation id,
        final ResourceLocation parent,
        final ResourceLocation branch,
        final TranslatableContents name,
        final TranslatableContents subtitle,
        final int depth,
        final int sortOrder,
        final boolean onlyChild,
        final boolean hidden,
        final boolean autostart,
        final boolean instant,
        final boolean immutable)
    {
        return new GlobalResearch(id, parent, branch, name, subtitle, depth, sortOrder, onlyChild, hidden, autostart, instant, immutable);
    }

    @NotNull
    @Override
    public NBTTagCompound serialize(@NotNull final IFactoryController controller, @NotNull final IGlobalResearch research)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        if (research.getParent() != null)
        {
            compound.setString(TAG_PARENT, research.getParent().toString());
        }
        compound.setString(TAG_ID, research.getId().toString());
        compound.setString(TAG_BRANCH, research.getBranch().toString());
        compound.setString(TAG_NAME, research.getName().getKey());
        compound.setInteger(TAG_RESEARCH_LVL, research.getDepth());
        compound.setInteger(TAG_RESEARCH_SORT, research.getSortOrder());
        compound.setBoolean(TAG_ONLY_CHILD, research.hasOnlyChild());
        compound.setString(TAG_SUBTITLE_NAME, research.getSubtitle().getKey());
        compound.setBoolean(TAG_INSTANT, research.isInstant());
        compound.setBoolean(TAG_AUTOSTART, research.isAutostart());
        compound.setBoolean(TAG_IMMUTABLE, research.isImmutable());
        compound.setBoolean(TAG_HIDDEN, research.isHidden());
        @NotNull final NBTTagList costTagList = research.getCostList().stream().map(cost ->
        {
            final NBTTagCompound costCompound = new NBTTagCompound();
            costCompound.putString(TAG_COST_TYPE, cost.getType().getRegistryName().toString());
            costcompound.setTag(TAG_COST_NBT, cost.writeToNBT());
            return compound;
        }).collect(NBTUtils.toListNBT());
        compound.setTag(TAG_COSTS, costTagList);

        @NotNull final NBTTagList reqTagList = research.getResearchRequirements().stream().map(req ->
        {
            final NBTTagCompound reqCompound = new NBTTagCompound();
            reqCompound.putString(TAG_REQ_TYPE, req.getRegistryEntry().getRegistryName().toString());
            reqcompound.setTag(TAG_REQ_ITEM, req.writeToNBT());
            return reqCompound;
        }).collect(NBTUtils.toListNBT());
        compound.setTag(TAG_REQS, reqTagList);

        @NotNull final NBTTagList effectTagList = research.getEffects().stream().map(eff ->
        {
            final NBTTagCompound effectCompound = new NBTTagCompound();
            effectCompound.putString(TAG_EFFECT_TYPE, eff.getRegistryEntry().getRegistryName().toString());
            effectcompound.setTag(TAG_EFFECT_ITEM, eff.writeToNBT());
            return effectCompound;
        }).collect(NBTUtils.toListNBT());
        compound.setTag(TAG_EFFECTS, effectTagList);

        @NotNull final NBTTagList childTagList = research.getChildren().stream().map(child ->
        {
            final NBTTagCompound childCompound = new NBTTagCompound();
            childCompound.putString(TAG_RESEARCH_CHILD, child.toString());
            return childCompound;
        }).collect(NBTUtils.toListNBT());
        compound.setTag(TAG_CHILDS, childTagList);

        return compound;
    }

    @NotNull
    @Override
    public IGlobalResearch deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt)
    {
        final ResourceLocation id = new ResourceLocation(nbt.getString(TAG_ID));
        final ResourceLocation parent = nbt.contains(TAG_PARENT) ? new ResourceLocation(nbt.getString(TAG_PARENT)) : null;
        final ResourceLocation branch = new ResourceLocation(nbt.getString(TAG_BRANCH));
        final TranslatableContents name = new TranslatableContents(nbt.getString(TAG_NAME), null, TranslatableContents.NO_ARGS);
        final TranslatableContents subtitle = new TranslatableContents(nbt.getString(TAG_SUBTITLE_NAME), null, TranslatableContents.NO_ARGS);
        final int depth = nbt.getInt(TAG_RESEARCH_LVL);
        final int sortOrder =  nbt.getInt(TAG_RESEARCH_SORT);
        final boolean onlyChild = nbt.getBoolean(TAG_ONLY_CHILD);
        final boolean instant = nbt.getBoolean(TAG_INSTANT);
        final boolean autostart = nbt.getBoolean(TAG_AUTOSTART);
        final boolean immutable = nbt.getBoolean(TAG_IMMUTABLE);
        final boolean hidden = nbt.getBoolean(TAG_HIDDEN);

        final IGlobalResearch research = getNewInstance(id, parent, branch, name, subtitle, depth, sortOrder, onlyChild, hidden, autostart, instant, immutable);

        NBTUtils.streamCompound(nbt.getTagList(TAG_COSTS, NBTBase.TAG_COMPOUND)).forEach(compound -> {
            final ModResearchCosts.ResearchCostEntry researchCostType = IMinecoloniesAPI.getInstance().getResearchCostRegistry().getValue(new ResourceLocation(compound.getString(TAG_COST_TYPE)));
            research.addCost(researchCostType.readFromNBT(compound.getCompoundTag(TAG_COST_NBT)));
        });
        NBTUtils.streamCompound(nbt.getTagList(TAG_REQS, NBTBase.TAG_COMPOUND))
            .forEach(compound -> research.addRequirement(Objects.requireNonNull(IMinecoloniesAPI.getInstance()
                .getResearchRequirementRegistry()
                .getValue(ResourceLocation.tryParse(compound.getString(TAG_REQ_TYPE)))).readFromNBT(compound.getCompoundTag(TAG_REQ_ITEM))));

        NBTUtils.streamCompound(nbt.getTagList(TAG_EFFECTS, NBTBase.TAG_COMPOUND))
            .forEach(compound -> research.addEffect(Objects.requireNonNull(IMinecoloniesAPI.getInstance().getResearchEffectRegistry()
                .getValue(ResourceLocation.tryParse(compound.getString(TAG_EFFECT_TYPE)))).readFromNBT(compound.getCompoundTag(TAG_EFFECT_ITEM))));

        NBTUtils.streamCompound(nbt.getTagList(TAG_CHILDS, NBTBase.TAG_COMPOUND)).forEach(compound -> research.addChild(new ResourceLocation(compound.getString(TAG_RESEARCH_CHILD))));
        return research;
    }

    @Override
    public void serialize(@NotNull IFactoryController controller, IGlobalResearch input, PacketBuffer packetBuffer)
    {
        packetBuffer.writeResourceLocation(input.getId());
        packetBuffer.writeBoolean(input.getParent() != null);
        if (input.getParent() != null)
        {
            packetBuffer.writeResourceLocation(input.getParent());
        }
        packetBuffer.writeResourceLocation(input.getBranch());
        packetBuffer.writeUtf(input.getName().getKey());
        packetBuffer.writeUtf(input.getSubtitle().getKey());
        packetBuffer.writeVarInt(input.getDepth());
        packetBuffer.writeVarInt(input.getSortOrder());
        packetBuffer.writeBoolean(input.hasOnlyChild());
        packetBuffer.writeBoolean(input.isInstant());
        packetBuffer.writeBoolean(input.isAutostart());
        packetBuffer.writeBoolean(input.isImmutable());
        packetBuffer.writeBoolean(input.isHidden());
        packetBuffer.writeVarInt(input.getCostList().size());
        for (IResearchCost cost : input.getCostList())
        {
            packetBuffer.writeRegistryId(IMinecoloniesAPI.getInstance().getResearchCostRegistry(), cost.getType());
            packetBuffer.writeNbt(cost.writeToNBT());
        }
        packetBuffer.writeVarInt(input.getResearchRequirements().size());
        for (IResearchRequirement req : input.getResearchRequirements())
        {
            packetBuffer.writeRegistryId(IMinecoloniesAPI.getInstance().getResearchRequirementRegistry(), req.getRegistryEntry());
            packetBuffer.writeNbt(req.writeToNBT());
        }
        packetBuffer.writeVarInt(input.getEffects().size());
        for (IResearchEffect effect : input.getEffects())
        {
            packetBuffer.writeRegistryId(IMinecoloniesAPI.getInstance().getResearchEffectRegistry(), effect.getRegistryEntry());
            packetBuffer.writeNbt(effect.writeToNBT());
        }
        packetBuffer.writeVarInt(input.getChildren().size());
        for (ResourceLocation child : input.getChildren())
        {
            packetBuffer.writeResourceLocation(child);
        }
    }

    @NotNull
    @Override
    public IGlobalResearch deserialize(@NotNull IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        final ResourceLocation id = buffer.readResourceLocation();
        final ResourceLocation parent = buffer.readBoolean() ? buffer.readResourceLocation() : null;
        final ResourceLocation branch = buffer.readResourceLocation();
        final TranslatableContents name = new TranslatableContents(buffer.readUtf(), null, TranslatableContents.NO_ARGS);
        final TranslatableContents subtitle = new TranslatableContents(buffer.readUtf(), null, TranslatableContents.NO_ARGS);
        final int depth = buffer.readVarInt();
        final int sortOrder = buffer.readVarInt();
        final boolean hasOnlyChild = buffer.readBoolean();
        final boolean instant = buffer.readBoolean();
        final boolean autostart = buffer.readBoolean();
        final boolean immutable = buffer.readBoolean();
        final boolean hidden = buffer.readBoolean();

        final IGlobalResearch research = getNewInstance(id, parent, branch, name, subtitle, depth, sortOrder, hasOnlyChild, hidden, autostart, instant, immutable);

        final int costSize = buffer.readVarInt();
        for(int i = 0; i < costSize; i++)
        {
            final ModResearchCosts.ResearchCostEntry researchCostEntry = buffer.readRegistryIdSafe(ModResearchCosts.ResearchCostEntry.class);
            research.addCost(researchCostEntry.readFromNBT(buffer.readNbt()));
        }

        final int reqCount = buffer.readVarInt();
        for(int i = 0; i < reqCount; i++)
        {
            final ModResearchRequirements.ResearchRequirementEntry researchRequirementEntry = buffer.readRegistryIdSafe(ModResearchRequirements.ResearchRequirementEntry.class);
            research.addRequirement(researchRequirementEntry.readFromNBT(buffer.readNbt()));
        }

        final int effectCount = buffer.readVarInt();
        for(int i = 0; i < effectCount; i++)
        {
            final ModResearchEffects.ResearchEffectEntry researchEffectEntry = buffer.readRegistryIdSafe(ModResearchEffects.ResearchEffectEntry.class);
            research.addEffect(researchEffectEntry.readFromNBT(buffer.readNbt()));
        }

        final int childCount = buffer.readVarInt();
        for(int i = 0; i < childCount; i++)
        {
            research.addChild(buffer.readResourceLocation());
        }
        return research;
    }

    @Override
    public short getSerializationId()
    {
        return SerializationIdentifierConstants.GLOBAL_RESEARCH_ID;
    }
}





