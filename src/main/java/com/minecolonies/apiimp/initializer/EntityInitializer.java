package com.minecolonies.apiimp.initializer;

import com.minecolonies.api.entity.other.MinecoloniesMinecart;
import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.mobs.camp.amazons.EntityAmazonChief;
import com.minecolonies.core.entity.mobs.camp.amazons.EntityAmazonSpearman;
import com.minecolonies.core.entity.mobs.camp.amazons.EntityArcherAmazon;
import com.minecolonies.core.entity.mobs.camp.barbarians.EntityArcherBarbarian;
import com.minecolonies.core.entity.mobs.camp.barbarians.EntityBarbarian;
import com.minecolonies.core.entity.mobs.camp.barbarians.EntityChiefBarbarian;
import com.minecolonies.core.entity.mobs.camp.drownedpirates.EntityDrownedArcherPirate;
import com.minecolonies.core.entity.mobs.camp.drownedpirates.EntityDrownedCaptainPirate;
import com.minecolonies.core.entity.mobs.camp.drownedpirates.EntityDrownedPirate;
import com.minecolonies.core.entity.mobs.camp.egyptians.EntityArcherMummy;
import com.minecolonies.core.entity.mobs.camp.egyptians.EntityMummy;
import com.minecolonies.core.entity.mobs.camp.egyptians.EntityPharao;
import com.minecolonies.core.entity.mobs.camp.norsemen.EntityNorsemenArcher;
import com.minecolonies.core.entity.mobs.camp.norsemen.EntityNorsemenChief;
import com.minecolonies.core.entity.mobs.camp.norsemen.EntityShieldmaiden;
import com.minecolonies.core.entity.mobs.camp.pirates.EntityArcherPirate;
import com.minecolonies.core.entity.mobs.camp.pirates.EntityCaptainPirate;
import com.minecolonies.core.entity.mobs.camp.pirates.EntityPirate;
import com.minecolonies.core.entity.mobs.raider.drownedpirates.EntityDrownedArcherPirateRaider;
import com.minecolonies.core.entity.mobs.raider.drownedpirates.EntityDrownedCaptainPirateRaider;
import com.minecolonies.core.entity.mobs.raider.drownedpirates.EntityDrownedPirateRaider;
import com.minecolonies.core.entity.visitor.VisitorCitizen;
import com.minecolonies.core.entity.mobs.EntityMercenary;
import com.minecolonies.core.entity.mobs.raider.amazons.EntityAmazonChiefRaider;
import com.minecolonies.core.entity.mobs.raider.amazons.EntityAmazonSpearmanRaider;
import com.minecolonies.core.entity.mobs.raider.amazons.EntityArcherAmazonRaider;
import com.minecolonies.core.entity.mobs.raider.barbarians.EntityArcherBarbarianRaider;
import com.minecolonies.core.entity.mobs.raider.barbarians.EntityBarbarianRaider;
import com.minecolonies.core.entity.mobs.raider.barbarians.EntityChiefBarbarianRaider;
import com.minecolonies.core.entity.mobs.raider.egyptians.EntityArcherMummyRaider;
import com.minecolonies.core.entity.mobs.raider.egyptians.EntityMummyRaider;
import com.minecolonies.core.entity.mobs.raider.egyptians.EntityPharaoRaider;
import com.minecolonies.core.entity.mobs.raider.norsemen.EntityNorsemenArcherRaider;
import com.minecolonies.core.entity.mobs.raider.norsemen.EntityNorsemenChiefRaider;
import com.minecolonies.core.entity.mobs.raider.norsemen.EntityShieldmaidenRaider;
import com.minecolonies.core.entity.mobs.raider.pirates.EntityArcherPirateRaider;
import com.minecolonies.core.entity.mobs.raider.pirates.EntityCaptainPirateRaider;
import com.minecolonies.core.entity.mobs.raider.pirates.EntityPirateRaider;
import com.minecolonies.core.entity.other.*;
import com.minecolonies.core.entity.other.cavalry.CavalryHorseEntity;

import cpw.mods.fml.common.registry.EntityRegistry;

import static com.minecolonies.api.util.constant.CitizenConstants.CITIZEN_HEIGHT;
import static com.minecolonies.api.util.constant.CitizenConstants.CITIZEN_WIDTH;
import static com.minecolonies.api.util.constant.Constants.*;

/**
 * Initializes and registers all MineColonies entities using the 1.7.10 EntityRegistry.
 */
