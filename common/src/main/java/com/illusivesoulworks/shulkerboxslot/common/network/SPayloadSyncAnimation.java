package com.illusivesoulworks.shulkerboxslot.common.network;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotConstants;
import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SPayloadSyncAnimation(int entityId, String identifier, int index,
                                    boolean isClosing) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<SPayloadSyncAnimation> TYPE =
      new CustomPacketPayload.Type<>(
          ResourceLocation.fromNamespaceAndPath(ShulkerBoxSlotConstants.MOD_ID, "sync_animation"));

  public static final StreamCodec<FriendlyByteBuf, SPayloadSyncAnimation> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          SPayloadSyncAnimation::entityId,
          ByteBufCodecs.STRING_UTF8,
          SPayloadSyncAnimation::identifier,
          ByteBufCodecs.VAR_INT,
          SPayloadSyncAnimation::index,
          ByteBufCodecs.BOOL,
          SPayloadSyncAnimation::isClosing,
          SPayloadSyncAnimation::new);

  @Nonnull
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
