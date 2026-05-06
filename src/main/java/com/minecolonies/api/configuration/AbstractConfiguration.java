package com.minecolonies.api.configuration;
import net.minecraft.core.Holder;

import com.ldtteam.structurize.util.LanguageHandler;
import com.minecolonies.api.util.constant.Constants;
// [1.7.10 BACKPORT] Forge 1.7.10 config system
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

// [1.7.10 BACKPORT] Removed:
//   net.minecraftforge.common.ForgeConfigSpec.*  — ForgeConfigSpec does not exist in 1.7.10.
//   ForgeConfigSpec.BooleanValue / IntValue / LongValue / DoubleValue / ConfigValue / EnumValue
//   are all replaced by the inner ConfigValue<T> class below, which provides the same .get()
//   interface so that all call-sites in the rest of the codebase remain unchanged.

/**
 * Base class for all MineColonies configuration sections.
 *
 * <p><b>1.7.10 Backport:</b> The 1.21 approach used {@code ForgeConfigSpec.Builder} to
 * build a typed configuration tree that was registered with FML's config system.
 * In 1.7.10, Forge uses {@code net.minecraftforge.common.config.Configuration} (a flat
 * key/category file). We replicate the same category/key structure and wrap each value
 * in a {@link ConfigValue} holder so that all existing {@code .get()} call-sites work
 * without modification.</p>
 */
public abstract class AbstractConfiguration
{
    // -----------------------------------------------------------------------
    // ConfigValue<T> — preserves .get() call-sites throughout the codebase
    // -----------------------------------------------------------------------

    /**
     * A simple typed holder that mirrors the {@code ForgeConfigSpec.*Value} interface.
     * Provides {@code .get()} and {@code .set()} so that all existing call-sites
     * ({@code config.getServer().pvp_mode.get()}, etc.) compile without changes.
     *
     * @param <T> the type of the configuration value.
     */
    public static class ConfigValue<T>
    {
        private T value;

        public ConfigValue(final T defaultValue)
        {
            this.value = defaultValue;
        }

        /** Returns the current configuration value. */
        public T get()
        {
            return value;
        }

        /** Updates the value (called during config (re-)load). */
        public void set(final T value)
        {
            this.value = value;
        }
    }

    // -----------------------------------------------------------------------
    // Convenience type aliases (match ForgeConfigSpec naming so diffs are minimal)
    // -----------------------------------------------------------------------

    /** Alias for {@code ConfigValue<Boolean>}, matching {@code ForgeConfigSpec.BooleanValue}. */
    public static final class BooleanValue extends ConfigValue<Boolean>
    {
        public BooleanValue(final boolean defaultValue) { super(defaultValue); }
    }

    /** Alias for {@code ConfigValue<Integer>}, matching {@code ForgeConfigSpec.IntValue}. */
    public static final class IntValue extends ConfigValue<Integer>
    {
        public IntValue(final int defaultValue) { super(defaultValue); }
    }

    /** Alias for {@code ConfigValue<Long>}, matching {@code ForgeConfigSpec.LongValue}. */
    public static final class LongValue extends ConfigValue<Long>
    {
        public LongValue(final long defaultValue) { super(defaultValue); }
    }

    /** Alias for {@code ConfigValue<Double>}, matching {@code ForgeConfigSpec.DoubleValue}. */
    public static final class DoubleValue extends ConfigValue<Double>
    {
        public DoubleValue(final double defaultValue) { super(defaultValue); }
    }

    /** Alias for {@code ConfigValue<List<String>>}, matching {@code ForgeConfigSpec.ConfigValue<List>}. */
    public static final class StringListValue extends ConfigValue<List<String>>
    {
        public StringListValue(final List<String> defaultValue) { super(defaultValue); }
    }

    /** Alias for enum config values, matching {@code ForgeConfigSpec.EnumValue<V>}. */
    public static final class EnumValue<V extends Enum<V>> extends ConfigValue<V>
    {
        private final Class<V> enumClass;
        public EnumValue(final V defaultValue, final Class<V> enumClass)
        {
            super(defaultValue);
            this.enumClass = enumClass;
        }
        public Class<V> getEnumClass() { return enumClass; }
    }

    // -----------------------------------------------------------------------
    // Category tracking (replaces Builder push/pop)
    // -----------------------------------------------------------------------

