package app.web.spsquared.network;

import org.jspecify.annotations.NonNull;
import app.web.spsquared.Version;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EnforcementHandshakePayload(String version) implements CustomPacketPayload {
    private static final @NonNull Identifier ID = Identifier.fromNamespaceAndPath(Version.NAMESPACE, "presence");
    public static final CustomPacketPayload.Type<EnforcementHandshakePayload> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final @NonNull StreamCodec<FriendlyByteBuf, EnforcementHandshakePayload> CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(11), EnforcementHandshakePayload::version, EnforcementHandshakePayload::new);

    @SuppressWarnings("null")
    @Override
    public @NonNull Type<EnforcementHandshakePayload> type() {
        return TYPE;
    }
}
