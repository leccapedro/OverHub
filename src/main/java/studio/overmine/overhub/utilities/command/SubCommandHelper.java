package studio.overmine.overhub.utilities.command;

import lombok.experimental.UtilityClass;
import studio.overmine.overhub.utilities.ChatUtil;

import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class SubCommandHelper {

    public static String build(String template, Map<String, String> replacements) {
        String result = template;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static String getSubCommandFormat(String label, Map<String, SubCommand> subCommands, String title) {
        StringBuilder subcommandsFormatted = new StringBuilder();
        for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            Map<String, String> placeholders = new LinkedHashMap<>();
            placeholders.put("%label%", label);
            placeholders.put("%subcommand%", entry.getKey() + entry.getValue().getParametersFormatted());
            placeholders.put("%description%", entry.getValue().getDescription());

            String line = build(
                    " &7● &f/%label% %subcommand% &8- &e%description%",
                    placeholders
            );
            subcommandsFormatted.append(line).append("\n");
        }

        String format =
                ChatUtil.NORMAL_LINE + "\n" +
                        "&l&6%title%\n" +
                        "\n" +
                        "&f<> &7= &fRequired &7| &f[] &7= &fOptional\n" +
                        "\n" +
                        "%subcommands%" +
                        ChatUtil.NORMAL_LINE;

        return format
                .replace("%title%", title)
                .replace("%subcommands%", subcommandsFormatted.toString())
                .replace("%line%", ChatUtil.NORMAL_LINE);
    }

    @SafeVarargs
    public <K, V> LinkedHashMap<K, V> of(Map.Entry<K, V>... entries) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries)
            map.put(entry.getKey(), entry.getValue());
        return map;
    }
}