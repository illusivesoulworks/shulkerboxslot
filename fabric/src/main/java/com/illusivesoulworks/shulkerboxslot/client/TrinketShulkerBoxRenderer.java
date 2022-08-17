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

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotFabricMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TrinketShulkerBoxRenderer implements TrinketRenderer {

  @Override
  public void render(ItemStack stack, SlotReference slotReference,
                     EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack,
                     MultiBufferSource multiBufferSource, int i, LivingEntity livingEntity, float v,
                     float v1, float v2, float v3, float v4, float v5) {
    ShulkerBoxSlotFabricMod.getShulkerBoxComponent(stack).ifPresent(component -> {

      if (livingEntity.isCrouching() && !entityModel.riding && !livingEntity.isSwimming()) {
        poseStack.translate(0.0F, 0.2F, 0.0F);

        if (entityModel instanceof HumanoidModel bipedEntityModel) {
          poseStack.mulPose(Vector3f.XP.rotation(bipedEntityModel.body.xRot));
        }
      }
      ShulkerBoxRenderer.render(poseStack, multiBufferSource, i, v2, livingEntity,
          component.getShulkerBoxAccessory(), stack);
    });
  }
}
