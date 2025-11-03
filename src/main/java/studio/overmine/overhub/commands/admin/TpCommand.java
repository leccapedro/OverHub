package studio.overmine.overhub.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TpCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, "&cYou must be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            ChatUtil.sendMessage(player, "&cUsage: /" + label + " <player>");
            return true;
        }

        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            ChatUtil.sendMessage(player, "&cPlayer not found.");
            return true;
        }

        player.teleport(target.getLocation());
        ChatUtil.sendMessage(player, LanguageResource.TP_MESSAGE_TP_MESSAGE);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

