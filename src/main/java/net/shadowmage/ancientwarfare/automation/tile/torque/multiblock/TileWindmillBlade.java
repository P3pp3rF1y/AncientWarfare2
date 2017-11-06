package net.shadowmage.ancientwarfare.automation.tile.torque.multiblock;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockFinder;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TileWindmillBlade extends TileUpdatable implements ITickable {

    double bladeRpm = 20.d;
    double bladeRpt = bladeRpm * AWAutomationStatics.rpmToRpt;

    public BlockPos controlPos;

    private boolean isControl = false;//set to true if this is the control block for a setup
    protected float rotation;//used in rendering
    protected float lastRotationDiff;

    /*
     * the raw size of the windmill in blocks tall
     */
    private int windmillSize = 0;
    public double energy = 0;
    private EnumFacing windmillDirection = EnumFacing.NORTH;

    public TileWindmillBlade() {

    }

    public boolean isControl() {
        return isControl;
    }

    public boolean isFormed() {
        return controlPos != null;
    }

    public EnumFacing getDirection() {
        return windmillDirection;
    }

    public int getWindmillSize() {
        return windmillSize;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public void update() {
        if (isControl) {
            if (!world.isRemote)
                energy = windmillSize * AWAutomationStatics.windmill_per_size_output;
            else
                updateRotation();
        }

    }

    public float getRotation(float delta){
        return rotation - lastRotationDiff * (1 - delta);
    }

    protected void updateRotation() {
        lastRotationDiff = (float) (bladeRpt * Trig.TORADIANS);
        rotation += lastRotationDiff;
        rotation %= Trig.PI * 2;
    }

    public void blockPlaced() {
        if(!world.isRemote)
            validateSetup();
    }

    public void blockBroken() {
        if(!world.isRemote) {
            if(isControl){
                int expand = (windmillSize - 1) / 2;
                if(expand>0) {
                    TileEntity tileEntity;
                    if (windmillDirection.getAxis() == EnumFacing.Axis.Z) {
                        for (int j = -expand; j < expand + 1; j++) {
                            for (int i = -expand; i < expand + 1; i++) {
                                tileEntity = world.getTileEntity(pos.add(i, j, 0));
                                if (tileEntity instanceof TileWindmillBlade){
                                    ((TileWindmillBlade) tileEntity).setController(null);
                                }
                            }
                        }
                    } else if (windmillDirection.getAxis() == EnumFacing.Axis.X) {
                        for (int j = -expand; j < expand + 1; j++) {
                            for (int i = -expand; i < expand + 1; i++) {
                                tileEntity = world.getTileEntity(pos.add(0, j, i));
                                if (tileEntity instanceof TileWindmillBlade) {
                                    ((TileWindmillBlade) tileEntity).setController(null);
                                }
                            }
                        }
                    }
                }
                isControl = false;
            }else if(controlPos!=null){
                TileEntity te = world.getTileEntity(controlPos);
                controlPos = null;
                if(te instanceof TileWindmillBlade)
                    ((TileWindmillBlade) te).validateSetup();
            }else
                informNeighborsToValidate();
        }
    }

    public TileWindmillBlade getMaster() {
        if (controlPos != null) {
            TileEntity te = world.getTileEntity(controlPos);
            return te instanceof TileWindmillBlade ? (TileWindmillBlade) te : null;
        }else if(isControl)
            return this;
        return null;
    }

    protected boolean validateSetup() {
        BlockFinder finder = new BlockFinder(world, getBlockType(), getBlockMetadata(), 9*9);
        Pair<BlockPos, BlockPos> corners = finder.cross(pos);
        int minX = corners.getLeft().getX(), minY = corners.getLeft().getY(), minZ = corners.getLeft().getZ();
        int xSize = corners.getRight().getX() - minX + 1, ySize = corners.getRight().getY() - minY + 1, zSize = corners.getRight().getZ() - minZ + 1;

        /*
         * if y size >= 5
         * and y size <= 17
         * and h%2==1 (is an odd size, 5, 7, 9, etc)
         * and either x or z size == 1 (one needs to be a single block thick)
         * and either x or z == y size (the other needs to be the same size as height)
         * and is full cube (all block spots filled) (will need to modify this check for those sizes with missing corner blocks, create bit mask 2d array to test for proper setup)
         */
        boolean valid = ySize >= 5 && ySize % 2 == 1 && (zSize == 1 || xSize == 1) && (zSize == ySize || xSize == ySize) && finder.box(corners);
        if (valid) {
            /*
             * calculate the control block coordinates from the min coordinate and sizes
             */
            int controlX, controlY, controlZ;

            int halfSize = (ySize - 1) / 2;

            controlY = minY + halfSize;//should be the center
            if (xSize > 1)//widest on X axis
            {
                controlX = minX + halfSize;//should be the center
                controlZ = minZ;//only 1 z-coordinate
            } else//widest on Z axis
            {
                controlX = minX;//only 1 x-coordinate
                controlZ = minZ + halfSize;//should be the center
            }
            setValidSetup(finder.getPositions(), controlX, controlY, controlZ, xSize, ySize, zSize);
        } else {
            finder.connect(corners.getLeft(), new BlockPos(xSize, ySize, zSize));
            setInvalidSetup(getAllConnectedBlades());
        }
        return valid;
    }

    private List<BlockPos> getAllConnectedBlades() {
        int maxRadius = 20;
        List<BlockPos> connectedBlocks = Lists.newArrayList();
        List<BlockPos> searchedPositions = Lists.newArrayList();
        BlockPos currentPos = pos;

        connectedBlocks.add(currentPos);
        searchedPositions.add(currentPos);

        getConnectedBlades(maxRadius, connectedBlocks, searchedPositions, currentPos);

        return connectedBlocks;
    }

    private void getConnectedBlades(int maxRadius, List<BlockPos> connectedBlocks, List<BlockPos> searchedPositions, BlockPos currentPos) {
        if(currentPos.getDistance(pos.getX(), pos.getY(), pos.getZ()) < maxRadius) {
            for(EnumFacing facing : EnumFacing.VALUES) {
                BlockPos offsetPos = currentPos.offset(facing);
                if (!searchedPositions.contains(offsetPos) && world.getBlockState(offsetPos).getBlock() == AWAutomationBlocks.windmillBlade) {
                    connectedBlocks.add(offsetPos);
                    searchedPositions.add(offsetPos);
                    getConnectedBlades(maxRadius, connectedBlocks, searchedPositions, offsetPos);
                }
            }
        }
    }

    private void informNeighborsToValidate() {
        TileEntity te;
        for (EnumFacing d : EnumFacing.VALUES) {
            te = world.getTileEntity(pos.offset(d));
            if (te instanceof TileWindmillBlade) {
                ((TileWindmillBlade) te).validateSetup();
            }
        }
    }

    private void setController(BlockPos pos) {
        boolean dirty = false;
        if(pos == null) {
            if(isControl || controlPos!=null)
                dirty = true;
            isControl = false;
            controlPos = null;
        }else {
            if(!pos.equals(controlPos))
                dirty = true;
            controlPos = pos;
        }
        if(dirty){
            markDirty();
            BlockTools.notifyBlockUpdate(this);
        }
    }

    private void setInvalidSetup(List<BlockPos> set) {
        TileEntity te;
        for (BlockPos pos : set) {
            te = world.getTileEntity(pos);
            if (te instanceof TileWindmillBlade) {
                ((TileWindmillBlade) te).setController(null);
            }
        }
    }

    private void setValidSetup(List<BlockPos> set, int cx, int cy, int cz, int xs, int ys, int zs) {
        setController(new BlockPos(cx, cy, cz));
        TileEntity te = world.getTileEntity(controlPos);
        if (te instanceof TileWindmillBlade) {
            ((TileWindmillBlade) te).setAsController(xs, ys, zs);
            for (BlockPos pos : set) {
                te = world.getTileEntity(pos);
                if (te instanceof TileWindmillBlade) {
                    ((TileWindmillBlade) te).setController(controlPos);
                }
            }
        }else
            setController(null);
    }

    private void setAsController(int xSize, int ySize, int zSize) {
        windmillDirection = xSize == 1 ? EnumFacing.WEST : EnumFacing.NORTH;
        windmillSize = ySize;
        this.isControl = true;
        markDirty();
        BlockTools.notifyBlockUpdate(this);
    }

    @Override
    protected void writeUpdateNBT(NBTTagCompound tag) {
        super.writeUpdateNBT(tag);
        tag.setBoolean("isControl", isControl);
        if (controlPos != null) {
            tag.setLong("controlPos", controlPos.toLong());
        }
        if (isControl) {
            tag.setInteger("size", windmillSize);
            tag.setByte("direction", (byte) windmillDirection.ordinal());
        }
    }

    @Override
    protected void handleUpdateNBT(NBTTagCompound tag) {
        super.handleUpdateNBT(tag);
        controlPos = tag.hasKey("controlPos") ? BlockPos.fromLong(tag.getLong("controlPos")) : null;
        isControl = tag.getBoolean("isControl");
        if (isControl) {
            windmillSize = tag.getInteger("size");
            windmillDirection = EnumFacing.VALUES[tag.getByte("direction")];
        }
        BlockTools.notifyBlockUpdate(this);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        controlPos = tag.hasKey("controlPos") ? BlockPos.fromLong(tag.getLong("controlPos")) : null;
        isControl = tag.getBoolean("isControl");
        if (isControl) {
            windmillSize = tag.getInteger("size");
            windmillDirection = EnumFacing.VALUES[tag.getByte("direction")];
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isControl", isControl);
        if (controlPos != null) {
            tag.setLong("controlPos", controlPos.toLong());
        }
        if (isControl) {
            tag.setInteger("size", windmillSize);
            tag.setByte("direction", (byte) windmillDirection.ordinal());
        }

        return tag;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (isControl) {
            int expand = (windmillSize - 1) / 2;
            return new AxisAlignedBB(pos.getX() - expand, pos.getY() - expand, pos.getZ() - expand, pos.getX() + 1 + expand, pos.getY() + 1 + expand, pos.getZ() + 1 + expand);
        }
        return getBlockType().getCollisionBoundingBox(world.getBlockState(pos), world, pos);
    }
}
