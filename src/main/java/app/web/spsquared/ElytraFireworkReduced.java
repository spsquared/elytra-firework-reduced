package app.web.spsquared;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.web.spsquared.config.ConfigManager;
import app.web.spsquared.gamerules.FireworkGameRules;
import app.web.spsquared.network.ServerPlay;

public class ElytraFireworkReduced implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Version.NAMESPACE);

    /**
     * Enable firework nerfs. Enabled by default, but for clients is disabled on join and re-enabled if
     * server sends packet to enable it to avoid nerfing unfairly. There is no logic to disable it
     * server-side.
     */
    public static boolean enabled = true;

    @Override
    public void onInitialize() {
        LOGGER.info("Oofing your fireworks");
        FireworkGameRules.init();
        ConfigManager.load();
        ServerPlay.init();
    }
}
