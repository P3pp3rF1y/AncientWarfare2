package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WorkSiteReedFarm extends TileWorksiteUserBlocks {

    private final Set<BlockPosition> cocoaToPlant;
    private final Set<BlockPosition> cactusToPlant;
    private final Set<BlockPosition> reedToPlant;
    private final Set<BlockPosition> blocksToHarvest;
    private final Set<BlockPosition> cocoaToGrow;

    private int reedCount;
    private int cactusCount;
    private int cocoaCount;
    private int bonemealCount;

    public WorkSiteReedFarm() {

        cocoaToPlant = new HashSet<BlockPosition>();
        cactusToPlant = new HashSet<BlockPosition>();
        reedToPlant = new HashSet<BlockPosition>();
        blocksToHarvest = new HashSet<BlockPosition>();
        cocoaToGrow = new HashSet<BlockPosition>();

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
            Iterator<BlockPosition> it = blocksToHarvest.iterator();
            BlockPosition p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if(harvestBlock(p)){
                    return true;
                }
            }
        } else if (cocoaCount > 0 && !cocoaToPlant.isEmpty()) {
            Iterator<BlockPosition> it = cocoaToPlant.iterator();
            BlockPosition p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if (plantCocoa(p)) {
                    return true;
                }
            }
        } else if (reedCount > 0 && !reedToPlant.isEmpty()) {
            Iterator<BlockPosition> it = reedToPlant.iterator();
            BlockPosition p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if (plantReeds(p)) {
                    return true;
                }
            }
        } else if (cactusCount > 0 && !cactusToPlant.isEmpty()) {
            Iterator<BlockPosition> it = cactusToPlant.iterator();
            BlockPosition p;
            while (it.hasNext()) {
                p = it.next();
                it.remove();
                if (plantCactus(p)) {
                    return true;
                }
            }
        } else if (bonemealCount > 0 && !cocoaToGrow.isEmpty()){
            Iterator<BlockPosition> it = cocoaToGrow.iterator();
            BlockPosition p;
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

    private boolean applyBonemeal(BlockPosition p) {
        Block block = worldObj.getBlock(p.x, p.y, p.z);
        if (block instanceof BlockCocoa && ((BlockCocoa) block).func_149851_a(worldObj, p.x, p.y, p.z, worldObj.isRemote)) {
            ItemStack stack;
            for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                stack = getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                if (isBonemeal(stack)) {
                    if (ItemDye.applyBonemeal(stack, worldObj, p.x, p.y, p.z, getOwnerAsPlayer())) {
                        bonemealCount--;
                        if (stack.stackSize <= 0) {
                            setInventorySlotContents(i, null);
                        }
                    }
                    if (((BlockCocoa) block).func_149851_a(worldObj, p.x, p.y, p.z, worldObj.isRemote)) {
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

    private boolean harvestBlock(BlockPosition p) {
        Block block = worldObj.getBlock(p.x, p.y, p.z);
        if (block instanceof BlockCactus || block instanceof BlockReed || block instanceof BlockCocoa) {
            return harvestBlock(p.x, p.y, p.z, RelativeSide.FRONT, RelativeSide.TOP);
        }
        return false;
    }

    private boolean plantCactus(BlockPosition p) {
        if (!canReplace(p.x, p.y, p.z) || !Blocks.cactus.canBlockStay(worldObj, p.x, p.y, p.z)) {
            return false;
        }
        ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == Item.getItemFromBlock(Blocks.cactus) && tryPlace(stack, p.x, p.y, p.z, ForgeDirection.UP)){
                cactusCount--;
                return true;
            }
        }
        return false;
    }

    private boolean plantReeds(BlockPosition p) {
        if (!canReplace(p.x, p.y, p.z) || !Blocks.reeds.canBlockStay(worldObj, p.x, p.y, p.z)) {
            return false;
        }
        ItemStack stack;
        for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (stack.getItem() == Items.reeds && tryPlace(stack, p.x, p.y, p.z, ForgeDirection.UP)){
                reedCount--;
                return true;
            }
        }
        return false;
    }

    private boolean plantCocoa(BlockPosition p) {
        if (!canReplace(p.x, p.y, p.z)) {
            return false;
        }
        ForgeDirection meta = null;
        if (isJungleLog(p.x - 1, p.y, p.z)) {
            meta = ForgeDirection.EAST;
        } else if (isJungleLog(p.x + 1, p.y, p.z)) {
            meta = ForgeDirection.WEST;
        } else if (isJungleLog(p.x, p.y, p.z - 1)) {
            meta = ForgeDirection.SOUTH;
        } else if (isJungleLog(p.x, p.y, p.z + 1)) {
            meta = ForgeDirection.NORTH;
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
        return worldObj.getBlock(x, y, z) == Blocks.log && BlockLog.func_150165_c(worldObj.getBlockMetadata(x, y, z)) == 3;
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
                cocoaCount += stack.stackSize;
            else if(stack.getItem() == Items.reeds)
                reedCount += stack.stackSize;
            else if (Block.getBlockFromItem(stack.getItem()) instanceof BlockCactus)
                cactusCount += stack.stackSize;
        }
        for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if(isBonemeal(stack)){
                bonemealCount += stack.stackSize;
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
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_REED_FARM, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    protected void scanBlockPosition(BlockPosition pos) {
        Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
        if (block instanceof BlockCactus || block instanceof BlockReed)//find top of cactus/reeds, harvest from top down (leave 1 at bottom)
        {
            for (int y = pos.y + 4; y > pos.y; y--) {
                if(worldObj.getBlock(pos.x, y, pos.z) == block) {
                    blocksToHarvest.add(new BlockPosition(pos.x, y, pos.z));
                }
            }
        } else if (block instanceof BlockCocoa) {
            if(!((IGrowable) block).func_149851_a(worldObj, pos.x, pos.y, pos.z, worldObj.isRemote)) {
                blocksToHarvest.add(pos.copy());
            }else{
                cocoaToGrow.add(pos.copy());
            }
        } else if (block instanceof BlockAir)//check for plantability for each type
        {
            if (Blocks.cactus.canBlockStay(worldObj, pos.x, pos.y, pos.z)) {
                cactusToPlant.add(pos.copy());
            } else if (Blocks.reeds.canBlockStay(worldObj, pos.x, pos.y, pos.z)) {
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
