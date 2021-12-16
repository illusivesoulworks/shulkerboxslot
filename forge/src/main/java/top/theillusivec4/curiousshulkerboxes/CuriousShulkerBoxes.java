/*
 * Copyright (c) 2019-2020 C4
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

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curiousshulkerboxes.client.EventHandlerClient;
import top.theillusivec4.curiousshulkerboxes.client.KeyRegistry;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.integration.enderitemod.EnderiteModIntegration;
import top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox.IronShulkerBoxIntegration;
import top.theillusivec4.curiousshulkerboxes.common.integration.netherite_plus.NetheritePlusIntegration;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkHandler;

@Mod(CuriousShulkerBoxes.MODID)
public class CuriousShulkerBoxes {

  public static final String MODID = "curiousshulkerboxes";

  public static boolean isIronShulkerBoxesLoaded = false;
  public static boolean isNetheritePlusLoaded = false;
  public static boolean isEnderiteLoaded = false;

  public CuriousShulkerBoxes() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::enqueue);
    ModList modList = ModList.get();
    isIronShulkerBoxesLoaded = modList.isLoaded("ironshulkerbox");
    isNetheritePlusLoaded = modList.isLoaded("netherite_plus");
    isEnderiteLoaded = modList.isLoaded("enderitemod");
  }

  public static boolean isShulkerBox(Item item) {
    Block block = Block.getBlockFromItem(item);

    if (isIronShulkerBoxesLoaded && IronShulkerBoxIntegration.isIronShulkerBox(block)) {
      return true;
    } else if (isNetheritePlusLoaded && NetheritePlusIntegration.isNetheriteShulkerBox(block)) {
      return true;
    } else if (isEnderiteLoaded && EnderiteModIntegration.isEnderiteShulkerBox(block)) {
      return true;
    }
    return block instanceof ShulkerBoxBlock;
  }

  public static Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioShulkerBox(
      LivingEntity livingEntity) {

    Predicate<ItemStack> shulkerBox = stack -> isShulkerBox(stack.getItem());
    return CuriosApi.getCuriosHelper().findEquippedCurio(shulkerBox, livingEntity);
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
    InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
        () -> SlotTypePreset.BACK.getMessageBuilder().build());
  }

  @SubscribeEvent
  public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
    ItemStack stack = evt.getObject();

    if (isShulkerBox(stack.getItem())) {
      Block block = Block.getBlockFromItem(stack.getItem());
      ICurio curioShulkerBox;

      if (isIronShulkerBoxesLoaded &&
          IronShulkerBoxIntegration.isIronShulkerBox(block)) {
        curioShulkerBox = IronShulkerBoxIntegration.getCurio(stack);
      } else if (isNetheritePlusLoaded && NetheritePlusIntegration.isNetheriteShulkerBox(block)) {
        curioShulkerBox = NetheritePlusIntegration.getCurio(stack);
      } else if (isEnderiteLoaded && EnderiteModIntegration.isEnderiteShulkerBox(block)) {
        curioShulkerBox = EnderiteModIntegration.getCurio(stack);
      } else {
        curioShulkerBox = new CurioShulkerBox(stack);
      }
      stack.getOrCreateChildTag("BlockEntityTag");

      evt.addCapability(CuriosCapability.ID_ITEM, new ICapabilityProvider() {
        LazyOptional<ICurio> curio = LazyOptional.of(() -> curioShulkerBox);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                                 @Nullable Direction side) {
          return CuriosCapability.ITEM.orEmpty(cap, curio);
        }
      });
    }
  }
}
