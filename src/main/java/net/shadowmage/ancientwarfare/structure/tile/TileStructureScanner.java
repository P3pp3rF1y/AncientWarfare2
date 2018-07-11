package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItems;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.scan.TemplateScanner;

import javax.annotation.Nonnull;

public class TileStructureScanner extends TileUpdatable {
	private static final String SCANNER_INVENTORY_TAG = "scannerInventory";
	private static final String BOUNDS_ACTIVE_TAG = "boundsActive";
	private static final String FACING_TAG = "facing";
	private ItemStackHandler scannerInventory = new ItemStackHandler(1) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return stack.getItem() == AWStructuresItems.structureScanner ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);

			if (!world.isRemote) {
				BlockTools.notifyBlockUpdate(TileStructureScanner.this);
			}
		}
	};

	private boolean boundsActive = true;
	private EnumFacing facing = EnumFacing.NORTH;
	private EnumFacing renderFacing = EnumFacing.NORTH;

	public ItemStackHandler getScannerInventory() {
		return scannerInventory;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setTag(SCANNER_INVENTORY_TAG, scannerInventory.serializeNBT());
		tag.setBoolean(BOUNDS_ACTIVE_TAG, boundsActive);
		tag.setByte(FACING_TAG, (byte) facing.ordinal());
	}

	private void updateRenderFacing() {
		ItemStack scanner = getScannerInventory().getStackInSlot(0);
		EnumFacing newRenderFacing = scanner.getItem() == AWStructuresItems.structureScanner &&
				ItemStructureScanner.readyToExport(scanner)
				? EnumFacing.UP : facing;

		if (newRenderFacing != renderFacing) {
			renderFacing = newRenderFacing;
			BlockTools.notifyBlockUpdate(this);
		}
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		scannerInventory.deserializeNBT(tag.getCompoundTag(SCANNER_INVENTORY_TAG));
		boundsActive = tag.getBoolean(BOUNDS_ACTIVE_TAG);
		facing = EnumFacing.VALUES[tag.getByte(FACING_TAG)];
		updateRenderFacing();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = super.writeToNBT(compound);
		tag.setTag(SCANNER_INVENTORY_TAG, scannerInventory.serializeNBT());
		tag.setBoolean(BOUNDS_ACTIVE_TAG, boundsActive);
		tag.setByte(FACING_TAG, (byte) facing.ordinal());
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		scannerInventory.deserializeNBT(compound.getCompoundTag(SCANNER_INVENTORY_TAG));
		boundsActive = compound.getBoolean(BOUNDS_ACTIVE_TAG);
		facing = EnumFacing.VALUES[compound.getByte(FACING_TAG)];
	}

	public boolean getBoundsActive() {
		return boundsActive;
	}

	public void setBoundsActive(boolean boundsActive) {
		this.boundsActive = boundsActive;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		ItemStack scanner = scannerInventory.getStackInSlot(0);
		if (scanner.getItem() != AWStructuresItems.structureScanner) {
			return super.getRenderBoundingBox();
		}

		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(scanner);

		if (!settings.hasPos1() || !settings.hasPos2()) {
			return super.getRenderBoundingBox();
		}

		return settings.getBoundingBox().grow(1, 0 , 1);
	}

	public EnumFacing getRenderFacing() {
		return renderFacing;
	}

	public void restoreTemplate(String name) {
		ItemStack scanner = scannerInventory.getStackInSlot(0);

		if (scanner.getItem() != AWStructuresItems.structureScanner) {
			return;
		}

		StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(name);
		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(scanner);
		if (ItemStructureScanner.readyToExport(scanner)) {
			int turns = (6 - settings.face().getHorizontalIndex()) % 4;
			StructureTemplate dummyTemplate = TemplateScanner.scan(world, settings.getMin(), settings.getMax(), settings.buildKey(), turns, "dummy");
			if (isSameTemplateSizeAndOffset(template, dummyTemplate)) {
				ItemStructureScanner.setStructureName(scanner, name);
				ItemStructureScanner.setValidator(scanner, template.getValidationSettings());
				//TODO fix incorrect y buildkey offset in structure builder and remove offesting buildkey up 1 block here
				restoreTemplate(template, settings.getBoundingBox(), settings.buildKey().offset(EnumFacing.UP, 1), settings.face());
				return;
			}
		}

		saveToScannerItemAndRestoreTemplate(name, scanner, template, settings);
	}

	private void saveToScannerItemAndRestoreTemplate(String name, ItemStack scanner, StructureTemplate template, ItemStructureSettings settings) {
		EnumFacing placementFacing = facing.getOpposite();
		BlockPos key = pos.offset(placementFacing, template.zSize - template.zOffset).offset(EnumFacing.DOWN, 1);
		StructureBB bb = new StructureBB(key, placementFacing, template);
		settings.setBuildKey(key, placementFacing);
		settings.setName(name);
		settings.setPos1(bb.min);
		settings.setPos2(bb.max);
		ItemStructureScanner.setStructureName(scanner, name);
		ItemStructureScanner.setValidator(scanner, template.getValidationSettings());
		ItemStructureSettings.setSettingsFor(scanner, settings);

		//TODO fix incorrect y buildkey offset in structure builder remove this offset up 1
		restoreTemplate(template, settings.getBoundingBox(), key.offset(EnumFacing.UP, 1), placementFacing);
	}

	private void restoreTemplate(StructureTemplate template, AxisAlignedBB boundingBox, BlockPos buildPos, EnumFacing face) {
		clearBoundingBox(boundingBox);
		buildTemplate(template, buildPos, face);
		clearItemsOnGround(boundingBox);
	}

	private void clearItemsOnGround(AxisAlignedBB boundingBox) {
		world.getEntitiesWithinAABB(EntityItem.class, boundingBox).forEach(Entity::setDead);
	}

	private void buildTemplate(StructureTemplate template, BlockPos buildPos, EnumFacing face) {
		StructureBuilder builder = new StructureBuilder(world, template, face, buildPos);
		builder.instantConstruction();
	}

	private void clearBoundingBox(AxisAlignedBB boundingBox) {
		clearEntities(boundingBox);
		clearBlocks(boundingBox);
	}

	private void clearBlocks(AxisAlignedBB boundingBox) {
		BlockPos.getAllInBox((int) boundingBox.minX, (int) boundingBox.minY, (int) boundingBox.minZ,
				(int) boundingBox.maxX, (int) boundingBox.maxY, (int) boundingBox.maxZ).forEach(world::setBlockToAir);
	}

	private void clearEntities(AxisAlignedBB boundingBox) {
		AxisAlignedBB expandedBoundingBox = new AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX + 1, boundingBox.maxY + 1, boundingBox.maxZ + 1);
		world.getEntitiesWithinAABB(Entity.class, expandedBoundingBox).forEach(Entity::setDead);
	}

	private boolean isSameTemplateSizeAndOffset(StructureTemplate template, StructureTemplate dummyTemplate) {
		return dimensionsAreSame(template, dummyTemplate) && offsetIsSame(template, dummyTemplate);
	}

	private boolean offsetIsSame(StructureTemplate template, StructureTemplate dummyTemplate) {
		return template.xOffset == dummyTemplate.xOffset && template.yOffset == dummyTemplate.yOffset && template.zOffset == dummyTemplate.zOffset;
	}

	private boolean dimensionsAreSame(StructureTemplate template, StructureTemplate dummyTemplate) {
		return template.xSize == dummyTemplate.xSize && template.ySize == dummyTemplate.ySize && template.zSize == dummyTemplate.zSize;
	}
}
