package com.illusivesoulworks.shulkerboxslot.common;

import com.illusivesoulworks.shulkerboxslot.common.network.CPayloadOpenShulkerBox;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ShulkerBoxServerPayloadHandler {

  private static final ShulkerBoxServerPayloadHandler INSTANCE =
      new ShulkerBoxServerPayloadHandler();

  public static ShulkerBoxServerPayloadHandler getInstance() {
    return INSTANCE;
  }

  public void handleOpenShulkerBox(CPayloadOpenShulkerBox msg, IPayloadContext ctx) {
    ctx.enqueueWork(() -> {
          if (ctx.player() instanceof ServerPlayer serverPlayer) {
            CPayloadOpenShulkerBox.handle(serverPlayer);
          }
        })
        .exceptionally(e -> {
          ctx.disconnect(
              Component.translatable("shulkerboxslot.networking.failed", e.getMessage()));
          return null;
        });
  }
}
