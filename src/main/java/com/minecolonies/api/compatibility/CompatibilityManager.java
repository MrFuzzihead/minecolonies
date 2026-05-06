package com.minecolonies.api.compatibility;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

// [1.7.10] BlockState removed — metadata used instead
import net.minecraft.tileentity.TileEntityFurnace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.requestsystem.StandardFactoryController;
import com.minecolonies.api.compatibility.dynamictrees.DynamicTreeCompat;
import com.minecolonies.api.compatibility.resourcefulbees.ResourcefulBeesCompat;
import com.minecolonies.api.compatibility.tinkers.SlimeTreeCheck;
import com.minecolonies.api.compatibility.tinkers.TinkersToolHelper;
import com.minecolonies.api.crafting.CompostRecipe;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.api.crafting.registry.ModRecipeSerializer;
import com.minecolonies.api.items.CheckedNbtKey;
import com.minecolonies.api.items.ModTags;
import com.minecolonies.api.util.*;
import com.minecolonies.core.colony.crafting.CustomRecipeManager;
import com.minecolonies.core.colony.crafting.LootTableAnalyzer;
import com.minecolonies.core.generation.ItemNbtCalculator;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] BuiltInRegistries removed
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
// [1.7.10] NbtUtils removed
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
// [1.7.10] int /* ResourceKey */ -> int dimensionId
import net.minecraft.util.ResourceLocation;
// [1.7.10] tags removed
// [1.7.10] world.entity removed
// [1.7.10] world.entity removed
import net.minecraft.item.Item; import net.minecraft.item.ItemStack; import net.minecraft.item.ItemBlock;
// [1.7.10] RecipeManager removed
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
// [1.7.10] block.entity removed
// [1.7.10] BlockState -> int metadata
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
// [1.7.10] Tags removed
// [1.7.10] ModList removed
// [1.7.10] registries removed
// [1.7.10] registries removed
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.minecolonies.api.util.ItemStackUtils.*;
import static com.minecolonies.api.util.constant.Constants.DEFAULT_TAB_KEY;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_SAP_LEAF;

/**
 * CompatibilityManager handling certain list and maps of itemStacks of certain types.
 */
public class CompatibilityManager implements ICompatibilityManager
{
    /**
     * Maximum depth sub items are explored at
     */
    private static final int MAX_DEPTH = 100;

    /**
     * BiMap of saplings and leaves.
     */
    private final Map<Block, ItemStorage> leavesToSaplingMap = new HashMap<>();

    /**
     * List of saplings. Works on client and server-side.
     */
    private final List<ItemStorage> saplings = new ArrayList<>();

    /**
     * List of all ore-like blocks. Works on client and server-side.
     */
    private final Set<Block> oreBlocks = new HashSet<>();

    /**
     * List of all ore-like items.
     */
    private final Set<ItemStorage> smeltableOres = new HashSet<>();

    /**
     * List of all the compost recipes
     */
    private final Map<Item, CompostRecipe> compostRecipes = new HashMap<>();

    /**
     * List of all the items that can be planted.
     */
    private final Set<ItemStorage> plantables = new HashSet<>();

    /**
     * List of all the items that can be used as fuel
     */
    private final Set<ItemStorage> fuel = new HashSet<>();

    /**
     * List of all the items that can be used as food
     */
    private final Set<ItemStorage> food = new HashSet<>();

    /**
     * List of all the items that can be used as food
     */
    private final Set<ItemStorage> edibles = new HashSet<>();

    /**
     * Set of all beekeeper flowers.
     */
    private ImmutableSet<ItemStorage> beekeeperflowers = ImmutableSet.of();

    /**
     * List of lucky oreBlocks which get dropped by the miner.
     */
    private final Map<Integer, List<ItemStorage>> luckyOres = new HashMap<>();

    /**
     * Random obj.
     */
    private static final Random random = new Random();

    /**
     * List of all blocks.
     */
    private static ImmutableList<ItemStack> allItems = ImmutableList.of();

    /**
     * Hashmap of mobs we may or may not attack.
     */
    private ImmutableSet<ResourceLocation> monsters = ImmutableSet.of();

    /**
     * Mapping of itemstorage to net.minecraft.creativetab.CreativeTabs.
     */
    private final Map<ItemStorage, net.minecraft.creativetab.CreativeTabs> creativeModeTabMap = new HashMap<>();

