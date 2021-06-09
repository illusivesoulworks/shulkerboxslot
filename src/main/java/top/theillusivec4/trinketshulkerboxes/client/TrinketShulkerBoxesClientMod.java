/*
 * Copyright (c) 2019-2021 C4
 *
 * This file is part of Trinket Shulker Boxes, a mod made for Minecraft.
 *
 * Trinket Shulker Boxes is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trinket Shulker Boxes is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Trinket Shulker Boxes. If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.trinketshulkerboxes.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import top.theillusivec4.trinketshulkerboxes.common.network.TSBClientNetwork;
import top.theillusivec4.trinketshulkerboxes.common.network.TSBPackets;

public class TrinketShulkerBoxesClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    LivingEntityFeatureRendererRegistrationCallback.EVENT
        .register((entityType, entityRenderer, registrationHelper, context) -> registrationHelper
            .register(new TrinketShulkerBoxFeatureRenderer<>(entityRenderer,
                context.getModelLoader())));
    ClientTickEvents.END_CLIENT_TICK.register((minecraftClient -> {
      ClientPlayerEntity clientPlayerEntity = minecraftClient.player;

      if (clientPlayerEntity != null && TSBKeys.openShulkerBox.wasPressed()) {
        ClientPlayNetworking.send(TSBPackets.OPEN_SHULKER_BOX, PacketByteBufs.create());
      }
    }));
    TSBKeys.registerKeys();
    TSBClientNetwork.register();
  }
}
