package studio.overmine.overhub.utilities;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.overmine.overhub.integrations.PlaceholderAPIHook;
import me.clip.placeholderapi.PlaceholderAPI;

@UtilityClass
public class ChatUtil {

    private final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    public String translate(String text) {
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(text);
        StringBuffer buffer = new StringBuffer(text.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public List<String> translate(List<String> list) {
        return list.stream().map(ChatUtil::translate).collect(Collectors.toList());
    }

    public String placeholder(Player player, String text) {
        return translate(PlaceholderAPIHook.enabled ? PlaceholderAPI.setPlaceholders(player, text) : text);
    }

    public List<String> placeholder(Player player, List<String> text) {
        return translate(PlaceholderAPIHook.enabled ? PlaceholderAPI.setPlaceholders(player, text) : text);
    }

    public void sendPlaceholderMessage(Player player, String text) {
        player.sendMessage(placeholder(player, text));
    }

    public void sendMessage(CommandSender sender, String text) {
        sender.sendMessage(translate(text));
    }
}
