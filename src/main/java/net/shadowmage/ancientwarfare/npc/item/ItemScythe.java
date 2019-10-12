package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

public class ItemScythe extends ItemBaseNPC {
	private final ToolMaterial material;

	private final float attackDamage;
	protected float attackSpeed;
	private static final int HARVEST_RADIUS = 2;

	public ItemScythe(ToolMaterial material, String registryName, float attackOffset, float attackSpeed) {
		super(registryName);
		this.material = material;
		setMaxStackSize(1);
		setMaxDamage(material.getMaxUses());
		attackDamage = material.getAttackDamage() + attackOffset;
		this.attackSpeed = attackSpeed;
		setUnlocalizedName(registryName);
		setCreativeTab(AncientWarfareNPC.TAB);
	}

	@Override
	public int getItemEnchantability() {
		return this.material.getEnchantability();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
		}
		return multimap;
	}

	@SuppressWarnings("squid:S1186") //used in child class
	protected void applyPotionEffect(EntityLivingBase target) {
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		applyPotionEffect(target);
		return true;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		World world = player.world;
		boolean harvestedSomething = false;
		for (BlockPos currPos : BlockPos.getAllInBox(pos.add(-HARVEST_RADIUS, -HARVEST_RADIUS, -HARVEST_RADIUS), pos.add(HARVEST_RADIUS, HARVEST_RADIUS, HARVEST_RADIUS))) {
			IBlockState currState = world.getBlockState(currPos);
			Block currBlock = currState.getBlock();

			if (isFarmable(currBlock)) {
				harvestedSomething = true;

				BlockTools.breakBlockAndDrop(world, currPos);
				world.playEvent(player, 2001, currPos, Block.getStateId(currState));
			}
		}
		if (!harvestedSomething) {
			return false;
		}
		player.getHeldItemMainhand().damageItem(2, player);
		return true;
	}

	private boolean isFarmable(Block block) {
		return block instanceof IPlantable;
	}
}
