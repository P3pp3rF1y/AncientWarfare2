package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.item.IExtendedReachWeapon;

import java.io.IOException;

public class PacketExtendedReachAttack extends PacketBase {
	private int entityId;

	public PacketExtendedReachAttack() {}

	public PacketExtendedReachAttack(int entityId) {this.entityId = entityId;}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(entityId);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		entityId = data.readInt();
	}

	@Override
	protected void execute(EntityPlayer player) {
		Entity entity = player.world.getEntityByID(entityId);
		if (entity == null) {
			return;
		}
		Item heldItem = player.getHeldItemMainhand().getItem();
		if (!(heldItem instanceof IExtendedReachWeapon) || player.getDistance(entity) > ((IExtendedReachWeapon) heldItem).getReach()) {
			return;
		}
		player.attackTargetEntityWithCurrentItem(entity);
	}
}
