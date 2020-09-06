package net.shadowmage.ancientwarfare.structure.render;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static net.shadowmage.ancientwarfare.core.util.RenderTools.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class PreviewRenderer {
	@SuppressWarnings("NewExpressionSideOnly")
	@SideOnly(Side.CLIENT)
	private static Cache<Integer, CachedState> previewCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	private PreviewRenderer() {}

	public static void clearCache() {
		previewCache.cleanUp();
	}

	@SideOnly(Side.CLIENT)
	public static void renderTemplatePreview(EntityPlayer player, EnumHand hand, ItemStack stack, float delta, StructureTemplate structure, StructureBB bb, int turns) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();
		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
		int cacheKey = getKey(stack, turns, hand);
		Optional<CachedState> state = Optional.ofNullable(previewCache.getIfPresent(cacheKey));
		Vec3i offset = Vec3i.NULL_VECTOR;
		if (state.isPresent()) {
			buffer.setVertexState(state.get().getVertexState());
			BlockPos bbMin = state.get().getBBMin();
			if (!bbMin.equals(bb.min)) {
				offset = bb.min.add(-bbMin.getX(), -bbMin.getY(), -bbMin.getZ());
			}
		} else {
			Map<BlockPos, TemplateRuleBlock> dynamicRenderRules = new HashMap<>();
			renderPreview(structure, bb, turns, buffer, dynamicRenderRules);
			CachedState stateToCache = new CachedState(bb.min, buffer.getVertexState(), dynamicRenderRules);
			previewCache.put(cacheKey, stateToCache);
			state = Optional.of(stateToCache);
		}
		GlStateManager.translate(-getRenderOffsetX(player, delta) + 0.005F + offset.getX(), -getRenderOffsetY(player, delta) + 0.005F + offset.getY(),
				-getRenderOffsetZ(player, delta) + 0.005F + offset.getZ());
		tessellator.draw();
		renderDynamicRules(turns, state.get());

		GlStateManager.popMatrix();
	}

	private static void renderDynamicRules(int turns, CachedState state) {
		Iterator<Map.Entry<BlockPos, TemplateRuleBlock>> it = state.getDynamicRenderRules().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<BlockPos, TemplateRuleBlock> entry = it.next();
			try {
				entry.getValue().renderRuleDynamic(turns, entry.getKey());
			}
			catch (Exception e) {
				it.remove(); // remove erroring tile rendering
			}
		}
	}

	private static int getKey(ItemStack stack, int turns, EnumHand hand) {
		int hash = stack.hashCode();
		hash = hash * 31 + turns;
		hash = hash * 31 + hand.hashCode();
		return hash;
	}

	@SideOnly(Side.CLIENT)
	private static void renderPreview(StructureTemplate structure, StructureBB bb, int turns, BufferBuilder buffer, Map<BlockPos, TemplateRuleBlock> dynamicRenderRules) {
		TemplateBlockAccess blockAccess = new TemplateBlockAccess(structure, bb, turns);
		for (int pass = 0; pass < 3; pass++) {
			for (int y = 0; y < structure.getSize().getY(); y++) {
				for (int x = 0; x < structure.getSize().getX(); x++) {
					for (int z = 0; z < structure.getSize().getZ(); z++) {
						BlockPos pos = new BlockPos(x, y, z);
						renderPreviewAt(structure, bb, turns, buffer, dynamicRenderRules, blockAccess, pos);
					}
				}
			}
		}
	}

	private static void renderPreviewAt(StructureTemplate structure, StructureBB bb, int turns, BufferBuilder buffer, Map<BlockPos, TemplateRuleBlock> dynamicRenderRules, TemplateBlockAccess blockAccess, BlockPos pos) {
		BlockPos translateTo = BlockTools.rotateInArea(pos, structure.getSize().getX(), structure.getSize().getZ(), turns).add(bb.min);
		structure.getRuleAt(pos).ifPresent(r -> {
			r.renderRule(turns, translateTo, blockAccess, buffer);
			if (r.isDynamicallyRendered(turns)) {
				dynamicRenderRules.put(translateTo, r);
			}
		});
	}

	private static class TemplateBlockAccess implements IBlockAccess {
		private final int templateXSize;
		private final int templateZSize;
		private StructureTemplate template;
		private StructureBB bb;
		private int turns;

		private Map<Long, IBlockState> positionStates = new HashMap<>();
		private Map<Long, TileEntity> positionTiles = new HashMap<>();

		private TemplateBlockAccess(StructureTemplate template, StructureBB bb, int turns) {
			this.template = template;
			this.bb = bb;
			this.turns = turns;
			int xSize = template.getSize().getX();
			int zSize = template.getSize().getZ();
			if (turns % 2D != 0) {
				this.templateXSize = zSize;
				this.templateZSize = xSize;
			} else {
				this.templateXSize = xSize;
				this.templateZSize = zSize;
			}
		}

		@Nullable
		@Override
		public TileEntity getTileEntity(BlockPos pos) {
			long posSerialized = pos.toLong();
			if (!positionTiles.containsKey(posSerialized)) {
				if (!bb.contains(pos)) {
					positionTiles.put(posSerialized, null);
				} else {
					getBlockRuleAt(pos).ifPresent(templateRuleBlock -> positionTiles.put(posSerialized, templateRuleBlock.getTileEntity(turns)));
				}
			}
			return positionTiles.get(posSerialized);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public int getCombinedLight(BlockPos pos, int lightValue) {
			return 8;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos) {
			long posSerialized = pos.toLong();
			IBlockState state = positionStates.get(posSerialized);

			if (state == null) {
				if (!bb.contains(pos)) {
					positionStates.put(posSerialized, Blocks.AIR.getDefaultState());
				} else {
					positionStates.put(posSerialized, getBlockRuleAt(pos).map(templateRuleBlock -> templateRuleBlock.getState(turns)).orElse(Blocks.AIR.getDefaultState()));
				}
				state = positionStates.get(posSerialized);
			}
			return state;
		}

		private Optional<TemplateRuleBlock> getBlockRuleAt(BlockPos pos) {
			Vec3i tempPos = BlockTools.rotateInArea(pos.add(-bb.min.getX(), -bb.min.getY(), -bb.min.getZ()), templateXSize, templateZSize, -turns);
			return template.getRuleAt(tempPos);
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			return false;
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return Biomes.PLAINS;
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {
			return WorldType.DEFAULT;
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean defaultValue) {
			return false;
		}
	}

	private static class CachedState {
		private final BlockPos bbMin;
		private final BufferBuilder.State vertexState;
		private final Map<BlockPos, TemplateRuleBlock> dynamicRenderRules;

		private CachedState(BlockPos bbMin, BufferBuilder.State vertexState, Map<BlockPos, TemplateRuleBlock> dynamicRenderRules) {
			this.bbMin = bbMin;
			this.vertexState = vertexState;
			this.dynamicRenderRules = dynamicRenderRules;
		}

		private BufferBuilder.State getVertexState() {
			return vertexState;
		}

		private BlockPos getBBMin() {
			return bbMin;
		}

		private Map<BlockPos, TemplateRuleBlock> getDynamicRenderRules() {
			return dynamicRenderRules;
		}
	}
}
