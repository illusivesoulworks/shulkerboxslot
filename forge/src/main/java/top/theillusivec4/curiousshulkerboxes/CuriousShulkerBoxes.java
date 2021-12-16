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
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curiousshulkerboxes.client.ClientEventListener;
import top.theillusivec4.curiousshulkerboxes.client.CurioShulkerBoxRenderer;
import top.theillusivec4.curiousshulkerboxes.client.KeyRegistry;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkHandler;

@Mod(CuriousShulkerBoxes.MODID)
public class CuriousShulkerBoxes {

  public static final String MODID = "curiousshulkerboxes";

  public CuriousShulkerBoxes() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::enqueue);
  }

  public static boolean isShulkerBox(Item item) {
    Block block = Block.byItem(item);
    return block instanceof ShulkerBoxBlock;
  }

  public static Optional<ImmutableTriple<String, Integer, ItemStack>> getCurioShulkerBox(
      LivingEntity livingEntity) {
    Predicate<ItemStack> shulkerBox = stack -> isShulkerBox(stack.getItem());
    return CuriosApi.getCuriosHelper().findEquippedCurio(shulkerBox, livingEntity);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::attachCapabilities);
    NetworkHandler.register();
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    KeyRegistry.register();
    MinecraftForge.EVENT_BUS.register(new ClientEventListener());
    Item[] shulkers = new Item[] {
        Items.SHULKER_BOX,
        Items.BLACK_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX,
        Items.BROWN_SHULKER_BOX,
        Items.CYAN_SHULKER_BOX,
        Items.GRAY_SHULKER_BOX,
        Items.GREEN_SHULKER_BOX,
        Items.LIGHT_BLUE_SHULKER_BOX,
        Items.LIGHT_GRAY_SHULKER_BOX,
        Items.LIME_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX,
        Items.ORANGE_SHULKER_BOX,
        Items.PINK_SHULKER_BOX,
        Items.PURPLE_SHULKER_BOX,
        Items.RED_SHULKER_BOX,
        Items.WHITE_SHULKER_BOX,
        Items.YELLOW_SHULKER_BOX
    };

    for (Item shulker : shulkers) {
      CuriosRendererRegistry.register(shulker, CurioShulkerBoxRenderer::new);
    }
  }

  private void enqueue(final InterModEnqueueEvent evt) {
    InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
        () -> SlotTypePreset.BACK.getMessageBuilder().build());
  }

  private void attachCapabilities(AttachCapabilitiesEvent<ItemStack> evt) {
    ItemStack stack = evt.getObject();

    if (isShulkerBox(stack.getItem())) {
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
}
