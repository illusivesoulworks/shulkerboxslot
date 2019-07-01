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

package top.theillusivec4.curiousshulkerboxes.common.capability;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.SoundCategory;
import top.theillusivec4.curios.api.capability.ICurio;

import javax.annotation.Nonnull;

public class CurioShulkerBox implements ICurio {

    private ItemStack stack;
    private TileEntityShulkerBox.AnimationStatus animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
    private float progress;
    private float progressOld;
    private Object model;

    public CurioShulkerBox(ItemStack stack) {
        this.stack = stack;
    }

    public void setAnimationStatus(TileEntityShulkerBox.AnimationStatus status) {
        this.animationStatus = status;
    }

    public float getProgress(float partialTicks) {
        return this.progressOld + (this.progress - this.progressOld) * partialTicks;
    }

    @Override
    public void onCurioTick(String identifier, EntityLivingBase entityLivingBase) {
        this.progressOld = this.progress;

        switch(this.animationStatus) {
            case CLOSED:
                this.progress = 0.0F;
                break;
            case OPENING:
                this.progress += 0.1F;
                if (this.progress >= 1.0F) {
                    this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENED;
                    this.progress = 1.0F;
                }
                break;
            case CLOSING:
                this.progress -= 0.1F;
                if (this.progress <= 0.0F) {
                    this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
                    this.progress = 0.0F;
                }
                break;
            case OPENED:
                this.progress = 1.0F;
        }
    }

    @Override
    public void playEquipSound(EntityLivingBase entityLivingBase) {
        entityLivingBase.world.playSound(null, entityLivingBase.getPosition(), SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    @Override
    public boolean canRightClickEquip() {
        return true;
    }

    @Override
    public boolean shouldSyncToTracking(String identifier, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Nonnull
    @Override
    public NBTTagCompound getSyncTag() {
        NBTTagCompound compound = new NBTTagCompound();
        int state = 0;

        switch(this.animationStatus) {
            case OPENING:
                state = 1;
                break;
            case CLOSING:
                state = 2;
                break;
            case OPENED:
                state = 3;
        }
        compound.putInt("Animation", state);
        compound.putFloat("Progress", this.progress);
        compound.putFloat("OldProgress", this.progressOld);
        return compound;
    }

    @Override
    public void readSyncTag(NBTTagCompound compound) {
        int state = compound.getInt("Animation");

        switch(state) {
            case 0:
                this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSED;
                break;
            case 1:
                this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENING;
                break;
            case 2:
                this.animationStatus = TileEntityShulkerBox.AnimationStatus.CLOSING;
                break;
            case 3:
                this.animationStatus = TileEntityShulkerBox.AnimationStatus.OPENED;
        }
        this.progress = compound.getFloat("Progress");
        this.progressOld = compound.getFloat("OldProgress");
    }

    @Override
    public boolean hasRender(String identifier, EntityLivingBase entityLivingBase) {
        return true;
    }

    @Override
    public void doRender(String identifier, EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        ICurio.RenderHelper.rotateIfSneaking(entitylivingbaseIn);
        EnumDyeColor color = BlockShulkerBox.getColorFromItem(stack.getItem());
        if (color == null) {
            textureManager.bindTexture(RenderShulker.field_204402_a);
        } else {
            textureManager.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[color.getId()]);
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.scalef(1.0F, -1.0F, -1.0F);
        GlStateManager.translatef(0.0F, 1.0F, 0.0F);
        float f = 0.45F;
        GlStateManager.scalef(f, f, f);
        GlStateManager.translatef(0.0F, -2.8F, -1.76F);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 1.0f);

        if (!(this.model instanceof ModelShulker)) {
            this.model = new ModelShulker();
        }
        ModelShulker model = (ModelShulker) this.model;
        model.getBase().render(0.0625F);
        GlStateManager.translatef(0.0F, -this.getProgress(partialTicks) * 0.5F, 0.0F);
        GlStateManager.rotatef(270.0F * this.getProgress(partialTicks), 0.0F, 1.0F, 0.0F);
        model.getLid().render(0.0625F);
        GlStateManager.enableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
