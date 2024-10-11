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

import com.illusivesoulworks.shulkerboxslot.AnimProgressComponent;
import com.illusivesoulworks.shulkerboxslot.BaseShulkerBoxAccessory;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotCommonMod;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotFabricMod;
import com.illusivesoulworks.shulkerboxslot.common.network.SPayloadSyncAnimation;
import com.illusivesoulworks.shulkerboxslot.platform.services.ICommonPlatform;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

public class FabricCommonPlatform implements ICommonPlatform {

  @Override
  public DataComponentType<AnimProgressComponent> getAnimationComponent() {
    return ShulkerBoxSlotFabricMod.ANIM_PROGRESS;
  }

  private static final Triple<ItemStack, String, Integer> EMPTY =
      ImmutableTriple.of(ItemStack.EMPTY, "", 0);

  @Override
  public Triple<ItemStack, String, Integer> findShulkerBoxAccessory(LivingEntity livingEntity) {
    AtomicReference<Triple<ItemStack, String, Integer>> result = new AtomicReference<>();
    result.set(EMPTY);
    TrinketsApi.getTrinketComponent(livingEntity).ifPresent(trinketComponent -> {
      List<Tuple<SlotReference, ItemStack>> list = trinketComponent.getEquipped(
          stack -> ShulkerBoxSlotCommonMod.isShulkerBox(stack.getItem()));

      if (!list.isEmpty()) {
        Tuple<SlotReference, ItemStack> res = list.getFirst();
        SlotType slotType = res.getA().inventory().getSlotType();
        result.set(ImmutableTriple.of(res.getB(), slotType.getGroup() + ":" + slotType.getName(),
            res.getA().index()));
      }
    });
    return result.get();
  }

  @Override
  public ItemStack getShulkerBoxAccessory(LivingEntity livingEntity, String id, int index) {
    ItemStack[] result = new ItemStack[] {ItemStack.EMPTY};
    String[] ids = id.split(":");

    if (ids.length == 2) {
      TrinketsApi.getTrinketComponent(livingEntity).ifPresent(trinketComponent -> {
        Map<String, Map<String, TrinketInventory>> inv = trinketComponent.getInventory();
        Map<String, TrinketInventory> group = inv.get(ids[0]);

        if (group != null) {
          TrinketInventory slotInv = group.get(ids[1]);

          if (index < slotInv.getContainerSize()) {
            ItemStack stack = slotInv.getItem(index);
            Trinket trinket = TrinketsApi.getTrinket(stack.getItem());

            if (trinket instanceof BaseShulkerBoxAccessory) {
              result[0] = stack;
            }
          }
        }
      });
    }
    return result[0];
  }

  @Override
  public void sendSyncPacket(SPayloadSyncAnimation packet, ServerPlayer player) {
    ServerPlayNetworking.send(player, packet);
    PlayerLookup.tracking(player).forEach(player1 -> ServerPlayNetworking.send(player1, packet));
  }

  private static Boolean isElytraSlotLoaded = null;

  @Override
  public boolean isElytraSlotLoaded() {

    if (isElytraSlotLoaded == null) {
      isElytraSlotLoaded = FabricLoader.getInstance().isModLoaded("elytraslot");
    }
    return isElytraSlotLoaded;
  }
}
