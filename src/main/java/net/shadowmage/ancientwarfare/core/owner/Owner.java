package net.shadowmage.ancientwarfare.core.owner;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public class Owner {
	public static final Owner EMPTY = new Owner();

	private static final String OWNER_NAME_TAG = "ownerName";
	private static final String OWNER_ID_TAG = "ownerId";
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

	public boolean isOwnerOrSameTeamOrFriend(@Nullable Entity entity) {
		if (entity instanceof IOwnable) {
			Owner owner = ((IOwnable) entity).getOwner();
			return isOwnerOrSameTeamOrFriend(entity.world, owner.getUUID(), owner.getName());
		}

		return entity != null && isOwnerOrSameTeamOrFriend(entity.world, entity.getUniqueID(), entity.getName());
	}

	public boolean isOwnerOrSameTeamOrFriend(World world, @Nullable UUID playerId, String playerName) {
		return TeamViewerRegistry.areFriendly(world, uuid, playerId, name, playerName);
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

	public boolean playerHasCommandPermissions(World world, UUID playerId, String playerName) {
		return this != Owner.EMPTY && TeamViewerRegistry.areTeamMates(world, uuid, playerId, name, playerName);
	}

}
