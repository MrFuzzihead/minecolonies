package net.minecraft.util;
/** [1.7.10 stub] RandomSource - maps to java.util.Random */
public interface RandomSource {
    static RandomSource create() { return new java.util.Random()::nextInt == null ? null : new Impl(); }
    static RandomSource create(long seed) { return new Impl(seed); }
    int nextInt(); int nextInt(int bound); long nextLong(); boolean nextBoolean(); float nextFloat(); double nextDouble(); double nextGaussian();
    class Impl implements RandomSource {
        private final java.util.Random rng;
        public Impl() { rng = new java.util.Random(); }
        public Impl(long seed) { rng = new java.util.Random(seed); }
        public int nextInt() { return rng.nextInt(); }
        public int nextInt(int b) { return rng.nextInt(b); }
        public long nextLong() { return rng.nextLong(); }
        public boolean nextBoolean() { return rng.nextBoolean(); }
        public float nextFloat() { return rng.nextFloat(); }
        public double nextDouble() { return rng.nextDouble(); }
        public double nextGaussian() { return rng.nextGaussian(); }
    }
}