    /**
     * Cached mapping of items and colors to dyes.
     */
    private final Int2ObjectMap<Int2IntMap> dyeColorMap = new Int2ObjectOpenHashMap<>();

    /**
     * Instantiates the compatibilityManager.
     */
    public CompatibilityManager()
    {
        /*
         * Intentionally left empty.
         */
    }

    private void clear()
    {
        saplings.clear();
        oreBlocks.clear();
        smeltableOres.clear();
        plantables.clear();
        beekeeperflowers = ImmutableSet.of();

        food.clear();
        edibles.clear();
        fuel.clear();
        compostRecipes.clear();

        monsters = ImmutableSet.of();
        creativeModeTabMap.clear();
    }

    /**
     * Called server-side *only* to calculate the various lists of items from the registry, recipes, and tags.
     *
     * @param recipeManager The vanilla recipe manager.
     */
    @Override
    public void discover(final World world)
    {
        clear();
        discoverAllItems(world);

        discoverModCompat();

        // [1.7.10] discoverCompostRecipes(recipeManager)  no RecipeManager
        discoverMobs();
    }

    @Override
    public void serialize(@NotNull final PacketBuffer buf)
    {
        serializeItemStorageList(buf, saplings);
        serializeBlockList(buf, oreBlocks);
        serializeItemStorageList(buf, smeltableOres);
        serializeItemStorageList(buf, plantables);
        serializeItemStorageList(buf, beekeeperflowers);

        serializeItemStorageList(buf, food);
        serializeItemStorageList(buf, edibles);
        serializeItemStorageList(buf, fuel);
        serializeRegistryIds(buf, null, monsters); // [1.7.10] ForgeRegistries.ENTITY_TYPES not available

        serializeCompostRecipes(buf, compostRecipes);

        buf.writeInt(CHECKED_NBT_KEYS.size());
        for (final var entry : CHECKED_NBT_KEYS.entrySet())
        {
            buf.writeInt(net.minecraft.item.Item.getIdFromItem(entry.getKey()));
            buf.writeInt(entry.getValue().size());
            for (final CheckedNbtKey key : entry.getValue())
            {
                ItemNbtCalculator.serializeKeyToBuffer(key, buf);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void deserialize(@NotNull final PacketBuffer buf, final net.minecraft.world.World World)
    {
        clear();
        discoverAllItems(World);

        saplings.addAll(deserializeItemStorageList(buf));
        oreBlocks.addAll(deserializeBlockList(buf));
        smeltableOres.addAll(deserializeItemStorageList(buf));
        plantables.addAll(deserializeItemStorageList(buf));
        beekeeperflowers = ImmutableSet.copyOf(deserializeItemStorageList(buf));

        food.addAll(deserializeItemStorageList(buf));
        edibles.addAll(deserializeItemStorageList(buf));
        fuel.addAll(deserializeItemStorageList(buf));
        monsters = ImmutableSet.copyOf(deserializeRegistryIds(buf, null)); // [1.7.10] ForgeRegistries.ENTITY_TYPES not available

        Log.getLogger().info("Synchronized {} saplings", saplings.size());
        Log.getLogger().info("Synchronized {} ore blocks with {} smeltable ores", oreBlocks.size(), smeltableOres.size());
        Log.getLogger().info("Synchronized {} plantables", plantables.size());
        Log.getLogger().info("Synchronized {} flowers", beekeeperflowers.size());

        Log.getLogger().info("Synchronized {} food types with {} edible", food.size(), edibles.size());
        Log.getLogger().info("Synchronized {} fuel types", fuel.size());
        Log.getLogger().info("Synchronized {} monsters", monsters.size());

        discoverCompostRecipes(deserializeCompostRecipes(buf));

        // the below are loaded from config files, which have been synched already by this point
        discoverModCompat();

        for (int i = 0, amount = buf.readInt(); i < amount; i++)
        {
            final Item item = net.minecraft.item.Item.getItemById(buf.readInt());
            Set<CheckedNbtKey> nbtKeys = new HashSet<>();
            for (int j = 0, children = buf.readInt(); j < children; j++)
            {
                nbtKeys.add(ItemNbtCalculator.deSerializeKeyFromBuffer(buf));
            }

            CHECKED_NBT_KEYS.put(item, nbtKeys);
        }
    }

    private static void serializeItemStorageList(
      @NotNull final PacketBuffer buf,
      @NotNull final Collection<ItemStorage> list)
    {
        // [1.7.10] buf.writeCollection does not exist; manual serialization
        buf.writeInt(list.size());
        for (final ItemStorage storage : list)
        {
            StandardFactoryController.getInstance().serialize(buf, storage);
        }
    }

    @NotNull
    private static List<ItemStorage> deserializeItemStorageList(@NotNull final PacketBuffer buf)
    {
        // [1.7.10] buf.readList does not exist; manual deserialization
        final int size = buf.readInt();
        final List<ItemStorage> result = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            result.add(StandardFactoryController.getInstance().deserialize(buf));
        }
        return result;
    }

    private static void serializeBlockList(
      @NotNull final PacketBuffer buf,
      @NotNull final Collection<Block> list)
    {
        // [1.7.10] Serialize blocks by item ID
        buf.writeInt(list.size());
        for (final Block block : list)
        {
            final Item item = net.minecraft.item.Item.getItemFromBlock(block);
            buf.writeInt(net.minecraft.item.Item.getIdFromItem(item));
        }
    }

    @NotNull
    private static List<Block> deserializeBlockList(@NotNull final PacketBuffer buf)
    {
        // [1.7.10] Deserialize blocks by item ID
        final int size = buf.readInt();
        final List<Block> blocks = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            final Item item = net.minecraft.item.Item.getItemById(buf.readInt());
            if (item instanceof net.minecraft.item.ItemBlock)
            {
                blocks.add(((net.minecraft.item.ItemBlock) item).field_150939_a);
            }
        }
        return blocks;
    }

    // [1.7.10] IForgeRegistry does not exist; monsters serialization stubbed.
    private static void serializeRegistryIds(
      @NotNull final PacketBuffer buf,
      @NotNull final Object registry,
      @NotNull final Collection<ResourceLocation> ids)
    {
        buf.writeInt(0); // stub: write empty list
    }

    @NotNull
    private static <T> List<ResourceLocation>
    deserializeRegistryIds(
      @NotNull final PacketBuffer buf,
      @NotNull final Object registry)
    {
        int count = buf.readInt(); // stub: read empty list
        return java.util.Collections.emptyList();
    }

    private static void serializeCompostRecipes(
      @NotNull final PacketBuffer buf,
      @NotNull final Map<Item, CompostRecipe> compostRecipes)
    {
        // [1.7.10] writeCollection replaced with manual loop
        final List<CompostRecipe> recipes = new java.util.ArrayList<>(new java.util.LinkedHashSet<>(compostRecipes.values()));
        buf.writeInt(recipes.size());
        for (final CompostRecipe recipe : recipes)
        {
            ModRecipeSerializer.CompostRecipeSerializer.get().toNetwork(buf, recipe);
        }
    }

    @NotNull
    private static List<CompostRecipe> deserializeCompostRecipes(@NotNull final PacketBuffer buf)
    {
        // [1.7.10] readList replaced with manual loop
        final CompostRecipe.Serializer serializer = ModRecipeSerializer.CompostRecipeSerializer.get();
        final ResourceLocation empty = new ResourceLocation("");
        final int size = buf.readInt();
        final List<CompostRecipe> result = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            result.add(serializer.fromNetwork(empty, buf));
        }
        return result;
    }

