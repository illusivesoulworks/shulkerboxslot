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

import com.illusivesoulworks.shulkerboxslot.common.CurioShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxClientPayloadHandler;
import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxServerPayloadHandler;
import com.illusivesoulworks.shulkerboxslot.common.network.CPayloadOpenShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.network.SPayloadSyncAnimation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.CuriosCapability;

@Mod(ShulkerBoxSlotConstants.MOD_ID)
public class ShulkerBoxSlotNeoForgeMod {

  public static boolean isQuickRightClickLoaded = false;

  public static final DeferredRegister.DataComponents DATA_COMPONENTS =
      DeferredRegister.createDataComponents(ShulkerBoxSlotConstants.MOD_ID);
  public static final DeferredHolder<DataComponentType<?>, DataComponentType<AnimProgressComponent>>
      ANIM_PROGRESS = DATA_COMPONENTS.registerComponentType(
      "animation_progress",
      builder -> builder
          .persistent(AnimProgressComponent.CODEC)
          .networkSynchronized(AnimProgressComponent.STREAM_CODEC)
  );

  public ShulkerBoxSlotNeoForgeMod(IEventBus eventBus) {
    DATA_COMPONENTS.register(eventBus);
    eventBus.addListener(this::setup);
    eventBus.addListener(this::registerPayloadHandler);
    eventBus.addListener(this::registerCaps);
    ShulkerBoxSlotConfig.setup();
  }

  private void setup(final FMLCommonSetupEvent evt) {
    isQuickRightClickLoaded = ModList.get().isLoaded("quickrightclick");
  }

  private void registerPayloadHandler(final RegisterPayloadHandlersEvent evt) {
    evt.registrar(ShulkerBoxSlotConstants.MOD_ID)
        .playToClient(SPayloadSyncAnimation.TYPE, SPayloadSyncAnimation.STREAM_CODEC,
            ShulkerBoxClientPayloadHandler.getInstance()::handle);
    evt.registrar(ShulkerBoxSlotConstants.MOD_ID)
        .playToServer(CPayloadOpenShulkerBox.TYPE, CPayloadOpenShulkerBox.STREAM_CODEC,
            ShulkerBoxServerPayloadHandler.getInstance()::handleOpenShulkerBox);
  }

  private void registerCaps(RegisterCapabilitiesEvent evt) {

    for (Item shulkerBox : ShulkerBoxSlotCommonMod.getShulkerBoxes()) {
      evt.registerItem(CuriosCapability.ITEM, (stack, ctx) -> new CurioShulkerBox(stack),
          shulkerBox);
    }
  }
}