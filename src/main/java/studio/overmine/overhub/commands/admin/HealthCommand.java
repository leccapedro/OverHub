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

public class HealthCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player target;
        
        if (args.length > 0) {
            if (!sender.hasPermission("overhub.command.heal.others")) {
                ChatUtil.sendMessage(sender, "&cYou don't have permission to heal other players.");
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

        double maxHealth = 20.0;
        if (target.getHealth() >= maxHealth) {
            if (LanguageResource.HEALTH_MESSAGE_FULL != null) {
                ChatUtil.sendMessage(target, LanguageResource.HEALTH_MESSAGE_FULL);
                if (!target.equals(sender)) {
                    ChatUtil.sendMessage(sender, LanguageResource.HEALTH_MESSAGE_FULL);
                }
            }
            return true;
        }

        target.setHealth(maxHealth);
        target.setFoodLevel(20);

        if (target.equals(sender)) {
            if (LanguageResource.HEALTH_MESSAGE_SUCCESSFULLY != null) {
                ChatUtil.sendMessage(target, LanguageResource.HEALTH_MESSAGE_SUCCESSFULLY);
            }
        } else {
            if (LanguageResource.HEALTH_MESSAGE_PLAYER_SUCCESSFULLY != null) {
                String messageToSender = LanguageResource.HEALTH_MESSAGE_PLAYER_SUCCESSFULLY
                        .replace("{name}", target.getName());
                ChatUtil.sendMessage(sender, messageToSender);
            }

            if (LanguageResource.HEALTH_MESSAGE_OTHER_SUCCESSFULLY != null) {
                String messageToTarget = LanguageResource.HEALTH_MESSAGE_OTHER_SUCCESSFULLY
                        .replace("{name}", sender.getName());
                ChatUtil.sendMessage(target, messageToTarget);
            }
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("overhub.command.heal.others")) {
            return sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

