package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueJunction;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransport;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransportSided;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class TorqueTransportSidedRenderer extends BaseTorqueRendererGeneric<TileTorqueSidedCell> {
	public Map<BlockTorqueTransport.Type, TextureAtlasSprite> sprites = Maps.newHashMap();
	public Map<BlockTorqueTransport.Type, IconTransformation> iconTransforms = Maps.newHashMap();

	protected Collection<CCModel>[] gearHeads = new Collection[6];

	protected TorqueTransportSidedRenderer() {
		super("automation/torque_transport.obj");
		gearHeads[0] = removeGroups(s -> s.startsWith("downShaft."));
		gearHeads[1] = removeGroups(s -> s.startsWith("upShaft."));
		gearHeads[2] = removeGroups(s -> s.startsWith("northShaft."));
		gearHeads[3] = removeGroups(s -> s.startsWith("southShaft."));
		gearHeads[4] = removeGroups(s -> s.startsWith("westShaft."));
		gearHeads[5] = removeGroups(s -> s.startsWith("eastShaft."));
	}

	public void setSprite(BlockTorqueJunction.Type type, TextureAtlasSprite sprite) {
		sprites.put(type, sprite);
		iconTransforms.put(type, new IconTransformation(sprite));
	}

	@Override
	protected Transformation getBaseTransformation() {
		return new Translation(0d, 0.5d, 0d);
	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, @Nullable IExtendedBlockState state) {
		for(EnumFacing facing : EnumFacing.VALUES) {
			if(state.getValue(BlockTorqueTransportSided.CONNECTIONS[facing.ordinal()])) {
				transformedGroups.addAll(rotateShaft(gearHeads[facing.ordinal()], facing, state.getValue(AutomationProperties.ROTATIONS[facing.ordinal()])));
			}
		}
	}

	private Collection<CCModel> rotateShaft(Collection<CCModel> groups, EnumFacing facing, float rotation) {
		return groups.stream().map(m -> rotateShaftPart(m, facing, rotation)).collect(Collectors.toSet());
	}

	private CCModel rotateShaftPart(CCModel part, EnumFacing facing, float rotation) {
		return part.copy().apply(new Rotation(rotation,
				facing.getAxis() == EnumFacing.Axis.X ? 1 : 0,
				facing.getAxis() == EnumFacing.Axis.Y ? 1 : 0,
				facing.getAxis() == EnumFacing.Axis.Z ? 1 : 0
		).at(Vector3.center));
	}

	@Override
	protected IExtendedBlockState handleAdditionalProperties(IExtendedBlockState state, TileTorqueSidedCell junction) {
		state = super.handleAdditionalProperties(state, junction);

		for(EnumFacing facing: EnumFacing.VALUES) {
			state = state.withProperty(BlockTorqueTransportSided.CONNECTIONS[facing.ordinal()], false);
		}

		return state;
	}

	@Override
	protected IconTransformation getIconTransform(IExtendedBlockState state) {
		return iconTransforms.get(state.getValue(BlockTorqueJunction.TYPE));
	}

	@Override
	protected IconTransformation getIconTransform(ItemStack stack) {
		return iconTransforms.get(BlockTorqueJunction.Type.values()[stack.getMetadata()]);
	}

	public TextureAtlasSprite getSprite(BlockTorqueJunction.Type type) {
		return sprites.get(type);
	}
}