    /** Currently active category (section) in the config file. */
    protected String currentCategory = Configuration.CATEGORY_GENERAL;

    protected void createCategory(final Configuration config, final String key)
    {
        currentCategory = key;
    }

    protected void swapToCategory(final Configuration config, final String key)
    {
        currentCategory = key;
    }

    protected void finishCategory(final Configuration config)
    {
        currentCategory = Configuration.CATEGORY_GENERAL;
    }

    // -----------------------------------------------------------------------
    // Value definition helpers (replace ForgeConfigSpec.Builder.define* methods)
    // Each helper reads the value from the Forge config file and stores it in a ConfigValue.
    // -----------------------------------------------------------------------

    private static String commentFor(final String key)
    {
        final String tKey = Constants.MOD_ID + ".config." + key + ".comment";
        return LanguageHandler.translateKey(tKey);
    }

    protected BooleanValue defineBoolean(final Configuration config, final String key, final boolean defaultValue)
    {
        final BooleanValue holder = new BooleanValue(defaultValue);
        final Property prop = config.get(currentCategory, key, defaultValue, commentFor(key));
        holder.set(prop.getBoolean(defaultValue));
        return holder;
    }

    protected IntValue defineInteger(final Configuration config, final String key, final int defaultValue)
    {
        return defineInteger(config, key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    protected IntValue defineInteger(final Configuration config, final String key, final int defaultValue, final int min, final int max)
    {
        final IntValue holder = new IntValue(defaultValue);
        final Property prop = config.get(currentCategory, key, defaultValue, commentFor(key), min, max);
        holder.set(prop.getInt(defaultValue));
        return holder;
    }

    protected LongValue defineLong(final Configuration config, final String key, final long defaultValue)
    {
        return defineLong(config, key, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    protected LongValue defineLong(final Configuration config, final String key, final long defaultValue, final long min, final long max)
    {
        // Forge 1.7.10 config has no native long; store as string and parse.
        final LongValue holder = new LongValue(defaultValue);
        final Property prop = config.get(currentCategory, key, String.valueOf(defaultValue), commentFor(key));
        try { holder.set(Long.parseLong(prop.getString())); }
        catch (final NumberFormatException ignored) { holder.set(defaultValue); }
        return holder;
    }

    protected DoubleValue defineDouble(final Configuration config, final String key, final double defaultValue)
    {
        return defineDouble(config, key, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    protected DoubleValue defineDouble(final Configuration config, final String key, final double defaultValue, final double min, final double max)
    {
        final DoubleValue holder = new DoubleValue(defaultValue);
        final Property prop = config.get(currentCategory, key, defaultValue, commentFor(key));
        holder.set(prop.getDouble(defaultValue));
        return holder;
    }

    protected StringListValue defineList(final Configuration config, final String key, final List<? extends String> defaultValue, final Predicate<Object> elementValidator)
    {
        final StringListValue holder = new StringListValue((List<String>) defaultValue);
        final String[] defaultArray = defaultValue.toArray(new String[0]);
        final Property prop = config.get(currentCategory, key, defaultArray, commentFor(key));
        holder.set(Arrays.asList(prop.getStringList()));
        return holder;
    }

    protected <V extends Enum<V>> EnumValue<V> defineEnum(final Configuration config, final String key, final V defaultValue)
    {
        @SuppressWarnings("unchecked")
        final Class<V> cls = (Class<V>) defaultValue.getClass();
        final EnumValue<V> holder = new EnumValue<>(defaultValue, cls);
        final Property prop = config.get(currentCategory, key, defaultValue.name(), commentFor(key));
        try { holder.set(Enum.valueOf(cls, prop.getString().toUpperCase())); }
        catch (final IllegalArgumentException ignored) { holder.set(defaultValue); }
        return holder;
    }

    // [1.7.10 BACKPORT] In 1.21, AbstractConfiguration used ForgeConfigSpec.Builder directly:
    //   protected void createCategory(Builder builder, String key) { builder.comment(...).push(key); }
    //   protected static BooleanValue defineBoolean(Builder builder, String key, boolean def) { ... }
    //   etc.
    // These are all replaced by the Configuration-file-based helpers above.
    // The Builder parameter type has been changed to net.minecraftforge.common.config.Configuration
    // throughout, which is why each subclass constructor now takes a Configuration instead of Builder.
}
