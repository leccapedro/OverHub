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

public class ClearInventoryCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player target;
        
        if (args.length > 0) {
            if (!sender.hasPermission("overhub.command.clearinventory.others")) {
                ChatUtil.sendMessage(sender, "&cYou don't have permission to clear inventory of other players.");
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

        target.getInventory().clear();

        if (target.equals(sender)) {
            ChatUtil.sendMessage(target, LanguageResource.CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY);
        } else {
            String messageToSender = LanguageResource.CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY_OTHER
                    .replace("{name}", target.getName());
            ChatUtil.sendMessage(sender, messageToSender);

            String messageToTarget = LanguageResource.CLEAR_INVENTORY_MESSAGE_CLEAR_INVENTORY_PLAYER
                    .replace("{player}", sender.getName());
            ChatUtil.sendMessage(target, messageToTarget);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("overhub.command.clearinventory.others")) {
            return sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

