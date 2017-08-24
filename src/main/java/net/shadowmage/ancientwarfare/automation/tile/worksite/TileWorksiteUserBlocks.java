package net.shadowmage.ancientwarfare.automation.tile.worksite;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.shadowmage.ancientwarfare.api.IAncientWarfarePlantable;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TileWorksiteUserBlocks extends TileWorksiteBlockBased {

    protected static final int TOP_LENGTH = 27, FRONT_LENGTH = 3, BOTTOM_LENGTH = 3;
    private static final int SIZE = 16;
    private byte[] targetMap = new byte[SIZE * SIZE];

    /**
     * flag should be set to true whenever updating inventory internally (e.g. harvesting blocks) to prevent
     * unnecessary inventory rescanning.  should be set back to false after blocks are added to inventory
     */
    private boolean shouldCountResources;

    public TileWorksiteUserBlocks() {
        this.shouldCountResources = true;
        this.inventory = new SlotListener(TOP_LENGTH + FRONT_LENGTH + BOTTOM_LENGTH);
    }

    @Override
    public final boolean userAdjustableBlocks() {
        return true;
    }

    protected boolean isTarget(BlockPos p) {
        return isTarget(p.getX(), p.getZ());
    }

    protected boolean isTarget(int x1, int y1) {
        int z = (y1 - getWorkBoundsMin().getZ()) * SIZE + x1 - getWorkBoundsMin().getX();
        return z >= 0 && z < targetMap.length && targetMap[z] == 1;
    }

    protected boolean isBonemeal(ItemStack stack) {
        return stack.getItem() == Items.DYE && stack.getItemDamage() == 15;
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

    protected boolean canReplace(BlockPos pos){
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isReplaceable(world, pos);
    }

    protected boolean tryPlace(ItemStack stack, BlockPos pos, EnumFacing face){
        EnumFacing direction = face.getOpposite();
        if(stack.getItem() instanceof IAncientWarfarePlantable) {
            return ((IAncientWarfarePlantable) stack.getItem()).tryPlant(world, pos.offset(direction), stack.copy());
        }
        EntityPlayer owner = getOwnerAsPlayer(); //TODO shouldn't this really only ever return fake player?
        if(owner instanceof FakePlayer){
            owner.setHeldItem(EnumHand.MAIN_HAND, stack);
        }
        return stack.onItemUse(owner, world, pos.offset(direction), EnumHand.MAIN_HAND, face, 0.25F, 0.25F, 0.25F) == EnumActionResult.SUCCESS;
    }

    protected final void pickupItems() {
        List<EntityItem> items = getEntitiesWithinBounds(EntityItem.class);
        if(items.isEmpty())
            return;
        int[] indices = getIndicesForPickup();
        ItemStack stack;
        for (EntityItem item : items) {
            if(item.isEntityAlive()) {
                stack = item.getItem();
                if (!stack.isEmpty()) {
                    stack = InventoryTools.mergeItemStack(inventory, stack, indices);
                    if (!stack.isEmpty()) {
                        item.setItem(stack);
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
    protected void validateCollection(Collection<BlockPos> blocks) {
        if(!hasWorkBounds()){
            blocks.clear();
            return;
        }
        Iterator<BlockPos> it = blocks.iterator();
        BlockPos pos;
        while (it.hasNext() && (pos = it.next()) != null) {
            if (!isInBounds(pos) || !isTarget(pos)) {
                it.remove();
            }
        }
    }

    @Override
    protected void fillBlocksToProcess(Collection<BlockPos> targets) {
        BlockPos min = getWorkBoundsMin();
        BlockPos max = getWorkBoundsMax();
        for (int x = min.getX(); x < max.getX() + 1; x++) {
            for (int z = min.getZ(); z < max.getZ() + 1; z++) {
                if (isTarget(x, z)) {
                    targets.add(new BlockPos(x, min.getY(), z));
                }
            }
        }
    }

    //TODO implement to check target blocks, clear invalid ones
    public void onTargetsAdjusted() {
        onBoundsAdjusted();
    }

    @Override
    protected void onBoundsSet() {
        for (int x = 0; x < SIZE; x++) {
            for (int z = 0; z < SIZE; z++) {
                targetMap[z * SIZE + x] = (byte) 1;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByteArray("targetMap", targetMap);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("targetMap")) {
            targetMap = tag.getByteArray("targetMap");
        }
    }

    public byte[] getTargetMap() {
        return targetMap;
    }

    public void setTargetBlocks(byte[] targets) {
        boolean change = !Objects.deepEquals(targetMap, targets);
        targetMap = targets;
        if(change) {
            onTargetsAdjusted();
            markDirty();
        }
    }

    @Override
    protected void updateBlockWorksite() {
        world.profiler.startSection("Items Pickup");
        if (world.getWorldTime() % 20 == 0) {
            pickupItems();
        }
        world.profiler.endStartSection("Count Resources");
        if (shouldCountResources) {
            countResources();
            shouldCountResources = false;
        }
        world.profiler.endSection();
    }

    protected abstract void countResources();

    protected final class SlotListener extends BlockRotationHandler.InventorySided{

        public SlotListener(int inventorySize) {
            super(TileWorksiteUserBlocks.this, BlockRotationHandler.RotationType.FOUR_WAY, inventorySize);
        }

        @Override
        public ItemStack decrStackSize(int var1, int var2) {
            @Nonnull ItemStack result = super.decrStackSize(var1, var2);
            if(!result.isEmpty() && getFilterForSlot(var1) != null)
                shouldCountResources = true;
            return result;
        }

        @Override
        public ItemStack removeStackFromSlot(int var1) {
            @Nonnull ItemStack result = super.removeStackFromSlot(var1);
            if(!result.isEmpty() && getFilterForSlot(var1) != null)
                shouldCountResources = true;
            return result;
        }

        @Override
        public void setInventorySlotContents(int var1, ItemStack var2) {
            super.setInventorySlotContents(var1, var2);
            if(getFilterForSlot(var1) != null)
                shouldCountResources = true;
        }
    }
}
