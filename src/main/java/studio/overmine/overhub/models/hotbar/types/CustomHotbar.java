package studio.overmine.overhub.models.hotbar.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studio.overmine.overhub.models.hotbar.Hotbar;
import studio.overmine.overhub.models.hotbar.HotbarAction;
import studio.overmine.overhub.utilities.ChatUtil;

import java.util.List;

/**
 * @author Risas
 * @date 06-07-2025
 * @discord https://risas.me/discord
 */
public class CustomHotbar extends Hotbar {

    private final List<String> actions;

    public CustomHotbar(String name, List<String> actions) {
        super(name, true);
        this.actions = actions;
    }

    @Override
    public void onAction(Player player) {
        actions.forEach(command -> execute(player, command.split(" ", 2)));
    }

    public void execute(Player player, String[] data) {
        if (data.length < 2) {
            Bukkit.getLogger().warning("Invalid command format: " + String.join(" ", data));
            return;
        }

        String action = data[0]
                .replace("[", "")
                .replace("]", "");
        String value = data[1];

        try {
            switch (HotbarAction.valueOf(action.toUpperCase())) {
                case MESSAGE:
                    ChatUtil.sendMessage(player, value);
                    break;
                case CONSOLE_COMMAND:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value
                            .replace("%player%", player.getName()));
                    break;
                case PLAYER_COMMAND:
                    Bukkit.dispatchCommand(player, value
                            .replace("%player%", player.getName()));
                    break;
            }
        }
        catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Invalid action type: " + action);
        }
    }
}