public class EntityInitializer
{
    /**
     * Starting entity ID for MineColonies entities.
     * Must not conflict with other mods. Range is modular per mod in GTNH.
     */
    private static final int ENTITY_ID_START = 200;

    /**
     * Register all MineColonies entities. Call during FMLPreInitializationEvent.
     *
     * @param modInstance the mod instance (@Mod annotated class).
     */
    public static void init(final Object modInstance)
    {
        int id = ENTITY_ID_START;

        ModEntities.CITIZEN = EntityCitizen.class;
        EntityRegistry.registerModEntity(EntityCitizen.class, "Citizen", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, true);

        ModEntities.VISITOR = VisitorCitizen.class;
        EntityRegistry.registerModEntity(VisitorCitizen.class, "VisitorCitizen", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, true);

        ModEntities.MERCENARY = EntityMercenary.class;
        EntityRegistry.registerModEntity(EntityMercenary.class, "Mercenary", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, true);

        ModEntities.FISHHOOK = NewBobberEntity.class;
        EntityRegistry.registerModEntity(NewBobberEntity.class, "MCFishHook", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY_FISHHOOK, true);

        ModEntities.SITTINGENTITY = SittingEntity.class;
        EntityRegistry.registerModEntity(SittingEntity.class, "SittingEntity", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.MINECART = MinecoloniesMinecart.class;
        EntityRegistry.registerModEntity(MinecoloniesMinecart.class, "MCMinecart", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, true);

        ModEntities.CAVALRY_HORSE = CavalryHorseEntity.class;
        EntityRegistry.registerModEntity(CavalryHorseEntity.class, "CavalryHorse", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, true);

        // Projectiles
        ModEntities.FIREARROW = FireArrowEntity.class;
        EntityRegistry.registerModEntity(FireArrowEntity.class, "FireArrow", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY_FISHHOOK, true);

        ModEntities.MC_NORMAL_ARROW = CustomArrowEntity.class;
        EntityRegistry.registerModEntity(CustomArrowEntity.class, "MCNormalArrow", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY_FISHHOOK, true);

        ModEntities.DRUID_POTION = DruidPotionEntity.class;
        EntityRegistry.registerModEntity(DruidPotionEntity.class, "DruidPotion", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY_FISHHOOK, true);

        ModEntities.SPEAR = SpearEntity.class;
        EntityRegistry.registerModEntity(SpearEntity.class, "Spear", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY_FISHHOOK, true);

        // Raider entities
        ModEntities.BARBARIAN = EntityBarbarianRaider.class;
        EntityRegistry.registerModEntity(EntityBarbarianRaider.class, "Barbarian", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.ARCHERBARBARIAN = EntityArcherBarbarianRaider.class;
        EntityRegistry.registerModEntity(EntityArcherBarbarianRaider.class, "ArcherBarbarian", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CHIEFBARBARIAN = EntityChiefBarbarianRaider.class;
        EntityRegistry.registerModEntity(EntityChiefBarbarianRaider.class, "ChiefBarbarian", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.PIRATE = EntityPirateRaider.class;
        EntityRegistry.registerModEntity(EntityPirateRaider.class, "Pirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.ARCHERPIRATE = EntityArcherPirateRaider.class;
        EntityRegistry.registerModEntity(EntityArcherPirateRaider.class, "ArcherPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CHIEFPIRATE = EntityCaptainPirateRaider.class;
        EntityRegistry.registerModEntity(EntityCaptainPirateRaider.class, "CaptainPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.MUMMY = EntityMummyRaider.class;
        EntityRegistry.registerModEntity(EntityMummyRaider.class, "Mummy", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.ARCHERMUMMY = EntityArcherMummyRaider.class;
        EntityRegistry.registerModEntity(EntityArcherMummyRaider.class, "ArcherMummy", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.PHARAO = EntityPharaoRaider.class;
        EntityRegistry.registerModEntity(EntityPharaoRaider.class, "Pharao", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.AMAZON = EntityArcherAmazonRaider.class;
        EntityRegistry.registerModEntity(EntityArcherAmazonRaider.class, "Amazon", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.AMAZONSPEARMAN = EntityAmazonSpearmanRaider.class;
        EntityRegistry.registerModEntity(EntityAmazonSpearmanRaider.class, "AmazonSpearman", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.AMAZONCHIEF = EntityAmazonChiefRaider.class;
        EntityRegistry.registerModEntity(EntityAmazonChiefRaider.class, "AmazonChief", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.SHIELDMAIDEN = EntityShieldmaidenRaider.class;
        EntityRegistry.registerModEntity(EntityShieldmaidenRaider.class, "Shieldmaiden", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.NORSEMEN_ARCHER = EntityNorsemenArcherRaider.class;
        EntityRegistry.registerModEntity(EntityNorsemenArcherRaider.class, "NorsemenArcher", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.NORSEMEN_CHIEF = EntityNorsemenChiefRaider.class;
        EntityRegistry.registerModEntity(EntityNorsemenChiefRaider.class, "NorsemenChief", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.DROWNED_PIRATE = EntityDrownedPirateRaider.class;
        EntityRegistry.registerModEntity(EntityDrownedPirateRaider.class, "DrownedPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.DROWNED_ARCHERPIRATE = EntityDrownedArcherPirateRaider.class;
        EntityRegistry.registerModEntity(EntityDrownedArcherPirateRaider.class, "DrownedArcherPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.DROWNED_CHIEFPIRATE = EntityDrownedCaptainPirateRaider.class;
        EntityRegistry.registerModEntity(EntityDrownedCaptainPirateRaider.class, "DrownedCaptainPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        // Camp Raiders
        ModEntities.CAMP_BARBARIAN = EntityBarbarian.class;
        EntityRegistry.registerModEntity(EntityBarbarian.class, "CampBarbarian", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_ARCHERBARBARIAN = EntityArcherBarbarian.class;
        EntityRegistry.registerModEntity(EntityArcherBarbarian.class, "CampArcherBarbarian", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_CHIEFBARBARIAN = EntityChiefBarbarian.class;
        EntityRegistry.registerModEntity(EntityChiefBarbarian.class, "CampChiefBarbarian", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_PIRATE = EntityPirate.class;
        EntityRegistry.registerModEntity(EntityPirate.class, "CampPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_ARCHERPIRATE = EntityArcherPirate.class;
        EntityRegistry.registerModEntity(EntityArcherPirate.class, "CampArcherPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_CHIEFPIRATE = EntityCaptainPirate.class;
        EntityRegistry.registerModEntity(EntityCaptainPirate.class, "CampCaptainPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_AMAZON = EntityArcherAmazon.class;
        EntityRegistry.registerModEntity(EntityArcherAmazon.class, "CampAmazon", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_AMAZONSPEARMAN = EntityAmazonSpearman.class;
        EntityRegistry.registerModEntity(EntityAmazonSpearman.class, "CampAmazonSpearman", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_AMAZONCHIEF = EntityAmazonChief.class;
        EntityRegistry.registerModEntity(EntityAmazonChief.class, "CampAmazonChief", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_MUMMY = EntityMummy.class;
        EntityRegistry.registerModEntity(EntityMummy.class, "CampMummy", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_ARCHERMUMMY = EntityArcherMummy.class;
        EntityRegistry.registerModEntity(EntityArcherMummy.class, "CampArcherMummy", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_PHARAO = EntityPharao.class;
        EntityRegistry.registerModEntity(EntityPharao.class, "CampPharao", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_SHIELDMAIDEN = EntityShieldmaiden.class;
        EntityRegistry.registerModEntity(EntityShieldmaiden.class, "CampShieldmaiden", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_NORSEMEN_ARCHER = EntityNorsemenArcher.class;
        EntityRegistry.registerModEntity(EntityNorsemenArcher.class, "CampNorsemenArcher", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_NORSEMEN_CHIEF = EntityNorsemenChief.class;
        EntityRegistry.registerModEntity(EntityNorsemenChief.class, "CampNorsemenChief", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        // Camp drowned pirates
        ModEntities.CAMP_DROWNED_PIRATE = EntityDrownedPirate.class;
        EntityRegistry.registerModEntity(EntityDrownedPirate.class, "CampDrownedPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_DROWNED_ARCHERPIRATE = EntityDrownedArcherPirate.class;
        EntityRegistry.registerModEntity(EntityDrownedArcherPirate.class, "CampDrownedArcherPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);

        ModEntities.CAMP_DROWNED_CHIEFPIRATE = EntityDrownedCaptainPirate.class;
        EntityRegistry.registerModEntity(EntityDrownedCaptainPirate.class, "CampDrownedCaptainPirate", id++, modInstance,
          ENTITY_TRACKING_RANGE, ENTITY_UPDATE_FREQUENCY, false);
    }
}
