package com.minecolonies.api.blocks.interfaces;

// [1.7.10 BACKPORT] The 1.21 ITickableBlockMinecolonies extended EntityBlock and used
// BlockEntityTicker<T> to drive per-tick logic from the block side.
// In 1.7.10, ticking is handled entirely by the TileEntity itself via updateEntity().
// This interface is therefore kept as an empty marker so that the class hierarchy compiles
// without changes, but it no longer needs to declare anything.
//
// [1.7.10 BACKPORT] Removed imports (all 1.21-only):
//   com.minecolonies.api.tileentities.ITickable
//   net.minecraft.world.World.World
//   net.minecraft.world.level.block.EntityBlock
//   net.minecraft.world.level.block.entity.BlockEntity
//   net.minecraft.world.level.block.entity.BlockEntityTicker
//   net.minecraft.world.level.block.entity.BlockEntityType
//   net.minecraft.world.level.block.state.BlockState

// TODO: [1.21 BACKPORT] Original interface body:
//   extends EntityBlock {
//     @Nullable @Override
//     default <T extends BlockEntity> BlockEntityTicker<T> getTicker(World World, BlockState state, BlockEntityType<T> type) {
//         return createTickerHelper(type, type, (l, pos, s, te) -> ((ITickable) te).tick(l, s, pos));
//     }
//     @Nullable
//     static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(...) { ... }
//   }
// Ticking is now performed by TileEntity.updateEntity() — see TileEntityColonyBuilding.

public interface ITickableBlockMinecolonies
{
    // Marker interface only in 1.7.10 — ticking is driven by TileEntity.updateEntity().
}

