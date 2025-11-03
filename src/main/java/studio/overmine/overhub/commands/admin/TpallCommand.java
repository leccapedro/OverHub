package studio.overmine.overhub.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.utilities.ChatUtil;

public class TpallCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, "&cYou must be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;
        
        for (Player target : sender.getServer().getOnlinePlayers()) {
            if (!target.equals(player)) {
                target.teleport(player.getLocation());
                String message = LanguageResource.TPALL_MESSAGE_PLAYERS
                        .replace("{player}", player.getName());
                ChatUtil.sendMessage(target, message);
            }
        }

        ChatUtil.sendMessage(player, LanguageResource.TPALL_MESSAGE_TPALL);
        return true;
    }
}

