package com.mojang.serialization.codecs;

/** [1.7.10 stub] RecordCodecBuilder. */
public class RecordCodecBuilder<O, F>
{
    public static <O> com.mojang.serialization.Codec<O> create(java.util.function.Function<Instance<O>, Object> builder)
    {
        return new com.mojang.serialization.Codec<O>(){};
    }

    public static class Instance<O>
    {
        public <F> Object group(Object... fields) { return null; }
    }
}

