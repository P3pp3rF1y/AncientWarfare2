package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;
import java.util.Optional;

public class TemplateRuleRotatable extends TemplateRuleBlock {
	private static final String TE_DATA_TAG = "teData";
	public static final String PLUGIN_NAME = "rotatable";
	private String blockName;
	private int meta;
	private int orientation;
	private BlockPos p1;
	private BlockPos p2;
	NBTTagCompound tag;

	public TemplateRuleRotatable(World world, BlockPos pos, Block block, int meta, int turns) {
		super(world, pos, block, meta, turns);
		this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
		this.meta = meta;
		Optional<TileEntity> te = WorldTools.getTile(world, pos);
		if (te.isPresent()) {
			TileEntity worksite = te.get();
			EnumFacing o = ((BlockRotationHandler.IRotatableTile) worksite).getPrimaryFacing();
			if (o.getAxis() != EnumFacing.Axis.Y) {
				for (int i = 0; i < turns; i++) {
					o = o.rotateY();
				}
			}
			this.orientation = o.ordinal();
			if (worksite instanceof IBoundedSite && ((IBoundedSite) worksite).hasWorkBounds()) {
				p1 = BlockTools.rotateAroundOrigin(((IBoundedSite) worksite).getWorkBoundsMin().add(-pos.getX(), -pos.getY(), -pos.getZ()), turns);
				p2 = BlockTools.rotateAroundOrigin(((IBoundedSite) worksite).getWorkBoundsMax().add(-pos.getX(), -pos.getY(), -pos.getZ()), turns);
			}
			tag = new NBTTagCompound();
			worksite.writeToNBT(tag);
		}
	}

	public TemplateRuleRotatable(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
		if (world.setBlockState(pos, block.getStateFromMeta(meta), 2)) {
			Optional<TileEntity> te = WorldTools.getTile(world, pos);
			if (te.isPresent()) {
				TileEntity worksite = te.get();
				//TODO look into changing this so that the whole TE doesn't need reloading from custom NBT
				tag.setString("id", block.getRegistryName().toString());
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				worksite.readFromNBT(tag);
				EnumFacing o = EnumFacing.VALUES[orientation];
				if (o.getAxis() != EnumFacing.Axis.Y) {
					for (int i = 0; i < turns; i++) {
						o = o.rotateY();
					}
				}
				((BlockRotationHandler.IRotatableTile) worksite).setPrimaryFacing(o);
				if (worksite instanceof IBoundedSite && p1 != null && p2 != null) {
					BlockPos pos1 = BlockTools.rotateAroundOrigin(p1, turns).add(pos);
					BlockPos pos2 = BlockTools.rotateAroundOrigin(p2, turns).add(pos);
					((IBoundedSite) worksite).setBounds(pos1, pos2);
				}
				BlockTools.notifyBlockUpdate(world, pos);
			}
		}
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		this.blockName = tag.getString("blockId");
		this.meta = tag.getInteger("meta");
		this.orientation = tag.getInteger("orientation");
		if (tag.hasKey(TE_DATA_TAG)) {
			this.tag = tag.getCompoundTag(TE_DATA_TAG);
		}
		if (tag.hasKey("pos1")) {
			this.p1 = getBlockPosFromNBT(tag.getCompoundTag("pos1"));
		}
		if (tag.hasKey("pos2")) {
			this.p2 = getBlockPosFromNBT(tag.getCompoundTag("pos2"));
		}
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setString("blockId", blockName);
		tag.setInteger("meta", meta);
		tag.setInteger("orientation", orientation);
		if (p1 != null) {
			tag.setTag("pos1", writeBlockPosToNBT(new NBTTagCompound(), p1));
		}
		if (p2 != null) {
			tag.setTag("pos2", writeBlockPosToNBT(new NBTTagCompound(), p2));
		}
		if (this.tag != null) {
			tag.setTag(TE_DATA_TAG, this.tag);
		}
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		resources.add(new ItemStack(Item.getItemFromBlock(BlockDataManager.INSTANCE.getBlockForName(blockName)), 1, meta));
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 0;
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
