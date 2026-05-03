package net.minecraft.network.chat;
/** [1.7.10 stub] FormattedCharSequence */
@FunctionalInterface public interface FormattedCharSequence { boolean accept(int index, Object style, int codePoint); static FormattedCharSequence EMPTY = (index, style, codePoint) -> true; }