    /**
     * Getter for the list.
     *
     * @return the list of itemStacks.
     */
    @Override
    public List<ItemStack> getListOfAllItems()
    {
        if (allItems.isEmpty())
        {
            Log.getLogger().error("getListOfAllItems when empty");
        }
        return allItems;
    }

    @Override
    public List<ItemStack> getListOfMatchingItems(final Predicate<ItemStack> predicate)
    {
        List<ItemStack> list = new ArrayList<>();
        for (final ItemStack stack : allItems)
        {
            if (predicate.test(stack))
            {
                list.add(stack);
            }
        }
        return list;
    }

    @Override
    public Set<ItemStorage> getSetOfAllItems()
    {
        if (creativeModeTabMap.isEmpty())
        {
            Log.getLogger().error("getSetOfAllItems when empty");
        }
        return creativeModeTabMap.keySet();
    }

    @Override
    public boolean isPlantable(final ItemStack itemStack)
    {
        // [1.7.10] Tags replaced with runtime plantables list check
        return !ItemStackUtils.isEmpty(itemStack) && plantables.contains(new ItemStorage(itemStack));
    }

    @Override
    public boolean isLuckyBlock(final Block block)
    {
        // [1.7.10] block.defaultBlockState().is(tag) not available; oreChanceBlocks tag replaced with oreBlocks set
        return oreBlocks.contains(block);
    }

