package net.minecraft.network.chat.contents;

/**
 * [1.7.10] Shim for 1.21 TranslatableContents.
 * In 1.7.10 translatable text is just a string translation key.
 */
public class TranslatableContents
{
    /** Empty args constant, matching 1.21 API. */
    public static final Object[] NO_ARGS = new Object[0];

    private final String key;
    private final Object[] args;

    public TranslatableContents(final String key, final Object fallback, final Object[] args)
    {
        this.key = key != null ? key : "";
        this.args = args != null ? args : NO_ARGS;
    }

    /** Returns the translation key. */
    public String getKey()
    {
        return key;
    }

    /** Returns the format arguments. */
    public Object[] getArgs()
    {
        return args;
    }

    @Override
    public String toString()
    {
        return key;
    }
}

