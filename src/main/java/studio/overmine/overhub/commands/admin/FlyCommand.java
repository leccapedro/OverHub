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

public class FlyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player target;
        
        if (args.length > 0) {
            if (!sender.hasPermission("overhub.command.fly.others")) {
                ChatUtil.sendMessage(sender, "&cYou don't have permission to change fly mode for other players.");
                return true;
            }
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                ChatUtil.sendMessage(sender, "&cPlayer not found.");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                ChatUtil.sendMessage(sender, "&cYou must specify a player when using this command from console.");
                return true;
            }
            target = (Player) sender;
        }

        boolean newFlyState = !target.getAllowFlight();
        target.setAllowFlight(newFlyState);
        if (newFlyState) {
            target.setFlying(true);
        }

        if (target.equals(sender)) {
            String message = newFlyState ? LanguageResource.FLY_MESSAGE_ENABLED : LanguageResource.FLY_MESSAGE_DISABLED;
            ChatUtil.sendMessage(target, message);
        } else {
            String messageToSender = newFlyState 
                    ? LanguageResource.FLY_MESSAGE_ENABLED_OTHER 
                    : LanguageResource.FLY_MESSAGE_DISABLED_OTHER;
            messageToSender = messageToSender.replace("{player}", target.getName());
            ChatUtil.sendMessage(sender, messageToSender);

            String messageToTarget = newFlyState 
                    ? LanguageResource.FLY_MESSAGE_ENABLED_PLAYER 
                    : LanguageResource.FLY_MESSAGE_DISABLED_PLAYER;
            messageToTarget = messageToTarget.replace("{player}", sender.getName());
            ChatUtil.sendMessage(target, messageToTarget);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("overhub.command.fly.others")) {
            return sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

