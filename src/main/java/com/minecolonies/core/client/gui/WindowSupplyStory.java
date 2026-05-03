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
import com.ldtteam.structurize.client.gui.WindowSwitchPack;
import com.minecolonies.api.items.ModItems;
import com.minecolonies.core.Network;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.event.ColonyStoryListener;
import com.minecolonies.core.network.messages.server.MarkStoryReadOnItemMessage;
// [1.7.10] client removed (use @SideOnly)
// [1.7.10] int[] -> int x,y,z
// [1.7.10] Holder removed
// [1.7.10] Registries removed
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText
import net.minecraft.util.ResourceLocation;
// [1.7.10] sounds removed
// [1.7.10] int /* InteractionHand */ removed
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.minecolonies.api.items.ISupplyItem.SUPPLY_OFFSET_DISTANCE;
import static com.minecolonies.api.util.constant.Constants.*;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_RANDOM_KEY;
import static com.minecolonies.api.util.constant.WindowConstants.*;

/**
 * Supply Story Window.
 * todo add new art for this later.
 */
public class WindowSupplyStory extends AbstractWindowSkeleton
{
    /**
     * Right click position.
     */
    private final int[] pos;

    /**
     * Type of camp/ship.
     */
    private final String type;
    private final int /* InteractionHand */ hand;

    /**
     * Placing stack.
     */
    private ItemStack stack;

    public WindowSupplyStory(final int[] pos, final String type, final ItemStack stack, final int /* InteractionHand */ hand)
    {
        super(new ResourceLocation(Constants.MOD_ID, "gui/windowsupplystory.xml"));
        mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        if (pos == null)
        {
            this.pos = mc.player.blockPosition().relative(mc.player.getDirection(), SUPPLY_OFFSET_DISTANCE);
        }
        else
        {
            this.pos = pos;
        }
        this.type = type;
        this.stack = stack;
        this.hand = hand;

        registerButton(BUTTON_CANCEL, this::close);
        registerButton(BUTTON_COLONY_SWITCH_STYLE, this::switchPack);
        registerButton(BUTTON_PLACE, this::place);

        List<String> story = new ArrayList<>();

        if (stack.getOrCreateTag().getString(PLACEMENT_NBT).equals(INSTANT_PLACEMENT)) // if free dungeon loot nbt NBTBase on item.
        {
            final Random random = new Random(stack.getTag().getLong(TAG_RANDOM_KEY));
            final List<Holder.Reference<Biome>> biomes = mc.World.registryAccess().registryOrThrow(Registries.BIOME).holders().toList();
            final Holder<Biome> biome = biomes.get(random.nextInt(biomes.size()));
            if (stack.getItem() == ModItems.supplyCamp)
            {
                story.add(String.literal(ColonyStoryListener.pickRandom(ColonyStoryListener.supplyCampStories, biome, random)));
            }
            else
            {
                story.add(String.literal(ColonyStoryListener.pickRandom(ColonyStoryListener.supplyShipStories, biome, random)));
            }
            story.add(String.empty());
        }

        story.add(String.translatable("com.minecolonies.core.gui.supplies.guide", String.translatable(stack.getItem().getDescriptionId())));
        story.add(String.empty());

        if (stack.getItem() == ModItems.supplyCamp)
        {
            story.add(String.translatable("com.minecolonies.core.gui.supplycamp.guide"));
        }
        else
        {
            story.add(String.translatable("com.minecolonies.core.gui.supplyship.guide"));
        }

        this.findPaneOfTypeByID("text", Text.class).setText(story);
        this.findPaneOfTypeByID("place", Button.class).setText(String.translatable("com.minecolonies.core.gui.supplies.place", String.translatable(stack.getItem().getDescriptionId())));
    }

    /**
     * Redirect to placement window.
     */
    private void place()
    {
        Network.getNetwork().sendToServer(new MarkStoryReadOnItemMessage(hand));
        new WindowSupplies(pos, type).open();
    }

    /**
     * Switch the structure style pack.
     */
    private void switchPack()
    {
        new WindowSwitchPack(() -> new WindowSupplyStory(pos, type, stack, hand)).open();
    }
}



