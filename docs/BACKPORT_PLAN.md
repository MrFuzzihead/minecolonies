# MineColonies 1.21 → 1.7.10 Backport Plan

## Context

- **Source (upstream):** `origin version/main` — 1.21 Forge mod
- **Target:** `origin main` — 1.7.10 Forge `10.13.4.1614`
- **Java:** Java 25 via `lwjgl3ify` (no bytecode downgrade needed)
- **Build system:** GTNHConvention (`com.gtnewhorizons.gtnhconvention`)
- **Strategy:** Comment out rather than remove incompatible code; be as faithful to original intent as possible.

## Key Dependencies

| Dependency | Purpose |
|---|---|
| `com.falsepattern:endlessids-mc1.7.10:1.7.1` | Extends block/item IDs and block metadata beyond vanilla 4-bit limit (~70 000 states) |
| `com.falsepattern:chunkapi-mc1.7.10:0.8.2` | Chunk NBT extension — used to replace the Chunk capability system |
| `com.github.GTNewHorizons:lwjgl3ify:3.0.16` | Provides LWJGL3 + Java 25 runtime compatibility on 1.7.10 |
| `com.github.GTNewHorizons:ModularUI2:2.3.58-1.7.10` | GUI framework replacing BlockUI |
| Structurize (local jar) | Blueprint/structure system — assumed to be a 1.7.10-compatible build |
| `com.github.GTNewHorizons:NotEnoughItems:2.7.69-GTNH` | Runtime dev tool (NEI) — **replaces JEI** for recipe viewing; the `core/compatibility/jei/` code needs to be rewritten for NEI's API |
| `maven.modrinth:journeymap:v5.2.13` | JourneyMap 1.7.10 — added as a direct dependency; jar is obfuscated so stub classes exist in `src/main/java/journeymap/` for compilation; verify compat layer against actual 1.7.10 JM API at runtime |

## 1.21 → 1.7.10 API Mapping Reference

### Packages

| 1.21 | 1.7.10 |
|---|---|
| `net.minecraft.world.level.block.Block` | `net.minecraft.block.Block` |
| `net.minecraft.world.item.Item` | `net.minecraft.item.Item` |
| `net.minecraft.world.item.BlockItem` | `net.minecraft.item.ItemBlock` |
| `net.minecraft.world.item.ItemStack` | `net.minecraft.item.ItemStack` |
| `net.minecraft.world.level.Level` | `net.minecraft.world.World` |
| `net.minecraft.world.level.BlockGetter` | `net.minecraft.world.IBlockAccess` |
| `net.minecraft.world.entity.player.Player` | `net.minecraft.entity.player.EntityPlayer` |
| `net.minecraft.world.entity.LivingEntity` | `net.minecraft.entity.EntityLivingBase` |
| `net.minecraft.world.level.block.entity.BlockEntity` | `net.minecraft.tileentity.TileEntity` |
| `net.minecraft.core.BlockPos` | `int x, int y, int z` (separate params) |
| `net.minecraft.resources.ResourceLocation` | `net.minecraft.util.ResourceLocation` |
| `net.minecraft.nbt.CompoundTag` | `net.minecraft.nbt.NBTTagCompound` |
| `net.minecraft.network.chat.Component` | `net.minecraft.util.IChatComponent` / `ChatComponentText` |
| `net.minecraft.server.level.ServerPlayer` | `net.minecraft.entity.player.EntityPlayerMP` |
| `net.minecraftforge.registries.IForgeRegistry` | `net.minecraftforge.fml.common.registry.GameRegistry` |
| `net.minecraftforge.network.SimpleChannel` | `net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper` |
| `net.minecraftforge.network.PacketDistributor` | `cpw.mods.fml.common.network.NetworkRegistry` targets |
| `net.minecraft.world.level.block.state.BlockState` | `int` metadata (0–15 native, extended via EndlessIDs) |
| `net.minecraft.world.level.block.state.properties.DirectionProperty` | Encoded in metadata bits |
| `net.minecraft.world.phys.shapes.VoxelShape` | `setBlockBounds(float, float, float, float, float, float)` |
| `net.minecraft.world.InteractionResult` | `boolean` |
| `net.minecraft.world.InteractionHand` | (no equivalent — only main hand in 1.7.10) |
| `net.minecraft.world.item.context.BlockPlaceContext` | Method parameters |
| `net.minecraft.world.phys.BlockHitResult` | `net.minecraft.util.MovingObjectPosition` |

### Block Method Signatures

