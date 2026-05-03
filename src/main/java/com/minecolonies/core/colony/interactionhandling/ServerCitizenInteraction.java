package com.minecolonies.core.colony.interactionhandling;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

// [1.7.10] blockui replaced by ModularUI2
import com.minecolonies.api.colony.ICitizen;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.interactionhandling.AbstractInteractionResponseHandler;
import com.minecolonies.api.colony.interactionhandling.IChatPriority;
import com.minecolonies.api.colony.interactionhandling.InteractionValidatorRegistry;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.core.Network;
import com.minecolonies.core.client.gui.citizen.MainWindowCitizen;
import com.minecolonies.core.network.messages.server.colony.InteractionResponse;
import net.minecraft.nbt.NBTBase;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.minecolonies.api.util.constant.Constants.TICKS_SECOND;
import static com.minecolonies.core.colony.interactionhandling.StandardInteraction.*;

/**
 * The server side interaction response handler.
 */
public abstract class ServerCitizenInteraction extends AbstractInteractionResponseHandler
{
    private static final String TAG_DELAY        = "delay";
    private static final String TAG_PARENT       = "parent";
    private static final String TAG_PARENTS      = "parents";
    private static final String TAG_VALIDATOR_ID = "validator";

    /**
     * At which world tick this should be displayed again.
     */
    private int displayAtWorldTick = 0;

    /**
     * Validator to test for this.
     */
    private Predicate<ICitizenData> validator;

    /**
     * The id of the validator.
     */
    protected String validatorId;

    /**
     * All registered parents of this response handler.
     */
    protected Set<String> parents = new HashSet<>();

    /**
     * The server interaction response handler.
     *
     * @param inquiry        the client inquiry.
     * @param primary        if primary interaction.
     * @param priority       the interaction priority.
     * @param validator      validation predicate to check if this interaction is still valid.
     * @param validatorId    the id of the validator.
     * @param responseTuples the tuples mapping player responses to further interactions.
     */
    @SafeVarargs
    public ServerCitizenInteraction(
      final String inquiry,
      final boolean primary,
      final IChatPriority priority,
      final Predicate<ICitizenData> validator,
      final String validatorId,
      final Tuple<String, String>... responseTuples)
    {
        super(inquiry, primary, priority, responseTuples);
        this.validator = validator;
        this.validatorId = validatorId;
    }

    /**
     * Way to load the response handler for a citizen.
     *
     * @param data the citizen owning this handler.
     */
    public ServerCitizenInteraction(final ICitizen data)
    {
        super();
    }

    @Override
    public boolean isVisible(final World world)
    {
        return displayAtWorldTick == 0 || displayAtWorldTick < world.getGameTime();
    }

    @Override
    public boolean isValid(final ICitizenData citizen)
    {
        return (validator == null && !this.parents.isEmpty()) || (validator != null && validator.test(citizen));
    }

    /**
     * Add a parent to the list.
     *
     * @param parent the parent to add.
     */
    public void addParent(final String parent)
    {
        this.parents.add(parent);
    }

    /**
     * Remove an old parent and return true if no parent is left.
     *
     * @param oldParent the parent to remove.
     */
    public void removeParent(final String oldParent)
    {
        this.parents.remove(oldParent);
    }

    @Override
    public void onServerResponseTriggered(final int responseId, final Player player, final ICitizenData data)
    {
        final String response = getPossibleResponses().get(responseId);
        tryHandleIgnoreResponse(response, player);
    }

    /**
     * Check if the response was an ignore response.
     * @param response the response to compare.
     * @param player the player that triggered it.
     */
    private void tryHandleIgnoreResponse(final String response, final Player player)
    {
        if (response.getContents() instanceof TranslatableContents)
        {
            if (((TranslatableContents) response.getContents()).getKey().equals(INTERACTION_R_IGNORE))
            {
                // 6 hours later
                displayAtWorldTick = (int) (player.World.getGameTime() + (TICKS_SECOND * 60 * 60 * 6));
            }
            else if (((TranslatableContents) response.getContents()).getKey().equals(INTERACTION_R_REMIND))
            {
                // 1 hour later
                displayAtWorldTick = (int) (player.World.getGameTime() + (TICKS_SECOND * 60 * 60));
            }
            else if (((TranslatableContents) response.getContents()).getKey().equals(INTERACTION_R_OKAY) || ((TranslatableContents) response.getContents()).getKey().equals(INTERACTION_R_SKIP))
            {
                // 5 minutes
                displayAtWorldTick = (int) (player.World.getGameTime() + (TICKS_SECOND * 60 * 5));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean onClientResponseTriggered(final int responseId, final Player player, final ICitizenDataView data, final Object /* BOWindow: todo ModularUI2 */ window)
    {
        final String response = getPossibleResponses().get(responseId);
        tryHandleIgnoreResponse(response, player);
        if (((TranslatableContents) getPossibleResponses().get(responseId).getContents()).getKey().equals("com.minecolonies.coremod.gui.chat.skipchitchat"))
        {
            final MainWindowCitizen windowCitizen = new MainWindowCitizen(data);
            windowCitizen.open();
        }

        Network.getNetwork().sendToServer(new InteractionResponse(data.getColonyId(), data.getId(), player.World.dimension(), this.getInquiry(), responseId));
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound compoundNBT = super.serializeNBT();
        compoundNBT.putInt(TAG_DELAY, displayAtWorldTick);
        final NBTTagList list = new NBTTagList();
        for (final String element : parents)
        {
            final NBTTagCompound elementTag = new NBTTagCompound();
            elementTag.putString(TAG_PARENT, String.Serializer.toJson(element));
            list.add(elementTag);
        }
        compoundNBT.put(TAG_PARENTS, list);
        compoundNBT.putString(TAG_VALIDATOR_ID, String.Serializer.toJson(validatorId));
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(@NotNull final NBTTagCompound compoundNBT)
    {
        super.deserializeNBT(compoundNBT);
        this.displayAtWorldTick = compoundNBT.getInt(TAG_DELAY);
        this.parents.clear();
        final NBTTagList list = compoundNBT.getList(TAG_PARENTS, NBTBase.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            this.parents.add(String.Serializer.fromJson(compoundNBT.getString(TAG_PARENT)));
        }
        this.validatorId = String.Serializer.fromJson(compoundNBT.getString(TAG_VALIDATOR_ID));
        loadValidator();
    }

    /**
     * Load the validator.
     */
    protected void loadValidator()
    {
        this.validator = InteractionValidatorRegistry.getStandardInteractionValidatorPredicate(validatorId);
    }

    @Override
    public String getId()
    {
        return getInquiry();
    }
}





