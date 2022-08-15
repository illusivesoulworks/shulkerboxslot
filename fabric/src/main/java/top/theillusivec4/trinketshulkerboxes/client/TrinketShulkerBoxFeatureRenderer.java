/*
 * Copyright (c) 2019-2021 C4
 *
 * This file is part of Trinket Shulker Boxes, a mod made for Minecraft.
 *
 * Trinket Shulker Boxes is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trinket Shulker Boxes is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Trinket Shulker Boxes. If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.trinketshulkerboxes.client;

import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import java.util.Map;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import top.theillusivec4.trinketshulkerboxes.common.TrinketShulkerBoxesMod;

public class TrinketShulkerBoxFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
    extends FeatureRenderer<T, M> {

  private final ShulkerEntityModel<?> model;

  public TrinketShulkerBoxFeatureRenderer(FeatureRendererContext<T, M> ctx,
                                          EntityModelLoader loader) {
    super(ctx);
    this.model = new ShulkerEntityModel<>(loader.getModelPart(EntityModelLayers.SHULKER));
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                     T entity, float limbAngle, float limbDistance, float tickDelta,
                     float animationProgress, float headYaw, float headPitch) {
    TrinketsApi.getTrinketComponent(entity).ifPresent(component -> {

      for (Map.Entry<String, Map<String, TrinketInventory>> group : component
          .getInventory().entrySet()) {

        for (Map.Entry<String, TrinketInventory> slotType : group.getValue()
            .entrySet()) {
          TrinketInventory inv = slotType.getValue();

          for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            try {
              TrinketShulkerBoxesMod.TRINKET_SHULKER_BOX_COMPONENT.maybeGet(stack)
                  .ifPresent(trinket -> {
                    Direction direction = Direction.SOUTH;
                    DyeColor color = ShulkerBoxBlock.getColor(stack.getItem());
                    SpriteIdentifier spriteIdentifier;

                    if (color == null) {
                      spriteIdentifier = TexturedRenderLayers.SHULKER_TEXTURE_ID;
                    } else {
                      spriteIdentifier =
                          TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(color.getId());
                    }

                    if (entity.isInSneakingPose() && !this.getContextModel().riding &&
                        !entity.isSwimming()) {
                      matrices.translate(0.0F, 0.2F, 0.0F);

                      if (this.getContextModel() instanceof BipedEntityModel bipedEntityModel) {
                        matrices.multiply(
                            Vec3f.POSITIVE_X.getDegreesQuaternion(
                                bipedEntityModel.body.pitch * TrinketRenderer.MAGIC_ROTATION));
                      }
                    }
                    matrices.push();
                    matrices.translate(0.5D, 0.5D, 0.5D);
                    float f = 0.45F;
                    matrices.scale(f, f, f);
                    matrices.multiply(direction.getRotationQuaternion());
                    matrices.scale(1.0F, -1.0F, -1.0F);
                    matrices.translate(-1.1125D, -0.675D, -0.5D);
                    ModelPart modelPart = this.model.getLid();
                    modelPart
                        .setPivot(0.0F,
                            24.0F - trinket.getAnimationProgress(tickDelta) * 0.5F * 16.0F, 0.0F);
                    modelPart.yaw = 270.0F * trinket.getAnimationProgress(tickDelta) * 0.017453292F;
                    VertexConsumer vertexConsumer = spriteIdentifier
                        .getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
                    this.model
                        .render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F,
                            1.0F, 1.0F, 1.0F);
                    matrices.pop();
                  });
            } catch (IllegalStateException e) {
              TrinketShulkerBoxesMod.LOGGER.error("Cannot obtain component for shulker box!");
              e.printStackTrace();
            }
          }
        }
      }
    });
  }
}
