package studio.overmine.overhub.utilities.menu.decoration;

import studio.overmine.overhub.utilities.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.function.BiConsumer;

@UtilityClass
public class DecorationUtil {

    public List<Integer> getDecorationSlots(String data) {
        if (data == null || data.isEmpty()) return Collections.emptyList();

        List<Integer> slots = new ArrayList<>();
        String[] splittedData = data.split(";");

        for (String part : splittedData) {
            if (part.contains("-")) {
                String[] rangeParts = part.split("-");

                if (rangeParts.length == 2) {
                    try {
                        int start = Integer.parseInt(rangeParts[0].trim());
                        int end = Integer.parseInt(rangeParts[1].trim());

                        if (start <= end) {
                            for (int i = start; i <= end; i++) {
                                slots.add(i);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid range format: " + part);
                    }
                }
            }
            else {
                try {
                    slots.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format: " + part);
                }
            }
        }

        return slots;
    }

    public void processDecorationKeys(ConfigurationSection section, BiConsumer<String, Integer> action) {
        for (String key : section.getKeys(false)) {
            for (Integer slot : DecorationUtil.getDecorationSlots(key)) {
                action.accept(key, slot);
            }
        }
    }

    public void registerDecorations(ConfigurationSection section, Set<Decoration> decorations) {
        if (section != null) {
            DecorationUtil.processDecorationKeys(section, (key, slot) ->
                    DecorationUtil.buildDecoration(decorations, section, key, slot)
            );
        }
    }

    public void buildDecoration(
            Set<Decoration> decorations,
            ConfigurationSection section,
            String key,
            int slot) {
        Decoration decoration = new Decoration();
        decoration.setSlot(slot);
        decoration.setItemStack(new ItemBuilder(Objects.requireNonNull(section.getString(key + ".material")))
                .setSkullOwner(section.getString(key + ".head"))
                .setData(section.getInt(key + ".data"))
                .setDisplayName(section.getString(key + ".name"))
                .setLore(section.getStringList(key + ".description"))
                .setEnchanted(section.getBoolean(key + ".enchanted"))
                .build());

        decorations.add(decoration);
    }
}
