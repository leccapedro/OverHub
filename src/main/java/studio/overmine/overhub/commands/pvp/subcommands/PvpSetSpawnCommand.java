package studio.overmine.overhub.commands.pvp.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.controllers.SpawnController;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;

public class PvpSetSpawnCommand extends SubCommand {

    private final OverHub plugin;

    public PvpSetSpawnCommand(OverHub plugin) {
        super("overhub.command.pvp.setspawn", "Set the PvP combat spawn location", true);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, "&cYou must be a player to use this command.");
            return;
        }

        Player player = (Player) sender;
        SpawnController spawnController = plugin.getSpawnController();
        if (spawnController == null) {
            ChatUtil.sendMessage(player, "&cSpawn controller is not available.");
            return;
        }

        Location location = player.getLocation();
        spawnController.setCombatSpawnLocation(location);

        ChatUtil.sendMessage(player, "&aCombat spawn location set to your current position.");
    }
}