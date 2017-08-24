package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.shadowmage.ancientwarfare.automation.tile.TreeFinder;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WorkSiteMushroomFarm extends TileWorksiteUserBlocks {

    private final Set<BlockPos> blocksToHarvest;
    private final Set<BlockPos> blocksToPlantMushroom;
    private final Set<BlockPos> blocksToPlantNetherWart;
    private int mushroomCount;
    private int netherWartCount;

    public WorkSiteMushroomFarm() {

        blocksToHarvest = new HashSet<BlockPos>();
        blocksToPlantMushroom = new HashSet<BlockPos>();
        blocksToPlantNetherWart = new HashSet<BlockPos>();

        this.inventory = new SlotListener(TOP_LENGTH + FRONT_LENGTH);
        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(TOP_LENGTH);
        int[] frontIndices = helper.getIndiceArrayForSpread(FRONT_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                if (stack == null || stack.getItem() == Items.NETHER_WART) {
                    return true;
                } else {
                    Block block = Block.getBlockFromItem(stack.getItem());
                    return isFarmable(block);
                }
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);
    }

    @Override
    protected boolean isFarmable(Block block, int x, int y, int z){
        if(super.isFarmable(block, x, y, z)) {
            EnumPlantType type = ((IPlantable) block).getPlantType(world, x, y, z);
            return type == EnumPlantType.Cave || type == EnumPlantType.Nether;
        }
        return false;
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(blocksToPlantMushroom);
        validateCollection(blocksToHarvest);
        validateCollection(blocksToPlantNetherWart);
    }

    @Override
    protected void countResources() {
        this.mushroomCount = 0;
        this.netherWartCount = 0;
        ItemStack item;
        for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
            item = getStackInSlot(i);
            if (item == null) {
                continue;
            }
            if (item.getItem() == Items.NETHER_WART) {
                netherWartCount += item.getCount();
            } else {
                Block block = Block.getBlockFromItem(item.getItem());
                if(isFarmable(block))
                    mushroomCount += item.getCount();
            }
        }
    }

    @Override
    protected int[] getIndicesForPickup(){
        return inventory.getRawIndicesCombined(RelativeSide.FRONT, RelativeSide.TOP);
    }

    @Override
    protected boolean processWork() {
        Iterator<BlockPos> it;
        if (!blocksToHarvest.isEmpty()) {
            it = blocksToHarvest.iterator();
            BlockPos pos;
            Block block;
            while (it.hasNext() && (pos = it.next()) != null) {
                it.remove();
                block = world.getBlock(pos.x, pos.y, pos.z);
                if (block instanceof BlockHugeMushroom || isFarmable(block, pos.x, pos.y, pos.z)) {
                    return harvestBlock(pos.x, pos.y, pos.z, RelativeSide.FRONT, RelativeSide.TOP);
                }
            }
        }
        else if (mushroomCount>0 && !blocksToPlantMushroom.isEmpty()) {
            it = blocksToPlantMushroom.iterator();
            BlockPos pos;
            ItemStack item;
            for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
                item = getStackInSlot(i);
                if (item == null) {
                    continue;
                }
                Block block = Block.getBlockFromItem(item.getItem());
                if(isFarmable(block)) {
                    while (it.hasNext() && (pos = it.next()) != null) {
                        it.remove();
                        if(tryPlace(item, pos.x, pos.y, pos.z, EnumFacing.UP)) {//plant the mushroom, decrease stack size
                            mushroomCount--;
                            if (item.getCount() <= 0) {
                                setInventorySlotContents(i, ItemStack.EMPTY);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            }
        } else if (netherWartCount>0 && !blocksToPlantNetherWart.isEmpty()) {
            it = blocksToPlantNetherWart.iterator();
            BlockPos pos;
            ItemStack item;
            for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
                item = getStackInSlot(i);
                if (item == null) {
                    continue;
                }
                if (item.getItem() == Items.NETHER_WART) {
                    while (it.hasNext() && (pos = it.next()) != null) {
                        it.remove();
                        if(tryPlace(item, pos.x, pos.y, pos.z, EnumFacing.UP)) {
                            netherWartCount--;
                            if (item.getCount() <= 0) {
                                setInventorySlotContents(i, ItemStack.EMPTY);
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
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, pos);
        }
        return true;
    }

    @Override
    protected void scanBlockPosition(BlockPos pos) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        if (block.isReplaceable(world, pos.x, pos.y, pos.z)) {
            if (Blocks.nether_wart.canPlaceBlockAt(world, pos.x, pos.y, pos.z)) {
                blocksToPlantNetherWart.add(pos);
            } else if (Blocks.brown_mushroom.canPlaceBlockAt(world, pos.x, pos.y, pos.z)) {
                blocksToPlantMushroom.add(pos);
            }
        } else//not an air block, check for harvestable nether-wart
        {
            if (block == Blocks.nether_wart){
                if(world.getBlockMetadata(pos.x, pos.y, pos.z) >= 3)
                    blocksToHarvest.add(pos);
            } else if (block instanceof BlockHugeMushroom && !blocksToHarvest.contains(pos)) {
                TreeFinder.DEFAULT.findAttachedTreeBlocks(block, world, pos, blocksToHarvest);
            } else if (isFarmable(block, pos.x, pos.y, pos.z)){
                Set<BlockPos> harvestSet = new HashSet<BlockPos>();
                TreeFinder.DEFAULT.findAttachedTreeBlocks(block, world, pos, harvestSet);
                for (BlockPos tp : harvestSet) {
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
