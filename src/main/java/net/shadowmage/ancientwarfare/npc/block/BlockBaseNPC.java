package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItemLoader;

public class BlockBaseNPC extends BlockBase implements IClientRegistrar {
	public BlockBaseNPC(Material material, String regName) {
		super(material, AncientWarfareNPC.modID, regName);
		setCreativeTab(AWNPCItemLoader.npcTab);

		AncientWarfareNPC.proxy.addClientRegistrar(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		final ModelResourceLocation modelLocation = new ModelResourceLocation(
				new ResourceLocation(AncientWarfareCore.modID, "npc/" + getRegistryName().getResourcePath()), "normal");
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return modelLocation;
			}
		});

		ModelLoaderHelper.registerItem(this, "npc", "normal");
	}
}
