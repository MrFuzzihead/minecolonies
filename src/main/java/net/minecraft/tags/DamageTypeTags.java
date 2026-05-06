package net.minecraft.tags;
import net.minecraft.world.damagesource.DamageType;
/** [1.7.10 bridge] DamageTypeTags */
public class DamageTypeTags {
    public static final TagKey<DamageType> BYPASSES_ARMOR = new TagKey<>();
    public static final TagKey<DamageType> IS_PROJECTILE = new TagKey<>();
    public static final TagKey<DamageType> BYPASSES_INVULNERABILITY = new TagKey<>();
    public static final TagKey<DamageType> IS_FIRE = new TagKey<>();
    public static final TagKey<DamageType> IS_FALL = new TagKey<>();
    public static final TagKey<DamageType> IS_EXPLOSION = new TagKey<>();
}
