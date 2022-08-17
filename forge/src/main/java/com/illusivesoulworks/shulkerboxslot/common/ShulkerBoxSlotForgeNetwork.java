/*
 * Copyright (C) 2019-2022 Illusive Soulworks
 *
 * Shulker Box Slot is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Shulker Box Slot is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Shulker Box Slot.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.shulkerboxslot.common;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotConstants;
import com.illusivesoulworks.shulkerboxslot.common.network.CPacketOpenShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.network.SPacketSyncAnimation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ShulkerBoxSlotForgeNetwork {

  private static final String PTC_VERSION = "1";

  private static SimpleChannel instance;
  private static int id = 0;

  public static SimpleChannel get() {
    return instance;
  }

  public static void setup() {
    instance = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(ShulkerBoxSlotConstants.MOD_ID, "main"))
        .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
        .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    // Client-to-Server
    registerC2S(CPacketOpenShulkerBox.class, CPacketOpenShulkerBox::encode,
        CPacketOpenShulkerBox::decode, CPacketOpenShulkerBox::handle);

    // Server-to-Client
    registerS2C(SPacketSyncAnimation.class, SPacketSyncAnimation::encode,
        SPacketSyncAnimation::decode, SPacketSyncAnimation::handle);
  }

  public static <M> void registerC2S(Class<M> clazz, BiConsumer<M, FriendlyByteBuf> encoder,
                                     Function<FriendlyByteBuf, M> decoder,
                                     BiConsumer<M, ServerPlayer> handler) {
    instance.registerMessage(id++, clazz, encoder, decoder, (message, contextSupplier) -> {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(() -> {
        ServerPlayer sender = context.getSender();

        if (sender != null) {
          handler.accept(message, sender);
        }
      });
      context.setPacketHandled(true);
    });
  }

  public static <M> void registerS2C(Class<M> clazz, BiConsumer<M, FriendlyByteBuf> encoder,
                                     Function<FriendlyByteBuf, M> decoder, Consumer<M> handler) {
    instance.registerMessage(id++, clazz, encoder, decoder, (message, contextSupplier) -> {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(
          () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handler.accept(message)));
      context.setPacketHandled(true);
    });
  }
}
