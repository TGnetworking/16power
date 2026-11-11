package com.voidgamerz.custompowers;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PowerListener implements Listener {

    private final CustomPowers plugin;
    private final PowerManager powers;

    // cooldowns[player][power] -> millis when usable again
    private final Map<UUID, Map<PowerType, Long>> cooldowns = new HashMap<>();
    // separate low-frequency timers for passive triggers (berserker, crown burst)
    private final Map<UUID, Long> berserkerCooldown = new HashMap<>();
    private final Map<UUID, Long> crownBurstCooldown = new HashMap<>();

    private static final double TAU = Math.PI * 2;

    public PowerListener(CustomPowers plugin, PowerManager powers) {
        this.plugin = plugin;
        this.powers = powers;
        startCrownHeartbeat();
    }

    // ---------------------------------------------------------- utility

    private boolean onCooldown(Player p, PowerType type) {
        Map<PowerType, Long> map = cooldowns.get(p.getUniqueId());
        if (map == null) return false;
        Long until = map.get(type);
        return until != null && until > System.currentTimeMillis();
    }

    private void setCooldown(Player p, PowerType type) {
        cooldowns.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>())
                .put(type, System.currentTimeMillis() + type.getCooldownSeconds() * 1000L);
    }

    private void sendCooldownMessage(Player p, PowerType type) {
        long until = cooldowns.get(p.getUniqueId()).get(type);
        double left = (until - System.currentTimeMillis()) / 1000.0;
        p.sendActionBar(ChatColor.RED + type.getDisplayName() + " is recharging — "
                + String.format("%.1f", left) + "s");
    }

    private List<LivingEntity> nearbyEnemies(Player source, double radius) {
        List<LivingEntity> result = new ArrayList<>();
        for (Entity e : source.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof LivingEntity le && e != source) {
                result.add(le);
            }
        }
        return result;
    }

    private LivingEntity nearestEnemy(Player source, double radius) {
        LivingEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (LivingEntity le : nearbyEnemies(source, radius)) {
            double d = le.getLocation().distanceSquared(source.getLocation());
            if (d < best) {
                best = d;
                nearest = le;
            }
        }
        return nearest;
    }

    // ---------------------------------------------------------- right-click activations

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                && e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;

        Player p = e.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();
        PowerType type = powers.readPower(hand);
        if (type == null || type.isGodPower() || type.getCooldownSeconds() == 0) return;

        if (onCooldown(p, type)) {
            sendCooldownMessage(p, type);
            e.setCancelled(true);
            return;
        }

        switch (type) {
            case EMBER_CORE -> emberCore(p);
            case DRAKONS_MAW -> drakonsMaw(p);
            case UMBRAL_STEP -> umbralStep(p);
            case GLACIAL_GRASP -> glacialGrasp(p);
            case GRAVITUM_PULL -> gravitumPull(p);
            case PHANTOM_WINGS -> phantomWings(p);
            case SEISMIC_FIST -> seismicFist(p);
            case MIRROR_WALKER -> mirrorWalker(p);
            case VOID_RIPPLE -> voidRipple(p);
            case SOUL_SHACKLE -> soulShackle(p);
            default -> { return; }
        }

        setCooldown(p, type);
        e.setCancelled(true);
    }

    private void emberCore(Player p) {
        p.getWorld().spawnParticle(Particle.FLAME, p.getLocation().add(0, 1, 0), 80, 2, 1, 2, 0.05);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5f, 0.8f);
        for (LivingEntity le : nearbyEnemies(p, 6)) {
            le.setFireTicks(160);
            le.damage(10.0, p);
        }
    }

    private void drakonsMaw(Player p) {
        Vector dir = p.getLocation().getDirection().normalize();
        Location origin = p.getEyeLocation();
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.5f, 1f);
        for (LivingEntity le : nearbyEnemies(p, 8)) {
            Vector toTarget = le.getLocation().toVector().subtract(origin.toVector()).normalize();
            if (dir.dot(toTarget) > 0.6) { // roughly within a forward cone
                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 140, 2));
                le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 1));
                le.damage(10.0, p);
                le.getWorld().spawnParticle(Particle.DRAGON_BREATH, le.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.02);
            }
        }
    }

    private void umbralStep(Player p) {
        LivingEntity target = nearestEnemy(p, 15);
        if (target == null) {
            p.sendActionBar(ChatColor.GRAY + "No target nearby to step to.");
            return;
        }
        Vector behind = target.getLocation().getDirection().normalize().multiply(-1.5);
        Location dest = target.getLocation().add(behind);
        dest.setY(target.getLocation().getY());
        dest.setDirection(target.getLocation().toVector().subtract(dest.toVector()));
        p.getWorld().spawnParticle(Particle.SMOKE, p.getLocation(), 40, 0.3, 0.5, 0.3, 0.05);
        p.teleport(dest);
        p.getWorld().spawnParticle(Particle.SMOKE, dest, 40, 0.3, 0.5, 0.3, 0.05);
        p.getWorld().playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        target.damage(12.0, p);
    }

    private void glacialGrasp(Player p) {
        LivingEntity target = nearestEnemy(p, 10);
        if (target == null) return;
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 15));
        target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 120, 3));
        target.damage(8.0, p);
        target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation().add(0, 1, 0), 60, 0.5, 1, 0.5, 0.05);
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.6f);
    }

    private void gravitumPull(Player p) {
        p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation().add(0, 1, 0), 100, 2, 1, 2, 0.1);
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.6f);
        for (LivingEntity le : nearbyEnemies(p, 10)) {
            Vector pull = p.getLocation().toVector().subtract(le.getLocation().toVector()).normalize().multiply(1.3);
            pull.setY(Math.max(pull.getY(), 0.2));
            le.setVelocity(pull);
        }
    }

    private void phantomWings(Player p) {
        p.setVelocity(p.getLocation().getDirection().multiply(1.4).setY(0.9));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
        p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 40, 0.5, 0.2, 0.5, 0.05);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1f, 1f);
    }

    private void seismicFist(Player p) {
        p.getWorld().spawnParticle(Particle.EXPLOSION, p.getLocation(), 3, 1, 0.2, 1, 0);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.3f, 0.7f);
        for (LivingEntity le : nearbyEnemies(p, 6)) {
            Vector launch = le.getLocation().toVector().subtract(p.getLocation().toVector());
            launch.setY(0);
            if (launch.lengthSquared() < 0.01) launch = new Vector(0.1, 0, 0.1);
            launch.normalize().multiply(1.1).setY(1.2);
            le.setVelocity(launch);
            le.damage(9.0, p);
        }
    }

    private void mirrorWalker(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 80, 0));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.4f);
        for (int i = 0; i < 3; i++) {
            double angle = TAU * i / 3.0;
            Location loc = p.getLocation().clone().add(Math.cos(angle) * 2, 0, Math.sin(angle) * 2);
            Zombie decoy = p.getWorld().spawn(loc, Zombie.class, z -> {
                z.setCustomName(ChatColor.LIGHT_PURPLE + p.getName());
                z.setCustomNameVisible(true);
                z.setSilent(true);
                z.setInvulnerable(true);
                z.setGlowing(true);
                z.setAI(false);
                if (z.getEquipment() != null) z.getEquipment().clear();
            });
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!decoy.isDead()) decoy.remove();
                }
            }.runTaskLater(plugin, 100L); // 5 seconds
        }
    }

    private void voidRipple(Player p) {
        p.getWorld().spawnParticle(Particle.SONIC_BOOM, p.getLocation().add(0, 1, 0), 1);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f);
        for (LivingEntity le : nearbyEnemies(p, 8)) {
            le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            Vector away = le.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(1.6);
            away.setY(0.4);
            le.setVelocity(away);
            le.damage(6.0, p);
        }
    }

    private void soulShackle(Player p) {
        LivingEntity target = nearestEnemy(p, 10);
        if (target == null) return;
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 200));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 3));
        target.damage(5.0, p);
        target.getWorld().spawnParticle(Particle.SOUL, target.getLocation().add(0, 1, 0), 40, 0.4, 0.8, 0.4, 0.02);
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_SOUL_SAND_BREAK, 1f, 0.5f);
    }

    // ---------------------------------------------------------- on-hit passives

    @EventHandler
    public void onDamageDealt(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        if (!(e.getEntity() instanceof LivingEntity target)) return;

        PowerType type = powers.readPower(p.getInventory().getItemInMainHand());
        if (type == null) return;

        switch (type) {
            case REAPERS_MARK -> {
                if (Math.random() < 0.10) {
                    target.setHealth(0);
                    p.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 1.5f);
                    p.sendActionBar(ChatColor.DARK_RED + "Reaper's Mark claims a life.");
                }
            }
            case STORMCALLER -> {
                if (Math.random() < 0.25) {
                    target.getWorld().strikeLightning(target.getLocation());
                }
            }
            case CRIMSON_THIRST -> {
                double heal = e.getFinalDamage() * 0.35;
                double max = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                p.setHealth(Math.min(max, p.getHealth() + heal));
                p.getWorld().spawnParticle(Particle.DUST, p.getLocation().add(0, 1, 0), 10,
                        new Particle.DustOptions(Color.RED, 1.2f));
            }
            case VENOMOUS_BITE -> {
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 140, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 140, 1));
            }
            default -> { }
        }
    }

    // ---------------------------------------------------------- Berserker's Wrath (low HP)

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (e.isCancelled()) return;

        boolean hasTotem = powers.readPower(p.getInventory().getItemInMainHand()) == PowerType.BERSERKERS_WRATH
                || powers.readPower(p.getInventory().getItemInOffHand()) == PowerType.BERSERKERS_WRATH;
        if (!hasTotem) return;

        double maxHealth = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double healthAfter = Math.max(0, p.getHealth() - e.getFinalDamage());
        if (healthAfter / maxHealth > 0.30) return;

        Long until = berserkerCooldown.get(p.getUniqueId());
        if (until != null && until > System.currentTimeMillis()) return;
        berserkerCooldown.put(p.getUniqueId(), System.currentTimeMillis() + 45_000L);

        p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 160, 2));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 2));
        p.setHealth(Math.min(maxHealth, healthAfter + 6.0));
        p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation().add(0, 1, 0), 80, 0.5, 1, 0.5, 0.2);
        p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
        p.sendActionBar(ChatColor.YELLOW + "Berserker's Wrath erupts!");
    }

    // ---------------------------------------------------------- Crown of the Void King (worn)

    private void startCrownHeartbeat() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    ItemStack helmet = p.getInventory().getHelmet();
                    if (powers.readPower(helmet) != PowerType.CROWN) continue;

                    p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 2, true, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 3, true, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, true, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, 1, true, false));

                    Long until = crownBurstCooldown.get(p.getUniqueId());
                    if (until == null || until <= System.currentTimeMillis()) {
                        royalDecree(p);
                        crownBurstCooldown.put(p.getUniqueId(),
                                System.currentTimeMillis() + PowerType.CROWN.getCooldownSeconds() * 1000L);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every 1 second
    }

    private void royalDecree(Player wearer) {
        wearer.getWorld().spawnParticle(Particle.FLAME, wearer.getLocation().add(0, 1, 0), 150, 3, 1.5, 3, 0.08);
        wearer.getWorld().spawnParticle(Particle.END_ROD, wearer.getLocation().add(0, 1, 0), 60, 3, 1.5, 3, 0.05);
        wearer.getWorld().playSound(wearer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.2f, 1f);

        for (LivingEntity le : nearbyEnemies(wearer, 8)) {
            le.setFireTicks(160);
            le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 1));
            Vector away = le.getLocation().toVector().subtract(wearer.getLocation().toVector()).normalize().multiply(1.5);
            away.setY(0.5);
            le.setVelocity(away);
            le.damage(12.0, wearer);
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD
                + "⚜ " + wearer.getName() + " unleashes the Royal Decree! ⚜");
    }
}
