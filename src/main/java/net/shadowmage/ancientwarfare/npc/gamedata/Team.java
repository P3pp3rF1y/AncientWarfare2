package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.npc.faction.FactionEntry;
import net.shadowmage.ancientwarfare.npc.faction.PlayerFactionEntry;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.HashSet;
import java.util.Set;

public class Team {
	private final ResourceLocation teamName;
	private Set<String> playerMembers = new HashSet<>();

	public FactionEntry getFactionStandings() {
		return factionStandings;
	}

	public void setFactionStandings(FactionEntry factionStandings) {
		this.factionStandings = factionStandings;
	}

	private FactionEntry factionStandings = new FactionEntry();

	public Team(ResourceLocation teamName) {
		this.teamName = teamName;
	}

	public void addMember(String playerName) {
		if (!playerMembers.contains(playerName)) {
			playerMembers.add(playerName);
		}
	}

	public void updateFactionStandings(FactionData factionData, String playerName) {
		PlayerFactionEntry playerEntry = factionData.getEntryFor(playerName);
		for (String factionName : FactionRegistry.getFactionNames()) {
			updateFactionStanding(factionName, playerEntry, factionData);
		}
	}

	private void updateFactionStanding(String factionName, PlayerFactionEntry playerEntry, FactionData factionData) {
		int newPlayerStanding = playerEntry.getStandingFor(factionName);
		int newTeamStanding;
		if (playerMembers.size() == 1) {
			newTeamStanding = newPlayerStanding;
		} else {
			int currentTeamStanding = factionStandings.getStandingFor(factionName);
			newTeamStanding = (currentTeamStanding * (playerMembers.size() - 1) + newPlayerStanding) / playerMembers.size();
			updateMembersStanding(factionName, factionData, newTeamStanding - currentTeamStanding, playerEntry.playerName);
		}
		factionStandings.setStandingFor(factionName, newTeamStanding);
	}

	private void updateMembersStanding(String factionName, FactionData factionData, int teamStandingChange, String playerToExclude) {
		for (String playerName : playerMembers) {
			if (!playerName.equals(playerToExclude)) {
				int currentPlayerStanding = factionData.getEntryFor(playerName).getStandingFor(factionName);
				factionData.setStandingFor(playerName, factionName, currentPlayerStanding + (teamStandingChange / playerMembers.size()));

			}
		}
	}

	public void removeMember(String playerName) {
		playerMembers.remove(playerName);
	}

	public boolean isMember(String playerName) {
		return playerMembers.contains(playerName);
	}

	public int getFactionStanding(String factionName) {
		return factionStandings.getStandingFor(factionName);
	}

	public ResourceLocation getName() {
		return teamName;
	}

	public void adjustStandingFor(World world, FactionData factionData, String factionName, int standingChange, String playerToExclude) {
		factionStandings.adjustStandingFor(factionName, standingChange);
		if (playerMembers.size() > 1) {
			int individualMemberSplit = standingChange / playerMembers.size();
			playerMembers.forEach(playerName -> {
				if (!playerName.equals(playerToExclude)) {
					factionData.adjustStandingFor(world, playerName, factionName, individualMemberSplit);
				}
			});
		}
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();

		ret.setString("teamName", teamName.toString());
		ret.setTag("playerMembers", NBTHelper.getNBTStringList(playerMembers));
		ret.setTag("factionStandings", factionStandings.writeToNBT(new NBTTagCompound()));

		return ret;
	}

	public static Team deserializeNBT(NBTTagCompound nbt) {
		ResourceLocation teamName = new ResourceLocation(nbt.getString("teamName"));
		Team team = new Team(teamName);
		team.playerMembers = NBTHelper.getStringSet(nbt.getTagList("playerMembers", Constants.NBT.TAG_STRING));
		team.factionStandings = new FactionEntry(nbt.getCompoundTag("factionStandings"));
		return team;
	}

	public Set<String> getMembers() {
		return playerMembers;
	}

	public void setFactionStanding(String factionName, int standing) {
		factionStandings.setStandingFor(factionName, standing);
	}
}
