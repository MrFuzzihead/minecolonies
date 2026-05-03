package com.minecolonies.core.commands.arguments;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.util.constant.translation.CommandTranslationConstants;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A command argument that can dynamically resolve to a colony id.
 */
public class ColonyIdArgument extends MultipleOptionsArgument<Integer>
{
    private static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(String.translatable("argument.player.unknown"));
    private static final SimpleCommandExceptionType ERROR_UNKNOWN_COLONY =
        new SimpleCommandExceptionType(String.translatable("com.minecolonies.command.argument.colony.unknown"));

    private ColonyIdArgument()
    {
        super(List.of(new HereOption(), new MineOption(), new ColonyIdOption(), new PlayerUuidOption(), new PlayerNameOption()));
    }

    public static ColonyIdArgument id()
    {
        return new ColonyIdArgument();
    }

    /**
     * Resolve the actual argument value into a colony.
     *
     * @param context the command context.
     * @param name    the argument name.
     * @param report  report failure to the command source.
     * @return the colony, or null if there is no such colony.
     */
    @Nullable
    public static IColony tryGetColony(@NotNull final CommandContext<CommandSourceStack> context, @NotNull final String name, boolean report)
    {
        final int colonyId = getColonyId(context, name);

        final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, context.getSource().getLevel());
        if (colony == null)
        {
            if (report)
            {
                final String message = String.translatable(CommandTranslationConstants.COMMAND_COLONY_ID_NOT_FOUND, colonyId);
                context.getSource().sendFailure(message);
            }
            return null;
        }

        return colony;
    }

    /**
     * Resolve the actual argument value into a colony id.
     *
     * @param context the command context.
     * @param name    the argument name.
     * @return the colony id.
     * @throws RuntimeException if a colony id cannot be parsed from the given argument (this is already reported back).
     */
    public static int getColonyId(@NotNull final CommandContext<CommandSourceStack> context, @NotNull final String name)
    {
        try
        {
            return getValue(context, name);
        }
        catch (CommandSyntaxException e)
        {
            final String message = ComponentUtils.fromMessage(e.getRawMessage());
            context.getSource().sendFailure(message);
            throw new RuntimeException(message.getString());
        }
    }

    /**
     * Resolve the actual argument value into a colony.
     *
     * @param context the command context.
     * @param name    the argument name.
     * @return the colony.
     * @throws CommandRuntimeException if a colony id cannot be parsed from the given argument (this is already reported back).
     */
    @NotNull
    public static IColony getColony(@NotNull final CommandContext<CommandSourceStack> context, @NotNull final String name)
    {
        final int colonyId = getColonyId(context, name);

        final IColony colony = IColonyManager.getInstance().getColonyByWorld(colonyId, context.getSource().getLevel());
        if (colony == null)
        {
            final String message = String.translatable(CommandTranslationConstants.COMMAND_COLONY_ID_NOT_FOUND, colonyId);
            context.getSource().sendFailure(message);
            throw new CommandRuntimeException(message);
        }

        return colony;
    }

    private static int resolveByOwner(@NotNull final CommandSourceStack source, @NotNull final UUID id) throws CommandSyntaxException
    {
        final IColony colony = IColonyManager.getInstance().getIColonyByOwner(source.getLevel(), id);
        if (colony == null)
        {
            throw ERROR_UNKNOWN_COLONY.create();
        }
        return colony.getID();
    }

    public static class HereOption implements ArgumentOption<Integer>
    {
        @Override
        public boolean matches(final String value)
        {
            return Objects.equals(value, "@here");
        }

        @Override
        public Integer resolveValue(final CommandSourceStack source, final String value) throws CommandSyntaxException
        {
            final IColony colony = IColonyManager.getInstance().getIColony(source.getLevel(), new int[]{(int)source.getPosition().x, (int)source.getPosition().y, (int)source.getPosition().z});
            if (colony == null)
            {
                throw ERROR_UNKNOWN_COLONY.create();
            }
            return colony.getID();
        }

        @Override
        public void createSuggestions(final World world, final SharedSuggestionProvider suggestionProvider, final SuggestionsBuilder builder)
        {
            builder.suggest("@here", String.translatable("com.minecolonies.command.argument.colony.here"));
        }
    }

    public static class MineOption implements ArgumentOption<Integer>
    {
        @Override
        public boolean matches(final String value)
        {
            return Objects.equals(value, "@mine");
        }

        @Override
        public Integer resolveValue(final CommandSourceStack source, final String value) throws CommandSyntaxException
        {
            return resolveByOwner(source, source.getPlayerOrException().getGameProfile().getId());
        }

        @Override
        public void createSuggestions(final World world, final SharedSuggestionProvider suggestionProvider, final SuggestionsBuilder builder)
        {
            builder.suggest("@mine", String.translatable("com.minecolonies.command.argument.colony.mine"));
        }
    }

    public static class ColonyIdOption implements ArgumentOption<Integer>
    {
        @Override
        public boolean matches(final String value)
        {
            try
            {
                Integer.parseInt(value);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        @Override
        public Integer resolveValue(final CommandSourceStack source, final String value) throws CommandSyntaxException
        {
            return Integer.parseInt(value);
        }

        @Override
        public void createSuggestions(final World world, final SharedSuggestionProvider suggestionProvider, final SuggestionsBuilder builder)
        {
            IColonyManager.getInstance().getIColonies(world).stream().map(IColony::getID).forEach(builder::suggest);
        }
    }

    public static class PlayerUuidOption implements ArgumentOption<Integer>
    {
        @Override
        public boolean matches(final String value)
        {
            try
            {
                UUID.fromString(value);
                return true;
            }
            catch (IllegalArgumentException e)
            {
                return false;
            }
        }

        @Override
        public Integer resolveValue(final CommandSourceStack source, final String value) throws CommandSyntaxException
        {
            return ColonyIdArgument.resolveByOwner(source, UUID.fromString(value));
        }

        @Override
        public void createSuggestions(final World world, final SharedSuggestionProvider suggestionProvider, final SuggestionsBuilder builder)
        {
        }
    }

    public static class PlayerNameOption implements ArgumentOption<Integer>
    {
        @Override
        public boolean matches(final String value)
        {
            return true;
        }

        @Override
        public Integer resolveValue(final CommandSourceStack source, final String value) throws CommandSyntaxException
        {
            final Optional<GameProfile> profile = Optional.ofNullable(source.getServer().getProfileCache()).flatMap(m -> m.get(value));
            if (profile.isPresent())
            {
                return resolveByOwner(source, profile.get().getId());
            }
            throw ERROR_UNKNOWN_PLAYER.create();
        }

        @Override
        public void createSuggestions(final World world, final SharedSuggestionProvider suggestionProvider, final SuggestionsBuilder builder)
        {
            suggestionProvider.getOnlinePlayerNames().forEach(builder::suggest);
        }
    }
}



