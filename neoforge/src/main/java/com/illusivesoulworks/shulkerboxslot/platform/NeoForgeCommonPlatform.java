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
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotNeoForgeMod;
import com.illusivesoulworks.shulkerboxslot.common.network.SPayloadSyncAnimation;
import com.illusivesoulworks.shulkerboxslot.platform.services.ICommonPlatform;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

public class NeoForgeCommonPlatform implements ICommonPlatform {

  @Override
  public DataComponentType<AnimProgressComponent> getAnimationComponent() {
    return ShulkerBoxSlotNeoForgeMod.ANIM_PROGRESS.get();
  }

  private static final Triple<ItemStack, String, Integer> EMPTY =
      ImmutableTriple.of(ItemStack.EMPTY, "", 0);

  @Override
  public Triple<ItemStack, String, Integer> findShulkerBoxAccessory(LivingEntity livingEntity) {
    SlotResult slotResult = CuriosApi.getCuriosInventory(livingEntity).flatMap(
            inv -> inv.findFirstCurio(stack -> ShulkerBoxSlotCommonMod.isShulkerBox(stack.getItem())))
        .orElse(null);

    if (slotResult != null) {
      SlotContext slotContext = slotResult.slotContext();
      return ImmutableTriple.of(slotResult.stack(), slotContext.identifier(), slotContext.index());
    }
    return EMPTY;
  }

  @Override
  public ItemStack getShulkerBoxAccessory(LivingEntity livingEntity, String id, int index) {
    ItemStack[] result = new ItemStack[] {ItemStack.EMPTY};
    CuriosApi.getCuriosInventory(livingEntity)
        .flatMap(curiosHandler -> curiosHandler.getStacksHandler(id)).ifPresent(stacksHandler -> {
          if (index < stacksHandler.getSlots()) {
            ItemStack stack = stacksHandler.getStacks().getStackInSlot(index);
            CuriosApi.getCurio(stack).ifPresent(curio -> {
              if (curio instanceof BaseShulkerBoxAccessory) {
                result[0] = stack;
              }
            });
          }
        });
    return result[0];
  }

  @Override
  public void sendSyncPacket(SPayloadSyncAnimation packet, ServerPlayer player) {
    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
  }

  private static Boolean isElytraSlotLoaded = null;

  @Override
  public boolean isElytraSlotLoaded() {

    if (isElytraSlotLoaded == null) {
      isElytraSlotLoaded = ModList.get().isLoaded("elytraslot");
    }
    return isElytraSlotLoaded;
  }
}
