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

    @Override
    public void onInitialize() {
        LOGGER.info("Oofing your fireworks");
        // buh networking
        PayloadTypeRegistry.serverboundPlay().register(EnforcementHandshakePayload.ID, EnforcementHandshakePayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            enforcementList.put(handler.player.getUUID(), 0);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            enforcementList.remove(handler.player.getUUID());
        });
        ServerPlayNetworking.registerGlobalReceiver(EnforcementHandshakePayload.ID, (payload, context) -> {
            String clientVersion = payload.version();
            if (!clientVersion.equals(Version.VERSION)) {
                context.player().connection.disconnect(enforcementMessage);
            }
            enforcementList.remove(context.player().getUUID());
        });
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            PlayerList players = server.getPlayerList();
            for (UUID id : enforcementList.keySet()) {
                Integer i = enforcementList.get(id);
                enforcementList.put(id, i + 1);
                if (i >= enforcementHandshakeDeadline) { // intentional
                    ServerPlayer p = players.getPlayer(id);
                    if (p != null)
                        p.connection.disconnect(enforcementMessage); // hopefully this doesn't crash if the player is already disconnected?
                    enforcementList.remove(id);
                }
            }
        });
    }
}
