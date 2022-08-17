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
import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxSlotForgeNetwork;
import com.illusivesoulworks.shulkerboxslot.common.network.CPacketOpenShulkerBox;
import com.illusivesoulworks.shulkerboxslot.platform.services.IClientPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.settings.IKeyConflictContext;
import top.theillusivec4.curios.api.CuriosApi;

public class ForgeClientPlatform implements IClientPlatform {

  @Override
  public KeyMapping createKeyMapping(int key, String desc, String category) {
    IKeyConflictContext ctx = new IKeyConflictContext() {
      @Override
      public boolean isActive() {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null) {
          return CuriosApi.getCuriosHelper().findFirstCurio(player,
              (stack) -> ShulkerBoxSlotCommonMod.isShulkerBox(stack.getItem())).isPresent();
        }
        return false;
      }

      @Override
      public boolean conflicts(IKeyConflictContext other) {
        return false;
      }
    };
    KeyMapping mapping = new KeyMapping(desc, key, category);
    mapping.setKeyConflictContext(ctx);
    return mapping;
  }

  @Override
  public void sendOpenPacket() {
    ShulkerBoxSlotForgeNetwork.get().sendToServer(new CPacketOpenShulkerBox());
  }
}
