package com.voidgamerz.custompowers;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.List;

/**
 * All 16 powers. The first 15 are unique, independently-triggered relics.
 * CROWN is the 16th, god-tier power — only works while worn as a helmet.
 */
public enum PowerType {

    EMBER_CORE(
            "ember_core", "Ember Core", Material.BLAZE_POWDER, ChatColor.GOLD,
            List.of("Right-click to erupt in a fire pulse,", "igniting every enemy nearby."),
            12),

    DRAKONS_MAW(
            "drakons_maw", "Drakon's Maw", Material.DRAGON_BREATH, ChatColor.LIGHT_PURPLE,
            List.of("Right-click to breathe a cone of", "draconic venom at your enemies."),
            15),

    UMBRAL_STEP(
            "umbral_step", "Umbral Step", Material.ENDER_PEARL, ChatColor.DARK_GRAY,
            List.of("Right-click to vanish into shadow", "and reappear behind your nearest foe."),
            10),

    REAPERS_MARK(
            "reapers_mark", "Reaper's Mark", Material.WITHER_SKELETON_SKULL, ChatColor.DARK_RED,
            List.of("A marked strike carries a chance to", "end the fight in a single hit."),
            0), // passive, on-hit chance — no cooldown needed, luck-gated

    STORMCALLER(
            "stormcaller", "Stormcaller", Material.TRIDENT, ChatColor.AQUA,
            List.of("Every strike has a chance to call", "lightning down on your target."),
            0), // passive, on-hit chance

    GLACIAL_GRASP(
            "glacial_grasp", "Glacial Grasp", Material.BLUE_ICE, ChatColor.BLUE,
            List.of("Right-click to freeze the nearest", "enemy solid and shatter their armor."),
            14),

    GRAVITUM_PULL(
            "gravitum_pull", "Gravitum Pull", Material.ENDER_EYE, ChatColor.DARK_PURPLE,
            List.of("Right-click to collapse space around", "you, yanking every nearby enemy in."),
            14),

    PHANTOM_WINGS(
            "phantom_wings", "Phantom Wings", Material.PHANTOM_MEMBRANE, ChatColor.GRAY,
            List.of("Right-click to burst into flight,", "soaring free of danger for a few seconds."),
            20),

    CRIMSON_THIRST(
            "crimson_thirst", "Crimson Thirst", Material.REDSTONE, ChatColor.RED,
            List.of("Every strike drains your enemy's", "life force straight into your own."),
            0), // passive lifesteal on every hit

    SEISMIC_FIST(
            "seismic_fist", "Seismic Fist", Material.NETHERITE_INGOT, ChatColor.DARK_GRAY,
            List.of("Right-click to slam the ground,", "launching every enemy around you skyward."),
            14),

    VENOMOUS_BITE(
            "venomous_bite", "Venomous Bite", Material.SPIDER_EYE, ChatColor.DARK_GREEN,
            List.of("Every strike injects a toxin that", "withers your enemy over time."),
            0), // passive on-hit

    MIRROR_WALKER(
            "mirror_walker", "Mirror Walker", Material.AMETHYST_SHARD, ChatColor.LIGHT_PURPLE,
            List.of("Right-click to split into decoys", "that confuse and distract your enemies."),
            20),

    VOID_RIPPLE(
            "void_ripple", "Void Ripple", Material.GUNPOWDER, ChatColor.BLACK,
            List.of("Right-click to unleash a shockwave,", "blinding and scattering everyone nearby."),
            14),

    BERSERKERS_WRATH(
            "berserkers_wrath", "Berserker's Wrath", Material.TOTEM_OF_UNDYING, ChatColor.YELLOW,
            List.of("When your health runs low, this relic", "erupts — flooding you with rage and speed."),
            0), // passive, triggers on low HP

    SOUL_SHACKLE(
            "soul_shackle", "Soul Shackle", Material.CHAIN, ChatColor.GRAY,
            List.of("Right-click to bind the nearest enemy's", "soul, rooting them in place."),
            18),

    CROWN(
            "crown", "Crown of the Void King", Material.GOLDEN_HELMET, ChatColor.GOLD,
            List.of(
                    ChatColor.BOLD + "" + ChatColor.GOLD + "THE GOD POWER",
                    "Worn, not wielded. The one who bears it",
                    "commands every power the other 15 hold.",
                    "Fireproof. Nearly unkillable. Absolute."),
            25); // interval between automatic "Royal Decree" bursts

    private final String id;
    private final String displayName;
    private final Material material;
    private final ChatColor color;
    private final List<String> lore;
    private final int cooldownSeconds;

    PowerType(String id, String displayName, Material material, ChatColor color,
              List<String> lore, int cooldownSeconds) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.color = color;
        this.lore = lore;
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Material getMaterial() { return material; }
    public ChatColor getColor() { return color; }
    public List<String> getLore() { return lore; }
    public int getCooldownSeconds() { return cooldownSeconds; }
    public boolean isGodPower() { return this == CROWN; }

    public static PowerType fromId(String id) {
        for (PowerType p : values()) {
            if (p.id.equalsIgnoreCase(id)) return p;
        }
        return null;
    }
}