| 1.21 | 1.7.10 |
|---|---|
| `use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)` | `onBlockActivated(World, int x, int y, int z, EntityPlayer, int side, float hitX, float hitY, float hitZ)` |
| `setPlacedBy(Level, BlockPos, BlockState, LivingEntity, ItemStack)` | `onBlockPlacedBy(World, int x, int y, int z, EntityLivingBase, ItemStack)` |
| `onRemove(BlockState, Level, BlockPos, BlockState, boolean)` | `breakBlock(World, int x, int y, int z, Block, int meta)` |
| `newBlockEntity(BlockPos, BlockState)` | `createTileEntity(World, int meta)` + `hasTileEntity(int meta)` |
| `getShape(BlockState, BlockGetter, BlockPos, CollisionContext)` | `setBlockBoundsBasedOnState(IBlockAccess, int x, int y, int z)` |
| `getDestroyProgress(BlockState, Player, BlockGetter, BlockPos)` | `getPlayerRelativeBlockHardness(EntityPlayer, World, int x, int y, int z)` |
| `createBlockStateDefinition(StateDefinition.Builder)` | Encode in metadata; override `getMetaFromBlock()` / `getBlockFromMeta()` |
| `rotate(BlockState, Rotation)` | Manual metadata rotation |

### Mod Lifecycle

| 1.21 | 1.7.10 |
|---|---|
| Constructor-time `DeferredRegister.register()` calls | `FMLPreInitializationEvent` — register blocks, items, TEs via `GameRegistry` |
| `FMLCommonSetupEvent` | `FMLInitializationEvent` |
| `FMLLoadCompleteEvent` | `FMLPostInitializationEvent` |
| `RegisterCapabilitiesEvent` | N/A — use `IExtendedEntityProperties` / ChunkAPI / `WorldSavedData` |
| `EntityAttributeCreationEvent` | Entity attribute setup in entity class constructors |
| `RegisterEvent` (recipe serializers) | `FMLInitializationEvent` — `GameRegistry.addRecipe()` |
| `NewRegistryEvent` | N/A — use `GameRegistry` or custom maps |
| `DistExecutor` / `Dist.CLIENT` | `@SideOnly(Side.CLIENT)` / `FMLEnvironment.side.isClient()` |

### Capability System Replacements

| Original Capability | 1.7.10 Replacement |
|---|---|
| `IChunkmanagerCapability` (per-chunk) | **ChunkAPI** — attach NBT data to chunks via `IChunkDataHandler` |
| `IColonyTagCapability` (per-chunk, colony ownership) | **ChunkAPI** — store colony ownership tags in chunk NBT |
| `IColonyManagerCapability` (world-level) | **`WorldSavedData`** subclass stored in `MapStorage` |

### Networking

| 1.21 | 1.7.10 |
|---|---|
| `SimpleChannel` (Forge network) | `SimpleNetworkWrapper` (`NetworkRegistry.INSTANCE.newSimpleChannel`) |
| `FriendlyByteBuf` | `net.minecraft.network.PacketBuffer` (or raw `ByteBuf`) |
| `PacketDistributor.PLAYER` | `channel.sendTo(message, player.playerNetServerHandler)` |
| `PacketDistributor.ALL` | `channel.sendToAll(message)` |
| `PacketDistributor.DIMENSION` | `channel.sendToDimension(message, dimId)` |
| `PacketDistributor.NEAR` | `channel.sendToAllAround(message, point)` |
| `PacketDistributor.TRACKING_ENTITY` | `channel.sendToAllTracking(message, entity)` |
| `IMessage.onExecute(ctx, isClient)` | Split into `IMessageHandler<MSG, IMessage>` server/client handler classes |

### GUI / UI

| 1.21 (BlockUI) | 1.7.10 (ModularUI2) |
|---|---|
| `AbstractBuildingWindow extends AbstractWindow` | `ModularWindow` factory via `UIFactory` |
| `@OnlyIn(Dist.CLIENT)` GUI classes | `@SideOnly(Side.CLIENT)` |
| XML/JSON layouts | ModularUI2 programmatic widget API |
| `WindowResearchTree` | Port to `ModularWindow` with `DrawableWidget` canvas |

### Registration

| 1.21 | 1.7.10 |
|---|---|
| `DeferredRegister<Block>` | `GameRegistry.registerBlock(block, ItemBlock.class, name)` in preInit |
| `DeferredRegister<Item>` | `GameRegistry.registerItem(item, name)` in preInit |
| `DeferredRegister<TileEntity>` | `GameRegistry.registerTileEntity(cls, name)` in preInit |
| `DeferredRegister<EntityType>` | `EntityRegistry.registerModEntity(cls, name, id, mod, trackRange, updateFreq, sendVelocity)` |
| `DeferredRegister<Enchantment>` | Direct `EnchantmentHelper` registration |
| `DeferredRegister<SoundEvent>` | Reference `ResourceLocation` directly; register in mcmod.info sounds.json |
| Creative tab | `new CreativeTabs(tabName) { ... }` |

### Block Metadata / Direction Encoding

In place of BlockState with a `FACING` DirectionProperty, encode facing in metadata bits:

```
meta 0 = NORTH
meta 1 = SOUTH
meta 2 = WEST
meta 3 = EAST
```

With EndlessIDs, additional bits are available for other properties (e.g. building level, activation state).

---

## Implementation Phases

### Phase 0 — Build Infrastructure ✅ (Already Done)

- `gradle.properties`: `minecraftVersion = 1.7.10`, `forgeVersion = 10.13.4.1614`
- GTNHConvention build script
- Dependencies: EndlessIDs, ChunkAPI, lwjgl3ify, ModularUI2, Structurize (local jar)

