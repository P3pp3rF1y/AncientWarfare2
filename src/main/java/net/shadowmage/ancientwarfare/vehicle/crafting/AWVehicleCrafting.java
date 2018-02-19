// TODO research recipes
// package net.shadowmage.ancientwarfare.vehicle.crafting;
//
//
//public class AWVehicleCrafting {
//
//    /*
//     * load any recipes for vehicle module recipes
//     */
//    public static void loadRecipes() {
//
//        //vehicle
//        this.vanillaRecipeList.add(CraftingManager.getInstance().addRecipe(new ItemStack(BlockLoader.crafting, 1, 3),
//                new Object[] {"sss", "wcw", 's', new ItemStack(Block.stoneSingleSlab, 1, 0), 'w', Block.planks, 'c', Block.chest}));
//
//        //ammo
//        this.vanillaRecipeList.add(CraftingManager.getInstance().addRecipe(new ItemStack(BlockLoader.crafting, 1, 4),
//                new Object[] {"sis", "wcw", 's', new ItemStack(Block.stoneSingleSlab, 1, 0), 'w', Block.planks, 'c', Block.chest, 'i', Item.ingotIron}));
//
//    }
//
//    protected void addUpgradeRecipes() {
//        ResourceListRecipe recipe;
//        for (IVehicleUpgradeType upgrade : VehicleUpgradeRegistry.instance().getUpgradeList()) {
//            if (upgrade == null) {
//                continue;
//            }
//            recipe = upgrade.constructRecipe();
//            if (recipe != null) {
//                this.upgradeRecipes.add(recipe);
//            }
//        }
//        for (IVehicleArmor t : ArmorRegistry.instance().getArmorTypes()) {
//            recipe = t.constructRecipe();
//            if (t != null) {
//                this.upgradeRecipes.add(recipe);
//            }
//        }
//
//        RecipeType type = RecipeType.VEHICLE_MISC;
//
//        recipe = new ResourceListRecipe(ItemLoader.mobilityUnit, 1, type);
//        recipe.addResource(Item.ingotIron, 2, false);
//        recipe.addResource(Block.pistonBase, 1, false);
//        recipe.addResource(Item.redstone, 4, false);
//        recipe.addNeededResearch(ResearchGoalNumbers.mobility1);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.turretComponents, 1, type);
//        recipe.addResource(Item.ingotIron, 1, false);
//        recipe.addResource(Block.pistonBase, 1, false);
//        recipe.addResource(Item.silk, 1, false);
//        recipe.addResource(Item.redstone, 2, false);
//        recipe.addNeededResearch(ResearchGoalNumbers.turrets1);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.torsionUnit, 1, type);
//        recipe.addResource(Item.ingotIron, 1, false);
//        recipe.addResource(Item.stick, 2, false);
//        recipe.addResource(Item.silk, 4, false);
//        recipe.addNeededResearch(ResearchGoalNumbers.torsion1);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.counterWeightUnit, 1, type);
//        recipe.addResource(Item.ingotIron, 2, false);
//        recipe.addResource(Block.cobblestone, 4, false);
//        recipe.addResource(Item.silk, 4, false);
//        recipe.addNeededResearch(ResearchGoalNumbers.counterweights1);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.powderCase, 1, type);
//        recipe.addResource(Item.ingotIron, 4, false);
//        recipe.addResource(Block.cloth, 2, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.explosives1);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.equipmentBay, 1, type);
//        recipe.addResource(Item.ingotIron, 6, false);
//        recipe.addResource(Block.cloth, 2, true);
//        recipe.addResource(Block.planks, 2, true);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.wood1, 8, type);
//        recipe.addResource(Item.leather, 1, false);
//        recipe.addResource(Block.planks, 7, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.wood1);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.wood2, 8, type);
//        recipe.addResource(Item.leather, 2, false);
//        recipe.addResource(Item.ingotIron, 1, false);
//        recipe.addResource(Block.planks, 5, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.wood2);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.wood3, 8, type);
//        recipe.addResource(Item.leather, 1, false);
//        recipe.addResource(Item.ingotIron, 3, false);
//        recipe.addResource(Block.planks, 4, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.wood3);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.wood4, 8, type);
//        recipe.addResource(Item.ingotIron, 4, false);
//        recipe.addResource(Block.planks, 4, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.wood4);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.iron1, 8, type);
//        recipe.addResource(Item.ingotIron, 8, false);
//        recipe.addNeededResearch(ResearchGoalNumbers.iron1);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.iron2, 8, type);
//        recipe.addResource(Item.ingotIron, 8, false);
//        recipe.addResource(Item.coal, 2, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.iron2);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.iron3, 8, type);
//        recipe.addResource(Item.ingotIron, 12, false);
//        recipe.addResource(Item.coal, 4, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.iron3);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.iron4, 8, type);
//        recipe.addResource(Item.ingotIron, 8, false);
//        recipe.addResource(Item.ingotGold, 4, false);
//        recipe.addResource(Item.redstone, 4, false);
//        recipe.addResource(Item.coal, 8, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.iron4);
//        this.upgradeRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.iron5, 8, type);
//        recipe.addResource(Item.ingotIron, 8, false);
//        recipe.addResource(Item.ingotGold, 8, false);
//        recipe.addResource(Item.redstone, 4, false);
//        recipe.addResource(Item.coal, 8, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.iron5);
//        this.upgradeRecipes.add(recipe);
//
//    }
//
//    protected void addAmmoRecipes() {
//        ResourceListRecipe recipe;
//        for (IAmmo ammo : Ammo.ammoTypes) {
//            if (ammo == null) {
//                continue;
//            }
//            {
//                recipe = ammo.constructRecipe();
//                if (recipe != null) {
//                    ammoRecipes.add(recipe);
//                }
//            }
//        }
//
//        RecipeType type = RecipeType.AMMO_MISC;
//
//        recipe = new ResourceListRecipe(ItemLoader.flameCharge, 8, type);
//        recipe.addResource(Item.coal, 2, true);
//        recipe.addResource(Block.cloth, 1, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.flammables1);
//        this.componentRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.explosiveCharge, 8, type);
//        recipe.addResource(Item.gunpowder, 2, false);
//        recipe.addResource(Item.coal, 1, true);
//        recipe.addResource(Item.silk, 1, false);
//        recipe.addNeededResearch(ResearchGoalNumbers.explosives1);
//        this.componentRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.rocketCharge, 8, type);
//        recipe.addResource(Item.gunpowder, 2, false);
//        recipe.addResource(Item.paper, 1, false);
//        recipe.addResource(Item.silk, 1, false);
//        recipe.addNeededResearch(ResearchGoalNumbers.rockets1);
//        this.componentRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.clusterCharge, 8, type);
//        recipe.addResource(Item.ingotIron, 1, false);
//        recipe.addResource(Block.cloth, 1, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.ballistics1);
//        this.componentRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.napalmCharge, 8, type);
//        recipe.addResource(Item.gunpowder, 4, false);
//        recipe.addResource(Block.cloth, 2, true);
//        recipe.addResource(Block.cactus, 2, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.flammables2);
//        recipe.addNeededResearch(ResearchGoalNumbers.ballistics1);
//        this.componentRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.clayCasing, 8, type);
//        recipe.addResource(Item.clay, 4, false);
//        recipe.addResource(Block.cloth, 1, true);
//        this.componentRecipes.add(recipe);
//
//        recipe = new ResourceListRecipe(ItemLoader.ironCasing, 8, type);
//        recipe.addResource(Item.ingotIron, 4, false);
//        recipe.addResource(Block.cloth, 1, true);
//        recipe.addNeededResearch(ResearchGoalNumbers.iron1);
//        this.componentRecipes.add(recipe);
//    }
//
//}
