package net.shadowmage.ancientwarfare.structure.tile;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;

public class TileFlag extends TileUpdatable {
	static final String NAME_TAG = "name";
	int topColor = -1;
	int bottomColor = -1;
	protected String name = "";

	public boolean isPlayerOwned() {
		return false;
	}

	public GameProfile getPlayerProfile() {
		return null;
	}

	public int getTopColor() {
		return topColor;
	}

	public int getBottomColor() {
		return bottomColor;
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
