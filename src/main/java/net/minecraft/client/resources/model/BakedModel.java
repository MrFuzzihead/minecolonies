package net.minecraft.client.resources.model;
/** [1.7.10 stub] BakedModel */
public interface BakedModel { java.util.List<Object> getQuads(Object state, Object face, java.util.Random random); boolean useAmbientOcclusion(); boolean isGui3d(); boolean usesBlockLight(); boolean isCustomRenderer(); Object getParticleIcon(); Object getTransforms(); Object getOverrides(); }