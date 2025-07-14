package studio.overmine.overhub.utilities.menu.decoration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import studio.overmine.overhub.utilities.item.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import studio.overmine.overhub.utilities.menu.Button;

import java.util.*;
import java.util.function.BiConsumer;

@UtilityClass
public class DecorationUtil {

    public void loadDecorations(Player player, Inventory inventory, Set<Decoration> decorations) {
        for (Decoration decoration : decorations) {
            try {
                inventory.setItem(decoration.getSlot(), decoration.getItemStack(player));
            }
            catch (Exception e) {
                Bukkit.getLogger().warning("Failed to apply decoration: " + decoration.getItemStack().getType() + " at slot " + decoration.getSlot());
            }
        }
    }

    public void loadDecorations(HashMap<Integer, Button> buttons, Set<Decoration> decorations) {
        for (Decoration decoration : decorations) {
            try {
                buttons.put(decoration.getSlot(), new DecorationButton(decoration));
            }
            catch (Exception e) {
                Bukkit.getLogger().warning("Failed to apply decoration: " + decoration.getItemStack().getType() + " at slot " + decoration.getSlot());
            }
        }
    }

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
                .setDisplayName(section.getString(key + ".name"))
                .setLore(section.getStringList(key + ".lore"))
                .setEnchanted(section.getBoolean(key + ".enchanted"))
                .setModelData(section.getInt(key + ".model-data"))
                .build());
        decoration.setCommands(section.getStringList(key + ".commands"));

        decorations.add(decoration);
    }
}
