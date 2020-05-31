package net.shadowmage.ancientwarfare.structure.tile;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureEntry;
import net.shadowmage.ancientwarfare.structure.util.ConquerHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public class TileProtectionFlag extends TileFlag {
	private static final String PLAYER_PROFILE_TAG = "playerProfile";
	private static final String OWNER_TAG = "owner";
	private static final float UNBREAKABLE = -1F;
	private Owner owner = Owner.EMPTY;
	private GameProfile playerProfile;

	@Override
	protected void readNBT(NBTTagCompound tag) {
		super.readNBT(tag);
		if (tag.hasKey(OWNER_TAG)) {
			owner = Owner.deserializeFromNBT(tag.getCompoundTag(OWNER_TAG));
			playerProfile = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag(PLAYER_PROFILE_TAG));
		}
	}

	@Override
	protected NBTTagCompound writeNBT(NBTTagCompound tag) {
		super.writeNBT(tag);
		if (owner != Owner.EMPTY) {
			tag.setTag(OWNER_TAG, owner.serializeToNBT(new NBTTagCompound()));
			tag.setTag(PLAYER_PROFILE_TAG, NBTUtil.writeGameProfile(new NBTTagCompound(), playerProfile));
		}
		return tag;
	}

	@Override
	public void validate() {
		super.validate();
		AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class).getStructureAt(world, pos).ifPresent(structure -> {
			if (!structure.getProtectionFlagPos().equals(pos)) {
				structure.setProtectionFlagPos(pos);
				if (!world.isRemote) {
					NetworkHandler.sendToAllPlayers(new PacketStructureEntry(world.provider.getDimension(), structure.getChunkX(), structure.getChunkZ(), structure));
				}
			}
		});
	}

	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(AWStructureBlocks.PROTECTION_FLAG);
		NBTTagCompound tag = new NBTTagCompound();
		writeNBT(tag);
		stack.setTagCompound(tag);
		return stack;
	}

	public void onActivatedBy(EntityPlayer player) {
		if (isPlayerOwned() || world.isRemote) {
			return;
		}

		Optional<StructureEntry> structure = AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class).getStructureAt(world, pos);
		if (!structure.isPresent()) {
			return;
		}

		StructureEntry st = structure.get();
		if (ConquerHelper.checkBBConquered(player, st.getBB())) {
			turnOffSoundBlocks(st);
			setOwner(player, player.getGameProfile());
			st.setConquered();
			player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_conquered", st.getName()), true);
			world.playSound(null, pos, AWStructureSounds.PROTECTION_FLAG_CLAIM, SoundCategory.BLOCKS, 1, 1);
		}
		markDirty();
		BlockTools.notifyBlockUpdate(this);
	}

	private void turnOffSoundBlocks(StructureEntry structure) {
		for (BlockPos blockPos : BlockPos.getAllInBox(structure.getBB().min, structure.getBB().max)) {
			if (world.getBlockState(blockPos).getBlock() == AWStructureBlocks.SOUND_BLOCK) {
				WorldTools.getTile(world, blockPos, TileSoundBlock.class).ifPresent(TileSoundBlock::turnOffByProtectionFlag);
			}
		}
	}

	private void setOwner(EntityPlayer player, GameProfile playerProfile) {
		owner = new Owner(player);
		this.playerProfile = playerProfile;
	}

	public boolean isPlayerOwned() {
		return owner != Owner.EMPTY;
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return playerProfile;
	}

	public float getPlayerRelativeBlockHardness(EntityPlayer player, float original) {
		return owner.isOwnerOrSameTeamOrFriend(player) ? original : UNBREAKABLE;
	}

	public boolean shouldProtectAgainst(EntityPlayer player) {
		return !owner.isOwnerOrSameTeamOrFriend(player);
	}
}
