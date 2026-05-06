package net.minecraft.network.chat;
import net.minecraft.network.chat.Style;

import java.util.Optional;
import java.util.function.Function;

/** [1.7.10 stub] FormattedText. */
public interface FormattedText
{
    <T> Optional<T> visit(Function<String, Optional<T>> visitor);
    static FormattedText literal(String text) { return visitor -> visitor.apply(text); }
    static FormattedText translatable(String key) { return literal(key); }
    FormattedText setStyle(Style style);
}

