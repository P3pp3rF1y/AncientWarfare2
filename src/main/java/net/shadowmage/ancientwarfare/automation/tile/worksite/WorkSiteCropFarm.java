package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.api.IAncientWarfareFarmable;
import net.shadowmage.ancientwarfare.api.IAncientWarfarePlantable;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WorkSiteCropFarm extends TileWorksiteUserBlocks {

    private final Set<BlockPos> blocksToTill;
    private final Set<BlockPos> blocksToHarvest;
    private final Set<BlockPos> blocksToPlant;
    private final Set<BlockPos> blocksToFertilize;

    private int plantableCount;
    private int bonemealCount;
    
    private final static List<Block[]> blocksTillableAndTilled = new ArrayList<>();

    public WorkSiteCropFarm() {

        blocksToTill = new HashSet<>();
        blocksToHarvest = new HashSet<>();
        blocksToPlant = new HashSet<>();
        blocksToFertilize = new HashSet<>();
        
        if (blocksTillableAndTilled.size() == 0) {
            AncientWarfareCore.log.info("Building crop farmable block list...");
            for (String entry : AWAutomationStatics.crop_farm_blocks) {
                String[] farmablePair = entry.split("\\|");
                if (farmablePair.length != 2) {
                    AncientWarfareCore.log.error("Invalid entry: " + entry);
                    continue;
                }
                Block[] farmablePairBlocks = new Block[2];
                for (int i = 0; i < 2; i++) {
                    if (!farmablePair[i].trim().equals("")) {
                        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(farmablePair[i]));
                        if (block != null) {
                            farmablePairBlocks[i] = block;
                        } else {
                            // just dummy the block entry so we can silently ignore this missing block
                            farmablePairBlocks[i] = Blocks.AIR;
                        }
                    }
                }
                if (farmablePairBlocks[0] == null || farmablePairBlocks[1] == null) {
                    AncientWarfareCore.log.error("Invalid entry: " + entry);
                    continue;
                } else if (farmablePairBlocks[0] != Blocks.AIR && farmablePairBlocks[1] != Blocks.AIR) {
                    blocksTillableAndTilled.add(farmablePairBlocks);
                    AncientWarfareCore.log.info("...added " + farmablePair[0] + " > " + farmablePair[1] + " as " + farmablePairBlocks[0].getLocalizedName() + " > " + farmablePairBlocks[1].getLocalizedName());
                }
            }
        }

        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(TOP_LENGTH);
        int[] frontIndices = helper.getIndiceArrayForSpread(FRONT_LENGTH);
        int[] bottomIndices = helper.getIndiceArrayForSpread(BOTTOM_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//plantables
        this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean test(ItemStack stack) {
                return stack.isEmpty() || isPlantable(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, frontIndices);

        filter = new ItemSlotFilter() {
            @Override
            public boolean test(ItemStack stack) {
                return stack.isEmpty() || isBonemeal(stack);
            }
        };
        this.inventory.setFilterForSlots(filter, bottomIndices);
    }

    private boolean isPlantable(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof IAncientWarfarePlantable) {
            return ((IAncientWarfarePlantable) item).isPlantable(stack);
        }
        return item instanceof IPlantable;
    }

    @Override
    protected boolean isFarmable(Block block, BlockPos farmablePos) {
        if(block instanceof IAncientWarfareFarmable && ((IAncientWarfareFarmable)block).isMature(world, farmablePos)) {
            return true;
        }
        if(super.isFarmable(block, farmablePos)){
            return ((IPlantable) block).getPlantType(world, farmablePos) == EnumPlantType.Crop;
        }
        return block instanceof BlockCrops || block instanceof BlockStem;
    }

    private boolean isTillable(Block block){
        for (Block farmableBlockPair[] : blocksTillableAndTilled) {
            if (block == farmableBlockPair[0])
                return true;
        }
        return false;
    }

    @Override
    public void onBoundsAdjusted() {
        validateCollection(blocksToFertilize);
        validateCollection(blocksToHarvest);
        validateCollection(blocksToPlant);
        validateCollection(blocksToTill);
    }

    @Override
    protected void countResources() {
        plantableCount = 0;
        bonemealCount = 0;
        @Nonnull ItemStack stack;
        for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (i < TOP_LENGTH + FRONT_LENGTH){
                if(isPlantable(stack))
                    plantableCount += stack.getCount();
            }else if(isBonemeal(stack)){
                bonemealCount += stack.getCount();
            }
        }
    }

    @Override
    protected int[] getIndicesForPickup(){
        return inventory.getRawIndicesCombined(RelativeSide.BOTTOM, RelativeSide.FRONT, RelativeSide.TOP);
    }

    @Override
    protected void scanBlockPosition(BlockPos position) {
        IBlockState state = world.getBlockState(position);
        Block block = world.getBlockState(position).getBlock();
        if (block.isReplaceable(world, position)) {
            block = world.getBlockState(position.down()).getBlock();
            if (isTillable(block)) {
                blocksToTill.add(position.down());
            } else {
                for (Block farmableBlockPair[] : blocksTillableAndTilled) {
                    if (block == farmableBlockPair[1])
                        blocksToPlant.add(position);
                }
            }
        } else if (block instanceof BlockStem) {
            if (!((IGrowable) block).canGrow(world, position, state, world.isRemote)) {
                state = world.getBlockState(position.west());
                if (melonOrPumpkin(state)) {
                    blocksToHarvest.add(position.west());
                }
                state = world.getBlockState(position.east());
                if (melonOrPumpkin(state)) {
                    blocksToHarvest.add(position.east());
                }
                state = world.getBlockState(position.north());
                if (melonOrPumpkin(state)) {
                    blocksToHarvest.add(position.north());
                }
                state = world.getBlockState(position.south());
                if (melonOrPumpkin(state)) {
                    blocksToHarvest.add(position.south());
                }
            } else {
                blocksToFertilize.add(position);
            }
        } else if (block instanceof IGrowable && ((IGrowable) block).canGrow(world, position, state, world.isRemote)) {
            blocksToFertilize.add(position);
        } else if (isFarmable(block, position)) {
            blocksToHarvest.add(position);
        }
    }

    private boolean melonOrPumpkin(IBlockState state){
        return state.getMaterial() == Material.GOURD;
    }

    @Override
    protected boolean processWork() {
        
        Iterator<BlockPos> it;
        BlockPos position;
        Block block;
        if (!blocksToTill.isEmpty()) {
            it = blocksToTill.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = world.getBlockState(position).getBlock();
                if (isTillable(block) && canReplace(position.up())) {
                    //for (Block farmableBlockPair[] : blocksTillableAndTilled) {
                    for (int i = 0; i < blocksTillableAndTilled.size(); i++) {
                        if (block == blocksTillableAndTilled.get(i)[0]) {
                            world.setBlockState(position, blocksTillableAndTilled.get(i)[1].getDefaultState());
                        }
                    }
                    return true;
                }
            }
        } else if (!blocksToHarvest.isEmpty()) {
            it = blocksToHarvest.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                IBlockState state = world.getBlockState(position);
                block = state.getBlock();
                if (melonOrPumpkin(state)) {
                    return harvestBlock(position, RelativeSide.FRONT, RelativeSide.TOP);
                }
                else if (block instanceof IGrowable) {
                    if (!((IGrowable) block).canGrow(world, position, state, world.isRemote) && !(block instanceof BlockStem)) {
                        if(Loader.isModLoaded("AgriCraft")){
                            if(!(block instanceof IAncientWarfareFarmable)) {//Not using the API
                                Class<? extends Block> c = block.getClass();
                                if ("com.InfinityRaider.AgriCraft.blocks.BlockCrop".equals(c.getName())) {//A crop from AgriCraft
                                    try {//Use the harvest method, hopefully dropping stuff
                                        c.getDeclaredMethod("harvest", World.class, int.class, int.class, int.class, EntityPlayer.class).invoke(block, world, position, null);
                                        return true;
                                    } catch (Throwable ignored) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return harvestBlock(position, RelativeSide.FRONT, RelativeSide.TOP);
                    }
                }else if(isFarmable(block, position)){
                    return harvestBlock(position, RelativeSide.FRONT, RelativeSide.TOP);
                }
            }
        } else if (hasToPlant()) {
            it = blocksToPlant.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                if (canReplace(position)) {
                    @Nonnull ItemStack stack;
                    for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
                        stack = getStackInSlot(i);
                        if (stack.isEmpty()) {
                            continue;
                        }
                        if (isPlantable(stack)) {
                            if(tryPlace(stack, position, EnumFacing.UP)) {
                                plantableCount--;
                                if (stack.getCount() <= 0) {
                                    setInventorySlotContents(i, ItemStack.EMPTY);
                                }
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        } else if (hasToFertilize()) {
            it = blocksToFertilize.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                IBlockState state = world.getBlockState(position);
                block = state.getBlock();
                if (block instanceof IGrowable) {
                    @Nonnull ItemStack stack;
                    for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                        stack = getStackInSlot(i);
                        if (stack.isEmpty()) {
                            continue;
                        }
                        if (isBonemeal(stack)) {
                            if(ItemDye.applyBonemeal(stack, world, position, getOwnerAsPlayer(), EnumHand.MAIN_HAND)){
                                bonemealCount--;
                                if (stack.getCount() <= 0) {
                                    setInventorySlotContents(i, ItemStack.EMPTY);
                                }
                            }
                            block = world.getBlockState(position).getBlock();
                            if(block instanceof IAncientWarfareFarmable) {
                                IAncientWarfareFarmable farmable = (IAncientWarfareFarmable) block;
                                if(farmable.isMature(world, position)) {
                                    blocksToHarvest.add(position);
                                } else if(farmable.canGrow(world, position, state, world.isRemote)) {
                                    blocksToFertilize.add(position);
                                }
                            }
                            else if (block instanceof IGrowable) {
                                if (((IGrowable) block).canGrow(world, position, state, world.isRemote)) {
                                    blocksToFertilize.add(position);
                                } else if (isFarmable(block, position)) {
                                    blocksToHarvest.add(position);
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
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_CROP_FARM, pos);
        }
        return true;
    }

    @Override
    protected boolean hasWorksiteWork() {
        return hasToPlant() || hasToFertilize() || !blocksToTill.isEmpty() || !blocksToHarvest.isEmpty();
    }

    private boolean hasToPlant(){
        return (plantableCount > 0 && !blocksToPlant.isEmpty());
    }

    private boolean hasToFertilize(){
        return (bonemealCount > 0 && !blocksToFertilize.isEmpty());
    }
}
