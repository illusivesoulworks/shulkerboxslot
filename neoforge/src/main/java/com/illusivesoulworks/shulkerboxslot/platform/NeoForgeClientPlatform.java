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

package com.illusivesoulworks.shulkerboxslot.platform;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotCommonMod;
import com.illusivesoulworks.shulkerboxslot.common.network.CPayloadOpenShulkerBox;
import com.illusivesoulworks.shulkerboxslot.platform.services.IClientPlatform;
import javax.annotation.Nonnull;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;

public class NeoForgeClientPlatform implements IClientPlatform {

  @Override
  public KeyMapping createKeyMapping(int key, String desc, String category) {
    IKeyConflictContext ctx = new IKeyConflictContext() {
      @Override
      public boolean isActive() {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
          return CuriosApi.getCuriosInventory(player).map(inv -> inv.findFirstCurio(
                  stack -> ShulkerBoxSlotCommonMod.isShulkerBox(stack.getItem())).isPresent())
              .orElse(false);
        }
        return false;
      }

      @Override
      public boolean conflicts(@Nonnull IKeyConflictContext other) {
        return false;
      }
    };
    KeyMapping mapping = new KeyMapping(desc, key, category);
    mapping.setKeyConflictContext(ctx);
    return mapping;
  }

  @Override
  public void sendOpenPacket() {
    PacketDistributor.sendToServer(CPayloadOpenShulkerBox.INSTANCE);
  }
}
