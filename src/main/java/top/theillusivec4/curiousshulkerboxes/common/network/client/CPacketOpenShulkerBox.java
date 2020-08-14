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

package top.theillusivec4.curiousshulkerboxes.common.network.client;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.Stats;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curiousshulkerboxes.CuriousShulkerBoxes;
import top.theillusivec4.curiousshulkerboxes.common.inventory.CurioShulkerBoxInventory;

public class CPacketOpenShulkerBox {

  public static void encode(CPacketOpenShulkerBox msg, PacketBuffer buf) {

  }

  public static CPacketOpenShulkerBox decode(PacketBuffer buf) {
    return new CPacketOpenShulkerBox();
  }

  public static void handle(CPacketOpenShulkerBox msg, Supplier<NetworkEvent.Context> ctx) {

    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender == null) {
        return;
      }
      sender.addStat(Stats.OPEN_SHULKER_BOX);
      Optional<ImmutableTriple<String, Integer, ItemStack>> curioShulkerBox = CuriousShulkerBoxes
          .getCurioShulkerBox(sender);
      curioShulkerBox.ifPresent(box -> {
        ItemStack stack = box.getRight();
        String identifier = box.getLeft();
        int index = box.getMiddle();
        INamedContainerProvider container = new CurioShulkerBoxInventory(stack, identifier, index);
        NetworkHooks.openGui(sender, container);
      });
    });
    ctx.get().setPacketHandled(true);
  }
}
