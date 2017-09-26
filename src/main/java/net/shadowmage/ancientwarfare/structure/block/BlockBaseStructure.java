package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockBase;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;

public class BlockBaseStructure extends BlockBase implements IClientRegistrar {
    public BlockBaseStructure(Material material, String regName) {
        super(material, AncientWarfareStructures.modID, regName);
        setCreativeTab(AWStructuresItemLoader.structureTab);

        AncientWarfareStructures.proxy.addClientRegistrar(this);
    }

    @Override
    public void registerClient() {
        final ResourceLocation assetLocation = new ResourceLocation(AncientWarfareCore.modID, "structure/" + getRegistryName().getResourcePath());

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(assetLocation, getPropertyString(state.getProperties()));
            }
        });

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(assetLocation, "inventory"));
    }
}
