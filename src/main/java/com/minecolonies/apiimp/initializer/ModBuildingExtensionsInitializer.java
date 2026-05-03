package com.minecolonies.apiimp.initializer;

import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries;
import com.minecolonies.api.colony.buildingextensions.registry.BuildingExtensionRegistries.BuildingExtensionEntry;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.buildings.workerbuildings.plantation.modules.specific.*;
import com.minecolonies.core.colony.buildingextensions.FarmField;
import com.minecolonies.core.colony.buildingextensions.PlantationField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.Items;
// [1.7.10] registries removed - use direct registration
// [1.7.10] registries removed

import java.util.function.Consumer;

import static com.minecolonies.api.util.constant.SchematicTagConstants.*;

public final class ModBuildingExtensionsInitializer
{
    static
    {
        BuildingExtensionRegistries.farmField = createEntry(BuildingExtensionRegistries.FARM_FIELD_ID, builder -> builder.setExtensionProducer(FarmField::new));

        BuildingExtensionRegistries.plantationSugarCaneField = createEntry(BuildingExtensionRegistries.PLANTATION_SUGAR_CANE_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new SugarCanePlantModule(field, SUGAR_FIELD, SUGAR_CROP, Items.reeds)));

        BuildingExtensionRegistries.plantationCactusField = createEntry(BuildingExtensionRegistries.PLANTATION_CACTUS_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new).addExtensionModuleProducer(field -> new CactusPlantModule(field, CACTUS_FIELD, CACTUS_CROP, Items.cactus)));

        BuildingExtensionRegistries.plantationBambooField = createEntry(BuildingExtensionRegistries.PLANTATION_BAMBOO_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new).addExtensionModuleProducer(field -> new BambooPlantModule(field, BAMBOO_FIELD, BAMBOO_CROP, Items.wheat_seeds /* [1.7.10] bamboo NA */)));

        BuildingExtensionRegistries.plantationCocoaBeansField = createEntry(BuildingExtensionRegistries.PLANTATION_COCOA_BEANS_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new CocoaPlantModule(field, COCOA_FIELD, COCOA_CROP, Items.dye /* cocoa */)));

        BuildingExtensionRegistries.plantationVinesField = createEntry(BuildingExtensionRegistries.PLANTATION_VINES_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new).addExtensionModuleProducer(field -> new VinePlantModule(field, VINE_FIELD, VINE_CROP, Items.vine /* [1.7.10] */)));

        BuildingExtensionRegistries.plantationKelpField = createEntry(BuildingExtensionRegistries.PLANTATION_KELP_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new).addExtensionModuleProducer(field -> new KelpPlantModule(field, KELP_FIELD, KELP_CROP, Items.waterlily /* [1.7.10] no kelp */)));

        BuildingExtensionRegistries.plantationSeagrassField = createEntry(BuildingExtensionRegistries.PLANTATION_SEAGRASS_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new SeagrassPlantModule(field, SEA_GRASS_FIELD, SEA_GRASS_CROP, Items.waterlily /* [1.7.10] no seagrass */)));

        BuildingExtensionRegistries.plantationSeaPicklesField = createEntry(BuildingExtensionRegistries.PLANTATION_SEA_PICKLES_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new SeapicklePlantModule(field, SEA_PICKLE_FIELD, SEA_PICKLE_CROP, Items.glowstone_dust /* [1.7.10] no sea pickle */)));

        BuildingExtensionRegistries.plantationGlowberriesField = createEntry(BuildingExtensionRegistries.PLANTATION_GLOWBERRIES_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new GlowBerriesPlantModule(field, GLOW_BERRY_FIELD, GLOW_BERRY_CROP, Items.glowstone_dust /* [1.7.10] no glow berries */)));

        BuildingExtensionRegistries.plantationWeepingVinesField = createEntry(BuildingExtensionRegistries.PLANTATION_WEEPING_VINES_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new WeepingVinesPlantModule(field, WEEPY_VINE_FIELD, WEEPY_VINE_CROP, Items.nether_wart /* [1.7.10] no weeping vines */)));

        BuildingExtensionRegistries.plantationTwistingVinesField = createEntry(BuildingExtensionRegistries.PLANTATION_TWISTING_VINES_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new TwistingVinesPlantModule(field, TWISTY_VINE_FIELD, TWISTY_VINE_CROP, Items.nether_wart /* [1.7.10] no twisting vines */)));

        BuildingExtensionRegistries.plantationCrimsonPlantsField = createEntry(BuildingExtensionRegistries.PLANTATION_CRIMSON_PLANTS_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new CrimsonPlantsPlantModule(field, CRIMSON_FIELD, CRIMSON_CROP, Items.nether_wart /* [1.7.10] no crimson fungus */)));

        BuildingExtensionRegistries.plantationWarpedPlantsField = createEntry(BuildingExtensionRegistries.PLANTATION_WARPED_PLANTS_FIELD_ID,
          builder -> builder.setExtensionProducer(PlantationField::new)
                       .addExtensionModuleProducer(field -> new WarpedPlantsPlantModule(field, WARPED_FIELD, WARPED_CROP, Items.nether_wart /* [1.7.10] no warped fungus */)));
    }

    private ModBuildingExtensionsInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModFieldsInitializer but this is a Utility class.");
    }

    private static BuildingExtensionEntry createEntry(final ResourceLocation registryName, final Consumer<BuildingExtensionEntry.Builder> builder)
    {
        BuildingExtensionEntry.Builder field = new BuildingExtensionEntry.Builder().setRegistryName(registryName);
        builder.accept(field);
        return field.createExtensionEntry();
    }
}
