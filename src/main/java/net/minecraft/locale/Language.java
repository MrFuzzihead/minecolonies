package net.minecraft.locale;

/** [1.7.10 stub] Language — localization helper. */
public class Language
{
    private static final Language INSTANCE = new Language();

    public static Language getInstance() { return INSTANCE; }

    public String getOrDefault(String key) { return key; }

    public net.minecraft.util.FormattedCharSequence getVisualOrder(net.minecraft.util.FormattedCharSequence text) { return text; }

    public String getOrDefault(String key, String fallback) { return fallback; }
}

