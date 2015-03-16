package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.TreeFinder;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WorkSiteMushroomFarm extends TileWorksiteUserBlocks {

    Set<BlockPosition> blocksToHarvest;
    Set<BlockPosition> blocksToPlantMushroom;
    Set<BlockPosition> blocksToPlantNetherWart;
    int mushroomCount;
    int netherWartCount;
    boolean shouldCountResources;

    public WorkSiteMushroomFarm() {
        this.shouldCountResources = true;

        blocksToHarvest = new HashSet<BlockPosition>();
        blocksToPlantMushroom = new HashSet<BlockPosition>();
        blocksToPlantNetherWart = new HashSet<BlockPosition>();

        this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 30) {
            @Override
            public void markDirty() {
                super.markDirty();
                shouldCountResources = true;
            }
        };
        int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
        int[] frontIndices = InventoryTools.getIndiceArrayForSpread(27, 3);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean isItemValid(ItemStack stack) {
                if (stack == null) {
                    return true;
                }
                Item item = stack.getItem();
                if (item == Items.nether_wart) {
                    return true;
                } else if (item instanceof ItemBlock) {
                    ItemBlock ib = (ItemBlock) item;
                    if (isMushroom(ib.field_150939_a)) {
                        return true;
                    }
                }
                return false;
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);
    }

    private boolean isMushroom(Block block) {
        return block instanceof BlockMushroom;
    }

    @Override
    public void onTargetsAdjusted() {
        validateCollection(blocksToPlantMushroom);
        validateCollection(blocksToHarvest);
        validateCollection(blocksToPlantNetherWart);
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(blocksToPlantMushroom);
        validateCollection(blocksToHarvest);
        validateCollection(blocksToPlantNetherWart);
    }

    @Override
    protected void updateBlockWorksite() {
        worldObj.theProfiler.startSection("Count Resources");
        if (shouldCountResources) {
            countResources();
        }
        worldObj.theProfiler.endSection();
    }

    private void countResources() {
        this.mushroomCount = 0;
        this.netherWartCount = 0;
        this.shouldCountResources = false;
        ItemStack item;
        for (int i = 27; i < 30; i++) {
            item = inventory.getStackInSlot(i);
            if (item == null) {
                continue;
            }
            if (item.getItem() == Items.nether_wart) {
                netherWartCount += item.stackSize;
            } else if (item.getItem() instanceof ItemBlock) {
                ItemBlock ib = (ItemBlock) item.getItem();
                if (isMushroom(ib.field_150939_a)) {
                    mushroomCount += item.stackSize;
                }
            }
        }
    }

    @Override
    protected boolean processWork() {
        Iterator<BlockPosition> it;
        if (!blocksToPlantMushroom.isEmpty()) {
            it = blocksToPlantMushroom.iterator();
            BlockPosition pos;
            ItemStack item;
            for (int i = 27; i < 30; i++) {
                item = inventory.getStackInSlot(i);
                if (item == null) {
                    continue;
                }
                if (item.getItem() instanceof ItemBlock) {
                    ItemBlock ib = (ItemBlock) item.getItem();
                    if (isMushroom(ib.field_150939_a)) {
                        while (it.hasNext() && (pos = it.next()) != null) {
                            it.remove();
                            if (ib.field_150939_a.canPlaceBlockAt(worldObj, pos.x, pos.y, pos.z)) {
                                worldObj.setBlock(pos.x, pos.y, pos.z, ib.field_150939_a);
                                //plant the mushroom, decrease stack size
                                item.stackSize--;
                                mushroomCount--;
                                if (item.stackSize <= 0) {
                                    inventory.setInventorySlotContents(i, null);
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                }
            }
        } else if (!blocksToPlantNetherWart.isEmpty()) {
            it = blocksToPlantNetherWart.iterator();
            BlockPosition pos;
            ItemStack item;
            for (int i = 27; i < 30; i++) {
                item = inventory.getStackInSlot(i);
                if (item == null) {
                    continue;
                }
                if (item.getItem() == Items.nether_wart) {
                    while (it.hasNext() && (pos = it.next()) != null) {
                        it.remove();
                        if (Blocks.nether_wart.canPlaceBlockAt(worldObj, pos.x, pos.y, pos.z)) {
                            worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.nether_wart);
                            item.stackSize--;
                            netherWartCount--;
                            if (item.stackSize <= 0) {
                                inventory.setInventorySlotContents(i, null);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            }
        } else if (!blocksToHarvest.isEmpty()) {
            it = blocksToHarvest.iterator();
            BlockPosition pos;
            Block block;
            while (it.hasNext() && (pos = it.next()) != null) {
                it.remove();
                block = worldObj.getBlock(pos.x, pos.y, pos.z);
                if (block == Blocks.nether_wart || block == Blocks.red_mushroom || block == Blocks.brown_mushroom_block) {
                    return harvestBlock(pos.x, pos.y, pos.z, RelativeSide.FRONT, RelativeSide.TOP);
                }
            }
        }
        return false;
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FARMING;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, xCoord, yCoord, zCoord);
        }
        return true;
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
        Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
        if (block.isAir(worldObj, pos.x, pos.y, pos.z)) {
            if (Blocks.nether_wart.canPlaceBlockAt(worldObj, pos.x, pos.y, pos.z)) {
                blocksToPlantNetherWart.add(pos);
            } else if (Blocks.brown_mushroom.canPlaceBlockAt(worldObj, pos.x, pos.y, pos.z)) {
                blocksToPlantMushroom.add(pos);
            }
        } else//not an air block, check for harvestable nether-wart
        {
            if (block == Blocks.nether_wart && worldObj.getBlockMetadata(pos.x, pos.y, pos.z) >= 3) {
                blocksToHarvest.add(pos);
            } else if (isMushroom(block)) {
                Set<BlockPosition> harvestSet = new HashSet<BlockPosition>();
                TreeFinder.findAttachedTreeBlocks(block, worldObj, pos.x, pos.y, pos.z, harvestSet);
                for (BlockPosition tp : harvestSet) {
                    if (!isTarget(tp) && !blocksToHarvest.contains(tp))//don't harvest user-set planting blocks...
                    {
                        blocksToHarvest.add(tp);
                    }
                }
            }
        }
    }

    @Override
    protected boolean hasWorksiteWork() {
        return (mushroomCount > 0 && !blocksToPlantMushroom.isEmpty()) || (netherWartCount > 0 && !blocksToPlantNetherWart.isEmpty()) || !blocksToHarvest.isEmpty();
    }

}
