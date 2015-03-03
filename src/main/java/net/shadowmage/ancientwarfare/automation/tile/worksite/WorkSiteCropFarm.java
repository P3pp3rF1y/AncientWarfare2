package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WorkSiteCropFarm extends TileWorksiteUserBlocks {

    Set<BlockPosition> blocksToTill;
    Set<BlockPosition> blocksToHarvest;
    Set<BlockPosition> blocksToPlant;
    Set<BlockPosition> blocksToFertilize;

    int plantableCount;
    int bonemealCount;
    boolean shouldCountResources;

    public WorkSiteCropFarm() {
        this.shouldCountResources = true;

        blocksToTill = new HashSet<BlockPosition>();
        blocksToHarvest = new HashSet<BlockPosition>();
        blocksToPlant = new HashSet<BlockPosition>();
        blocksToFertilize = new HashSet<BlockPosition>();

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
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables
        this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack == null || isPlantable(stack.getItem());
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

    private boolean isPlantable(Item item){
        return item instanceof IPlantable;
    }

    private boolean isPlant(Block block){
        return block instanceof BlockCrops || block instanceof BlockStem;
    }

    @Override
    public void onTargetsAdjusted() {
        validateCollection(blocksToFertilize);
        validateCollection(blocksToHarvest);
        validateCollection(blocksToPlant);
        validateCollection(blocksToTill);
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(blocksToFertilize);
        validateCollection(blocksToHarvest);
        validateCollection(blocksToPlant);
        validateCollection(blocksToTill);
    }

    private void countResources() {
        shouldCountResources = false;
        plantableCount = 0;
        bonemealCount = 0;
        ItemStack stack;
        for (int i = 27; i < 30; i++) {
            stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (isPlantable(stack.getItem())) {
                plantableCount += stack.stackSize;
            }
        }
        for (int i = 30; i < 33; i++) {
            stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (isBonemeal(stack)) {
                bonemealCount += stack.stackSize;
            }
        }
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
    protected void scanBlockPosition(BlockPosition position) {
        Block block = worldObj.getBlock(position.x, position.y, position.z);
        if (block.isAir(worldObj, position.x, position.y, position.z)) {
            block = worldObj.getBlock(position.x, position.y - 1, position.z);
            if (block == Blocks.dirt || block == Blocks.grass) {
                blocksToTill.add(new BlockPosition(position.x, position.y - 1, position.z));
            } else if (block == Blocks.farmland) {
                blocksToPlant.add(position);
            }
        } else if (block instanceof BlockCrops) {
            if (!((IGrowable)block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
                blocksToHarvest.add(position);
            } else {
                blocksToFertilize.add(position);
            }
        } else if (block instanceof BlockStem) {
            if (!((IGrowable)block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
                block = worldObj.getBlock(position.x - 1, position.y, position.z);
                if (block == Blocks.melon_block || block == Blocks.pumpkin) {
                    blocksToHarvest.add(new BlockPosition(position.x - 1, position.y, position.z));
                }
                block = worldObj.getBlock(position.x + 1, position.y, position.z);
                if (block == Blocks.melon_block || block == Blocks.pumpkin) {
                    blocksToHarvest.add(new BlockPosition(position.x + 1, position.y, position.z));
                }
                block = worldObj.getBlock(position.x, position.y, position.z - 1);
                if (block == Blocks.melon_block || block == Blocks.pumpkin) {
                    blocksToHarvest.add(new BlockPosition(position.x, position.y, position.z - 1));
                }
                block = worldObj.getBlock(position.x, position.y, position.z + 1);
                if (block == Blocks.melon_block || block == Blocks.pumpkin) {
                    blocksToHarvest.add(new BlockPosition(position.x, position.y, position.z + 1));
                }
            } else {
                blocksToFertilize.add(position);
            }
        }
    }

    @Override
    protected boolean processWork() {
        Iterator<BlockPosition> it;
        BlockPosition position;
        Block block;
        if (!blocksToTill.isEmpty()) {
            it = blocksToTill.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = worldObj.getBlock(position.x, position.y, position.z);
                if ((block == Blocks.grass || block == Blocks.dirt) && worldObj.isAirBlock(position.x, position.y + 1, position.z)) {
                    worldObj.setBlock(position.x, position.y, position.z, Blocks.farmland);
                    return true;
                }
            }
        } else if (!blocksToHarvest.isEmpty()) {
            it = blocksToHarvest.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = worldObj.getBlock(position.x, position.y, position.z);
                if (block instanceof BlockCrops) {
                    if (((IGrowable)block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
                        continue;
                    }
                    return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                } else if (block == Blocks.pumpkin || block == Blocks.melon_block) {
                    return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                }
            }
        } else if (!blocksToPlant.isEmpty() && plantableCount > 0) {
            it = blocksToPlant.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                if (worldObj.getBlock(position.x, position.y - 1, position.z) == Blocks.farmland && worldObj.isAirBlock(position.x, position.y, position.z)) {
                    ItemStack stack;
                    for (int i = 27; i < 30; i++) {
                        stack = inventory.getStackInSlot(i);
                        if (stack == null) {
                            continue;
                        }
                        if (isPlantable(stack.getItem())) {
                            plantableCount--;
                            stack.stackSize--;
                            if (stack.stackSize <= 0) {
                                inventory.setInventorySlotContents(i, null);
                            }
                            block = ((IPlantable)stack.getItem()).getPlant(worldObj, position.x, position.y, position.z);
                            worldObj.setBlock(position.x, position.y, position.z, block, ((IPlantable)stack.getItem()).getPlantMetadata(worldObj, position.x, position.y, position.z), 3);
                            return true;
                        }
                    }
                    return false;
                }
            }
        } else if (!blocksToFertilize.isEmpty() && bonemealCount > 0) {
            it = blocksToFertilize.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = worldObj.getBlock(position.x, position.y, position.z);
                if (isPlant(block)) {
                    ItemStack stack;
                    for (int i = 30; i < 33; i++) {
                        stack = inventory.getStackInSlot(i);
                        if (stack == null) {
                            continue;
                        }
                        if (isBonemeal(stack)) {
                            bonemealCount--;
                            ItemDye.applyBonemeal(stack, worldObj, position.x, position.y, position.z, getOwnerAsPlayer());
                            if (stack.stackSize <= 0) {
                                inventory.setInventorySlotContents(i, null);
                            }
                            block = worldObj.getBlock(position.x, position.y, position.z);
                            if (isPlant(block)) {
                                if (((IGrowable)block).func_149851_a(worldObj, position.x, position.y, position.z, worldObj.isRemote)) {
                                    blocksToFertilize.add(position);
                                } else {
                                    if (block instanceof BlockCrops) {
                                        blocksToHarvest.add(position);
                                    }
                                }
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
    public WorkType getWorkType() {
        return WorkType.FARMING;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_CROP_FARM, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    protected boolean hasWorksiteWork() {
        return (plantableCount > 0 && !blocksToPlant.isEmpty()) || (bonemealCount > 0 && !blocksToFertilize.isEmpty()) || !blocksToTill.isEmpty() || !blocksToHarvest.isEmpty();
    }

    @Override
    protected void updateBlockWorksite() {
        worldObj.theProfiler.startSection("Count Resources");
        if (shouldCountResources) {
            countResources();
        }
        worldObj.theProfiler.endSection();
    }

}
