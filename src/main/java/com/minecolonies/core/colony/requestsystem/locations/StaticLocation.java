package com.minecolonies.core.colony.requestsystem.locations;

import com.google.common.reflect.TypeToken;
import com.minecolonies.api.colony.requestsystem.factory.IFactoryController;
import com.minecolonies.api.colony.requestsystem.location.ILocation;
import com.minecolonies.api.colony.requestsystem.location.ILocationFactory;
import com.minecolonies.api.util.constant.SerializationIdentifierConstants;
// [1.7.10] Registries removed
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Location described by an immutable int[] and a dimension.
 */
public class StaticLocation implements ILocation
{

    private static final int NUMBER_OR_CONTEXTS = 1;

    @NotNull
    private final int[] pos;

    private final int /* ResourceKey */ dimension;

    public StaticLocation(@NotNull final int[] pos, final int /* ResourceKey */ dimension)
    {
        this.pos = pos;
        this.dimension = dimension;
    }

    /**
     * Method to get the location in the dimension
     *
     * @return The location.
     */
    @NotNull
    @Override
    public int[] getInDimensionLocation()
    {
        return pos;
    }

    /**
     * Method to get the dimension of the location.
     *
     * @return The dimension of the location.
     */
    @NotNull
    @Override
    public int /* ResourceKey */ getDimension()
    {
        return dimension;
    }

    /**
     * Method to check if this location is reachable from the other.
     *
     * @param location The check if it is reachable from here.
     * @return True when reachable, false when not.
     */
    @Override
    public boolean isReachableFromLocation(@NotNull final ILocation location)
    {
        return location.getDimension() == getDimension();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof StaticLocation))
        {
            return false;
        }

        final StaticLocation that = (StaticLocation) o;

        if (getDimension() != that.getDimension())
        {
            return false;
        }
        return pos.equals(that.pos);
    }

    @Override
    public int hashCode()
    {
        int result = pos.hashCode();
        result = 31 * result + getDimension().location().toString().hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Dim: " + dimension.location() + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " ";
    }

    /**
     * Internal factory class.
     */
    @SuppressWarnings("squid:S2972")
    /**
     * We have this class the way it is for a reason.
     */
    public static class Factory implements ILocationFactory<int[], StaticLocation>
    {

        ////// --------------------------- NBTConstants --------------------------- \\\\\\
        private static final String NBT_POS = "Pos";
        private static final String NBT_DIM = "Dim";
        ////// --------------------------- NBTConstants --------------------------- \\\\\\

        @NotNull
        @Override
        @SuppressWarnings("squid:LeftCurlyBraceStartLineCheck")
        /**
         * Moving the curly braces really makes the code hard to read.
         */
        public TypeToken<StaticLocation> getFactoryOutputType()
        {
            return TypeToken.of(StaticLocation.class);
        }

        @NotNull
        @Override
        @SuppressWarnings("squid:LeftCurlyBraceStartLineCheck")
        /**
         * Moving the curly braces really makes the code hard to read.
         */
        public TypeToken<int[]> getFactoryInputType()
        {
            return TypeToken.of(int[].class);
        }

        /**
         * Method to serialize a given constructable.
         *
         * @param controller The controller that can be used to serialize complicated types.
         * @param request    The request to serialize.
         * @return The serialized data of the given requets.
         */
        @NotNull
        @Override
        public NBTTagCompound serialize(@NotNull final IFactoryController controller, @NotNull final StaticLocation request)
        {
            final NBTTagCompound compound = new NBTTagCompound();
            compound.putLong(NBT_POS, request.getInDimensionLocation().asLong());
            compound.putString(NBT_DIM, request.getDimension().location().toString());
            return compound;
        }

        /**
         * Method to deserialize a given constructable.
         *
         * @param controller The controller that can be used to deserialize complicated types.
         * @param nbt        The data of the request that should be deserialized.
         * @return The request that corresponds with the given data in the nbt
         */
        @NotNull
        @Override
        public StaticLocation deserialize(@NotNull final IFactoryController controller, @NotNull final NBTTagCompound nbt)
        {
            // [1.7.10] Decode packed long (x<<38 | z<<12 | y) — same as BlockPos.of()
            final long packed = nbt.getLong(NBT_POS);
            final int[] pos = new int[]{(int)(packed >> 38), (int)(packed << 52 >> 52), (int)(packed << 26 >> 38)};
            final String dim = nbt.getString(NBT_DIM);
            return new StaticLocation(pos, ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dim)));
        }

        @NotNull
        @Override
        public StaticLocation getNewInstance(@NotNull final IFactoryController factoryController, @NotNull final int[] blockPos, @NotNull final Object... context)
        {
            if (context.length != NUMBER_OR_CONTEXTS)
            {
                throw new IllegalArgumentException("Unsupported context - Not the correct amount available. Needed is 1!");
            }

            if (!(context[0] instanceof ResourceKey))
            {
                throw new IllegalArgumentException("Unsupported context - First context object is not a ResourceLocation. Provide an ResourceLocation as Dimension.");
            }

            return new StaticLocation(blockPos, (int /* ResourceKey */) context[0]);
        }

        /**
         * Method to get a new instance of a location given the input. Method not used in this factory.
         *
         * @param input The input to build a new location for.
         * @return The new output instance for a given input.
         */
        @NotNull
        @Override
        public StaticLocation getNewInstance(@NotNull final IFactoryController factoryController, @NotNull final int[] input)
        {
            return new StaticLocation(input, World.OVERWORLD);
        }

        @Override
        public void serialize(@NotNull IFactoryController controller, @NotNull StaticLocation input, PacketBuffer packetBuffer)
        {
            StaticLocation.serialize(packetBuffer, input);
        }

        @NotNull
        @Override
        public StaticLocation deserialize(@NotNull IFactoryController controller, @NotNull PacketBuffer buffer) throws Throwable
        {
            return StaticLocation.deserialize(buffer);
        }

        @Override
        public short getSerializationId()
        {
            return SerializationIdentifierConstants.STATIC_LOCATION_ID;
        }
    }

    /**
     * Serialize this location to the given {@link PacketBuffer}.
     *
     * @param buffer the buffer to serialize this location to.
     */
    public static void serialize(PacketBuffer buffer, StaticLocation location)
    {
        buffer.writeBlockPos(location.pos);
        buffer.writeUtf(location.dimension.location().toString());
    }

    /**
     * Deserialize the location from the given {@link PacketBuffer}
     *
     * @param buffer the buffer to read.
     * @return the deserialized location.
     */
    public static StaticLocation deserialize(PacketBuffer buffer)
    {
        final int[] pos = buffer.readBlockPos();
        final ResourceLocation dimension = new ResourceLocation(buffer.readUtf(32767));

        return new StaticLocation(pos, ResourceKey.create(Registries.DIMENSION, dimension));
    }
}






