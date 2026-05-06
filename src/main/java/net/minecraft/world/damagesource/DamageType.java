package net.minecraft.world.damagesource;
import com.mojang.serialization.Codec;
/** [1.7.10 bridge] DamageType */
public record DamageType(String msgId, DamageScaling scaling, float exhaustion) {
    public static final Codec<DamageType> CODEC = null;
}
