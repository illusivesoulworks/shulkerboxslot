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

package com.illusivesoulworks.shulkerboxslot.common.network;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxAccessoryInventory;
import com.illusivesoulworks.shulkerboxslot.platform.Services;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

public class CPacketOpenShulkerBox {

  public static void encode(CPacketOpenShulkerBox msg, FriendlyByteBuf buf) {

  }

  public static CPacketOpenShulkerBox decode(FriendlyByteBuf buf) {
    return new CPacketOpenShulkerBox();
  }

  public static void handle(CPacketOpenShulkerBox msg, ServerPlayer player) {
    player.awardStat(Stats.OPEN_SHULKER_BOX);
    Optional<Triple<ItemStack, String, Integer>> accessory = Services.INSTANCE.findShulkerBoxAccessory(player);
    accessory.ifPresent(box -> {
      ItemStack stack = box.getLeft();
      String identifier = box.getMiddle();
      int index = box.getRight();
      MenuProvider container = new ShulkerBoxAccessoryInventory(stack, identifier, index);
      Services.INSTANCE.openScreen(container, player);
    });
  }
}