    @Nullable
    @Override
    public ItemStack getSaplingForLeaf(final Block block)
    {
        if (leavesToSaplingMap.containsKey(block))
        {
            return leavesToSaplingMap.get(block).getItemStack();
        }
        return null;
    }

    @Override
    public Set<ItemStorage> getCopyOfSaplings()
    {
        if (saplings.isEmpty())
        {
            Log.getLogger().error("getCopyOfSaplings when empty");
        }
        return new HashSet<>(saplings);
    }

    @Override
    public Set<ItemStorage> getFuel()
    {
        if (fuel.isEmpty())
        {
            Log.getLogger().error("getFuel when empty");
        }
        return fuel;
    }

    @Override
    public Set<ItemStorage> getFood()
    {
        if (food.isEmpty())
        {
            Log.getLogger().error("getFood when empty");
        }
        return food;
    }

    @Override
    public Set<ItemStorage> getEdibles(final int minNutrition)
    {
        if (edibles.isEmpty())
        {
            Log.getLogger().error("getEdibles when empty");
        }
        final Set<ItemStorage> filteredEdibles = new HashSet<>();
        for (final ItemStorage storage : edibles)
        {
            final ItemStack s = storage.getItemStack();
            if (s.getItem() instanceof net.minecraft.item.ItemFood
                    && ((net.minecraft.item.ItemFood) s.getItem()).func_150905_g(s) >= minNutrition)
            {
                filteredEdibles.add(storage);
            }
        }
        return filteredEdibles;
    }

    @Override
    public Set<ItemStorage> getSmeltableOres()
    {
        if (smeltableOres.isEmpty())
        {
            Log.getLogger().error("getSmeltableOres when empty");
        }
        return smeltableOres;
    }

    @Override
    public Map<Item, CompostRecipe> getCopyOfCompostRecipes()
    {
        if (compostRecipes.isEmpty())
        {
            Log.getLogger().error("getCopyOfCompostRecipes when empty");
        }
        return ImmutableMap.copyOf(compostRecipes);
    }

    @Override
    public Set<ItemStorage> getCompostInputs()
    {
        if (compostRecipes.isEmpty())
        {
            Log.getLogger().error("getCompostInputs when empty");
        }
        return compostRecipes.keySet().stream()
          .map(item -> new ItemStorage(new ItemStack(item)))
          .collect(Collectors.toSet());
    }

    @Override
    public Set<ItemStorage> getCopyOfPlantables()
    {
        if (plantables.isEmpty())
        {
            Log.getLogger().error("getCopyOfPlantables when empty");
        }
        return new HashSet<>(plantables);
    }

    @Override
    public Set<ItemStorage> getImmutableFlowers()
    {
        if (beekeeperflowers.isEmpty())
        {
            Log.getLogger().error("getImmutableFlowers when empty");
        }
        return beekeeperflowers;
    }

    @Override
    public boolean isOre(final Block block)
    {
        if (oreBlocks.isEmpty())
        {
            Log.getLogger().error("isOre when empty");
        }
        return oreBlocks.contains(block);
    }

    @Override
    public boolean isOre(@NotNull final ItemStack stack)
    {
        if (isBreakableOre(stack))
        {
            return true;
        }
        if (isMineableOre(stack)) // [1.7.10] raw_ore tag removed
        {
            ItemStack smeltingResult = MinecoloniesAPIProxy.getInstance().getFurnaceRecipes().getSmeltingResult(stack);
            return !ItemStackUtils.isEmpty(smeltingResult);
        }

        return false;
    }

