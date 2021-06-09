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

package top.theillusivec4.trinketshulkerboxes.common.network;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Pair;
import top.theillusivec4.trinketshulkerboxes.common.TrinketShulkerBoxInventory;

public class TSBNetwork {

  public static void register() {
    ServerPlayNetworking.registerGlobalReceiver(TSBPackets.OPEN_SHULKER_BOX,
        (server, player, handler, buf, responseSender) -> {
          player.incrementStat(Stats.OPEN_SHULKER_BOX);
          TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            List<Pair<SlotReference, ItemStack>> res =
                component.getEquipped(TSBNetwork::isShulkerBox);

            if (res.size() > 0) {
              player.openHandledScreen(new TrinketShulkerBoxInventory(res.get(0).getRight()));
            }
          });
        });
  }

  private static boolean isShulkerBox(ItemStack stack) {

    if (stack.getItem() instanceof BlockItem blockItem) {
      Block block = blockItem.getBlock();
      return block instanceof ShulkerBoxBlock;
    }
    return false;
  }
}
