package app.web.spsquared.client.network;

import app.web.spsquared.ElytraFireworkReduced;
import app.web.spsquared.Version;
import app.web.spsquared.network.GameRuleMirror;
import app.web.spsquared.network.payload.EnforcementHandshakePayload;
import app.web.spsquared.network.payload.GameRuleSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class ClientPlay {
    public static void init() {
        initEnforcement();
        initSync();
    }

    private static void initEnforcement() {
        // register payload so the server can check immediately if the mod is installed
        PayloadTypeRegistry.clientboundPlay().register(EnforcementHandshakePayload.TYPE, EnforcementHandshakePayload.CODEC);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (ClientPlayNetworking.canSend(EnforcementHandshakePayload.TYPE)) {
                ClientPlayNetworking.send(new EnforcementHandshakePayload(Version.VERSION));
                ElytraFireworkReduced.enabled = true;
                ElytraFireworkReduced.LOGGER.info("Server has elytra_firework_reduced, enabling changes");
            } else {
                ElytraFireworkReduced.enabled = false;
                ElytraFireworkReduced.LOGGER.info("Server appears to not have elytra_firework_reduced, disabling changes");
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(EnforcementHandshakePayload.TYPE, (payload, context) -> {
            // mod is enabled, but we may want a warning if the wrong version is present
            // (if the server doesn't kick us for wrong version in the case that enforcement is disabled)
            String serverVersion = payload.version();
            if (!serverVersion.equals(Version.VERSION)) {
                LocalPlayer player = context.player();
                if (player != null)
                    player.sendSystemMessage(Component.translatable("multiplayer.reduced_elytra_firework.version_warning", serverVersion, Version.VERSION));
            }
        });
    }

    private static void initSync() {
        PayloadTypeRegistry.clientboundPlay().register(GameRuleSyncPayload.TYPE, GameRuleSyncPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(GameRuleSyncPayload.TYPE, (payload, context) -> {
            GameRuleMirror.update(new GameRuleMirror(payload.power(), payload.speed(), payload.time()));
        });
    }
}
