package net.shadowmage.ancientwarfare.structure.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.TextUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.network.PacketHighlightBlock;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ConquerHelper {
	private static final Cache<StructureBB, Boolean> STRUCTURE_BB_CONQUERED = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

	private ConquerHelper() {}

	private static boolean checkBBConquered(World world, StructureBB bb) {
		return checkBBConquered(world, bb, npc -> {}, pos -> {});
	}

	public static boolean checkBBConquered(EntityPlayer player, StructureBB bb) {
		return checkBBConquered(player.getEntityWorld(), bb, player, npc -> markNpcAndMessagePlayer(player, npc), pos -> markSpawnerAndMessagePlayer(player, pos));
	}

	private static void markSpawnerAndMessagePlayer(EntityPlayer player, BlockPos pos) {
		NetworkHandler.sendToPlayer((EntityPlayerMP) player, new PacketHighlightBlock(new BlockHighlightInfo(pos, player.getEntityWorld().getTotalWorldTime() + 200)));
		player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_spawner_present"), true);
	}

	private static void markNpcAndMessagePlayer(EntityPlayer player, NpcFaction npc) {
		npc.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200));
		player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarestructure.structure_hostile_alive",
				TextUtils.getSimpleBlockPosString(npc.getPosition())), true);
	}

	private static boolean checkBBConquered(World world, StructureBB bb, Consumer<NpcFaction> onHostileNpcFound, Consumer<BlockPos> onHostileSpawnerFound) {
		return checkBBConquered(world, bb, null, onHostileNpcFound, onHostileSpawnerFound);
	}

	private static boolean checkBBConquered(World world, StructureBB bb, @Nullable EntityPlayer player, Consumer<NpcFaction> onHostileNpcFound, Consumer<BlockPos> onHostileSpawnerFound) {
		AxisAlignedBB boundingBox = bb.getAABB();
		for (NpcFaction factionNpc : world.getEntitiesWithinAABB(NpcFaction.class, boundingBox)) {
			if ((player != null && factionNpc.isHostileTowards(player)) || !factionNpc.isPassive()) {
				onHostileNpcFound.accept(factionNpc);
				return false;
			}
		}

		for (BlockPos blockPos : BlockPos.getAllInBox(bb.min, bb.max)) {
			if (!world.isBlockLoaded(blockPos)) {
				return false;
			}
			if (world.getBlockState(blockPos).getBlock() == AWStructureBlocks.ADVANCED_SPAWNER &&
					WorldTools.getTile(world, blockPos, TileAdvancedSpawner.class).map(te -> SpawnerSettings.spawnsHostileNpcs(te.getSettings())).orElse(false)) {
				onHostileSpawnerFound.accept(blockPos);
				return false;
			}
		}
		return true;
	}

	public static boolean checkBBNotConquered(World world, StructureBB bb) {
		try {
			return !STRUCTURE_BB_CONQUERED.get(bb, () -> checkBBConquered(world, bb));
		}
		catch (ExecutionException e) {
			AncientWarfareNPC.LOG.error("Error getting conquered structureBB info ", e);
			return false;
		}
	}
}
