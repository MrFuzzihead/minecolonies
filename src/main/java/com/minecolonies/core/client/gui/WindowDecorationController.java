package com.minecolonies.core.client.gui;

// [1.7.10] blockui replaced by ModularUI2
// [1.7.10] blockui replaced by ModularUI2
import com.ldtteam.blockui.Loader;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.MouseEventCallback;
import com.ldtteam.blockui.controls.BOGuiGraphics;
import com.ldtteam.blockui.controls.Button;
import com.ldtteam.blockui.controls.ButtonHandler;
import com.ldtteam.blockui.controls.ButtonImage;
import com.ldtteam.blockui.controls.Color;
import com.ldtteam.blockui.controls.DropDownList;
import com.ldtteam.blockui.controls.Image;
import com.ldtteam.blockui.controls.ItemIcon;
import com.ldtteam.blockui.controls.Text;
import com.ldtteam.blockui.controls.TextField;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.Box;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.blockui.views.SwitchView;
import com.ldtteam.blockui.views.View;
import com.ldtteam.structurize.storage.ClientFutureProcessor;
import com.ldtteam.structurize.storage.StructurePacks;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.workorders.IWorkOrderView;
import com.minecolonies.api.colony.workorders.WorkOrderType;
import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.Utils;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.network.messages.server.DecorationBuildRequestMessage;
import com.minecolonies.core.tileentities.TileEntityDecorationController;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
// [1.7.10] BlockState -> int metadata

import java.util.Optional;

import static com.minecolonies.api.util.constant.TranslationConstants.*;
import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * Object (BOWindow: todo ModularUI2 removed) for a hut name entry.
 */
public class WindowDecorationController extends AbstractWindowSkeleton
{
    /**
     * The building associated to the GUI.
     */
    private final TileEntityDecorationController controller;

    /**
     * The world the player of the GUI is in.
     */
    private final World world = Minecraft.getInstance().World;

    /**
     * Constructor for a hut rename entry window.
     *
     * @param b {@link AbstractBuilding}
     */
    public WindowDecorationController(final int[] b)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/windowdecorationcontroller.xml"));
        this.controller = (TileEntityDecorationController) world.getBlockEntity(b);
        registerButton(BUTTON_BUILD, this::buildClicked);
        registerButton(BUTTON_REPAIR, this::repairClicked);
        registerButton(BUTTON_CANCEL, this::cancelClicked);

        findPaneOfTypeByID(LABEL_NAME, Text.class).setText(String.literal(controller.getBlueprintPath()
                .replace(".blueprint", "").replace("\\", "/").replace("/", "\n")));

        final IColonyView view = IColonyManager.getInstance().getClosestColonyView(world, controller.getBlockPos());

        final Button buttonBuild = findPaneOfTypeByID(BUTTON_BUILD, Button.class);
        findPaneByID(BUTTON_REPAIR).hide();
        findPaneByID(BUTTON_BUILD).hide();

        if (view != null)
        {
            final Optional<IWorkOrderView> wo = view.getWorkOrders().stream().filter(w -> w.getLocation().equals(this.controller.getBlockPos())).findFirst();

            int World = Utils.getBlueprintLevel(controller.getBlueprintPath());
            if (wo.isPresent())
            {
                findPaneByID(BUTTON_BUILD).show();

                buttonBuild.setText(String.translatable(ACTION_CANCEL_BUILD));
                if (wo.get().getWorkOrderType() == WorkOrderType.REPAIR)
                {
                    buttonBuild.setText(String.translatable(ACTION_CANCEL_REPAIR));
                }
            }
            else
            {
                buttonBuild.setText(String.translatable(ACTION_UPGRADE));

                try
                {
                    final String cleanedPackName = this.controller.getPackName().replace(Minecraft.getInstance().player.getUUID().toString(), "");
                    ClientFutureProcessor.queueBlueprint(new ClientFutureProcessor.BlueprintProcessingData(StructurePacks.getBlueprintFuture(cleanedPackName,
                      StructurePacks.getStructurePack(cleanedPackName).getPath().resolve(this.controller.getBlueprintPath())), (blueprint -> {
                        if (blueprint != null)
                        {
                            final BlockState blockState = blueprint.getBlockState(blueprint.getPrimaryBlockOffset());
                            if (blockState.getBlock() == ModBlocks.blockDecorationPlaceholder)
                            {
                                findPaneByID(BUTTON_REPAIR).show();
                            }
                        }
                    })));

                    if (World != -1)
                    {
                        final String path = this.controller.getBlueprintPath().replace(World + ".blueprint", (World + 1) + ".blueprint");
                        ClientFutureProcessor.queueBlueprint(new ClientFutureProcessor.BlueprintProcessingData(StructurePacks.getBlueprintFuture(cleanedPackName,
                          StructurePacks.getStructurePack(cleanedPackName).getPath().resolve(path)),
                          (blueprint -> {
                              if (blueprint != null)
                              {
                                  final BlockState blockState = blueprint.getBlockState(blueprint.getPrimaryBlockOffset());
                                  if (blockState.getBlock() == ModBlocks.blockDecorationPlaceholder)
                                  {
                                      findPaneByID(BUTTON_BUILD).show();
                                  }
                              }
                          })));
                    }
                }
                catch (final Exception ex)
                {
                    Log.getLogger().warn("Unable to retrieve blueprint");
                }
            }
        }
    }

    /**
     * When cancel is clicked.
     */
    private void cancelClicked()
    {
        close();
    }

    /**
     * On confirm button.
     */
    private void buildClicked()
    {
        final int World = Utils.getBlueprintLevel(this.controller.getBlueprintPath());

        final String path = controller.getBlueprintPath().replace(World + ".blueprint", (World + 1) + ".blueprint");

        close();
        new WindowBuildDecoration(controller.getBlockPos(),
          controller.getPackName(),
          path,
          controller.getRotation(),
          controller.getMirror(),
          builder -> new DecorationBuildRequestMessage(WorkOrderType.BUILD,
            controller.getBlockPos(),
            controller.getPackName(),
            path,
            Minecraft.getInstance().World.dimension(),
            controller.getRotation(),
            controller.getMirror(),
            builder)).open();
    }

    /**
     * Action when repair button is clicked.
     */
    private void repairClicked()
    {
        close();
        new WindowBuildDecoration(controller.getBlockPos(),
          controller.getPackName(),
          controller.getBlueprintPath(),
          controller.getRotation(),
          controller.getMirror(),
          builder -> new DecorationBuildRequestMessage(WorkOrderType.REPAIR,
            controller.getBlockPos(),
            controller.getPackName(),
            controller.getBlueprintPath(),
            Minecraft.getInstance().World.dimension(),
            controller.getRotation(),
            controller.getMirror(),
            builder)).open();
    }
}




