package net.shadowmage.ancientwarfare.npc.entity;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMeleeLongRange;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDistressResponse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMedicBase;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIOwnerHurtByTarget;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIOwnerHurtTarget;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAlarmResponse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAttackRanged;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedCommander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedPatrol;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

import javax.annotation.Nonnull;
import java.util.Collection;

@SuppressWarnings({"squid:MaximumInheritanceDepth", "squid:S2160"})
public class NpcCombat extends NpcPlayerOwned implements IRangedAttackMob {
	private static final String PATROL_AI_TAG = "patrolAI";
	private NpcAIAttackMeleeLongRange meleeAI;
	private EntityAIBase arrowAI;
	private NpcAIPlayerOwnedPatrol patrolAI;

	private NpcBase distressedTarget;

	@SuppressWarnings("squid:S4738")
	public NpcCombat(World par1World) {
		super(par1World);
		meleeAI = new NpcAIAttackMeleeLongRange(this);
		arrowAI = new NpcAIPlayerOwnedAttackRanged(this);
		horseAI = new NpcAIPlayerOwnedRideHorse(this);
		patrolAI = new NpcAIPlayerOwnedPatrol(this);

		//noinspection Guava
		Predicate<Entity> selector = this::isHostileTowards;

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(0, new NpcAIDoor(this, true));
		tasks.addTask(0, horseAI);
		tasks.addTask(1, new NpcAIPlayerOwnedFollowCommand(this));
		tasks.addTask(2, new NpcAIPlayerOwnedGetFood(this));
		tasks.addTask(3, new NpcAIPlayerOwnedIdleWhenHungry(this));

		tasks.addTask(5, new NpcAIFollowPlayer(this));
		tasks.addTask(6, new NpcAIPlayerOwnedAlarmResponse(this));

		//6--empty....
		//7==combat task, inserted from onweaponinventoryupdated
		tasks.addTask(8, new NpcAIMedicBase(this));
		tasks.addTask(8, new NpcAIDistressResponse(this));
		tasks.addTask(9, patrolAI);

		tasks.addTask(10, new NpcAIMoveHome(this, 50F, 5F, 20F, 5F));

		//post-100 -- used by delayed shared tasks (look at random stuff, wander)
		tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(102, new NpcAIWander(this));
		tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

		targetTasks.addTask(0, new NpcAIPlayerOwnedCommander(this));
		targetTasks.addTask(1, new NpcAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new NpcAIOwnerHurtTarget(this));
		targetTasks.addTask(3, new NpcAIHurt(this));
		targetTasks.addTask(4, new NpcAIAttackNearest(this, selector));

		setCanPickUpLoot(true);
	}

	@Override
	protected boolean canEquipItem(ItemStack stack) {
		return getHeldItemMainhand().isEmpty() || getHeldItemMainhand().getItem() == stack.getItem();
	}

	@Override
	public boolean isValidOrdersStack(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemCombatOrder;
	}

	@Override
	public void onOrdersInventoryChanged() {
		patrolAI.onOrdersInventoryChanged();
	}

	@Override
	public void onWeaponInventoryChanged() {
		super.onWeaponInventoryChanged();
		if (!world.isRemote) {
			tasks.removeTask(arrowAI);
			tasks.removeTask(meleeAI);
			@Nonnull ItemStack stack = getHeldItemMainhand();
			if (isBow(stack.getItem())) {
				tasks.addTask(4, arrowAI);
			} else {
				tasks.addTask(4, meleeAI);
			}
			if (meleeAI != null) {
				meleeAI.setAttackReachFromWeapon(getHeldItemMainhand());
			}
		}
	}

	@Override
	public boolean canAttackClass(Class claz) {
		return (isBow(getHeldItemMainhand().getItem())) || super.canAttackClass(claz);
	}

	@Override
	public boolean worksInRain() {
		return true;
	}

	@Override
	public boolean isPassive() {
		return false;
	}

	@Override
	public boolean shouldSleep() {
		return false;
	}

	private boolean isBow(Item item) {
		return item instanceof ItemBow;
	}

	@Override
	public String getNpcSubType() {
		return getSubtypeFromEquipment();
	}

	private String getSubtypeFromEquipment() {
		@Nonnull ItemStack stack = getHeldItemMainhand();
		if (!stack.isEmpty()) {
			Item item = stack.getItem();
			Collection<String> tools = item.getToolClasses(stack);
			if (tools.contains("axe")) {
				return "medic";
			} else if (tools.contains("hammer")) {
				return "engineer";
			}
			if (isBow(item)) {
				return "archer";
			} else if (item instanceof ItemCommandBaton) {
				return "commander";
			} else if (item.isEnchantable(stack)) {
				return "soldier";
			}
		}
		return "";
	}

	@Override
	public String getNpcType() {
		return "combat";
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		onWeaponInventoryChanged();
		if (tag.hasKey(PATROL_AI_TAG)) {
			patrolAI.readFromNBT(tag.getCompoundTag(PATROL_AI_TAG));
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setTag(PATROL_AI_TAG, patrolAI.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float force) {
		// minimum inaccuracy = 3.0f, slowly reaches 0 (or close to it) as the NPC reaches max level
		float inaccuracy = 3.0f - ((float) Math.sqrt(getLevelingStats().getBaseLevel()) / (float) Math.sqrt(AWNPCStatics.maxNpcLevel) * 5.0f);
		RangeAttackHelper.doRangedAttack(this, target, force, inaccuracy);
	}

	public void respondToDistress(NpcBase source) {
		// TODO: Target prioritizing or something...?
		distressedTarget = source;
	}

	public NpcBase getDistressedTarget() {
		return distressedTarget;
	}

	public void clearDistress() {
		distressedTarget = null;
	}
}
