package com.minecolonies.api.entity;

import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.mobs.AbstractEntityMinecoloniesRaider;
import com.minecolonies.api.entity.mobs.amazons.AbstractEntityAmazon;
import com.minecolonies.api.entity.mobs.amazons.AbstractEntityAmazonRaider;
import com.minecolonies.api.entity.mobs.barbarians.AbstractEntityBarbarian;
import com.minecolonies.api.entity.mobs.barbarians.AbstractEntityBarbarianRaider;
import com.minecolonies.api.entity.mobs.drownedpirate.AbstractDrownedEntityPirate;
import com.minecolonies.api.entity.mobs.drownedpirate.AbstractDrownedEntityPirateRaider;
import com.minecolonies.api.entity.mobs.egyptians.AbstractEntityEgyptian;
import com.minecolonies.api.entity.mobs.egyptians.AbstractEntityEgyptianRaider;
import com.minecolonies.api.entity.mobs.pirates.AbstractEntityPirate;
import com.minecolonies.api.entity.mobs.pirates.AbstractEntityPirateRaider;
import com.minecolonies.api.entity.mobs.vikings.AbstractEntityNorsemen;
import com.minecolonies.api.entity.mobs.vikings.AbstractEntityNorsemenRaider;
import com.minecolonies.api.entity.other.MinecoloniesMinecart;
import com.minecolonies.core.entity.other.cavalry.CavalryHorseEntity;

import net.minecraft.entity.Entity;

import java.util.List;

/**
 * Registry of all MineColonies entity classes.
 * In 1.7.10 we store Class<T> references instead of EntityType<T>.
 */
public class ModEntities
{
    public static Class<? extends AbstractEntityCitizen> CITIZEN;

    public static Class<? extends AbstractEntityCitizen> VISITOR;

    // FishHook equivalent - projectile entity
    public static Class<? extends Entity> FISHHOOK;

    public static Class<? extends Entity> MERCENARY;

    public static Class<? extends AbstractEntityBarbarianRaider> BARBARIAN;

    public static Class<? extends AbstractEntityBarbarianRaider> ARCHERBARBARIAN;

    public static Class<? extends AbstractEntityBarbarianRaider> CHIEFBARBARIAN;

    public static Class<? extends AbstractEntityPirateRaider> PIRATE;

    public static Class<? extends AbstractEntityPirateRaider> CHIEFPIRATE;

    public static Class<? extends AbstractEntityPirateRaider> ARCHERPIRATE;

    public static Class<? extends Entity> SITTINGENTITY;

    public static Class<? extends AbstractEntityEgyptianRaider> MUMMY;

    public static Class<? extends AbstractEntityEgyptianRaider> PHARAO;

    public static Class<? extends AbstractEntityEgyptianRaider> ARCHERMUMMY;

    public static Class<? extends AbstractEntityNorsemenRaider> NORSEMEN_ARCHER;

    public static Class<? extends AbstractEntityNorsemenRaider> SHIELDMAIDEN;

    public static Class<? extends AbstractEntityNorsemenRaider> NORSEMEN_CHIEF;

    public static Class<? extends AbstractEntityAmazonRaider> AMAZON;

    public static Class<? extends AbstractEntityAmazonRaider> AMAZONSPEARMAN;

    public static Class<? extends AbstractEntityAmazonRaider> AMAZONCHIEF;

    public static Class<MinecoloniesMinecart> MINECART;

    public static Class<CavalryHorseEntity> CAVALRY_HORSE;

    // Projectile entities
    public static Class<? extends Entity> FIREARROW;

    public static Class<? extends Entity> MC_NORMAL_ARROW;

    public static Class<? extends Entity> DRUID_POTION;

    public static Class<? extends Entity> SPEAR;

    public static Class<? extends AbstractDrownedEntityPirateRaider> DROWNED_PIRATE;

    public static Class<? extends AbstractDrownedEntityPirateRaider> DROWNED_CHIEFPIRATE;

    public static Class<? extends AbstractDrownedEntityPirateRaider> DROWNED_ARCHERPIRATE;

    // Camp Raiders

    public static Class<? extends AbstractEntityBarbarian> CAMP_BARBARIAN;

    public static Class<? extends AbstractEntityBarbarian> CAMP_ARCHERBARBARIAN;

    public static Class<? extends AbstractEntityBarbarian> CAMP_CHIEFBARBARIAN;

    public static Class<? extends AbstractEntityPirate> CAMP_PIRATE;

    public static Class<? extends AbstractEntityPirate> CAMP_CHIEFPIRATE;

    public static Class<? extends AbstractEntityPirate> CAMP_ARCHERPIRATE;

    public static Class<? extends AbstractEntityAmazon> CAMP_AMAZON;

    public static Class<? extends AbstractEntityAmazon> CAMP_AMAZONSPEARMAN;

    public static Class<? extends AbstractEntityAmazon> CAMP_AMAZONCHIEF;

    public static Class<? extends AbstractEntityEgyptian> CAMP_MUMMY;

    public static Class<? extends AbstractEntityEgyptian> CAMP_PHARAO;

    public static Class<? extends AbstractEntityEgyptian> CAMP_ARCHERMUMMY;

    public static Class<? extends AbstractEntityNorsemen> CAMP_NORSEMEN_ARCHER;

    public static Class<? extends AbstractEntityNorsemen> CAMP_SHIELDMAIDEN;

    public static Class<? extends AbstractEntityNorsemen> CAMP_NORSEMEN_CHIEF;

    public static Class<? extends AbstractDrownedEntityPirate> CAMP_DROWNED_PIRATE;

    public static Class<? extends AbstractDrownedEntityPirate> CAMP_DROWNED_CHIEFPIRATE;

    public static Class<? extends AbstractDrownedEntityPirate> CAMP_DROWNED_ARCHERPIRATE;

    public static List<Class<? extends AbstractEntityMinecoloniesRaider>> getRaiders()
    {
        return List.of(
          BARBARIAN,
          ARCHERBARBARIAN,
          CHIEFBARBARIAN,
          AMAZON,
          AMAZONSPEARMAN,
          AMAZONCHIEF,
          MUMMY,
          ARCHERMUMMY,
          PHARAO,
          PIRATE,
          ARCHERPIRATE,
          CHIEFPIRATE,
          SHIELDMAIDEN,
          NORSEMEN_ARCHER,
          NORSEMEN_CHIEF,
          DROWNED_PIRATE,
          DROWNED_ARCHERPIRATE,
          DROWNED_CHIEFPIRATE
        );
    }
}
