package studio.overmine.overhub.utilities.item;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import studio.overmine.overhub.utilities.ChatUtil;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(material, 1, 0);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, 0);
    }

    public ItemBuilder(String material) {
        XMaterial xmaterial = XMaterial.matchXMaterial(material)
                .orElse(null);

        if (xmaterial == null) {
            this.itemStack = new ItemStack(Material.BARRIER);
            this.itemMeta = itemStack.getItemMeta();

            Bukkit.getLogger().severe("ERROR - INVALID MATERIAL: " + material);
            return;
        }

        this.itemStack = new ItemStack(xmaterial.get(), 1, xmaterial.getData());
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int amount, int data) {
        this.itemStack = new ItemStack(material, amount, (short) data);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setData(int data) {
        this.itemStack.setDurability((short) data);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder addAmount(int amount) {
        this.itemStack.setAmount(this.itemStack.getAmount() + amount);
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        this.itemMeta.setDisplayName(ChatUtil.translate(name));
        return this;
    }

    public ItemBuilder setDisplayName(Player player, String name) {
        this.itemMeta.setDisplayName(ChatUtil.placeholder(player, name));
        return this;
    }

    public ItemBuilder setSkullOwner(Player player, String texture) {
        if (texture == null || texture.isEmpty()) return this;

        if (!(this.itemMeta instanceof SkullMeta)) {
            throw new IllegalArgumentException("setSkullOwner() only applicable for Skull");
        }

        SkullMeta meta = (SkullMeta) this.itemMeta;

        if (isBase64(texture)) {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            try {
                textures.setSkin(new URL(texture));
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL for skin texture: " + texture, e);
            }

            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
        }
        else {
            String owner = player != null ? ChatUtil.placeholder(player, texture) : texture;
            meta.setOwner(owner);
        }
        return this;
    }

    public ItemBuilder setSkullOwner(String ownerOrValue) {
        return setSkullOwner(null, ownerOrValue);
    }

    public boolean isBase64(String value) {
        return value.startsWith("eyJ") || value.startsWith("http") || value.startsWith("https");
    }

    public boolean isSkullOwner() {
        return this.itemMeta instanceof SkullMeta;
    }

    public ItemBuilder setArmorColor(Color color) {
        if (color == null) return this;

        if (!(this.itemMeta instanceof LeatherArmorMeta)) {
            throw new IllegalArgumentException("setArmorColor() only applicable for LeatherArmor");
        }

        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemMeta;
        leatherArmorMeta.setColor(color);
        itemStack.setItemMeta(leatherArmorMeta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore == null || lore.isEmpty()) return this;
        this.itemMeta.setLore(ChatUtil.translate(lore));
        return this;
    }

    public ItemBuilder setLore(Player player, List<String> lore) {
        if (lore == null || lore.isEmpty()) return this;
        this.itemMeta.setLore(ChatUtil.placeholder(player, lore));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        if (lore == null) return this;
        this.itemMeta.setLore(ChatUtil.translate(Arrays.asList(lore)));
        return this;
    }

    public ItemBuilder setLore(Player player, String... lore) {
        if (lore == null) return this;
        this.itemMeta.setLore(ChatUtil.placeholder(player, Arrays.asList(lore)));
        return this;
    }

    public ItemBuilder setEnchanted(boolean enchanted) {
        if (enchanted) {
            this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder addEnchantments(ConfigurationSection section) {
        if (section != null) {
            section.getKeys(false).forEach(enchantId -> {
                Enchantment enchantment = Enchantment.getByName(enchantId.toUpperCase());

                if (enchantment == null) {
                    Bukkit.getLogger().warning("Enchantment " + enchantId + " not found in config file.");
                    return;
                }

                this.itemMeta.addEnchant(enchantment, section.getInt(enchantId), true);
            });
        }
        return this;
    }

    public ItemBuilder addUnbreakable() {
        this.itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemStack build() {
        for (ItemFlag itemFlag : ItemFlag.values()) {
            this.itemMeta.addItemFlags(itemFlag);
        }

        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
