package app.web.spsquared.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import app.web.spsquared.ElytraFireworkReduced;
import app.web.spsquared.Version;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigManager {
    private static Config current = new Config();
    private static final Path path = FabricLoader.getInstance().getConfigDir().resolve(Version.NAMESPACE + ".json");
    private static final Gson gson = new Gson();

    public static void load() {
        if (!Files.exists(path)) {
            save();
        } else {
            try {
                current = gson.fromJson(Files.readString(path), Config.class);
            } catch (IOException exception) {
                ElytraFireworkReduced.LOGGER.error("Failed to read configuration file", exception);
                ElytraFireworkReduced.LOGGER.warn("Using default configuration");
            }
        }
    }

    public static void save() {
        try {
            Files.writeString(path, gson.toJson(current));
        } catch (IOException exception) {
            ElytraFireworkReduced.LOGGER.error("Failed to write configuration file", exception);
        }
    }

    public static Config config() {
        return current;
    }
}
