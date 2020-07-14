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

package top.theillusivec4.curiousshulkerboxes.client;

import io.netty.buffer.Unpooled;
import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosComponent;
import top.theillusivec4.curios.api.type.component.IRenderableCurio;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkPackets;

public class CuriousShulkerBoxesClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    Item[] shulkerBoxes = new Item[]{Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX,
        Items.RED_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX,
        Items.LIME_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX,
        Items.PURPLE_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX};

    for (Item item : shulkerBoxes) {
      ItemComponentCallbackV2.event(item).register(((item1, stack, components) -> components
          .put(CuriosComponent.ITEM_RENDER, new IRenderableCurio() {
            ShulkerEntityModel<?> model = new ShulkerEntityModel<>();

            @Override
            public void render(String identifier, int index, MatrixStack matrixStack,
                VertexConsumerProvider vertexConsumerProvider, int light, LivingEntity livingEntity,
                float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                float netHeadYaw, float headPitch) {
              CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> {

                if (curio instanceof CurioShulkerBox) {
                  CurioShulkerBox curioShulkerBox = (CurioShulkerBox) curio;
                  Direction direction = Direction.SOUTH;
                  DyeColor color = ShulkerBoxBlock.getColor(stack.getItem());
                  SpriteIdentifier spriteIdentifier;

                  if (color == null) {
                    spriteIdentifier = TexturedRenderLayers.SHULKER_TEXTURE_ID;
                  } else {
                    spriteIdentifier = TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES
                        .get(color.getId());
                  }
                  IRenderableCurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
                  IRenderableCurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
                  matrixStack.push();
                  matrixStack.translate(0.5D, 0.5D, 0.5D);
                  float f = 0.45F;
                  matrixStack.scale(f, f, f);
                  matrixStack.multiply(direction.getRotationQuaternion());
                  matrixStack.scale(1.0F, -1.0F, -1.0F);
                  matrixStack.translate(-1.1125D, -0.675D, -0.5D);
                  VertexConsumer vertexConsumer = spriteIdentifier
                      .getVertexConsumer(vertexConsumerProvider,
                          RenderLayer::getEntityCutoutNoCull);
                  model.getBottomShell()
                      .render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                  matrixStack
                      .translate(0.0D, (-curioShulkerBox.getProgress(partialTicks) * 0.5F), 0.0D);
                  matrixStack.multiply(Vector3f.POSITIVE_Y
                      .getDegreesQuaternion(270.0F * curioShulkerBox.getProgress(partialTicks)));
                  model.getTopShell()
                      .render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                  matrixStack.pop();
                }
              });
            }
          })));
    }
    KeyRegistry.registerKeys();
    ClientTickCallback.EVENT.register((client) -> {
      ClientPlayerEntity clientPlayerEntity = client.player;

      if (clientPlayerEntity != null && KeyRegistry.openShulkerBox.wasPressed()) {
        ClientSidePacketRegistry.INSTANCE
            .sendToServer(NetworkPackets.OPEN_SHULKER_BOX, new PacketByteBuf(Unpooled.buffer()));
      }
    });
    ClientNetworkHandler.register();
  }
}
