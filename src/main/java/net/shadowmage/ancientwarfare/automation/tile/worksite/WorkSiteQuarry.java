package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.EnumSet;

public class WorkSiteQuarry extends TileWorksiteBoundedInventory {

    boolean finished;
    private boolean hasDoneInit = false;

    /**
     * Current position within work bounds.
     * Incremented when work is processed.
     */
    int currentX, currentY, currentZ;//position within bounds that is the 'active' position
    int validateX, validateY, validateZ;

    public WorkSiteQuarry() {
        this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 27);
        int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
        this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
    }

    @Override
    public boolean userAdjustableBlocks() {
        return false;
    }

    @Override
    protected void onBoundsSet() {
        super.onBoundsSet();
        bbMax.y = yCoord - 1;
        bbMin.y = 1;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void onBoundsAdjusted() {
        bbMax.y = yCoord - 1;
        bbMin.y = 1;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        currentX = bbMin.x;
        currentY = bbMax.y;
        currentZ = bbMin.z;

        validateX = currentX;
        validateY = currentY;
        validateZ = currentZ;
    }

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.of(
                WorksiteUpgrade.ENCHANTED_TOOLS_1,
                WorksiteUpgrade.ENCHANTED_TOOLS_2,
                WorksiteUpgrade.QUARRY_MEDIUM,
                WorksiteUpgrade.QUARRY_LARGE,
                WorksiteUpgrade.TOOL_QUALITY_1,
                WorksiteUpgrade.TOOL_QUALITY_2,
                WorksiteUpgrade.TOOL_QUALITY_3,
                WorksiteUpgrade.QUARRY_CHUNK_LOADER
        );
    }

    @Override
    public int getBoundsMaxWidth() {
        return getUpgrades().contains(WorksiteUpgrade.QUARRY_MEDIUM) ? 32 : getUpgrades().contains(WorksiteUpgrade.QUARRY_LARGE) ? 64 : 16;
    }

    @Override
    protected void updateWorksite() {
        if (!hasDoneInit) {
            initWorkSite();
            hasDoneInit = true;
        }
        incrementalScan();
    }

    private void incrementalScan() {
        worldObj.theProfiler.startSection("Incremental Scan");
        if (validatePosition(validateX, validateY, validateZ)) {
            currentX = validateX;
            currentY = validateY;
            currentZ = validateZ;
            finished = false;
        } else {
            incrementValidationPosition();
        }
        worldObj.theProfiler.endSection();
    }

    @Override
    public void addUpgrade(WorksiteUpgrade upgrade) {
        super.addUpgrade(upgrade);
        this.currentY = this.getWorkBoundsMax().y;
        this.currentX = this.getWorkBoundsMin().x;
        this.currentZ = this.getWorkBoundsMin().z;
        this.validateX = this.currentX;
        this.validateY = this.currentY;
        this.validateZ = this.currentZ;
        this.finished = false;
    }

    @Override
    protected boolean processWork() {
        if (!hasDoneInit) {
            initWorkSite();
            hasDoneInit = true;
        }
        if (finished) {
            return false;
        }
        /**
         * while the current position is invalid, increment to a valid one. generally the incremental scan
         * should have take care of this prior to processWork being called, but just in case...
         */
        while (!validatePosition(currentX, currentY, currentZ)) {
            if (!incrementPosition()) {
                /**
                 * if no valid position was found, set finished, exit
                 */
                finished = true;
                return false;
            }
        }
        /**
         * if made it this far, a valid position was found, break it and add blocks to inventory
         */
        return harvestBlock(currentX, currentY, currentZ, RelativeSide.TOP);
    }

    private boolean validatePosition(int x, int y, int z) {
        return !worldObj.isAirBlock(x, y, z) && canHarvest(worldObj.getBlock(x, y, z));
    }

    private boolean incrementPosition() {
        if (finished) {
            return false;
        }
        currentX++;
        if (currentX > getWorkBoundsMax().x) {
            currentX = getWorkBoundsMin().x;
            currentZ++;
            if (currentZ > getWorkBoundsMax().z) {
                currentZ = getWorkBoundsMin().z;
                currentY--;
                if (currentY <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void incrementValidationPosition() {
        validateX++;
        if (validateY >= currentY && validateZ >= currentZ && validateX >= currentX) {//dont let validation pass current position
            validateY = getWorkBoundsMax().y;
            validateX = getWorkBoundsMin().x;
            validateZ = getWorkBoundsMin().z;
        } else if (validateX > getWorkBoundsMax().x) {
            validateX = getWorkBoundsMin().x;
            validateZ++;
            if (validateZ > getWorkBoundsMax().z) {
                validateZ = getWorkBoundsMin().z;
                validateY--;
                if (validateY <= 0) {
                    validateY = getWorkBoundsMax().y;
                    validateX = getWorkBoundsMin().x;
                    validateZ = getWorkBoundsMin().z;
                }
            }
        }
    }

    private boolean canHarvest(Block block) {
        //TODO add block-breaking exclusion list to config
        int harvestLevel = block.getHarvestLevel(worldObj.getBlockMetadata(currentX, currentY, currentZ));
        if (harvestLevel >= 2) {
            int toolLevel = 1;
            if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_3)) {
                toolLevel = Integer.MAX_VALUE;
            } else if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_2)) {
                toolLevel = 3;
            } else if (getUpgrades().contains(WorksiteUpgrade.TOOL_QUALITY_1)) {
                toolLevel = 2;
            }
            if (toolLevel < harvestLevel) {
                return false;
            }//else is harvestable, check the rest of the checks
        }
        return !block.getMaterial().isLiquid() && block.getBlockHardness(worldObj, currentX, currentY, currentZ) >= 0;
    }

    public void initWorkSite() {
        this.getWorkBoundsMin().y = 1;
        this.currentY = this.getWorkBoundsMax().y;
        this.currentX = this.getWorkBoundsMin().x;
        this.currentZ = this.getWorkBoundsMin().z;
        this.validateX = this.currentX;
        this.validateY = this.currentY;
        this.validateZ = this.currentZ;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);//resend work-bounds change
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.MINING;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        currentY = tag.getInteger("currentY");
        currentX = tag.getInteger("currentX");
        currentZ = tag.getInteger("currentZ");
        validateX = tag.getInteger("validateX");
        validateY = tag.getInteger("validateY");
        validateZ = tag.getInteger("validateZ");
        finished = tag.getBoolean("finished");
        hasDoneInit = tag.getBoolean("init");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("currentY", currentY);
        tag.setInteger("currentX", currentX);
        tag.setInteger("currentZ", currentZ);
        tag.setInteger("validateX", validateX);
        tag.setInteger("validateY", validateY);
        tag.setInteger("validateZ", validateZ);
        tag.setBoolean("finished", finished);
        tag.setBoolean("init", hasDoneInit);
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_QUARRY, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    protected boolean hasWorksiteWork() {
        return !finished;
    }


}
