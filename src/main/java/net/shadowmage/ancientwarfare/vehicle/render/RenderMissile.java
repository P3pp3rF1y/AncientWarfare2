package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.render.missile.RenderArrow;
import net.shadowmage.ancientwarfare.vehicle.render.missile.RenderShot;

import javax.annotation.Nullable;
import java.util.HashMap;

public class RenderMissile extends Render<MissileBase> {
	private HashMap<IAmmo, Render<MissileBase>> missileRenders = new HashMap<>();
	private RenderArrow arrowRender;
	private RenderShot shotRender;

	public RenderMissile(RenderManager renderManager) {
		super(renderManager);

		arrowRender = new RenderArrow(renderManager);
		shotRender = new RenderShot(renderManager);

		missileRenders.put(AmmoRegistry.ammoStoneShot10, shotRender);
		missileRenders.put(AmmoRegistry.ammoStoneShot15, shotRender);
		missileRenders.put(AmmoRegistry.ammoStoneShot30, shotRender);
		missileRenders.put(AmmoRegistry.ammoStoneShot45, shotRender);
		missileRenders.put(AmmoRegistry.ammoFireShot10, shotRender);
		missileRenders.put(AmmoRegistry.ammoFireShot15, shotRender);
		missileRenders.put(AmmoRegistry.ammoFireShot30, shotRender);
		missileRenders.put(AmmoRegistry.ammoFireShot45, shotRender);
		missileRenders.put(AmmoRegistry.ammoClusterShot10, shotRender);
		missileRenders.put(AmmoRegistry.ammoClusterShot15, shotRender);
		missileRenders.put(AmmoRegistry.ammoClusterShot30, shotRender);
		missileRenders.put(AmmoRegistry.ammoClusterShot45, shotRender);
		missileRenders.put(AmmoRegistry.ammoPebbleShot10, shotRender);
		missileRenders.put(AmmoRegistry.ammoPebbleShot15, shotRender);
		missileRenders.put(AmmoRegistry.ammoPebbleShot30, shotRender);
		missileRenders.put(AmmoRegistry.ammoPebbleShot45, shotRender);
		missileRenders.put(AmmoRegistry.ammoNapalm10, shotRender);
		missileRenders.put(AmmoRegistry.ammoNapalm15, shotRender);
		missileRenders.put(AmmoRegistry.ammoNapalm30, shotRender);
		missileRenders.put(AmmoRegistry.ammoNapalm45, shotRender);
		missileRenders.put(AmmoRegistry.ammoExplosive10, shotRender);
		missileRenders.put(AmmoRegistry.ammoExplosive15, shotRender);
		missileRenders.put(AmmoRegistry.ammoExplosive30, shotRender);
		missileRenders.put(AmmoRegistry.ammoExplosive45, shotRender);
		missileRenders.put(AmmoRegistry.ammoHE10, shotRender);
		missileRenders.put(AmmoRegistry.ammoHE15, shotRender);
		missileRenders.put(AmmoRegistry.ammoHE30, shotRender);
		missileRenders.put(AmmoRegistry.ammoHE45, shotRender);

		missileRenders.put(AmmoRegistry.ammoIronShot5, shotRender);
		missileRenders.put(AmmoRegistry.ammoIronShot10, shotRender);
		missileRenders.put(AmmoRegistry.ammoIronShot15, shotRender);
		missileRenders.put(AmmoRegistry.ammoIronShot25, shotRender);
		missileRenders.put(AmmoRegistry.ammoGrapeShot5, shotRender);
		missileRenders.put(AmmoRegistry.ammoGrapeShot10, shotRender);
		missileRenders.put(AmmoRegistry.ammoGrapeShot15, shotRender);
		missileRenders.put(AmmoRegistry.ammoGrapeShot25, shotRender);
		missileRenders.put(AmmoRegistry.ammoCanisterShot5, shotRender);
		missileRenders.put(AmmoRegistry.ammoCanisterShot10, shotRender);
		missileRenders.put(AmmoRegistry.ammoCanisterShot15, shotRender);
		missileRenders.put(AmmoRegistry.ammoCanisterShot25, shotRender);

		missileRenders.put(AmmoRegistry.ammoArrow, arrowRender);
		missileRenders.put(AmmoRegistry.ammoArrowIron, arrowRender);
		missileRenders.put(AmmoRegistry.ammoArrowFlame, arrowRender);
		missileRenders.put(AmmoRegistry.ammoArrowIronFlame, arrowRender);
		missileRenders.put(AmmoRegistry.ammoRocket, arrowRender);
		missileRenders.put(AmmoRegistry.ammoHwachaRocketFlame, arrowRender);
		missileRenders.put(AmmoRegistry.ammoHwachaRocketExplosive, arrowRender);
		missileRenders.put(AmmoRegistry.ammoHwachaRocketAirburst, arrowRender);
		missileRenders.put(AmmoRegistry.ammoBallistaBolt, arrowRender);
		missileRenders.put(AmmoRegistry.ammoBallistaBoltExplosive, arrowRender);
		missileRenders.put(AmmoRegistry.ammoBallistaBoltFlame, arrowRender);
		missileRenders.put(AmmoRegistry.ammoBallistaBoltIron, arrowRender);

		missileRenders.put(AmmoRegistry.ammoBallShot, shotRender);
		missileRenders.put(AmmoRegistry.ammoBallIronShot, shotRender);
	}

	@Override
	public void doRender(MissileBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
		Render<MissileBase> render = missileRenders.get(entity.ammoType);
		render.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(MissileBase entity) {
		return entity.getTexture();
	}
}
