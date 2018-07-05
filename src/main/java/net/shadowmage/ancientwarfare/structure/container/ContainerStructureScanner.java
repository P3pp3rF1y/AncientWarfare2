package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItems;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;

import java.util.Optional;
import java.util.function.Consumer;

public class ContainerStructureScanner extends ContainerBase {
	private static final String INCLUDE_TAG = "include";
	private static final String VALIDATOR_TAG = "validator";
	private static final String BOUNDS_ACTIVE_TAG = "boundsActive";
	private ItemStack scanner;
	private final EnumHand hand;
	private final TileStructureScanner scannerTile;

	public Optional<TileStructureScanner> getScannerTile() {
		return Optional.ofNullable(scannerTile);
	}

	public ContainerStructureScanner(EntityPlayer player, int x, int y, int z) {
		super(player);
		if (y > 0) {
			scannerTile = (TileStructureScanner) player.world.getTileEntity(new BlockPos(x, y, z));
			//noinspection ConstantConditions
			scanner = scannerTile.getScannerInventory().getStackInSlot(0);
			Slot slot = new SlotItemHandler(scannerTile.getScannerInventory(), 0, 8, 8) {
				@Override
				public void onSlotChanged() {
					super.onSlotChanged();

					Optional<TileStructureScanner> te = getScannerTile();
					scanner = te.map(tileStructureScanner -> tileStructureScanner.getScannerInventory().getStackInSlot(0)).orElse(ItemStack.EMPTY);
					if (player.world.isRemote) {
						listeners.forEach(l -> l.sendSlotContents(ContainerStructureScanner.this, 0, scanner));
					}
				}
			};

			addSlotToContainer(slot);
			addPlayerSlots();
			hand = null;
		} else {
			scannerTile = null;
			scanner = EntityTools.getItemFromEitherHand(player, ItemStructureScanner.class);
			hand = EntityTools.getHandHoldingItem(player, AWStructuresItems.structureScanner);
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (!tag.hasKey("mode")) {
			return;
		}
		String mode = tag.getString("mode");
		if (mode.equals("export") && ItemStructureScanner.scanStructure(player.world, scanner) && !getScannerTile().isPresent()) {
			ItemStructureScanner.clearSettings(scanner);
			saveScannerData(player);
		} else if (mode.equals("update")) {
			if (tag.hasKey("name")) {
				updateName(tag.getString("name"));
			} else if (tag.hasKey(INCLUDE_TAG)) {
				setIncludeImmediately(tag.getBoolean(INCLUDE_TAG));
			} else if (tag.hasKey(VALIDATOR_TAG)) {
				NBTTagCompound validatorNBT = tag.getCompoundTag(VALIDATOR_TAG);
				StructureValidationType type = StructureValidationType.getTypeFromName(validatorNBT.getString("validationType"));
				//noinspection ConstantConditions
				StructureValidator validator = type.getValidator();
				validator.readFromNBT(validatorNBT);
				setValidator(validator);
			} else if (tag.hasKey(BOUNDS_ACTIVE_TAG)) {
				setBoundsActive(tag.getBoolean(BOUNDS_ACTIVE_TAG));
			}
		}
	}

	public boolean hasScanner() {
		return !scanner.isEmpty();
	}

	private void sendUpdateData(String name, NBTBase data) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("mode", "update");
		tag.setTag(name, data);
		sendDataToServer(tag);
	}

	private void saveScannerData(EntityPlayer player) {
		if (!getScannerTile().isPresent()) {
			player.setHeldItem(hand, scanner);
		}
	}

	public void export() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("mode", "export");
		sendDataToServer(tag);
	}

	public void updateName(String name) {
		if (player.world.isRemote) {
			sendUpdateData("name", new NBTTagString(name));
			return;
		}
		//noinspection ConstantConditions
		ItemStructureScanner.setStructureName(scanner, name);
		saveScannerData(player);
	}

	public String getName() {
		return ItemStructureScanner.getStructureName(scanner);
	}

	public String getValidationTypeName() {
		return ItemStructureScanner.getValidator(scanner).validationType.getName();
	}

	public void setIncludeImmediately(boolean checked) {
		if (player.world.isRemote) {
			sendUpdateData(INCLUDE_TAG, new NBTTagByte((byte) (checked ? 1 : 0)));
			return;
		}
		ItemStructureScanner.setIncludeImmediately(scanner, checked);
		saveScannerData(player);
	}

	public boolean getIncludeImmediately() {
		return ItemStructureScanner.getIncludeImmediately(scanner);
	}

	public StructureValidator getValidator() {
		return ItemStructureScanner.getValidator(scanner);
	}

	public void setValidator(StructureValidator validator) {
		if (player.world.isRemote) {
			sendUpdateData(VALIDATOR_TAG, validator.serializeToNBT());
		}
		ItemStructureScanner.setValidator(scanner, validator);
		saveScannerData(player);
	}

	public void updateValidator(Consumer<StructureValidator> doUpdate) {
		StructureValidator validator = getValidator();
		doUpdate.accept(validator);
		setValidator(validator);
	}

	public void toggleBounds() {
		setBoundsActive(!getBoundsActive());
	}

	private void setBoundsActive(boolean boundsActive) {
		if (player.world.isRemote) {
			sendUpdateData(BOUNDS_ACTIVE_TAG, new NBTTagByte((byte) (boundsActive ? 1 : 0)));
		}
		getScannerTile().ifPresent(t -> t.setBoundsActive(boundsActive));
	}

	public boolean getBoundsActive() {
		return getScannerTile().map(TileStructureScanner::getBoundsActive).orElse(false);
	}

	public boolean getReadyToExport() {
		return ItemStructureScanner.readyToExport(scanner);
	}
}
