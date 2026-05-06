package net.minecraft.world.entity.player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
/**
 * [1.7.10] Compatibility shim for 1.21 Player (extends 1.7.10 EntityPlayer).
 */
public abstract class Player extends EntityPlayer
{
    /** [1.7.10 bridge] 1.21 getItemInHand(hand) -> 1.7.10 getHeldItem() */
    public ItemStack getItemInHand(Object hand) { return getHeldItem(); }
    /** [1.7.10 bridge] 1.21 getInventory() */
    public net.minecraft.entity.player.InventoryPlayer getInventory() { return this.inventory; }
    /** [1.7.10 bridge] 1.21 isCreative() */
    public boolean isCreative() { return this.capabilities.isCreativeMode; }
    /** [1.7.10 bridge] 1.21 blockPosition() */
    public int[] blockPosition() { return new int[]{(int)this.posX, (int)this.posY, (int)this.posZ}; }
    /** [1.7.10 bridge] 1.21 getAbilities() stub - returns null for compilation */
    public Object getAbilities() { return null; }
    /** [1.7.10 bridge] 1.21 sendSystemMessage */
    public void sendSystemMessage(Object component) {}
    /** [1.7.10 bridge] 1.21 isSpectator */
    public boolean isSpectator() { return false; }
}