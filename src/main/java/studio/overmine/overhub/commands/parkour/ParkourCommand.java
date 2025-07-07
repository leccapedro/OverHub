package studio.overmine.overhub.commands.parkour;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.overmine.overhub.commands.parkour.subcommands.ParkourSetAreaCommand;
import studio.overmine.overhub.commands.parkour.subcommands.ParkourStartCommand;
import studio.overmine.overhub.commands.parkour.subcommands.ParkourWandCommand;
import studio.overmine.overhub.controllers.ParkourController;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.command.SubCommand;
import studio.overmine.overhub.utilities.command.SubCommandHelper;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParkourCommand implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands;

    public ParkourCommand(ParkourController parkourController) {
        this.subCommands = SubCommandHelper.of(
                new AbstractMap.SimpleEntry<>("setarea", new ParkourSetAreaCommand(parkourController)),
                new AbstractMap.SimpleEntry<>("wand", new ParkourWandCommand()),
                new AbstractMap.SimpleEntry<>("start", new ParkourStartCommand(parkourController))
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            ChatUtil.sendMessage(sender, SubCommandHelper.getSubCommandFormat(label, subCommands, "Parkour Commands"));
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
