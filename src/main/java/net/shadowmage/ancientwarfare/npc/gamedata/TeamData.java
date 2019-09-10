package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class TeamData extends WorldSavedData {
	public TeamData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return null;
	}

	public void updatePlayerTeamStandings(EntityPlayer player) {
		//check player teams (scoreboard and FTB)

		//check if these are added in

		//if they were not previously there or player wasn't add team, player, team standings with factions becomes average of all players in the team including this new player
		//record initial standings for the teams for individual factions (make sure that new faction added is covered here as well)

		//if the player was already in the team take a look at last standings recorded for the teams for factions and adjust player standing by differences in the standings last recorded vs current

	}
}
