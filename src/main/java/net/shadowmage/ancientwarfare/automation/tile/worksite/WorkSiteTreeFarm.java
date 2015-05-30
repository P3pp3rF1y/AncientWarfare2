package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.TreeFinder;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.*;

public class WorkSiteTreeFarm extends TileWorksiteUserBlocks {

    int saplingCount;
    int bonemealCount;
    Set<BlockPosition> blocksToChop;
    List<BlockPosition> blocksToPlant;
    List<BlockPosition> blocksToFertilize;

    public WorkSiteTreeFarm() {
        shouldCountResources = true;
        blocksToChop = new HashSet<BlockPosition>();
        blocksToPlant = new ArrayList<BlockPosition>();
        blocksToFertilize = new ArrayList<BlockPosition>();

        this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 33) {
            @Override
            public void markDirty() {
                super.markDirty();
                shouldCountResources = true;
            }
        };
        int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
        int[] frontIndices = InventoryTools.getIndiceArrayForSpread(27, 3);
        int[] bottomIndices = InventoryTools.getIndiceArrayForSpread(30, 3);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//saplings
        this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal
        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack == null || isSapling(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);
        filter = new ItemSlotFilter() {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack == null || isBonemeal(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isSapling(ItemStack stack) {
        return Block.getBlockFromItem(stack.getItem()) instanceof BlockSapling;
    }

    @Override
    public void onTargetsAdjusted() {
        validateCollection(blocksToFertilize);
        validateCollection(blocksToChop);
        validateCollection(blocksToPlant);
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(blocksToFertilize);
        validateCollection(blocksToChop);
        validateCollection(blocksToPlant);
    }

    @Override
    protected void countResources() {
        super.countResources();
        saplingCount = 0;
        bonemealCount = 0;
        ItemStack stack;
        for (int i = 27; i < inventory.getSizeInventory(); i++) {
            stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (i < 30 && isSapling(stack)) {
                saplingCount += stack.stackSize;
            } else if (i > 29 && isBonemeal(stack)) {
                bonemealCount += stack.stackSize;
            }
        }
    }

    @Override
    protected boolean processWork() {
        BlockPosition position;
        if (!blocksToChop.isEmpty()) {
            Iterator<BlockPosition> it = blocksToChop.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                if(harvestBlock(position.x, position.y, position.z, RelativeSide.TOP)){
                    return true;
                }
            }
        } else if (saplingCount > 0 && !blocksToPlant.isEmpty()) {
            ItemStack stack = null;
            for (int i = 27; i < 30; i++) {
                stack = inventory.getStackInSlot(i);
                if (stack != null && isSapling(stack)) {
                    break;
                } else {
                    stack = null;
                }
            }
            if (stack != null)//e.g. a sapling stack is present
            {
                Iterator<BlockPosition> it = blocksToPlant.iterator();
                while (it.hasNext() && (position = it.next()) != null) {
                    it.remove();
                    if (worldObj.isAirBlock(position.x, position.y, position.z)) {
                        if(tryPlace(stack, position.x, position.y, position.z, ForgeDirection.UP)) {
                            saplingCount--;
                            return true;
                        }
                    }
                }
            }
        } else if (bonemealCount > 0 && !blocksToFertilize.isEmpty()) {
            Iterator<BlockPosition> it = blocksToFertilize.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                Block block = worldObj.getBlock(position.x, position.y, position.z);
                if (block instanceof BlockSapling) {
                    ItemStack stack;
                    for (int i = 30; i < inventory.getSizeInventory(); i++) {
                        stack = inventory.getStackInSlot(i);
                        if (stack != null && isBonemeal(stack)) {
                            if(ItemDye.applyBonemeal(stack, worldObj, position.x, position.y, position.z, getOwnerAsPlayer())){
                                bonemealCount--;
                                if (stack.stackSize <= 0) {
                                    inventory.setInventorySlotContents(i, null);
                                }
                            }
                            block = worldObj.getBlock(position.x, position.y, position.z);
                            if (block instanceof BlockSapling) {
                                blocksToFertilize.add(position);//possible concurrent access exception?
                                //technically, it would be, except by the time it hits this inner block, it is already
                                //done iterating, as it will immediately hit the following break statement, and break
                                //out of the iterating loop before the next element would have been iterated over
                            } else if (block instanceof BlockLog) {
                                TreeFinder.findAttachedTreeBlocks(block, worldObj, position.x, position.y, position.z, blocksToChop);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    protected int[] getIndicesForPickup(){
        return inventory.getRawIndicesCombined(RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
    }

    private void addTreeBlocks(Block block, BlockPosition base) {
        worldObj.theProfiler.startSection("TreeFinder");
        TreeFinder.findAttachedTreeBlocks(block, worldObj, base.x, base.y, base.z, blocksToChop);
        worldObj.theProfiler.endSection();
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FORESTRY;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_TREE_FARM, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (!blocksToChop.isEmpty()) {
            NBTTagList chopList = new NBTTagList();
            for (BlockPosition position : blocksToChop) {
                chopList.appendTag(position.writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("targetList", chopList);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("targetList")) {
            NBTTagList chopList = tag.getTagList("targetList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < chopList.tagCount(); i++) {
                blocksToChop.add(new BlockPosition(chopList.getCompoundTagAt(i)));
            }
        }
        this.shouldCountResources = true;
    }

    @Override
    protected void scanBlockPosition(BlockPosition pos) {
        Block block;
        if (worldObj.isAirBlock(pos.x, pos.y, pos.z)) {
            block = worldObj.getBlock(pos.x, pos.y - 1, pos.z);
            if (block == Blocks.dirt || block == Blocks.grass) {
                blocksToPlant.add(pos.copy());
            }
        } else {
            block = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (block instanceof BlockSapling) {
                blocksToFertilize.add(pos.copy());
            } else if (block instanceof BlockLog && !blocksToChop.contains(pos)) {
                addTreeBlocks(block, pos);
            }
        }
    }

    @Override
    protected boolean hasWorksiteWork() {
        return (bonemealCount > 0 && !blocksToFertilize.isEmpty()) || (saplingCount > 0 && !blocksToPlant.isEmpty()) || !blocksToChop.isEmpty();
    }
}
