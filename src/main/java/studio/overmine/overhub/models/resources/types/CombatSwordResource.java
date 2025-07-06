package studio.overmine.overhub.models.resources.types;

import org.bukkit.configuration.ConfigurationSection;
import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.utilities.FileConfig;

public class CombatSwordResource extends Resource {
    public static ConfigurationSection ARMOR_HELMET, ARMOR_CHESTPLATE, ARMOR_LEGGINS,
            ARMOR_BOOTS, ITEM_SWORD;
    public static int EQUIP_DELAY;

    public CombatSwordResource(OverHub plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        FileConfig combatSwordFile = plugin.getFileConfig("combat-sword-config");

        ARMOR_HELMET = combatSwordFile.getConfiguration().getConfigurationSection("armor.helmet");
        ARMOR_CHESTPLATE = combatSwordFile.getConfiguration().getConfigurationSection("armor.chestplate");
        ARMOR_LEGGINS = combatSwordFile.getConfiguration().getConfigurationSection("armor.leggings");
        ARMOR_BOOTS = combatSwordFile.getConfiguration().getConfigurationSection("armor.boots");
        ITEM_SWORD = combatSwordFile.getConfiguration().getConfigurationSection("sword");
        EQUIP_DELAY = combatSwordFile.getInt("equip-delay", 5);

    }
}
