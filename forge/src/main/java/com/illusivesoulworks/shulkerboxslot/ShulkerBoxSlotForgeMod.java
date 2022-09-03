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

import com.illusivesoulworks.shulkerboxslot.client.CurioShulkerBoxRenderer;
import com.illusivesoulworks.shulkerboxslot.client.ForgeClientEventListener;
import com.illusivesoulworks.shulkerboxslot.client.ShulkerBoxSlotKeyRegistry;
import com.illusivesoulworks.shulkerboxslot.common.CurioShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.ShulkerBoxSlotForgeNetwork;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICurio;

@Mod(ShulkerBoxSlotConstants.MOD_ID)
public class ShulkerBoxSlotForgeMod {

  public ShulkerBoxSlotForgeMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::enqueue);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::attachCapabilities);
    ShulkerBoxSlotForgeNetwork.setup();
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    ForgeClientEventListener.setup();

    for (Item shulkerBox : ShulkerBoxSlotCommonMod.getShulkerBoxes()) {
      CuriosRendererRegistry.register(shulkerBox, CurioShulkerBoxRenderer::new);
    }
  }

  private void enqueue(final InterModEnqueueEvent evt) {
    InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
        () -> SlotTypePreset.BACK.getMessageBuilder().build());
  }

  private void attachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
    ItemStack stack = evt.getObject();

    if (ShulkerBoxSlotCommonMod.isShulkerBox(stack.getItem())) {
      ICurio curioShulkerBox = new CurioShulkerBox(stack);
      stack.getOrCreateTagElement("BlockEntityTag");

      evt.addCapability(CuriosCapability.ID_ITEM, new ICapabilityProvider() {
        final LazyOptional<ICurio> curio = LazyOptional.of(() -> curioShulkerBox);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                                 @Nullable Direction side) {
          return CuriosCapability.ITEM.orEmpty(cap, curio);
        }
      });
    }
  }

  @Mod.EventBusSubscriber(modid = ShulkerBoxSlotConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientModEvents {

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent evt) {
      ShulkerBoxSlotKeyRegistry.setup();
      evt.register(ShulkerBoxSlotKeyRegistry.openShulkerBox);
    }
  }
}