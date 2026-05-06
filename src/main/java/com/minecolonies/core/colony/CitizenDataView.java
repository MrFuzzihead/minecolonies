package com.minecolonies.core.colony;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.interactionhandling.ChatPriority;
import com.minecolonies.api.colony.interactionhandling.IInteractionResponseHandler;
import com.minecolonies.api.colony.jobs.IJobView;
import com.minecolonies.api.colony.jobs.registry.IJobDataManager;
import com.minecolonies.api.entity.citizen.VisibleCitizenStatus;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenHappinessHandler;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenSkillHandler;
import com.minecolonies.api.inventory.InventoryCitizen;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.api.util.constant.Suppression;
import com.minecolonies.core.MineColonies;
import com.minecolonies.core.colony.interactionhandling.ServerCitizenInteraction;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenHappinessHandler;
import com.minecolonies.core.entity.citizen.citizenhandlers.CitizenSkillHandler;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
// [1.7.10] int /* InteractionHand */ removed
import net.minecraft.entity.Entity;
// [1.7.10] world.entity removed
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_OFFHAND_HELD_ITEM_SLOT;
import static com.minecolonies.api.util.constant.TranslationConstants.COM_MINECOLONIES_COREMOD_GUI_TOWNHALL_CITIZEN_UNEMPLOYED;

/**
 * The CitizenDataView is the client-side representation of a CitizenData. Views contain the CitizenData's data that is relevant to a Client, in a more client-friendly form.
 * Mutable operations on a View result in a message to the server to perform the operation.
 */
public class CitizenDataView implements ICitizenDataView
{
    /**
     * Santa Hat.
     */
    private static ItemStack cachedDisplaySantaHat = null;

    private static final String TAG_HELD_ITEM_SLOT = "HeldItemSlot";

    /**
     * The resource location for the blocking overlay.
     */
    private static final ResourceLocation BLOCKING_RESOURCE = new ResourceLocation(Constants.MOD_ID, "textures/icons/blocking.png");

    /**
     * The resource location for the pending overlay.
     */
    private static final ResourceLocation PENDING_RESOURCE = new ResourceLocation(Constants.MOD_ID, "textures/icons/warning.png");

    /**
     * Attributes.
     */
    private final int     id;
    private final IColonyView colonyView;
    protected     int     entityId;
    protected     String  name;
    protected     boolean female;
    protected     boolean paused;
    protected     boolean isChild;

    private IJobView jobView;

    /**
     * colony id of the citizen.
     */
    protected int colonyId;

    /**
     * Placeholder skills.
     */
    private double saturation;

    /**
     * holds the current citizen happiness value
     */
    private double happiness;

    /**
     * The position of the guard.
     */
    private int[] position;

    /**
     * Job identifier.
     */
    private String job;

    /**
     * Working and home position.
     */
    @Nullable
    private int[] homeBuilding;
    @Nullable
    private int[] workBuilding;

    private InventoryCitizen inventory;

    /**
     * The citizen chat options on the server side.
     */
    private final Map<String, IInteractionResponseHandler> citizenChatOptions = new LinkedHashMap<>();

    /**
     * List of primary interactions (sorted by priority).
     */
    private List<IInteractionResponseHandler> sortedInteractions;

    /**
     * The citizen skill handler on the client side.
     */
    private final CitizenSkillHandler citizenSkillHandler;

    /**
     * The citizen happiness handler.
     */
    private final CitizenHappinessHandler citizenHappinessHandler;

    /**
     * The citizens status icon
     */
    private VisibleCitizenStatus statusIcon;

    /**
     * The current location of interest.
     */
    @Nullable private int[] statusPosition;

    /**
     * Parents of the citizen.
     */
    private Tuple<String, String> parents = new Tuple<>("", "");

    /**
     * Alive children of the citizen
     */
    private List<Integer> children = new ArrayList<>();

    /**
     * Alive siblings of the citizen.
     */
    private List<Integer> siblings = new ArrayList<>();

    /**
     * Alive partner of the citizen.
     */
    private Integer partner;

    /**
     * The list of available quests the citizen can give out.
     */
    private final List<ResourceLocation> availableQuests = new ArrayList<>();

    /**
     * The list of participating quests the citizen can give out.
     */
    private final List<ResourceLocation> participatingQuests = new ArrayList<>();

    /**
     * Texture UUID.
     */
    protected UUID textureUUID;

    /**
     * Flag is citizen is sick.
     */
    private boolean isSick;

