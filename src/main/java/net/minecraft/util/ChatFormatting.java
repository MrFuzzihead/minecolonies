package net.minecraft.util;

/**
 * [1.7.10] Shim for 1.21 ChatFormatting.
 * Maps to 1.7.10 EnumChatFormatting values.
 */
public enum ChatFormatting
{
    BLACK      (EnumChatFormatting.BLACK),
    DARK_BLUE  (EnumChatFormatting.DARK_BLUE),
    DARK_GREEN (EnumChatFormatting.DARK_GREEN),
    DARK_AQUA  (EnumChatFormatting.DARK_AQUA),
    DARK_RED   (EnumChatFormatting.DARK_RED),
    DARK_PURPLE(EnumChatFormatting.DARK_PURPLE),
    GOLD       (EnumChatFormatting.GOLD),
    GRAY       (EnumChatFormatting.GRAY),
    DARK_GRAY  (EnumChatFormatting.DARK_GRAY),
    BLUE       (EnumChatFormatting.BLUE),
    GREEN      (EnumChatFormatting.GREEN),
    AQUA       (EnumChatFormatting.AQUA),
    RED        (EnumChatFormatting.RED),
    LIGHT_PURPLE(EnumChatFormatting.LIGHT_PURPLE),
    YELLOW     (EnumChatFormatting.YELLOW),
    WHITE      (EnumChatFormatting.WHITE),
    OBFUSCATED (EnumChatFormatting.OBFUSCATED),
    BOLD       (EnumChatFormatting.BOLD),
    STRIKETHROUGH(EnumChatFormatting.STRIKETHROUGH),
    UNDERLINE  (EnumChatFormatting.UNDERLINE),
    ITALIC     (EnumChatFormatting.ITALIC),
    RESET      (EnumChatFormatting.RESET);

    private final EnumChatFormatting delegate;

    ChatFormatting(final EnumChatFormatting delegate)
    {
        this.delegate = delegate;
    }

    public EnumChatFormatting getDelegate()
    {
        return delegate;
    }

    @Override
    public String toString()
    {
        return delegate.toString();
    }
}

