package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.model.DummyBakedModel;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class ParticleOnlyModel extends DummyBakedModel implements IModelParticleProvider, IResourceManagerReloadListener {
	public static final ParticleOnlyModel INSTANCE = new ParticleOnlyModel();
	private Set<TextureAtlasSprite> sprite;

	private Set<TextureAtlasSprite> getSprite() {
		if (sprite == null) {
			sprite = Collections.singleton(TextureUtils.getBlockTexture("planks_oak"));
		}
		return sprite;
	}

	@Override
	public Set<TextureAtlasSprite> getHitEffects(@Nonnull RayTraceResult traceResult, IBlockState state, IBlockAccess world, BlockPos pos) {
		return getSprite();
	}

	@Override
	public Set<TextureAtlasSprite> getDestroyEffects(IBlockState state, IBlockAccess world, BlockPos pos) {
		return getSprite();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getSprite().iterator().next();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		sprite = null;
	}
}
