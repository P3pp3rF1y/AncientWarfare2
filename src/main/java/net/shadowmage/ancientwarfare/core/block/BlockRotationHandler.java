package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;

public class BlockRotationHandler {
    public static EnumFacing getFaceForPlacement(EntityLivingBase entity, IRotatableBlock block, EnumFacing sideHit) {
        if (block.getRotationType() == RotationType.NONE) {
            return EnumFacing.NORTH;
        }
        EnumFacing facing = entity.getHorizontalFacing();
        if (block.getRotationType() == RotationType.SIX_WAY) {
            if (sideHit.getAxis() == EnumFacing.Axis.Y) {
                facing = sideHit.getOpposite();
            }
        }
        if (block.invertFacing()) {
            facing = facing.getOpposite();
        }
        return facing;
    }

    public interface IRotatableBlock {
        public RotationType getRotationType();

        public boolean invertFacing();

/* TODO reimplement this icon stuff
        public Block setIcon(RelativeSide side, String texName);
*/
    }

    public interface IRotatableTile {
        public EnumFacing getPrimaryFacing();

        public void setPrimaryFacing(EnumFacing face);
    }

    public enum RotationType {
        /*
         * Can have 6 textures / inventories.<br>
         * Top, Bottom, Front, Rear, Left, Right<br>
         * Can only face in one of four-directions - N/S/E/W
         */
        FOUR_WAY(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.REAR)),
        /*
         * Can have 3 textures / inventories<br>
         * Top, Bottom, Sides<br>
         * Can face in any orientation - U/D/N/S/E/W
         */
        SIX_WAY(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.ANY_SIDE)),
        /*
         * No rotation, can still have relative sides, but FRONT always == NORTH
         */
        NONE(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.REAR));

        private RotationType(EnumSet<RelativeSide> sides) {
            validSides = sides;
        }

        EnumSet<RelativeSide> validSides;

        public EnumSet<RelativeSide> getValidSides() {
            return validSides;
        }
    }

