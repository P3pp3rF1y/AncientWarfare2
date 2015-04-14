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

    /**
     * flag should be set to true whenever updating inventory internally (e.g. harvesting blocks) to prevent
     * unnecessary inventory rescanning.  should be set back to false after blocks are added to inventory
     */
    private boolean shouldCountResources = true;
    int saplingCount;
    int bonemealCount;
    Set<BlockPosition> blocksToChop;
    List<BlockPosition> blocksToPlant;
    List<BlockPosition> blocksToFertilize;

    /**
     *
     */
    public WorkSiteTreeFarm() {
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
                if (stack == null) {
                    return true;
                }
                return isSapling(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);
        filter = new ItemSlotFilter() {
            @Override
            public boolean isItemValid(ItemStack stack) {
                if (stack == null) {
                    return true;
                }
                return isBonemeal(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isSapling(ItemStack stack) {
        return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).field_150939_a instanceof BlockSapling;
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

    private void countResources() {
        shouldCountResources = false;
        saplingCount = 0;
        bonemealCount = 0;
        ItemStack stack;
        for (int i = 27; i < 33; i++) {
            stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (isSapling(stack)) {
                saplingCount += stack.stackSize;
            } else if (isBonemeal(stack)) {
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
                return harvestBlock(position.x, position.y, position.z, RelativeSide.TOP);
            }
        } else if (saplingCount > 0 && !blocksToPlant.isEmpty()) {
            ItemStack stack = null;
            int slot = 27;
            for (int i = 27; i < 30; i++) {
                stack = inventory.getStackInSlot(i);
                if (stack != null && isSapling(stack)) {
                    slot = i;
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
                        Block block = ((ItemBlock) stack.getItem()).field_150939_a;
                        worldObj.setBlock(position.x, position.y, position.z, block, stack.getItemDamage(), 3);
                        saplingCount--;
                        inventory.decrStackSize(slot, 1);
                        return true;
                    }
                }
            }
        } else if (bonemealCount > 0 && !blocksToFertilize.isEmpty()) {
            Iterator<BlockPosition> it = blocksToFertilize.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                Block block = worldObj.getBlock(position.x, position.y, position.z);
                if (block instanceof BlockSapling) {
                    ItemStack stack = null;
                    for (int i = 30; i < 33; i++) {
                        stack = inventory.getStackInSlot(i);
                        if (stack != null && isBonemeal(stack)) {
                            bonemealCount--;
                            ItemDye.applyBonemeal(stack, worldObj, position.x, position.y, position.z, getOwnerAsPlayer());
                            if (stack.stackSize <= 0) {
                                inventory.setInventorySlotContents(i, null);
                            }
                            block = worldObj.getBlock(position.x, position.y, position.z);
                            if (block instanceof BlockSapling) {
                                blocksToFertilize.add(position);//possible concurrent access exception?
                                //technically, it would be, except by the time it hits this inner block, it is already
                                //done iterating, as it will immediately hit the following break statement, and break
                                //out of the iterating loop before the next element would have been iterated over
                            } else if (block.getMaterial() == Material.wood) {
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

    @SuppressWarnings("unchecked")
    private void pickupSaplings() {
        BlockPosition p1 = getWorkBoundsMin();
        BlockPosition p2 = getWorkBoundsMax().copy().offset(1, 1, 1);
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
        List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, bb);
        ItemStack stack;
        for (EntityItem item : items) {
            stack = item.getEntityItem();
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == Items.apple) {
                item.setDead();
                addStackToInventory(stack, RelativeSide.TOP);
                continue;
            }
            if (isSapling(stack)) {
                if (!InventoryTools.canInventoryHold(inventory, inventory.getRawIndicesCombined(RelativeSide.FRONT, RelativeSide.TOP), stack)) {
                    break;
                }
                item.setDead();
                addStackToInventory(stack, RelativeSide.FRONT, RelativeSide.TOP);
            }
        }
    }

    private void addTreeBlocks(BlockPosition base) {
        worldObj.theProfiler.startSection("TreeFinder");
        TreeFinder.findAttachedTreeBlocks(worldObj.getBlock(base.x, base.y, base.z), worldObj, base.x, base.y, base.z, blocksToChop);
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
            NBTTagCompound posTag;
            for (BlockPosition position : blocksToChop) {
                posTag = new NBTTagCompound();
                position.writeToNBT(posTag);
                chopList.appendTag(posTag);
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
    protected void fillBlocksToProcess(Collection<BlockPosition> targets) {
        int w = bbMax.x - bbMin.x + 1;
        int h = bbMax.z - bbMin.z + 1;
        BlockPosition p;
        for (int x = 0; x < w; x++) {
            for (int z = 0; z < h; z++) {
                if (isTarget(bbMin.x + x, bbMin.z + z)) {
                    p = new BlockPosition(bbMin);
                    p.offset(x, 0, z);
                    targets.add(p);
                }
            }
        }
    }

    @Override
    protected void scanBlockPosition(BlockPosition pos) {
        Block block;
        if (worldObj.isAirBlock(pos.x, pos.y, pos.z)) {
            block = worldObj.getBlock(pos.x, pos.y - 1, pos.z);
            if (block == Blocks.dirt || block == Blocks.grass) {
                blocksToPlant.add(pos.copy().reassign(pos.x, pos.y, pos.z));
            }
        } else {
            block = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (block instanceof BlockSapling) {
                blocksToFertilize.add(pos.copy().reassign(pos.x, pos.y, pos.z));
            } else if (block instanceof BlockLog && !blocksToChop.contains(pos)) {
                BlockPosition p1 = pos.copy().reassign(pos.x, pos.y, pos.z);
                if (!blocksToChop.contains(p1)) {
                    addTreeBlocks(p1);
                }
            }
        }
    }

    @Override
    protected boolean hasWorksiteWork() {
        return (bonemealCount > 0 && !blocksToFertilize.isEmpty()) || (saplingCount > 0 && !blocksToPlant.isEmpty()) || !blocksToChop.isEmpty();
    }

    @Override
    protected void updateBlockWorksite() {
        worldObj.theProfiler.startSection("Count Resources");
        if (shouldCountResources) {
            countResources();
        }
        worldObj.theProfiler.endStartSection("SaplingPickup");
        if (worldObj.getWorldTime() % 20 == 0) {
            pickupSaplings();
        }
        worldObj.theProfiler.endSection();
    }

}
