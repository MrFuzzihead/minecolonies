package com.minecolonies.api.entity.mobs;
import net.minecraft.sounds.SoundEvent;

// [1.7.10] SoundEvent does not exist in 1.7.10; replaced with String sound name.
public interface ICustomAttackSound
{
    /**
     * The custom sound name to be used instead of skeleton shoot sound.
     * @return The sound name (e.g. "mob.skeleton.hurt") to play when attacking.
     */
    String getAttackSound();
}
