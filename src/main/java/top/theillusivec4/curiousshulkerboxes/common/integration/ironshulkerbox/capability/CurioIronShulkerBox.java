/*
 * Copyright (C) 2019  C4
 *
 * This file is part of Curious Shulker Boxes, a mod made for Minecraft.
 *
 * Curious Shulker Boxes is free software: you can redistribute it and/or
 * modify it
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
 * License along with Curious Shulker Boxes.  If not, see <https://www.gnu
 * .org/licenses/>.
 */

package top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox.capability;

import com.mojang.blaze3d.platform.GlStateManager;
import com.progwml6.ironshulkerbox.common.blocks.ShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.blocks.ShulkerBoxType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;

public class CurioIronShulkerBox extends CurioShulkerBox {

  private ShulkerBoxType shulkerBoxType;

  public CurioIronShulkerBox(ItemStack stack) {

    super(stack);
    ShulkerBoxType type = ShulkerBoxBlock.getTypeFromItem(stack.getItem());
    this.shulkerBoxType = type != null ? type : ShulkerBoxType.IRON;
  }

  @Override
  public void doRender(String identifier, LivingEntity livingEntity,
                       float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {

    GlStateManager.enableDepthTest();
    GlStateManager.depthFunc(515);
    GlStateManager.depthMask(true);
    GlStateManager.disableCull();
    TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    ICurio.RenderHelper.rotateIfSneaking(livingEntity);
    DyeColor color = ShulkerBoxBlock.getColorFromItem(stack.getItem());

    if (color == null) {
      textureManager.bindTexture(ShulkerRenderer.field_204402_a);
    } else {
      ResourceLocation rs = new ResourceLocation("ironshulkerbox",
                                                 "textures/model/" +
                                                 color.getName() + "/shulker_" +
                                                 color.getName() +
                                                 shulkerBoxType.modelTexture);
      textureManager.bindTexture(rs);
    }

    GlStateManager.pushMatrix();
    GlStateManager.enableRescaleNormal();
    GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    GlStateManager.scalef(1.0F, -1.0F, -1.0F);
    GlStateManager.translatef(0.0F, 1.0F, 0.0F);
    float newScale = 0.45F;
    GlStateManager.scalef(newScale, newScale, newScale);
    GlStateManager.translatef(0.0F, -2.8F, -1.76F);
    GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 1.0f);

    if (!(this.model instanceof ShulkerModel)) {
      this.model = new ShulkerModel<>();
    }

    ShulkerModel model = (ShulkerModel) this.model;
    model.getBase().render(0.0625F);
    GlStateManager.translatef(0.0F, -this.getProgress(partialTicks) * 0.5F,
                              0.0F);
    GlStateManager.rotatef(270.0F * this.getProgress(partialTicks), 0.0F, 1.0F,
                           0.0F);
    model.getLid().render(0.0625F);
    GlStateManager.enableCull();
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
  }
}
