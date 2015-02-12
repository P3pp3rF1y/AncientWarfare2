package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class FactionTrade {

    private ItemStack[] input;
    private ItemStack[] output;
    private int refillFrequency;
    private int ticksTilRefill;
    private int maxAvailable;
    private int currentAvailable;
//TODO add minLevel stat -- used to determine if trade should be available

    public FactionTrade() {
        input = new ItemStack[9];
        output = new ItemStack[9];
        refillFrequency = 20 * 60 * 5;//five minutes per item refilled
        ticksTilRefill = refillFrequency;
        maxAvailable = 1;
        currentAvailable = 1;
    }

    public ItemStack[] getInput() {
        return input;
    }

    public ItemStack[] getOutput() {
        return output;
    }

    public int getRefillFrequency() {
        return refillFrequency;
    }

    public int getMaxAvailable() {
        return maxAvailable;
    }

    public int getCurrentAvailable() {
        return currentAvailable;
    }

    public void setRefillFrequency(int refill) {
        refillFrequency = refill;
        ticksTilRefill = refillFrequency;
    }

    public void setMaxAvailable(int max) {
        maxAvailable = max;
        currentAvailable = max;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("refillFrequency", refillFrequency);
        tag.setInteger("ticksTilRefill", ticksTilRefill);
        tag.setInteger("maxAvailable", maxAvailable);
        tag.setInteger("currentAvailable", currentAvailable);

        NBTTagList list = new NBTTagList();
        NBTTagCompound itemTag;

        for (int i = 0; i < input.length; i++) {
            if (input[i] == null) {
                continue;
            }
            itemTag = new NBTTagCompound();
            InventoryTools.writeItemStack(input[i], itemTag);
            itemTag.setInteger("slot", i);
            list.appendTag(itemTag);
        }
        tag.setTag("inputItems", list);

        list = new NBTTagList();
        for (int i = 0; i < output.length; i++) {
            if (output[i] == null) {
                continue;
            }
            itemTag = new NBTTagCompound();
            InventoryTools.writeItemStack(output[i], itemTag);
            itemTag.setInteger("slot", i);
            list.appendTag(itemTag);
        }
        tag.setTag("outputItems", list);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        refillFrequency = tag.getInteger("refillFrequency");
        ticksTilRefill = tag.getInteger("ticksTilRefill");
        maxAvailable = tag.getInteger("maxAvailable");
        currentAvailable = tag.getInteger("currentAvailable");

        NBTTagCompound itemTag;

        NBTTagList inputList = tag.getTagList("inputItems", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < inputList.tagCount(); i++) {
            itemTag = inputList.getCompoundTagAt(i);
            input[itemTag.getInteger("slot")] = InventoryTools.readItemStack(itemTag);
        }

        NBTTagList outputList = tag.getTagList("outputItems", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < outputList.tagCount(); i++) {
            itemTag = outputList.getCompoundTagAt(i);
            output[itemTag.getInteger("slot")] = InventoryTools.readItemStack(itemTag);
        }
    }

    public void updateTrade(int ticks) {
        ticksTilRefill += ticks;
        if (refillFrequency > 0)//update per freq period
        {
            while (ticksTilRefill >= refillFrequency) {
                ticksTilRefill -= refillFrequency;
                if (currentAvailable < maxAvailable) {
                    currentAvailable++;
                }
            }
        } else if (refillFrequency == 0)//full refill automatically if frequency==0
        {
            currentAvailable = maxAvailable;
        } else {
        }//dont refill if frequency<0
    }

    public void performTrade(EntityPlayer player, IInventory inputInventory) {
        if (currentAvailable > 0) {
            boolean found = true;
            ItemStack inputStack, invStack;
            for (int i = 0; i < input.length; i++) {
                inputStack = input[i];
                if (inputStack == null) {
                    continue;
                }
                invStack = inputInventory.getStackInSlot(i);
                if (invStack == null || !InventoryTools.doItemStacksMatch(inputStack, invStack) || invStack.stackSize < inputStack.stackSize) {
                    found = false;
                    break;
                }
            }
            if (found) {
                if (refillFrequency != 0) {
                    currentAvailable--;
                }//0 denotes instant restock, no reason to decrease qty if it will just be instantly restocked when GUI is opened next
                for (int i = 0; i < input.length; i++) {
                    inputStack = input[i];
                    if (inputStack == null) {
                        continue;
                    }
                    inputInventory.decrStackSize(i, inputStack.stackSize);
                }
                ItemStack outputStack;
                for (int i = 0; i < output.length; i++) {
                    outputStack = output[i];
                    if (outputStack == null) {
                        continue;
                    }
                    outputStack = InventoryTools.mergeItemStack(player.inventory, outputStack.copy(), -1);
                    if (outputStack != null && !player.worldObj.isRemote) {
                        InventoryTools.dropItemInWorld(player.worldObj, outputStack, player.posX, player.posY, player.posZ);
                    }
                }
            }
        }
    }

}
