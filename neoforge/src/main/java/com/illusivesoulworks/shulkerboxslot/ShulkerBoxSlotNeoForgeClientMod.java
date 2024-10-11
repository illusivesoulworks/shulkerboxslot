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

import com.illusivesoulworks.shulkerboxslot.client.CurioShulkerBoxRenderer;
import com.illusivesoulworks.shulkerboxslot.client.ShulkerBoxSlotClientEvents;
import com.illusivesoulworks.shulkerboxslot.client.ShulkerBoxSlotKeyRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(value = ShulkerBoxSlotConstants.MOD_ID, dist = Dist.CLIENT)
public class ShulkerBoxSlotNeoForgeClientMod {

  public ShulkerBoxSlotNeoForgeClientMod(IEventBus eventBus) {
    eventBus.addListener(this::registerKeys);
    eventBus.addListener(this::clientSetup);
    NeoForge.EVENT_BUS.addListener(this::clientTick);
  }

  private void registerKeys(final RegisterKeyMappingsEvent evt) {
    ShulkerBoxSlotKeyRegistry.setup();
    evt.register(ShulkerBoxSlotKeyRegistry.openShulkerBox);
  }

  private void clientTick(ClientTickEvent.Post evt) {
    ShulkerBoxSlotClientEvents.clientTick();
  }

  private void clientSetup(final FMLClientSetupEvent evt) {

    for (Item shulkerBox : ShulkerBoxSlotCommonMod.getShulkerBoxes()) {
      CuriosRendererRegistry.register(shulkerBox, CurioShulkerBoxRenderer::new);
    }
  }
}