package studio.overmine.overhub.commands;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.SpawnController;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.BaseCommand;
import studio.overmine.overhub.utilities.command.Command;
import studio.overmine.overhub.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand {

	private final SpawnController spawnController;

	public SetSpawnCommand(OverHub plugin) {
		this.spawnController = plugin.getSpawnController();
	}

	@Command(name = "setspawn", permission = "overhub.command.setspawn")
	@Override
	public void onCommand(CommandArgs command) {
		Player player = command.getPlayer();

		spawnController.setSpawnLocation(player.getLocation());
		ChatUtil.sendMessage(player, "&aSpawn location has been set.");
	}
}