### Phase 1 — Block & Item Layer

Files to port (comment out 1.21-specific code, provide 1.7.10 skeleton):

1. `api/blocks/interfaces/IBlockMinecolonies.java` — remove `IForgeRegistry`, use `GameRegistry` pattern
2. `api/blocks/AbstractBlockMinecolonies.java` — extend 1.7.10 `Block`
3. `api/blocks/AbstractBlockMinecoloniesDirectional.java` — encode FACING in metadata
4. `api/blocks/AbstractBlockMinecoloniesHorizontal.java` — encode horizontal facing in metadata
5. `api/blocks/AbstractColonyBlock.java` — main hut/building block base; port all block methods
6. `api/blocks/AbstractBlockHut.java` — blueprint anchor interfaces (depends on Structurize compat)
7. `core/blocks/**` — all concrete block implementations
8. `apiimp/initializer/ModBlocksInitializer.java` — `GameRegistry.registerBlock()` in preInit
9. `api/items/**` — item classes (swap `Item.Properties` builder, `BlockItem` → `ItemBlock`)
10. `apiimp/initializer/ModItemsInitializer.java` — `GameRegistry.registerItem()` in preInit

### Phase 2 — Mod Entry Point & Lifecycle

Files to port:

1. `core/MineColonies.java` — replace 1.21 `@Mod` constructor pattern with `@Mod.EventHandler` methods
2. `apiimp/CommonMinecoloniesAPIImpl.java` — port to init-time registration
3. `apiimp/ClientMinecoloniesAPIImpl.java` — `@SideOnly(Side.CLIENT)`
4. `api/configuration/Configuration.java` — replace ForgeConfigSpec with Forge 1.7.10 config (`Configuration` from `net.minecraftforge.common`)
5. Creative tabs — replace `DeferredRegister` with direct `CreativeTabs` instantiation

### Phase 3 — TileEntity / Block Entity Layer

Files to port:

1. `api/tileentities/**` — replace `BlockEntity` with `TileEntity`; port NBT read/write (`readFromNBT` / `writeToNBT`)
2. `core/tileentities/**` — concrete TE implementations
3. `apiimp/initializer/TileEntityInitializer.java` — `GameRegistry.registerTileEntity()`
4. Replace `BlockEntityTicker` tick pattern with `ITickableTileEntity` → override `updateEntity()` in 1.7.10

### Phase 4 — Capability System

Replace modern Capability API with 1.7.10-compatible equivalents:

1. **`IColonyManagerCapability`** → `ColonyManagerWorldSavedData extends WorldSavedData`
   - Stored per-world via `world.mapStorage.setData(id, data)` / `getData(id)`
   - Register in `FMLServerStartingEvent`
2. **`IChunkmanagerCapability` + `IColonyTagCapability`** → ChunkAPI (`IChunkDataHandler`)
   - Implement `IChunkDataHandler` to read/write colony chunk data to chunk NBT
   - Register handler in `FMLPreInitializationEvent`
3. Remove all `CapabilityManager`, `CapabilityToken`, `RegisterCapabilitiesEvent` references

### Phase 5 — Entity Layer

Files to port:

1. `api/entity/**`, `core/entity/**` — replace modern entity classes with 1.7.10 equivalents
2. Entity registration: `EntityRegistry.registerModEntity()` instead of `DeferredRegister<EntityType>`
3. Entity attributes: set in entity constructor (`getAttributeMap().registerAttribute(...)`) instead of `EntityAttributeCreationEvent`
4. Remove `EntityType<?>` references; use direct class references
5. NBT: `readEntityFromNBT` / `writeEntityToNBT` method signatures
6. Rendering: `@SideOnly(Side.CLIENT)` renderer registration via `RenderingRegistry.registerEntityRenderingHandler()`

### Phase 6 — Networking Layer

Files to port:

1. `core/network/NetworkChannel.java` — replace `SimpleChannel` with `SimpleNetworkWrapper`
2. `api/network/IMessage.java` — split into message + handler pair (implement `IMessageHandler<MSG, IMessage>`)
3. All `core/network/messages/**` — port `fromBytes(FriendlyByteBuf)` / `toBytes(FriendlyByteBuf)` to 1.7.10 `ByteBuf`
4. Replace `PacketDistributor` send targets
5. Keep custom split-packet logic (`SplitPacketMessage`) with updated byte buffer API

### Phase 7 — Colony & Game Logic

Large, domain-specific logic that uses many APIs. Port file-by-file:

1. `api/colony/**`, `core/colony/**` — core colony data model
   - Replace `BlockPos` with `ChunkCoordinates` or store as 3 ints in NBT
   - Replace `Level`/`ServerLevel` with `World`/`WorldServer`
   - Replace `CompoundTag` with `NBTTagCompound`
