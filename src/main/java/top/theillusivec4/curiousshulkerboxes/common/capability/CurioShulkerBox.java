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

package top.theillusivec4.curiousshulkerboxes.common.capability;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import net.minecraft.block.ShulkerBoxBlock;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import top.theillusivec4.curios.api.capability.ICurio;

public class CurioShulkerBox implements ICurio {

  private static final String ANIMATION_TAG = "Animation";
  private static final String PROGRESS_TAG = "Progress";
  private static final String OLD_PROGRESS_TAG = "OldProgress";

  protected ItemStack stack;
  protected Object model;

  private ShulkerBoxTileEntity.AnimationStatus animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;

  public CurioShulkerBox(ItemStack stack) {
    this.stack = stack;
  }

  public void setAnimationStatus(ShulkerBoxTileEntity.AnimationStatus status) {
    this.animationStatus = status;
  }

  public float getProgress(float partialTicks) {
    return this.progressOld + (this.progress - this.progressOld) * partialTicks;
  }

  @Override
  public void onCurioTick(String identifier, int index, LivingEntity livingEntity) {
    this.progressOld = this.progress;

    switch (this.animationStatus) {
      case CLOSED:
        this.progress = 0.0F;
        break;
      case OPENING:
        this.progress += 0.1F;
        if (this.progress >= 1.0F) {
          this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENED;
          this.progress = 1.0F;
        }
        break;
      case CLOSING:
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
          this.progress = 0.0F;
        }
        break;
      case OPENED:
        this.progress = 1.0F;
    }
  }

  @Override
  public void playEquipSound(LivingEntity livingEntity) {
    livingEntity.world
        .playSound(null, livingEntity.getPosition(), SoundEvents.BLOCK_SHULKER_BOX_CLOSE,
            SoundCategory.NEUTRAL, 1.0F, 1.0F);
  }

  @Override
  public boolean canRightClickEquip() {
    return true;
  }

  @Override
  public boolean shouldSyncToTracking(String identifier, LivingEntity livingEntity) {
    return true;
  }

  @Nonnull
  @Override
  public CompoundNBT getSyncTag() {
    CompoundNBT compound = new CompoundNBT();
    int state = 0;

    switch (this.animationStatus) {
      case OPENING:
        state = 1;
        break;
      case CLOSING:
        state = 2;
        break;
      case OPENED:
        state = 3;
    }
    compound.putInt(ANIMATION_TAG, state);
    compound.putFloat(PROGRESS_TAG, this.progress);
    compound.putFloat(OLD_PROGRESS_TAG, this.progressOld);
    return compound;
  }

  @Override
  public void readSyncTag(CompoundNBT compound) {
    int state = compound.getInt(ANIMATION_TAG);

    switch (state) {
      case 0:
        this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
        break;
      case 1:
        this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENING;
        break;
      case 2:
        this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSING;
        break;
      case 3:
        this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENED;
    }
    this.progress = compound.getFloat(PROGRESS_TAG);
    this.progressOld = compound.getFloat(OLD_PROGRESS_TAG);
  }

  @Override
  public boolean hasRender(String identifier, LivingEntity livingEntity) {
    return true;
  }

  @Override
  public void render(String identifier, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer,
      int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount,
      float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    Direction direction = Direction.SOUTH;
    DyeColor color = ShulkerBoxBlock.getColorFromItem(stack.getItem());
    Material material;

    if (color == null) {
      material = Atlases.DEFAULT_SHULKER_TEXTURE;
    } else {
      material = Atlases.SHULKER_TEXTURES.get(color.getId());
    }
    ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
    ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
    matrixStack.push();
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    float f = 0.45F;
    matrixStack.scale(f, f, f);
    matrixStack.rotate(direction.getRotation());
    matrixStack.scale(1.0F, -1.0F, -1.0F);
    matrixStack.translate(-1.1125D, -0.675D, -0.5D);
    IVertexBuilder ivertexbuilder = material
        .getBuffer(renderTypeBuffer, RenderType::entityCutoutNoCull);

    if (!(this.model instanceof ShulkerModel)) {
      this.model = new ShulkerModel<>();
    }
    ShulkerModel<?> model = (ShulkerModel<?>) this.model;
    model.getBase().render(matrixStack, ivertexbuilder, light, OverlayTexture.DEFAULT_LIGHT);
    matrixStack.translate(0.0D, (-this.getProgress(partialTicks) * 0.5F), 0.0D);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(270.0F * this.getProgress(partialTicks)));
    model.getLid().render(matrixStack, ivertexbuilder, light, OverlayTexture.DEFAULT_LIGHT);
    matrixStack.pop();
  }
}
