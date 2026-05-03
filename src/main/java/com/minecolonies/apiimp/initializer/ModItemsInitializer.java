package com.minecolonies.apiimp.initializer;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.items.*;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;

import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.IRON_GATE;
import static com.minecolonies.api.blocks.decorative.AbstractBlockGate.WOODEN_GATE;

public final class ModItemsInitializer
{
    private ModItemsInitializer()
    {
        throw new IllegalStateException("Tried to initialize: ModItemsInitializer but this is a Utility class.");
    }

    /**
     * Initiates all items.  Called from FMLPreInitializationEvent.
     * [1.7.10 BACKPORT] Replaces the RegisterEvent + IForgeRegistry<Item> pattern.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static void init()
    {
        ModItems.scepterLumberjack = new ItemScepterLumberjack();
        ModItems.supplyChest = new ItemSupplyChestDeployer();
        ModItems.permTool = new ItemScepterPermission();
        ModItems.scepterGuard = new ItemScepterGuard();
        ModItems.assistantHammer_Gold = new ItemAssistantHammer("assistanthammer_gold", 200, 1);
        ModItems.assistantHammer_Iron = new ItemAssistantHammer("assistanthammer_iron", 400, 2);
        ModItems.assistantHammer_Diamond = new ItemAssistantHammer("assistanthammer_diamond", 1000, 3);
        ModItems.bannerRallyGuards = new ItemBannerRallyGuards();
        ModItems.supplyCamp = new ItemSupplyCampDeployer();
        ModItems.ancientTome = new ItemAncientTome();
        ModItems.chiefSword = new ItemChiefSword();
        ModItems.scimitar = new ItemIronScimitar();
        ModItems.clipboard = new ItemClipboard();
        ModItems.compost = new ItemCompost();
        ModItems.resourceScroll = new ItemResourceScroll();
        ModItems.pharaoscepter = new ItemPharaoScepter();
        ModItems.firearrow = new ItemFireArrow();
        ModItems.scepterBeekeeper = new ItemScepterBeekeeper();
        ModItems.mistletoe = new ItemMistletoe();
        ModItems.spear = new ItemSpear();
        ModItems.questLog = new ItemQuestLog();

        ModItems.breadDough = new ItemBreadDough();
        ModItems.cookieDough = new ItemCookieDough();
        ModItems.cakeBatter = new ItemCakeBatter();
        ModItems.rawPumpkinPie = new ItemRawPumpkinPie();

        ModItems.milkyBread = new ItemMilkyBread();
        ModItems.sugaryBread = new ItemSugaryBread();
        ModItems.goldenBread = new ItemGoldenBread();
        ModItems.chorusBread = new ItemChorusBread();

        ModItems.adventureToken = new ItemAdventureToken();

        ModItems.scrollColonyTP = new ItemScrollColonyTP();
        GameRegistry.registerItem(ModItems.scrollColonyTP, "scroll_tp", Constants.MOD_ID);

        ModItems.scrollColonyAreaTP = new ItemScrollColonyAreaTP();
        GameRegistry.registerItem(ModItems.scrollColonyAreaTP, "scroll_area_tp", Constants.MOD_ID);

        ModItems.scrollBuff = new ItemScrollBuff();
        GameRegistry.registerItem(ModItems.scrollBuff, "scroll_buff", Constants.MOD_ID);

        ModItems.scrollGuardHelp = new ItemScrollGuardHelp();
        GameRegistry.registerItem(ModItems.scrollGuardHelp, "scroll_guard_help", Constants.MOD_ID);

        ModItems.scrollHighLight = new ItemScrollHighlight();
        GameRegistry.registerItem(ModItems.scrollHighLight, "scroll_highlight", Constants.MOD_ID);

        // [1.7.10 BACKPORT] ArmorItem.Type.HELMET etc. → armor slot integers handled inside each item class.
        ModItems.santaHat = new ItemSantaHead("santa_hat", ItemSantaHead.SANTA_HAT, 0);
        ModItems.irongate = new ItemGate(IRON_GATE, ModBlocks.blockIronGate);
        ModItems.woodgate = new ItemGate(WOODEN_GATE, ModBlocks.blockWoodenGate);

        ModItems.flagBanner = new ItemColonyFlagBanner("colony_banner");
        ModItems.pirateHelmet_1 = new ItemPirateGear("pirate_hat", ItemPirateGear.PIRATE_ARMOR_1, 0);
        ModItems.pirateChest_1 = new ItemPirateGear("pirate_top", ItemPirateGear.PIRATE_ARMOR_1, 1);
        ModItems.pirateLegs_1 = new ItemPirateGear("pirate_leggins", ItemPirateGear.PIRATE_ARMOR_1, 2);
        ModItems.pirateBoots_1 = new ItemPirateGear("pirate_boots", ItemPirateGear.PIRATE_ARMOR_1, 3);

        ModItems.pirateHelmet_2 = new ItemPirateGear("pirate_cap", ItemPirateGear.PIRATE_ARMOR_2, 0);
        ModItems.pirateChest_2 = new ItemPirateGear("pirate_chest", ItemPirateGear.PIRATE_ARMOR_2, 1);
        ModItems.pirateLegs_2 = new ItemPirateGear("pirate_legs", ItemPirateGear.PIRATE_ARMOR_2, 2);
        ModItems.pirateBoots_2 = new ItemPirateGear("pirate_shoes", ItemPirateGear.PIRATE_ARMOR_2, 3);

        ModItems.plateArmorHelmet = new ItemPlateArmor("plate_armor_helmet", ItemPlateArmor.PLATE_ARMOR, 0);
        ModItems.plateArmorChest = new ItemPlateArmor("plate_armor_chest", ItemPlateArmor.PLATE_ARMOR, 1);
        ModItems.plateArmorLegs = new ItemPlateArmor("plate_armor_legs", ItemPlateArmor.PLATE_ARMOR, 2);
        ModItems.plateArmorBoots = new ItemPlateArmor("plate_armor_boots", ItemPlateArmor.PLATE_ARMOR, 3);

        ModItems.sifterMeshString = new ItemSifterMesh("sifter_mesh_string", 500);
        ModItems.sifterMeshFlint = new ItemSifterMesh("sifter_mesh_flint", 1000);
        ModItems.sifterMeshIron = new ItemSifterMesh("sifter_mesh_iron", 1500);
        ModItems.sifterMeshDiamond = new ItemSifterMesh("sifter_mesh_diamond", 2000);

        ModItems.magicpotion = new ItemMagicPotion("magicpotion");
        ModItems.buildGoggles = new ItemBuildGoggles("build_goggles");
        ModItems.scanAnalyzer = new ItemScanAnalyzer("scan_analyzer");
        ModItems.colonyMap = new ItemColonyMap();

        // All-biome Tier 1 food
        ModItems.cheddar_cheese = new ItemFood(1);
        ModItems.feta_cheese = new ItemFood(1);
        ModItems.cooked_rice = new ItemBowlFood(1);
        ModItems.tofu = new ItemFood(1);
        ModItems.flatbread = new ItemFood(1);
        ModItems.cheese_ravioli = new ItemFood(1);
        ModItems.chicken_broth = new ItemFood(1);
        ModItems.meat_ravioli = new ItemFood(1);
        ModItems.mint_jelly = new ItemFood(1);
        ModItems.mint_tea = new ItemFood(1);
        ModItems.polenta = new ItemFood(1);
        ModItems.potato_soup = new ItemFood(1);
        ModItems.veggie_ravioli = new ItemFood(1);
        ModItems.yogurt = new ItemFood(1);

        // All-biome Tier 2 food
        ModItems.manchet_bread = new ItemFood(2);
        ModItems.lembas_scone = new ItemFood(2);
        ModItems.muffin = new ItemFood(2);
        ModItems.pottage = new ItemBowlFood(2);
        ModItems.pasta_plain = new ItemBowlFood(2);
        ModItems.apple_pie = new ItemFood(2);
        ModItems.plain_cheesecake = new ItemFood(2);
        ModItems.baked_salmon = new ItemFood(2);
        ModItems.eggdrop_soup = new ItemFood(2);
        ModItems.fish_n_chips = new ItemFood(2);
        ModItems.pierogi = new ItemFood(2);
        ModItems.veggie_soup = new ItemFood(2);
        ModItems.yogurt_with_berries = new ItemBowlFood(2);

        // All-biome Tier 3 food
        ModItems.hand_pie = new ItemFood(3);
        ModItems.mintchoco_cheesecake = new ItemFood(3);
        ModItems.borscht = new ItemFood(3);
        ModItems.schnitzel = new ItemFood(3);
        ModItems.steak_dinner = new ItemFood(3);

        // Cold biomes
        ModItems.squash_soup = new ItemFood(1);
        ModItems.cabochis = new ItemBowlFood(2);
        ModItems.veggie_quiche = new ItemFood(2);
        ModItems.lamb_stew = new ItemBowlFood(3);
        ModItems.fish_dinner = new ItemFood(3);

        // Hot humid biomes
        ModItems.pea_soup = new ItemFood(1);
        ModItems.rice_ball = new ItemFood(2);
        ModItems.eggplant_dolma = new ItemFood(3);
        ModItems.kimchi = new ItemFood(2);
        ModItems.mutton_dinner = new ItemFood(3);
        ModItems.sushi_roll = new ItemFood(3);
        ModItems.ramen = new ItemFood(3);
        ModItems.fried_rice = new ItemFood(3);

        // Temperate biomes
        ModItems.corn_chowder = new ItemFood(1);
        ModItems.tortillas = new ItemFood(1);
        ModItems.pasta_tomato = new ItemBowlFood(2);
        ModItems.cheese_pizza = new ItemFood(2);
        ModItems.stuffed_pita = new ItemFood(3);
        ModItems.mushroom_pizza = new ItemFood(3);

        // Hot dry biomes
        ModItems.spicy_grilled_chicken = new ItemFood(1);
        ModItems.pepper_hummus = new ItemFood(2);
        ModItems.kebab = new ItemFood(2);
        ModItems.pita_hummus = new ItemFood(3);
        ModItems.spicy_eggplant = new ItemFood(3);

        // Traded items
        ModItems.congee = new ItemBowlFood(2);
        ModItems.stew_trencher = new ItemFood(3);
        ModItems.stuffed_pepper = new ItemFood(3);
        ModItems.tacos = new ItemFood(3);

        // Ingredients
        ModItems.muffin_dough = new Item();
        ModItems.manchet_dough = new Item();
        ModItems.raw_noodle = new Item();
        ModItems.butter = new Item();
        ModItems.cornmeal = new Item();
        ModItems.creamcheese = new Item();
        ModItems.soysauce = new Item();

        // Bottles
        ModItems.large_empty_bottle = new ItemLargeBottle();
        ModItems.large_milk_bottle = new ItemLargeBottle();
        ModItems.large_water_bottle = new ItemLargeBottle();
        ModItems.large_soy_milk_bottle = new ItemLargeBottle();

        // [1.7.10 BACKPORT] Spawn eggs (ForgeSpawnEggItem) have no 1.7.10 equivalent and are omitted.

        // ---- Registration ----
        GameRegistry.registerItem(ModItems.supplyChest, "supplychestdeployer", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.scanAnalyzer, "scan_analyzer", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.permTool, "scepterpermission", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.scepterGuard, "scepterguard", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.assistantHammer_Gold, "assistanthammer_gold", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.assistantHammer_Iron, "assistanthammer_iron", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.assistantHammer_Diamond, "assistanthammer_diamond", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.bannerRallyGuards, "banner_rally_guards", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.supplyCamp, "supplycampdeployer", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.ancientTome, "ancienttome", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.chiefSword, "chiefsword", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.clipboard, "clipboard", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.compost, "compost", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.resourceScroll, "resourcescroll", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.scimitar, "iron_scimitar", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.scepterLumberjack, "scepterlumberjack", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pharaoscepter, "pharaoscepter", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.firearrow, "firearrow", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.scepterBeekeeper, "scepterbeekeeper", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.mistletoe, "mistletoe", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.spear, "spear", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.questLog, "questlog", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.colonyMap, "colonymap", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.breadDough, "bread_dough", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.cookieDough, "cookie_dough", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.cakeBatter, "cake_batter", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.rawPumpkinPie, "raw_pumpkin_pie", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.milkyBread, "milky_bread", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.sugaryBread, "sugary_bread", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.goldenBread, "golden_bread", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.chorusBread, "chorus_bread", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.adventureToken, "adventure_token", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.pirateHelmet_1, "pirate_hat", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pirateChest_1, "pirate_top", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pirateLegs_1, "pirate_leggins", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pirateBoots_1, "pirate_boots", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.pirateHelmet_2, "pirate_cap", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pirateChest_2, "pirate_chest", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pirateLegs_2, "pirate_legs", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pirateBoots_2, "pirate_shoes", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.plateArmorHelmet, "plate_armor_helmet", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.plateArmorChest, "plate_armor_chest", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.plateArmorLegs, "plate_armor_legs", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.plateArmorBoots, "plate_armor_boots", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.santaHat, "santa_hat", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.irongate, IRON_GATE, Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.woodgate, WOODEN_GATE, Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.flagBanner, "colony_banner", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.sifterMeshString, "sifter_mesh_string", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.sifterMeshFlint, "sifter_mesh_flint", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.sifterMeshIron, "sifter_mesh_iron", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.sifterMeshDiamond, "sifter_mesh_diamond", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.magicpotion, "magicpotion", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.buildGoggles, "build_goggles", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.butter, "butter", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.cabochis, "cabochis", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.cheddar_cheese, "cheddar_cheese", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.congee, "congee", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.cooked_rice, "cooked_rice", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.eggplant_dolma, "eggplant_dolma", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.feta_cheese, "feta_cheese", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.flatbread, "flatbread", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.hand_pie, "hand_pie", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.lamb_stew, "lamb_stew", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.lembas_scone, "lembas_scone", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.manchet_bread, "manchet_bread", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.manchet_dough, "manchet_dough", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.muffin, "muffin", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.muffin_dough, "muffin_dough", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pasta_plain, "pasta_plain", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pasta_tomato, "pasta_tomato", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pepper_hummus, "pepper_hummus", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pita_hummus, "pita_hummus", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pottage, "pottage", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.raw_noodle, "raw_noodle", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.rice_ball, "rice_ball", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.stew_trencher, "stew_trencher", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.stuffed_pepper, "stuffed_pepper", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.stuffed_pita, "stuffed_pita", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.sushi_roll, "sushi_roll", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.tofu, "tofu", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.cheese_ravioli, "cheese_ravioli", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.chicken_broth, "chicken_broth", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.corn_chowder, "corn_chowder", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.spicy_grilled_chicken, "spicy_grilled_chicken", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.kebab, "kebab", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.meat_ravioli, "meat_ravioli", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.mint_jelly, "mint_jelly", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.mint_tea, "mint_tea", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pea_soup, "pea_soup", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.polenta, "polenta", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.potato_soup, "potato_soup", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.squash_soup, "squash_soup", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.veggie_ravioli, "veggie_ravioli", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.yogurt, "yogurt", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.baked_salmon, "baked_salmon", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.eggdrop_soup, "eggdrop_soup", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.fish_n_chips, "fish_n_chips", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.kimchi, "kimchi", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.pierogi, "pierogi", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.veggie_quiche, "veggie_quiche", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.veggie_soup, "veggie_soup", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.yogurt_with_berries, "yogurt_with_berries", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.borscht, "borscht", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.fish_dinner, "fish_dinner", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.mutton_dinner, "mutton_dinner", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.ramen, "ramen", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.fried_rice, "fried_rice", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.schnitzel, "schnitzel", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.steak_dinner, "steak_dinner", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.tacos, "tacos", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.cornmeal, "cornmeal", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.creamcheese, "creamcheese", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.soysauce, "soysauce", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.tortillas, "tortillas", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.apple_pie, "apple_pie", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.cheese_pizza, "cheese_pizza", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.mushroom_pizza, "mushroom_pizza", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.plain_cheesecake, "plain_cheesecake", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.mintchoco_cheesecake, "mintchoco_cheesecake", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.spicy_eggplant, "spicy_eggplant", Constants.MOD_ID);

        GameRegistry.registerItem(ModItems.large_empty_bottle, "large_empty_bottle", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.large_water_bottle, "large_water_bottle", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.large_milk_bottle, "large_milk_bottle", Constants.MOD_ID);
        GameRegistry.registerItem(ModItems.large_soy_milk_bottle, "large_soy_milk_bottle", Constants.MOD_ID);

        // [1.7.10 BACKPORT] Spawn eggs removed — ForgeSpawnEggItem has no 1.7.10 equivalent.
        // TODO: no 1.7.10 equivalent for barbarianegg, barbarcheregg, barbchiefegg,
        //       pirateegg, piratearcheregg, piratecaptainegg, mummyegg, mummyarcheregg, pharaoegg,
        //       shieldmaidenegg, norsemenarcheregg, norsemenchiefegg,
        //       amazonegg, amazonspearmanegg, amazonchiefegg,
        //       drownedpirateegg, drownedpiratearcheregg, drownedpiratecaptainegg
    }
}
