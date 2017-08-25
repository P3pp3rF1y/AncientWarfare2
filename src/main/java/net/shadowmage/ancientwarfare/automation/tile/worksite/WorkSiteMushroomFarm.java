package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.shadowmage.ancientwarfare.automation.tile.TreeFinder;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
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

        blocksToHarvest = new HashSet<>();
        blocksToPlantMushroom = new HashSet<>();
        blocksToPlantNetherWart = new HashSet<>();

        this.inventory = new SlotListener(TOP_LENGTH + FRONT_LENGTH);
        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(TOP_LENGTH);
        int[] frontIndices = helper.getIndiceArrayForSpread(FRONT_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean apply(@Nullable ItemStack stack) {
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
    protected boolean isFarmable(Block block, BlockPos farmablePos){
        if(super.isFarmable(block, farmablePos)) {
            EnumPlantType type = ((IPlantable) block).getPlantType(world, farmablePos);
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
        @Nonnull ItemStack item;
        for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
            item = getStackInSlot(i);
            if (item.isEmpty()) {
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
            BlockPos harvestPos;
            Block block;
            while (it.hasNext() && (harvestPos = it.next()) != null) {
                it.remove();
                block = world.getBlockState(harvestPos).getBlock();
                if (block instanceof BlockHugeMushroom || isFarmable(block,harvestPos)) {
                    return harvestBlock(harvestPos, RelativeSide.FRONT, RelativeSide.TOP);
                }
            }
        }
        else if (mushroomCount>0 && !blocksToPlantMushroom.isEmpty()) {
            it = blocksToPlantMushroom.iterator();
            BlockPos plantPos;
            @Nonnull ItemStack item;
            for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
                item = getStackInSlot(i);
                if (item.isEmpty()) {
                    continue;
                }
                Block block = Block.getBlockFromItem(item.getItem());
                if(isFarmable(block)) {
                    while (it.hasNext() && (plantPos = it.next()) != null) {
                        it.remove();
                        if(tryPlace(item, plantPos, EnumFacing.UP)) {//plant the mushroom, decrease stack size
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
            BlockPos plantPos;
            @Nonnull ItemStack item;
            for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
                item = getStackInSlot(i);
                if (item.isEmpty()) {
                    continue;
                }
                if (item.getItem() == Items.NETHER_WART) {
                    while (it.hasNext() && (plantPos = it.next()) != null) {
                        it.remove();
                        if(tryPlace(item, plantPos, EnumFacing.UP)) {
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
    public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, pos);
        }
        return true;
    }

    @Override
    protected void scanBlockPosition(BlockPos scanPos) {
        IBlockState state = world.getBlockState(scanPos);
        Block block = state.getBlock();
        if (block.isReplaceable(world, scanPos)) {
            if (Blocks.NETHER_WART.canPlaceBlockAt(world, scanPos)) {
                blocksToPlantNetherWart.add(scanPos);
            } else if (Blocks.BROWN_MUSHROOM.canPlaceBlockAt(world, scanPos)) {
                blocksToPlantMushroom.add(scanPos);
            }
        } else//not an air block, check for harvestable nether-wart
        {
            if (block == Blocks.NETHER_WART){
                if(state.getValue(BlockNetherWart.AGE) >= 3)
                    blocksToHarvest.add(scanPos);
            } else if (block instanceof BlockHugeMushroom && !blocksToHarvest.contains(scanPos)) {
                TreeFinder.DEFAULT.findAttachedTreeBlocks(block, world, scanPos, blocksToHarvest);
            } else if (isFarmable(block, scanPos)){
                Set<BlockPos> harvestSet = new HashSet<>();
                TreeFinder.DEFAULT.findAttachedTreeBlocks(block, world, scanPos, harvestSet);
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
