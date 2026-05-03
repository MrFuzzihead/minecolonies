package net.minecraft.client.renderer;

/**
 * [1.7.10] Compatibility stub for 1.21 MultiBufferSource.
 */
public interface MultiBufferSource
{
    Object getBuffer(Object renderType);

    class BufferSource implements MultiBufferSource
    {
        @Override
        public Object getBuffer(Object renderType) { return null; }
        public void endBatch() {}
    }
}

