package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class TemplateRuleEntityLogic extends TemplateRuleVanillaEntity {
	private static final String INVENTORY_DATA_TAG = "inventoryData";
	private static final String EQUIPMENT_DATA_TAG = "equipmentData";
	public static final String PLUGIN_NAME = "vanillaLogicEntity";
	private NBTTagCompound tag;

	private NonNullList<ItemStack> inventory;
	private NonNullList<ItemStack> equipment;

	public TemplateRuleEntityLogic(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	public TemplateRuleEntityLogic(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		initEquipment();
		tag = new NBTTagCompound();
		entity.writeToNBT(tag);
		if (entity instanceof EntityLiving)//handles villagers / potentially other living npcs with inventories
		{
			tag.removeTag("Equipment");
			EntityLiving living = (EntityLiving) entity;
			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				equipment.set(slot.ordinal(), living.getItemStackFromSlot(slot).isEmpty() ? ItemStack.EMPTY : living.getItemStackFromSlot(slot).copy());
			}
		}
		if (entity instanceof IInventory)//handles minecart-chests
		{
			tag.removeTag("Items");
			IInventory eInv = (IInventory) entity;
			this.inventory = NonNullList.withSize(eInv.getSizeInventory(), ItemStack.EMPTY);
			for (int i = 0; i < eInv.getSizeInventory(); i++) {
				this.inventory.set(i, eInv.getStackInSlot(i).isEmpty() ? ItemStack.EMPTY : eInv.getStackInSlot(i).copy());
			}
		}
		tag.removeTag("UUIDMost");
		tag.removeTag("UUIDLeast");
	}

	private void initEquipment() {
		equipment = NonNullList.withSize(EntityEquipmentSlot.values().length, ItemStack.EMPTY);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		createEntity(world, turns, pos, builder).ifPresent(world::spawnEntity);
	}

	protected Optional<Entity> createEntity(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Entity e = EntityList.createEntityByIDFromName(registryName, world);
		if (e == null) {
			AncientWarfareStructure.LOG.warn("Could not create entity for name: " + registryName.toString() + " Entity skipped during structure creation.\n" + "Entity data: " + tag);
			return Optional.empty();
		}
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagDouble(pos.getX() + BlockTools.rotateFloatX(xOffset, zOffset, turns)));
		list.appendTag(new NBTTagDouble(pos.getY()));
		list.appendTag(new NBTTagDouble(pos.getZ() + BlockTools.rotateFloatZ(xOffset, zOffset, turns)));
		tag.setTag("Pos", list);
		e.readFromNBT(tag);
		if (equipment != null && e instanceof EntityLiving) {
			EntityLiving living = (EntityLiving) e;
			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				living.setItemStackToSlot(slot, equipment.get(slot.ordinal()).isEmpty() ? ItemStack.EMPTY : equipment.get(slot.ordinal()).copy());
			}
		}
		if (inventory != null && e instanceof IInventory) {
			IInventory eInv = (IInventory) e;
			for (int i = 0; i < inventory.size() && i < eInv.getSizeInventory(); i++) {
				eInv.setInventorySlotContents(i, inventory.get(i).isEmpty() ? ItemStack.EMPTY : inventory.get(i).copy());
			}
		}
		e.rotationYaw = (rotation + 90.f * turns) % 360.f;
		return Optional.of(e);
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setTag("entityData", this.tag);

		if (inventory != null) {
			NBTTagCompound invData = new NBTTagCompound();
			invData.setInteger("length", inventory.size());
			NBTTagCompound itemTag;
			NBTTagList list = new NBTTagList();
			@Nonnull ItemStack stack;
			for (int i = 0; i < inventory.size(); i++) {
				stack = inventory.get(i);
				if (stack.isEmpty()) {
					continue;
				}
				itemTag = stack.writeToNBT(new NBTTagCompound());
				itemTag.setInteger("slot", i);
				list.appendTag(itemTag);
			}
			invData.setTag("inventoryContents", list);
			tag.setTag(INVENTORY_DATA_TAG, invData);
		}
		if (equipment != null) {
			NBTTagCompound invData = new NBTTagCompound();
			NBTTagCompound itemTag;
			NBTTagList list = new NBTTagList();
			@Nonnull ItemStack stack;
			for (int i = 0; i < equipment.size(); i++) {
				stack = equipment.get(i);
				if (stack.isEmpty()) {
					continue;
				}
				itemTag = stack.writeToNBT(new NBTTagCompound());
				itemTag.setInteger("slot", i);
				list.appendTag(itemTag);
			}
			invData.setTag("equipmentContents", list);
			tag.setTag(EQUIPMENT_DATA_TAG, invData);
		}
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		this.tag = tag.getCompoundTag("entityData");
		if (tag.hasKey(INVENTORY_DATA_TAG)) {
			NBTTagCompound inventoryTag = tag.getCompoundTag(INVENTORY_DATA_TAG);
			int length = inventoryTag.getInteger("length");
			inventory = NonNullList.withSize(length, ItemStack.EMPTY);
			NBTTagCompound itemTag;
			NBTTagList list = tag.getTagList("inventoryContents", Constants.NBT.TAG_COMPOUND);
			int slot;
			@Nonnull ItemStack stack;
			for (int i = 0; i < list.tagCount(); i++) {
				itemTag = list.getCompoundTagAt(i);
				stack = new ItemStack(itemTag);
				if (!stack.isEmpty()) {
					slot = itemTag.getInteger("slot");
					inventory.set(slot, stack);
				}
			}
		}

		initEquipment(); //here instead of with field because call to super is done before field can be initialized

		if (tag.hasKey(EQUIPMENT_DATA_TAG)) {
			NBTTagCompound inventoryTag = tag.getCompoundTag(EQUIPMENT_DATA_TAG);
			NBTTagCompound itemTag;
			NBTTagList list = inventoryTag.getTagList("equipmentContents", Constants.NBT.TAG_COMPOUND);
			int slot;
			@Nonnull ItemStack stack;
			for (int i = 0; i < list.tagCount(); i++) {
				itemTag = list.getCompoundTagAt(i);
				stack = new ItemStack(itemTag);
				if (!stack.isEmpty()) {
					slot = itemTag.getInteger("slot");
					equipment.set(slot, stack);
				}
			}
		}
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
