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
import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxSlotForgeNetwork;
import com.illusivesoulworks.shulkerboxslot.common.network.SPacketSyncAnimation;
import com.illusivesoulworks.shulkerboxslot.platform.services.ICommonPlatform;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class ForgeCommonPlatform implements ICommonPlatform {

  @Override
  public Optional<Triple<ItemStack, String, Integer>> findShulkerBoxAccessory(
      LivingEntity livingEntity) {
    return CuriosApi.getCuriosHelper().findFirstCurio(livingEntity,
        stack -> ShulkerBoxSlotCommonMod.isShulkerBox(stack.getItem())).map(
        result -> ImmutableTriple.of(result.stack(), result.slotContext().identifier(),
            result.slotContext().index()));
  }

  @Override
  public Optional<BaseShulkerBoxAccessory> getShulkerBoxAccessory(ItemStack stack) {
    ICurio curio = CuriosApi.getCuriosHelper().getCurio(stack).orElse(null);

    if (curio instanceof BaseShulkerBoxAccessory shulkerBoxAccessory) {
      return Optional.of(shulkerBoxAccessory);
    }
    return Optional.empty();
  }

  @Override
  public Optional<BaseShulkerBoxAccessory> getShulkerBoxAccessory(LivingEntity livingEntity,
                                                                  String id, int index) {
    AtomicReference<BaseShulkerBoxAccessory> result = new AtomicReference<>();
    CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity)
        .ifPresent(curiosHandler -> curiosHandler.getStacksHandler(id).ifPresent(stacksHandler -> {
          if (index < stacksHandler.getSlots()) {
            ItemStack stack = stacksHandler.getStacks().getStackInSlot(index);
            CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> {
              if (curio instanceof BaseShulkerBoxAccessory shulkerBoxAccessory) {
                result.set(shulkerBoxAccessory);
              }
            });
          }
        }));
    return Optional.ofNullable(result.get());
  }

  @Override
  public void openScreen(MenuProvider container, ServerPlayer player) {
    NetworkHooks.openScreen(player, container);
  }

  @Override
  public void sendSyncPacket(SPacketSyncAnimation packet, ServerPlayer player) {
    ShulkerBoxSlotForgeNetwork.get()
        .send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
  }
}