    @Override
    public boolean isMineableOre(@NotNull final ItemStack stack)
    {
        // [1.7.10] Tags replaced with OreDictionary check for ores
        return !isEmpty(stack) && net.minecraftforge.oredict.OreDictionary.getOreIDs(stack).length > 0
                   && java.util.Arrays.stream(net.minecraftforge.oredict.OreDictionary.getOreIDs(stack))
                   .anyMatch(id -> net.minecraftforge.oredict.OreDictionary.getOreName(id).startsWith("ore"));
    }

    @Override
    public boolean isBreakableOre(@NotNull final ItemStack stack)
    {
        // [1.7.10] Tags not available; check OreDictionary for "ore*" names pointing to blocks
        if (ItemStackUtils.isEmpty(stack)) return false;
        final Block block = net.minecraft.block.Block.getBlockFromItem(stack.getItem());
        if (block != null && block != net.minecraft.init.Blocks.air)
        {
            // If the ore drops itself, it's not a "breakable" ore (e.g. iron ore dropping iron ore)
            // For 1.7.10, we rely on isMineableOre
            return isMineableOre(stack);
        }
        return false;
    }

    @Override
    public void write(@NotNull final NBTTagCompound compound)
    {
        @NotNull final NBTTagList saplingsLeavesTagList = new net.minecraft.nbt.NBTTagList();
        for (final java.util.Map.Entry<Block, ItemStorage> entry : leavesToSaplingMap.entrySet())
        {
            if (entry.getKey() != null)
            {
                saplingsLeavesTagList.appendTag(writeLeafSaplingEntryToNBT(net.minecraft.block.Block.getIdFromBlock(entry.getKey()), entry.getValue()));
            }
        }
        compound.setTag(TAG_SAP_LEAF, saplingsLeavesTagList);
    }

    @Override
    public void read(@NotNull final NBTTagCompound compound)
    {
        NBTUtils.streamCompound(compound.getTagList(TAG_SAP_LEAF, 10))
          .map(CompatibilityManager::readLeafSaplingEntryFromNBT)
          .filter(key -> key.getA() != null && !leavesToSaplingMap.containsKey(key.getA()) && !leavesToSaplingMap.containsValue(key.getB()))
          .forEach(key -> leavesToSaplingMap.put(key.getA(), key.getB()));
    }

    @Override
    public void connectLeafToSapling(final Block leaf, final ItemStack stack)
    {
        if (!leavesToSaplingMap.containsKey(leaf))
        {
            leavesToSaplingMap.put(leaf, new ItemStorage(stack, false, true));
        }
    }

    // [1.7.10] Not in interface; kept without @Override
    public net.minecraft.creativetab.CreativeTabs getCreativeTab(final ItemStorage checkItem)
    {
        return creativeModeTabMap.get(checkItem);
    }

    @Override
    public int getCreativeTabKey(final ItemStorage checkItem)
    {
        final net.minecraft.creativetab.CreativeTabs creativeTab = creativeModeTabMap.get(checkItem);
        return creativeTab == null ? DEFAULT_TAB_KEY : creativeTab.getTabIndex();
    }

    @Override
    public ImmutableSet<ResourceLocation> getAllMonsters()
    {
        if (monsters.isEmpty())
        {
            Log.getLogger().error("getAllMonsters when empty");
        }
        return monsters;
    }

    //------------------------------- Private Utility Methods -------------------------------//

    /**
     * Calculate all monsters.
     */
    private void discoverMobs()
    {
        Set<ResourceLocation> monsterSet = new HashSet<>();
        // [1.7.10] Iterate EntityList string IDs; check if class implements IMob
        for (final Object obj : net.minecraft.entity.EntityList.stringToClassMapping.entrySet())
        {
            @SuppressWarnings("unchecked")
            final java.util.Map.Entry<String, Class<?>> entry = (java.util.Map.Entry<String, Class<?>>) obj;
            if (net.minecraft.entity.monster.IMob.class.isAssignableFrom(entry.getValue()))
            {
                monsterSet.add(new ResourceLocation(entry.getKey()));
            }
        }
        monsters = ImmutableSet.copyOf(monsterSet);
    }

