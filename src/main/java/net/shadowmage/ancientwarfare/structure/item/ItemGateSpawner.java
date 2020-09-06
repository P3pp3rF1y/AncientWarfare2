package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemGateSpawner extends ItemBaseStructure implements IItemKeyInterface, IBoxRenderer {
	private static final String AW_GATE_INFO_TAG = "AWGateInfo";

	public ItemGateSpawner(String name) {
		super(name);
		setMaxStackSize(1);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		NBTTagCompound tag;
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(AW_GATE_INFO_TAG)) {
			tag = stack.getTagCompound().getCompoundTag(AW_GATE_INFO_TAG);
		} else {
			tag = new NBTTagCompound();
		}
		if (tag.hasKey("pos1") && tag.hasKey("pos2")) {
			tooltip.add(I18n.format("guistrings.gate.construct"));
		} else {
			String key = InputHandler.ALT_ITEM_USE_1.getDisplayName();
			tooltip.add(I18n.format("guistrings.gate.use_primary_item_key", key));
		}
		tooltip.add(I18n.format("guistrings.gate.clear_item"));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}

		Gate g;
		for (int i = 0; i < 16; i++) {
			g = Gate.getGateByID(i);
			if (g == null) {
				continue;
			}
			items.add(g.getDisplayStack());
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return "item." + Gate.getGateByID(par1ItemStack.getItemDamage()).getDisplayName();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		NBTTagCompound tag;
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(AW_GATE_INFO_TAG)) {
			tag = stack.getTagCompound().getCompoundTag(AW_GATE_INFO_TAG);
		} else {
			tag = new NBTTagCompound();
		}
		if (player.isSneaking()) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		} else if (tag.hasKey("pos1") && tag.hasKey("pos2")) {
			BlockPos pos1 = BlockPos.fromLong(tag.getLong("pos1"));
			BlockPos pos2 = BlockPos.fromLong(tag.getLong("pos2"));
			BlockPos avg = BlockTools.getAverageOf(pos1, pos2);
			int max = 10;
			if (pos1.getX() - pos2.getX() > max)
				max = pos1.getX() - pos2.getX();
			else if (pos2.getX() - pos1.getX() > max)
				max = pos2.getX() - pos1.getX();
			if (pos1.getZ() - pos2.getZ() > max)
				max = pos1.getZ() - pos2.getZ();
			else if (pos2.getZ() - pos1.getZ() > max)
				max = pos2.getZ() - pos1.getZ();
			if (player.getDistance(avg.getX() + 0.5, pos1.getY() + 0.5, avg.getZ() + 0.5) > max && player.getDistance(avg.getX() + 0.5, pos2.getY() + 0.5, avg.getZ() + 0.5) > max) {
				player.sendMessage(new TextComponentTranslation("guistrings.gate.too_far"));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
			if (!canSpawnGate(world, pos1, pos2)) {
				player.sendMessage(new TextComponentTranslation("guistrings.gate.exists"));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
			Optional<EntityGate> entity = Gate.constructGate(world, pos1, pos2, Gate.getGateByID(stack.getItemDamage()), player.getHorizontalFacing(), new Owner(player));
			if (entity.isPresent()) {
				world.spawnEntity(entity.get());
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				tag.removeTag("pos1");
				tag.removeTag("pos2");
				stack.setTagCompound(tag);
			} else {
				player.sendMessage(new TextComponentTranslation("guistrings.gate.need_to_clear"));
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	private boolean canSpawnGate(World world, BlockPos pos1, BlockPos pos2) {
		BlockPos min = BlockTools.getMin(pos1, pos2);
		BlockPos max = BlockTools.getMax(pos1, pos2);
		AxisAlignedBB newGateBB = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
		AxisAlignedBB oldGateBB;
		List<EntityGate> gates = world.getEntitiesWithinAABB(EntityGate.class, newGateBB);
		for (EntityGate gate : gates) {
			min = BlockTools.getMin(gate.pos1, gate.pos2);
			max = BlockTools.getMax(gate.pos1, gate.pos2);
			oldGateBB = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
			if (oldGateBB.intersects(newGateBB)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, true);
		if (hit == null) {
			return;
		}
		NBTTagCompound tag;
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(AW_GATE_INFO_TAG)) {
			tag = stack.getTagCompound().getCompoundTag(AW_GATE_INFO_TAG);
		} else {
			tag = new NBTTagCompound();
		}
		if (!tag.hasKey("pos2")) {
			if (tag.hasKey("pos1")) {
				Gate g = Gate.getGateByID(stack.getItemDamage());
				if (g.arePointsValidPair(BlockPos.fromLong(tag.getLong("pos1")), hit)) {
					tag.setLong("pos2", hit.toLong());
					player.sendMessage(new TextComponentTranslation("guistrings.gate.set_pos_two"));
				} else {
					player.sendMessage(new TextComponentTranslation("guistrings.gate.invalid_position"));
				}
			} else {
				tag.setLong("pos1", hit.toLong());
				player.sendMessage(new TextComponentTranslation("guistrings.gate.set_pos_one"));
			}
		}
		stack.setTagInfo(AW_GATE_INFO_TAG, tag);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBox(EntityPlayer player, EnumHand hand, ItemStack stack, float delta) {
		NBTTagCompound tag = stack.getTagCompound();
		BlockPos p1;
		BlockPos p2;
		if (tag != null && tag.hasKey(AW_GATE_INFO_TAG)) {
			tag = tag.getCompoundTag(AW_GATE_INFO_TAG);
			if (tag.hasKey("pos1")) {
				p1 = BlockPos.fromLong(tag.getLong("pos1"));
				if (tag.hasKey("pos2")) {
					p2 = BlockPos.fromLong(tag.getLong("pos2"));
				} else {
					p2 = BlockTools.getBlockClickedOn(player, player.world, true);
					if (p2 == null) {
						return;
					}
				}
			} else {
				p1 = BlockTools.getBlockClickedOn(player, player.world, true);
				if (p1 == null) {
					return;
				}
				p2 = p1;
			}
		} else {
			p1 = BlockTools.getBlockClickedOn(player, player.world, true);
			if (p1 == null) {
				return;
			}
			p2 = p1;
		}
		Util.renderBoundingBox(player, BlockTools.getMin(p1, p2), BlockTools.getMax(p1, p2), delta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ResourceLocation basePath = new ResourceLocation(AncientWarfareCore.MOD_ID, "structure/gate_spawner");
		ModelResourceLocation ironBasic = new ModelResourceLocation(basePath, "variant=gate_iron_basic");
		ModelResourceLocation ironDouble = new ModelResourceLocation(basePath, "variant=gate_iron_double");
		ModelResourceLocation ironSingle = new ModelResourceLocation(basePath, "variant=gate_iron_single");
		ModelResourceLocation woodBasic = new ModelResourceLocation(basePath, "variant=gate_wood_basic");
		ModelResourceLocation woodDouble = new ModelResourceLocation(basePath, "variant=gate_wood_double");
		ModelResourceLocation woodRotating = new ModelResourceLocation(basePath, "variant=gate_wood_rotating");
		ModelResourceLocation woodSingle = new ModelResourceLocation(basePath, "variant=gate_wood_single");

		ModelLoader.setCustomMeshDefinition(this, stack -> {
			switch (Gate.getGateByID(stack.getMetadata()).getVariant()) {
				case IRON_BASIC:
					return ironBasic;
				case IRON_DOUBLE:
					return ironDouble;
				case IRON_SINGLE:
					return ironSingle;
				case WOOD_BASIC:
					return woodBasic;
				case WOOD_DOUBLE:
					return woodDouble;
				case WOOD_ROTATING:
					return woodRotating;
				case WOOD_SINGLE:
					return woodSingle;
				default:
					return woodBasic;
			}
		});

		ModelLoader.registerItemVariants(this, ironBasic, ironDouble, ironSingle, woodBasic, woodDouble, woodRotating, woodSingle);
	}
}
