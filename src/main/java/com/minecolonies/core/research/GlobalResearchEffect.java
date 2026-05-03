package com.minecolonies.core.research;

import com.minecolonies.api.research.ModResearchEffects;
import com.minecolonies.api.research.IResearchEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.contents.TranslatableContents;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An instance of a Research Effect at a specific strength, to be applied to a specific colony.
 */
public class GlobalResearchEffect implements IResearchEffect
{
    /**
     * The NBT NBTBase for an individual effect's identifier, as a ResourceLocation.
     */
    private static final String TAG_ID = "id";

    /**
     * The NBT NBTBase for an individual effect's description, as a human-readable string or TranslationText key.
     */
    private static final String TAG_DESC = "desc";

    /**
     * The NBT NBTBase for an individual effect's subtitle, as a human-readable string or TranslationText key.
     */
    private static final String TAG_SUBTITLE = "subtitle";

    /**
     * The NBT NBTBase for an individual effect's strength, in magnitude.
     */
    private static final String TAG_EFFECT = "effect";

    /**
     * The NBT NBTBase for an individual effect's display value, usually the difference between its strength and the previous World.
     */
    private static final String TAG_DISPLAY_EFFECT = "display";

    /**
     * The unique effect Id.
     */
    private final ResourceLocation id;

    /**
     * The optional text description of the effect. If empty, a translation key will be derived from id.
     */
    private final TranslatableContents name;

    /**
     * The optional subtitle text description of the effect. If empty, a translation key will be derived from id.
     */
    private final TranslatableContents subtitle;

    /**
     * The absolute effect strength to apply.
     */
    private final double effect;

    /**
     * The relative strength of effect to display
     */
    private final double displayEffect;

    /**
     * The constructor to create a new global research effect, with a statically assigned description.
     *
     * @param id            the id to unlock.
     * @param name          the effect's description, for display.
     * @param subtitle      the effect's subtitle description.
     * @param effect        the effect's absolute strength.
     * @param displayEffect the effect's relative strength, for display purposes.
     */
    public GlobalResearchEffect(final ResourceLocation id, final String name, final String subtitle, final double effect, final double displayEffect)
    {
        this.id = id;
        this.name = new TranslatableContents(name, null, List.of(displayEffect, effect, Math.round(displayEffect * 100), Math.round(effect * 100)).toArray());
        this.subtitle = new TranslatableContents(subtitle, null, TranslatableContents.NO_ARGS);
        this.effect = effect;
        this.displayEffect = displayEffect;
    }

    /**
     * The constructor to build a new global research effect from an NBT.
     *
     * @param nbt the nbt containing the traits for the global research.
     */
    public GlobalResearchEffect(final NBTTagCompound nbt)
    {
        this.id = new ResourceLocation(nbt.getString(TAG_ID));
        this.effect = nbt.getDouble(TAG_EFFECT);
        this.displayEffect = nbt.getDouble(TAG_DISPLAY_EFFECT);
        this.name = new TranslatableContents(nbt.getString(TAG_DESC), null, List.of(displayEffect, effect, Math.round(displayEffect * 100), Math.round(effect * 100)).toArray());
        this.subtitle = new TranslatableContents(nbt.getString(TAG_SUBTITLE), null, TranslatableContents.NO_ARGS);
    }

    @Override
    public ModResearchEffects.ResearchEffectEntry getRegistryEntry()
    {
        return ModResearchEffects.globalResearchEffect.get();
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public TranslatableContents getName()
    {
        return this.name;
    }

    @Override
    public TranslatableContents getSubtitle()
    {
        return this.subtitle;
    }

    @Override
    public double getEffect()
    {
        return this.effect;
    }

    @Override
    public boolean overrides(@NotNull final IResearchEffect other)
    {
        return Math.abs(effect) > Math.abs(((GlobalResearchEffect) other).effect);
    }

    @Override
    public NBTTagCompound writeToNBT()
    {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString(TAG_ID, id.toString());
        nbt.setString(TAG_DESC, name.getKey());
        nbt.setString(TAG_SUBTITLE, subtitle.getKey());
        nbt.setDouble(TAG_EFFECT, effect);
        nbt.setDouble(TAG_DISPLAY_EFFECT, displayEffect);
        return nbt;
    }
}




