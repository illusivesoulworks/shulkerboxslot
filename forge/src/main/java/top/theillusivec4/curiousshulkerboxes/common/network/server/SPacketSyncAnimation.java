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

package top.theillusivec4.curiousshulkerboxes.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curiousshulkerboxes.CuriousShulkerBoxes;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;

public class SPacketSyncAnimation {

  private final int entityId;
  private final String identifier;
  private final int index;
  private final boolean isClosing;

  public SPacketSyncAnimation(int entityId, String identifier, int index, boolean isClosing) {
    this.entityId = entityId;
    this.identifier = identifier;
    this.index = index;
    this.isClosing = isClosing;
  }

  public static void encode(SPacketSyncAnimation msg, FriendlyByteBuf buf) {

    buf.writeInt(msg.entityId);
    buf.writeUtf(msg.identifier);
    buf.writeInt(msg.index);
    buf.writeBoolean(msg.isClosing);
  }

  public static SPacketSyncAnimation decode(FriendlyByteBuf buf) {

    return new SPacketSyncAnimation(buf.readInt(), buf.readUtf(25), buf.readInt(),
        buf.readBoolean());
  }

  public static void handle(SPacketSyncAnimation msg, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ClientLevel world = Minecraft.getInstance().level;

      if (world == null) {
        return;
      }
      Entity entity = world.getEntity(msg.entityId);

      if (!(entity instanceof LivingEntity)) {
        return;
      }

      LivingEntity livingEntity = (LivingEntity) entity;
      CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity).ifPresent(
          handler -> handler.getStacksHandler(msg.identifier).ifPresent(stacksHandler -> {

            if (msg.index < stacksHandler.getSlots()) {
              ItemStack stack = stacksHandler.getStacks().getStackInSlot(msg.index);

              if (CuriousShulkerBoxes.isShulkerBox(stack.getItem())) {
                CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> {

                  if (curio instanceof CurioShulkerBox) {

                    if (msg.isClosing) {
                      ((CurioShulkerBox) curio)
                          .setAnimationStatus(ShulkerBoxBlockEntity.AnimationStatus.CLOSING);
                    } else {
                      ((CurioShulkerBox) curio)
                          .setAnimationStatus(ShulkerBoxBlockEntity.AnimationStatus.OPENING);
                    }
                  }
                });
              }
            }
          }));
    });

    ctx.get().setPacketHandled(true);
  }
}
