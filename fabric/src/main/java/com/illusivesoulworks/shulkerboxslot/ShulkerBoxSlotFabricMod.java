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

package com.illusivesoulworks.shulkerboxslot;

import com.illusivesoulworks.shulkerboxslot.common.TrinketShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.integration.reinfshulker.ReinfShulkerPlugin;
import com.illusivesoulworks.shulkerboxslot.common.network.CPayloadOpenShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.network.SPayloadSyncAnimation;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class ShulkerBoxSlotFabricMod implements ModInitializer {

  public static final DataComponentType<AnimProgressComponent> ANIM_PROGRESS =
      Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
          ResourceLocation.fromNamespaceAndPath(ShulkerBoxSlotConstants.MOD_ID,
              "animation_progress"),
          DataComponentType.<AnimProgressComponent>builder().persistent(AnimProgressComponent.CODEC)
              .networkSynchronized(AnimProgressComponent.STREAM_CODEC).build()
      );

  public static boolean isReinfShulkerLoaded = false;

  @Override
  public void onInitialize() {
    isReinfShulkerLoaded = FabricLoader.getInstance().isModLoaded("reinfshulker");

    for (Item shulkerBox : ShulkerBoxSlotCommonMod.getShulkerBoxes()) {
      TrinketsApi.registerTrinket(shulkerBox, new TrinketShulkerBox());
    }
    PayloadTypeRegistry.playC2S()
        .register(CPayloadOpenShulkerBox.TYPE, CPayloadOpenShulkerBox.STREAM_CODEC);
    PayloadTypeRegistry.playS2C()
        .register(SPayloadSyncAnimation.TYPE, SPayloadSyncAnimation.STREAM_CODEC);
    ServerPlayNetworking.registerGlobalReceiver(CPayloadOpenShulkerBox.TYPE, (payload, context) -> {
      ServerPlayer player = context.player();
      context.server().execute(() -> {

        if (isReinfShulkerLoaded) {
          ReinfShulkerPlugin.handleOpenPacket(player);
        } else {
          CPayloadOpenShulkerBox.handle(player);
        }
      });
    });

    if (isReinfShulkerLoaded) {
      ReinfShulkerPlugin.onInitialize();
    }
  }
}
