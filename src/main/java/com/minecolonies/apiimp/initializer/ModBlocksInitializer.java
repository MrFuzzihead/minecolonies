package com.minecolonies.apiimp.initializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;


import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.blocks.*;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagBanner;
import com.minecolonies.core.blocks.decorative.BlockColonyFlagWallBanner;
import com.minecolonies.core.blocks.decorative.BlockConstructionTape;
import com.minecolonies.core.blocks.decorative.BlockGate;
import com.minecolonies.core.blocks.huts.*;
import com.minecolonies.core.blocks.schematic.BlockWaypoint;

import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.IRON_GATE;
import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.WOODEN_GATE;
import static com.minecolonies.core.blocks.MinecoloniesCropBlock.*;
import static com.minecolonies.core.blocks.MinecoloniesFarmland.FARMLAND;
import static com.minecolonies.core.blocks.MinecoloniesFarmland.FLOODED_FARMLAND;

/**
 * Initialises all {@link ModBlocks} instances and registers them with the game.
 *
 * <p><b>1.7.10 Backport Notes:</b>
 * Call {@link #init()} from {@code MineColonies.preInit(FMLPreInitializationEvent)}.
 * Each {@code registerBlock()} call invokes {@code GameRegistry.registerBlock()} internally
 * (see {@code AbstractBlockMinecolonies.registerBlock()}), so there is no longer a
 * separate block-registry pass and item-registry pass — both happen in one call.
 * The {@code registerBlockItem()} calls are kept as no-ops for readability/API parity.</p>
 *
 * <p>In 1.21 this class used {@code @Mod.EventBusSubscriber} and reacted to
 * {@code RegisterEvent}. In 1.7.10 it is called directly from the mod entry point.</p>
 */
