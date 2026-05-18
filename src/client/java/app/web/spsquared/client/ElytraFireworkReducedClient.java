package app.web.spsquared.client;

import app.web.spsquared.client.network.ClientPlay;
import net.fabricmc.api.ClientModInitializer;

public class ElytraFireworkReducedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlay.init();
    }
}
