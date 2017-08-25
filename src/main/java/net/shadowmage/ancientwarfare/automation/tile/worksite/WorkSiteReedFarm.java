package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockReed;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WorkSiteReedFarm extends TileWorksiteUserBlocks {

    private final Set<BlockPos> cocoaToPlant;
    private final Set<BlockPos> cactusToPlant;
    private final Set<BlockPos> reedToPlant;
    private final Set<BlockPos> blocksToHarvest;
    private final Set<BlockPos> cocoaToGrow;

    private int reedCount;
    private int cactusCount;
    private int cocoaCount;
    private int bonemealCount;

    public WorkSiteReedFarm() {

        cocoaToPlant = new HashSet<>();
        cactusToPlant = new HashSet<>();
        reedToPlant = new HashSet<>();
        blocksToHarvest = new HashSet<>();
        cocoaToGrow = new HashSet<>();

        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(TOP_LENGTH);
        int[] frontIndices = helper.getIndiceArrayForSpread(FRONT_LENGTH);
        int[] bottomIndices = helper.getIndiceArrayForSpread(BOTTOM_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables
        this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return stack.isEmpty() || isCocoDye(stack) || stack.getItem() == Items.REEDS || Block.getBlockFromItem(stack.getItem()) instanceof BlockCactus;
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);

        filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return stack.isEmpty() || isBonemeal(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isCocoDye(ItemStack stack){
        return stack.getItem() == Items.DYE && stack.getItemDamage() == 3;
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(cocoaToPlant);
        validateCollection(blocksToHarvest);
        validateCollection(cactusToPlant);
        validateCollection(reedToPlant);
        validateCollection(cocoaToGrow);
    }

    @Override
    protected boolean processWork() {
        if (!blocksToHarvest.isEmpty()) {
            Iterator<BlockPos> it = blocksToHarvest.iterator();
            BlockPos p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if(harvestBlock(p)){
                    return true;
                }
            }
        } else if (cocoaCount > 0 && !cocoaToPlant.isEmpty()) {
            Iterator<BlockPos> it = cocoaToPlant.iterator();
            BlockPos p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if (plantCocoa(p)) {
                    return true;
                }
            }
        } else if (reedCount > 0 && !reedToPlant.isEmpty()) {
            Iterator<BlockPos> it = reedToPlant.iterator();
            BlockPos p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if (plantReeds(p)) {
                    return true;
                }
            }
        } else if (cactusCount > 0 && !cactusToPlant.isEmpty()) {
            Iterator<BlockPos> it = cactusToPlant.iterator();
            BlockPos p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if (plantCactus(p)) {
                    return true;
                }
            }
        } else if (bonemealCount > 0 && !cocoaToGrow.isEmpty()){
            Iterator<BlockPos> it = cocoaToGrow.iterator();
            BlockPos p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if (applyBonemeal(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean applyBonemeal(BlockPos p) {
        IBlockState state = world.getBlockState(p);
        Block block = state.getBlock();
        if (block instanceof BlockCocoa && ((BlockCocoa) block).canGrow(world, p, state, world.isRemote)) {
            @Nonnull ItemStack stack;
            for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                stack = getStackInSlot(i);
                if (stack.isEmpty()) {
                    continue;
                }
                if (isBonemeal(stack)) {
                    if (ItemDye.applyBonemeal(stack, world, p, getOwnerAsPlayer(), EnumHand.MAIN_HAND)) {
                        bonemealCount--;
                        if (stack.getCount() <= 0) {
                            setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }
                    if (((BlockCocoa) block).canGrow(world, p, state, world.isRemote)) {
                        cocoaToGrow.add(p);
                    } else {
                        blocksToHarvest.add(p);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean harvestBlock(BlockPos p) {
        Block block = world.getBlockState(p).getBlock();
        if (block instanceof BlockCactus || block instanceof BlockReed || block instanceof BlockCocoa) {
            return harvestBlock(p, RelativeSide.FRONT, RelativeSide.TOP);
        }
        return false;
    }

    private boolean plantCactus(BlockPos p) {
        if (!canReplace(p) || !Blocks.CACTUS.canBlockStay(world, p)) {
            return false;
        }
        @Nonnull ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == Item.getItemFromBlock(Blocks.CACTUS) && tryPlace(stack, p, EnumFacing.UP)){
                cactusCount--;
                return true;
            }
        }
        return false;
    }

    private boolean plantReeds(BlockPos p) {
        if (!canReplace(p) || !Blocks.REEDS.canBlockStay(world, p)) {
            return false;
        }
        @Nonnull ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() == Items.REEDS && tryPlace(stack, p, EnumFacing.UP)){
                reedCount--;
                return true;
            }
        }
        return false;
    }

    private boolean plantCocoa(BlockPos p) {
        if (!canReplace(p)) {
            return false;
        }
        EnumFacing meta = null;
        if (isJungleLog(p.west())) {
            meta = EnumFacing.EAST;
        } else if (isJungleLog(p.east())) {
            meta = EnumFacing.WEST;
        } else if (isJungleLog(p.north())) {
            meta = EnumFacing.SOUTH;
        } else if (isJungleLog(p.south())) {
            meta = EnumFacing.NORTH;
        }
        if(meta == null)
            return false;
        @Nonnull ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isCocoDye(stack) && tryPlace(stack, p, meta)) {
                cocoaCount--;
                return true;
            }
        }
        return false;
    }

    protected boolean isJungleLog(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == Blocks.LOG && state.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE;
    }

    @Override
    protected void countResources() {
        cactusCount = 0;
        reedCount = 0;
        cocoaCount = 0;
        bonemealCount = 0;
        @Nonnull ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isCocoDye(stack))
                cocoaCount += stack.getCount();
            else if(stack.getItem() == Items.REEDS)
                reedCount += stack.getCount();
            else if (Block.getBlockFromItem(stack.getItem()) instanceof BlockCactus)
                cactusCount += stack.getCount();
        }
        for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if(isBonemeal(stack)){
                bonemealCount += stack.getCount();
            }
        }
    }

    @Override
    protected int[] getIndicesForPickup(){
        return inventory.getRawIndicesCombined(RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FARMING;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_REED_FARM, pos);
        }
        return true;
    }

    @Override
    protected void scanBlockPosition(BlockPos scanPos) {
        Block block = world.getBlockState(scanPos).getBlock();
        if (block instanceof BlockCactus || block instanceof BlockReed) {//find top of cactus/reeds, harvest from top down (leave 1 at bottom)
            for (BlockPos current = scanPos.up(4); current.getY() > scanPos.getY(); current = current.down()) {
                if(world.getBlockState(current) == block) {
                    blocksToHarvest.add(current);
                }
            }
        } else if (block instanceof BlockCocoa) {
            if(!((IGrowable) block).canGrow(world, scanPos, world.getBlockState(scanPos), world.isRemote)) {
                blocksToHarvest.add(scanPos);
            }else{
                cocoaToGrow.add(scanPos);
            }
        } else if (block instanceof BlockAir)//check for plantability for each type
        {
            if (Blocks.CACTUS.canBlockStay(world, scanPos)) {
                cactusToPlant.add(scanPos);
            } else if (Blocks.REEDS.canBlockStay(world, scanPos)) {
                reedToPlant.add(scanPos);
            }else if (isJungleLog(scanPos.west()) || isJungleLog(scanPos.east()) || isJungleLog(scanPos.north()) || isJungleLog(scanPos.south())) {
                cocoaToPlant.add(scanPos);
            }
        }
    }

    @Override
    protected boolean hasWorksiteWork() {
        return (reedCount > 0 && !reedToPlant.isEmpty()) || (cactusCount > 0 && !cactusToPlant.isEmpty()) || (cocoaCount > 0 && !cocoaToPlant.isEmpty()) || !blocksToHarvest.isEmpty() || (bonemealCount>0 && !cocoaToGrow.isEmpty());
    }
}
