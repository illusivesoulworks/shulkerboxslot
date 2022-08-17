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

package com.illusivesoulworks.shulkerboxslot;

import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxSlotPackets;
import com.illusivesoulworks.shulkerboxslot.common.TrinketShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.TrinketShulkerBoxComponent;
import com.illusivesoulworks.shulkerboxslot.common.network.CPacketOpenShulkerBox;
import dev.emi.trinkets.api.TrinketsApi;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxSlotFabricMod implements ModInitializer, ItemComponentInitializer {

  private static final ComponentKey<TrinketShulkerBoxComponent> TRINKET_SHULKER_BOX_COMPONENT =
      ComponentRegistry.getOrCreate(
          new ResourceLocation(ShulkerBoxSlotConstants.MOD_ID, "shulker_box"),
          TrinketShulkerBoxComponent.class);

  public static Optional<TrinketShulkerBoxComponent> getShulkerBoxComponent(ItemStack stack) {
    try {
      return TRINKET_SHULKER_BOX_COMPONENT.maybeGet(stack);
    } catch (IllegalStateException e) {
      ShulkerBoxSlotConstants.LOG.error("Cannot obtain component for shulker box!");
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public void onInitialize() {

    for (Item shulkerBox : ShulkerBoxSlotCommonMod.getShulkerBoxes()) {
      TrinketsApi.registerTrinket(shulkerBox, new TrinketShulkerBox());
    }
    ServerPlayNetworking.registerGlobalReceiver(ShulkerBoxSlotPackets.OPEN_SHULKER_BOX,
        (server, player, handler, buf, responseSender) -> server.execute(
            () -> CPacketOpenShulkerBox.handle(null, player)));
  }

  @Override
  public void registerItemComponentFactories(@Nonnull ItemComponentFactoryRegistry registry) {

    for (Item shulkerBox : ShulkerBoxSlotCommonMod.getShulkerBoxes()) {
      registry.register(shulkerBox, TRINKET_SHULKER_BOX_COMPONENT, TrinketShulkerBoxComponent::new);
    }
  }
}
