package net.shadowmage.ancientwarfare.automation.tile.worksite;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;

import javax.annotation.Nullable;
import java.util.Map;

/*
 * abstract base class for worksite based tile-entities (or at least a template to copy from)
 * <p/>
 * handles the management of worker references and work-bounds, as well as inventory bridge methods.
 * <p/>
 * All implementing classes must initialize the inventory field in their constructor, or things
 * will go very crashy when the block is placed in the world.
 *
 * @author Shadowmage
 */
public abstract class TileWorksiteBoundedInventory extends TileWorksiteBounded {
    private static final int MAIN_INVENTORY_SIZE = 27;
    public final ItemStackHandler mainInventory;
    private final Map<RelativeSide, IItemHandler> sideInventories = Maps.newHashMap();
    private final Map<RelativeSide, RelativeSide> inventorySideMappings = Maps.newHashMap();

    public TileWorksiteBoundedInventory() {
        initSideMappings();
        mainInventory = new ItemStackHandler(MAIN_INVENTORY_SIZE);
        setSideInventory(RelativeSide.TOP, mainInventory, RelativeSide.BOTTOM);
    }

    private void initSideMappings() {
        for(RelativeSide side : BlockRotationHandler.RotationType.FOUR_WAY.getValidSides()) {
            inventorySideMappings.put(side, RelativeSide.NONE);
        }
    }

    public void setSideInventory(RelativeSide inventorySide, @Nullable IItemHandler handler, RelativeSide defaultMachineSide) {
        sideInventories.put(inventorySide, handler);
        setInventorySideMappings(defaultMachineSide, inventorySide);
    }

    public void setInventorySideMappings(RelativeSide machineSide, RelativeSide inventorySide) {
        inventorySideMappings.put(machineSide, inventorySide);
    }

    public Map<RelativeSide, IItemHandler> getSideInventories() {
        return sideInventories;
    }

    public Map<RelativeSide, RelativeSide> getInventorySideMappings() {
        return inventorySideMappings;
    }

    @Nullable
    private IItemHandler getInventoryMappedToFacing(@Nullable EnumFacing facing) {
        RelativeSide machineSide = RelativeSide.getSideViewed(BlockRotationHandler.RotationType.FOUR_WAY, getPrimaryFacing(), facing);

        if(inventorySideMappings.containsKey(machineSide)) {
            return sideInventories.get(inventorySideMappings.get(machineSide));
        }

        return null;
    }

    public void openAltGui(EntityPlayer player) {
        //noop, must be implemented by individual tiles, if they have an alt-control gui
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("mainInventory", mainInventory.serializeNBT());

        NBTTagList sideMappings = new NBTTagList();
        for(Map.Entry<RelativeSide, RelativeSide> mapping : inventorySideMappings.entrySet()) {
            sideMappings.appendTag(new NBTTagIntArray(new int[] {mapping.getKey().ordinal(), mapping.getValue().ordinal()}));
        }
        tag.setTag("inventorySideMappings", sideMappings);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey("mainInventory")) {
            mainInventory.deserializeNBT(tag.getCompoundTag("mainInventory"));
        }
        for(NBTBase nbt : tag.getTagList("inventorySideMappings", Constants.NBT.TAG_INT_ARRAY)) {
            NBTTagIntArray mapping = (NBTTagIntArray) nbt;
            setInventorySideMappings(RelativeSide.values()[mapping.getIntArray()[0]], RelativeSide.values()[mapping.getIntArray()[1]]);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getInventoryMappedToFacing(facing) != null) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            IItemHandler handler = getInventoryMappedToFacing(facing);
            if(handler != null) {
                return (T) handler;
            }
        }
        return super.getCapability(capability, facing);
    }
}