2. `core/event/**` — event handlers, swap to 1.7.10 Forge event bus (`@SubscribeEvent` still works via GTNH)
3. `core/generation/**` — world gen: replace `BiomeProvider`, `ChunkGenerator` with 1.7.10 `IWorldGenerator`
4. `core/research/**` — research tree (mostly pure Java data structures, lower API surface)
5. `core/colony/requestsystem/**` — request system (pure Java, should be mostly portable)
6. `core/placementhandlers/**` — structure placement (depends on Structurize compat)
7. Comment out `core/structures/MineColoniesStructures.java` (Jigsaw structures — 1.7.10 has no equivalent)

### Phase 8 — Recipe & Crafting System

1. Replace `IRecipeSerializer` / `RecipeType` / `DeferredRegister` with `GameRegistry.addShapedRecipe()` / `addShapelessRecipe()`
2. Comment out `FoodIngredient`, `PlantIngredient`, `CountedIngredient` custom ingredient serializers (no equivalent in 1.7.10) — replace with standard `ItemStack` matching
3. Port `core/recipes/**`

### Phase 9 — UI Layer (ModularUI2)

Replace BlockUI windows with ModularUI2 equivalents:

1. Implement `IUIFactory` for each building type that currently has a `WindowHut*` class
2. Port `AbstractBuildingWindow`, `AbstractBuildingMainWindow` → `ModularWindow` factory
3. For each `WindowHut*.java`:
   - Replace XML/`@Bindable` pattern with ModularUI2 widget builders
   - Replace `BlockUI` text/button/list widgets with ModularUI2 equivalents
4. Comment out all `@OnlyIn(Dist.CLIENT)` and replace with `@SideOnly(Side.CLIENT)`
5. Port research tree rendering (`WindowResearchTree`) to a ModularUI2 custom drawable widget

### Phase 10 — Stubs to Comment Out Entirely

These 1.21 features have no equivalent in 1.7.10 and should be commented out with `// TODO: no 1.7.10 equivalent`:

| System | Files |
|---|---|
| Advancements | `api/advancements/**` |
| Loot tables | `api/loot/**`, `core/loot/**` |
| Tags (`ModTags`) | `api/items/ModTags.java` (keep class, comment body) |
| Jigsaw structures | `core/structures/MineColoniesStructures.java` |
| Data generators | `src/datagen/**` |
| Banner patterns | `api/items/ModBannerPatterns.java` |
| Command argument types | `core/commands/arguments/ModArgumentTypes.java` |
| Particle types | `apiimp/initializer/ModParticleTypesInitializer.java` |
| Model types | `apiimp/initializer/ModModelTypeInitializer.java` |

---

## Decisions Made

1. **Blockstates → Metadata**: Use native metadata 0–15 for simple state (facing, activation). EndlessIDs is available for blocks needing more state, but the primary use is extended block/item IDs.
2. **Capability → ChunkAPI + WorldSavedData**: ChunkAPI's `IChunkDataHandler` replaces chunk capabilities; `WorldSavedData` replaces world-level capabilities.
3. **GUI → ModularUI2**: All BlockUI windows will be ported to ModularUI2 programmatic widget API.
4. **Java 25**: Keep modern Java syntax (records, sealed classes, text blocks, etc.) — lwjgl3ify handles runtime compatibility.
5. **Structurize**: Treat the included local jar as the 1.7.10-compatible Structurize; adapt calls to its API as needed.
6. **No Reflection**: Follow the coding guidelines — no reflection, no bytecode manipulation. Access Transformers (AT) should be used if private/protected access is needed.
7. **NEI replaces JEI**: The `core/compatibility/jei/` compatibility layer was written for the modern JEI API. On 1.7.10, `NotEnoughItems` (NEI) is used instead. The JEI compat code needs to be rewritten to use NEI's API (`codechicken.nei.*`). Stub classes exist for compilation only.
8. **JourneyMap (1.7.10 native)**: JourneyMap v5.2.13 for 1.7.10 is added as `maven.modrinth:journeymap:v5.2.13`. The jar is obfuscated (MCP-mapped), so stub sources exist in `src/main/java/journeymap/` for compilation purposes. At runtime the real JM classes take precedence. The compat code in `core/compatibility/journeymap/` should work once the stub/real JM API classes are reconciled.

---

## File-by-File Progress Tracker

Track implementation status here as work progresses.

