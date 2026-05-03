package com.minecolonies.core.colony.interactionhandling;

import com.ldtteam.structurize.api.util.BlockPosUtil;
import com.minecolonies.api.colony.ICitizen;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.interactionhandling.IChatPriority;
import com.minecolonies.api.colony.interactionhandling.IInteractionResponseHandler;
import com.minecolonies.api.colony.interactionhandling.InteractionValidatorRegistry;
import com.minecolonies.api.colony.interactionhandling.ModInteractionResponseHandlers;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.nbt.NBTTagCompound;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import static com.minecolonies.core.colony.interactionhandling.StandardInteraction.*;

/**
 * The position based interaction response handler.
 */
public class PosBasedInteraction extends ServerCitizenInteraction
{
    private static final String POS_TAG = "pos";

    @SuppressWarnings("unchecked")
    private static final Tuple<String, String>[] responses = (Tuple<String, String>[]) new Tuple[] {
      new Tuple<>(String.translatable(INTERACTION_R_OKAY), null),
      new Tuple<>(String.translatable(INTERACTION_R_IGNORE), null),
      new Tuple<>(String.translatable(INTERACTION_R_REMIND), null),
      new Tuple<>(String.translatable(INTERACTION_R_SKIP), null)};

    /**
     * The position this is related to.
     */
    private int[] pos = null;

    /**
     * Specific validator for this one.
     */
    private BiPredicate<ICitizenData, int[]> validator;

    /**
     * The server interaction response handler.
     *
     * @param inquiry   the client inquiry.
     * @param priority  the interaction priority.
     * @param pos       the pos this is related to.
     * @param validator the validator id.
     */
    public PosBasedInteraction(
      final String inquiry,
      final IChatPriority priority,
      final String validator,
      final int[] pos)
    {
        super(inquiry, true, priority, null, validator, responses);
        this.validator = InteractionValidatorRegistry.getPosBasedInteractionValidatorPredicate(validator);
        this.pos = pos;
    }

    /**
     * The server interaction response handler.
     *
     * @param inquiry  the client inquiry.
     * @param priority the interaction priority.
     * @param pos      the pos this is related to.
     */
    public PosBasedInteraction(
      final String inquiry,
      final IChatPriority priority,
      final int[] pos)
    {
        super(inquiry, true, priority, null, inquiry, responses);
        this.validator = InteractionValidatorRegistry.getPosBasedInteractionValidatorPredicate(inquiry);
        this.pos = pos;
    }

    /**
     * Way to load the response handler for a citizen.
     *
     * @param data the citizen owning this handler.
     */
    public PosBasedInteraction(final ICitizen data)
    {
        super(data);
    }

    @Override
    public List<IInteractionResponseHandler> genChildInteractions()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean isValid(final ICitizenData citizen)
    {
        if (pos != null)
        {
            if (!WorldUtil.isBlockLoaded(citizen.getColony().getWorld(), pos))
            {
                return true;
            }
        }

        return (validator == null && !this.parents.isEmpty()) || (validator != null && validator.test(citizen, pos));
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound NBTBase = super.serializeNBT();
        BlockPosUtil.writeToNBT(NBTBase, POS_TAG, pos);
        return NBTBase;
    }

    @Override
    public void deserializeNBT(@NotNull final NBTTagCompound compoundNBT)
    {
        super.deserializeNBT(compoundNBT);
        this.pos = BlockPosUtil.readFromNBT(compoundNBT, POS_TAG);
    }

    @Override
    protected void loadValidator()
    {
        this.validator = InteractionValidatorRegistry.getPosBasedInteractionValidatorPredicate(validatorId);
    }

    @Override
    public String getType()
    {
        return ModInteractionResponseHandlers.POS.getPath();
    }
}



