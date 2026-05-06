package com.minecolonies.core.colony.requestsystem.requests;
import net.minecraft.tags.TagKey;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.Direction;
// [1.7.10] removed: import net.minecraft.core.Direction; (use net.minecraft.util.Direction)
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;

import com.google.common.collect.ImmutableList;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.buildings.ICommonBuilding;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.requestsystem.request.RequestState;
import com.minecolonies.api.colony.requestsystem.requestable.*;
import com.minecolonies.api.colony.requestsystem.requestable.crafting.AbstractCrafting;
import com.minecolonies.api.colony.requestsystem.requestable.crafting.PrivateCrafting;
import com.minecolonies.api.colony.requestsystem.requestable.crafting.PublicCrafting;
import com.minecolonies.api.colony.requestsystem.requestable.deliveryman.Delivery;
import com.minecolonies.api.colony.requestsystem.requestable.deliveryman.Pickup;
import com.minecolonies.api.colony.requestsystem.requester.IRequester;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.ItemStackUtils;
import com.minecolonies.api.util.constant.EquipmentLevelConstants;
import com.minecolonies.api.util.constant.TranslationConstants;
import com.minecolonies.api.util.constant.translation.RequestSystemTranslationConstants;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.moduleviews.WorkerBuildingModuleView;
import com.minecolonies.core.colony.jobs.views.CrafterJobView;
import com.minecolonies.core.colony.jobs.views.DmanJobView;
import com.minecolonies.core.colony.requestable.SmeltableOre;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
// [1.7.10] block.entity removed
// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Final class holding all the requests for requestables inside minecolonie
 */
public final class StandardRequests
{

    /**
     * private constructor to hide the implicit public one.
     */
    private StandardRequests()
    {
        super();
    }

    /**
     * Request for a single ItemStack.
     */
    public static class ItemStackRequest extends AbstractRequest<Stack>
    {
        public ItemStackRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final Stack requested)
        {
            super(requester, token, requested);
        }

        public ItemStackRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestState state, @NotNull final Stack requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String combined = String.literal("");

            if (getRequest().getMinimumCount() == getRequest().getCount())
            {
                combined.append(String.literal(getRequest().getCount() + " "));
                combined.append(getRequest().getStack().getHoverName());
            }
            else
            {
                combined.append(String.literal(getRequest().getMinimumCount() + "-" + getRequest().getCount() + " "));
                combined.append(getRequest().getStack().getHoverName());
            }

