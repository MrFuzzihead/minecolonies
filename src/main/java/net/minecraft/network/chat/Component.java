package net.minecraft.network.chat;
import net.minecraft.network.chat.Style;

/**
 * [1.7.10] Compatibility stub for 1.21 Component (text component).
 */
public interface Component
{
    String getString();

    static MutableComponent literal(String text) { return new MutableComponent(text); }
    static MutableComponent translatable(String key, Object... args) { return new MutableComponent(key); }

    class MutableComponent implements Component
    {
        private final String text;
        public MutableComponent(String text) { this.text = text; }

        @Override
        public String getString() { return text; }

        public MutableComponent append(Object other) { return this; }
        public MutableComponent withStyle(Object style) { return this; }

        @Override
        public String toString() { return text; }
    }
}

