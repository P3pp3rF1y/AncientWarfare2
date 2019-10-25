package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionPriest;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRangedAttack;
import net.shadowmage.ancientwarfare.npc.entity.NoFriendlyFirePotion;

public class NpcFactionPriest extends NpcFaction implements IRangedAttackMob {
	public NpcFactionPriest(World world) {
		super(world);
		addAI();
	}

	public NpcFactionPriest(World world, String factionName) {
		super(world, factionName);
		addAI();
	}

	private void addAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(0, new NpcAIDoor(this, true));
		tasks.addTask(1, new NpcAIFollowPlayer(this));
		tasks.addTask(2, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));
		tasks.addTask(3, new NpcAIFactionPriest(this));
		tasks.addTask(16, new NpcAIFactionRangedAttack(this, 0.8d, 20, 80));

		tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(102, new NpcAIWander(this));
		tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

		targetTasks.addTask(1, new NpcAIHurt(this));
		targetTasks.addTask(15, new NpcAIAttackNearest(this,
				entity -> {
					//noinspection ConstantConditions
					if (!isHostileTowards(entity)) {
						return false;
					}
					if (hasHome()) {
						BlockPos home = getHomePosition();
						double dist = entity.getDistanceSq(home.getX() + 0.5d, home.getY(), home.getZ() + 0.5d);
						if (dist > 30 * 30) {
							return false;
						}
					}
					return true;
				}));
	}

	@Override
	public String getNpcType() {
		return "priest";
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		processWitchThrowPotionLogic(target);
	}

	//copy paste from EntityWitch with the exception of potion entity creation where getPotion is called to get no friendly fire potion
	private void processWitchThrowPotionLogic(EntityLivingBase target) {
		double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
		double d1 = target.posX + target.motionX - posX;
		double d2 = d0 - posY;
		double d3 = target.posZ + target.motionZ - posZ;
		float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
		PotionType potiontype = PotionTypes.HARMING;

		if (f >= 8.0F && !target.isPotionActive(MobEffects.SLOWNESS)) {
			potiontype = PotionTypes.SLOWNESS;
		} else if (target.getHealth() >= 8.0F && !target.isPotionActive(MobEffects.POISON)) {
			potiontype = PotionTypes.POISON;
		} else if (f <= 3.0F && !target.isPotionActive(MobEffects.WEAKNESS) && rand.nextFloat() < 0.25F) {
			potiontype = PotionTypes.WEAKNESS;
		}

		EntityPotion entitypotion = getPotion(potiontype);
		entitypotion.rotationPitch -= -20.0F;
		entitypotion.shoot(d1, d2 + (double) (f * 0.2F), d3, 0.75F, 8.0F);
		world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_WITCH_THROW, getSoundCategory(), 1.0F, 0.8F + rand.nextFloat() * 0.4F);
		world.spawnEntity(entitypotion);
	}

	private EntityPotion getPotion(PotionType potiontype) {
		return new NoFriendlyFirePotion(world, this, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype));
	}

	@Override
	public boolean canAttackClass(Class claz) {
		return true;
	}

	@Override
	public boolean isPassive() {
		return false;
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potionEffect) {
		if (potionEffect.getPotion() == MobEffects.POISON) {
			PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, potionEffect);
			MinecraftForge.EVENT_BUS.post(event);
			return event.getResult() == Event.Result.ALLOW;
		}
		return super.isPotionApplicable(potionEffect);
	}
}
