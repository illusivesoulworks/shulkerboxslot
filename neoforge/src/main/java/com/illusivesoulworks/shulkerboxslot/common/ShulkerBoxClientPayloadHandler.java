package com.illusivesoulworks.shulkerboxslot.common;

import com.illusivesoulworks.shulkerboxslot.common.network.SPayloadSyncAnimation;
import com.illusivesoulworks.shulkerboxslot.common.network.ShulkerBoxClientPackets;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ShulkerBoxClientPayloadHandler {

  private static final ShulkerBoxClientPayloadHandler INSTANCE =
      new ShulkerBoxClientPayloadHandler();

  public static ShulkerBoxClientPayloadHandler getInstance() {
    return INSTANCE;
  }

  public void handle(SPayloadSyncAnimation msg, IPayloadContext ctx) {
    boolean isClosing = msg.isClosing();
    String identifier = msg.identifier();
    int entityId = msg.entityId();
    int index = msg.index();
    ctx.enqueueWork(() -> {
          ShulkerBoxClientPackets.handle(entityId, identifier, index, isClosing);
        })
        .exceptionally(e -> {
          ctx.disconnect(
              Component.translatable("shulkerboxslot.networking.failed", e.getMessage()));
          return null;
        });
  }
}
