package com.minecolonies.core.entity.ai.workers.util;

import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.AbstractBlueprintIterator;
import com.ldtteam.structurize.placement.AbstractBlueprintIteratorWrapper;
import com.ldtteam.structurize.placement.StructureIterators;
import com.ldtteam.structurize.placement.structure.IStructureHandler;
import com.ldtteam.structurize.util.BlueprintPositionInfo;
import com.ldtteam.structurize.util.PlacementSettings;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.TriPredicate;
// [1.7.10] items shim in com.minecolonies.api.shim
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A BlueprintIterator which only iterates over one Y-World (layer) of a blueprint, using the iterator pattern of a different iterator
 */
public class LayerBlueprintIterator extends AbstractBlueprintIteratorWrapper
{
    /**
     * The current selected layer
     */
    private int layer;
    /**
     * The handler of the wrapped iterator (which wraps all methods to only work on a blueprint of one block high, of the current layer)
     */
    private final StructureHandlerWrapper handler;
    /**
     * The originally passed handler (containing the entire blueprint). As the progressPos of this iterator is relative to the entire blueprint, this is the structureHandler "used" by this iterator
     */
    private final IStructureHandler originalHandler;

    /**
     * Construct a new LayerBlueprintIterator, based on the given iterator strategy and using the given handler
     * @param iteratorId The iterator strategy. It accepts any of the names of the registered iterators (e.g. inwardcircle, hilbert, default, ...)
     * @param handler The IStructureHandler to be used with this iterator
     */
    public LayerBlueprintIterator(final String iteratorId, final IStructureHandler handler)
    {
        this(iteratorId, new StructureHandlerWrapper(handler), handler);
    }

    /**
     * Internally used iterator, with both a handler for the wrapped iterator, and the original one, used by this iterator
     * (This is a trick to have a reference to the handler to pass to both the super class, and to set it to a field at the same time,
     * since you cannot have a variable declaration before a super call in any other way)
     * @param iteratorId The iterator strategy
     * @param handler The wrapped structure handler, to be used by the wrapped iterator
     * @param originalHandler The original one
     */
    private LayerBlueprintIterator(final String iteratorId, final StructureHandlerWrapper handler, final IStructureHandler originalHandler)
    {
        super(StructureIterators.getIterator(iteratorId, handler));
        this.handler = handler;
        this.originalHandler = originalHandler;
        handler.setOuter(this);
    }

    /**
     * Set the layer of this iterator. It resets the blueprint slice used by the wrapped iterator, and resets it to the beginning
     * @param newLevel the new layer
     */
    public void setLayer(final int newLevel)
    {
        if (layer != newLevel)
        {
            layer = newLevel;
            // Reset the blueprint, as we now need a new layer of the blueprint
            handler.setLayerBlueprint();
        }
        delegate.setProgressPos(NULL_POS);
    }

    /**
     * Get the current layer
     * @return the current layer
     */
    public int getLayer()
    {
        return layer;
    }

    @Override
    public void setProgressPos(final int[] localPosition)
    {
        if (Arrays.equals(localPosition, NULL_POS))
        {
            delegate.setProgressPos(NULL_POS);
        }
        else
        {
            // [1.7.10] atY(0) → set y=0
            delegate.setProgressPos(new int[]{localPosition[0], 0, localPosition[2]});
        }
    }

    @Override
    public int[] getProgressPos()
    {
        final int[] progressPos = delegate.getProgressPos();
        if (Arrays.equals(progressPos, NULL_POS))
        {
            return NULL_POS;
        }
        return new int[]{progressPos[0], layer, progressPos[2]};
    }

    @Override
    public int[] getPrevProgressPos()
    {
        final int[] prevProgressPos = delegate.getPrevProgressPos();
        if (Arrays.equals(prevProgressPos, NULL_POS))
        {
            return NULL_POS;
        }
        return new int[]{prevProgressPos[0], layer, prevProgressPos[2]};
    }

    @Override
    public BlueprintPositionInfo getBluePrintPositionInfo(final int[] localPos)
    {
        // localPos is relative to the original blueprint, so we need to use the original blueprint to retrieve the information
        return originalHandler.getBluePrint().getBluePrintPositionInfo(localPos, hasEntities());
    }

    @Override
    public int[] getSize()
    {
        final Blueprint blueprint = originalHandler.getBluePrint();
        return new int[]{blueprint.getSizeX(), blueprint.getSizeY(), blueprint.getSizeZ()};
    }

    @Override
    protected IStructureHandler getStructureHandler()
    {
        // The LayerIterator uses the original structureHandler, as it handles the original blueprint and positions of the entire blueprint
        return originalHandler;
    }

    /**
     * A wrapper for an IStructureHandler, to return information about a blueprint of a single layer
     */
    private static class StructureHandlerWrapper implements IStructureHandler
    {
        /**
         * The original IStructureHandler, which this is based on
         */
        private final IStructureHandler delegate;
        /**
         * The wrapping LayerBlueprintIterator. It is used to get the current layer we are iterating over, to get just that Y-World from the blueprint
         */
        private LayerBlueprintIterator outer;

        /**
         * A blueprint slice of the original blueprint
         */
        private       Blueprint         layerBlueprint;

        /**
         * Create a new StructureHandlerWrapper
         * @param delegate The IStructureHandler to wrap
         */
        private StructureHandlerWrapper(final IStructureHandler delegate)
        {
            this.delegate = delegate;
        }

