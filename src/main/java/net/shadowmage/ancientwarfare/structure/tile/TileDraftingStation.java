package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import javax.annotation.Nonnull;
import java.util.List;

public class TileDraftingStation extends TileEntity implements ITickable, IBlockBreakHandler {

	private String structureName;//structure pulled from live structure list anytime a ref is needed
	private boolean isStarted;//has started compiling resources -- will need input to cancel
	private List<ItemStack> neededResources = NonNullList.create();
	private List<ItemStack> returnResources = NonNullList.create();
	private boolean isFinished;//is finished compiling resources, awaiting output-slot availability
	private int remainingTime;//not really time, but raw item count
	private int totalTime;//total raw-item count

	public ItemStackHandler inputSlots = new ItemStackHandler(27) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	public ItemStackHandler outputSlot = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	public TileDraftingStation() {

	}

	@Override
	public void update() {
		if (!hasWorld() || world.isRemote) {
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
		@Nonnull ItemStack stack1, stack2;
		outerLoopLabel:
		for (int k = 0; k < inputSlots.getSlots(); k++) {
			stack2 = inputSlots.getStackInSlot(k);
			if (stack2.isEmpty()) {
				continue;
			}
			for (int i = 0; i < neededResources.size(); i++) {
				stack1 = neededResources.get(i);
				if (InventoryTools.doItemStacksMatchRelaxed(stack1, stack2)) {
					stack1.shrink(1);
					stack2.shrink(1);
					if (stack1.getCount() <= 0) {
						neededResources.remove(i);
					}
					if (stack2.getCount() <= 0) {
						inputSlots.setStackInSlot(k, ItemStack.EMPTY);
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
		if (outputSlot.getStackInSlot(0).isEmpty()) {
			@Nonnull ItemStack item = new ItemStack(AWStructureBlocks.STRUCTURE_BUILDER_TICKED);
			item.setTagInfo("structureName", new NBTTagString(structureName));
			outputSlot.setStackInSlot(0, item);
			InventoryTools.insertOrDropItems(inputSlots, returnResources, world, pos);
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
		markDirty();
	}

	/*
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
		returnResources.clear();
		this.remainingTime = 0;
		StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(templateName);
		if (t != null) {
			if (t.getValidationSettings().isSurvival())
				this.structureName = templateName;
			for (ItemStack item : t.getResourceList()) {
				this.neededResources.add(item.copy());
			}
			for (ItemStack item : t.getRemainingStacks()) {
				returnResources.add(item.copy());
			}
			calcTime();
		}
		markDirty();
	}

	private void calcTime() {
		int count = 0;
		for (ItemStack item : this.neededResources) {
			count += item.getCount();
		}
		this.totalTime = this.remainingTime = count;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inputSlots.deserializeNBT(tag.getCompoundTag("inputInventory"));
		outputSlot.deserializeNBT(tag.getCompoundTag("outputInventory"));
		if (tag.hasKey("structureName")) {
			structureName = tag.getString("structureName");
		} else {
			structureName = null;
		}
		isStarted = tag.getBoolean("isStarted");
		isFinished = tag.getBoolean("isFinished");
		remainingTime = tag.getInteger("remainingTime");
		totalTime = tag.getInteger("totalTime");
		neededResources = NBTHelper.deserializeItemStackList(tag.getTagList("neededResources", Constants.NBT.TAG_COMPOUND));
		returnResources = NBTHelper.deserializeItemStackList(tag.getTagList("returnResources", Constants.NBT.TAG_COMPOUND));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inputInventory", inputSlots.serializeNBT());

		tag.setTag("outputInventory", outputSlot.serializeNBT());

		if (structureName != null) {
			tag.setString("structureName", structureName);
		}
		tag.setBoolean("isStarted", isStarted);
		tag.setBoolean("isFinished", isFinished);
		tag.setInteger("remainingTime", remainingTime);
		tag.setInteger("totalTime", totalTime);
		tag.setTag("neededResources", NBTHelper.serializeItemStackList(neededResources));
		tag.setTag("returnResources", NBTHelper.serializeItemStackList(returnResources));

		return tag;
	}

	@Override
	public void onBlockBroken() {
		InventoryTools.dropItemsInWorld(world, inputSlots, pos);
		InventoryTools.dropItemsInWorld(world, outputSlot, pos);
	}
}