    /**
     * Set View id.
     *
     * @param id the id to set.
     */
    protected CitizenDataView(final int id, final IColonyView colonyView)
    {
        this.id = id;
        this.citizenSkillHandler = new CitizenSkillHandler();
        this.citizenHappinessHandler = new CitizenHappinessHandler();
        this.colonyView = colonyView;
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public int getEntityId()
    {
        return entityId;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isFemale()
    {
        return female;
    }

    @Override
    public boolean isPaused()
    {
        return paused;
    }

    @Override
    public IColonyView getColony()
    {
        return colonyView;
    }

    @Override
    public boolean isChild()
    {
        return isChild;
    }

    /**
     * DEPRECATED
     */
    @Override
    public void setPaused(final boolean p)
    {
        this.paused = p;
    }

    @Override
    public String getJob()
    {
        return job;
    }

    @Override
    public String getJobComponent()
    {
        return job.isEmpty() ? String.translatable(COM_MINECOLONIES_COREMOD_GUI_TOWNHALL_CITIZEN_UNEMPLOYED) : String.translatable(job);
    }

    @Override
    @Nullable
    public int[] getHomeBuilding()
    {
        return homeBuilding;
    }

    @Override
    @Nullable
    public int[] getWorkBuilding()
    {
        return workBuilding;
    }

    @Override
    public void setHomeBuilding(final int[] homeBuilding)
    {
        this.homeBuilding = homeBuilding;
    }

    @Override
    public void setWorkBuilding(@Nullable final int[] bp)
    {
        this.workBuilding = bp;
    }

    @Override
    public int getColonyId()
    {
        return colonyId;
    }

    @Override
    public double getHappiness()
    {
        return happiness;
    }

    @Override
    public double getSaturation()
    {
        return saturation;
    }

    @Override
    public double getHealth()
    {
        final Entity entity = colonyView.getWorld().getEntity(entityId);

        if (entity instanceof EntityLivingBase)
        {
            return ((EntityLivingBase) entity).getHealth();
        }

        return CitizenData.MAX_HEALTH;
    }

    @Override
    public double getMaxHealth()
    {
        final Entity entity = colonyView.getWorld().getEntity(entityId);

        if (entity instanceof EntityLivingBase)
        {
            return ((EntityLivingBase) entity).getMaxHealth();
        }

        return CitizenData.MAX_HEALTH;
    }

    @Override
    public int[] getPosition()
    {
        return position;
    }

    @Override
    public void deserialize(@NotNull final PacketBuffer buf)
    {
        name = buf.readUtf(32767);
        female = buf.readBoolean();
        entityId = buf.readInt();
        paused = buf.readBoolean();
        isChild = buf.readBoolean();

        homeBuilding = buf.readBoolean() ? buf.readBlockPos() : null;
        workBuilding = buf.readBoolean() ? buf.readBlockPos() : null;

        saturation = buf.readDouble();
        happiness = buf.readDouble();

        citizenSkillHandler.read(buf.readNbt());

        job = buf.readUtf(32767);

        colonyId = buf.readInt();

        final NBTTagCompound compound = buf.readNbt();
        inventory = new InventoryCitizen(this.name, true);
        this.inventory.read(compound);
        this.inventory.setHeldItem(0 /* InteractionHand.MAIN_HAND */, compound.getInt(TAG_HELD_ITEM_SLOT));
        this.inventory.setHeldItem(1 /* InteractionHand.OFF_HAND */, compound.getInt(TAG_OFFHAND_HELD_ITEM_SLOT));

        position = buf.readBlockPos();

        citizenChatOptions.clear();
        final int size = buf.readInt();
        for (int i = 0; i < size; i++)
        {
            final NBTTagCompound compoundNBT = buf.readNbt();
            final ServerCitizenInteraction handler =
              (ServerCitizenInteraction) MinecoloniesAPIProxy.getInstance().getInteractionResponseHandlerDataManager().createFrom(this, compoundNBT);
            citizenChatOptions.put(handler.getInquiry(), handler);
        }

        sortedInteractions = new ArrayList<>(citizenChatOptions.values());
        sortedInteractions.sort(Comparator.comparingInt(e -> -e.getPriority().getPriority()));

        citizenHappinessHandler.read(buf.readNbt(), false);

        int statusindex = buf.readInt();
        statusIcon = statusindex >= 0 ? VisibleCitizenStatus.getForId(statusindex) : null;
        statusPosition = buf.readBoolean() ? buf.readBlockPos() : null;

        if (buf.readBoolean())
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(colonyId, Minecraft.getInstance().World.dimension());
            jobView = IJobDataManager.getInstance().createViewFrom(colonyView, this, buf);
        }
        else
        {
            jobView = null;
        }

        children.clear();
        siblings.clear();

        partner = buf.readInt();
        final int siblingsSize = buf.readInt();
        for (int i = 0; i < siblingsSize; i++)
        {
            siblings.add(buf.readInt());
        }

        final int childrenSize = buf.readInt();
        for (int i = 0; i < childrenSize; i++)
        {
            children.add(buf.readInt());
        }

        final String parentA = buf.readUtf();
        final String parentB = buf.readUtf();
        parents = new Tuple<>(parentA, parentB);

        availableQuests.clear();
        participatingQuests.clear();

        final int avSize = buf.readInt();
        for (int i = 0; i < avSize; i++)
        {
            availableQuests.add(buf.readResourceLocation());
        }

        final int partSize = buf.readInt();
        for (int i = 0; i < partSize; i++)
        {
            participatingQuests.add(buf.readResourceLocation());
        }

        if (buf.readBoolean())
        {
            textureUUID = buf.readUUID();
        }
        this.isSick = buf.readBoolean();
    }

    @Override
    public IJobView getJobView()
    {
        return this.jobView;
    }

    @Override
    public InventoryCitizen getInventory()
    {
        return inventory;
    }

    @Override
    public List<IInteractionResponseHandler> getOrderedInteractions()
    {
        return sortedInteractions;
    }

    @Override
    @Nullable
    public IInteractionResponseHandler getSpecificInteraction(@NotNull final String String)
    {
        return citizenChatOptions.getOrDefault(String, null);
    }

    @Override
    public boolean hasBlockingInteractions()
    {
        if (sortedInteractions.isEmpty())
        {
            return false;
        }

        for (final IInteractionResponseHandler interaction : sortedInteractions)
        {
            if (interaction.getPriority().getPriority() >= ChatPriority.IMPORTANT.getPriority())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasVisibleStatus()
    {
        if (statusIcon != null && statusIcon.shouldRender())
        {
            return true;
        }

        if (sortedInteractions.isEmpty())
        {
            return false;
        }

        for (final IInteractionResponseHandler interaction : sortedInteractions)
        {
            if (interaction.getPriority().getPriority() >= ChatPriority.CHITCHAT.getPriority())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPendingInteractions()
    {
        if (sortedInteractions.isEmpty())
        {
            return false;
        }

        for (final IInteractionResponseHandler interaction : sortedInteractions)
        {
            if (interaction.isPrimary())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public ICitizenSkillHandler getCitizenSkillHandler()
    {
        return citizenSkillHandler;
    }

    @Override
    public ICitizenHappinessHandler getHappinessHandler()
    {
        return citizenHappinessHandler;
    }

    @Override
    public ResourceLocation getStatusIcon()
    {
        if (statusIcon != null && statusIcon.shouldRender())
        {
            return statusIcon.getIcon();
        }

        if (sortedInteractions == null || sortedInteractions.isEmpty())
        {
            return null;
        }

        ResourceLocation icon = sortedInteractions.get(0).getInteractionIcon();
        if (icon == null)
        {
            if (hasBlockingInteractions())
            {
                icon = BLOCKING_RESOURCE;
            }
            else if (hasVisibleStatus())
            {
                icon = PENDING_RESOURCE;
            }
        }

        return icon;
    }

    @Override
    public VisibleCitizenStatus getVisibleStatus()
    {
        return statusIcon;
    }

    @Override
    public @Nullable int[] getStatusPosition()
    {
        return statusPosition;
    }

    @Nullable
    @Override
    public Integer getPartner()
    {
        return partner;
    }

    @Override
    public List<Integer> getChildren()
    {
        return new ArrayList<>(children);
    }

    @Override
    public List<Integer> getSiblings()
    {
        return new ArrayList<>(siblings);
    }

    @Override
    public Tuple<String, String> getParents()
    {
        return parents;
    }

    @Override
    public ResourceLocation getCustomTexture()
    {
        return null;
    }

    @Override
    public UUID getCustomTextureUUID()
    {
        return textureUUID;
    }

    @Override
    public void setJobView(final IJobView jobView)
    {
        this.jobView = jobView;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @SuppressWarnings(Suppression.TOO_MANY_RETURNS)
    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final ICitizenDataView data = (ICitizenDataView) o;

        return id == data.getId();
    }

    @Override
    public ItemStack getDisplayArmor(final int slot /* EquipmentSlot */)
    {
        if (cachedDisplaySantaHat == null)
        {
            if (MineColonies.getConfig().getClient().holidayFeatures.get() && LocalDate.now(Clock.systemDefaultZone()).getMonth() == Month.DECEMBER)
            {
                cachedDisplaySantaHat = new ItemStack(ModItems.santaHat);
            }
            else
            {
                cachedDisplaySantaHat = null;
            }
        }

        final ItemStack currentHat = getInventory().getArmorInSlot(slot);
        // [1.7.10] slot 3 == helmet slot; show santa hat if helmet slot is empty in December
        if (currentHat.isEmpty() && cachedDisplaySantaHat != null && cachedDisplaySantaHat != null && slot == 3 /* HELMET */)
        {
            return cachedDisplaySantaHat;
        }

        return currentHat;
    }

    @Override
    public boolean isSick()
    {
        return this.isSick;
    }
}







