package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.TextUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionMounted;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSiegeEngineer;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSoldier;
import net.shadowmage.ancientwarfare.npc.event.EventHandler;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;
import net.shadowmage.ancientwarfare.structure.network.PacketHighlightBlock;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureEntry;
import net.shadowmage.ancientwarfare.structure.util.BlockHighlightInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TileProtectionFlag extends TileUpdatable {
	private static final String NAME_TAG = "name";
	private static final String PLAYER_PROFILE_TAG = "playerProfile";
	private static final String OWNER_TAG = "owner";
	private static final float UNBREAKABLE = -1F;

	private int topColor = -1;
	private int bottomColor = -1;
	private String name = "";
	private Owner owner = Owner.EMPTY;
	private GameProfile playerProfile;

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	private NBTTagCompound writeNBT(NBTTagCompound tag) {
		tag.setInteger("topColor", topColor);
		tag.setInteger("bottomColor", bottomColor);
		if (owner != Owner.EMPTY) {
			tag.setTag(OWNER_TAG, owner.serializeToNBT(new NBTTagCompound()));
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
		if (tag.hasKey(OWNER_TAG)) {
			owner = Owner.deserializeFromNBT(tag.getCompoundTag(OWNER_TAG));
			playerProfile = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag(PLAYER_PROFILE_TAG));
		}
	}

	@Override
	public void validate() {
		super.validate();
		AWGameData.INSTANCE.getData(world, StructureMap.class).getStructureAt(world, pos).ifPresent(structure -> {
			if (!structure.getProtectionFlagPos().equals(pos)) {
				structure.setProtectionFlagPos(pos);
				if (!world.isRemote) {
					NetworkHandler.sendToAllPlayers(new PacketStructureEntry(world.provider.getDimension(), structure.getChunkX(), structure.getChunkZ(), structure));
				}
			}
		});
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
		name = compound.getString(NAME_TAG);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = writeNBT(super.writeToNBT(compound));
		tag.setString(NAME_TAG, name);
		return tag;
	}

	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(AWStructureBlocks.PROTECTION_FLAG);
		NBTTagCompound tag = new NBTTagCompound();
		writeNBT(tag);
		tag.setString(NAME_TAG, name);
		stack.setTagCompound(tag);
		return stack;
	}

	@SuppressWarnings("ConstantConditions")
	public void setFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			readNBT(tag);
			name = tag.getString(NAME_TAG);
		}
	}

	public int getTopColor() {
		return topColor;
	}

	public int getBottomColor() {
		return bottomColor;
	}

	public void onActivatedBy(EntityPlayer player) {
		if (isPlayerOwned() || world.isRemote) {
			return;
		}

		Optional<StructureEntry> structure = AWGameData.INSTANCE.getData(world, StructureMap.class).getStructureAt(world, pos);
		if (!structure.isPresent()) {
			return;
		}

		StructureEntry st = structure.get();
		if (checkStructureConquered(st, player)) {
			turnOffSoundBlocks(st);
			setOwner(player, player.getGameProfile());
			stopHostileSpawnPrevention(st);
			player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_conquered", st.getName()), true);
			world.playSound(null, pos, AWStructureSounds.PROTECTION_FLAG_CLAIM, SoundCategory.BLOCKS, 1, 1);
		}
		markDirty();
		BlockTools.notifyBlockUpdate(this);
	}

	private void stopHostileSpawnPrevention(StructureEntry st) {
		st.stopHostileNaturalSpawnsPrevention();
		EventHandler.invalidatedChunkStructureEntriesCache();
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

	private boolean checkStructureConquered(StructureEntry structure, EntityPlayer player) {
		AxisAlignedBB boundingBox = structure.getBB().getAABB();
		for (NpcFaction factionNpc : world.getEntitiesWithinAABB(NpcFaction.class, boundingBox)) {
			if (!factionNpc.isPassive()) {
				factionNpc.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200));
				player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_hostile_alive",
						TextUtils.getSimpleBlockPosString(factionNpc.getPosition())), true);
				return false;
			}
		}

		for (BlockPos blockPos : BlockPos.getAllInBox(structure.getBB().min, structure.getBB().max)) {
			if (world.getBlockState(blockPos).getBlock() == AWStructureBlocks.ADVANCED_SPAWNER &&
					WorldTools.getTile(world, blockPos, TileAdvancedSpawner.class).map(this::isHostileSpawner).orElse(false)) {

				NetworkHandler.sendToPlayer((EntityPlayerMP) player, new PacketHighlightBlock(new BlockHighlightInfo(blockPos, world.getTotalWorldTime() + 200)));
				player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_spawner_present"), true);
				return false;
			}
		}
		return true;
	}

	private boolean isHostileSpawner(TileAdvancedSpawner te) {
		List<SpawnerSettings.EntitySpawnGroup> groups = te.getSettings().getSpawnGroups();
		if (groups.isEmpty()) {
			return false;
		}
		SpawnerSettings.EntitySpawnGroup firstGroup = groups.get(0);
		List<SpawnerSettings.EntitySpawnSettings> spawnEntities = firstGroup.getEntitiesToSpawn();
		if (spawnEntities.isEmpty()) {
			return false;
		}

		return isHostileNpc(ForgeRegistries.ENTITIES.getValue(spawnEntities.get(0).getEntityId()).getEntityClass());
	}

	private static final Set<Class<? extends Entity>> HOSTILE_NPC_CLASS_TYPES = ImmutableSet.of(
			NpcFactionLeader.class, NpcFactionPriest.class, NpcFactionArcher.class, NpcFactionSiegeEngineer.class, NpcFactionMounted.class, NpcFactionSoldier.class
	);

	private boolean isHostileNpc(Class<? extends Entity> entityClass) {
		return HOSTILE_NPC_CLASS_TYPES.stream().anyMatch(entityClass::isAssignableFrom);
	}

	public boolean isPlayerOwned() {
		return owner != Owner.EMPTY;
	}

	public GameProfile getPlayerProfile() {
		return playerProfile;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos, pos.add(1, 3, 1));
	}

	public float getPlayerRelativeBlockHardness(EntityPlayer player, float original) {
		return owner.isOwnerOrSameTeamOrFriend(player) ? original : UNBREAKABLE;
	}

	public boolean shouldProtectAgainst(EntityPlayer player) {
		return !owner.isOwnerOrSameTeamOrFriend(player);
	}
}