@SuppressWarnings("PMD.ExcessiveMethodLength")
public final class ModBlocksInitializer
{
    private ModBlocksInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModBlockInitializer but this is a Utility class.");
    }

    // [1.7.10 BACKPORT] In 1.21 there were two @SubscribeEvent methods:
    //   registerBlocks(RegisterEvent)  — filled ModBlocks fields
    //   registerItems(RegisterEvent)   — registered the ItemBlocks
    // In 1.7.10 a single init() method handles everything (called from preInit).

    /**
     * Registers every block (and its ItemBlock) with the game.
     * Must be called during {@code FMLPreInitializationEvent}.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static void init()
    {
        ModBlocks.blockHutBaker          = new BlockHutBaker().registerBlock();
        ModBlocks.blockHutBlacksmith     = new BlockHutBlacksmith().registerBlock();
        ModBlocks.blockHutBuilder        = new BlockHutBuilder().registerBlock();
        ModBlocks.blockHutHome           = new BlockHutCitizen().registerBlock();
        ModBlocks.blockHutDeliveryman    = new BlockHutDeliveryman().registerBlock();
        ModBlocks.blockHutFarmer         = new BlockHutFarmer().registerBlock();
        ModBlocks.blockScarecrow         = new BlockScarecrow().registerBlock();
        ModBlocks.blockHutFisherman      = new BlockHutFisherman().registerBlock();
        ModBlocks.blockHutGuardTower     = new BlockHutGuardTower().registerBlock();
        ModBlocks.blockHutLumberjack     = new BlockHutLumberjack().registerBlock();
        ModBlocks.blockHutMiner          = new BlockHutMiner().registerBlock();
        ModBlocks.blockHutStonemason     = new BlockHutStonemason().registerBlock();
        ModBlocks.blockHutTownHall       = new BlockHutTownHall().registerBlock();
        ModBlocks.blockHutWareHouse      = new BlockHutWareHouse().registerBlock();
        ModBlocks.blockHutShepherd       = new BlockHutShepherd().registerBlock();
        ModBlocks.blockHutCowboy         = new BlockHutCowboy().registerBlock();
        ModBlocks.blockHutSwineHerder    = new BlockHutSwineHerder().registerBlock();
        ModBlocks.blockHutChickenHerder  = new BlockHutChickenHerder().registerBlock();
        ModBlocks.blockHutBarracks       = new BlockHutBarracks().registerBlock();
        ModBlocks.blockHutBarracksTower  = new BlockHutBarracksTower().registerBlock();
        ModBlocks.blockHutCook           = new BlockHutCook().registerBlock();
        ModBlocks.blockHutSmeltery       = new BlockHutSmeltery().registerBlock();
        ModBlocks.blockHutComposter      = new BlockHutComposter().registerBlock();
        ModBlocks.blockHutLibrary        = new BlockHutLibrary().registerBlock();
        ModBlocks.blockHutArchery        = new BlockHutArchery().registerBlock();
        ModBlocks.blockHutSawmill        = new BlockHutSawmill().registerBlock();
        ModBlocks.blockHutCombatAcademy  = new BlockHutCombatAcademy().registerBlock();
        ModBlocks.blockHutStoneSmeltery  = new BlockHutStoneSmeltery().registerBlock();
        ModBlocks.blockHutCrusher        = new BlockHutCrusher().registerBlock();
        ModBlocks.blockHutSifter         = new BlockHutSifter().registerBlock();
        ModBlocks.blockHutFlorist        = new BlockHutFlorist().registerBlock();
        ModBlocks.blockHutEnchanter      = new BlockHutEnchanter().registerBlock();
        ModBlocks.blockHutUniversity     = new BlockHutUniversity().registerBlock();
        ModBlocks.blockHutHospital       = new BlockHutHospital().registerBlock();
        ModBlocks.blockHutSchool         = new BlockHutSchool().registerBlock();
        ModBlocks.blockHutGlassblower    = new BlockHutGlassblower().registerBlock();
        ModBlocks.blockHutDyer           = new BlockHutDyer().registerBlock();
        ModBlocks.blockHutFletcher       = new BlockHutFletcher().registerBlock();
        ModBlocks.blockHutMechanic       = new BlockHutMechanic().registerBlock();
        ModBlocks.blockHutTavern         = new BlockHutTavern().registerBlock();
        ModBlocks.blockHutPlantation     = new BlockHutPlantation().registerBlock();
        ModBlocks.blockPlantationField   = new BlockPlantationField().registerBlock();
        ModBlocks.blockHutRabbitHutch    = new BlockHutRabbitHutch().registerBlock();
        ModBlocks.blockHutConcreteMixer  = new BlockHutConcreteMixer().registerBlock();
        ModBlocks.blockHutBeekeeper      = new BlockHutBeekeeper().registerBlock();
        ModBlocks.blockHutMysticalSite   = new BlockHutMysticalSite().registerBlock();
        ModBlocks.blockHutGraveyard      = new BlockHutGraveyard().registerBlock();
        ModBlocks.blockHutNetherWorker   = new BlockHutNetherWorker().registerBlock();
        ModBlocks.blockHutAlchemist      = new BlockHutAlchemist().registerBlock();
        ModBlocks.blockHutKitchen        = new BlockHutKitchen().registerBlock();
        ModBlocks.blockHutGateHouse      = new BlockHutGateHouse().registerBlock();
        ModBlocks.blockSimpleQuarry      = new SimpleQuarry().registerBlock();
        ModBlocks.blockMediumQuarry      = new MediumQuarry().registerBlock();
        //ModBlocks.blockLargeQuarry     = new LargeQuarry().registerBlock();

        ModBlocks.blockConstructionTape      = new BlockConstructionTape().registerBlock();
        ModBlocks.blockRack                  = new BlockMinecoloniesRack().registerBlock();
        ModBlocks.blockGrave                 = new BlockMinecoloniesGrave().registerBlock();
        ModBlocks.blockNamedGrave            = new BlockMinecoloniesNamedGrave().registerBlock();
        ModBlocks.blockWayPoint              = new BlockWaypoint().registerBlock();
        ModBlocks.blockPostBox               = new BlockPostBox().registerBlock();
        ModBlocks.blockStash                 = new BlockStash().registerBlock();
        ModBlocks.blockDecorationPlaceholder = new BlockDecorationController().registerBlock();
        ModBlocks.blockBarrel                = new BlockBarrel().registerBlock();
        ModBlocks.blockCompostedDirt         = new BlockCompostedDirt().registerBlock();
        ModBlocks.blockColonyBanner          = new BlockColonyFlagBanner().registerBlock();
        ModBlocks.blockColonyWallBanner      = new BlockColonyFlagWallBanner().registerBlock();
        ModBlocks.blockIronGate              = new BlockGate(IRON_GATE, 10f, 6, 8).registerBlock();
        ModBlocks.blockWoodenGate            = new BlockGate(WOODEN_GATE, 7f, 6, 5).registerBlock();
        ModBlocks.farmland                   = new MinecoloniesFarmland(FARMLAND, false, 15.0).registerBlock();
        ModBlocks.floodedFarmland            = new MinecoloniesFarmland(FLOODED_FARMLAND, true, 13.0).registerBlock();
        ModBlocks.blockColonySign            = new BlockColonySign().registerBlock();

        // Crop blocks — note that Tags (ModTags.temperateBiomes etc.) are not available in
        // 1.7.10. The biome-NBTBase parameters are passed as null for now; the MinecoloniesCropBlock
        // constructor should handle null gracefully (or be adapted to use biome lists / IDs).
        // TODO: [1.7.10 BACKPORT] Replace ModTags.* biome tags with 1.7.10 biome checks.
        // TODO: [1.7.10 BACKPORT] Blocks.GRASS/TALL_GRASS/FERN/SEAGRASS/DEAD_BUSH/SMALL_DRIPLEAF
        //   reference vanilla blocks that may have different names in 1.7.10 (use Blocks.*
        //   from the 1.7.10 deobf jar, or net.minecraft.init.Blocks.*).
        ModBlocks.blockBellPepper     = new MinecoloniesCropBlock(BELL_PEPPER,     ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockCabbage        = new MinecoloniesCropBlock(CABBAGE,         ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockChickpea       = new MinecoloniesCropBlock(CHICKPEA,        ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockDurum          = new MinecoloniesCropBlock(DURUM,           ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockEggplant       = new MinecoloniesCropBlock(EGGPLANT,        ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockGarlic         = new MinecoloniesCropBlock(GARLIC,          ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockOnion          = new MinecoloniesCropBlock(ONION,           ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockSoyBean        = new MinecoloniesCropBlock(SOYBEAN,         ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockTomato         = new MinecoloniesCropBlock(TOMATO,          ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockRice           = new MinecoloniesCropBlock(RICE,            ModBlocks.floodedFarmland, null, null).registerBlock();
        ModBlocks.blockButternutSquash= new MinecoloniesCropBlock(BUTTERNUT_SQUASH,ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockCorn           = new MinecoloniesCropBlock(CORN,            ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockMint           = new MinecoloniesCropBlock(MINT,            ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockNetherPepper   = new MinecoloniesCropBlock(NETHER_PEPPER,   ModBlocks.farmland,        null, null).registerBlock();
        ModBlocks.blockPeas           = new MinecoloniesCropBlock(PEAS,            ModBlocks.farmland,        null, null).registerBlock();

        // [1.7.10 BACKPORT] registerBlockItem() calls below are no-ops — kept for API parity.
        // In 1.21 these registered the ItemBlock via IForgeRegistry<Item>.
        // In 1.7.10, ItemBlock registration happens inside each registerBlock() call above.
        registerBlockItems();

        // [1.7.10 BACKPORT] registerCompostItems() is commented out — ComposterBlock does not
        // exist in 1.7.10; composting logic should be handled differently (e.g., a custom event
        // or IC2/Forestry compost integration).
        // registerCompostItems();
    }

    /**
     * No-op method kept for API parity with the 1.21 version.
     *
     * <p>In 1.21 this iterated over all registered blocks and called
     * {@code registerBlockItem(IForgeRegistry<Item>, Item.Properties)} to register their
     * associated ItemBlocks. In 1.7.10, that registration is done inside each
     * {@code registerBlock()} call via {@code GameRegistry.registerBlock(block, itemClass, name)}.</p>
     */
    private static void registerBlockItems()
    {
        // All registerBlockItem() calls are no-ops in 1.7.10 (see IBlockMinecolonies).
        // Kept here as documentation of original intent.
        ModBlocks.blockHutBaker.registerBlockItem();
        ModBlocks.blockHutBlacksmith.registerBlockItem();
        ModBlocks.blockHutBuilder.registerBlockItem();
        ModBlocks.blockHutHome.registerBlockItem();
        ModBlocks.blockHutDeliveryman.registerBlockItem();
        ModBlocks.blockHutFarmer.registerBlockItem();
        ModBlocks.blockScarecrow.registerBlockItem();
        ModBlocks.blockHutFisherman.registerBlockItem();
        ModBlocks.blockHutGuardTower.registerBlockItem();
        ModBlocks.blockHutLumberjack.registerBlockItem();
        ModBlocks.blockHutMiner.registerBlockItem();
        ModBlocks.blockHutStonemason.registerBlockItem();
        ModBlocks.blockHutTownHall.registerBlockItem();
        ModBlocks.blockHutWareHouse.registerBlockItem();
        ModBlocks.blockHutShepherd.registerBlockItem();
        ModBlocks.blockHutCowboy.registerBlockItem();
        ModBlocks.blockHutSwineHerder.registerBlockItem();
        ModBlocks.blockHutChickenHerder.registerBlockItem();
        ModBlocks.blockHutBarracksTower.registerBlockItem();
        ModBlocks.blockHutBarracks.registerBlockItem();
        ModBlocks.blockHutCook.registerBlockItem();
        ModBlocks.blockHutSmeltery.registerBlockItem();
        ModBlocks.blockHutComposter.registerBlockItem();
        ModBlocks.blockHutLibrary.registerBlockItem();
        ModBlocks.blockHutArchery.registerBlockItem();
        ModBlocks.blockHutCombatAcademy.registerBlockItem();
        ModBlocks.blockHutSawmill.registerBlockItem();
        ModBlocks.blockHutStoneSmeltery.registerBlockItem();
        ModBlocks.blockHutCrusher.registerBlockItem();
        ModBlocks.blockHutSifter.registerBlockItem();
        ModBlocks.blockHutFlorist.registerBlockItem();
        ModBlocks.blockHutEnchanter.registerBlockItem();
        ModBlocks.blockHutUniversity.registerBlockItem();
        ModBlocks.blockHutHospital.registerBlockItem();
        ModBlocks.blockHutSchool.registerBlockItem();
        ModBlocks.blockHutGlassblower.registerBlockItem();
        ModBlocks.blockHutDyer.registerBlockItem();
        ModBlocks.blockHutFletcher.registerBlockItem();
        ModBlocks.blockHutMechanic.registerBlockItem();
        ModBlocks.blockHutTavern.registerBlockItem();
        ModBlocks.blockHutPlantation.registerBlockItem();
        ModBlocks.blockPlantationField.registerBlockItem();
        ModBlocks.blockHutRabbitHutch.registerBlockItem();
        ModBlocks.blockHutConcreteMixer.registerBlockItem();
        ModBlocks.blockHutBeekeeper.registerBlockItem();
        ModBlocks.blockHutMysticalSite.registerBlockItem();
        ModBlocks.blockHutGraveyard.registerBlockItem();
        ModBlocks.blockHutNetherWorker.registerBlockItem();
        ModBlocks.blockHutAlchemist.registerBlockItem();
        ModBlocks.blockHutKitchen.registerBlockItem();
        ModBlocks.blockHutGateHouse.registerBlockItem();
        ModBlocks.blockConstructionTape.registerBlockItem();
        ModBlocks.blockRack.registerBlockItem();
        ModBlocks.blockGrave.registerBlockItem();
        ModBlocks.blockNamedGrave.registerBlockItem();
        ModBlocks.blockWayPoint.registerBlockItem();
        ModBlocks.blockBarrel.registerBlockItem();
        ModBlocks.blockPostBox.registerBlockItem();
        ModBlocks.blockStash.registerBlockItem();
        ModBlocks.blockDecorationPlaceholder.registerBlockItem();
        ModBlocks.blockCompostedDirt.registerBlockItem();
        ModBlocks.farmland.registerBlockItem();
        ModBlocks.floodedFarmland.registerBlockItem();
        ModBlocks.blockColonySign.registerBlockItem();
        ModBlocks.blockBellPepper.registerBlockItem();
        ModBlocks.blockCabbage.registerBlockItem();
        ModBlocks.blockChickpea.registerBlockItem();
        ModBlocks.blockDurum.registerBlockItem();
        ModBlocks.blockEggplant.registerBlockItem();
        ModBlocks.blockGarlic.registerBlockItem();
        ModBlocks.blockOnion.registerBlockItem();
        ModBlocks.blockSoyBean.registerBlockItem();
        ModBlocks.blockTomato.registerBlockItem();
        ModBlocks.blockRice.registerBlockItem();
        ModBlocks.blockButternutSquash.registerBlockItem();
        ModBlocks.blockCorn.registerBlockItem();
        ModBlocks.blockMint.registerBlockItem();
        ModBlocks.blockNetherPepper.registerBlockItem();
        ModBlocks.blockPeas.registerBlockItem();
        ModBlocks.blockSimpleQuarry.registerBlockItem();
        ModBlocks.blockMediumQuarry.registerBlockItem();
    }

    // [1.7.10 BACKPORT] registerCompostItems() is omitted — ComposterBlock.COMPOSTABLES
    // does not exist in 1.7.10. Composting support will be addressed separately.
    //
    // private static void registerCompostItems() {
    //     for (final Block block : ModBlocks.getCrops()) {
    //         ComposterBlock.COMPOSTABLES.put(block.asItem(), 0.5f);
    //     }
    //     ComposterBlock.COMPOSTABLES.put(ModBlocks.blockCompostedDirt.asItem(), 1f);
    // }
}

