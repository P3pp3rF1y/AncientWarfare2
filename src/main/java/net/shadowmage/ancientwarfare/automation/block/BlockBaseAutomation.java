package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public abstract class BlockBaseAutomation extends BlockBase implements IClientRegistrar {

	public BlockBaseAutomation(Material material, String regName) {
		super(material, AncientWarfareAutomation.modID, regName);
		setCreativeTab(AWAutomationItemLoader.automationTab);

		AncientWarfareAutomation.proxy.addClientRegistrar(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		final ResourceLocation assetLocation = new ResourceLocation(AncientWarfareCore.modID, "automation/" + getRegistryName().getResourcePath());

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(assetLocation, getPropertyString(state.getProperties()));
			}
		});

		ModelLoaderHelper.registerItem(this, "automation", "normal");
	}
}
