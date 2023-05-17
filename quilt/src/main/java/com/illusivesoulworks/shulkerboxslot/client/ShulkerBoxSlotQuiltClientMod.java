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

package com.illusivesoulworks.shulkerboxslot.client;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotCommonMod;
import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxSlotPackets;
import com.illusivesoulworks.shulkerboxslot.common.network.SPacketSyncAnimation;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.world.item.Item;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class ShulkerBoxSlotQuiltClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient(ModContainer modContainer) {
    ShulkerBoxSlotKeyRegistry.setup();
    KeyBindingHelper.registerKeyBinding(ShulkerBoxSlotKeyRegistry.openShulkerBox);
    ClientTickEvents.END.register(
        (minecraftClient -> ShulkerBoxSlotClientEvents.clientTick()));
    ClientPlayNetworking.registerGlobalReceiver(ShulkerBoxSlotPackets.SYNC_SHULKER_BOX,
        (client, handler, buf, responseSender) -> {
          SPacketSyncAnimation msg = SPacketSyncAnimation.decode(buf);
          client.execute(() -> SPacketSyncAnimation.handle(msg));
        });

    for (Item shulkerBox : ShulkerBoxSlotCommonMod.getShulkerBoxes()) {
      TrinketRendererRegistry.registerRenderer(shulkerBox, new TrinketShulkerBoxRenderer());
    }
  }
}