        /**
         * Sets the LayerBlueprintIterator. Because of technical reasons (i.e. an instance of this class is needed when the superclass constructor of LayerBlueprintIterator is called),
         * it is not possible to give an instance of the class in the constructor, or to make this an inner class
         * @param iterator The iterator
         */
        private void setOuter(final LayerBlueprintIterator iterator)
        {
            outer = iterator;
        }

        @Override
        public void setBlueprint(final Blueprint blueprint)
        {
            delegate.setBlueprint(blueprint);
            layerBlueprint = null;
        }

        @Override
        public void setMd5(final String s)
        {
            delegate.setMd5(s);
        }

        @Override
        public String getMd5()
        {
            return delegate.getMd5();
        }

        /**
         * Helper to get the layer from the iterator. `outer` may temporarily be null, during the super class constructor call of LayerBlueprintIterator
         * @return the layer, or a default "0" when outer is not initialised yet
         */
        private int getLayer()
        {
            return outer == null ? 0 : outer.getLayer();
        }

        /**
         * Create a new slice of the blueprint based on the current layer of the outer LayerBlueprintIterator (and cache it)
         */
        private void setLayerBlueprint()
        {
            final Blueprint blueprint = delegate.getBluePrint();

            final short sizeX = blueprint.getSizeX();
            final short sizeY = 1;
            final short sizeZ = blueprint.getSizeZ();
            final NBTTagCompound[][][] tags = blueprint.getTileEntities();
            final short[][][] structure = blueprint.getStructure();
            final int layer = getLayer();
            final short[][][] structureAtLayer = new short[][][] { structure[layer] };
            final List<NBTTagCompound> tagsAtLayer = new ArrayList<>();

            for (int i = 0; i < sizeZ; i++)
            {
                for (int j = 0; j < sizeX; j++)
                {
                    if (tags[layer][i][j] != null)
                    {
                        final NBTTagCompound NBTBase = tags[layer][i][j].copy();
                        // The Blueprint will sort them by stored position again, which will be on the 0'th Y-World in the slice
                        NBTBase.putShort("y", (short)0);
                        tagsAtLayer.add(NBTBase);
                    }
                }
            }

            layerBlueprint = new Blueprint(sizeX, sizeY, sizeZ, blueprint.getPalleteSize(), Arrays.asList(blueprint.getPalette()), structureAtLayer, tagsAtLayer.toArray(new NBTTagCompound[0]), blueprint.getRequiredMods());
        }

        @Override
        public Blueprint getBluePrint()
        {
            if (layerBlueprint == null)
            {
                setLayerBlueprint();
            }

            return layerBlueprint;
        }

        @Override
        public World getWorld()
        {
            return delegate.getWorld();
        }

        @Override
        public int[] getCenterPos()
        {
            return delegate.getCenterPos()
                .subtract(delegate.getBluePrint().getPrimaryBlockOffset())
                .offset(layerBlueprint.getPrimaryBlockOffset().atY(getLayer()));
        }

        @Override
        public PlacementSettings getSettings()
        {
            return delegate.getSettings();
        }

        @Override
        public PlacementSettings getRotationMirror()
        {
            return delegate.getRotationMirror();
        }

        @Override
        public @Nullable net.minecraftforge.items.IItemHandler getInventory()
        {
            return delegate.getInventory();
        }

        @Override
        public void triggerSuccess(final int[] blockPos, final List<ItemStack> list, final boolean b)
        {
            delegate.triggerSuccess(blockPos, list, b);
        }

        @Override
        public void triggerEntitySuccess(final int[] blockPos, final List<ItemStack> list, final boolean b)
        {
            delegate.triggerEntitySuccess(blockPos, list, b);
        }

        @Override
        public boolean isCreative()
        {
            return delegate.isCreative();
        }

        @Override
        public boolean hasBluePrint()
        {
            return delegate.hasBluePrint();
        }

        @Override
        public int getStepsPerCall()
        {
            return delegate.getStepsPerCall();
        }

        @Override
        public int getMaxBlocksCheckedPerCall()
        {
            return delegate.getMaxBlocksCheckedPerCall();
        }

        @Override
        public boolean isStackFree(@Nullable final ItemStack itemStack)
        {
            return delegate.isStackFree(itemStack);
        }

        @Override
        public boolean allowReplace()
        {
            return delegate.allowReplace();
        }

        @Override
        public ItemStack getHeldItem()
        {
            return delegate.getHeldItem();
        }

        @Override
        public boolean replaceWithSolidBlock(final BlockState blockState)
        {
            return delegate.replaceWithSolidBlock(blockState);
        }

        @Override
        public boolean fancyPlacement()
        {
            return delegate.fancyPlacement();
        }

        @Override
        public boolean shouldBlocksBeConsideredEqual(final BlockState blockState, final BlockState blockState1)
        {
            return false;
        }

        @Override
        public boolean hasRequiredItems(final List<ItemStack> list)
        {
            return delegate.hasRequiredItems(list);
        }

        @Override
        public void prePlacementLogic(final int[] blockPos, final BlockState blockState, final List<ItemStack> list)
        {
            delegate.prePlacementLogic(blockPos, blockState, list);
        }

        @Override
        public BlockState getSolidBlockForPos(final int[] blockPos)
        {
            return delegate.getSolidBlockForPos(blockPos);
        }

        @Override
        public BlockState getSolidBlockForPos(final int[] blockPos, @Nullable final Function<int[], BlockState> function)
        {
            return delegate.getSolidBlockForPos(blockPos, function);
        }

        @Override
        public boolean isReady()
        {
            return delegate.isReady();
        }
    }
}







