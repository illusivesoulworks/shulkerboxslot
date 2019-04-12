/*
 * Copyright (C) 2019  C4
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

package top.theillusivec4.curiousshulkerboxes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.CuriosRegistry;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;
import top.theillusivec4.curiousshulkerboxes.client.EventHandlerClient;
import top.theillusivec4.curiousshulkerboxes.client.KeyRegistry;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(CuriousShulkerBoxes.MODID)
public class CuriousShulkerBoxes {

    public static final String MODID = "curiousshulkerboxes";

    public CuriousShulkerBoxes() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::enqueue);
    }

    private void setup(final FMLCommonSetupEvent evt) {
        MinecraftForge.EVENT_BUS.register(this);
        NetworkHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent evt) {
        KeyRegistry.register();
        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
    }

    private void enqueue(final InterModEnqueueEvent evt) {
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("back"));
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
        ItemStack stack = evt.getObject();

        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox) {
            CurioShulkerBox curioShulkerBox = new CurioShulkerBox(stack);
            evt.addCapability(CuriosCapability.ID_ITEM, new ICapabilityProvider() {
                LazyOptional<ICurio> curio = LazyOptional.of(() -> curioShulkerBox);

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
                    return CuriosCapability.ITEM.orEmpty(cap, curio);
                }
            });
        }
    }
}
