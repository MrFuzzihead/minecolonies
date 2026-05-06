package com.minecolonies.api.blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.InteractionResult;
import net.minecraft.util.Direction;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.tileentity.BlockEntity; // [1.7.10] alias -> TileEntity
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.blocks.interfaces.IBlockMinecolonies;
import com.minecolonies.api.blocks.interfaces.ITickableBlockMinecolonies;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.api.colony.buildings.views.IBuildingView;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.entity.ai.workers.util.IBuilderUndestroyable;
import com.minecolonies.api.items.ItemBlockHut;
import com.minecolonies.api.tileentities.AbstractTileEntityColonyBuilding;
import com.minecolonies.api.tileentities.MinecoloniesTileEntities;
import com.minecolonies.api.util.MessageUtils;
import com.minecolonies.api.util.ColonyUtils;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.tileentities.TileEntityColonyBuilding;

// [1.7.10 BACKPORT] 1.7.10 Minecraft/Forge API imports
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// [1.7.10 BACKPORT] Removed 1.21-only imports:
//   net.minecraft.core.int[]                          — replaced by int x,y,z params
//   net.minecraft.core.Direction                         — replaced by metadata int (META_*)
//   net.minecraft.world.int /* InteractionHand */                  — no equivalent; 1.7.10 only has main hand
//   net.minecraft.world.InteractionResult                — replaced by boolean
//   net.minecraft.world.item.context.BlockPlaceContext   — replaced by entity yaw in onBlockPlacedBy
//   net.minecraft.world.level.blockGetter                — replaced by IBlockAccess / World
//   net.minecraft.world.level.block.*                    — replaced by 1.7.10 Block
//   net.minecraft.world.level.block.state.*              — replaced by metadata int
//   net.minecraft.world.World.chunk.LevelChunk           — replaced by Chunk
//   net.minecraft.world.World.material.MapColor          — no equivalent; Material carries this
//   net.minecraft.world.phys.BlockHitResult              — replaced by MovingObjectPosition
//   net.minecraft.world.phys.shapes.*                    — replaced by setBlockBounds()
//   net.minecraftforge.registries.IForgeRegistry         — replaced by GameRegistry

import static com.minecolonies.api.util.constant.BuildingConstants.DEACTIVATED;
import static com.minecolonies.api.util.constant.TranslationConstants.*;

/**
 * Base class for all blocks that have a functionality within a colony.
 * Applies to both buildings (huts) and functional blocks like post-box/stash.
 *
 * <p><b>1.7.10 Backport Notes:</b>
 * <ul>
 *   <li>Block facing is stored in metadata bits 0-1 (4 horizontal directions):
 *       0=SOUTH, 1=WEST, 2=NORTH, 3=EAST. Use {@link #getMetaFromPlacer(EntityLivingBase)}.</li>
 *   <li>{@code BlockState} / {@code DirectionProperty FACING} are replaced by metadata.</li>
 *   <li>{@code BlockEntity}/{@code newBlockEntity()} → {@link #createTileEntity(World, int)} +
 *       {@link #hasTileEntity(int)}.</li>
 *   <li>{@code use()} → {@link #onBlockActivated(World, int, int, int, EntityPlayer, int, float, float, float)}.</li>
 *   <li>{@code setPlacedBy()} → {@link #onBlockPlacedBy(World, int, int, int, EntityLivingBase, ItemStack)}.</li>
 *   <li>{@code onRemove()} → {@link #breakBlock(World, int, int, int, Block, int)}.</li>
 *   <li>{@code getDestroyProgress()} → {@link #getPlayerRelativeBlockHardness(EntityPlayer, World, int, int, int)}.</li>
 *   <li>{@code VoxelShape getShape()} → {@code setBlockBoundsBasedOnState()} in constructor.</li>
 * </ul>
 */
