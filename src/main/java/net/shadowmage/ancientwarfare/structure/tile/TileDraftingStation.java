package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.inventory.IInvBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;

public class TileDraftingStation extends TileEntity implements IInvBasic {

    private String structureName;//structure pulled from live structure list anytime a ref is needed
    private boolean isStarted;//has started compiling resources -- will need input to cancel
    private ArrayList<ItemStack> neededResources = new ArrayList<ItemStack>();
    private boolean isFinished;//is finished compiling resources, awaiting output-slot availability
    private int remainingTime;//not really time, but raw item count
    private int totalTime;//total raw-item count

    public InventoryBasic inputSlots = new InventoryBasic(27, this);
    public InventoryBasic outputSlot = new InventoryBasic(1, this);

    public TileDraftingStation() {

    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }
        if (structureName != null) {
            StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(structureName);
            if (t == null) {
                stopCurrentWork();
            }
        }
        if (structureName == null || !isStarted) {
            return;
        }
        if (!isFinished) {
            if (tryRemoveResource()) {
                isFinished = true;
            }
        }
        if (isFinished)//check finished again, as it might have changed during previous operation
        {
            if (tryFinish())//try to output item to output slot
            {
                stopCurrentWork();//clear structure, times, flags
            }
        }
    }

    private boolean tryRemoveResource() {
        ItemStack stack1, stack2;
        outerLoopLabel:
        for (int k = 0; k < inputSlots.getSizeInventory(); k++) {
            stack2 = inputSlots.getStackInSlot(k);
            if (stack2 == null) {
                continue;
            }
            for (int i = 0; i < neededResources.size(); i++) {
                stack1 = neededResources.get(i);
                if (InventoryTools.doItemStacksMatch(stack1, stack2)) {
                    stack1.stackSize--;
                    stack2.stackSize--;
                    if (stack1.stackSize <= 0) {
                        neededResources.remove(i);
                    }
                    if (stack2.stackSize <= 0) {
                        inputSlots.setInventorySlotContents(k, null);
                    }
                    break outerLoopLabel;
                }
            }
        }
        return neededResources.isEmpty();
    }

    public void tryStart() {
        if (structureName != null && StructureTemplateManager.INSTANCE.getTemplate(structureName) != null) {
            this.isStarted = true;
        }
    }

    private boolean tryFinish() {
        if (outputSlot.getStackInSlot(0) == null) {
            ItemStack item = new ItemStack(AWBlocks.builderBlock);
            item.setTagInfo("structureName", new NBTTagString(structureName));
            outputSlot.setInventorySlotContents(0, item);
            return true;
        }
        return false;
    }

    public String getCurrentTemplateName() {
        return structureName;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public List<ItemStack> getNeededResources() {
        return neededResources;
    }

    public void stopCurrentWork() {
        this.structureName = null;
        this.neededResources.clear();
        this.remainingTime = 0;
        this.isFinished = false;
        this.isStarted = false;
    }

    /**
     * should be called client-side to TRY to set the
     * current template to the input name.
     * Will not change templates if production has already
     * started on the current template
     */
    public void setTemplate(String templateName) {
        if (isStarted) {
            return;
        }
        this.structureName = null;
        this.neededResources.clear();
        this.remainingTime = 0;
        StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(templateName);
        if (t != null) {
            if(t.getValidationSettings().isSurvival())
                this.structureName = templateName;
            for (ItemStack item : t.getResourceList()) {
                this.neededResources.add(item.copy());
            }
            calcTime();
        }
    }

    private void calcTime() {
        int count = 0;
        for (ItemStack item : this.neededResources) {
            count += item.stackSize;
        }
        this.totalTime = this.remainingTime = count;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inputSlots.readFromNBT(tag.getCompoundTag("inputInventory"));
        outputSlot.readFromNBT(tag.getCompoundTag("outputInventory"));
        if (tag.hasKey("structureName")) {
            structureName = tag.getString("structureName");
        } else {
            structureName = null;
        }
        isStarted = tag.getBoolean("isStarted");
        isFinished = tag.getBoolean("isFinished");
        remainingTime = tag.getInteger("remainingTime");
        totalTime = tag.getInteger("totalTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inputInventory", inputSlots.writeToNBT(new NBTTagCompound()));

        tag.setTag("outputInventory", outputSlot.writeToNBT(new NBTTagCompound()));

        if (structureName != null) {
            tag.setString("structureName", structureName);
        }
        tag.setBoolean("isStarted", isStarted);
        tag.setBoolean("isFinished", isFinished);
        tag.setInteger("remainingTime", remainingTime);
        tag.setInteger("totalTime", totalTime);

        /**
         * TODO write out resource-list
         */
    }

    @Override
    public void onInventoryChanged(net.minecraft.inventory.InventoryBasic internal){
        markDirty();
    }

}
