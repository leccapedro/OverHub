package studio.overmine.overhub.commands.spawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import studio.overmine.overhub.controllers.SpawnController;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

	private final SpawnController spawnController;

	public SetSpawnCommand(SpawnController spawnController) {
		this.spawnController = spawnController;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			ChatUtil.sendMessage(sender, "&cYou must be a player to use this command.");
			return true;
		}

		Player player = (Player) sender;

		spawnController.setSpawnLocation(player.getLocation());
		ChatUtil.sendMessage(player, LanguageResource.SPAWN_MESSAGE_SET);
		return false;
	}
}
