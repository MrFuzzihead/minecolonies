package com.minecolonies.core.network;
import net.minecraft.world.entity.player.Player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.crafting.CustomRecipeManagerMessage;
import com.minecolonies.core.debug.messages.DebugEnableMessage;
import com.minecolonies.core.debug.messages.DebugEnablePathfindingMessage;
import com.minecolonies.core.debug.messages.DebugOutputMessage;
import com.minecolonies.core.debug.messages.QueryCitizenAIHistoryMessage;
import com.minecolonies.core.network.messages.PermissionsMessage;
import com.minecolonies.core.network.messages.client.*;
import com.minecolonies.core.network.messages.client.colony.*;
import com.minecolonies.core.network.messages.server.*;
import com.minecolonies.core.network.messages.server.colony.*;
import com.minecolonies.core.network.messages.server.colony.building.*;
import com.minecolonies.core.network.messages.server.colony.building.builder.BuilderSelectWorkOrderMessage;
import com.minecolonies.core.network.messages.server.colony.building.enchanter.EnchanterWorkerSetMessage;
import com.minecolonies.core.network.messages.server.colony.building.fields.*;
import com.minecolonies.core.network.messages.server.colony.building.guard.GuardSetMinePosMessage;
import com.minecolonies.core.network.messages.server.colony.building.home.AssignUnassignMessage;
import com.minecolonies.core.network.messages.server.colony.building.miner.MinerRepairLevelMessage;
import com.minecolonies.core.network.messages.server.colony.building.miner.MinerSetLevelMessage;
import com.minecolonies.core.network.messages.server.colony.building.postbox.PostBoxRequestMessage;
import com.minecolonies.core.network.messages.server.colony.building.university.TryResearchMessage;
import com.minecolonies.core.network.messages.server.colony.building.warehouse.SortBuildingMessage;
import com.minecolonies.core.network.messages.server.colony.building.warehouse.UpgradeWarehouseMessage;
import com.minecolonies.core.network.messages.server.colony.building.worker.*;
import com.minecolonies.core.network.messages.server.colony.citizen.*;
import com.minecolonies.core.network.messages.splitting.SplitPacketMessage;
import com.minecolonies.core.research.GlobalResearchTreeMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Our wrapper for Forge 1.7.10 network layer (SimpleNetworkWrapper).
 */
public class NetworkChannel
{
    /**
     * Forge 1.7.10 network channel
     */
    private final SimpleNetworkWrapper rawChannel;

    /**
     * The messages that this channel can process, as viewed from a message id.
     */
    private final Map<Integer, NetworkingMessageEntry<?>> messagesTypes = Maps.newHashMap();

    /**
     * The message that this channel can process, as viewed from a message type.
     */
    private final Map<Class<? extends IMessage>, Integer> messageTypeToIdMap = Maps.newHashMap();

    /**
     * Cache of partially received messages, this holds the data until it is processed.
     */
    private final Cache<Integer, Map<Integer, byte[]>> messageCache = CacheBuilder.newBuilder()
                                                                        .expireAfterAccess(1, TimeUnit.MINUTES)
                                                                        .concurrencyLevel(8)
                                                                        .build();

    /**
     * An atomic counter which keeps track of the split messages that have been sent.
     */
    private final AtomicInteger messageCounter = new AtomicInteger();

