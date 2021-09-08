package fr.milekat.grimtown;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataManager {
    private final MainBungee mainBungee;

    public DataManager(MainBungee plugin) {
        mainBungee = plugin;
    }

    public Configuration getConfigurations() {
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    new File(mainBungee.getDataFolder(),"config.yml")
            );
            MainBungee.DEBUG_ERRORS = config.getBoolean("other.debug_exeptions");
            MainBungee.DEBUG_RABBIT = config.getBoolean("redis.debug");
            return config;
        } catch (IOException throwable) {
            MainBungee.warning("Error config File: " + throwable);
            throwable.printStackTrace();
        }
        return null;
    }
}
