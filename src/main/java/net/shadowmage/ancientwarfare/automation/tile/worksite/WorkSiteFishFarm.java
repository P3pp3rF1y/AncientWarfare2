package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.FishingHooks;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteFishFarm extends TileWorksiteBoundedInventory {

    private static final int TOP_LENGTH = 27;
    private static final int MAX_WATER = 1280;
    private boolean harvestFish = true;
    private boolean harvestInk = true;

    private int waterBlockCount = 0;
    private int waterRescanDelay = 0;

    public WorkSiteFishFarm() {
        this.inventory = new InventorySided(this, RotationType.FOUR_WAY, TOP_LENGTH);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, InventoryTools.getIndiceArrayForSpread(TOP_LENGTH));
    }

    @Override
    public boolean userAdjustableBlocks() {
        return false;
    }

    @Override
    protected void onBoundsSet() {
        super.onBoundsSet();
        BlockPosition pos = getWorkBoundsMax();
        setWorkBoundsMax(pos.moveUp(yCoord - 1 - pos.y));
        pos = getWorkBoundsMin();
        setWorkBoundsMin(pos.moveUp(yCoord - 5 - pos.y));
        this.worldObj.notifyBlockUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void onBoundsAdjusted() {
        super.onBoundsAdjusted();
        BlockPosition pos = getWorkBoundsMax();
        setWorkBoundsMax(pos.moveUp(yCoord - 1 - pos.y));
        pos = getWorkBoundsMin();
        setWorkBoundsMin(pos.moveUp(yCoord - 5 - pos.y));
        this.worldObj.notifyBlockUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public int getBoundsMaxHeight() {
        return 4;
    }

    public boolean harvestFish(){
        return harvestFish;
    }

    public boolean harvestInk(){
        return harvestInk;
    }

    public void setHarvest(boolean fish, boolean ink){
        if(harvestFish != fish || harvestInk != ink){
            harvestFish = fish;
            harvestInk = ink;
            markDirty();
        }
    }

    @Override
    protected boolean processWork() {
        if (waterBlockCount > 0) {
            float percentOfMax = ((float) waterBlockCount) / MAX_WATER;
            float check = worldObj.rand.nextFloat();
            if (check <= percentOfMax) {
                boolean fish = harvestFish, ink = harvestInk;
                if (fish && ink) {
                    fish = worldObj.rand.nextBoolean();
                    ink = !fish;
                }
                if (fish) {
                    ItemStack fishStack = FishingHooks.getRandomFishable(worldObj.rand, 1F);
                    if (fishStack != null) {
                        int fortune = getFortune();
                        if (fortune > 0) {
                            fishStack.stackSize += worldObj.rand.nextInt(fortune + 1);
                        }
                        addStackToInventory(fishStack, RelativeSide.TOP);
                        return true;
                    }
                }
                if (ink) {
                    ItemStack inkItem = new ItemStack(Items.dye, 1, 0);
                    int fortune = getFortune();
                    if (fortune > 0) {
                        inkItem.stackSize += worldObj.rand.nextInt(fortune + 1);
                    }
                    addStackToInventory(inkItem, RelativeSide.TOP);
                    return true;
                }
            }
        }
        return false;
    }

    private void countWater() {
        waterBlockCount = 0;
        BlockPosition min = getWorkBoundsMin();
        BlockPosition max = getWorkBoundsMax();
        for (int x = min.x; x <= max.x; x++) {
            for (int z = min.z; z <= max.z; z++) {
                for (int y = max.y; y >= min.y; y--) {
                    if (worldObj.getBlock(x, y, z).getMaterial() == Material.water) {
                        waterBlockCount++;
                    } else {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        harvestFish = tag.getBoolean("fish");
        harvestInk = tag.getBoolean("ink");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("fish", harvestFish);
        tag.setBoolean("ink", harvestInk);
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FARMING;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_FARM, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public void openAltGui(EntityPlayer player) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_CONTROL, xCoord, yCoord, zCoord);
    }

    @Override
    protected boolean hasWorksiteWork() {
        return waterBlockCount > 0;
    }

    @Override
    protected void updateWorksite() {
        worldObj.theProfiler.startSection("WaterCount");
        if (waterRescanDelay-- <= 0) {
            countWater();
            waterRescanDelay = 200;
        }
        worldObj.theProfiler.endSection();
    }

}
