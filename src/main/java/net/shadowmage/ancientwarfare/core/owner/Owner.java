package net.shadowmage.ancientwarfare.core.owner;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public class Owner {
	public static final Owner EMPTY = new Owner();
	public static final String OWNER_NAME_TAG = "ownerName";
	public static final String OWNER_ID_TAG = "ownerId";
	private final UUID uuid;
	private final String name;

	private Owner() {
		this(new UUID(0, 0), "");
	}

	private Owner(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public Owner(EntityPlayer player) {
		this(player.getUniqueID(), player.getName());
	}

	public Owner(ByteBuf buffer) {
		this(new UUID(buffer.readLong(), buffer.readLong()), ByteBufUtils.readUTF8String(buffer));
	}

	public Owner(World world, String name) {
		EntityPlayer player = world.getPlayerEntityByName(name);
		uuid = player != null ? player.getUniqueID() : new UUID(0, 0);
		this.name = name;
	}

	public boolean isOwnerOrSameTeam(@Nullable EntityPlayer player) {
		return player != null && isOwnerOrSameTeam(player.getUniqueID(), player.getName());
	}

	public boolean isOwnerOrSameTeam(@Nullable UUID playerId, @Nullable String playerName) {
		return ModAccessors.FTBU.areFriendly(playerId, uuid) || (playerName != null && playerName.equals(name));
	}

	public String getName() {
		return name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void serializeToBuffer(ByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		ByteBufUtils.writeUTF8String(buffer, name);
	}

	public NBTTagCompound serializeToNBT(NBTTagCompound tag) {
		if (this == EMPTY) {
			return tag;
		}
		tag.setString(OWNER_NAME_TAG, name);
		tag.setUniqueId(OWNER_ID_TAG, uuid);

		return tag;
	}

	public static Owner deserializeFromNBT(NBTTagCompound tag) {
		if (tag.hasKey(OWNER_NAME_TAG)) {
			//noinspection ConstantConditions - NBTTagCompound has getUniqueId marked as Nullable incorrectly
			return new Owner(tag.getUniqueId(OWNER_ID_TAG), tag.getString(OWNER_NAME_TAG));
		}
		return Owner.EMPTY;
	}
}