### Phase 1 — Block & Item Layer
- [x] `api/blocks/interfaces/IBlockMinecolonies.java` — removed IForgeRegistry; registerBlock() no-arg; registerBlockItem() no-op
- [x] `api/blocks/interfaces/ITickableBlockMinecolonies.java` — marker interface; ticking moved to TileEntity.updateEntity()
- [x] `api/blocks/AbstractBlockMinecolonies.java` — Material replaces Properties; GameRegistry.registerBlock() inside registerBlock()
- [x] `api/blocks/AbstractBlockMinecoloniesDirectional.java` — directional facing in metadata (META_DOWN/UP/NORTH/SOUTH/WEST/EAST)
- [x] `api/blocks/AbstractBlockMinecoloniesHorizontal.java` — horizontal facing in metadata (META_SOUTH/WEST/NORTH/EAST 0-3)
- [x] `api/blocks/AbstractColonyBlock.java` — all block API methods ported; BlockState→metadata; BlockPos→xyz; Level→World
- [x] `api/blocks/AbstractBlockHut.java` — BlockPos→xyz; Level→World; Player→EntityPlayer; CompoundTag→NBTTagCompound; Component→String; @OnlyIn→@SideOnly; registerBlockItem() no-arg; Structurize interface methods tentatively ported
- [x] `core/blocks/**` — concrete block implementations (all blocks ported: Material constructors, createTileEntity, onBlockActivated, onBlockPlacedBy, breakBlock, setBlockBoundsBasedOnState; VoxelShape/BlockState/InteractionResult/EntityBlock removed)
- [x] `apiimp/initializer/ModBlocksInitializer.java` — RegisterEvent→direct init(); IForgeRegistry→GameRegistry; registerBlock() no-arg
- [x] `api/items/ItemBlockHut.java` — BlockItem→ItemBlock; appendHoverText→addInformation; Component→String; EnumChatFormatting; world.dimension()→dimensionId
- [x] `apiimp/initializer/ModItemsInitializer.java` — RegisterEvent→static init(); IForgeRegistry→GameRegistry.registerItem(); FoodProperties.Builder/ArmorItem.Type/ForgeSpawnEggItem/ComposterBlock removed

### Phase 2 — Mod Entry Point & Lifecycle
- [x] `core/MineColonies.java` — DeferredRegister→@Mod.EventHandler preInit/init/postInit; DistExecutor→sided proxy; capabilities commented out; Advancements/Tags/Loot/Jigsaw/BannerPatterns commented out; ModList→Loader.isModLoaded()
- [x] `apiimp/CommonMinecoloniesAPIImpl.java` — IForgeRegistry<T>→SimpleRegistry<T>; NewRegistryEvent/RegistryBuilder removed; registries initialised as final fields
- [x] `apiimp/ClientMinecoloniesAPIImpl.java` — NewRegistryEvent removed; onRegistryNewRegistry() removed
- [x] `api/IMinecoloniesAPI.java` — IForgeRegistry<T>→SimpleRegistry<T>; onRegistryNewRegistry(NewRegistryEvent) removed
- [x] `api/registry/SimpleRegistry.java` — new class; replacement for IForgeRegistry<T> for mod-internal custom registries
- [x] `api/configuration/AbstractConfiguration.java` — ForgeConfigSpec.Builder→Forge 1.7.10 Configuration; new ConfigValue<T>/BooleanValue/IntValue/DoubleValue wrappers preserving .get() call-sites
- [x] `api/configuration/Configuration.java` — three separate .cfg files (server/client/common) via FMLPreInitializationEvent config directory
- [x] `api/configuration/ServerConfiguration.java` — all fields ported to wrapper types; constructor takes Configuration not Builder
- [x] `api/configuration/ClientConfiguration.java` — ported to wrapper types
- [x] `api/configuration/CommonConfiguration.java` — ported to wrapper types
- [x] `api/creativetab/ModCreativeTabs.java` — ported to 1.7.10 CreativeTabs; DeferredRegister/CreativeModeTab removed; safeAddSpawnEgg removed
### Phase 3 — TileEntity / Block Entity Layer
- [x] `api/tileentities/ITickable.java` — simplified to parameterless tick(); 1.7.10 TEs call from updateEntity()
- [x] `api/tileentities/MinecoloniesTileEntities.java` — RegistryObject<BlockEntityType>→Class<?>; no type registry in 1.7.10
- [x] `api/tileentities/AbstractTileEntityBarrel.java` — BlockEntity→TileEntity; removed BlockEntityType/BlockPos/BlockState params
- [x] `api/tileentities/AbstractTileEntityScarecrow.java` — BlockEntity→TileEntity; removed BlockEntityType/BlockPos/BlockState params
- [x] `api/tileentities/AbstractTileEntityNamedGrave.java` — BlockEntity→TileEntity; CompoundTag→NBTTagCompound; load/saveAdditional→readFromNBT/writeToNBT; removed ClientboundBlockEntityDataPacket/Connection
- [x] `api/tileentities/AbstractTileEntityRack.java` — BlockEntity→TileEntity; MenuProvider removed; ItemStackHandler shim used; buildingPos→int xyz triple; level→worldObj; setChanged→markDirty
- [x] `api/tileentities/AbstractTileEntityGrave.java` — extends TileEntityRack; removed BlockEntityType/BlockPos/BlockState/MenuProvider
- [x] `api/tileentities/AbstractTileEntityPlantationField.java` — BlockEntity→TileEntity; ResourceKey<Level>→Integer; List<BlockPos>→List<int[]>; Rotation→int; removed ClientboundBlockEntityDataPacket
- [x] `api/tileentities/AbstractTileEntityColonyBuilding.java` — BlockEntity→TileEntity; BlockPos→int[]; ICapabilityProvider→IInventory; CompoundTag→NBTTagCompound; load/saveAdditional→readFromNBT/writeToNBT
- [x] `api/tileentities/AbstractTileEntityWareHouse.java` — removed BlockEntityType/BlockPos/BlockState; Tuple<ItemStack,BlockPos>→Tuple<ItemStack,int[]>
- [x] `apiimp/initializer/TileEntityInitializer.java` — DeferredRegister→GameRegistry.registerTileEntity(); static init() method
- [x] `core/tileentities/TileEntityRack.java` — removed IMateriallyTexturedBlockEntity/LazyOptional/ForgeCapabilities/ModelData/MenuProvider/ClientboundBlockEntityDataPacket; CompoundTag→NBTTagCompound; BlockPos→xyz; level→worldObj; setChanged→markDirty
- [x] `api/util/WorldUtil.java` — Level→World; BlockPos→int xyz; ServerLevel→WorldServer; AABB→AxisAlignedBB; DimensionType→dimensionId; GameRules→getGameRuleBooleanValue; removed ChunkPos/ChunkHolder/ServerChunkCache
- [x] `api/util/BlockPosUtil.java` — BlockPos→int[]/xyz; CompoundTag→NBTTagCompound; ListTag→NBTTagList; Level→World; removed Vec3/BoundingBox/BlockGetter/loot APIs; all distance helpers ported
- [x] `net/minecraftforge/items/IItemHandler.java` (new shim) — replaces Forge Cap IItemHandler
- [x] `net/minecraftforge/items/IItemHandlerModifiable.java` (new shim) — extends IItemHandler
- [x] `net/minecraftforge/items/ItemStackHandler.java` (new shim) — array-backed implementation
- [x] `core/tileentities/TileEntityColonyBuilding.java` — LazyOptional/ForgeCapabilities/MenuProvider/SignText removed; BlockPos→xyz; readFromNBT/writeToNBT; updateEntity()->tick()
- [x] `core/tileentities/TileEntityBarrel.java` — Direction→int; Level→World; CompoundTag→NBTTagCompound; EntityItem; updateEntity()->tick()
- [x] `core/tileentities/TileEntityGrave.java` — BlockEntityType/ClientboundBlockEntityDataPacket/MenuProvider removed; readFromNBT/writeToNBT; Blocks.air
- [x] `core/tileentities/TileEntityNamedGrave.java` — minimal stub; default no-arg constructor
- [x] `core/tileentities/TileEntityScarecrow.java` — Direction→int; level→worldObj; readFromNBT/writeToNBT
- [x] `core/tileentities/TileEntityPlantationField.java` — BlockPos→int[]; RotationMirror→int; ResourceKey<Level>→Integer; readFromNBT/writeToNBT; IBlueprintDataProviderBE retained
- [x] `core/tileentities/TileEntityWareHouse.java` — BlockPos→int[]; getContainers() Set<int[]>; Tuple<ItemStack,BlockPos>→Tuple<ItemStack,int[]>; level→worldObj
- [x] `core/tileentities/TileEntityCompostedDirt.java` — BlockEntity→TileEntity; flower growing stubbed for 1.7.10
- [x] `core/tileentities/TileEntityEnchanter.java` — extends TileEntityColonyBuilding; animation logic kept; level→worldObj
- [x] `core/tileentities/TileEntityStash.java` — NotifyingRackInventory kept; createPickupRequest wired
- [x] `core/tileentities/TileEntityColonyFlag.java` — BannerPattern/Holder removed; patterns as raw NBTTagList
- [x] `core/tileentities/TileEntityColonySign.java` — BlockEntity→TileEntity; anchor→int[]; tick logic stubbed
- [x] `core/tileentities/TileEntityDecorationController.java` — IRotatableBlockEntity removed; BlockPos→int[]; IBlueprintDataProviderBE retained

