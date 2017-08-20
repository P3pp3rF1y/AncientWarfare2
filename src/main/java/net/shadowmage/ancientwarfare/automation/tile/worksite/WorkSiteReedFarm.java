package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

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

        cocoaToPlant = new HashSet<BlockPos>();
        cactusToPlant = new HashSet<BlockPos>();
        reedToPlant = new HashSet<BlockPos>();
        blocksToHarvest = new HashSet<BlockPos>();
        cocoaToGrow = new HashSet<BlockPos>();

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
                return stack == null || isCocoDye(stack) || stack.getItem() == Items.reeds || Block.getBlockFromItem(stack.getItem()) instanceof BlockCactus;
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);

        filter = new ItemSlotFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return stack == null || isBonemeal(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isCocoDye(ItemStack stack){
        return stack.getItem() == Items.dye && stack.getItemDamage() == 3;
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
        Block block = world.getBlock(p.x, p.y, p.z);
        if (block instanceof BlockCocoa && ((BlockCocoa) block).func_149851_a(world, p.x, p.y, p.z, world.isRemote)) {
            ItemStack stack;
            for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                stack = getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                if (isBonemeal(stack)) {
                    if (ItemDye.applyBonemeal(stack, world, p.x, p.y, p.z, getOwnerAsPlayer())) {
                        bonemealCount--;
                        if (stack.getCount() <= 0) {
                            setInventorySlotContents(i, null);
                        }
                    }
                    if (((BlockCocoa) block).func_149851_a(world, p.x, p.y, p.z, world.isRemote)) {
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
        Block block = world.getBlock(p.x, p.y, p.z);
        if (block instanceof BlockCactus || block instanceof BlockReed || block instanceof BlockCocoa) {
            return harvestBlock(p.x, p.y, p.z, RelativeSide.FRONT, RelativeSide.TOP);
        }
        return false;
    }

    private boolean plantCactus(BlockPos p) {
        if (!canReplace(p.x, p.y, p.z) || !Blocks.cactus.canBlockStay(world, p.x, p.y, p.z)) {
            return false;
        }
        ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == Item.getItemFromBlock(Blocks.cactus) && tryPlace(stack, p.x, p.y, p.z, EnumFacing.UP)){
                cactusCount--;
                return true;
            }
        }
        return false;
    }

    private boolean plantReeds(BlockPos p) {
        if (!canReplace(p.x, p.y, p.z) || !Blocks.reeds.canBlockStay(world, p.x, p.y, p.z)) {
            return false;
        }
        ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == Items.reeds && tryPlace(stack, p.x, p.y, p.z, EnumFacing.UP)){
                reedCount--;
                return true;
            }
        }
        return false;
    }

    private boolean plantCocoa(BlockPos p) {
        if (!canReplace(p.x, p.y, p.z)) {
            return false;
        }
        EnumFacing meta = null;
        if (isJungleLog(p.x - 1, p.y, p.z)) {
            meta = EnumFacing.EAST;
        } else if (isJungleLog(p.x + 1, p.y, p.z)) {
            meta = EnumFacing.WEST;
        } else if (isJungleLog(p.x, p.y, p.z - 1)) {
            meta = EnumFacing.SOUTH;
        } else if (isJungleLog(p.x, p.y, p.z + 1)) {
            meta = EnumFacing.NORTH;
        }
        if(meta == null)
            return false;
        ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (isCocoDye(stack) && tryPlace(stack, p.x, p.y, p.z, meta)) {
                cocoaCount--;
                return true;
            }
        }
        return false;
    }

    protected boolean isJungleLog(int x, int y, int z) {
        return world.getBlock(x, y, z) == Blocks.log && BlockLog.func_150165_c(world.getBlockMetadata(x, y, z)) == 3;
    }

    @Override
    protected void countResources() {
        cactusCount = 0;
        reedCount = 0;
        cocoaCount = 0;
        bonemealCount = 0;
        ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (isCocoDye(stack))
                cocoaCount += stack.getCount();
            else if(stack.getItem() == Items.reeds)
                reedCount += stack.getCount();
            else if (Block.getBlockFromItem(stack.getItem()) instanceof BlockCactus)
                cactusCount += stack.getCount();
        }
        for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
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
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_REED_FARM, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    protected void scanBlockPosition(BlockPos pos) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        if (block instanceof BlockCactus || block instanceof BlockReed)//find top of cactus/reeds, harvest from top down (leave 1 at bottom)
        {
            for (int y = pos.y + 4; y > pos.y; y--) {
                if(world.getBlock(pos.x, y, pos.z) == block) {
                    blocksToHarvest.add(new BlockPos(pos.x, y, pos.z));
                }
            }
        } else if (block instanceof BlockCocoa) {
            if(!((IGrowable) block).func_149851_a(world, pos.x, pos.y, pos.z, world.isRemote)) {
                blocksToHarvest.add(pos.copy());
            }else{
                cocoaToGrow.add(pos.copy());
            }
        } else if (block instanceof BlockAir)//check for plantability for each type
        {
            if (Blocks.cactus.canBlockStay(world, pos.x, pos.y, pos.z)) {
                cactusToPlant.add(pos.copy());
            } else if (Blocks.reeds.canBlockStay(world, pos.x, pos.y, pos.z)) {
                reedToPlant.add(pos.copy());
            }else if (isJungleLog(pos.x - 1, pos.y, pos.z) || isJungleLog(pos.x + 1, pos.y, pos.z) || isJungleLog(pos.x, pos.y, pos.z - 1) || isJungleLog(pos.x, pos.y, pos.z + 1)) {
                cocoaToPlant.add(pos.copy());
            }
        }
    }

    @Override
    protected boolean hasWorksiteWork() {
        return (reedCount > 0 && !reedToPlant.isEmpty()) || (cactusCount > 0 && !cactusToPlant.isEmpty()) || (cocoaCount > 0 && !cocoaToPlant.isEmpty()) || !blocksToHarvest.isEmpty() || (bonemealCount>0 && !cocoaToGrow.isEmpty());
    }
}
