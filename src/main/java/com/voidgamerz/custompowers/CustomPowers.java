package com.voidgamerz.custompowers;

import org.bukkit.plugin.java.JavaPlugin;

public class CustomPowers extends JavaPlugin {

    private PowerManager powerManager;

    @Override
    public void onEnable() {
        powerManager = new PowerManager(this);

        getServer().getPluginManager().registerEvents(new PowerListener(this, powerManager), this);

        PowerCommands commands = new PowerCommands(powerManager);
        getCommand("givepower").setExecutor(commands);
        getCommand("givepower").setTabCompleter(commands);
        getCommand("listpowers").setExecutor(commands);

        getLogger().info("CustomPowers enabled — 15 powers + the Crown of the Void King are live.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomPowers disabled.");
    }

    public PowerManager getPowerManager() {
        return powerManager;
    }
}
