package com.minecolonies.core.client.gui.map;

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
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneParams;
import com.ldtteam.blockui.controls.BOGuiGraphics;
// [1.7.10] DynamicTexture shim
import net.minecraft.client.renderer.texture.DynamicTexture;
// [1.7.10] client removed (use @SideOnly)
import net.minecraft.util.ResourceLocation;
// [1.7.10] World.material removed
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

/**
 * Simple minecraft map element.
 */
public class MinecraftMap extends Pane implements AutoCloseable
{
    private DynamicTexture texture;
    private ResourceLocation textureResLoc;

    /**
     * Default Constructor.
     */
    public MinecraftMap()
    {
        super();
    }

    /**
     * Constructor used by the xml loader.
     *
     * @param params PaneParams loaded from the xml.
     */
    public MinecraftMap(final PaneParams params)
    {
        super(params);
    }

    /**
     * Set the fitting map data.
     * @param mapData the mapData to set.
     */
    public void setMapData(final MapItemSavedData mapData)
    {
        if (texture != null)
        {
            freeTexture();
        }

        texture = new DynamicTexture(128, 128, false);

        for (int y = 0; y < 128; ++y)
        {
            for (int x = 0; x < 128; ++x)
            {
                texture.getPixels().setPixelRGBA(x, y, MapColor.getColorFromPackedId(mapData.colors[x + y * 128]));
            }
        }

        texture.upload();
        textureResLoc = mc.textureManager.register("minecolonies_map/" + id, texture);
    }

    /**
     * Draw this image on the GUI.
     *
     * @param mx Mouse x (relative to parent)
     * @param my Mouse y (relative to parent)
     */
    @Override
    public void drawSelf(final BOGuiGraphics ms, final double mx, final double my)
    {
        if (textureResLoc != null)
        {
            blit(ms.pose(), textureResLoc, x, y, width, height);
        }
    }

    private void freeTexture()
    {
        if (textureResLoc != null)
        {
            texture.close();
            mc.textureManager.release(textureResLoc);

            texture = null;
            textureResLoc = null;
        }
    }

    @Override
    public void close()
    {
        freeTexture();
    }
}



