package com.minecolonies.core.colony.requestsystem.resolvers.factory;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactory;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.manager.IRequestManager;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.api.util.constant.SerializationIdentifierConstants;
import com.minecolonies.core.colony.requestsystem.resolvers.StandardRetryingRequestResolver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StandardRetryingRequestResolverFactory implements IFactory<IRequestManager, StandardRetryingRequestResolver>
{
    ////// --------------------------- NBTConstants --------------------------- \\\\\\
    private static final String NBT_TOKEN    = "Token";
    private static final String NBT_LOCATION = "Location";
    private static final String NBT_VALUE    = "Value";
    private static final String NBT_TRIES    = "Requests";
    private static final String NBT_DELAYS   = "Delays";
    ////// --------------------------- NBTConstants --------------------------- \\\\\\

    @NotNull
    @Override
    public TypeToken<? extends StandardRetryingRequestResolver> getFactoryOutputType()
    {
        return TypeToken.of(StandardRetryingRequestResolver.class);
    }

    @NotNull
    @Override
    public TypeToken<? extends IRequestManager> getFactoryInputType()
    {
        return TypeToken.of(IRequestManager.class);
    }

    @NotNull
    @Override
    public StandardRetryingRequestResolver getNewInstance(
      @NotNull final IFactoryController factoryController,
      @NotNull final IRequestManager iRequestManager,
      @NotNull final Object... context)
      throws IllegalArgumentException
    {
        if (context.length != 0)
        {
            throw new IllegalArgumentException("Context is not empty.");
        }

        return new StandardRetryingRequestResolver(factoryController, iRequestManager);
    }

    @NotNull
    @Override
    public NBTTagCompound serialize(
      @NotNull final IFactoryController controller, @NotNull final StandardRetryingRequestResolver standardRetryingRequestResolver)
    {
        final NBTTagCompound compound = new NBTTagCompound();

        compound.setTag(NBT_TRIES, standardRetryingRequestResolver.getAssignedRequests().keySet().stream().map(t -> {
            final NBTTagCompound assignmentCompound = new NBTTagCompound();

            assignmentcompound.setTag(NBT_TOKEN, controller.serialize(t));
            assignmentCompound.putInt(NBT_VALUE, standardRetryingRequestResolver.getAssignedRequests().get(t));

            return assignmentCompound;
        }).collect(NBTUtils.toListNBT()));
        compound.setTag(NBT_DELAYS, standardRetryingRequestResolver.getDelays().keySet().stream().map(t -> {
            final NBTTagCompound delayCompound = new NBTTagCompound();

            delaycompound.setTag(NBT_TOKEN, controller.serialize(t));
            delayCompound.putInt(NBT_VALUE, standardRetryingRequestResolver.getDelays().get(t));

            return delayCompound;
        }).collect(NBTUtils.toListNBT()));

        compound.setTag(NBT_TOKEN, controller.serialize(standardRetryingRequestResolver.getId()));
        compound.setTag(NBT_LOCATION, controller.serialize(standardRetryingRequestResolver.getLocation()));

        return compound;
    }

    @NotNull
    @Override
    public StandardRetryingRequestResolver deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt)
    {
        final Map<IToken<?>, Integer> assignments = NBTUtils.streamCompound(nbt.getTagList(NBT_TRIES, NBTBase.TAG_COMPOUND)).map(assignmentCompound -> {
            IToken<?> token = controller.deserialize(assignmentCompound.getCompoundTag(NBT_TOKEN));
            Integer tries = assignmentCompound.getInt(NBT_VALUE);

            return new HashMap.SimpleEntry<>(token, tries);
        }).collect(Collectors.toMap(HashMap.SimpleEntry::getKey, HashMap.SimpleEntry::getValue));

        final Map<IToken<?>, Integer> delays = NBTUtils.streamCompound(nbt.getTagList(NBT_DELAYS, NBTBase.TAG_COMPOUND)).map(assignmentCompound -> {
            IToken<?> token = controller.deserialize(assignmentCompound.getCompoundTag(NBT_TOKEN));
            Integer tries = assignmentCompound.getInt(NBT_VALUE);

            return new HashMap.SimpleEntry<>(token, tries);
        }).collect(Collectors.toMap(HashMap.SimpleEntry::getKey, HashMap.SimpleEntry::getValue));

        final IToken<?> token = controller.deserialize(nbt.getCompoundTag(NBT_TOKEN));
        final ILocation location = controller.deserialize(nbt.getCompoundTag(NBT_LOCATION));

        final StandardRetryingRequestResolver retryingRequestResolver = new StandardRetryingRequestResolver(token, location);
        retryingRequestResolver.updateData(assignments, delays);
        return retryingRequestResolver;
    }

    @Override
    public void serialize(IFactoryController controller, StandardRetryingRequestResolver input, PacketBuffer packetBuffer)
    {
        packetBuffer.writeInt(input.getAssignedRequests().size());
        input.getAssignedRequests().forEach((key, value) -> {
            controller.serialize(packetBuffer, key);
            packetBuffer.writeInt(value);
        });

        packetBuffer.writeInt(input.getDelays().size());
        input.getDelays().forEach((key, value) -> {
            controller.serialize(packetBuffer, key);
            packetBuffer.writeInt(value);
        });

        controller.serialize(packetBuffer, input.getId());
        controller.serialize(packetBuffer, input.getLocation());
    }

    @Override
    public StandardRetryingRequestResolver deserialize(IFactoryController controller, PacketBuffer buffer) throws Throwable
    {
        final Map<IToken<?>, Integer> requests = new HashMap<>();
        final int requestsSize = buffer.readInt();
        for (int i = 0; i < requestsSize; ++i)
        {
            requests.put(controller.deserialize(buffer), buffer.readInt());
        }

        final Map<IToken<?>, Integer> delays = new HashMap<>();
        final int delaysSize = buffer.readInt();
        for (int i = 0; i < delaysSize; ++i)
        {
            delays.put(controller.deserialize(buffer), buffer.readInt());
        }

        final IToken<?> token = controller.deserialize(buffer);
        final ILocation location = controller.deserialize(buffer);

        final StandardRetryingRequestResolver resolver = new StandardRetryingRequestResolver(token, location);
        resolver.updateData(requests, delays);
        return resolver;
    }

    @Override
    public short getSerializationId()
    {
        return SerializationIdentifierConstants.STANDARD_RETRYING_REQUEST_RESOLVER_ID;
    }
}




