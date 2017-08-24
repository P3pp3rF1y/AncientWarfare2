package net.shadowmage.ancientwarfare.automation.tile.worksite;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.shadowmage.ancientwarfare.api.IAncientWarfareFarmable;
import net.shadowmage.ancientwarfare.api.IAncientWarfarePlantable;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.*;

public class WorkSiteCropFarm extends TileWorksiteUserBlocks {

    private final Set<BlockPos> blocksToTill;
    private final Set<BlockPos> blocksToHarvest;
    private final Set<BlockPos> blocksToPlant;
    private final Set<BlockPos> blocksToFertilize;

    private int plantableCount;
    private int bonemealCount;
    
    private final static List<Block[]> blocksTillableAndTilled = new ArrayList<Block[]>();

    public WorkSiteCropFarm() {

        blocksToTill = new HashSet<BlockPos>();
        blocksToHarvest = new HashSet<BlockPos>();
        blocksToPlant = new HashSet<BlockPos>();
        blocksToFertilize = new HashSet<BlockPos>();
        
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
                        String[] blockId = farmablePair[i].split(":");
                        if (blockId[0] != null && blockId[1] != null) {
                            Block block = GameRegistry.findBlock(blockId[0], blockId[1]);
                            if (block != null) {
                                farmablePairBlocks[i] = block;
                            } else {
                                // just dummy the block entry so we can silently ignore this missing block
                                farmablePairBlocks[i] = Blocks.air;
                            }
                        }
                    }
                }
                if (farmablePairBlocks[0] == null || farmablePairBlocks[1] == null) {
                    AncientWarfareCore.log.error("Invalid entry: " + entry);
                    continue;
                } else if (farmablePairBlocks[0] != Blocks.air && farmablePairBlocks[1] != Blocks.air) {
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
            public boolean apply(ItemStack stack) {
                return stack == null || isPlantable(stack);
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

    private boolean isPlantable(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof IAncientWarfarePlantable) {
            return ((IAncientWarfarePlantable) item).isPlantable(stack);
        }
        return item instanceof IPlantable;
    }

    @Override
    protected boolean isFarmable(Block block, int x, int y, int z) {
        if(block instanceof IAncientWarfareFarmable && ((IAncientWarfareFarmable)block).isMature(world, x, y, z)) {
            return true;
        }
        if(super.isFarmable(block, x, y, z)){
            return ((IPlantable) block).getPlantType(world, x, y, z) == EnumPlantType.Crop;
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
        ItemStack stack;
        for (int i = TOP_LENGTH; i < getSizeInventory(); i++) {
            stack = getStackInSlot(i);
            if (stack == null) {
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
        Block block = world.getBlock(position.x, position.y, position.z);
        if (block.isReplaceable(world, position.x, position.y, position.z)) {
            block = world.getBlock(position.x, position.y - 1, position.z);
            if (isTillable(block)) {
                blocksToTill.add(new BlockPos(position.x, position.y - 1, position.z));
            } else {
                for (Block farmableBlockPair[] : blocksTillableAndTilled) {
                    if (block == farmableBlockPair[1])
                        blocksToPlant.add(position);
                }
            }
        } else if (block instanceof BlockStem) {
            if (!((IGrowable) block).func_149851_a(world, position.x, position.y, position.z, world.isRemote)) {
                block = world.getBlock(position.x - 1, position.y, position.z);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPos(position.x - 1, position.y, position.z));
                }
                block = world.getBlock(position.x + 1, position.y, position.z);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPos(position.x + 1, position.y, position.z));
                }
                block = world.getBlock(position.x, position.y, position.z - 1);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPos(position.x, position.y, position.z - 1));
                }
                block = world.getBlock(position.x, position.y, position.z + 1);
                if (melonOrPumpkin(block)) {
                    blocksToHarvest.add(new BlockPos(position.x, position.y, position.z + 1));
                }
            } else {
                blocksToFertilize.add(position);
            }
        } else if (block instanceof IGrowable && ((IGrowable) block).func_149851_a(world, position.x, position.y, position.z, world.isRemote)) {
            blocksToFertilize.add(position);
        } else if (isFarmable(block, position.x, position.y, position.z)) {
            blocksToHarvest.add(position);
        }
    }

    private boolean melonOrPumpkin(Block block){
        return block.getMaterial() == Material.gourd;
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
                block = world.getBlock(position.x, position.y, position.z);
                if (isTillable(block) && canReplace(position.x, position.y + 1, position.z)) {
                    //for (Block farmableBlockPair[] : blocksTillableAndTilled) {
                    for (int i = 0; i < blocksTillableAndTilled.size(); i++) {
                        if (block == blocksTillableAndTilled.get(i)[0]) {
                            world.setBlock(position.x, position.y, position.z, blocksTillableAndTilled.get(i)[1]);
                        }
                    }
                    return true;
                }
            }
        } else if (!blocksToHarvest.isEmpty()) {
            it = blocksToHarvest.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                block = world.getBlock(position.x, position.y, position.z);
                if (melonOrPumpkin(block)) {
                    return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                }
                else if (block instanceof IGrowable) {
                    if (!((IGrowable) block).func_149851_a(world, position.x, position.y, position.z, world.isRemote) && !(block instanceof BlockStem)) {
                        if(Loader.isModLoaded("AgriCraft")){
                            if(!(block instanceof IAncientWarfareFarmable)) {//Not using the API
                                Class<? extends Block> c = block.getClass();
                                if ("com.InfinityRaider.AgriCraft.blocks.BlockCrop".equals(c.getName())) {//A crop from AgriCraft
                                    try {//Use the harvest method, hopefully dropping stuff
                                        c.getDeclaredMethod("harvest", World.class, int.class, int.class, int.class, EntityPlayer.class).invoke(block, world, position.x, position.y, position.z, null);
                                        return true;
                                    } catch (Throwable ignored) {
                                        return false;
                                    }
                                }
                            }
                        }
                        return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                    }
                }else if(isFarmable(block, position.x, position.y, position.z)){
                    return harvestBlock(position.x, position.y, position.z, RelativeSide.FRONT, RelativeSide.TOP);
                }
            }
        } else if (hasToPlant()) {
            it = blocksToPlant.iterator();
            while (it.hasNext() && (position = it.next()) != null) {
                it.remove();
                if (canReplace(position.x, position.y, position.z)) {
                    ItemStack stack;
                    for (int i = TOP_LENGTH; i < TOP_LENGTH + FRONT_LENGTH; i++) {
                        stack = getStackInSlot(i);
                        if (stack == null) {
                            continue;
                        }
                        if (isPlantable(stack)) {
                            if(tryPlace(stack, position.x, position.y, position.z, EnumFacing.UP)) {
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
                block = world.getBlock(position.x, position.y, position.z);
                if (block instanceof IGrowable) {
                    ItemStack stack;
                    for (int i = TOP_LENGTH + FRONT_LENGTH; i < getSizeInventory(); i++) {
                        stack = getStackInSlot(i);
                        if (stack == null) {
                            continue;
                        }
                        if (isBonemeal(stack)) {
                            if(ItemDye.applyBonemeal(stack, world, position.x, position.y, position.z, getOwnerAsPlayer())){
                                bonemealCount--;
                                if (stack.getCount() <= 0) {
                                    setInventorySlotContents(i, ItemStack.EMPTY);
                                }
                            }
                            block = world.getBlock(position.x, position.y, position.z);
                            if(block instanceof IAncientWarfareFarmable) {
                                IAncientWarfareFarmable farmable = (IAncientWarfareFarmable) block;
                                if(farmable.isMature(world, position.x, position.y, position.z)) {
                                    blocksToHarvest.add(position);
                                } else if(farmable.func_149851_a(world, position.x, position.y, position.z, world.isRemote)) {
                                    blocksToFertilize.add(position);
                                }
                            }
                            else if (block instanceof IGrowable) {
                                if (((IGrowable) block).func_149851_a(world, position.x, position.y, position.z, world.isRemote)) {
                                    blocksToFertilize.add(position);
                                } else if (isFarmable(block, position.x, position.y, position.z)) {
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
