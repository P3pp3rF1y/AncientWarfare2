package net.shadowmage.ancientwarfare.structure.datafixes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.structure.block.BlockWoodenCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.TileRuleDataFixer;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleCoffin;

public class WoodenCoffinFixer extends TileRuleDataFixer implements IFixableData {

	private static final String OLD_COFFIN_REG_NAME = "ancientwarfarestructure:coffin";

	public WoodenCoffinFixer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void missingMappingBlock(RegistryEvent.MissingMappings<Block> event) {
		for (RegistryEvent.MissingMappings.Mapping<Block> entry : event.getAllMappings()) {
			if (entry.key.getResourceDomain().equals("ancientwarfarestructure") && entry.key.getResourcePath().equals("coffin")) {
				entry.remap(AWStructureBlocks.WOODEN_COFFIN);
			}
		}
	}

	@SubscribeEvent
	public void missingMappingItem(RegistryEvent.MissingMappings<Item> event) {
		for (RegistryEvent.MissingMappings.Mapping<Item> entry : event.getAllMappings()) {
			if (entry.key.getResourceDomain().equals("ancientwarfarestructure") && entry.key.getResourcePath().equals("coffin")) {
				entry.ignore();
			}
		}
	}

	@Override
	public int getFixVersion() {
		return 11;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String oldId = compound.getString("id");
		if (oldId.equals(OLD_COFFIN_REG_NAME)) {
			compound.setString("id", "ancientwarfarestructure:wooden_coffin");
			compound.setString("variant", mapToNewVariant(compound.getInteger("variant")));
		}
		return compound;
	}

	@Override
	public NBTTagCompound fixRuleCompoundTag(NBTTagCompound compound) {
		return fixTagCompound(compound);
	}

	@Override
	protected String getFixerName() {
		return "WoodenCoffinFixer";
	}

	@Override
	protected FixResult<String> fixJSONData(String data, NBTTagCompound tag) {
		if (tag.hasKey("blockState")) {
			NBTTagCompound blockStateTag = tag.getCompoundTag("blockState");
			if (blockStateTag.getString("blockName").equals(OLD_COFFIN_REG_NAME)) {
				//noinspection ConstantConditions
				blockStateTag.setString("blockName", AWStructureBlocks.WOODEN_COFFIN.getRegistryName().toString());
			}
		}
		return super.fixJSONData(data, tag);
	}

	private String mapToNewVariant(int variant) {
		switch (variant) {
			case 2:
				return BlockWoodenCoffin.Variant.BIRCH.getName();
			case 3:
				return BlockWoodenCoffin.Variant.SPRUCE.getName();
			case 4:
				return BlockWoodenCoffin.Variant.JUNGLE.getName();
			case 5:
				return BlockWoodenCoffin.Variant.ACACIA.getName();
			case 6:
				return BlockWoodenCoffin.Variant.DARK_OAK.getName();
			case 1:
			default:
				return BlockWoodenCoffin.Variant.OAK.getName();
		}
	}

	private static final StructureTemplate.Version VERSION = new StructureTemplate.Version(2, 11);

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return ruleName.equals(TemplateRuleCoffin.PLUGIN_NAME);
	}
}
