package app.web.spsquared.client;

import app.web.spsquared.EnforcementHandshakePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ElytraFireworkNerfClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientPlayNetworking.send(new EnforcementHandshakePayload("1.0.0"));
        });
	}
}