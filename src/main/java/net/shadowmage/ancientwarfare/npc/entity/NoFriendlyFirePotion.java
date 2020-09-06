package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class NoFriendlyFirePotion extends EntityPotion {
	public NoFriendlyFirePotion(World worldIn) {
		super(worldIn);
	}

	public NoFriendlyFirePotion(World worldIn, EntityLivingBase throwerIn, ItemStack potionDamageIn) {
		super(worldIn, throwerIn, potionDamageIn);
	}

	//copy paste from EntityPotion just with a change to call canApplyToEntity to allow for additional conditions there
	@Override
	@SuppressWarnings("squid:S3776") //copy pasted so leaving as close to the original as possible
	protected void applySplash(RayTraceResult rayTraceResult, List<PotionEffect> effects) {
		AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
		List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

		if (!list.isEmpty()) {
			for (EntityLivingBase entitylivingbase : list) {
				if (canApplyToEntity(entitylivingbase)) {
					double d0 = this.getDistanceSq(entitylivingbase);

					if (d0 < 16.0D) {
						double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

						if (entitylivingbase == rayTraceResult.entityHit) {
							d1 = 1.0D;
						}

						for (PotionEffect potioneffect : effects) {
							Potion potion = potioneffect.getPotion();

							if (potion.isInstant()) {
								potion.affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(), d1);
							} else {
								int i = (int) (d1 * (double) potioneffect.getDuration() + 0.5D);

								if (i > 20) {
									entitylivingbase.addPotionEffect(new PotionEffect(potion, i, potioneffect.getAmplifier(), potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean canApplyToEntity(EntityLivingBase entitylivingbase) {
		return entitylivingbase.canBeHitWithPotion() && (!(thrower instanceof NpcBase) || ((NpcBase) thrower).isHostileTowards(entitylivingbase));
	}
}
