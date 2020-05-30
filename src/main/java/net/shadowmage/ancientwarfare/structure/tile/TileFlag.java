package net.shadowmage.ancientwarfare.structure.tile;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;

public class TileFlag extends TileUpdatable {
	static final String NAME_TAG = "name";
	protected String name = "";

	public boolean isPlayerOwned() {
		return false;
	}

	public GameProfile getPlayerProfile() {
		return null;
	}

	public String getName() {
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos, pos.add(1, 3, 1));
	}
}
