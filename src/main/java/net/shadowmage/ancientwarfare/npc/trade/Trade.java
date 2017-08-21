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

/**
 * Created by Olivier on 12/05/2015.
 */
public abstract class Trade {
    protected ItemStack[] input = new ItemStack[size()];
    protected ItemStack[] output = new ItemStack[size()];

    public int size(){
        return 9;
    }

    public ItemStack getInputStack(int index) {
        return input[index];
    }

    public ItemStack getOutputStack(int index) {
        return output[index];
    }

    public void setInputStack(int index, ItemStack stack) {
        input[index] = stack;
    }

    public void setOutputStack(int index, ItemStack stack) {
        output[index] = stack;
    }

    /**
     * If items are all present in trade grid, delegate to #doTrade
     */
    public void performTrade(EntityPlayer player, IInventory storage) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack temp : input) {
            if (temp != null) {
                list.add(temp.copy());
            }
        }
        list = InventoryTools.compactStackList3(list);
        for (ItemStack stack : list) {
            if (InventoryTools.getCountOf(player.inventory, -1, stack) < stack.getCount()) {
                return;
            }
        }
        doTrade(player, storage);
    }

    /**
     * will remove necessary items from player inventory and add to storage
     * and remove result from storage and merge into player inventory/drop on ground<br>
     */
    protected void doTrade(EntityPlayer player, IInventory storage) {
        for (ItemStack inputStack : input) {
            if (inputStack == null) {
                continue;
            }
            ItemStack result = InventoryTools.removeItems(player.inventory, -1, inputStack, inputStack.getCount());//remove from trade grid
            if(result!=null && storage!=null)
                InventoryTools.mergeItemStack(storage, result, -1);//merge into storage
        }
        for (ItemStack outputStack : output) {
            if (outputStack == null) {
                continue;
            }
            if(storage!=null)
                outputStack = InventoryTools.removeItems(storage, -1, outputStack, outputStack.getCount());//remove from storage
            else
                outputStack = outputStack.copy();
            outputStack = InventoryTools.mergeItemStack(player.inventory, outputStack, -1);//merge into player inventory, drop any unused portion on next line
            if (outputStack != null && !player.world.isRemote) {//only drop into world if on server!
                InventoryTools.dropItemInWorld(player.world, outputStack, player.posX, player.posY, player.posZ);
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
            itemTag = input[i].writeToNBT(new NBTTagCompound());
            itemTag.setInteger("slot", i);
            list.appendTag(itemTag);
        }
        tag.setTag("inputItems", list);

        list = new NBTTagList();
        for (int i = 0; i < output.length; i++) {
            if (output[i] == null) {
                continue;
            }
            itemTag = output[i].writeToNBT(new NBTTagCompound());
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
            input[itemTag.getInteger("slot")] = new ItemStack(itemTag);
        }

        NBTTagList outputList = tag.getTagList("outputItems", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < outputList.tagCount(); i++) {
            itemTag = outputList.getCompoundTagAt(i);
            output[itemTag.getInteger("slot")] = new ItemStack(itemTag);
        }
    }
}