//public static enum InventorySide
//{
//BOTTOM,TOP,FRONT,REAR,LEFT,RIGHT,NONE;
//}

    public enum RelativeSide {
        TOP("guistrings.inventory.side.top"),
        BOTTOM("guistrings.inventory.side.bottom"),
        FRONT("guistrings.inventory.side.front"),
        REAR("guistrings.inventory.side.rear"),
        LEFT("guistrings.inventory.side.left"),
        RIGHT("guistrings.inventory.side.right"),
        ANY_SIDE("guistrings.inventory.side.all_sides"),
        NONE("guistrings.inventory.side.none");

        private static final int DOWN = EnumFacing.DOWN.ordinal();
        private static final int UP = EnumFacing.UP.ordinal();
        private static final int NORTH = EnumFacing.NORTH.ordinal();
        private static final int SOUTH = EnumFacing.SOUTH.ordinal();
        private static final int WEST = EnumFacing.WEST.ordinal();
        private static final int EAST = EnumFacing.EAST.ordinal();
        //[side-viewed][block-facing]=relative side viewed
        public static final RelativeSide[][] sixWayMap = new RelativeSide[6][6];
        //[side-viewed][block-facing]=relative side viewed
        public static final RelativeSide[][] fourWayMap = new RelativeSide[6][6];
        //[block_meta][relative_side.ordinal] = mcSide output
        public static final int[][] accessMapFourWay = new int[6][6];

        static {
//D,U,N,S,W,E
//[side-viewed][block-facing]=relative side viewed
//fourWayMap[X][0-1] SHOULD BE NEVER REFERENCED AS BLOCK CAN NEVER POINT U/D
            sixWayMap[DOWN][DOWN] = TOP;
            sixWayMap[DOWN][UP] = BOTTOM;
            sixWayMap[DOWN][NORTH] = ANY_SIDE;
            sixWayMap[DOWN][SOUTH] = ANY_SIDE;
            sixWayMap[DOWN][WEST] = ANY_SIDE;
            sixWayMap[DOWN][EAST] = ANY_SIDE;

            sixWayMap[UP][DOWN] = BOTTOM;
            sixWayMap[UP][UP] = TOP;
            sixWayMap[UP][NORTH] = ANY_SIDE;
            sixWayMap[UP][SOUTH] = ANY_SIDE;
            sixWayMap[UP][WEST] = ANY_SIDE;
            sixWayMap[UP][EAST] = ANY_SIDE;

            sixWayMap[NORTH][DOWN] = ANY_SIDE;
            sixWayMap[NORTH][UP] = ANY_SIDE;
            sixWayMap[NORTH][NORTH] = TOP;
            sixWayMap[NORTH][SOUTH] = BOTTOM;
            sixWayMap[NORTH][WEST]= ANY_SIDE;
            sixWayMap[NORTH][EAST] = ANY_SIDE;

            sixWayMap[SOUTH][DOWN] = ANY_SIDE;
            sixWayMap[SOUTH][UP] = ANY_SIDE;
            sixWayMap[SOUTH][NORTH] = BOTTOM;
            sixWayMap[SOUTH][SOUTH] = TOP;
            sixWayMap[SOUTH][WEST]= ANY_SIDE;
            sixWayMap[SOUTH][EAST] = ANY_SIDE;

            sixWayMap[WEST][DOWN] = ANY_SIDE;
            sixWayMap[WEST][UP] = ANY_SIDE;
            sixWayMap[WEST][NORTH] = ANY_SIDE;
            sixWayMap[WEST][SOUTH] = ANY_SIDE;
            sixWayMap[WEST][WEST]= TOP;
            sixWayMap[WEST][EAST] = BOTTOM;

            sixWayMap[EAST][DOWN] = ANY_SIDE;
            sixWayMap[EAST][UP] = ANY_SIDE;
            sixWayMap[EAST][NORTH] = ANY_SIDE;
            sixWayMap[EAST][SOUTH] = ANY_SIDE;
            sixWayMap[EAST][WEST]= BOTTOM;
            sixWayMap[EAST][EAST] = TOP;

            fourWayMap[DOWN][DOWN] = ANY_SIDE;
            fourWayMap[DOWN][UP] = ANY_SIDE;
            fourWayMap[DOWN][NORTH] = BOTTOM;
            fourWayMap[DOWN][SOUTH] = BOTTOM;
            fourWayMap[DOWN][WEST] = BOTTOM;
            fourWayMap[DOWN][EAST] = BOTTOM;

            fourWayMap[UP][DOWN] = ANY_SIDE;
            fourWayMap[UP][UP] = ANY_SIDE;
            fourWayMap[UP][NORTH] = TOP;
            fourWayMap[UP][SOUTH] = TOP;
            fourWayMap[UP][WEST] = TOP;
            fourWayMap[UP][EAST] = TOP;

            fourWayMap[NORTH][DOWN] = ANY_SIDE;
            fourWayMap[NORTH][UP] = ANY_SIDE;
            fourWayMap[NORTH][NORTH] = FRONT;
            fourWayMap[NORTH][SOUTH] = REAR;
            fourWayMap[NORTH][WEST] = RIGHT;
            fourWayMap[NORTH][EAST] = LEFT;

            fourWayMap[SOUTH][DOWN] = ANY_SIDE;
            fourWayMap[SOUTH][UP] = ANY_SIDE;
            fourWayMap[SOUTH][NORTH] = REAR;
            fourWayMap[SOUTH][SOUTH] = FRONT;
            fourWayMap[SOUTH][WEST] = LEFT;
            fourWayMap[SOUTH][EAST] = RIGHT;

            fourWayMap[WEST][DOWN] = ANY_SIDE;
            fourWayMap[WEST][UP] = ANY_SIDE;
            fourWayMap[WEST][NORTH] = LEFT;
            fourWayMap[WEST][SOUTH] = RIGHT;
            fourWayMap[WEST][WEST] = FRONT;
            fourWayMap[WEST][EAST] = REAR;

            fourWayMap[EAST][DOWN] = ANY_SIDE;
            fourWayMap[EAST][UP] = ANY_SIDE;
            fourWayMap[EAST][NORTH] = RIGHT;
            fourWayMap[EAST][SOUTH] = LEFT;
            fourWayMap[EAST][WEST] = REAR;
            fourWayMap[EAST][EAST] = FRONT;
        }

        private String key;

        private RelativeSide(String key) {
            this.key = key;
        }

        public String getTranslationKey() {
            return key;
        }

        public static RelativeSide getSideViewed(RotationType t, EnumFacing facing, EnumFacing side) {
            if (t == RotationType.FOUR_WAY) {
                return fourWayMap[side.ordinal()][facing.ordinal()];
            } else if (t == RotationType.SIX_WAY) {
                return sixWayMap[side.ordinal()][facing.ordinal()];
            }
            return ANY_SIDE;
        }

        @Nullable
        public static EnumFacing getMCSideToAccess(RotationType t, EnumFacing facing, RelativeSide access) {
            RelativeSide[][] map = t == RotationType.FOUR_WAY ? fourWayMap : sixWayMap;
            for (int x = 0; x < map.length; x++) {
                if (map[x][facing.ordinal()] == access) {
                    return EnumFacing.VALUES[x];
                }
            }
            return null;
        }

        @Override
        public String toString(){
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    public static class InventorySided implements ISidedInventory, INBTSerializable<NBTTagCompound> {

        private EnumSet<RelativeSide> validSides = EnumSet.of(RelativeSide.NONE);

        /*
         * Block side to Inventory Side
         * inventorySide should only contain validSides
         */
        private HashMap<RelativeSide, RelativeSide> accessMap = new HashMap<>();

        private HashMap<RelativeSide, int[]> slotsByInventorySide = new HashMap<>();
        private HashMap<RelativeSide, boolean[]> extractInsertFlags = new HashMap<>();//inventoryside x boolean[2]; [0]=extract, [1]=insert
        public final IRotatableTile te;
        public final RotationType rType;
        private NonNullList<ItemStack> inventorySlots;
        private ItemSlotFilter[] filtersByInventorySlot;

        public InventorySided(IRotatableTile te, RotationType rType, int inventorySize) {
            if (te == null || rType == null || inventorySize <= 0) {
                throw new IllegalArgumentException("te and rotation type may not be null, inventory size must be greater than 0");
            }
            this.te = te;
            this.rType = rType;
            inventorySlots = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
            filtersByInventorySlot = new ItemSlotFilter[inventorySize];
            for (RelativeSide rSide : rType.getValidSides()) {
                setAccessibleSideDefault(rSide, RelativeSide.NONE, new int[]{});
            }
        }

        /*
         * Should be called to configure the default access directly after construction of the inventory
         */
        public void setAccessibleSideDefault(RelativeSide rSide, RelativeSide iSide, int[] indices) {
            if (rSide == null || iSide == null || indices == null) {
                throw new IllegalArgumentException("sides or indices may not be null!");
            }
            if (rSide == RelativeSide.NONE) {
                throw new IllegalArgumentException("base side may not be NONE");
            }
            addValidSide(iSide);
            accessMap.put(rSide, iSide);
            setInventoryIndices(iSide, indices);
        }

        public int[] getRawIndices(RelativeSide side) {
            return slotsByInventorySide.get(side);
        }

        public int[] getRawIndicesCombined(){
            return getRawIndicesCombined(validSides.toArray(new RelativeSide[validSides.size()]));
        }

        public int[] getRawIndicesCombined(RelativeSide... sides) {
            int len = 0;
            int[] indices, combindedIndices;
            for (RelativeSide side : sides) {
                indices = getRawIndices(side);
                if (indices != null) {
                    len += indices.length;
                }
            }
            combindedIndices = new int[len];
            int index = 0;
            for (RelativeSide side : sides) {
                indices = getRawIndices(side);
                if (indices != null) {
                    for (int i : indices) {
                        combindedIndices[index] = i;
                        index++;
                    }
                }
            }
            return combindedIndices;
        }

        private void addValidSide(RelativeSide side) {
            validSides.add(side);
        }

        public void remapSideAccess(RelativeSide baseSide, RelativeSide remappedSide) {
            boolean baseValid = rType.getValidSides().contains(baseSide);
            boolean remapValid = baseValid && getValidSides().contains(remappedSide);
            if (baseValid && remapValid) {
                accessMap.put(baseSide, remappedSide);
                markDirty();
            } else {
                throw new IllegalArgumentException("could not remap: " + baseSide + " to: " + remappedSide);
            }
        }

        public RelativeSide getRemappedSide(RelativeSide accessSide) {
            if (!accessMap.containsKey(accessSide)) {
                throw new IllegalArgumentException("no mapping exists for: " + accessSide);
            }
            return accessMap.get(accessSide);
        }

        private void setInventoryIndices(RelativeSide inventorySide, int[] indices) {
            slotsByInventorySide.put(inventorySide, indices);
            markDirty();
        }

        public void setFilterForSlots(ItemSlotFilter filter, int[] indices) {
            for (int i : indices) {
                filtersByInventorySlot[i] = filter;
            }
        }

        public void setExtractInsertFlags(RelativeSide inventorySide, boolean[] flags) {
            if (inventorySide == null || inventorySide == RelativeSide.NONE || flags == null) {
                throw new IllegalArgumentException("inventory side must not be null or NONE, flags must not be null");
            }
            extractInsertFlags.put(inventorySide, flags);
        }

        public RelativeSide getInventorySide(EnumFacing mcSide) {
            RelativeSide rSide = RelativeSide.getSideViewed(rType, te.getPrimaryFacing(), mcSide);
            rSide = accessMap.get(rSide);
            return rSide;
        }

        public ItemSlotFilter getFilterForSlot(int slot) {
            return filtersByInventorySlot[slot];
        }

        @Override
        public int[] getSlotsForFace(EnumFacing side) {
            RelativeSide iSide = getInventorySide(side);
            int[] slots = slotsByInventorySide.get(iSide);
            return slots == null ? new int[]{} : slots;
        }

        @Override
        public boolean canInsertItem(int slot, ItemStack var2, EnumFacing mcSide) {
            RelativeSide iSide = getInventorySide(mcSide);
            if (iSide == null) {
                return false;
            }
            boolean[] flags = extractInsertFlags.get(iSide);
            if (flags != null && !flags[1]) {
                return false;
            }
            return isItemValidForSlot(slot, var2);
        }

        @Override
        public boolean canExtractItem(int slot, ItemStack var2, EnumFacing mcSide) {
            RelativeSide iSide = getInventorySide(mcSide);
            if (iSide == null) {
                return false;
            }
            boolean[] flags = extractInsertFlags.get(iSide);
            return flags == null || flags[0];
        }

        @Override
        public int getSizeInventory() {
            return inventorySlots.size();
        }

        @Override
        public boolean isEmpty() {
            for(ItemStack stack : inventorySlots) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public ItemStack getStackInSlot(int var1) {
            return inventorySlots.get(var1);
        }

        @Override
        public ItemStack decrStackSize(int var1, int var2) {
            @Nonnull ItemStack stack = inventorySlots.get(var1);
            if (!stack.isEmpty()) {
                int qty = var2 > stack.getCount() ? stack.getCount() : var2;
                @Nonnull ItemStack returnStack = stack.copy();
                returnStack.setCount(qty);
                stack.shrink(qty);
                if (stack.getCount() <= 0) {
                    inventorySlots.set(var1, ItemStack.EMPTY);
                }
                if (returnStack.getCount() <= 0) {
                    returnStack = ItemStack.EMPTY;
                }
                markDirty();
                return returnStack;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStackFromSlot(int slot) {
            @Nonnull ItemStack stack = inventorySlots.set(slot, ItemStack.EMPTY);
            markDirty();
            return stack;
        }

        @Override
        public void setInventorySlotContents(int var1, ItemStack var2) {
            inventorySlots.set(var1, var2);
            markDirty();
        }

        @Override
        public String getName() {
            return "aw_inventory_sided";
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Nullable
        @Override
        public ITextComponent getDisplayName() {
            return null;
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {
            ((TileEntity) te).markDirty();
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer var1) {
            return true;
        }

        @Override
        public void openInventory(EntityPlayer player) {
        }

        @Override
        public void closeInventory(EntityPlayer player) {
        }

        @Override
        public boolean isItemValidForSlot(int var1, ItemStack var2) {
            ItemSlotFilter filter = filtersByInventorySlot[var1];
            return filter == null || filter.test(var2);
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) {

        }

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {
            inventorySlots.clear();
        }

        @Override
        public void deserializeNBT(NBTTagCompound tag) {
            InventoryTools.readInventoryFromNBT(this, tag);
            NBTTagCompound accessTag = tag.getCompoundTag("accessTag");
            int[] rMap = accessTag.getIntArray("rMap");
            int[] rMap2 = accessTag.getIntArray("iMap");
            RelativeSide rSide;
            RelativeSide iSide;
            for (int i = 0; i < rMap.length && i < rMap2.length; i++) {
                rSide = RelativeSide.values()[rMap[i]];
                iSide = RelativeSide.values()[rMap2[i]];
                accessMap.put(rSide, iSide);
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            InventoryTools.writeInventoryToNBT(this, tag);
            int l = accessMap.size();
            int rMap[] = new int[l];
            int iMap[] = new int[l];
            int index = 0;
            RelativeSide iSide;
            for (RelativeSide rSide : accessMap.keySet()) {
                iSide = accessMap.get(rSide);
                rMap[index] = rSide.ordinal();
                iMap[index] = iSide.ordinal();
                index++;
            }
            NBTTagCompound accessTag = new NBTTagCompound();
            accessTag.setIntArray("rMap", rMap);
            accessTag.setIntArray("iMap", iMap);
            tag.setTag("accessTag", accessTag);
            return tag;
        }

        public EnumFacing getAccessDirectionFor(RelativeSide blockSide) {
            return RelativeSide.getMCSideToAccess(rType, te.getPrimaryFacing(), blockSide);
        }

        public EnumSet<RelativeSide> getValidSides() {
            return validSides;
        }
    }
}