### Phase 4 — Capability System
- [x] `ColonyManagerWorldSavedData` (new class) — WorldSavedData wrapping IColonyManagerCapability.Impl; static getOrCreate(World) accessor replaces world.getCapability(COLONY_MANAGER_CAP,…)
- [x] `ChunkManagerWorldSavedData` (new class) — WorldSavedData wrapping IChunkmanagerCapability.Impl; replaces world.getCapability(CHUNK_STORAGE_UPDATE_CAP,…)
- [x] `ColonyChunkDataHandler` (new class) — ChunkAPI IChunkDataHandler; WeakHashMap<Chunk,IColonyTagCapability>; static getColonyTagCapability(chunk) replaces chunk.getCapability(CLOSE_COLONY_CAP,…)
- [x] `api/colony/IColony.java` — removed CLOSE_COLONY_CAP / CapabilityManager / CapabilityToken imports; added migration comment
- [x] `api/colony/IColonyTagCapability.java` — removed Capability<>/Direction params from Storage; pass Object (null) at call-sites
- [x] `api/colony/IChunkmanagerCapability.java` — removed Capability<>/Direction params from Storage
- [x] `core/colony/IColonyManagerCapability.java` — removed Capability<> param from Storage
- [x] `core/event/capabilityproviders/MinecoloniesChunkCapabilityProvider.java` — stubbed (replaced by ColonyChunkDataHandler)
- [x] `core/event/capabilityproviders/MinecoloniesWorldCapabilityProvider.java` — stubbed (replaced by ChunkManagerWorldSavedData)
- [x] `core/event/capabilityproviders/MinecoloniesWorldColonyManagerCapabilityProvider.java` — stubbed (replaced by ColonyManagerWorldSavedData)
- [x] `core/util/ChunkDataHelper.java` — all getCapability() call-sites updated; BlockPos→int xyz; LevelChunk→Chunk; Level→World; ChunkPos→int pair; SectionPos→>>4; chunkPosAsLong() helper added
- [x] `api/util/ColonyUtils.java` — getOwningColony/getAllClaimingBuildings/getStaticClaims/getChunkCapData updated to ColonyChunkDataHandler; calculateCorners uses int[] arrays; Level→World
- [x] `core/colony/ColonyManager.java` — all COLONY_MANAGER_CAP accesses replaced with ColonyManagerWorldSavedData; ResourceKey<Level>→int; BlockPos→xyz; LevelChunk→Chunk; Level→World; FriendlyByteBuf→ByteBuf; ServerPlayer→EntityPlayerMP; CompoundTag→NBTTagCompound; colonyViews map keyed by int dim ID

