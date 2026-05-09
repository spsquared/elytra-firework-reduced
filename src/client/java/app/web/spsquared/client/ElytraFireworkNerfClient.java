package app.web.spsquared.client;

import app.web.spsquared.ElytraFireworkNerf;
import app.web.spsquared.EnforcementHandshakePayload;
import app.web.spsquared.Version;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ElytraFireworkNerfClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // we're registering this anyway so the server can check immediately if the mod is installed
        PayloadTypeRegistry.clientboundPlay().register(EnforcementHandshakePayload.TYPE, EnforcementHandshakePayload.CODEC);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (ClientPlayNetworking.canSend(EnforcementHandshakePayload.TYPE)) {
                ClientPlayNetworking.send(new EnforcementHandshakePayload(Version.VERSION));
                ElytraFireworkNerf.enabled = true;
                ElytraFireworkNerf.LOGGER.info("Server has elyfireworknerf, enabling changes");
            } else {
                ElytraFireworkNerf.enabled = false;
                ElytraFireworkNerf.LOGGER.info("Server appears to not have elyfireworknerf, disabling changes");
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(EnforcementHandshakePayload.TYPE, (payload, context) -> {
            // well it's probably already enabled
        });
    }
}
