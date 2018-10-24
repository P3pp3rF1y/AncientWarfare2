package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;

import java.util.List;

public class TemplateRuleBlockSign extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "vanillaSign";
	private ITextComponent signContents[];

	public TemplateRuleBlockSign(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		WorldTools.getTile(world, pos, TileEntitySign.class).ifPresent(t -> {
			signContents = new ITextComponent[t.signText.length];
			System.arraycopy(t.signText, 0, signContents, 0, signContents.length);
		});
	}

	public TemplateRuleBlockSign(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (builder.placeBlock(pos, BlockTools.rotateFacing(state, turns), buildPass)) {
			WorldTools.getTile(world, pos, TileEntitySign.class)
					.ifPresent(t -> System.arraycopy(this.signContents, 0, t.signText, 0, this.signContents.length));
			BlockTools.notifyBlockUpdate(world, pos);
		}
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		for (int i = 0; i < 4; i++) {
			tag.setString("signContents" + i, signContents[i].getFormattedText());
		}
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		super.parseRuleData(tag);
		this.signContents = new ITextComponent[4];
		for (int i = 0; i < 4; i++) {
			//TODO make sure that deserializing here works correctly. For some reason TileEntitySign does this through command instance
			this.signContents[i] = new TextComponentString(tag.getString("signContents" + i));
		}
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
