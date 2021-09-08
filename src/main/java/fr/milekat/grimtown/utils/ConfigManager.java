package fr.milekat.grimtown.utils;

import fr.milekat.grimtown.MainBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public record ConfigManager(MainBungee mainBungee) {
    private static Configuration config;

    /**
     * Load config file
     */
    public Configuration getConfigs() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    new File(mainBungee.getDataFolder(),"config.yml")
            );
            return config;
        } catch (IOException throwable) {
            MainBungee.warning("Error config File: " + throwable);
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     * Load values
     */
    public void loadConfigs() {
        MainBungee.PREFIX = config.getString("core.prefix");
        MainBungee.DEBUG_ERRORS = config.getBoolean("core.debug-exceptions");
        MainBungee.DEBUG_RABBIT = config.getBoolean("rabbitmq.debug");
    }
}
