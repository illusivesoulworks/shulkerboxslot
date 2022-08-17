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

package com.illusivesoulworks.shulkerboxslot.platform.services;

import com.illusivesoulworks.shulkerboxslot.BaseShulkerBoxAccessory;
import com.illusivesoulworks.shulkerboxslot.common.network.SPacketSyncAnimation;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

public interface ICommonPlatform {

  Optional<Triple<ItemStack, String, Integer>> findShulkerBoxAccessory(LivingEntity livingEntity);

  Optional<BaseShulkerBoxAccessory> getShulkerBoxAccessory(ItemStack stack);

  Optional<BaseShulkerBoxAccessory> getShulkerBoxAccessory(LivingEntity livingEntity, String id,
                                                           int index);

  void openScreen(MenuProvider container, ServerPlayer player);

  void sendSyncPacket(SPacketSyncAnimation packet, ServerPlayer player);
}
