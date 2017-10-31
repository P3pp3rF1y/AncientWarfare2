package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransport;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueTransportJunction;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class TorqueJunctionRenderer extends BaseTorqueRendererGeneric<TileTorqueSidedCell> {
	public static final ModelResourceLocation LIGHT_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":automation/torque_junction", "light");
	public static final ModelResourceLocation MEDIUM_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":automation/torque_junction", "medium");
	public static final ModelResourceLocation HEAVY_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":automation/torque_junction", "heavy");

	public static final TorqueJunctionRenderer INSTANCE = new TorqueJunctionRenderer();

	public Map<BlockTorqueTransport.Type, TextureAtlasSprite> sprites = Maps.newHashMap();
	public Map<BlockTorqueTransport.Type, IconTransformation> iconTransforms = Maps.newHashMap();

	private Collection<CCModel>[] gearHeads = new Collection[6];

	protected TorqueJunctionRenderer() {
		super("automation/torque_junction.obj");
		gearHeads[0] = removeGroups(s -> s.startsWith("downShaft."));
		gearHeads[1] = removeGroups(s -> s.startsWith("upShaft."));
		gearHeads[2] = removeGroups(s -> s.startsWith("northShaft."));
		gearHeads[3] = removeGroups(s -> s.startsWith("southShaft."));
		gearHeads[4] = removeGroups(s -> s.startsWith("westShaft."));
		gearHeads[5] = removeGroups(s -> s.startsWith("eastShaft."));
	}

	public void setSprite(BlockTorqueTransportJunction.Type type, TextureAtlasSprite sprite) {
		sprites.put(type, sprite);
		iconTransforms.put(type, new IconTransformation(sprite));
	}

	@Override
	protected Transformation getBaseTransformation() {
		return new Translation(0d, 0.5d, 0d);
	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, @Nullable IExtendedBlockState state) {
		if(state != null) {
			for(EnumFacing facing : EnumFacing.VALUES) {
				if(state.getValue(BlockTorqueTransportJunction.CONNECTIONS[facing.ordinal()])) {
					transformedGroups.addAll(rotateShaft(gearHeads[facing.ordinal()], facing, state.getValue(AutomationProperties.ROTATIONS[facing.ordinal()])));
				}
			}
		} else {
			transformedGroups.addAll(gearHeads[EnumFacing.NORTH.ordinal()]);
			transformedGroups.addAll(gearHeads[EnumFacing.SOUTH.ordinal()]);
			transformedGroups.addAll(gearHeads[EnumFacing.EAST.ordinal()]);
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
			state = state.withProperty(BlockTorqueTransportJunction.CONNECTIONS[facing.ordinal()], false);
		}

		return state;
	}

	@Override
	protected IconTransformation getIconTransform(IExtendedBlockState state) {
		return iconTransforms.get(state.getValue(BlockTorqueTransportJunction.TYPE));
	}

	@Override
	protected IconTransformation getIconTransform(ItemStack stack) {
		return iconTransforms.get(BlockTorqueTransportJunction.Type.values()[stack.getMetadata()]);
	}

	public TextureAtlasSprite getSprite(BlockTorqueTransportJunction.Type type) {
		return sprites.get(type);
	}
}
