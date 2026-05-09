package app.web.spsquared;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraFireworkNerf implements ModInitializer {
    public static final String MOD_ID = "elyfireworknerf";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final int enforcementHandshakeDeadline = 200; // you have 10 seconds to verify
    private static final Map<UUID, Integer> enforcementList = new ConcurrentHashMap<>();
    private static final DisconnectionDetails enforcementMessage = new DisconnectionDetails(Component.literal("Elytra Firework Nerf v" + Version.VERSION + " must be installed"));

    /**
     * Enable firework nerfs. Enabled by default, but for clients is disabled on join and re-enabled if
     * server sends packet to enable it to avoid nerfing unfairly.
     */
    public static boolean enabled = true;

    @Override
    public void onInitialize() {
        LOGGER.info("Oofing your fireworks");
        // buh networking
        PayloadTypeRegistry.serverboundPlay().register(EnforcementHandshakePayload.TYPE, EnforcementHandshakePayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            enforcementList.put(handler.player.getUUID(), 0);
            if (!ServerPlayNetworking.canSend(handler.player, EnforcementHandshakePayload.TYPE)) {
                // disconnect if client hasn't registered mod
                handler.player.connection.disconnect(enforcementMessage);
                LOGGER.warn(String.format("Disconnected as packet %s was not registered", EnforcementHandshakePayload.ID.toString()));
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            enforcementList.remove(handler.player.getUUID());
        });
        ServerPlayNetworking.registerGlobalReceiver(EnforcementHandshakePayload.TYPE, (payload, context) -> {
            String clientVersion = payload.version();
            if (!clientVersion.equals(Version.VERSION)) {
                // disconnect if wrong version
                context.player().connection.disconnect(enforcementMessage);
                LOGGER.warn(String.format("Disconnected as client has wrong version (expected %s, got %s)", Version.VERSION, clientVersion));
            }
            enforcementList.remove(context.player().getUUID());
        });
        // this is probably way overkill
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            // disconnect if somehow mod namespace was registered but packet wasn't sent in time
            PlayerList players = server.getPlayerList();
            for (UUID id : enforcementList.keySet()) {
                Integer i = enforcementList.get(id);
                enforcementList.put(id, i + 1);
                if (i >= enforcementHandshakeDeadline) { // intentional
                    ServerPlayer p = players.getPlayer(id);
                    if (p != null) {
                        p.connection.disconnect(enforcementMessage); // hopefully this doesn't crash if the player is already disconnected?
                        LOGGER.warn("Disconnected as client presence packet timed out");
                    }
                    enforcementList.remove(id);
                }
            }
        });
    }
}
