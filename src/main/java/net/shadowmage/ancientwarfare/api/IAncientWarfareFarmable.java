package net.shadowmage.ancientwarfare.api;

import net.minecraft.block.IGrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/*
 * Interface used for compatibility with Ancient Warfare crop farms for crops which don't follow the standard MinecraftForge crop code
 * This interface has to be implemented in the Block class of your crops
 */
public interface IAncientWarfareFarmable extends IGrowable {
    /*
     * Checks if the crop is mature and can be harvested by the crop farm
     * @param world
     * @param pos
     * @return true if the crop is ready to be harvested, false if not
     */
    boolean isMature(World world, BlockPos pos);

    /*
     * This is called when the crop farm harvests this crop. The crop farm will do nothing with the crop.
     * All necessary operations to be performed on the crop should be done in this method.
     * @param world
     * @param pos
     * @param fortune
     * @return A list containing ItemStacks of all fruits
     */
    NonNullList<ItemStack> doHarvest(World world, BlockPos pos, int fortune);
}
