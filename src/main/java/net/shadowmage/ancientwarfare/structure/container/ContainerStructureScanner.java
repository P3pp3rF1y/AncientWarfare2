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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.init.AWStructureItems;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;

import java.util.Optional;
import java.util.Set;
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
			scannerTile = WorldTools.getTile(player.world, new BlockPos(x, y, z), TileStructureScanner.class).orElse(null);
			//noinspection ConstantConditions
			scanner = scannerTile.getScannerInventory().getStackInSlot(0).copy();
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
			hand = EntityTools.getHandHoldingItem(player, AWStructureItems.STRUCTURE_SCANNER);
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
		} else if (mode.equals("restore")) {
			getScannerTile().ifPresent(t -> t.restoreTemplate(tag.getString("templateName")));
		} else if (mode.equals("update")) {
			if (tag.hasKey("name")) {
				updateName(tag.getString("name"));
			} else if (tag.hasKey(INCLUDE_TAG)) {
				setIncludeImmediately(tag.getBoolean(INCLUDE_TAG));
			} else if (tag.hasKey(VALIDATOR_TAG)) {
				NBTTagCompound validatorNBT = tag.getCompoundTag(VALIDATOR_TAG);
				StructureValidationType.getTypeFromName(validatorNBT.getString("validationType")).ifPresent(type -> {
					StructureValidator validator = type.getValidator();
					validator.readFromNBT(validatorNBT);
					setValidator(validator);
				});
			} else if (tag.hasKey(BOUNDS_ACTIVE_TAG)) {
				setBoundsActive(tag.getBoolean(BOUNDS_ACTIVE_TAG));
			} else if (tag.hasKey("mods")) {
				updateModDependencies(NBTHelper.getStringSet(tag.getTagList("mods", Constants.NBT.TAG_STRING)));
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
			return;
		}
		getScannerTile().ifPresent(tile -> {
			tile.getScannerInventory().setStackInSlot(0, scanner);
			tile.markDirty();
		});
	}

	public void export() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("mode", "export");
		sendDataToServer(tag);
	}

	public void updateName(String name) {
		//noinspection ConstantConditions
		ItemStructureScanner.setStructureName(scanner, name);
		if (player.world.isRemote) {
			sendUpdateData("name", new NBTTagString(name));
			return;
		}
		saveScannerData(player);
	}

	public Set<String> getModDependencies() {
		return ItemStructureScanner.getModDependencies(scanner);
	}

	public String getName() {
		return ItemStructureScanner.getStructureName(scanner);
	}

	public String getValidationTypeName() {
		return ItemStructureScanner.getValidator(scanner).validationType.getName();
	}

	public void setIncludeImmediately(boolean checked) {
		ItemStructureScanner.setIncludeImmediately(scanner, checked);
		if (player.world.isRemote) {
			sendUpdateData(INCLUDE_TAG, new NBTTagByte((byte) (checked ? 1 : 0)));
			return;
		}
		saveScannerData(player);
	}

	public boolean getIncludeImmediately() {
		return ItemStructureScanner.getIncludeImmediately(scanner);
	}

	public StructureValidator getValidator() {
		return ItemStructureScanner.getValidator(scanner);
	}

	public void setValidator(StructureValidator validator) {
		ItemStructureScanner.setValidator(scanner, validator);
		if (player.world.isRemote) {
			sendUpdateData(VALIDATOR_TAG, validator.serializeToNBT());
			return;
		}
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

	public void restoreTemplate(String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("mode", "restore");
		tag.setString("templateName", name);
		sendDataToServer(tag);
	}

	public void updateModDependencies(Set<String> mods) {
		//noinspection ConstantConditions
		ItemStructureScanner.setModDependencies(scanner, mods);
		if (player.world.isRemote) {
			sendUpdateData("mods", NBTHelper.getNBTStringList(mods));
			return;
		}
		saveScannerData(player);
	}
}
