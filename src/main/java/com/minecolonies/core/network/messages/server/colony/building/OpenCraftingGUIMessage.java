package com.minecolonies.core.network.messages.server.colony.building;
import net.minecraft.world.entity.player.Player;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.crafting.ModCraftingTypes;
import com.minecolonies.api.inventory.container.ContainerCrafting;
import com.minecolonies.api.inventory.container.ContainerCraftingBrewingstand;
import com.minecolonies.api.inventory.container.ContainerCraftingFurnace;
import com.minecolonies.core.colony.buildings.modules.AbstractCraftingBuildingModule;
import com.minecolonies.core.colony.buildings.views.AbstractBuildingView;
import com.minecolonies.core.network.messages.server.AbstractBuildingServerMessage;
import net.minecraft.entity.player.EntityPlayer;
// [1.7.10] world.entity removed
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.IChatComponent;

import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

/**
 * Message sent to open an inventory.
 */
public class OpenCraftingGUIMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * The type of container.
     */
    private int id;

    /**
     * Empty public constructor.
     */
    public OpenCraftingGUIMessage()
    {
        super();
    }

    /**
     * Creates an open inventory message for a building.
     * @param id the string id.
     * @param building {@link AbstractBuildingView}
     */
    public OpenCraftingGUIMessage(@NotNull final AbstractBuildingView building, final int id)
    {
        super(building);
        this.id = id;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {
        this.id = buf.readInt();
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(id);
    }

    @Override
    protected void onExecute(final MessageContext ctx, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null)
        {
            return;
        }

        if (building.getModule(id) instanceof AbstractCraftingBuildingModule module)
        {
            if (module.canLearn(ModCraftingTypes.SMELTING.get()))
            {
                NetworkHooks.openScreen(player, new MenuProvider()
                {
                    @NotNull
                    @Override
                    public String getDisplayName()
                    {
                        return String.literal("Furnace Crafting GUI");
                    }

                    @NotNull
                    @Override
                    public AbstractContainerMenu createMenu(final int id, @NotNull final Inventory inv, @NotNull final Player player)
                    {
                        return new ContainerCraftingFurnace(id, inv, building.getID(), module.getProducer().getRuntimeID());
                    }
                }, buffer -> new PacketBuffer(buffer.writeBlockPos(building.getID()).writeInt(module.getProducer().getRuntimeID())));
            }
            else if (module.canLearn(ModCraftingTypes.BREWING.get()))
            {
                NetworkHooks.openScreen(player, new MenuProvider()
                {
                    @NotNull
                    @Override
                    public String getDisplayName()
                    {
                        return String.literal("Brewing Crafting GUI");
                    }

                    @NotNull
                    @Override
                    public AbstractContainerMenu createMenu(final int id, @NotNull final Inventory inv, @NotNull final Player player)
                    {
                        return new ContainerCraftingBrewingstand(id, inv, building.getID(), module.getProducer().getRuntimeID());
                    }
                }, buffer -> new PacketBuffer(buffer.writeBlockPos(building.getID()).writeInt(module.getProducer().getRuntimeID())));
            }
            else
            {
                net.minecraftforge.network.NetworkHooks.openScreen(player,
                  new MenuProvider()
                  {
                      @NotNull
                      @Override
                      public String getDisplayName()
                      {
                          return String.literal("Crafting GUI");
                      }

                      @NotNull
                      @Override
                      public AbstractContainerMenu createMenu(final int id, @NotNull final Inventory inv, @NotNull final Player player)
                      {
                          return new ContainerCrafting(id, inv, module.canLearn(ModCraftingTypes.LARGE_CRAFTING.get()), building.getID(), module.getProducer().getRuntimeID());
                      }
                  },
                  buffer -> new PacketBuffer(buffer.writeBoolean(module.canLearn(ModCraftingTypes.LARGE_CRAFTING.get()))).writeBlockPos(building.getID())
                    .writeInt(module.getProducer().getRuntimeID()));
            }
        }
    }
}



