package studio.overmine.overhub.controllers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import studio.overmine.overhub.models.resources.types.CombatSwordResource;
import studio.overmine.overhub.utilities.ChatUtil;
import studio.overmine.overhub.utilities.item.ItemBuilder;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class CombatController {
    private final HashMap<UUID, BukkitTask> equipTasks;
    private final HashMap<UUID, Boolean> playersPvP;

    public CombatController() {
        this.equipTasks = new HashMap<>();
        this.playersPvP = new HashMap<>();
    }

    public void equipPlayer(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.setHelmet(buildItem(CombatSwordResource.ARMOR_HELMET));
        inventory.setChestplate(buildItem(CombatSwordResource.ARMOR_CHESTPLATE));
        inventory.setLeggings(buildItem(CombatSwordResource.ARMOR_LEGGINS));
        inventory.setBoots(buildItem(CombatSwordResource.ARMOR_BOOTS));

        inventory.setItem(inventory.getHeldItemSlot(), buildItem(CombatSwordResource.ITEM_SWORD));
    }

    public ItemStack buildItem(ConfigurationSection section) {
        if (section == null) {
            return new ItemStack(Material.AIR);
        }

        ItemBuilder builder = new ItemBuilder(Material.valueOf(section.getString("material")));
        if (section.contains("display-name")) {
            builder.setDisplayName(section.getString("display-name"));
        }
        if (section.contains("lore")) {
            builder.setLore(section.getStringList("lore"));
        }
        if (section.contains("enchants")) {
            ConfigurationSection enchants = section.getConfigurationSection("enchants");
            if (enchants != null) {
                for (String key : enchants.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
                    if (enchantment != null) {
                        builder.addEnchantment(enchantment, enchants.getInt(key));
                    }
                }
            }
        }
        return builder.build();
    }
}
