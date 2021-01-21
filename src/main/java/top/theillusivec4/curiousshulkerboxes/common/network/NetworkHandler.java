/*
 * Copyright (c) 2019-2020 C4
 *
 * This file is part of Curious Shulker Boxes, a mod made for Minecraft.
 *
 * Curious Shulker Boxes is free software: you can redistribute it and/or modify it
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
 * License along with Curious Shulker Boxes.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.curiousshulkerboxes.common.network;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBoxInventory;
import top.theillusivec4.curiousshulkerboxes.common.CuriousShulkerBoxesCommon;
import top.theillusivec4.curiousshulkerboxes.common.integration.enderite.EnderiteIntegration;
import top.theillusivec4.curiousshulkerboxes.common.integration.netheriteplus.NetheriteIntegration;

public class NetworkHandler {

  public static void register() {
    ServerSidePacketRegistry.INSTANCE.register(NetworkPackets.OPEN_SHULKER_BOX,
        (((packetContext, packetByteBuf) -> packetContext.getTaskQueue().execute(() -> {
          PlayerEntity playerEntity = packetContext.getPlayer();

          if (playerEntity != null) {
            playerEntity.incrementStat(Stats.OPEN_SHULKER_BOX);
            CuriosApi.getCuriosHelper()
                .findEquippedCurio((itemStack) -> isShulkerBox(itemStack), playerEntity)
                .ifPresent(found -> {
                  ItemStack stack = found.getRight();
                  Block block = Block.getBlockFromItem(stack.getItem());
                  String id = found.getLeft();
                  int index = found.getMiddle();

                  if (CuriousShulkerBoxesCommon.isEnderiteLoaded &&
                      EnderiteIntegration.isEnderiteShulkerBox(block)) {
                    EnderiteIntegration.openHandledScreen(playerEntity, stack, id, index);
                  } else {
                    playerEntity.openHandledScreen(new CurioShulkerBoxInventory(stack, id, index));
                  }
                });
          }
        }))));
  }

  private static boolean isShulkerBox(ItemStack stack) {

    if (stack.getItem() instanceof BlockItem) {
      BlockItem blockItem = (BlockItem) stack.getItem();
      Block block = blockItem.getBlock();

      if (CuriousShulkerBoxesCommon.isEnderiteLoaded &&
          EnderiteIntegration.isEnderiteShulkerBox(block)) {
        return true;
      } else if (CuriousShulkerBoxesCommon.isNetheriteLoaded &&
          NetheriteIntegration.isNetheriteShulkerBox(block)) {
        return true;
      } else {
        return block instanceof ShulkerBoxBlock;
      }
    }
    return false;
  }
}