### Phase 5 — Entity Layer
- [x] `api/entity/ModEntities.java` — EntityType<X>→Class<X>; all raider/citizen/camp entity fields
- [x] `apiimp/initializer/EntityInitializer.java` — EntityType/DeferredRegister→EntityRegistry.registerModEntity(); static init(Object modInstance)
- [x] `api/entity/CustomGoalSelector.java` — GoalSelector (1.21)→wraps EntityAITasks; addGoal/removeGoal delegate to addTask/removeTask
- [x] `api/entity/other/AbstractFastMinecoloniesEntity.java` — PathfinderMob→EntityCreature; EntityType param removed; 1.21 overrides replaced with 1.7.10 equivalents
- [x] `api/entity/citizen/AbstractCivilianEntity.java` — EntityType/Npc/BlockPos/SoundEvent removed; uses World/posXYZ/applyEntityCollision/mountEntity
- [x] `api/entity/mobs/AbstractEntityMinecoloniesMonster.java` — EntityType/AttributeSupplier/ServerLevel/Enemy removed; uses EntityCreature/NBTTagCompound/SharedMonsterAttributes; onLivingUpdate replaces aiStep; spawnPos→int[]; goalSelector/targetSelector fields added
- [x] `api/entity/mobs/AbstractEntityMinecoloniesRaider.java` — EntityType/ChunkPos/MobEffects/DamageTypes removed; Potion/PotionEffect/Chunk/NBTTagCompound; onLivingUpdate; setDead(); writeEntityToNBT/readEntityFromNBT
- [x] `api/entity/mobs/barbarians/AbstractEntityBarbarian.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/barbarians/AbstractEntityBarbarianRaider.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/amazons/AbstractEntityAmazon.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/amazons/AbstractEntityAmazonRaider.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/drownedpirate/AbstractDrownedEntityPirate.java` — World constructor; placeholder sound
- [x] `api/entity/mobs/drownedpirate/AbstractDrownedEntityPirateRaider.java` — World constructor; placeholder sound
- [x] `api/entity/mobs/egyptians/AbstractEntityEgyptian.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/egyptians/AbstractEntityEgyptianRaider.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/pirates/AbstractEntityPirate.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/pirates/AbstractEntityPirateRaider.java` — World constructor; playLivingSound
- [x] `api/entity/mobs/vikings/AbstractEntityNorsemen.java` — World constructor; playLivingSound; getSoundPitch
- [x] `api/entity/mobs/vikings/AbstractEntityNorsemenRaider.java` — World constructor; playLivingSound; getSoundPitch
- [x] All concrete raider entities (barbarians/pirates/egyptians/norsemen/amazons/drownedpirates — raider + camp variants) — World constructor; SharedMonsterAttributes; setCustomNameTag
- [x] `api/entity/citizen/AbstractEntityCitizen.java` — EntityDataAccessor/SynchedEntityData→DataWatcher (IDs 17-30); MenuProvider removed; EntityType removed; onLivingUpdate; applyEntityCollision; interact; entityInit; setupBaseAttributes
- [x] `core/entity/citizen/EntityCitizen.java` — EntityType/MenuProvider/Capabilities removed; World constructor; DamageSource/ChunkPos/BlockPos ported; onLivingUpdate; writeEntityToNBT/readEntityFromNBT; attackEntityFrom; onDeath; setDead
- [x] `core/entity/visitor/VisitorCitizen.java` — EntityType removed; World constructor; onLivingUpdate; attackEntityFrom; onDeath; setDead; 1.7.10 NBT methods
- [x] `core/entity/mobs/EntityMercenary.java` — EntityType/Npc/AttributeSupplier removed; World constructor; 1.7.10 equipment/enchantment APIs; EntityAINearestAttackableTarget; onLivingUpdate; setDead; spawnEntityInWorld
- [x] `core/entity/other/SittingEntity.java` — Entity base; onUpdate; setDead; mountEntity; spawnEntityInWorld; dimensionId keying
- [x] `core/entity/other/CustomArrowEntity.java` — EntityArrow base; onUpdate; writeEntityToNBT/readEntityFromNBT stubs; no EntityType
- [x] `core/entity/other/FireArrowEntity.java` — extends CustomArrowEntity; World+shooter constructor; setFire
- [x] `core/entity/other/SpearEntity.java` — EntityArrow base (ThrownTrident N/A in 1.7.10); onUpdate; NBT stubs
- [x] `core/entity/other/DruidPotionEntity.java` — EntityPotion base; PotionEffect; Potion; func_70665_d splash override; func_70186_c throw
- [x] `core/entity/other/NewBobberEntity.java` — stub extends EntityFishHook; TODO full fishing port
- [x] `core/entity/other/cavalry/CavalryHorseEntity.java` — stub extends EntityHorse; basic attributes/NBT; TODO full port

