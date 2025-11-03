package studio.overmine.overhub.commands.admin;

import org.bukkit.GameMode;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                ChatUtil.sendMessage(sender, "&cUsage: /" + label + " <gamemode> [player]");
                return true;
            }
            ChatUtil.sendMessage(sender, "&cUsage: /" + label + " <gamemode> [player]");
            return true;
        }

        GameMode gameMode = parseGameMode(args[0]);
        if (gameMode == null) {
            ChatUtil.sendMessage(sender, "&cInvalid gamemode. Use: survival, creative, adventure, or spectator");
            return true;
        }

        Player target;
        if (args.length > 1) {
            target = sender.getServer().getPlayer(args[1]);
            if (target == null) {
                ChatUtil.sendMessage(sender, "&cPlayer not found.");
                return true;
            }

            String oldGamemode = getGameModeName(target.getGameMode());
            target.setGameMode(gameMode);
            String newGamemode = getGameModeName(gameMode);

            String messageToSender = LanguageResource.GAMEMODE_MESSAGE_GAMEMODE_CHANGE
                    .replace("{name}", sender.getName())
                    .replace("{other_gamemode}", oldGamemode)
                    .replace("{other_name}", target.getName());
            ChatUtil.sendMessage(sender, messageToSender);

            String messageToTarget = LanguageResource.GAMEMODE_MESSAGE_PLAYER_CHANGE_GAMEMODE
                    .replace("{name}", sender.getName())
                    .replace("{gamemode}", newGamemode);
            ChatUtil.sendMessage(target, messageToTarget);
        } else {
            if (!(sender instanceof Player)) {
                ChatUtil.sendMessage(sender, "&cYou must specify a player when using this command from console.");
                return true;
            }
            target = (Player) sender;
            target.setGameMode(gameMode);
            String gamemodeName = getGameModeName(gameMode);
            String message = LanguageResource.GAMEMODE_MESSAGE_GAMEMODE
                    .replace("{name}", target.getName())
                    .replace("{gamemode}", gamemodeName);
            ChatUtil.sendMessage(target, message);
        }

        return true;
    }

    private GameMode parseGameMode(String input) {
        String lower = input.toLowerCase();
        switch (lower) {
            case "0":
            case "s":
            case "survival":
                return GameMode.SURVIVAL;
            case "1":
            case "c":
            case "creative":
                return GameMode.CREATIVE;
            case "2":
            case "a":
            case "adventure":
                return GameMode.ADVENTURE;
            case "3":
            case "sp":
            case "spectator":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }

    private String getGameModeName(GameMode gameMode) {
        switch (gameMode) {
            case SURVIVAL:
                return "Survival";
            case CREATIVE:
                return "Creative";
            case ADVENTURE:
                return "Adventure";
            case SPECTATOR:
                return "Spectator";
            default:
                return gameMode.name();
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> gamemodes = Arrays.asList("survival", "creative", "adventure", "spectator", "0", "1", "2", "3", "s", "c", "a", "sp");
            return gamemodes.stream()
                    .filter(gm -> gm.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

