/*
 * Copyright (C) 2019  C4
 *
 * This file is part of Curious Shulker Boxes, a mod made for Minecraft.
 *
 * Curious Shulker Boxes is free software: you can redistribute it and/or
 * modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curious Shulker Boxes is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curious Shulker Boxes.  If not, see <https://www.gnu
 * .org/licenses/>.
 */

package top.theillusivec4.curiousshulkerboxes.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.curiousshulkerboxes.CuriousShulkerBoxes;
import top.theillusivec4.curiousshulkerboxes.common.network.client.CPacketOpenShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.server.SPacketSyncAnimation;

public class NetworkHandler {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void register() {
    INSTANCE = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(CuriousShulkerBoxes.MODID, "main"))
        .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
        .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    registerMessage(CPacketOpenShulkerBox.class, CPacketOpenShulkerBox::encode,
        CPacketOpenShulkerBox::decode, CPacketOpenShulkerBox::handle);
    registerMessage(SPacketSyncAnimation.class, SPacketSyncAnimation::encode,
        SPacketSyncAnimation::decode, SPacketSyncAnimation::handle);
  }

  private static <M> void registerMessage(Class<M> messageType, BiConsumer<M, PacketBuffer> encoder,
      Function<PacketBuffer, M> decoder,
      BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {

    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