    /**
     * Create complete list of all existing items, client side only.
     */
    private void discoverAllItems(final World world)
    {
        if (!food.isEmpty())
        {
            return;
        }

        final Set<ItemStorage> tempDuplicates = new HashSet<>();
        final Set<ItemStorage> tempFlowers = new HashSet<>();

        final ImmutableList.Builder<ItemStack> listBuilder = new ImmutableList.Builder<>();

        // [1.7.10] Iterate items from each creative tab
        for (final net.minecraft.creativetab.CreativeTabs tab : net.minecraft.creativetab.CreativeTabs.creativeTabArray)
        {
            if (tab == null) continue;
            final java.util.List<ItemStack> stacks = new java.util.ArrayList<>();
            try
            {
                tab.displayAllReleventItems(stacks); // [1.7.10] typo in original API
            }
            catch (final Exception e)
            {
                Log.getLogger().warn("Error getting items from creative tab " + tab.getTabLabel(), e);
                continue;
            }
            final Object2IntLinkedOpenHashMap<net.minecraft.item.Item> mapping = new Object2IntLinkedOpenHashMap<>();
            for (final ItemStack item : stacks)
            {
                if (ItemStackUtils.isEmpty(item)) continue;
                if (!tempDuplicates.add(new ItemStorage(item)) || mapping.addTo(item.getItem(), 1) > MAX_DEPTH)
                {
                    continue;
                }
                listBuilder.add(item);
                discoverSaplings(item);
                discoverOres(item);
                discoverPlantables(item);
                discoverFood(item);
                discoverFuel(item);
                discoverBeekeeperFlowers(item, tempFlowers);

                creativeModeTabMap.put(new ItemStorage(item), tab);
            }
        }

        discoverFungi();

        beekeeperflowers = ImmutableSet.copyOf(tempFlowers);
        Log.getLogger().info("Finished discovering Ores " + oreBlocks.size() + " " + smeltableOres.size());
        Log.getLogger().info("Finished discovering saplings " + saplings.size());
        Log.getLogger().info("Finished discovering plantables " + plantables.size());
        Log.getLogger().info("Finished discovering food " + edibles.size() + " " + food.size());
        Log.getLogger().info("Finished discovering fuel " + fuel.size());
        Log.getLogger().info("Finished discovering flowers " + beekeeperflowers.size());

        allItems = listBuilder.build();
        Log.getLogger().info("Finished discovering items " + allItems.size());
    }

    /**
     * Discover all flowers for the beekeeper.
     */
    private void discoverBeekeeperFlowers(final ItemStack item, final Set<ItemStorage> tempFlowers)
    {
        // [1.7.10] Use OreDictionary for "flower*" entries
        for (final String name : net.minecraftforge.oredict.OreDictionary.getOreNames())
        {
            if (name.startsWith("flower"))
            {
                for (final ItemStack ore : net.minecraftforge.oredict.OreDictionary.getOres(name))
                {
                    if (net.minecraftforge.oredict.OreDictionary.itemMatches(ore, item, false))
                    {
                        tempFlowers.add(new ItemStorage(item));
                        return;
                    }
                }
            }
        }
    }

    /**
     * Discover ores for the Smelter and Miners.
     */
    private void discoverOres(final ItemStack stack)
    {
        // [1.7.10] Use OreDictionary for "ore*" entries
        for (final int oreId : net.minecraftforge.oredict.OreDictionary.getOreIDs(stack))
        {
            final String oreName = net.minecraftforge.oredict.OreDictionary.getOreName(oreId);
            if (oreName.startsWith("ore") || oreName.startsWith("rawOre"))
            {
                final Block block = net.minecraft.block.Block.getBlockFromItem(stack.getItem());
                if (block != null && block != net.minecraft.init.Blocks.air)
                {
                    oreBlocks.add(block);
                }
                if (!ItemStackUtils.isEmpty(MinecoloniesAPIProxy.getInstance().getFurnaceRecipes().getSmeltingResult(stack)))
                {
                    smeltableOres.add(new ItemStorage(stack));
                }
                return;
            }
        }
    }

    /**
     * Discover saplings from the vanilla Saplings NBTBase, used for the Forester
     */
    private void discoverSaplings(final ItemStack stack)
    {
        // [1.7.10] Use OreDictionary for "treeSapling*" and "mushroom*" entries
        for (final int oreId : net.minecraftforge.oredict.OreDictionary.getOreIDs(stack))
        {
            final String name = net.minecraftforge.oredict.OreDictionary.getOreName(oreId);
            if (name.startsWith("treeSapling") || name.startsWith("mushroom"))
            {
                saplings.add(new ItemStorage(stack, false, true));
                return;
            }
        }
    }

