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

import com.illusivesoulworks.shulkerboxslot.BaseShulkerBoxAccessory;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotCommonMod;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotQuiltMod;
import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxSlotPackets;
import com.illusivesoulworks.shulkerboxslot.common.TrinketShulkerBoxComponent;
import com.illusivesoulworks.shulkerboxslot.common.network.SPacketSyncAnimation;
import com.illusivesoulworks.shulkerboxslot.platform.services.ICommonPlatform;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class QuiltCommonPlatform implements ICommonPlatform {

  @Override
  public Optional<Triple<ItemStack, String, Integer>> findShulkerBoxAccessory(
      LivingEntity livingEntity) {
    AtomicReference<Triple<ItemStack, String, Integer>> result = new AtomicReference<>();
    TrinketsApi.getTrinketComponent(livingEntity).ifPresent(trinketComponent -> {
      List<Tuple<SlotReference, ItemStack>> list = trinketComponent.getEquipped(
          stack -> ShulkerBoxSlotCommonMod.isShulkerBox(stack.getItem()));

      if (!list.isEmpty()) {
        Tuple<SlotReference, ItemStack> res = list.get(0);
        SlotType slotType = res.getA().inventory().getSlotType();
        result.set(ImmutableTriple.of(res.getB(), slotType.getGroup() + ":" + slotType.getName(),
            res.getA().index()));
      }
    });
    return Optional.ofNullable(result.get());
  }

  @Override
  public Optional<BaseShulkerBoxAccessory> getShulkerBoxAccessory(ItemStack stack) {
    TrinketShulkerBoxComponent component =
        ShulkerBoxSlotQuiltMod.getShulkerBoxComponent(stack).orElse(null);

    if (component != null) {
      return Optional.of(component.getShulkerBoxAccessory());
    }
    return Optional.empty();
  }

  @Override
  public Optional<BaseShulkerBoxAccessory> getShulkerBoxAccessory(LivingEntity livingEntity,
                                                                  String id, int index) {
    AtomicReference<BaseShulkerBoxAccessory> result = new AtomicReference<>();
    String[] ids = id.split(":");

    if (ids.length == 2) {
      TrinketsApi.getTrinketComponent(livingEntity).ifPresent(trinketComponent -> {
        Map<String, Map<String, TrinketInventory>> inv = trinketComponent.getInventory();
        Map<String, TrinketInventory> group = inv.get(ids[0]);

        if (group != null) {
          TrinketInventory slotInv = group.get(ids[1]);

          if (index < slotInv.getContainerSize()) {
            ItemStack stack = slotInv.getItem(index);
            ShulkerBoxSlotQuiltMod.getShulkerBoxComponent(stack)
                .ifPresent(component -> result.set(component.getShulkerBoxAccessory()));
          }
        }
      });
    }
    return Optional.ofNullable(result.get());
  }

  @Override
  public void openScreen(MenuProvider container, ServerPlayer player) {
    player.openMenu(container);
  }

  @Override
  public void sendSyncPacket(SPacketSyncAnimation packet, ServerPlayer player) {
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketSyncAnimation.encode(packet, buf);
    ServerPlayNetworking.send(player, ShulkerBoxSlotPackets.SYNC_SHULKER_BOX, buf);
    PlayerLookup.tracking(player).forEach(
        player1 -> ServerPlayNetworking.send(player1, ShulkerBoxSlotPackets.SYNC_SHULKER_BOX, buf));
  }
}
