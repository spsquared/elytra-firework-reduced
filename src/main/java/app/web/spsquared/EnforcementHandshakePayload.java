package app.web.spsquared;

import org.jspecify.annotations.NonNull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EnforcementHandshakePayload(String version) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, EnforcementHandshakePayload> CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(11), EnforcementHandshakePayload::version, EnforcementHandshakePayload::new);
    public static final Identifier ID = Identifier.fromNamespaceAndPath("elyfireworknerf", "presence");
    public static final CustomPacketPayload.Type<EnforcementHandshakePayload> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public @NonNull Type<EnforcementHandshakePayload> type() {
        return TYPE;
    }
}
