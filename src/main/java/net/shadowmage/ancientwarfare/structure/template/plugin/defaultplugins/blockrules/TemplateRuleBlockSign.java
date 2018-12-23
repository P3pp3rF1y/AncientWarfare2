package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import javax.annotation.Nullable;

public class TemplateRuleBlockSign extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "vanillaSign";
	private ITextComponent[] signContents;
	private int rotation = 0;
	private Tuple<Integer, TileEntitySign> tileCache;

	public TemplateRuleBlockSign(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		WorldTools.getTile(world, pos, TileEntitySign.class).ifPresent(t -> {
			signContents = new ITextComponent[4];
			for (int i = 0; i < 4; i++) {
				signContents[i] = t.signText[i] == null ? new TextComponentString("") : t.signText[i];
			}
		});

		if (state.getBlock() == Blocks.STANDING_SIGN) {
			rotation = (state.getValue(BlockStandingSign.ROTATION) + turns * 4) % 16;
		}
	}

	public TemplateRuleBlockSign() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (builder.placeBlock(pos, getState(turns), buildPass)) {
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
		tag.setInteger("rotation", rotation);
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		this.signContents = new ITextComponent[4];
		for (int i = 0; i < 4; i++) {
			this.signContents[i] = new TextComponentString(tag.getString("signContents" + i));
		}
		rotation = tag.getInteger("rotation");
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(int turns) {
		if (tileCache == null || tileCache.getFirst() != turns) {
			TileEntitySign te = new TileEntitySign();
			System.arraycopy(signContents, 0, te.signText, 0, signContents.length);
			te.setWorld(new RuleWorld(getState(turns)));
			tileCache = new Tuple<>(turns, te);
		}
		return tileCache.getSecond();
	}

	@Override
	public boolean isDynamicallyRendered(int turns) {
		return true;
	}

	@Override
	public IBlockState getState(int turns) {
		IBlockState state = super.getState(turns);
		if (state.getBlock() == Blocks.STANDING_SIGN) {
			state = state.withProperty(BlockStandingSign.ROTATION, (rotation + turns * 4) % 16);
		}
		return state;
	}
}
