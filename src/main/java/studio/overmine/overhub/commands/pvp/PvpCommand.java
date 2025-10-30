package studio.overmine.overhub.commands.pvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.commands.pvp.subcommands.PvpSetInventoryCommand;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;
import studio.overmine.overhub.utilities.command.SubCommandHelper;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PvpCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands;

    public PvpCommand(OverHub plugin) {
        SubCommand setInventoryCommand = new PvpSetInventoryCommand(plugin);
        this.subCommands = SubCommandHelper.of(
                new AbstractMap.SimpleEntry<>("setinventory", setInventoryCommand),
                new AbstractMap.SimpleEntry<>("setinv", setInventoryCommand),
                new AbstractMap.SimpleEntry<>("save", setInventoryCommand)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            ChatUtil.sendMessage(sender, SubCommandHelper.getSubCommandFormat(label, subCommands, "PvP Commands"));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        SubCommand subCommandModel = subCommands.get(subCommand);

        if (subCommandModel == null) {
            ChatUtil.sendMessage(sender, "&cUnknown '" + subCommand + "' command. Use /" + label + " for help.");
            return true;
        }

        if (!subCommandModel.hasPermission(sender)) {
            ChatUtil.sendMessage(sender, "&cYou do not have permission to use this command.");
            return true;
        }

        if (subCommandModel.isPlayerOnly() && !(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, "&cYou must be a player to use this command.");
            return true;
        }

        subCommandModel.execute(sender, label, args);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(key -> key.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
