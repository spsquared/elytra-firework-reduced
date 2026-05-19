package app.web.spsquared.network;

import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents.ValueUpdate;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.gamerules.GameRules;
import app.web.spsquared.ElytraFireworkReduced;
import app.web.spsquared.Version;
import app.web.spsquared.config.ConfigManager;
import app.web.spsquared.gamerules.FireworkGameRules;
import app.web.spsquared.network.payload.EnforcementHandshakePayload;
import app.web.spsquared.network.payload.GameRuleSyncPayload;

public class ServerPlay {

    public static void init() {
        initEnforcement();
        initSync();
    }

    private static final int enforcementHandshakeDeadline = 200; // you have 10 seconds to verify
    private static final Map<@NonNull UUID, Integer> enforcementList = new ConcurrentHashMap<>();
    private static final @NonNull DisconnectionDetails enforcementMessage = new DisconnectionDetails(Component.literal("Reduced Elytra Firework v" + Version.VERSION + " must be installed"));

    private static void initEnforcement() {
        // always register this payload so client knows if the mod is present
        PayloadTypeRegistry.serverboundPlay().register(EnforcementHandshakePayload.TYPE, EnforcementHandshakePayload.CODEC);

        // MASSIVE ASSUMPTION that config is only loaded ONCE at init, and
        // subsequent changes require a server restart, and not a reload
        if (ConfigManager.config().enforce()) {
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                final ServerPlayer player = handler.player;
                enforcementList.put(player.getUUID(), 0);
                if (ServerPlayNetworking.canSend(player, EnforcementHandshakePayload.TYPE)) {
                    ServerPlayNetworking.send(player, new EnforcementHandshakePayload(Version.VERSION));
                } else {
                    player.connection.disconnect(enforcementMessage);
                    ElytraFireworkReduced.LOGGER.warn(String.format("Disconnected as packet %s was not registered", EnforcementHandshakePayload.TYPE.id().toString()));
                }
            });
            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                enforcementList.remove(handler.player.getUUID());
                ElytraFireworkReduced.playersWithMod.remove(handler.player.getUUID());
            });

            ServerPlayNetworking.registerGlobalReceiver(EnforcementHandshakePayload.TYPE, (payload, context) -> {
                final ServerPlayer player = context.player();
                String clientVersion = payload.version();
                if (!clientVersion.equals(Version.VERSION)) {
                    // disconnect if wrong version
                    player.connection.disconnect(enforcementMessage);
                    ElytraFireworkReduced.LOGGER.warn(String.format("Disconnected as client has wrong version (expected %s, got %s)", Version.VERSION, clientVersion));
                } else {
                    ElytraFireworkReduced.playersWithMod.put(player.getUUID(), true);
                }
                enforcementList.remove(player.getUUID());
            });

            ServerTickEvents.END_SERVER_TICK.register((server) -> {
                // this is probably way overkill
                // disconnect if somehow mod namespace was registered but packet wasn't sent in time
                PlayerList players = server.getPlayerList();
                for (@NonNull
                UUID id : enforcementList.keySet()) {
                    Integer i = enforcementList.get(id);
                    enforcementList.put(id, i + 1);
                    if (i >= enforcementHandshakeDeadline) { // intentional
                        ServerPlayer p = players.getPlayer(id);
                        if (p != null) {
                            p.connection.disconnect(enforcementMessage); // hopefully this doesn't crash if the player is already disconnected?
                            ElytraFireworkReduced.LOGGER.warn("Disconnected as client presence packet timed out");
                        }
                        enforcementList.remove(id);
                    }
                }
            });
        } else {
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                final ServerPlayer player = handler.player;
                enforcementList.put(player.getUUID(), 0);
                if (ServerPlayNetworking.canSend(player, EnforcementHandshakePayload.TYPE)) {
                    ServerPlayNetworking.send(player, new EnforcementHandshakePayload(Version.VERSION));
                }
            });
            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                ElytraFireworkReduced.playersWithMod.remove(handler.player.getUUID());
            });

            ServerPlayNetworking.registerGlobalReceiver(EnforcementHandshakePayload.TYPE, (payload, context) -> {
                ElytraFireworkReduced.playersWithMod.put(context.player().getUUID(), true);
            });
        }
    }

    private static void initSync() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ServerPlayNetworking.canSend(handler.player, GameRuleSyncPayload.TYPE)) {
                ServerPlayNetworking.send(handler.player, new GameRuleSyncPayload(server.getGameRules()));
            }
        });
        final ValueUpdate<Double> updateListener = (value, server) -> {
            GameRules gameRules = server.getGameRules();
            for (ServerPlayer player : PlayerLookup.all(server)) {
                if (ServerPlayNetworking.canSend(player, GameRuleSyncPayload.TYPE)) {
                    ServerPlayNetworking.send(player, new GameRuleSyncPayload(gameRules));
                }
            }
            GameRuleMirror.update(new GameRuleMirror(gameRules.get(FireworkGameRules.FIREWORK_POWER), gameRules.get(FireworkGameRules.FIREWORK_SPEED), gameRules.get(FireworkGameRules.FIREWORK_TIME)));
        };
        GameRuleEvents.changeCallback(FireworkGameRules.FIREWORK_POWER).register(updateListener);
        GameRuleEvents.changeCallback(FireworkGameRules.FIREWORK_SPEED).register(updateListener);
        GameRuleEvents.changeCallback(FireworkGameRules.FIREWORK_TIME).register(updateListener);
    }
}
