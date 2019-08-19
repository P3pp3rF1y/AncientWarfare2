package net.shadowmage.ancientwarfare.structure.render.statue;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class StatueEntityRegistry {
	private StatueEntityRegistry() {}

	private static final Map<String, StatueEntity> STATUE_ENTITIES;

	static {
		ImmutableMap.Builder<String, StatueEntity> builder = new ImmutableMap.Builder<>();
		builder.put("Zombie", new StatueEntity("Zombie", EntityZombie::new, () -> new BipedStatueModel<ModelBiped>(new ModelZombie())));
		builder.put("Enderman", new StatueEntity("Enderman", EntityEnderman::new, () -> new BipedStatueModel<ModelBiped>(new ModelEnderman(0f))));
		builder.put("Skeleton", new StatueEntity("Skeleton", EntitySkeleton::new, () -> new BipedStatueModel<ModelBiped>(new ModelSkeleton())));
		builder.put("Vex", new StatueEntity("Vex", EntityVex::new, VexStatueModel::new));
		builder.put("Zombie Villager", new StatueEntity("Zombie Villager", EntityZombieVillager::new, () -> new BipedStatueModel<ModelBiped>(new ModelZombieVillager())));
		builder.put("Elder Guardian", new StatueEntity("Elder Guardian", EntityElderGuardian::new, GuardianStatueModel::new));
		builder.put("Guardian", new StatueEntity("Guardian", EntityGuardian::new, GuardianStatueModel::new));
		STATUE_ENTITIES = builder.build();
	}

	public static Set<String> getStatueEntityNames() {
		return STATUE_ENTITIES.keySet();
	}

	public static StatueEntity getStatueEntity(String name) {
		return STATUE_ENTITIES.get(name);
	}

	public static class StatueEntity {
		private String name;
		private Function<World, EntityLivingBase> instantiateEntity;
		private Supplier<IStatueModel> getStatueModel;

		public StatueEntity(String name, Function<World, EntityLivingBase> instantiateEntity, Supplier<IStatueModel> getStatueModel) {
			this.name = name;
			this.instantiateEntity = instantiateEntity;
			this.getStatueModel = getStatueModel;
		}

		public EntityLivingBase instantiateEntity(World world) {
			return instantiateEntity.apply(world);
		}

		public IStatueModel getStatueModel() {
			return getStatueModel.get();
		}
	}

}
