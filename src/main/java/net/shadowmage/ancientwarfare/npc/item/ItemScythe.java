package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ItemScythe extends ItemBaseNPC {
	private final ToolMaterial material;

	private final float attackDamage;
	protected float attackSpeed;
	private final int harvestRadius;
	private final Set<BlockPos> blocksToHarvest = new LinkedHashSet<>();

	public ItemScythe(ToolMaterial material, String registryName, float attackOffset, float attackSpeed) {
		super(registryName);
		this.material = material;
		this.maxStackSize = 1;
		this.setMaxDamage(material.getMaxUses());
		this.attackDamage = material.getAttackDamage() + attackOffset;
		this.attackSpeed = attackSpeed;
		setUnlocalizedName(registryName);
		setCreativeTab(AncientWarfareNPC.TAB);
		this.harvestRadius = 3;
	}

	public float getAttackDamage()
	{
		return this.material.getAttackDamage();
	}

	public int getItemEnchantability()
	{
		return this.material.getEnchantability();
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
	{
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

		if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
		{
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed , 0));
		}
		return multimap;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (this.getRegistryName().toString().equals("ancientwarfarenpc:death_scythe")){
			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100));
		}
			return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		List<BlockPos> blocksToCheck = getBlocksToCheck(pos);
		BlockPos currPos;
		// get the list of blocks to check
		for (int i = 0; i < blocksToCheck.size(); i++) {
			currPos = blocksToCheck.get(i);
			Block currBlock = worldIn.getBlockState(currPos).getBlock();
			// add only the farmable blocks to the list
			if (isFarmable(currBlock)) {
				blocksToHarvest.add(currPos);
			}
		}
		Iterator<BlockPos> it = blocksToHarvest.iterator();
		if (blocksToHarvest.isEmpty()) {
			return EnumActionResult.FAIL;
		}
		it.forEachRemaining(t -> BlockTools.breakBlockAndDrop(worldIn, t));	// harvest blocks
		worldIn.playSound(player, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
		blocksToHarvest.clear();
		// damage the stack
		ItemStack item = player.getHeldItem(hand);
		item.damageItem(2, player);
		return EnumActionResult.SUCCESS;
	}

	private boolean isFarmable(Block block) {
		return block instanceof IPlantable;
	}

	public List<BlockPos> getBlocksToCheck(BlockPos pos) {
		List<BlockPos> blocksToCheck = new ArrayList<>();
		blocksToCheck.add(pos);
		if (harvestRadius == 3) {
			blocksToCheck.add(pos.north());
			blocksToCheck.add(pos.north().east());
			blocksToCheck.add(pos.north().west());
			blocksToCheck.add(pos.south());
			blocksToCheck.add(pos.south().east());
			blocksToCheck.add(pos.south().west());
			blocksToCheck.add(pos.east());
			blocksToCheck.add(pos.west());
		}
		return blocksToCheck;
	}
}
