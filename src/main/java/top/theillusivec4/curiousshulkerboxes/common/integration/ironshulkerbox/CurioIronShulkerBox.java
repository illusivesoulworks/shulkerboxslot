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

package top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.progwml6.ironshulkerbox.client.tileentity.IronShulkerBoxesModels;
import com.progwml6.ironshulkerbox.common.block.GenericIronShulkerBlock;
import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;

public class CurioIronShulkerBox extends CurioShulkerBox {

  private IronShulkerBoxesTypes shulkerBoxType;

  public CurioIronShulkerBox(ItemStack stack) {
    super(stack);
    this.shulkerBoxType = GenericIronShulkerBlock.getTypeFromItem(stack.getItem());
  }

  @Override
  public void render(String identifier, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer,
      int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount,
      float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    Direction direction = Direction.SOUTH;
    ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
    ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
    DyeColor color = GenericIronShulkerBlock.getColorFromItem(stack.getItem());

    if (color == null) {
      color = DyeColor.WHITE;
    }
    Material material = new Material(Atlases.SHULKER_BOX_ATLAS,
        IronShulkerBoxesModels.chooseShulkerBoxModel(this.shulkerBoxType, color.getId()));

    if (!(this.model instanceof ShulkerModel)) {
      this.model = new ShulkerModel<>();
    }
    ShulkerModel<?> model = (ShulkerModel<?>) this.model;
    matrixStack.push();
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    float f = 0.45F;
    matrixStack.scale(f, f, f);
    matrixStack.rotate(direction.getRotation());
    matrixStack.scale(1.0F, -1.0F, -1.0F);
    matrixStack.translate(-1.1125D, -0.675D, -0.5D);
    IVertexBuilder ivertexbuilder = material
        .getBuffer(renderTypeBuffer, RenderType::entityCutoutNoCull);
    model.getBase().render(matrixStack, ivertexbuilder, light, OverlayTexture.DEFAULT_LIGHT);
    matrixStack.translate(0.0D, -this.getProgress(partialTicks) * 0.5F, 0.0D);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(270.0F * this.getProgress(partialTicks)));
    model.getLid().render(matrixStack, ivertexbuilder, light, OverlayTexture.DEFAULT_LIGHT);
    matrixStack.pop();
  }
}
