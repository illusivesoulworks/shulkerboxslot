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

import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxSlotPackets;
import com.illusivesoulworks.shulkerboxslot.platform.services.IClientPlatform;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;

public class FabricClientPlatform implements IClientPlatform {

  @Override
  public KeyMapping createKeyMapping(int key, String desc, String category) {
    return new KeyMapping(desc, key, category);
  }

  @Override
  public void sendOpenPacket() {
    ClientPlayNetworking.send(ShulkerBoxSlotPackets.OPEN_SHULKER_BOX, PacketByteBufs.create());
  }
}
