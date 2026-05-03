package com.minecolonies.api.colony;

import com.minecolonies.api.colony.interactionhandling.IInteractionResponseHandler;
import com.minecolonies.api.colony.jobs.IJobView;
import com.minecolonies.api.entity.citizen.VisibleCitizenStatus;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenHappinessHandler;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenSkillHandler;
import com.minecolonies.api.util.Tuple;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
// [1.7.10] world.entity removed
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ICitizenDataView extends ICitizen
{
    /**
     * Entity Id getter.
     *
     * @return entity id.
     */
    int getEntityId();

    /**
     * Entity job getter.
     *
     * @return the job as a string.
     */
    String getJob();

    /**
     * Get the job as mutable String.
     * @return the job desc.
     */
    String getJobComponent();

    /**
     * Get the entities home building.
     *
     * @return the home coordinates.
     */
    @Nullable
    int[] getHomeBuilding();

    /**
     * Get the entities work building.
     *
     * @return the work coordinates.
     */
    @Nullable
    int[] getWorkBuilding();

    /**
     * DEPRECATED
     *
     * @param bp the position.
     */
    void setWorkBuilding(int[] bp);

    /**
     * Get the colony id of the citizen.
     *
     * @return unique id of the colony.
     */
    int getColonyId();

    /**
     * Gets the current Happiness value for the citizen
     *
     * @return citizens current Happiness value
     */
    double getHappiness();

    /**
     * Get the last registered position of the citizen.
     *
     * @return the int[].
     */
    int[] getPosition();

    /**
     * Deserialize the attributes and variables from transition.
     *
     * @param buf Byte buffer to deserialize.
     */
    void deserialize(@NotNull PacketBuffer buf);

    /**
     * @return current health.
     */
    double getHealth();

    /**
     * @return max health.
     */
    double getMaxHealth();

    /**
     * Get the list of ordered interactions.
     *
     * @return the list.
     */
    List<IInteractionResponseHandler> getOrderedInteractions();

    /**
     * Get a specific interaction by key.
     *
     * @param String the key.
     * @return the interaction or null.
     */
    @Nullable
    IInteractionResponseHandler getSpecificInteraction(@NotNull String String);

    /**
     * Check if the citizen has important interactions.
     *
     * @return true if so.
     */
    boolean hasBlockingInteractions();

    /**
     * Check if the citizen has any visible interactions.
     * 
     * @return true if so.
     */
    boolean hasVisibleStatus();

    /**
     * Check if the citizen has any interactions.
     *
     * @return true if so.
     */
    boolean hasPendingInteractions();

    /**
     * Get an instance of the skill handler.
     *
     * @return the instance.
     */
    ICitizenSkillHandler getCitizenSkillHandler();

    /**
     * The citizen happiness handler.
     *
     * @return the handler.
     */
    ICitizenHappinessHandler getHappinessHandler();

    /**
     * The texture to render for interactions
     *
     * @return resourcelocation
     */
    ResourceLocation getStatusIcon();

    /**
     * Get the visible citizen status
     *
     * @return status
     */
    VisibleCitizenStatus getVisibleStatus();

    /**
     * Gets a location of interest of this citizen's job.
     *
     * @return the location, or null if nowhere is particularly interesting right now.
     */
    @Nullable int[] getStatusPosition();

    /**
     * Get the job view that belongs to this citizen (or null).
     * @return the job.
     */
    @Nullable
    IJobView getJobView();

    /**
     * Get the partner of the citizen.
     * @return the partner or null if non existent.
     */
    @Nullable
    Integer getPartner();

    /**
     * Get the list of children of a citizen.
     * @return the citizen ids.
     */
    List<Integer> getChildren();

    /**
     * Get the list of children of a citizen.
     * @return the citizen ids.
     */
    List<Integer> getSiblings();

    /**
     * Get the names of the parents.
     * @return the name.
     */
    Tuple<String, String> getParents();

    /**
     * Get the custom texture of the citizen.
     * @return the res location.
     */
    ResourceLocation getCustomTexture();

    /**
     * Force set the job view.
     * @param view the job view to set.
     */
    void setJobView(IJobView view);

    /**
     * Set the home building on the client side.
     * @param position the pos of the home building.
     */
    void setHomeBuilding(int[] position);

    /**
     * Get UUID of the custom texture.
     * @return the uuid.
     */
    UUID getCustomTextureUUID();

    /**
     * Get Armor in slot of citizen data view.
     * @param slot the equipment slot to get it from.
     * @return the armor in the slot.
     */
    ItemStack getDisplayArmor(int slot /* EquipmentSlot */);

    /**
     * Check if sick.
     * @return true if so.
     */
    boolean isSick();

    @Override
    IColonyView getColony();
}





