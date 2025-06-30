package studio.overmine.overhub.utilities;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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
        Material materialType = Material.matchMaterial(material);

        if (materialType == null) {
            this.itemStack = new ItemStack(Material.STONE);
            this.itemMeta = itemStack.getItemMeta();

            Bukkit.getLogger().severe("ERROR - INVALID MATERIAL: " + material);
            return;
        }

        this.itemStack = new ItemStack(materialType);
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
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", texture));

            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            }
            catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                throw new RuntimeException("Error setting skull skin - ", e);
            }
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

    public ItemBuilder addLoreLine(String line) {
        this.itemMeta.getLore().add(ChatUtil.translate(line));
        return this;
    }

    public ItemBuilder setEnchanted(boolean enchanted) {
        if (enchanted) {
            this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder addEnchantment() {
        this.itemStack.addEnchantment(Enchantment.DURABILITY, 1);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemStack.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
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
