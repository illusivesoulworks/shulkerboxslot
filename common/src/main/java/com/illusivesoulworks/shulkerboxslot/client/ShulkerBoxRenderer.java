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

package com.illusivesoulworks.shulkerboxslot.client;

import com.illusivesoulworks.shulkerboxslot.BaseShulkerBoxAccessory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxRenderer {

  private static ShulkerModel<?> model;

  public static void render(PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light,
                            float partialTicks, LivingEntity livingEntity,
                            BaseShulkerBoxAccessory shulkerBoxAccessory, ItemStack stack) {
    Direction direction = Direction.SOUTH;
    DyeColor color = ShulkerBoxBlock.getColorFromItem(stack.getItem());
    Material material;

    if (color == null) {
      material = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
    } else {
      material = Sheets.SHULKER_TEXTURE_LOCATION.get(color.getId());
    }
    poseStack.pushPose();
    poseStack.translate(0.5D, 0.5D, 0.5D);
    float f = 0.45F;
    poseStack.scale(f, f, f);
    poseStack.mulPose(direction.getRotation());
    poseStack.scale(1.0F, -1.0F, -1.0F);
    poseStack.translate(-1.1125D, -0.675D, -0.5D);
    VertexConsumer ivertexbuilder = material
        .buffer(renderTypeBuffer, RenderType::entityCutoutNoCull);

    if (model == null) {
      model = new ShulkerModel<>(
          Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER));
    }
    ModelPart modelpart = model.getLid();
    modelpart.setPos(0.0F, 24.0F - shulkerBoxAccessory.getProgress(partialTicks) * 0.5F * 16.0F,
        0.0F);
    modelpart.yRot =
        270.0F * shulkerBoxAccessory.getProgress(partialTicks) * ((float) Math.PI / 180F);
    model.renderToBuffer(poseStack, ivertexbuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
        1.0F, 1.0F);
    poseStack.popPose();
  }
}
