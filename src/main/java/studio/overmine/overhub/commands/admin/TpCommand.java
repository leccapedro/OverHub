package studio.overmine.overhub.commands.admin;

import org.bukkit.Location;
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
            ChatUtil.sendMessage(player, "&cUsage: /" + label + " <player> or /" + label + " <player1> <player2> or /" + label + " <x> <y> <z>");
            return true;
        }

        // Si hay 2 argumentos, intentar teletransportar un jugador a otro
        if (args.length == 2) {
            Player playerToTeleport = sender.getServer().getPlayer(args[0]);
            Player targetPlayer = sender.getServer().getPlayer(args[1]);
            
            if (playerToTeleport != null && targetPlayer != null) {
                playerToTeleport.teleport(targetPlayer.getLocation());
                String message = LanguageResource.TP_MESSAGE_TP_PLAYER_TO_PLAYER
                        .replace("{player1}", playerToTeleport.getName())
                        .replace("{player2}", targetPlayer.getName());
                ChatUtil.sendMessage(player, message);
                return true;
            }
            
            // Si no son dos jugadores válidos, mostrar error
            if (playerToTeleport == null) {
                ChatUtil.sendMessage(player, "&cPlayer '" + args[0] + "' not found.");
                return true;
            }
            if (targetPlayer == null) {
                ChatUtil.sendMessage(player, "&cPlayer '" + args[1] + "' not found.");
                return true;
            }
        }

        // Si hay 3 argumentos, intentar parsearlos como coordenadas
        if (args.length == 3) {
            try {
                double x = parseCoordinate(args[0], player.getLocation().getX());
                double y = parseCoordinate(args[1], player.getLocation().getY());
                double z = parseCoordinate(args[2], player.getLocation().getZ());

                Location targetLocation = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(targetLocation);
                String message = LanguageResource.TP_MESSAGE_TP_COORDINATES
                        .replace("{x}", String.valueOf((int) x))
                        .replace("{y}", String.valueOf((int) y))
                        .replace("{z}", String.valueOf((int) z));
                ChatUtil.sendMessage(player, message);
                return true;
            } catch (NumberFormatException e) {
                // Si no son números, continuar con la búsqueda de jugador
            }
        }

        // Si hay 1 argumento, intentar encontrar un jugador y teletransportarse a él
        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null) {
            ChatUtil.sendMessage(player, "&cPlayer not found or invalid coordinates.");
            return true;
        }

        player.teleport(target.getLocation());
        ChatUtil.sendMessage(player, LanguageResource.TP_MESSAGE_TP_MESSAGE);
        return true;
    }

    private double parseCoordinate(String arg, double relativeBase) {
        if (arg.startsWith("~")) {
            String offsetStr = arg.substring(1);
            double offset = offsetStr.isEmpty() ? 0 : Double.parseDouble(offsetStr);
            return relativeBase + offset;
        }
        return Double.parseDouble(arg);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 || args.length == 2) {
            return sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

