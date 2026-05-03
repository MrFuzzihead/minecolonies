package com.minecolonies.core.network.messages.client;

import com.ldtteam.structurize.storage.StructurePacks;
import com.ldtteam.structurize.storage.rendering.RenderingCache;
import com.minecolonies.api.network.IMessage;
import com.minecolonies.api.util.Log;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Locale;

import static com.ldtteam.structurize.api.util.constant.Constants.BLUEPRINT_FOLDER;
import static com.ldtteam.structurize.api.util.constant.Constants.SCANS_FOLDER;

/**
 * Handles sendScanMessages.
 */
public class SaveStructureNBTMessage implements IMessage
{
    private static final String TAG_MILLIS    = "millies";
    public static final  String TAG_SCHEMATIC = "schematic";

    private NBTTagCompound compoundNBT;
    private String      fileName;

    /**
     * Send a scan compound to the client.
     */
    public SaveStructureNBTMessage()
    {
        super();
    }

    /**
     * Send a scan compound to the client.
     *
     * @param CompoundNBT the stream.
     * @param fileName  String with the name of the file.
     */
    public SaveStructureNBTMessage(final NBTTagCompound CompoundNBT, final String fileName)
    {
        this.fileName = fileName;
        this.compoundNBT = CompoundNBT;
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        final PacketBuffer buffer = new PacketBuffer(buf);
        try (ByteBufInputStream stream = new ByteBufInputStream(buffer))
        {
            final NBTTagCompound wrapperCompound = NbtIo.readCompressed(stream);
            this.compoundNBT = wrapperCompound.getCompound(TAG_SCHEMATIC);
            this.fileName = wrapperCompound.getString(TAG_MILLIS);
        }
        catch (final RuntimeException e)
        {
            Log.getLogger().info("Structure too big to be processed", e);
        }
        catch (final IOException e)
        {
            Log.getLogger().info("Problem at retrieving structure on server.", e);
        }
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        final NBTTagCompound wrapperCompound = new NBTTagCompound();
        wrapperCompound.putString(TAG_MILLIS, fileName);
        wrapperCompound.put(TAG_SCHEMATIC, compoundNBT);

        final PacketBuffer buffer = new PacketBuffer(buf);
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buffer))
        {
            NbtIo.writeCompressed(wrapperCompound, stream);
        }
        catch (final IOException e)
        {
            Log.getLogger().info("Problem at retrieving structure on server.", e);
        }
    }

    @Nullable
    @Override
    public Boolean getExecutionSide()
    {
        return Boolean.FALSE;
    }

    @Override
    public void onExecute(final MessageContext ctx, final boolean isLogicalServer)
    {
        if (compoundNBT != null)
        {
            final String packName = Minecraft.getInstance().getUser().getName().toLowerCase(Locale.US);
            RenderingCache.getOrCreateBlueprintPreviewData("blueprint").setBlueprintFuture(
              StructurePacks.storeBlueprint(packName, compoundNBT, Minecraft.getInstance().gameDirectory.toPath()
                                                                  .resolve(BLUEPRINT_FOLDER)
                                                                  .resolve(Minecraft.getInstance().getUser().getName().toLowerCase(Locale.US))
                                                                  .resolve(SCANS_FOLDER).resolve(fileName)));
            Minecraft.getInstance().player.displayClientMessage(String.translatable("Scan successfully saved as %s", fileName), false);
        }
    }
}




