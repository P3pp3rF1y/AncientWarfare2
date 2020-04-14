package net.shadowmage.ancientwarfare.vehicle.entity.types;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.materials.VehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.helpers.VehicleFiringVarsHelper;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.UpgradeRegistry;

public class VehicleTypeChestCart extends VehicleType {

	//TODO implement chest cart
	public VehicleTypeChestCart(int typeNum) {
		super(typeNum);
		this.configName = "chest_cart";
		this.vehicleMaterial = VehicleMaterial.materialWood;
		this.materialCount = 3;
		baseHealth = AWVehicleStatics.vehicleStats.vehicleChestCartHealth;
		this.validArmors.add(ArmorRegistry.armorStone);
		this.validArmors.add(ArmorRegistry.armorObsidian);
		this.validArmors.add(ArmorRegistry.armorIron);
		this.validUpgrades.add(UpgradeRegistry.speedUpgrade);
		this.width = 2.7f;
		this.height = 1.8f;
		this.mountable = true;
		this.drivable = true;
		this.combatEngine = false;
		this.riderSits = false;
		this.pilotableBySoldiers = false;
		this.riderVerticalOffset = 0.35f;
		this.riderForwardsOffset = 2.85f;
		this.baseForwardSpeed = 3.7f * 0.05f;
		this.baseStrafeSpeed = 1.75f;
		this.ammoBaySize = 0;
		this.upgradeBaySize = 6;
		this.armorBaySize = 6;
		this.storageBaySize = 54 * 4;
		this.displayName = "item.vehicleSpawner.17";
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noweapon");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.mobile");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.noturret");
		this.displayTooltip.add("item.vehicleSpawner.tooltip.storage");
	}

	@Override
	public VehicleFiringVarsHelper getFiringVarsHelper(VehicleBase veh) {
		return new ChestCartVarHelper(veh);
	}

	@Override
	public ResourceLocation getTextureForMaterialLevel(int level) {
		switch (level) {
			case 0:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/chest_cart_1.png");
			case 1:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/chest_cart_2.png");
			case 2:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/chest_cart_3.png");
			case 3:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/chest_cart_4.png");
			case 4:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/chest_cart_5.png");
			default:
				return new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/vehicle/chest_cart_1.png");
		}
	}

	public class ChestCartVarHelper extends VehicleFiringVarsHelper {

		/**
		 * @param vehicle
		 */
		public ChestCartVarHelper(VehicleBase vehicle) {
			super(vehicle);
		}

		//TODO implement chest cart GUI
		//@Override
		//public boolean interact(EntityPlayer player)
		//  {
		//  boolean control = PlayerTracker.instance().isControlPressed(player);
		//  if(vehicle.mountable() && !player.world.isRemote && !control && (vehicle.getControllingPassenger()==null || vehicle.getControllingPassenger()==player))
		//    {
		//    player.mountEntity(vehicle);
		//    return true;
		//    }
		//  else if(!player.world.isRemote && control)
		//    {
		//    GUIHandler.instance().openGUI(GuiIds.GUI_VEHICLE_INVENTORY, player, vehicle.world, vehicle.entityId, 0, 0);
		//    }
		//  return true;
		//  }

		@Override
		public NBTTagCompound serializeNBT() {
			return new NBTTagCompound();
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFiringUpdate() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReloadUpdate() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLaunchingUpdate() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReloadingFinished() {
			// TODO Auto-generated method stub

		}

		@Override
		public float getVar1() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar2() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar3() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar4() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar5() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar6() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar7() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getVar8() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
