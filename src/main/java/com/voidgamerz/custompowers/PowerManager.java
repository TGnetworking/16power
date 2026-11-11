package com.voidgamerz.custompowers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PowerManager {

    private final NamespacedKey powerKey;

    public PowerManager(CustomPowers plugin) {
        this.powerKey = new NamespacedKey(plugin, "power_id");
    }

    public NamespacedKey getPowerKey() {
        return powerKey;
    }

    /** Builds the finished, glowing, lore-tagged item for a power. */
    public ItemStack createItem(PowerType type) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(type.getColor() + "" + ChatColor.BOLD + type.getDisplayName());

        List<String> lore = new ArrayList<>();
        for (String line : type.getLore()) {
            lore.add(ChatColor.GRAY + line);
        }
        lore.add("");
        if (type.isGodPower()) {
            lore.add(ChatColor.YELLOW + "Wear it. Become it.");
        } else if (type.getCooldownSeconds() > 0) {
            lore.add(ChatColor.DARK_GRAY + "Right-click to unleash  |  " + type.getCooldownSeconds() + "s cooldown");
        } else {
            lore.add(ChatColor.DARK_GRAY + "Passive — triggers in combat");
        }
        meta.setLore(lore);

        // Fake-enchant glow without an actual enchant effect
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        meta.getPersistentDataContainer().set(powerKey, PersistentDataType.STRING, type.getId());

        int cmd = customModelDataFor(type);
        if (cmd != 0) {
            meta.setCustomModelData(cmd);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Matches the "custom_model_data" predicates in the resource pack's
     * models/item/*.json overrides. 0 means "no custom model — use the
     * item's normal vanilla look" (used for Trident, Wither Skeleton Skull
     * and Totem of Undying, which already have their own built-in 3D
     * models and don't need a resource-pack override).
     */
    private int customModelDataFor(PowerType type) {
        return switch (type) {
            case CROWN -> 90001;
            case EMBER_CORE -> 90002;
            case UMBRAL_STEP -> 90003;
            case DRAKONS_MAW -> 90004;
            case GLACIAL_GRASP -> 90005;
            case GRAVITUM_PULL -> 90006;
            case PHANTOM_WINGS -> 90007;
            case CRIMSON_THIRST -> 90008;
            case SEISMIC_FIST -> 90009;
            case VENOMOUS_BITE -> 90010;
            case MIRROR_WALKER -> 90011;
            case VOID_RIPPLE -> 90012;
            case SOUL_SHACKLE -> 90013;
            default -> 0; // REAPERS_MARK, STORMCALLER, BERSERKERS_WRATH keep their vanilla look
        };
    }

    /** Returns which power this item is, or null if it isn't a tagged power item. */
    public PowerType readPower(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        String id = meta.getPersistentDataContainer().get(powerKey, PersistentDataType.STRING);
        if (id == null) return null;
        return PowerType.fromId(id);
    }
}