    /**
     * Creates a new instance of network channel.
     *
     * @param channelName unique channel name
     */
    public NetworkChannel(final String channelName)
    {
        rawChannel = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID + ":" + channelName);
    }

    /**
     * Registers all common messages.
     */
    public void registerCommonMessages()
    {
        setupInternalMessages();

        int idx = 1;

        //  ColonyView messages
        registerMessage(++idx, ColonyViewMessage.class, ColonyViewMessage::new);
        registerMessage(++idx, ColonyViewCitizenViewMessage.class, ColonyViewCitizenViewMessage::new);
        registerMessage(++idx, ColonyViewRemoveCitizenMessage.class, ColonyViewRemoveCitizenMessage::new);
        registerMessage(++idx, ColonyViewBuildingViewMessage.class, ColonyViewBuildingViewMessage::new);
        registerMessage(++idx, ColonyViewRemoveBuildingMessage.class, ColonyViewRemoveBuildingMessage::new);
        registerMessage(++idx, ColonyViewBuildingExtensionsUpdateMessage.class, ColonyViewBuildingExtensionsUpdateMessage::new);
        registerMessage(++idx, PermissionsMessage.View.class, PermissionsMessage.View::new);
        registerMessage(++idx, ColonyViewWorkOrderMessage.class, ColonyViewWorkOrderMessage::new);
        registerMessage(++idx, ColonyViewRemoveWorkOrderMessage.class, ColonyViewRemoveWorkOrderMessage::new);
        registerMessage(++idx, UpdateChunkCapabilityMessage.class, UpdateChunkCapabilityMessage::new);
        registerMessage(++idx, ColonyViewResearchManagerViewMessage.class, ColonyViewResearchManagerViewMessage::new);

        //  Permission Request messages
        registerMessage(++idx, PermissionsMessage.Permission.class, PermissionsMessage.Permission::new);
        registerMessage(++idx, PermissionsMessage.AddPlayer.class, PermissionsMessage.AddPlayer::new);
        registerMessage(++idx, PermissionsMessage.RemovePlayer.class, PermissionsMessage.RemovePlayer::new);
        registerMessage(++idx, PermissionsMessage.ChangePlayerRank.class, PermissionsMessage.ChangePlayerRank::new);
        registerMessage(++idx, PermissionsMessage.AddPlayerOrFakePlayer.class, PermissionsMessage.AddPlayerOrFakePlayer::new);
        registerMessage(++idx, PermissionsMessage.AddRank.class, PermissionsMessage.AddRank::new);
        registerMessage(++idx, PermissionsMessage.RemoveRank.class, PermissionsMessage.RemoveRank::new);
        registerMessage(++idx, PermissionsMessage.EditRankType.class, PermissionsMessage.EditRankType::new);

        //  Colony Request messages
        registerMessage(++idx, BuildRequestMessage.class, BuildRequestMessage::new);
        registerMessage(++idx, OpenInventoryMessage.class, OpenInventoryMessage::new);
        registerMessage(++idx, TownHallRenameMessage.class, TownHallRenameMessage::new);
        registerMessage(++idx, MinerSetLevelMessage.class, MinerSetLevelMessage::new);
        registerMessage(++idx, RecallCitizenMessage.class, RecallCitizenMessage::new);
        registerMessage(++idx, HireFireMessage.class, HireFireMessage::new);
        registerMessage(++idx, WorkOrderChangeMessage.class, WorkOrderChangeMessage::new);
        registerMessage(++idx, AssignFieldMessage.class, AssignFieldMessage::new);
        registerMessage(++idx, AssignmentModeMessage.class, AssignmentModeMessage::new);
        registerMessage(++idx, GuardSetMinePosMessage.class, GuardSetMinePosMessage::new);
        registerMessage(++idx, RecallCitizenHutMessage.class, RecallCitizenHutMessage::new);
        registerMessage(++idx, TransferItemsRequestMessage.class, TransferItemsRequestMessage::new);
        registerMessage(++idx, MarkBuildingDirtyMessage.class, MarkBuildingDirtyMessage::new);
        registerMessage(++idx, ChangeFreeToInteractBlockMessage.class, ChangeFreeToInteractBlockMessage::new);
        registerMessage(++idx, CreateColonyMessage.class, CreateColonyMessage::new);
        registerMessage(++idx, ColonyDeleteOwnMessage.class, ColonyDeleteOwnMessage::new);
        registerMessage(++idx, ColonyViewRemoveMessage.class, ColonyViewRemoveMessage::new);
        registerMessage(++idx, GiveToolMessage.class, GiveToolMessage::new);
        registerMessage(++idx, ColonyAbandonOwnMessage.class, ColonyAbandonOwnMessage::new);
        registerMessage(++idx, TriggerConnectionEventMessage.class, TriggerConnectionEventMessage::new);

        registerMessage(++idx, AssignUnassignMessage.class, AssignUnassignMessage::new);
        registerMessage(++idx, OpenCraftingGUIMessage.class, OpenCraftingGUIMessage::new);
        registerMessage(++idx, AddRemoveRecipeMessage.class, AddRemoveRecipeMessage::new);
        registerMessage(++idx, ChangeRecipePriorityMessage.class, ChangeRecipePriorityMessage::new);
        registerMessage(++idx, ChangeDeliveryPriorityMessage.class, ChangeDeliveryPriorityMessage::new);
        registerMessage(++idx, ForcePickupMessage.class, ForcePickupMessage::new);
        registerMessage(++idx, UpgradeWarehouseMessage.class, UpgradeWarehouseMessage::new);
        registerMessage(++idx, TransferItemsToCitizenRequestMessage.class, TransferItemsToCitizenRequestMessage::new);
        registerMessage(++idx, UpdateRequestStateMessage.class, UpdateRequestStateMessage::new);
        registerMessage(++idx, BuildingSetStyleMessage.class, BuildingSetStyleMessage::new);
        registerMessage(++idx, RecallSingleCitizenMessage.class, RecallSingleCitizenMessage::new);
        registerMessage(++idx, AssignFilterableItemMessage.class, AssignFilterableItemMessage::new);
        registerMessage(++idx, TeamColonyColorChangeMessage.class, TeamColonyColorChangeMessage::new);
        registerMessage(++idx, ColonyFlagChangeMessage.class, ColonyFlagChangeMessage::new);
        registerMessage(++idx, ColonyStructureStyleMessage.class, ColonyStructureStyleMessage::new);
        registerMessage(++idx, PauseCitizenMessage.class, PauseCitizenMessage::new);
        registerMessage(++idx, RestartCitizenMessage.class, RestartCitizenMessage::new);
        registerMessage(++idx, SortBuildingMessage.class, SortBuildingMessage::new);
        registerMessage(++idx, PostBoxRequestMessage.class, PostBoxRequestMessage::new);
        registerMessage(++idx, HireMercenaryMessage.class, HireMercenaryMessage::new);
        registerMessage(++idx, HutRenameMessage.class, HutRenameMessage::new);
        registerMessage(++idx, BuildingHiringModeMessage.class, BuildingHiringModeMessage::new);
        registerMessage(++idx, DecorationBuildRequestMessage.class, DecorationBuildRequestMessage::new);
        registerMessage(++idx, DirectPlaceMessage.class, DirectPlaceMessage::new);
        registerMessage(++idx, TeleportToColonyMessage.class, TeleportToColonyMessage::new);
        registerMessage(++idx, EnchanterWorkerSetMessage.class, EnchanterWorkerSetMessage::new);
        registerMessage(++idx, InteractionResponse.class, InteractionResponse::new);
        registerMessage(++idx, TryResearchMessage.class, TryResearchMessage::new);
        registerMessage(++idx, HireSpiesMessage.class, HireSpiesMessage::new);
        registerMessage(++idx, AddMinimumStockToBuildingModuleMessage.class, AddMinimumStockToBuildingModuleMessage::new);
        registerMessage(++idx, RemoveMinimumStockFromBuildingModuleMessage.class, RemoveMinimumStockFromBuildingModuleMessage::new);
        registerMessage(++idx, FarmFieldPlotResizeMessage.class, FarmFieldPlotResizeMessage::new);
        registerMessage(++idx, FarmFieldRegistrationMessage.class, FarmFieldRegistrationMessage::new);
        registerMessage(++idx, FarmFieldUpdateSeedMessage.class, FarmFieldUpdateSeedMessage::new);
        registerMessage(++idx, AdjustSkillCitizenMessage.class, AdjustSkillCitizenMessage::new);
        registerMessage(++idx, BuilderSelectWorkOrderMessage.class, BuilderSelectWorkOrderMessage::new);
        registerMessage(++idx, TriggerSettingMessage.class, TriggerSettingMessage::new);
        registerMessage(++idx, AssignFilterableEntityMessage.class, AssignFilterableEntityMessage::new);
        registerMessage(++idx, BuildPickUpMessage.class, BuildPickUpMessage::new);
        registerMessage(++idx, SwitchBuildingWithToolMessage.class, SwitchBuildingWithToolMessage::new);
        registerMessage(++idx, ColonyTextureStyleMessage.class, ColonyTextureStyleMessage::new);
        registerMessage(++idx, MinerRepairLevelMessage.class, MinerRepairLevelMessage::new);
        registerMessage(++idx, PlantationFieldBuildRequestMessage.class, PlantationFieldBuildRequestMessage::new);
        registerMessage(++idx, ResetFilterableItemMessage.class, ResetFilterableItemMessage::new);
        registerMessage(++idx, CourierHiringModeMessage.class, CourierHiringModeMessage::new);
        registerMessage(++idx, QuarryHiringModeMessage.class, QuarryHiringModeMessage::new);
        registerMessage(++idx, ToggleRecipeMessage.class, ToggleRecipeMessage::new);
        registerMessage(++idx, ColonyNameStyleMessage.class, ColonyNameStyleMessage::new);
        registerMessage(++idx, InteractionClose.class, InteractionClose::new);
        registerMessage(++idx, GetColonyInfoMessage.class, GetColonyInfoMessage::new);
        registerMessage(++idx, PickupBlockMessage.class, PickupBlockMessage::new);
        registerMessage(++idx, MarkStoryReadOnItemMessage.class, MarkStoryReadOnItemMessage::new);
        registerMessage(++idx, AlterRestaurantMenuItemMessage.class, AlterRestaurantMenuItemMessage::new);

        // Client side only
        registerMessage(++idx, BlockParticleEffectMessage.class, BlockParticleEffectMessage::new);
        registerMessage(++idx, CompostParticleMessage.class, CompostParticleMessage::new);
        registerMessage(++idx, ItemParticleEffectMessage.class, ItemParticleEffectMessage::new);
        registerMessage(++idx, LocalizedParticleEffectMessage.class, LocalizedParticleEffectMessage::new);
        registerMessage(++idx, UpdateChunkRangeCapabilityMessage.class, UpdateChunkRangeCapabilityMessage::new);
        registerMessage(++idx, OpenSuggestionWindowMessage.class, OpenSuggestionWindowMessage::new);
        registerMessage(++idx, UpdateClientWithCompatibilityMessage.class, UpdateClientWithCompatibilityMessage::new);
        registerMessage(++idx, CircleParticleEffectMessage.class, CircleParticleEffectMessage::new);
        registerMessage(++idx, StreamParticleEffectMessage.class, StreamParticleEffectMessage::new);
        registerMessage(++idx, SleepingParticleMessage.class, SleepingParticleMessage::new);
        registerMessage(++idx, VanillaParticleMessage.class, VanillaParticleMessage::new);
        registerMessage(++idx, StopMusicMessage.class, StopMusicMessage::new);
        registerMessage(++idx, PlayAudioMessage.class, PlayAudioMessage::new);
        registerMessage(++idx, PlayMusicAtPosMessage.class, PlayMusicAtPosMessage::new);
        registerMessage(++idx, ColonyVisitorViewDataMessage.class, ColonyVisitorViewDataMessage::new);
        registerMessage(++idx, ColonyViewAnimalViewDataMessage.class, ColonyViewAnimalViewDataMessage::new);
        registerMessage(++idx, SyncPathMessage.class, SyncPathMessage::new);
        registerMessage(++idx, SyncPathReachedMessage.class, SyncPathReachedMessage::new);
        registerMessage(++idx, ReactivateBuildingMessage.class, ReactivateBuildingMessage::new);
        registerMessage(++idx, PlaySoundForCitizenMessage.class, PlaySoundForCitizenMessage::new);
        registerMessage(++idx, OpenDecoBuildWindowMessage.class, OpenDecoBuildWindowMessage::new);
        registerMessage(++idx, OpenPlantationFieldBuildWindowMessage.class, OpenPlantationFieldBuildWindowMessage::new);
        registerMessage(++idx, SaveStructureNBTMessage.class, SaveStructureNBTMessage::new);
        registerMessage(++idx, GlobalQuestSyncMessage.class, GlobalQuestSyncMessage::new);
        registerMessage(++idx, GlobalDiseaseSyncMessage.class, GlobalDiseaseSyncMessage::new);
        registerMessage(++idx, OpenColonyFoundingCovenantMessage.class, OpenColonyFoundingCovenantMessage::new);
        registerMessage(++idx, OpenBuildingUIMessage.class, OpenBuildingUIMessage::new);
        registerMessage(++idx, OpenCantFoundColonyWarningMessage.class, OpenCantFoundColonyWarningMessage::new);
        registerMessage(++idx, OpenDeleteAbandonColonyMessage.class, OpenDeleteAbandonColonyMessage::new);
        registerMessage(++idx, OpenReactivateColonyMessage.class, OpenReactivateColonyMessage::new);

        // JEI Messages
        registerMessage(++idx, TransferRecipeCraftingTeachingMessage.class, TransferRecipeCraftingTeachingMessage::new);

        // Advancement Messages
        registerMessage(++idx, OpenGuiWindowTriggerMessage.class, OpenGuiWindowTriggerMessage::new);
        registerMessage(++idx, ClickGuiButtonTriggerMessage.class, ClickGuiButtonTriggerMessage::new);

        // Colony-Independent items
        registerMessage(++idx, RemoveFromRallyingListMessage.class, RemoveFromRallyingListMessage::new);
        registerMessage(++idx, ToggleBannerRallyGuardsMessage.class, ToggleBannerRallyGuardsMessage::new);

        // Research-related messages
        registerMessage(++idx, GlobalResearchTreeMessage.class, GlobalResearchTreeMessage::new);

        // Crafter Recipe-related messages
        registerMessage(++idx, CustomRecipeManagerMessage.class, CustomRecipeManagerMessage::new);

        registerMessage(++idx, ColonyListMessage.class, ColonyListMessage::new);

        // Resource scroll NBT share message
        registerMessage(++idx, ResourceScrollSaveWarehouseSnapshotMessage.class, ResourceScrollSaveWarehouseSnapshotMessage::new);

        // Crafting GUI
        registerMessage(++idx, SwitchRecipeCraftingTeachingMessage.class, SwitchRecipeCraftingTeachingMessage::new);

        // Assistant block place request
        registerMessage(++idx, PlayerAssistantBuildRequestMessage.class, PlayerAssistantBuildRequestMessage::new);

        // Debug messages
        registerMessage(++idx, QueryCitizenAIHistoryMessage.class, QueryCitizenAIHistoryMessage::new);
        registerMessage(++idx, DebugEnablePathfindingMessage.class, DebugEnablePathfindingMessage::new);
        registerMessage(++idx, DebugOutputMessage.class, DebugOutputMessage::new);
        registerMessage(++idx, DebugEnableMessage.class, DebugEnableMessage::new);

        // Item Setting Messages
        registerMessage(++idx, ItemSettingMessage.class, ItemSettingMessage::new);
    }

    /**
     * Registers the internal SplitPacketMessage on both sides (id = 0).
     */
    private void setupInternalMessages()
    {
        // [1.7.10] register on both sides with the universal dispatch handler
        rawChannel.registerMessage(UniversalMessageHandler.class, SplitPacketMessage.class, 0, Side.CLIENT);
        rawChannel.registerMessage(UniversalMessageHandler.class, SplitPacketMessage.class, 0, Side.SERVER);
        this.messagesTypes.put(0, new NetworkingMessageEntry<>(SplitPacketMessage::new));
        this.messageTypeToIdMap.put(SplitPacketMessage.class, 0);
    }

    /**
     * Register a message into rawChannel.
     *
     * @param <MSG>      message class type
     * @param id         network id
     * @param msgClazz   message class
     * @param msgCreator supplier with new instance of msgClazz
     */
    @SuppressWarnings("unchecked")
    private <MSG extends IMessage> void registerMessage(final int id, final Class<MSG> msgClazz, final Supplier<MSG> msgCreator)
    {
        this.messagesTypes.put(id, new NetworkingMessageEntry<>(msgCreator));
        this.messageTypeToIdMap.put(msgClazz, id);
        // [1.7.10] register on both sides
        rawChannel.registerMessage(UniversalMessageHandler.class, msgClazz, id, Side.CLIENT);
        rawChannel.registerMessage(UniversalMessageHandler.class, msgClazz, id, Side.SERVER);
    }

    /**
     * Sends to server.
     *
     * @param msg message to send
     */
    public void sendToServer(final IMessage msg)
    {
        handleSplitting(msg, rawChannel::sendToServer);
    }

    /**
     * Sends to player.
     *
     * @param msg    message to send
     * @param player target player
     */
    public void sendToPlayer(final IMessage msg, final EntityPlayerMP player)
    {
        handleSplitting(msg, s -> rawChannel.sendTo(s, player.playerNetServerHandler));
    }

    /**
     * Sends to everyone in dimension.
     *
     * @param msg message to send
     * @param dim target dimension id
     */
    public void sendToDimension(final IMessage msg, final int dim)
    {
        handleSplitting(msg, s -> rawChannel.sendToDimension(s, dim));
    }

    /**
     * Sends to everyone in circle made using given target point.
     *
     * @param msg message to send
     * @param pos target position and radius
     */
    public void sendToPosition(final IMessage msg, final NetworkRegistry.TargetPoint pos)
    {
        handleSplitting(msg, s -> rawChannel.sendToAllAround(s, pos));
    }

    /**
     * Sends to everyone.
     *
     * @param msg message to send
     */
    public void sendToEveryone(final IMessage msg)
    {
        handleSplitting(msg, rawChannel::sendToAll);
    }

    /**
     * Sends to everyone tracking the given entity.
     * [1.7.10] Uses sendToAllTracking (if available) or falls back to sendToDimension.
     *
     * @param msg    message to send
     * @param entity target entity to look at
     */
    public void sendToTrackingEntity(final IMessage msg, final Entity entity)
    {
        // [1.7.10] sendToAllTracking is available via NetworkRegistry
        handleSplitting(msg, s -> rawChannel.sendToAllTracking(s, entity));
    }

    /**
     * Sends to everyone (including given entity) who is tracking the entity.
     *
     * @param msg    message to send
     * @param entity target entity
     */
    public void sendToTrackingEntityAndSelf(final IMessage msg, final Entity entity)
    {
        // [1.7.10] Best approximation: send to tracking + direct send to player if player
        handleSplitting(msg, s -> {
            rawChannel.sendToAllTracking(s, entity);
            if (entity instanceof EntityPlayerMP)
            {
                rawChannel.sendTo(s, ((EntityPlayerMP) entity).playerNetServerHandler);
            }
        });
    }

    /**
     * Sends to everyone tracking a given chunk.
     *
     * @param msg   message to send
     * @param chunk target chunk
     */
    public void sendToTrackingChunk(final IMessage msg, final Chunk chunk)
    {
        // [1.7.10] Use sendToAllTracking chunk variant
        handleSplitting(msg, s -> rawChannel.sendToAllTracking(s, chunk));
    }

    /**
     * Method that handles the splitting of the message into chunks if need be.
     *
     * @param msg                  The message to split in question.
     * @param splitMessageConsumer The consumer that sends away the split parts.
     */
    private void handleSplitting(final IMessage msg, final Consumer<IMessage> splitMessageConsumer)
    {
        final int messageId = this.messageTypeToIdMap.getOrDefault(msg.getClass(), -1);
        if (messageId == -1)
        {
            throw new IllegalArgumentException("The message is unknown to this channel: " + msg.getClass().getName());
        }

        final ByteBuf buffer = Unpooled.buffer();
        final PacketBuffer innerBuf = new PacketBuffer(buffer);
        msg.toBytes(innerBuf);
        final byte[] data = buffer.array();
        buffer.release();

        final int max_packet_size = 943718; // 90% of max packet size
        int currentIndex = 0;
        int packetIndex = 0;
        final int comId = messageCounter.getAndIncrement();

        while (currentIndex < data.length)
        {
            this.getMessagesTypes().get(messageId).onSplitting(packetIndex);

            final int extra = Math.min(max_packet_size, data.length - currentIndex);
            final byte[] subPacketData = Arrays.copyOfRange(data, currentIndex, currentIndex + extra);
            final SplitPacketMessage splitPacketMessage = new SplitPacketMessage(comId, packetIndex++, (currentIndex + extra) >= data.length, messageId, subPacketData);

            splitMessageConsumer.accept(splitPacketMessage);
            currentIndex += extra;
        }
    }

    /**
     * Gives access to the cache of messages that are being received.
     *
     * @return The message cache.
     */
    public Cache<Integer, Map<Integer, byte[]>> getMessageCache()
    {
        return messageCache;
    }

    /**
     * Gives access to the internal index codec.
     *
     * @return The internal index codec map.
     */
    public Map<Integer, NetworkingMessageEntry<?>> getMessagesTypes()
    {
        return messagesTypes;
    }

    /**
     * Universal message handler that dispatches to IMessage.onExecute().
     * [1.7.10] All registered messages share this handler.
     */
    public static final class UniversalMessageHandler implements IMessageHandler<IMessage, cpw.mods.fml.common.network.simpleimpl.IMessage>
    {
        @Override
        public cpw.mods.fml.common.network.simpleimpl.IMessage onMessage(final IMessage message, final MessageContext ctx)
        {
            final boolean isLogicalServer = ctx.side.isServer();
            message.onExecute(ctx, isLogicalServer);
            return null;
        }
    }

    /**
     * A class that handles the data wrapping for our inner index codec.
     *
     * @param <MSG> The message type.
     */
    public static final class NetworkingMessageEntry<MSG extends IMessage>
    {
        private final AtomicBoolean hasWarned = new AtomicBoolean(true);
        private final Supplier<MSG> creator;

        private NetworkingMessageEntry(final Supplier<MSG> creator) { this.creator = creator; }

        public Supplier<MSG> getCreator() { return creator; }

        public void onSplitting(int packetIndex)
        {
            if (packetIndex != 1)
            {
                return;
            }
            if (hasWarned.getAndSet(false))
            {
                Log.getLogger().warn("Splitting message: " + creator.get().getClass() + " it is too big to send normally. This message is only printed once");
            }
        }
    }
}