@SuppressWarnings("PMD.ExcessiveImports")
public abstract class AbstractColonyBlock<B extends AbstractColonyBlock<B>> extends AbstractBlockMinecolonies<B> implements IBuilderUndestroyable, ITickableBlockMinecolonies
{
    /** Hardness factor applied to all colony blocks in PvP mode. */
    private static final int HARDNESS_PVP_FACTOR = 4;

    /**
     * Metadata constants for horizontal facing.
     * 0=SOUTH, 1=WEST, 2=NORTH, 3=EAST — matches vanilla 1.7.10 convention.
     */
    public static final int META_FACING_SOUTH = 0;
    public static final int META_FACING_WEST  = 1;
    public static final int META_FACING_NORTH = 2;
    public static final int META_FACING_EAST  = 3;

    // [1.7.10 BACKPORT] In 1.21 this was a DirectionProperty FACING BlockState property:
    //   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    // Facing is now encoded in metadata bits 0-1 (see META_FACING_* constants above).

    /** Default hardness for all colony blocks. */
    public static final float HARDNESS   = 10F;
    /** Default blast resistance for all colony blocks. */
    public static final float RESISTANCE = Float.MAX_VALUE;

    /** The hut's lower-case building-registry-compatible name. */
    private final String name;

    /** Throttle: world-time of the last "you are breaking a building" chat warning. */
    private long lastBreakTickWarn = 0;

    /**
     * Default constructor — uses wood material, wood sound, and colony-standard hardness.
     *
     * <p>In 1.7.10 we chain material/hardness/sound as separate calls instead of the
     * 1.21 {@code Block.Properties} builder.</p>
     */
    public AbstractColonyBlock()
    {
        super(Material.wood);
        setHardness(HARDNESS);
        setResistance(RESISTANCE / 5F);   // resistance in 1.7.10 is /5 of the 1.21 value
        setStepSound(Block.soundTypeWood);
        setLightOpacity(0);               // noOcclusion() equivalent
        this.name = getHutName();

        // [1.7.10 BACKPORT] In 1.21:
        //   super(Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD)
        //       .strength(HARDNESS, RESISTANCE).noOcclusion());
        //   this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        // Default facing (NORTH = meta 2) is handled when the block is placed.
    }

    /**
     * Constructor that allows subclasses to supply a custom {@link Material}.
     *
     * @param material the block material.
     */
    public AbstractColonyBlock(final Material material)
    {
        super(material);
        setHardness(HARDNESS);
        setResistance(RESISTANCE / 5F);
        setLightOpacity(0);
        this.name = getHutName();

        // [1.7.10 BACKPORT] Original Properties-based constructor:
        //   public AbstractColonyBlock(final Properties properties) {
        //       super(properties.noOcclusion());
        //       this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        //       this.name = getHutName(); }
    }

    /**
     * Returns the lower-case hut/colony-block name used for registration and display.
     *
     * @return the hut name string.
     */
    public abstract String getHutName();

    /**
     * Returns the building registry entry for this block's associated building.
     *
     * @return the {@link BuildingEntry}.
     */
    public abstract BuildingEntry getBuildingEntry();

    // -----------------------------------------------------------------------
    // Registration
    // -----------------------------------------------------------------------

