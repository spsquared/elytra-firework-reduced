package app.web.spsquared.network.payload;

import org.jspecify.annotations.NonNull;
import app.web.spsquared.Version;
import app.web.spsquared.gamerules.FireworkGameRules;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRules;

public record GameRuleSyncPayload(Double power, Double speed, Double time) implements CustomPacketPayload {
    private static final @NonNull Identifier ID = Identifier.fromNamespaceAndPath(Version.NAMESPACE, "config");
    public static final CustomPacketPayload.Type<GameRuleSyncPayload> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, GameRuleSyncPayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.DOUBLE, GameRuleSyncPayload::power, ByteBufCodecs.DOUBLE, GameRuleSyncPayload::speed, ByteBufCodecs.DOUBLE, GameRuleSyncPayload::time, GameRuleSyncPayload::new);

    public GameRuleSyncPayload(GameRules gameRules) {
        this(gameRules.get(FireworkGameRules.FIREWORK_POWER), gameRules.get(FireworkGameRules.FIREWORK_SPEED), gameRules.get(FireworkGameRules.FIREWORK_TIME));
    }

    @SuppressWarnings("null")
    @Override
    public @NonNull Type<GameRuleSyncPayload> type() {
        return TYPE;
    }
}