### Phase 6 — Networking Layer
- [x] `api/network/IMessage.java` — extends FML 1.7.10 IMessage; ByteBuf↔PacketBuffer bridges; onExecute(MessageContext,boolean); getExecutionSide()→Boolean
- [x] `core/network/NetworkChannel.java` — SimpleChannel→SimpleNetworkWrapper; PacketDistributor→sendTo/sendToAll/sendToDimension/sendToAllAround/sendToAllTracking; UniversalMessageHandler dispatches to IMessage.onExecute(); EntityPlayerMP; Chunk
- [x] `core/network/messages/splitting/SplitPacketMessage.java` — MessageContext; removed LogicalSide/NetworkEvent; Boolean getExecutionSide() side check
- [x] `core/network/messages/server/AbstractColonyServerMessage.java` — MessageContext; int dimensionId (buf.readInt/writeInt); Boolean.TRUE server-only; ctx.getServerHandler().playerEntity; EntityPlayerMP; player.getGameProfile().getId()
- [x] `core/network/messages/server/AbstractBuildingServerMessage.java` — MessageContext; buildingId int[] written as 3 ints instead of BlockPos
- [x] `core/network/messages/**` (136 files) — bulk: NetworkEvent.Context→MessageContext; LogicalSide→Boolean; ctx.getSender()→ctx.getServerHandler().playerEntity; ctx.enqueueWork→flattened; FriendlyByteBuf→PacketBuffer; ServerPlayer→EntityPlayerMP

### Phase 7 — Colony & Game Logic
- [x] `api/colony/IColony.java` — removed Style/ChunkPos/Animal/Player 1.21 imports; kept BlockState (valid 1.7.10)
- [x] `api/util/InventoryUtils.java` — removed FoodProperties/Player 1.21 imports; Item.byBlock→Item.getItemFromBlock; FoodProperties→ItemFood for transferFoodUpToSaturation
- [x] `api/compatibility/CompatibilityManager.java` — removed BlockState 1.21 import
- [ ] `core/colony/**` (remaining files)
- [ ] `core/event/**`
- [ ] `core/generation/**`
- [ ] `core/research/**`

### Phase 8 — Recipe & Crafting
- [ ] `core/recipes/**`
- [ ] `api/crafting/**`
- [ ] `apiimp/initializer/ModRecipeSerializerInitializer.java`

### Phase 9 — UI Layer
- [ ] `core/client/gui/**` (all WindowHut* files)

### Phase 10 — Stubs / Comment-Outs
- [x] `api/advancements/AbstractCriterionTrigger.java` — stubbed; removed 1.21 criterion imports
- [x] `api/advancements/CriterionListeners.java` — stubbed; all methods no-ops
- [x] `api/advancements/AdvancementTriggers.java` — stubbed; preInit() no-op
- [x] `api/advancements/**/` — all 42 trigger/instance/listener files auto-stubbed
- [x] `api/loot/ModLootConditions.java` — stubbed; DeferredRegister/LootItemConditionType removed
- [x] `api/loot/EntityInBiomeTag.java` — stubbed
- [x] `api/loot/ResearchUnlocked.java` — stubbed
- [x] `api/loot/ModLootTables.java` — ResourceLocation import fixed to 1.7.10
- [x] `core/loot/SupplyLoot.java` — stubbed; LootModifier/GlobalLootModifier removed
- [x] `api/items/ModTags.java` — stubbed; all TagKey fields commented out; crafter maps kept empty
- [x] `api/items/ModBannerPatterns.java` — stubbed; BannerPattern registry removed
- [x] `core/structures/MineColoniesStructures.java` — stubbed; StructureType/DeferredRegister removed
- [x] `core/structures/EmptyColonyStructure.java` — stubbed; Codec/Jigsaw APIs removed
- [x] `core/commands/arguments/ModArgumentTypes.java` — stubbed; ArgumentTypeInfo does not exist in 1.7.10
- [x] `apiimp/initializer/ModParticleTypesInitializer.java` — stubbed; ParticleType registry does not exist in 1.7.10
- [x] `apiimp/initializer/ModModelTypeInitializer.java` — stubbed; model type registry does not exist in 1.7.10
- [ ] `src/datagen/**`


