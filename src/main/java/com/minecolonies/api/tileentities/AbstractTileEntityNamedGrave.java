package com.minecolonies.api.tileentities;

import com.minecolonies.api.util.WorldUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_CONTENT;

public class AbstractTileEntityNamedGrave extends TileEntity
{
    /**
     * The text displayed on the name plate
     */
    private ArrayList<String> textLines = new ArrayList<>();

    public AbstractTileEntityNamedGrave()
    {
        super();
        textLines.add("Unknown Citizen");
    }

    public ArrayList<String> getTextLines()
    {
        return textLines;
    }

    public void setTextLines(final ArrayList<String> content)
    {
        this.textLines = content;
        markDirty();
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        textLines.clear();
        if (compound.hasKey(TAG_CONTENT))
        {
            final NBTTagList lines = compound.getTagList(TAG_CONTENT, 8); // 8 = NBTTagString type
            for (int i = 0; i < lines.tagCount(); i++)
            {
                final String line = lines.getStringTagAt(i);
                textLines.add(line);
            }
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        @NotNull final NBTTagList lines = new NBTTagList();
        for (@NotNull final String line : textLines)
        {
            lines.appendTag(new NBTTagString(line));
        }
        compound.setTag(TAG_CONTENT, lines);
    }

    @Override
    public void markDirty()
    {
        if (worldObj != null)
        {
            WorldUtil.markChunkDirty(worldObj, xCoord, yCoord, zCoord);
        }
    }
}
