package net.shadowmage.ancientwarfare.structure.tile;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.worldgen.StructureEntry;

import java.util.Optional;

public class TileProtectionFlag extends TileUpdatable {
	private static final String UNLOCALIZED_NAME_TAG = "unlocalizedName";
	private static final String PLAYER_PROFILE_TAG = "playerProfile";
	private int topColor = -1;
	private int bottomColor = -1;
	private String unlocalizedName;
	private GameProfile playerProfile = null;

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	private NBTTagCompound writeNBT(NBTTagCompound tag) {
		tag.setInteger("topColor", topColor);
		tag.setInteger("bottomColor", bottomColor);
		if (playerProfile != null) {
			tag.setTag(PLAYER_PROFILE_TAG, NBTUtil.writeGameProfile(new NBTTagCompound(), playerProfile));
		}
		return tag;
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}

	private void readNBT(NBTTagCompound tag) {
		topColor = tag.getInteger("topColor");
		bottomColor = tag.getInteger("bottomColor");
		if (tag.hasKey(PLAYER_PROFILE_TAG)) {
			playerProfile = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag(PLAYER_PROFILE_TAG));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
		unlocalizedName = compound.getString(UNLOCALIZED_NAME_TAG);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = writeNBT(super.writeToNBT(compound));
		tag.setString(UNLOCALIZED_NAME_TAG, unlocalizedName);
		return tag;
	}

	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(AWStructureBlocks.PROTECTION_FLAG);
		NBTTagCompound tag = new NBTTagCompound();
		writeNBT(tag);
		tag.setString(UNLOCALIZED_NAME_TAG, unlocalizedName);
		stack.setTagCompound(tag);
		return stack;
	}

	@SuppressWarnings("ConstantConditions")
	public void setFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			readNBT(tag);
			unlocalizedName = tag.getString(UNLOCALIZED_NAME_TAG);
		}
	}

	public int getTopColor() {
		return topColor;
	}

	public int getBottomColor() {
		return bottomColor;
	}

	public void onActivatedBy(EntityPlayer player) {
		if (isPlayerOwned()) {
			return;
		}

		Optional<StructureEntry> structure = AWGameData.INSTANCE.getData(world, StructureMap.class).getStructureAt(world, pos);
		if (!structure.isPresent()) {
			return;
		}

		if (checkStructureConquered(structure.get(), player)) {
			playerProfile = player.getGameProfile();
			player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_conquered", structure.get().getName()), true);
		}
		markDirty();
		BlockTools.notifyBlockUpdate(this);
	}

	private boolean checkStructureConquered(StructureEntry structure, EntityPlayer player) {
		AxisAlignedBB boundingBox = structure.getBB().getAABB();
		for (NpcFaction factionNpc : world.getEntitiesWithinAABB(NpcFaction.class, boundingBox)) {
			if (!factionNpc.isPassive()) {
				player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_hostile_alive",
						factionNpc.getPosition().toString()), true);
				return false;
			}
		}

		for (BlockPos blockPos : BlockPos.getAllInBox(structure.getBB().min, structure.getBB().max)) {
			if (world.getBlockState(blockPos).getBlock() == AWStructureBlocks.ADVANCED_SPAWNER) {
				player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_spawner_present",
						blockPos.toString()), true);
				return false;
			}
		}
		return true;
	}

	public boolean isPlayerOwned() {
		return playerProfile != null;
	}

	public GameProfile getPlayerProfile() {
		return playerProfile;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos, pos.add(1, 3, 1));
	}
}
