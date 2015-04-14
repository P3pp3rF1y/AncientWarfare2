package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.ArrayList;
import java.util.List;

public class POTrade {

    private List<ItemStack> compactInput = new ArrayList<ItemStack>();
    private List<ItemStack> compactOutput = new ArrayList<ItemStack>();
    private ItemStack[] input = new ItemStack[9];
    private ItemStack[] output = new ItemStack[9];

    public POTrade() {

    }

    public ItemStack getInputStack(int index) {
        return input[index];
    }

    public ItemStack getOutputStack(int index) {
        return output[index];
    }

    public void setInputStack(int index, ItemStack stack) {
        input[index] = stack;
        updateCompactInput();
    }

    public void setOutputStack(int index, ItemStack stack) {
        output[index] = stack;
        updateCompactOutput();
    }

    private void updateCompactInput() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack temp : input) {
            if (temp != null) {
                list.add(temp.copy());
            }
        }
        compactInput.clear();
        InventoryTools.compactStackList3(list, compactInput);
    }

    private void updateCompactOutput() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack temp : output) {
            if (temp != null) {
                list.add(temp.copy());
            }
        }
        compactOutput.clear();
        InventoryTools.compactStackList3(list, compactOutput);
    }

    /**
     * Check through the input inventory and ensure it contains all materials necessary to complete this trade.<br>
     */
    public boolean isAvailable(IInventory storage) {
        for (ItemStack stack : compactOutput) {
            if (InventoryTools.getCountOf(storage, -1, stack) < stack.stackSize) {
                return false;
            }
        }
        return InventoryTools.canInventoryHold(storage, -1, compactInput);
    }

    /**
     * If items are all present in trade grid will remove necessary items from trade grid and add to storage
     * and remove result from storage and merge into player inventory/drop on ground<br>
     * MUST have called trade.isAvailable() prior to any calls to performTrade
     */
    public void perfromTrade(EntityPlayer player, IInventory tradeGrid, IInventory storage) {
        boolean found = true;
        ItemStack inputStack, invStack;
        for (int i = 0; i < input.length; i++) {
            inputStack = input[i];
            if (inputStack == null) {
                continue;
            }
            invStack = tradeGrid.getStackInSlot(i);
            if (invStack == null || !InventoryTools.doItemStacksMatch(inputStack, invStack) || invStack.stackSize < inputStack.stackSize) {
                found = false;
                break;
            }
        }
        if (found) {
            for (int i = 0; i < input.length; i++) {
                inputStack = input[i];
                if (inputStack == null) {
                    continue;
                }
                InventoryTools.mergeItemStack(storage, tradeGrid.decrStackSize(i, inputStack.stackSize), -1);//remove from trade grid and merge into storage
            }
            for (ItemStack outputStack : output) {
                if (outputStack == null) {
                    continue;
                }
                outputStack = InventoryTools.removeItems(storage, -1, outputStack, outputStack.stackSize);//remove from storage
                outputStack = InventoryTools.mergeItemStack(player.inventory, outputStack, -1);//merge into player inventory, drop any unused portion on next line
                if (outputStack != null && !player.worldObj.isRemote) {
                    InventoryTools.dropItemInWorld(player.worldObj, outputStack, player.posX, player.posY, player.posZ);
                }//only drop into world if on server!
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        NBTTagCompound itemTag;

        for (int i = 0; i < input.length; i++) {
            if (input[i] == null) {
                continue;
            }
            itemTag = InventoryTools.writeItemStack(input[i]);
            itemTag.setInteger("slot", i);
            list.appendTag(itemTag);
        }
        tag.setTag("inputItems", list);

        list = new NBTTagList();
        for (int i = 0; i < output.length; i++) {
            if (output[i] == null) {
                continue;
            }
            itemTag = InventoryTools.writeItemStack(output[i]);
            itemTag.setInteger("slot", i);
            list.appendTag(itemTag);
        }
        tag.setTag("outputItems", list);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
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
        updateCompactInput();
        updateCompactOutput();
    }

}