    /**
     * "Discover" associated saplings for fungi; there currently isn't a great way to do this automatically,
     * so it's just hard-coded for now.  (TODO: datapack this in 1.20.4?)
     */
    private void discoverFungi()
    {
        // [1.7.10] Nether fungi blocks don't exist; no-op
    }

    /**
     * Create complete list of compost recipes.
     *
     * @param recipeManager recipe manager
     */
    private void discoverCompostRecipes()
    {
        // [1.7.10] RecipeManager not available; load from custom recipe list if present
        if (compostRecipes.isEmpty())
        {
            discoverCompostRecipes(java.util.Collections.emptyList());
            Log.getLogger().info("Finished discovering compostables " + compostRecipes.size());
        }
    }

    private void discoverCompostRecipes(@NotNull final List<CompostRecipe> recipes)
    {
        for (final CompostRecipe recipe : recipes)
        {
            for (final Item item : recipe.getInputItems())
            {
                final ItemStack stack = new ItemStack(item);
                // there can be duplicates due to overlapping tags.  weakest one wins.
                compostRecipes.merge(stack.getItem(), recipe,
                  (r1, r2) -> r1.getStrength() < r2.getStrength() ? r1 : r2);
            }
        }
    }

    /**
     * Create complete list of plantable items, from the "minecolonies:florist_flowers" NBTBase, for the Florist.
     */
    private void discoverPlantables(final ItemStack stack)
    {
        // [1.7.10] Use OreDictionary for florist flowers; check against known florist flower tag via config/oreDic
        for (final int oreId : net.minecraftforge.oredict.OreDictionary.getOreIDs(stack))
        {
            final String name = net.minecraftforge.oredict.OreDictionary.getOreName(oreId);
            if (name.startsWith("flower") || name.startsWith("plant"))
            {
                plantables.add(new ItemStorage(stack));
                return;
            }
        }
    }

    /**
     * Create complete list of fuel items.
     */
    private void discoverFuel(final ItemStack stack)
    {
        if (net.minecraft.tileentity.TileEntityFurnace.isItemFuel(stack))
        {
            fuel.add(new ItemStorage(stack));
        }
    }

    /**
     * Create complete list of food items.
     */
    private void discoverFood(final ItemStack stack)
    {
        if (ISFOOD.test(stack) || ISCOOKABLE.test(stack))
        {
            food.add(new ItemStorage(stack));
            if (FoodUtils.EDIBLE.test(stack))
            {
                edibles.add(new ItemStorage(stack));
            }
        }
    }

    // [1.7.10] BlockState/NbtUtils do not exist; leaf-sapling NBT helpers stubbed.
    private static NBTTagCompound writeLeafSaplingEntryToNBT(final int blockId, final ItemStorage storage)
    {
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("BlockId", blockId);
        storage.getItemStack().writeToNBT(compound);
        return compound;
    }

    private static com.minecolonies.api.util.Tuple<Block, ItemStorage> readLeafSaplingEntryFromNBT(final NBTTagCompound compound)
    {
        final Block block = net.minecraft.block.Block.getBlockById(compound.getInteger("BlockId"));
        return new com.minecolonies.api.util.Tuple<>(block,
            new ItemStorage(ItemStack.loadItemStackFromNBT(compound), false, true));
    }

    /**
     * Inits compats
     */
    private void discoverModCompat()
    {
        if (cpw.mods.fml.common.Loader.isModLoaded("resourcefulbees"))
        {
            Compatibility.beeHiveCompat = new ResourcefulBeesCompat();
        }
        if (cpw.mods.fml.common.Loader.isModLoaded("tconstruct"))
        {
            Compatibility.tinkersCompat = new TinkersToolHelper();
            Compatibility.tinkersSlimeCompat = new SlimeTreeCheck();
        }
        if (cpw.mods.fml.common.Loader.isModLoaded("dynamictrees"))
        {
            Compatibility.dynamicTreesCompat = new DynamicTreeCompat();
        }
    }

    @Override
    public int getNumberOfSaplings()
    {
        return saplings.size();
    }

    // [1.7.10] getDyeColor removed - DyeColor does not exist in 1.7.10.
}







