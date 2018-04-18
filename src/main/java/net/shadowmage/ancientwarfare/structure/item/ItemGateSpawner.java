/*
 Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.client.renderer.ItemMeshDefinition;
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
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGateSpawner extends ItemBaseStructure implements IItemKeyInterface, IBoxRenderer {

	public ItemGateSpawner(String name) {
		super(name);
		setMaxStackSize(1);
		setHasSubtypes(true);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		NBTTagCompound tag;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
			tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
		} else {
			tag = new NBTTagCompound();
		}
		if (tag.hasKey("pos1") && tag.hasKey("pos2")) {
			tooltip.add(I18n.format("guistrings.gate.construct"));
		} else {
			String key = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
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
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
			tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
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
			if (player.getDistance(avg.getX() + 0.5, pos1.getY() + 0.5, avg.getZ() + 0.5) > max && player
					.getDistance(avg.getX() + 0.5, pos2.getY() + 0.5, avg.getZ() + 0.5) > max) {
				player.sendMessage(new TextComponentTranslation("guistrings.gate.too_far"));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
			if (!canSpawnGate(world, pos1, pos2)) {
				player.sendMessage(new TextComponentTranslation("guistrings.gate.exists"));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
			EntityGate entity = Gate.constructGate(world, pos1, pos2, Gate.getGateByID(stack.getItemDamage()), player.getHorizontalFacing());
			if (entity != null) {
				entity.setOwner(player);
				world.spawnEntity(entity);
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

	protected boolean canSpawnGate(World world, BlockPos pos1, BlockPos pos2) {
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
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
		return key == ItemKey.KEY_0;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
		BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, true);
		if (hit == null) {
			return;
		}
		NBTTagCompound tag;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
			tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
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
		stack.setTagInfo("AWGateInfo", tag);
	}

	@Override
	public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
		NBTTagCompound tag = stack.getTagCompound();
		BlockPos p1, p2;
		if (tag != null && tag.hasKey("AWGateInfo")) {
			tag = tag.getCompoundTag("AWGateInfo");
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
		ResourceLocation basePath = new ResourceLocation(AncientWarfareCore.modID, "structure/gate_spawner");
		ModelResourceLocation IRON_BASIC = new ModelResourceLocation(basePath, "variant=gate_iron_basic");
		ModelResourceLocation IRON_DOUBLE = new ModelResourceLocation(basePath, "variant=gate_iron_double");
		ModelResourceLocation IRON_SINGLE = new ModelResourceLocation(basePath, "variant=gate_iron_single");
		ModelResourceLocation WOOD_BASIC = new ModelResourceLocation(basePath, "variant=gate_wood_basic");
		ModelResourceLocation WOOD_DOUBLE = new ModelResourceLocation(basePath, "variant=gate_wood_double");
		ModelResourceLocation WOOD_ROTATING = new ModelResourceLocation(basePath, "variant=gate_wood_rotating");
		ModelResourceLocation WOOD_SINGLE = new ModelResourceLocation(basePath, "variant=gate_wood_single");

		ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				switch (Gate.getGateByID(stack.getMetadata()).getVariant()) {
					case IRON_BASIC:
						return IRON_BASIC;
					case IRON_DOUBLE:
						return IRON_DOUBLE;
					case IRON_SINGLE:
						return IRON_SINGLE;
					case WOOD_BASIC:
						return WOOD_BASIC;
					case WOOD_DOUBLE:
						return WOOD_DOUBLE;
					case WOOD_ROTATING:
						return WOOD_ROTATING;
					case WOOD_SINGLE:
						return WOOD_SINGLE;
					default:
						return WOOD_BASIC;
				}
			}
		});

		ModelLoader.registerItemVariants(this, IRON_BASIC, IRON_DOUBLE, IRON_SINGLE, WOOD_BASIC, WOOD_DOUBLE, WOOD_ROTATING, WOOD_SINGLE);
	}
}
