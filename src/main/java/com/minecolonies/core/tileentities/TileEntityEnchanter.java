package com.minecolonies.core.tileentities;

import com.minecolonies.api.util.WorldUtil;

import java.util.Random;

/**
 * Class which handles the tileEntity of our enchanter building.
 */
public class TileEntityEnchanter extends TileEntityColonyBuilding
{
    public int   tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float flipT;
    public float flipA;
    public float bookSpread;
    public float bookSpreadPrev;
    public float bookRotation;
    public float bookRotationPrev;
    public float tRot;

    private static final Random rand = new Random();

    /**
     * Default constructor.
     */
    public TileEntityEnchanter()
    {
        super();
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!worldObj.isRemote)
        {
            return;
        }

        // Client-side book animation — port from 1.7.10 enchantment table logic
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        this.pageFlipPrev = this.pageFlip;

        // TODO: look for nearby players in 1.7.10 and animate book
        this.bookSpread += 0.1f;
        if (this.bookSpread > 1.0f) this.bookSpread = 1.0f;

        this.tickCount++;
        this.pageFlip += this.flipA;
        this.flipA *= 0.9f;
        this.flipA += rand.nextFloat() * rand.nextFloat() * rand.nextFloat() * 0.1f;
        this.bookRotation += (float) Math.PI * 2.0f / 300.0f;
        this.tRot += 0.02f;
    }
}
