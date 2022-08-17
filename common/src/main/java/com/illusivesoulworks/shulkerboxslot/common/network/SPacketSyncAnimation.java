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

import com.illusivesoulworks.shulkerboxslot.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

public record SPacketSyncAnimation(int entityId, String identifier, int index,
                                   boolean isClosing) {

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

  public static void handle(SPacketSyncAnimation msg) {
    ClientLevel world = Minecraft.getInstance().level;

    if (world == null) {
      return;
    }
    Entity entity = world.getEntity(msg.entityId);

    if (!(entity instanceof LivingEntity livingEntity)) {
      return;
    }
    Services.INSTANCE.getShulkerBoxAccessory(livingEntity, msg.identifier, msg.index).ifPresent(
        shulkerBoxAccessory -> shulkerBoxAccessory.setAnimationStatus(
            msg.isClosing ? ShulkerBoxBlockEntity.AnimationStatus.CLOSING :
                ShulkerBoxBlockEntity.AnimationStatus.OPENING));
  }
}
