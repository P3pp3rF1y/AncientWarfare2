package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public class TemplateRuleBlockTile<T extends TileEntity> extends TemplateRuleVanillaBlocks {
	private static final String FACING_TAG = "facing";
	public static final String PLUGIN_NAME = "blockTile";
	public NBTTagCompound tag;
	public EnumFacing facing = null;

	private Tuple<Integer, T> tileCache = null;

	public TemplateRuleBlockTile(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		WorldTools.getTile(world, pos).ifPresent(t -> {
			tag = new NBTTagCompound();
			t.writeToNBT(tag);
			tag.removeTag("x");
			tag.removeTag("y");
			tag.removeTag("z");
			if (t instanceof BlockRotationHandler.IRotatableTile) {
				facing = rotateFacing(turns, ((BlockRotationHandler.IRotatableTile) t).getPrimaryFacing());
			}
		});

	}

	private EnumFacing rotateFacing(int turns, EnumFacing o) {
		if (o.getAxis() != EnumFacing.Axis.Y) {
			for (int i = 0; i < turns; i++) {
				o = o.rotateY();
			}
		}
		return o;
	}

	public TemplateRuleBlockTile() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		getTeClass().ifPresent(teClass -> WorldTools.getTile(world, pos, teClass).ifPresent(t -> {
			tag.setInteger("x", pos.getX());
			tag.setInteger("y", pos.getY());
			tag.setInteger("z", pos.getZ());
			try {
				t.readFromNBT(tag);
				rotateTe(t, turns);
			}
			catch (Exception e) {
				AncientWarfareStructure.LOG.error("Error loading tile entity data from template for {}: {}", t.getClass(), e);
			}
		}));
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setTag("teData", this.tag);
		if (facing != null) {
			tag.setString(FACING_TAG, facing.getName());
		}
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		this.tag = tag.getCompoundTag("teData");
		if (tag.hasKey(FACING_TAG)) {
			facing = EnumFacing.byName(tag.getString(FACING_TAG));
		}
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	private static final Field TILE_REGISTRY = ReflectionHelper.findField(TileEntity.class, "REGISTRY", "field_190562_f");

	protected Optional<Class<T>> getTeClass() {
		try {
			//noinspection unchecked
			return Optional.ofNullable(((RegistryNamespaced<ResourceLocation, Class<T>>) TILE_REGISTRY.get(null))
					.getObject(new ResourceLocation(tag.getString("id"))));
		}
		catch (IllegalAccessException e) {
			AncientWarfareStructure.LOG.error("Error accessing TileEntity registry: ", e);
		}
		return Optional.empty();
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(int turns) {
		if (tileCache == null || tileCache.getFirst() != turns) {
			tileCache = new Tuple(turns, getTeClass().map(c -> instantiateTile(c, turns)).orElse(null));
		}
		return tileCache.getSecond();
	}

	private T instantiateTile(Class<T> teClass, int turns) {
		return Arrays.stream(teClass.getConstructors()).filter(c -> c.getParameterCount() == 0).findFirst().map(c -> {
					try {
						T te = teClass.cast(c.newInstance());
						te.readFromNBT(tag);
						te.setWorld(new RuleWorld(getState(turns)));
						rotateTe(te, turns);
						return te;
					}
					catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
						AncientWarfareStructure.LOG.error("Error instantiating tile instance: ", e);
					}
					return null;
				}
		).orElse(null);
	}

	@SuppressWarnings("squid:S1172") // parameters supposed to be used by overriding methods
	protected void rotateTe(T te, int turns) {
		if (facing != null && te instanceof BlockRotationHandler.IRotatableTile) {
			((BlockRotationHandler.IRotatableTile) te).setPrimaryFacing(rotateFacing(turns, facing));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isDynamicallyRendered(int turns) {
		return TileEntityRendererDispatcher.instance.getRenderer(getTileEntity(turns)) != null;
	}
}