            return combined;
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            return getRequest().getRequestedItems();
        }    
    }

    /**
     * Request for a list of potential candidates.
     */
    public static class ItemStackListRequest extends AbstractRequest<StackList>
    {
        /**
         * The list to display to the player.
         */
        private ImmutableList<ItemStack> displayList;

        /**
         * The Stacklist which is being requested.
         */
        private StackList stackList;

        /**
         * Constructor of the request.
         *
         * @param requester the requester.
         * @param token     the token assigned to this request.
         * @param requested the request data.
         */
        public ItemStackListRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final StackList requested)
        {
            super(requester, token, requested);
            this.displayList = ImmutableList.copyOf(requested.getStacks());
            this.stackList = requested;
        }

        /**
         * Constructor of the request.
         *
         * @param requester the requester.
         * @param token     the token assigned to this request.
         * @param state     the state of the request.
         * @param requested the request data.
         */
        public ItemStackListRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestState state, @NotNull final StackList requested)
        {
            super(requester, token, state, requested);
            this.displayList = ImmutableList.copyOf(requested.getStacks());
            this.stackList = requested;
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String result = String.literal("");
            result.append(String.translatable(stackList.getDescription()));
            return result;
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            if (displayList.isEmpty())
            {
                return ImmutableList.of();
            }
            return displayList;
        }
    }

    /**
     * Request for a single ItemStack.
     */
    public static class ItemTagRequest extends AbstractRequest<RequestTag>
    {

        private List<ItemStack> stacks;

        public ItemTagRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestTag requested)
        {
            super(requester, token, requested);
            stacks = ForgeRegistries.ITEMS.tags().getTag(requested.getTag()).stream().map(ItemStack::new).collect(Collectors.toList());
        }

        public ItemTagRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestState state, @NotNull final RequestTag requested)
        {
            super(requester, token, state, requested);
            stacks = ForgeRegistries.ITEMS.tags().getTag(requested.getTag()).stream().map(ItemStack::new).collect(Collectors.toList());
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String combined = String.literal("");
            combined.append(String.literal(getRequest().getCount() + " "));
            // getRequest().getTag() is a long string that can't be easily be read by players or turned into a translation key.
            // Instead, try to get a translated text first.
            final String tagKey = "com.minecolonies.coremod.NBTBase." + getRequest().getTag().toString().toLowerCase().replace
                                                                                                                     ("namedtag[", "").replace(':', '.').replace("]", "");
            final String tagText = String.translatable(tagKey);
            // test the translated text; if there's a difference, the client has a matching translation key.
            if (!tagText.getString().equals(tagKey))
            {
                combined.append(String.literal("#").append(tagText));
            }
            // Otherwise, use the first item from request set if present, or the full NBTBase identifier to assist debugging otherwise.
            else if (!stacks.isEmpty())
            {
                combined.append(String.literal("#").append(stacks.get(0).getHoverName()));
            }
            else
            {
                combined.append(String.literal("#").append(String.literal(getRequest().getTag().toString())));
            }
            return combined;
        }

        @Override
        public List<ItemStack> getDisplayStacks()
        {
            return Collections.unmodifiableList(this.stacks);
        }
    }

    /**
     * Generic delivery request.
     */
    public static class DeliveryRequest extends AbstractRequest<Delivery> implements IStackBasedTask
    {
        public DeliveryRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestState state, @NotNull final Delivery requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public ImmutableList<ItemStack> getDeliveries()
        {
            //This request type has no deliverable.
            //It is the delivery.
            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            return String.literal("")
                     .append(String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_DELIVERY)
                               .append(String.literal(getRequest().getStack().getCount() + " "))
                               .append(getRequest().getStack().getDisplayName()));
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            return ImmutableList.of();
        }

        @Override
        public String getDisplayPrefix()
        {
            return String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_DELIVERY);
        }

        @Override
        public int getDisplayCount()
        {
            return getRequest().getStack().getCount();
        }

        @Override
        public ItemStack getTaskStack()
        {
            return getRequest().getStack();
        }

        @Override
        public List<String> getResolverToolTip(final IColonyView colony)
        {
            final String requester = getRequester().getRequesterDisplayName(colony.getRequestManager(), this).getString();

            int posInList = -1;
            for (IBuildingView view : colony.getClientBuildingManager().getBuildings().values())
            {
                if (view.getBuildingType() == ModBuildings.deliveryman.get())
                {
                    posInList = getPosInList(colony, view, getId());
                    if (posInList >= 0)
                    {
                        break;
                    }
                }
                else if (view.getBuildingType() == ModBuildings.wareHouse.get())
                {
                    posInList = view.getModuleView(BuildingModules.WAREHOUSE_REQUEST_QUEUE).getTasks().indexOf(getId());
                    if (posInList >= 0)
                    {
                        break;
                    }
                }
            }

            if (posInList >= 0)
            {
            	return posInList == 0 ? ImmutableList.of(String.translatable(FROM, requester), String.translatable(IN_PROGRESS)) : ImmutableList.of(String.translatable(FROM, requester), String.translatable(IN_QUEUE, posInList));
            }
            else
            {
                return ImmutableList.of(String.translatable(FROM, requester));
            }
        }

        @NotNull
        @Override
        public ResourceLocation getDisplayIcon()
        {
            return new ResourceLocation("minecolonies:textures/gui/citizen/delivery.png");
        }
    }

    /**
     * Generic delivery request.
     */
    public static class PickupRequest extends AbstractRequest<Pickup>
    {
        public PickupRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestState state, @NotNull final Pickup requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public ImmutableList<ItemStack> getDeliveries()
        {
            //This request type has no deliverable.
            //It is a pickup.
            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String result = String.literal("");
            result.append(String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_PICKUP));
            return result;
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            return ImmutableList.of();
        }

        @NotNull
        @Override
        public ResourceLocation getDisplayIcon()
        {
            // This can be just the delivery icon. For the user, it's no big deal.
            return new ResourceLocation("minecolonies:textures/gui/citizen/delivery.png");
        }

        @Override
        public List<String> getResolverToolTip(final IColonyView colony)
        {
            final String requester = getRequester().getRequesterDisplayName(colony.getRequestManager(), this).getString();

            int posInList = -1;
            for (IBuildingView view : colony.getClientBuildingManager().getBuildings().values())
            {
                if (view.getBuildingType() == ModBuildings.deliveryman.get())
                {
                    posInList = getPosInList(colony, view, getId());
                    if (posInList >= 0)
                    {
                        break;
                    }
                }
                else if (view.getBuildingType() == ModBuildings.wareHouse.get())
                {
                    posInList = view.getModuleView(BuildingModules.WAREHOUSE_REQUEST_QUEUE).getTasks().indexOf(getId());
                    if (posInList >= 0)
                    {
                        break;
                    }
                }
            }

            if (posInList >= 0)
            {
                return posInList == 0 ? ImmutableList.of(String.translatable(FROM, requester), String.translatable(IN_PROGRESS)) : ImmutableList.of(String.translatable(FROM, requester), String.translatable(IN_QUEUE, posInList));
            }
            else
            {
                return ImmutableList.of(String.translatable(FROM, requester));
            }
        }

    }

    /**
     * An abstract implementation for crafting requests
     */
    public abstract static class AbstractCraftingRequest<C extends AbstractCrafting> extends AbstractRequest<C> implements IStackBasedTask
    {

        protected AbstractCraftingRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final C requested)
        {
            super(requester, token, requested);
        }

        protected AbstractCraftingRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final RequestState state,
          @NotNull final C requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public final String getShortDisplayString()
        {
            return String.translatable(RequestSystemTranslationConstants.REQUEST_SYSTEM_CRAFTING_DISPLAY, String.literal(String.valueOf(getRequest().getMinCount())), getRequest().getStack().getDisplayName());
        }

        @Override
        public String getDisplayPrefix()
        {
            return String.translatable(RequestSystemTranslationConstants.REQUEST_SYSTEM_CRAFTING_DISPLAY_SHORT, String.literal(String.valueOf(getRequest().getMinCount())));
        }

        @Override
        public int getDisplayCount()
        {
            return getRequest().getCount();
        }

        @Override
        public ItemStack getTaskStack()
        {
            return getRequest().getStack();
        }

        protected abstract String getTranslationKey();

        @NotNull
        @Override
        public final List<ItemStack> getDisplayStacks()
        {
            return ImmutableList.of();
        }

        @Override
        public List<String> getResolverToolTip(final IColonyView colony)
        {
            final String requester = getRequester().getRequesterDisplayName(colony.getRequestManager(), this).getString();

            try
            {
                final int[] resolver = colony.getRequestManager().getResolverForRequest(getId()).getLocation().getInDimensionLocation();
                final IBuildingView view = colony.getClientBuildingManager().getBuilding(resolver);

                int posInList = getPosInList(colony, view, getId());
                if (posInList >= 0)
                {
                	return posInList == 0 ? ImmutableList.of(String.translatable(AT, requester), String.translatable(IN_PROGRESS)) : ImmutableList.of(String.translatable(FROM, requester), String.translatable(IN_QUEUE, posInList));
                }
                else if (getState() == RequestState.FOLLOWUP_IN_PROGRESS)
                {
                    return ImmutableList.of(String.translatable(AT, requester), String.translatable(FINISHED));
                }
                else
                {
                    return ImmutableList.of(String.translatable(AT, requester), String.translatable(MISSING_DELIVERIES));
                }
            }
            catch (IllegalArgumentException ex)
            {
                return ImmutableList.of(String.translatable(AT, requester), String.translatable(NOT_RESOLVED));
            }
        }

        @NotNull
        @Override
        public final ResourceLocation getDisplayIcon()
        {
            return new ResourceLocation(getDisplayIconFile());
        }

        protected abstract String getDisplayIconFile();
    }

    /**
     * The crafting request for private crafting of a citizen
     */
    public static class PrivateCraftingRequest extends AbstractCraftingRequest<PrivateCrafting>
    {

        protected PrivateCraftingRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final PrivateCrafting requested)
        {
            super(requester, token, requested);
        }

        protected PrivateCraftingRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final RequestState state, @NotNull final PrivateCrafting requested)
        {
            super(requester, token, state, requested);
        }

        @Override
        protected String getTranslationKey()
        {
            return RequestSystemTranslationConstants.REQUESTS_TYPE_CRAFTING;
        }

        @Override
        protected String getDisplayIconFile()
        {
            return "minecolonies:textures/gui/citizen/crafting_public.png";
        }
    }

    /**
     * The public crafting requests, used for workers that perform crafting
     */
    public static class PublicCraftingRequest extends AbstractCraftingRequest<PublicCrafting>
    {

        protected PublicCraftingRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final PublicCrafting requested)
        {
            super(requester, token, requested);
        }

        protected PublicCraftingRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final RequestState state, @NotNull final PublicCrafting requested)
        {
            super(requester, token, state, requested);
        }

        @Override
        protected String getTranslationKey()
        {
            return RequestSystemTranslationConstants.REQUESTS_TYPE_CRAFTING;
        }

        @Override
        protected String getDisplayIconFile()
        {
            return "minecolonies:textures/gui/citizen/crafting_public.png";
        }
    }

    /**
     * Generic Tool Request.
     */
    public static class ToolRequest extends AbstractRequest<Tool>
    {
        public ToolRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final Tool requested)
        {
            super(requester, token, requested);
        }

        public ToolRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestState state, @NotNull final Tool requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public String getLongDisplayString()
        {
            final String result = String.literal("");
            result.append(getRequest().getEquipmentType().getDisplayName());

            if (getRequest().getMinLevel() > EquipmentLevelConstants.TOOL_LEVEL_HAND)
            {
                result.append(String.literal(" "));
                result.append(String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_TOOL_MINIMUM_LEVEL_PREFIX));
                result.append(String.literal(" "));
                result.append(getRequest().isArmor() ? ItemStackUtils.swapArmorGrade(getRequest().getMinLevel()) : ItemStackUtils.swapToolGrade(getRequest().getMinLevel()));
            }

            if (getRequest().getMaxLevel() < EquipmentLevelConstants.TOOL_LEVEL_MAXIMUM)
            {
                if (getRequest().getMinLevel() > EquipmentLevelConstants.TOOL_LEVEL_HAND)
                {
                    result.append(String.literal(" "));
                    result.append(String.translatable(TranslationConstants.COM_MINECOLONIES_GENERAL_AND));
                }

                result.append(String.literal(" "));
                result.append(String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_TOOL_MAXIMUM_LEVEL_PREFIX));
                result.append(String.literal(" "));
                result.append(getRequest().isArmor() ? ItemStackUtils.swapArmorGrade(getRequest().getMaxLevel()) : ItemStackUtils.swapToolGrade(getRequest().getMaxLevel()));
            }

            return result;
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String result = String.literal("");
            result.append(getRequest().getEquipmentType().getDisplayName());
            return result;
        }
    }

    /**
     * Generic food request.
     */
    public static class FoodRequest extends AbstractRequest<Food>
    {
        /**
         * Food examples to display.
         */
        private static ImmutableList<ItemStack> foodExamples;

        FoodRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final Food requested)
        {
            super(requester, token, requested);
        }

        FoodRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final RequestState state,
          @NotNull final Food requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String result = String.literal("");
            result.append(String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_FOOD));
            return result;
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            if (foodExamples == null)
            {
                foodExamples = ImmutableList.copyOf(IColonyManager.getInstance()
                                                      .getCompatibilityManager()
                                                      .getListOfAllItems()
                                                      .stream()
                                                      .filter(item -> item.isEdible())
                                                      .collect(Collectors.toList()));
            }

            if (!this.getRequest().getExclusionList().isEmpty())
            {
                return ImmutableList.copyOf(foodExamples.stream()
                        .filter(item -> this.getRequest().matches(item))
                        .collect(Collectors.toList()));
            }

            return foodExamples;
        }
    }

    /**
     * Generic smeltable ore request.
     */
    public static class SmeltAbleOreRequest extends AbstractRequest<SmeltableOre>
    {
        /**
         * Ore examples to display.
         */
        private static ImmutableList<ItemStack> oreExamples;

        SmeltAbleOreRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final SmeltableOre requested)
        {
            super(requester, token, requested);
        }

        SmeltAbleOreRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final RequestState state,
          @NotNull final SmeltableOre requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            return String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_SMELTABLE_ORE);
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            if (oreExamples == null)
            {
                oreExamples = ImmutableList.copyOf(IColonyManager.getInstance()
                                                     .getCompatibilityManager()
                                                     .getListOfAllItems()
                                                     .stream()
                                                     .filter(IColonyManager.getInstance().getCompatibilityManager()::isOre)
                                                     .collect(Collectors.toList()));
            }
            return oreExamples;
        }
    }

    /**
     * Generic burnable request.
     */
    public static class BurnableRequest extends AbstractRequest<Burnable>
    {
        /**
         * List of burnable examples.
         */
        private static ImmutableList<ItemStack> burnableExamples;

        BurnableRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final Burnable requested)
        {
            super(requester, token, requested);
        }

        BurnableRequest(
          @NotNull final IRequester requester,
          @NotNull final IToken<?> token,
          @NotNull final RequestState state,
          @NotNull final Burnable requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String result = String.literal("");
            result.append(String.translatable(RequestSystemTranslationConstants.REQUESTS_TYPE_BURNABLE));
            return result;
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            if (burnableExamples == null)
            {
                burnableExamples = ImmutableList.copyOf(IColonyManager.getInstance()
                                                          .getCompatibilityManager()
                                                          .getListOfAllItems()
                                                          .stream()
                                                          .filter(TileEntityFurnace::isFuel)
                                                          .collect(Collectors.toList()));
            }

            return burnableExamples;
        }
    }

    /**
     * Request for a single ItemStack.
     */
    public static class MinStackRequest extends AbstractRequest<MinimumStack>
    {
        public MinStackRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final MinimumStack requested)
        {
            super(requester, token, requested);
        }

        public MinStackRequest(@NotNull final IRequester requester, @NotNull final IToken<?> token, @NotNull final RequestState state, @NotNull final MinimumStack requested)
        {
            super(requester, token, state, requested);
        }

        @NotNull
        @Override
        public String getShortDisplayString()
        {
            final String combined = String.literal("");

            if (getRequest().getMinimumCount() == getRequest().getCount())
            {
                combined.append(String.literal(getRequest().getCount() + " "));
                combined.append(getRequest().getStack().getHoverName());
            }
            else
            {
                combined.append(String.literal(getRequest().getMinimumCount() + "-" + getRequest().getCount() + " "));
                combined.append(getRequest().getStack().getHoverName());
            }

            return combined;
        }

        @NotNull
        @Override
        public List<ItemStack> getDisplayStacks()
        {
            return getRequest().getRequestedItems();
        }
    }

    /**
     * Find the position the request is in the list.
     * @return the position.
     * @param colony the colony.
     * @param view the building view.
     */
    private static int getPosInList(final IColonyView colony, final IBuildingView view, final IToken<?> id)
    {
        if (view == null)
        {
            return 0;
        }

        for (final WorkerBuildingModuleView moduleView : view.getModuleViews(WorkerBuildingModuleView.class))
        {
            for (int worker : moduleView.getAssignedCitizens())
            {
                final ICitizenDataView citizen = colony.getCitizen(worker);
                if (citizen != null)
                {
                    if (citizen.getJobView() instanceof CrafterJobView)
                    {
                        int index = ((CrafterJobView) citizen.getJobView()).getDataStore().getQueue().indexOf(id);
                        if (index >= 0)
                        {
                            return index;
                        }
                    }
                    else if (citizen.getJobView() instanceof DmanJobView)
                    {
                        int index = ((DmanJobView) citizen.getJobView()).getDataStore().getQueue().indexOf(id);
                        if (index >= 0)
                        {
                            return index;
                        }
                    }
                }
            }
        }
        return -1;
    }
}




