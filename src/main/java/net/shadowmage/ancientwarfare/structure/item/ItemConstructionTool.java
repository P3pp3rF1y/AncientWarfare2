package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class ItemConstructionTool extends ItemBaseStructure implements IItemKeyInterface, IBoxRenderer {

	public ItemConstructionTool(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		ConstructionSettings settings = getSettings(stack);
		if (settings == null) {
			return;
		}
		String text = I18n.format("guistrings.construction.mode") + ": " + settings.type;
		tooltip.add(text);
		text = I18n.format("guistrings.construction.fill_block") + ": " + Block.REGISTRY.getNameForObject(settings.block);
		tooltip.add(text);

		String keyText;
		text = "RMB" + " = " + I18n.format("guistrings.construction.do_action");
		tooltip.add(text);

		keyText = InputHandler.ALT_ITEM_USE_1.getDisplayName();
		text = keyText + " = " + I18n.format("guistrings.construction.toggle_mode");
		tooltip.add(text);

		keyText = InputHandler.ALT_ITEM_USE_2.getDisplayName();
		text = keyText + " = " + I18n.format("guistrings.construction.set_fill_block");
		tooltip.add(text);

		keyText = InputHandler.ALT_ITEM_USE_3.getDisplayName();
		text = keyText + " = " + I18n.format("guistrings.construction.set_pos_1");
		tooltip.add(text);

		keyText = InputHandler.ALT_ITEM_USE_4.getDisplayName();
		text = keyText + " = " + I18n.format("guistrings.construction.set_pos_2");
		tooltip.add(text);

		keyText = InputHandler.ALT_ITEM_USE_5.getDisplayName();
		text = keyText + " = " + I18n.format("guistrings.construction.clear_positions");
		tooltip.add(text);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		ConstructionSettings settings = getSettings(stack);
		if (settings == null) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		switch (settings.type) {
			case SOLID_FILL: {
				handleSolidFill(player, settings);
			}
			break;
			case BOX_FILL: {
				handleBoxFill(player, settings);
			}
			break;
			case LAKE_FILL: {
				handleLakeFill(player, settings);
			}
			break;
			case LAYER_FILL: {
				handleLayerFill(player, settings);
			}
			break;
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	//TODO change all settings.block, settings.meta to use blockstate if possible
	private void handleSolidFill(EntityPlayer player, ConstructionSettings settings) {
		if (settings.pos1 != null && settings.pos2 != null && settings.block != null) {
			BlockPos min = BlockTools.getMin(settings.pos1, settings.pos2);
			BlockPos max = BlockTools.getMax(settings.pos1, settings.pos2);
			for (int x = min.getX(); x <= max.getX(); x++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					for (int y = min.getY(); y <= max.getY(); y++) {
						player.world.setBlockState(new BlockPos(x, y, z), settings.block.getStateFromMeta(settings.meta), 3);
					}
				}
			}
		}
	}

	private void handleBoxFill(EntityPlayer player, ConstructionSettings settings) {
		if (settings.pos1 != null && settings.pos2 != null && settings.block != null) {
			BlockPos min = BlockTools.getMin(settings.pos1, settings.pos2);
			BlockPos max = BlockTools.getMax(settings.pos1, settings.pos2);

			for (int x = min.getX(); x <= max.getX(); x++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					player.world.setBlockState(new BlockPos(x, max.getY(), z), settings.block.getStateFromMeta(settings.meta), 3);
					player.world.setBlockState(new BlockPos(x, min.getY(), z), settings.block.getStateFromMeta(settings.meta), 3);
				}
			}
			for (int x = min.getX(); x <= max.getX(); x++) {
				for (int y = min.getY(); y <= max.getY(); y++) {
					player.world.setBlockState(new BlockPos(x, y, min.getZ()), settings.block.getStateFromMeta(settings.meta), 3);
					player.world.setBlockState(new BlockPos(x, y, max.getZ()), settings.block.getStateFromMeta(settings.meta), 3);
				}
			}
			for (int z = min.getZ(); z <= max.getZ(); z++) {
				for (int y = min.getY(); y <= max.getY(); y++) {
					player.world.setBlockState(new BlockPos(min.getX(), y, z), settings.block.getStateFromMeta(settings.meta), 3);
					player.world.setBlockState(new BlockPos(max.getX(), y, z), settings.block.getStateFromMeta(settings.meta), 3);
				}
			}
		}
	}

	private void handleLakeFill(EntityPlayer player, ConstructionSettings settings) {
		if (settings.block != null) {
			BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
			if (pos != null) {
				IBlockState state = player.world.getBlockState(pos);
				Block block = state.getBlock();
				Set<BlockPos> toFill = new FloodFillPathfinder(player.world, pos, block, state, false, true).doFloodFill();
				for (BlockPos p1 : toFill) {
					player.world.setBlockState(new BlockPos(p1.getX(), p1.getY(), p1.getZ()), settings.block.getStateFromMeta(settings.meta), 3);
				}
			}
		}
	}

	private void handleLayerFill(EntityPlayer player, ConstructionSettings settings) {
		if (settings.block != null) {
			BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
			if (pos != null) {
				IBlockState state = player.world.getBlockState(pos);
				Block block = state.getBlock();
				Set<BlockPos> toFill = new FloodFillPathfinder(player.world, pos, block, state, false, false).doFloodFill();
				for (BlockPos p1 : toFill) {
					player.world.setBlockState(p1, settings.block.getStateFromMeta(settings.meta), 3);
				}
			}
		}
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return true;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		ConstructionSettings settings = getSettings(stack);
		if (settings == null) {
			return;
		}
		switch (altFunction) {
			case ALT_FUNCTION_1://toggle mode
			{
				settings.type = settings.type.next();
			}
			break;
			case ALT_FUNCTION_2://source block
			{
				BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
				if (pos != null) {
					IBlockState state = player.world.getBlockState(pos);
					settings.block = state.getBlock();
					settings.meta = state.getBlock().getMetaFromState(state);
					writeConstructionSettings(stack, settings);
				}
			}
			return;
			case ALT_FUNCTION_3://pos1
			{
				BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
				if (pos != null) {
					settings.pos1 = pos;
					writeConstructionSettings(stack, settings);
				}
			}
			return;
			case ALT_FUNCTION_4://pos2
			{
				BlockPos pos = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
				if (pos != null) {
					settings.pos2 = pos;
					writeConstructionSettings(stack, settings);
				}
			}
			return;
			case ALT_FUNCTION_5://clear pos
			{
				settings.pos1 = null;
				settings.pos2 = null;
			}
			break;
		}
		writeConstructionSettings(stack, settings);
	}

	public static ConstructionSettings getSettings(ItemStack item) {
		if (item.getItem() instanceof ItemConstructionTool) {
			ConstructionSettings settings = new ConstructionSettings();
			if (item.hasTagCompound() && item.getTagCompound().hasKey("constructionSettings")) {
				settings.readFromNBT(item.getTagCompound().getCompoundTag("constructionSettings"));
			}
			return settings;
		}
		return null;
	}

	public static void writeConstructionSettings(ItemStack item, ConstructionSettings settings) {
		if (item.getItem() instanceof ItemConstructionTool) {
			item.setTagInfo("constructionSettings", settings.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBox(EntityPlayer player, EnumHand hand, ItemStack stack, float delta) {
		ConstructionSettings settings = getSettings(stack);
		if (settings == null) {
			return;
		}
		BlockPos p1, p2;
		BlockPos p3 = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
		p1 = settings.hasPos1() ? settings.pos1() : p3;
		p2 = settings.hasPos2() ? settings.pos2() : p3;
		if (p1 != null && p2 != null) {
			Util.renderBoundingBox(player, BlockTools.getMin(p1, p2), BlockTools.getMax(p1, p2), delta);
		}
		if (p3 != null) {
			Util.renderBoundingBox(player, p3, p3, delta, 1, 0, 0);
		}
	}

	public static final class ConstructionSettings {
		Block block;
		int meta;
		BlockPos pos1;
		BlockPos pos2;
		ConstructionType type = ConstructionType.SOLID_FILL;

		protected void readFromNBT(NBTTagCompound tag) {
			if (tag.hasKey("pos1")) {
				pos1 = BlockPos.fromLong(tag.getLong("pos1"));
			}
			if (tag.hasKey("pos2")) {
				pos2 = BlockPos.fromLong(tag.getLong("pos2"));
			}
			if (tag.hasKey("block")) {
				block = Block.getBlockFromName(tag.getString("block"));
			}
			if (tag.hasKey("meta")) {
				meta = tag.getInteger("meta");
			}
			if (tag.hasKey("type")) {
				type = ConstructionType.get(tag.getInteger("type"));
			}
		}

		protected NBTTagCompound writeToNBT(NBTTagCompound tag) {
			if (block != null) {
				tag.setString("block", Block.REGISTRY.getNameForObject(block).toString());
			}
			tag.setInteger("meta", meta);
			if (pos1 != null) {
				tag.setLong("pos1", pos1.toLong());
			}
			if (pos2 != null) {
				tag.setLong("pos2", pos2.toLong());
			}
			tag.setInteger("type", type.ordinal());
			return tag;
		}

		public boolean hasPos1() {
			return pos1 != null;
		}

		public boolean hasPos2() {
			return pos2 != null;
		}

		public BlockPos pos1() {
			return pos1;
		}

		public BlockPos pos2() {
			return pos2;
		}

	}

	public enum ConstructionType {

		/*
		 * Fills current layer and downwards with chosen block
		 */
		LAKE_FILL,

		/*
		 * fills current layer only with chosen block
		 */
		LAYER_FILL,

		/*
		 * fills entire bounding box with chosen block
		 */
		SOLID_FILL,

		/*
		 * creates a box around chosen area with chosen block
		 */
		BOX_FILL;

		public ConstructionType next() {
			int ordinal = ordinal();
			ordinal++;
			if (ordinal >= values().length) {
				ordinal = 0;
			}
			return values()[ordinal];
		}

		public static ConstructionType get(int index) {
			if (index >= 0 && index < values().length) {
				return values()[index];
			}
			return SOLID_FILL;
		}
	}

}
