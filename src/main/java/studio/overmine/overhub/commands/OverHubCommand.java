package studio.overmine.overhub.commands;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.BaseCommand;
import studio.overmine.overhub.utilities.command.Command;
import studio.overmine.overhub.utilities.command.CommandArgs;
import org.bukkit.command.CommandSender;

public class OverHubCommand extends BaseCommand {

    private final OverHub plugin;

    public OverHubCommand(OverHub plugin) {
        this.plugin = plugin;
    }

    @Command(name = "overhub", permission = "overhub.command.overhub", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            ChatUtil.sendMessage(sender, "&cUsage: /overhub reload");
            return;
        }

        plugin.onReload();
        ChatUtil.sendMessage(sender, "&aOverHub has been reloaded.");
    }
}
