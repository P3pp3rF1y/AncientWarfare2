package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.shadowmage.ancientwarfare.core.command.ParentCommand;
import net.shadowmage.ancientwarfare.core.command.SimpleSubCommand;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.npc.gamedata.Team;
import net.shadowmage.ancientwarfare.npc.gamedata.TeamData;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CommandTeams extends ParentCommand {
	public CommandTeams() {
		registerSubCommand(new SimpleSubCommand("list", (server, sender, args) -> {
			for (Team team : AWGameData.INSTANCE.getData(sender.getEntityWorld(), TeamData.class).getTeams()) {
				sender.sendMessage(new TextComponentString(team.getName().toString()));
			}
		}));
		registerSubCommand(new SimpleSubCommand("describe", (server, sender, args) -> {
			if (args.length == 0) {
				sender.sendMessage(new TextComponentTranslation(CommandTeams.this.getUsage(sender)));
				return;
			}
			Optional<Team> t = AWGameData.INSTANCE.getData(sender.getEntityWorld(), TeamData.class).getTeam(new ResourceLocation(args[0]));

			if (t.isPresent()) {
				Team team = t.get();
				sender.sendMessage(new TextComponentTranslation("command.aw.teams.team_members", StringTools.joinElements(", ", team.getMembers())));
				outputTeamStandings(sender, team);
			} else {
				sender.sendMessage(new TextComponentTranslation("command.aw.teams.team_does_not_exist"));
			}
		}) {
			@Override
			public int getMaxArgs() {
				return 1;
			}
		});
		registerSubCommand(new SimpleSubCommand("player", (server, sender, args) -> {
			sender.sendMessage(new TextComponentTranslation("command.aw.teams.member_of"));
			String playerName = args.length == 0 ? sender.getName() : args[0];
			for (Team team : AWGameData.INSTANCE.getData(sender.getEntityWorld(), TeamData.class).getPlayerTeams(playerName)) {
				sender.sendMessage(new TextComponentTranslation("command.aw.teams.team_name", team.getName().toString()));
				outputTeamStandings(sender, team);
			}
		}) {
			@Override
			public int getMaxArgs() {
				return 1;
			}
		});
	}

	private void outputTeamStandings(ICommandSender sender, Team team) {
		sender.sendMessage(new TextComponentTranslation("command.aw.teams.standings"));
		for (Map.Entry<String, Integer> standing : StreamSupport.stream(team.getFactionStandings().spliterator(), false)
				.sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())) {
			sender.sendMessage(new TextComponentString(standing.getKey() + ": " + standing.getValue()));
		}
	}

	@Override
	public String getName() {
		return "awteams";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.aw.teams.usage";
	}
}
