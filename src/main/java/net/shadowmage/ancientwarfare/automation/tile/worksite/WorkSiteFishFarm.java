package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;

public class WorkSiteFishFarm extends TileWorksiteBoundedInventory {
    private static final int MAX_WATER = 1280;
    private boolean harvestFish = true;
    private boolean harvestInk = true;

    private int waterBlockCount = 0;
    private int waterRescanDelay = 0;

    public WorkSiteFishFarm() {
        super();
    }

    @Override
    public boolean userAdjustableBlocks() {
        return false;
    }

    @Override
    protected void onBoundsSet() {
        super.onBoundsSet();
        BlockPos boundsMax = getWorkBoundsMax();
        setWorkBoundsMax(boundsMax.up(pos.getY() - 1 - boundsMax.getY()));
        boundsMax = getWorkBoundsMin();
        setWorkBoundsMin(boundsMax.up(pos.getY() - 5 - boundsMax.getY()));
        BlockTools.notifyBlockUpdate(this);
    }

    @Override
    public void onBoundsAdjusted() {
        super.onBoundsAdjusted();
        BlockPos boundsMax = getWorkBoundsMax();
        setWorkBoundsMax(boundsMax.up(pos.getY() - 1 - boundsMax.getY()));
        boundsMax = getWorkBoundsMin();
        setWorkBoundsMin(boundsMax.up(pos.getY() - 5 - boundsMax.getY()));
        BlockTools.notifyBlockUpdate(this);
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
            float check = world.rand.nextFloat();
            if (check <= percentOfMax) {
                boolean fish = harvestFish, ink = harvestInk;
                if (fish && ink) {
                    fish = world.rand.nextBoolean();
                    ink = !fish;
                }
                if (fish) {
                    LootContext.Builder context = new LootContext.Builder((WorldServer)world);
                    context.withLuck(getFortune());
                    for (@Nonnull ItemStack fishStack : this.world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(world.rand, context.build())) {
                        InventoryTools.insertOrDropItem(mainInventory, fishStack, world, pos);
                    }
                }
                if (ink) {
                    @Nonnull ItemStack inkItem = new ItemStack(Items.DYE, 1, 0);
                    int fortune = getFortune();
                    if (fortune > 0) {
                        inkItem.grow(world.rand.nextInt(fortune + 1));
                    }
                    InventoryTools.insertOrDropItem(mainInventory, inkItem, world, pos);
                    return true;
                }
            }
        }
        return false;
    }

    private void countWater() {
        waterBlockCount = 0;
        BlockPos min = getWorkBoundsMin();
        BlockPos max = getWorkBoundsMax();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int y = max.getY(); y >= min.getY(); y--) {
                    IBlockState state = world.getBlockState(new BlockPos(x, y, z));
                    if (state.getMaterial() == Material.WATER) {
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("fish", harvestFish);
        tag.setBoolean("ink", harvestInk);
        return tag;
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.FARMING;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_FARM, pos);
        }
        return true;
    }

    @Override
    public void openAltGui(EntityPlayer player) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_CONTROL, pos);
    }

    @Override
    protected boolean hasWorksiteWork() {
        return waterBlockCount > 0;
    }

    @Override
    protected void updateWorksite() {
        world.profiler.startSection("WaterCount");
        if (waterRescanDelay-- <= 0) {
            countWater();
            waterRescanDelay = 200;
        }
        world.profiler.endSection();
    }

}
