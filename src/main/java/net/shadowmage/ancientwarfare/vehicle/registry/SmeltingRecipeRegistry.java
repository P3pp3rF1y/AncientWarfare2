package net.shadowmage.ancientwarfare.vehicle.registry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;

/**
 * Class responsible for defining and registering the smelting recipes, as these are non-JSON recipes.
 * Also handles dynamic recipe display and usage.
 */
@Mod.EventBusSubscriber(modid = AncientWarfareVehicles.MOD_ID)
public final class SmeltingRecipeRegistry {

	private SmeltingRecipeRegistry() {} // No instances!

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoIronShot5)), new ItemStack(Items.IRON_NUGGET, 3), 0.1f);
		FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoIronShot10)), new ItemStack(Items.IRON_NUGGET, 6), 0.1f);
		FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoIronShot15)), new ItemStack(Items.IRON_NUGGET, 13), 0.1f);
		FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoIronShot25)), new ItemStack(Items.IRON_NUGGET, 19), 0.1f);
	}
}
