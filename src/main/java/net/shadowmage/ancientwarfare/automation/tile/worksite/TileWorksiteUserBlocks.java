package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.api.IAncientWarfarePlantable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public abstract class TileWorksiteUserBlocks extends TileWorksiteBlockBased {

    private byte[] targetMap = new byte[16 * 16];

    /**
     * flag should be set to true whenever updating inventory internally (e.g. harvesting blocks) to prevent
     * unnecessary inventory rescanning.  should be set back to false after blocks are added to inventory
     */
    boolean shouldCountResources;

    public TileWorksiteUserBlocks() {

    }

    @Override
    public boolean userAdjustableBlocks() {
        return true;
    }

    protected boolean isTarget(BlockPosition p) {
        int z = (p.z - bbMin.z) * 16 + p.x - bbMin.x;
        return z >= 0 && z < targetMap.length && targetMap[z] == 1;
    }

    protected boolean isTarget(int x1, int y1) {
        int z = (y1 - bbMin.z) * 16 + x1 - bbMin.x;
        return z >= 0 && z < targetMap.length && targetMap[z] == 1;
    }

    protected boolean isBonemeal(ItemStack stack) {
        return stack.getItem() == Items.dye && stack.getItemDamage() == 15;
    }

    protected boolean isFarmable(Block block){
        try{
            return isFarmable(block, 0, 0, 0);
        }catch (Exception e){
            return false;
        }
    }

    protected boolean isFarmable(Block block, int x, int y, int z){
        return block instanceof IPlantable;
    }

    protected boolean tryPlace(ItemStack stack, int x, int y, int z, ForgeDirection face){
        ForgeDirection direction = face.getOpposite();
        if(stack.getItem() instanceof IAncientWarfarePlantable) {
            return ((IAncientWarfarePlantable) stack.getItem()).tryPlant(worldObj, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, stack.copy());
        }
        return stack.tryPlaceItemIntoWorld(getOwnerAsPlayer(), worldObj, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, face.ordinal(), 0.25F, 0.25F, 0.25F);
    }

    protected final void pickupItems() {
        List<EntityItem> items = getEntitiesWithinBounds(EntityItem.class);
        ItemStack stack;
        for (EntityItem item : items) {
            if(item.isEntityAlive()) {
                stack = item.getEntityItem();
                if (stack != null) {
                    stack = InventoryTools.mergeItemStack(inventory, stack, getIndicesForPickup());
                    if (stack != null) {
                        item.setEntityItemStack(stack);
                    }else{
                        item.setDead();
                    }
                }
            }
        }
    }

    protected int[] getIndicesForPickup(){
        return inventory.getRawIndicesCombined();
    }

    @Override
    protected void validateCollection(Collection<BlockPosition> blocks) {
        Iterator<BlockPosition> it = blocks.iterator();
        BlockPosition pos;
        while (it.hasNext() && (pos = it.next()) != null) {
            if (!isInBounds(pos) || !isTarget(pos)) {
                it.remove();
            }
        }
    }

    @Override
    protected void fillBlocksToProcess(Collection<BlockPosition> targets) {
        int w = bbMax.x - bbMin.x + 1;
        int h = bbMax.z - bbMin.z + 1;
        for (int x = 0; x < w; x++) {
            for (int z = 0; z < h; z++) {
                if (isTarget(bbMin.x + x, bbMin.z + z)) {
                    targets.add(new BlockPosition(bbMin).offset(x, 0, z));
                }
            }
        }
    }

    public void onTargetsAdjusted() {
        //TODO implement to check target blocks, clear invalid ones
    }

    @Override
    protected void onBoundsSet() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                targetMap[z * 16 + x] = (byte) 1;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByteArray("targetMap", targetMap);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("targetMap") && tag.getTag("targetMap") instanceof NBTTagByteArray) {
            targetMap = tag.getByteArray("targetMap");
        }
    }

    public byte[] getTargetMap() {
        return targetMap;
    }

    public void setTargetBlocks(byte[] targets) {
        targetMap = targets;
    }

    @Override
    protected void updateBlockWorksite() {
        worldObj.theProfiler.startSection("Items Pickup");
        if (worldObj.getWorldTime() % 20 == 0) {
            pickupItems();
        }
        worldObj.theProfiler.endStartSection("Count Resources");
        if (shouldCountResources) {
            countResources();
        }
        worldObj.theProfiler.endSection();
    }

    protected void countResources(){
        shouldCountResources = false;
    }
}
