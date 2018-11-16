package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public class TemplateRuleBlockTile extends TemplateRuleVanillaBlocks {

	public static final String PLUGIN_NAME = "blockTile";
	public NBTTagCompound tag;

	private Tuple<Integer, TileEntity> tileCache = null;

	public TemplateRuleBlockTile(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		WorldTools.getTile(world, pos).ifPresent(t -> {
			tag = new NBTTagCompound();
			t.writeToNBT(tag);
			tag.removeTag("x");
			tag.removeTag("y");
			tag.removeTag("z");
		});
	}

	public TemplateRuleBlockTile() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		WorldTools.getTile(world, pos).ifPresent(t -> {
			//TODO look into changing this so that the whole TE doesn't need reloading from custom NBT
			tag.setInteger("x", pos.getX());
			tag.setInteger("y", pos.getY());
			tag.setInteger("z", pos.getZ());
			t.readFromNBT(tag);
		});
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setTag("teData", this.tag);
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		this.tag = tag.getCompoundTag("teData");
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	private static final Field TILE_REGISTRY = ReflectionHelper.findField(TileEntity.class, "field_190562_f", "REGISTRY");

	private Optional<Class<? extends TileEntity>> getTeClass() {
		try {
			//noinspection unchecked
			return Optional.ofNullable(((RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>>) TILE_REGISTRY.get(null))
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

	private TileEntity instantiateTile(Class<? extends TileEntity> teClass, int turns) {
		return Arrays.stream(teClass.getConstructors()).filter(c -> c.getParameterCount() == 0).findFirst().map(c -> {
					try {
						TileEntity te = (TileEntity) c.newInstance();
						te.readFromNBT(tag);
						te.setWorld(new RuleWorld(getState(turns)));
						return te;
					}
					catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
						AncientWarfareStructure.LOG.error("Error instantiating tile instance: ", e);
					}
					return null;
				}
		).orElse(null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isDynamicallyRendered(int turns) {
		return TileEntityRendererDispatcher.instance.getRenderer(getTileEntity(turns)) != null;
	}
}