    @Override
    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(Constants.MOD_ID, getHutName());
    }

    @Override
    protected Class<? extends ItemBlock> getItemClass()
    {
        return ItemBlockHut.class;
    }

    // -----------------------------------------------------------------------
    // TileEntity
    // -----------------------------------------------------------------------

    @Override
    public boolean hasTileEntity(final int metadata)
    {
        return true;
    }

    /**
     * Creates the {@link TileEntityColonyBuilding} for this block.
     *
     * <p>In 1.21 this was {@code newBlockEntity(blockPos, BlockState)}.
     * In 1.7.10, Forge calls {@code createTileEntity(World, int meta)}.</p>
     */
    @Nullable
    @Override
    public TileEntity createTileEntity(final World world, final int meta)
    {
        try
        {
            final TileEntityColonyBuilding building = (TileEntityColonyBuilding) MinecoloniesTileEntities.BUILDING.newInstance();
            building.registryName = this.getBuildingEntry().getRegistryName();
            return building;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    // -----------------------------------------------------------------------
    // Block bounds (replaces VoxelShape / getShape())
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 the shape was returned from getShape():
    //   private static final VoxelShape SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
    //   public VoxelShape getShape(...) { return SHAPE; }
    // In 1.7.10 we set the bounding box in the constructor or override setBlockBoundsBasedOnState.

    @Override
    public void setBlockBoundsBasedOnState(final net.minecraft.world.IBlockAccess access, final int x, final int y, final int z)
    {
        setBlockBounds(0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F);
    }

    // -----------------------------------------------------------------------
    // Player hardness (getDestroyProgress equivalent)
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 this was:
    //   public float getDestroyProgress(BlockState state, Player player, BlockGetter world, int[] pos)
    // In 1.7.10 the equivalent is getPlayerRelativeBlockHardness.
    @Override
    public float getPlayerRelativeBlockHardness(final EntityPlayer player, final World world, final int x, final int y, final int z)
    {
        final IBuilding building = IColonyManager.getInstance().getBuilding(world, new int[]{x, y, z});
        if (building != null && !building.getChildren().isEmpty() && (world.getTotalWorldTime() - lastBreakTickWarn) >= 100)
        {
            lastBreakTickWarn = world.getTotalWorldTime();
            MessageUtils.format(HUT_BREAK_WARNING_CHILD_BUILDINGS).sendTo(player);
        }

        final float hardness = MinecoloniesAPIProxy.getInstance().getConfig().getServer().pvp_mode.get()
                                   ? HARDNESS * HARDNESS_PVP_FACTOR
                                   : HARDNESS;
        return (1F / hardness) / 30F;
    }

    // -----------------------------------------------------------------------
    // Block activation / right-click (use() equivalent)
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 this was:
    //   public InteractionResult use(BlockState state, World worldIn, int[] pos,
    //       Player player, int /* InteractionHand */ hand, BlockHitResult ray)
    // In 1.7.10 this becomes onBlockActivated.
    @Override
    public boolean onBlockActivated(
      final World worldIn,
      final int x,
      final int y,
      final int z,
      final EntityPlayer player,
      final int side,
      final float hitX,
      final float hitY,
      final float hitZ)
    {
        // Only open the GUI on the client side (equivalent to worldIn.isClientSide check).
        if (worldIn.isRemote)
        {
            @Nullable final IBuildingView building = IColonyManager.getInstance().getBuildingView(worldIn.provider.dimensionId, new int[]{x, y, z});
            final TileEntity entity = worldIn.getTileEntity(x, y, z);

            if (entity instanceof final TileEntityColonyBuilding te
                  && te.getPositionedTags().containsKey(null /* new int[]{0,0,0} */))
            {
                // TODO: [1.7.10 BACKPORT] new int[]{0,0,0} does not exist; replace with a sentinel
                //  ChunkCoordinates(0,0,0) or adapt getPositionedTags() to use ChunkCoordinates.
                if (te.getPositionedTags().get(null).contains(DEACTIVATED))
                {
                    // TODO: [1.7.10 BACKPORT] ColonyUtils.getOwningColony(chunk) used LevelChunk.
                    //  Replace with ChunkAPI-based colony ownership lookup.
                    if (building == null)
                    {
                        IColonyManager.getInstance().openReactivationWindow(new int[]{x, y, z});
                        return true;
                    }
                }
            }

            if (building == null)
            {
                MessageUtils.format(HUT_BLOCK_MISSING_BUILDING).sendTo(player);
                return true;
            }

            if (building.getColony() == null)
            {
                MessageUtils.format(HUT_BLOCK_MISSING_COLONY).sendTo(player);
                return true;
            }

            if (!building.getColony().getPermissions().hasPermission(player, Action.ACCESS_HUTS))
            {
                MessageUtils.format(PERMISSION_DENIED).sendTo(player);
                return true;
            }

            building.openGui(player.isSneaking());
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // Block placement (setPlacedBy() equivalent)
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 this was:
    //   public void setPlacedBy(World worldIn, int[] pos, BlockState state,
    //       EntityLivingBase placer, ItemStack stack)
    // In 1.7.10 this becomes onBlockPlacedBy.
    @Override
    public void onBlockPlacedBy(
      @NotNull final World worldIn,
      final int x,
      final int y,
      final int z,
      @NotNull final EntityLivingBase placer,
      final ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);

        // Set horizontal facing in metadata based on placer's yaw.
        final int meta = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        worldIn.setBlockMetadataWithNotify(x, y, z, meta, 2);

        // Server-side colony building registration.
        if (worldIn.isRemote)
        {
            return;
        }

        final TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityColonyBuilding)
        {
            @NotNull final TileEntityColonyBuilding hut = (TileEntityColonyBuilding) tileEntity;
            if (hut.getBuildingName() != getBuildingEntry().getRegistryName())
            {
                hut.registryName = getBuildingEntry().getRegistryName();
            }

            // TODO: [1.7.10 BACKPORT] hut.getPosition() returned int[]; replace with
            //  a ChunkCoordinates-based accessor once TileEntityColonyBuilding is ported.
            @Nullable final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(worldIn, new int[]{x, y, z});

            if (colony != null)
            {
                colony.getServerBuildingManager().addNewBuilding(hut, worldIn);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Block state for placement (getStateForPlacement equivalent)
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 this was:
    //   public BlockState getStateForPlacement(BlockPlaceContext context) {
    //       Direction facing = context.getPlayer() == null ? Direction.NORTH : ...;
    //       return this.defaultBlockState().setValue(FACING, facing);
    //   }
    // In 1.7.10 the initial metadata is returned from onBlockPlaced().
    // Facing is written in onBlockPlacedBy() above after the block is placed.
    @Override
    public int onBlockPlaced(
      final World world,
      final int x,
      final int y,
      final int z,
      final int side,
      final float hitX,
      final float hitY,
      final float hitZ,
      final int meta)
    {
        // Default to NORTH (meta 2) — will be updated to the actual placer yaw in onBlockPlacedBy.
        return META_FACING_NORTH;
    }

    // -----------------------------------------------------------------------
    // Block removal / destruction (onRemove() equivalent)
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 this was:
    //   public void onRemove(BlockState blockState, World World, int[] pos,
    //       BlockState newBlockState, boolean p_60519_)
    // In 1.7.10 this becomes breakBlock.
    @Override
    public void breakBlock(
      final World world,
      final int x,
      final int y,
      final int z,
      final Block block,
      final int meta)
    {
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof AbstractTileEntityColonyBuilding tileEntityColonyBuilding)
        {
            // TODO: [1.7.10 BACKPORT] InventoryUtils.dropItemHandler() used 1.21 int[].
            //  Update the overload signature to accept int x,y,z.
            InventoryUtils.dropItemHandler(tileEntityColonyBuilding.getInventory(), world, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    // -----------------------------------------------------------------------
    // Block state definition (createBlockStateDefinition equivalent)
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21 this registered the FACING property with the BlockState system:
    //   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    //       builder.add(FACING);
    //   }
    // In 1.7.10 there is no BlockState system; facing is encoded in metadata. No equivalent needed.

    // -----------------------------------------------------------------------
    // Block rotation (rotate() equivalent)
    // -----------------------------------------------------------------------

    // [1.7.10 BACKPORT] In 1.21:
    //   public BlockState rotate(BlockState state, Rotation rot) {
    //       return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    //   }
    // In 1.7.10, rotation is applied directly to the metadata integer.
    /**
     * Rotates the given facing metadata value 90° clockwise.
     * Intended for use by structure placement code (Structurize).
     *
     * @param meta current facing metadata (0–3).
     * @return the rotated metadata.
     */
    public static int rotateFacingMetaCW(final int meta)
    {
        // SOUTH(0) → WEST(1) → NORTH(2) → EAST(3) → SOUTH(0)
        return (meta + 1) & 3;
    }
}



