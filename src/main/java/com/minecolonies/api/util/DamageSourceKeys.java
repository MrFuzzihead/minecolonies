package com.minecolonies.api.util;

import com.minecolonies.api.util.constant.Constants;
// [1.7.10] Registries removed
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] net.minecraft.util.DamageSource removed

public class DamageSourceKeys
{
    public static int /* ResourceKey */ DESPAWN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "despawn"));
    public static int /* ResourceKey */ STUCK_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "stuckdamage"));
    public static int /* ResourceKey */ SPEAR = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "spear"));
    public static int /* ResourceKey */ NETHER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "nether"));
    public static int /* ResourceKey */ CONSOLE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "console"));
    public static int /* ResourceKey */ SLAP = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "slap"));
    public static int /* ResourceKey */ DEFAULT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "default"));
    public static int /* ResourceKey */ VISITOR = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "visitor"));
    public static int /* ResourceKey */ WAKEY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "wakeywakey"));
    public static int /* ResourceKey */ TRAINING = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "training"));
    public static int /* ResourceKey */ GUARD = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "guard"));
    public static int /* ResourceKey */ GUARD_PVP = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "guardpvp"));
    public static int /* ResourceKey */ NORSEMENCHIEF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "norsemenchief"));
    public static int /* ResourceKey */ NORSEMENARCHER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "norsemenarcher"));
    public static int /* ResourceKey */ SHIELDMAIDEN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "shieldmaiden"));
    public static int /* ResourceKey */ BARBARIAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "barbarian"));
    public static int /* ResourceKey */ CHIEFBARBARIAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "chiefbarbarian"));
    public static int /* ResourceKey */ ARCHERBARBARIAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "archerbarbarian"));
    public static int /* ResourceKey */ PIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "pirate"));
    public static int /* ResourceKey */ CHIEFPIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "chiefpirate"));
    public static int /* ResourceKey */ ARCHERPIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "archerpirate"));
    public static int /* ResourceKey */ MERCENARY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "mercenary"));
    public static int /* ResourceKey */ MUMMY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "mummy"));
    public static int /* ResourceKey */ PHARAO = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "pharao"));
    public static int /* ResourceKey */ ARCHERMUMMY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "archermummy"));
    public static int /* ResourceKey */ AMAZON = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "amazon"));
    public static int /* ResourceKey */ AMAZONSPEARMAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "amazonspearman"));
    public static int /* ResourceKey */ AMAZONCHIEF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "amazonchief"));
    public static int /* ResourceKey */ DROWNED_PIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "drownedpirate"));
    public static int /* ResourceKey */ DROWNED_CHIEFPIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "drownedchiefpirate"));
    public static int /* ResourceKey */ DROWNED_ARCHERPIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "drownedarcherpirate"));

    public static int /* ResourceKey */ CAMP_AMAZON = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campamazon"));
    public static int /* ResourceKey */ CAMP_AMAZONSPEARMAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campamazonspearman"));
    public static int /* ResourceKey */ CAMP_AMAZONCHIEF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campamazonchief"));
    public static int /* ResourceKey */ CAMP_MUMMY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campmummy"));
    public static int /* ResourceKey */ CAMP_PHARAO = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "camppharao"));
    public static int /* ResourceKey */ CAMP_ARCHERMUMMY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "camparchermummy"));
    public static int /* ResourceKey */ CAMP_NORSEMENCHIEF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campnorsemenchief"));
    public static int /* ResourceKey */ CAMP_NORSEMENARCHER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campnorsemenarcher"));
    public static int /* ResourceKey */ CAMP_SHIELDMAIDEN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campshieldmaiden"));
    public static int /* ResourceKey */ CAMP_BARBARIAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campbarbarian"));
    public static int /* ResourceKey */ CAMP_CHIEFBARBARIAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campchiefbarbarian"));
    public static int /* ResourceKey */ CAMP_ARCHERBARBARIAN = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "camparcherbarbarian"));
    public static int /* ResourceKey */ CAMP_PIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "camppirate"));
    public static int /* ResourceKey */ CAMP_CHIEFPIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "campchiefpirate"));
    public static int /* ResourceKey */ CAMP_ARCHERPIRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Constants.MOD_ID, "camparcherpirate"));

}



