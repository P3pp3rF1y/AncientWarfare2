package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.TreeFinder;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WorkSiteTreeFarm extends TileWorksiteUserBlocks {

    private static final TreeFinder TREE = new TreeFinder(17), LEAF = new TreeFinder(4);
    private boolean hasShears;
    private int saplingCount;
    private int bonemealCount;
    private final Set<BlockPos> blocksToShear;
    private final Set<BlockPos> blocksToChop;
    private final Set<BlockPos> blocksToPlant;
    private final Set<BlockPos> blocksToFertilize;

    public WorkSiteTreeFarm() {

        blocksToChop = new HashSet<>();
        blocksToPlant = new HashSet<>();
        blocksToFertilize = new HashSet<>();
        blocksToShear = new HashSet<>();

        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(TOP_LENGTH);
        int[] frontIndices = helper.getIndiceArrayForSpread(FRONT_LENGTH);
        int[] bottomIndices = helper.getIndiceArrayForSpread(BOTTOM_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//saplings
        this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal and shears
        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean apply(@Nullable ItemStack stack) {
                return stack == null || isSapling(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);
        filter = new ItemSlotFilter() {
            @Override
            public boolean apply(@Nullable ItemStack stack) {
                return stack == null || isBonemeal(stack) || stack.getItem() instanceof ItemShears;
            }
        };
        this.inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isSapling(ItemStack stack) {
        return isFarmable(Block.getBlockFromItem(stack.getItem()));
    }

    @Override
    protected boolean isFarmable(Block block, BlockPos farmablePos){
        if(super.isFarmable(block, farmablePos)){
            if(block instanceof BlockSapling){
                return true;
            }
            if(!(block instanceof IShearable) && !(block instanceof BlockFlower)){
                return ((IPlantable) block).getPlantType(world, pos) == EnumPlantType.Plains;
            }
        }
        return false;
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(blocksToFertilize);
        validateCollection(blocksToChop);
        validateCollection(blocksToPlant);
        if(!hasShears){
            blocksToShear.clear();
        }
        markDirty();
    }

    @Override
    protected void countResources() {
        hasShears = false;
        saplingCount = 0;
        bonemealCount = 0;
        @Nonnull ItemStack stack;
        for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (i < TOP_LENGTH + FRONT_LENGTH){
                if(isSapling(stack))
                    saplingCount += stack.getCount();
            } else if (isBonemeal(stack)) {
                bonemealCount += stack.getCount();
            } else if(stack.getItem() instanceof ItemShears){
                hasShears = true;
            }
        }
    }

    @Override
    protected boolean processWork() {
        BlockPos position;
        if(hasShears && !blocksToShear.isEmpty()){
            Iterator<BlockPos> it = blocksToShear.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                Block block = world.getBlockState(position).getBlock();
                if (block instanceof IShearable) {
                    @Nonnull ItemStack stack;
                    for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                        stack = getStackInSlot(i);
                        if(!stack.isEmpty() && stack.getItem() instanceof ItemShears){
                            if(((IShearable) block).isShearable(stack, world, position)){
                                List<ItemStack> drops = ((IShearable) block).onSheared(stack, world, position, getFortune());
                                int[] combinedIndices = inventory.getRawIndicesCombined(RelativeSide.TOP, RelativeSide.FRONT);
                                for(ItemStack drop : drops){
                                    if(drop!=null) {
                                        drop = InventoryTools.mergeItemStack(inventory, drop, combinedIndices);
                                        InventoryTools.dropItemInWorld(world, drop, position);
                                    }
                                }
                                world.setBlockToAir(position);
/* TODO enviromine integration
                                ModAccessors.ENVIROMINE.schedulePhysUpdate(world, position, true, "Normal");
*/
                                return true;
                            }
                        }
                    }
                }
            }
        } else if (!blocksToChop.isEmpty()) {
            Iterator<BlockPos> it = blocksToChop.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                if(harvestBlock(position, RelativeSide.TOP)){
                    addLeavesAround(position);
                    return true;
                }
            }
        } else if (saplingCount > 0 && !blocksToPlant.isEmpty()) {
            @Nonnull ItemStack stack = ItemStack.EMPTY;
            for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
                stack = getStackInSlot(i);
                if (!stack.isEmpty() && isSapling(stack)) {
                    break;
                } else {
                    stack = ItemStack.EMPTY;
                }
            }
            if (!stack.isEmpty())//e.g. a sapling stack is present
            {
                Iterator<BlockPos> it = blocksToPlant.iterator();
                while (it.hasNext() && (position = it.next()) != null) {
                    it.remove();
                    if (isUnwantedPlant(world.getBlockState(position).getBlock())) {
                        world.setBlockToAir(position);
/* TODO enviromine integration
                        ModAccessors.ENVIROMINE.schedulePhysUpdate(world, position, true, "Normal");
*/
                    }
                    if (canReplace(position) && tryPlace(stack, position, EnumFacing.UP)) {
                        saplingCount--;
                        return true;
                    }
                }
            }
        } else if (bonemealCount > 0 && !blocksToFertilize.isEmpty()) {
            Iterator<BlockPos> it = blocksToFertilize.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                Block block = world.getBlockState(position).getBlock();
                if (isFarmable(block, position)) {
                    @Nonnull ItemStack stack;
                    for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                        stack = getStackInSlot(i);
                        if (!stack.isEmpty() && isBonemeal(stack)) {
                            if(ItemDye.applyBonemeal(stack, world, position, getOwnerAsPlayer(), EnumHand.MAIN_HAND)){
                                bonemealCount--;
                                if (stack.getCount() <= 0) {
                                    setInventorySlotContents(i, ItemStack.EMPTY);
                                }
                            }
                            IBlockState state = world.getBlockState(position);
                            block = state.getBlock();
                            if (isFarmable(block, position)) {
                                blocksToFertilize.add(position);//possible concurrent access exception?
                                //technically, it would be, except by the time it hits this inner block, it is already
                                //done iterating, as it will immediately hit the following break statement, and break
                                //out of the iterating loop before the next element would have been iterated over
                            } else if (state.getMaterial() == Material.WOOD) {
                                addTreeBlocks(block, position);
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

    private void addTreeBlocks(Block block, BlockPos base) {
        world.profiler.startSection("TreeFinder");
        int chops = blocksToChop.size();
        TREE.findAttachedTreeBlocks(block, world, base, blocksToChop);
        if(blocksToChop.size() != chops){
            addLeavesAround(base);
            markDirty();
        }
        world.profiler.endSection();
    }

    private void addLeavesAround(BlockPos base){
        if(hasShears) {
            addLeaves(base, -1, (byte) 0);
            addLeaves(base, +1, (byte) 1);
            addLeaves(base, -1, (byte) 2);
        }
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FORESTRY;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_TREE_FARM, pos);
        }
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (!blocksToChop.isEmpty()) {
            NBTTagList chopList = new NBTTagList();
            for (BlockPos position : blocksToChop) {
                chopList.appendTag(new NBTTagLong(position.toLong()));
            }
            tag.setTag("targetList", chopList);
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        blocksToChop.clear();
        if (tag.hasKey("targetList")) {
            NBTTagList chopList = tag.getTagList("targetList", Constants.NBT.TAG_LONG);
            for (int i = 0; i < chopList.tagCount(); i++) {
                blocksToChop.add(BlockPos.fromLong(((NBTTagLong) chopList.get(i)).getLong()));
            }
        }
    }

    @Override
    protected void scanBlockPosition(BlockPos scanPos) {
        Block block;
        if (canReplace(scanPos)) {
            block = world.getBlockState(scanPos.down()).getBlock();
            if (block == Blocks.DIRT || block == Blocks.GRASS) {
                blocksToPlant.add(scanPos);
            }
        } else {
            IBlockState state = world.getBlockState(scanPos);
            block = state.getBlock();
            if (isFarmable(block, scanPos)) {
                blocksToFertilize.add(scanPos);
            } else if (state.getMaterial() == Material.WOOD) {
                addTreeBlocks(block, scanPos);
            } else if (isUnwantedPlant(block)) {
                blocksToPlant.add(scanPos);
            }
        }
    }
    
    private boolean isUnwantedPlant(Block block) {
        return block instanceof BlockBush && !(block instanceof BlockSapling);
    }

    private void addLeaves(BlockPos position, int offset, byte xOrYOrZ){
        BlockPos pos = position;
        if(xOrYOrZ == 0){
            pos = pos.offset(EnumFacing.EAST, offset);
        }else if(xOrYOrZ == 1){
            pos = pos.offset(EnumFacing.UP, offset);
        }else if(xOrYOrZ == 2){
            pos = pos.offset(EnumFacing.SOUTH, offset);
        }
        Block block = world.getBlockState(pos).getBlock();
        if(block instanceof IShearable){
            LEAF.findAttachedTreeBlocks(block, world, pos, blocksToShear);
        }
        if(offset<0){
            addLeaves(position, -offset, xOrYOrZ);
        }
    }

    @Override
    protected boolean hasWorksiteWork() {
        return (hasShears && !blocksToShear.isEmpty()) || !blocksToChop.isEmpty() || (bonemealCount > 0 && !blocksToFertilize.isEmpty()) || (saplingCount > 0 && !blocksToPlant.isEmpty());
    }
}
