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

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CurioShulkerBox implements ICurio {

  private static final String ANIMATION_TAG = "Animation";
  private static final String PROGRESS_TAG = "Progress";
  private static final String OLD_PROGRESS_TAG = "OldProgress";

  protected ItemStack stack;
  protected Object model;

  private ShulkerBoxBlockEntity.AnimationStatus animationStatus =
      ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;

  public CurioShulkerBox(ItemStack stack) {
    this.stack = stack;
  }

  public void setAnimationStatus(ShulkerBoxBlockEntity.AnimationStatus status) {
    this.animationStatus = status;
  }

  public float getProgress(float partialTicks) {
    return this.progressOld + (this.progress - this.progressOld) * partialTicks;
  }

  @Override
  public ItemStack getStack() {
    return this.stack;
  }

  @Override
  public void curioTick(SlotContext slotContext) {
    this.progressOld = this.progress;

    switch (this.animationStatus) {
      case CLOSED -> this.progress = 0.0F;
      case OPENING -> {
        this.progress += 0.1F;
        if (this.progress >= 1.0F) {
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
          this.progress = 1.0F;
        }
      }
      case CLOSING -> {
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
          this.progress = 0.0F;
        }
      }
      case OPENED -> this.progress = 1.0F;
    }
  }

  @Nonnull
  @Override
  public SoundInfo getEquipSound(SlotContext slotContext) {
    return new SoundInfo(SoundEvents.SHULKER_BOX_CLOSE, 1.0F, 1.0F);
  }

  @Override
  public boolean canEquipFromUse(SlotContext slotContext) {
    return true;
  }

  @Override
  public boolean canSync(SlotContext slotContext) {
    return true;
  }

  @Nonnull
  @Override
  public CompoundTag writeSyncData(SlotContext slotContext) {
    CompoundTag compound = new CompoundTag();
    int state = switch (this.animationStatus) {
      case OPENING -> 1;
      case CLOSING -> 2;
      case OPENED -> 3;
      default -> 0;
    };
    compound.putInt(ANIMATION_TAG, state);
    compound.putFloat(PROGRESS_TAG, this.progress);
    compound.putFloat(OLD_PROGRESS_TAG, this.progressOld);
    return compound;
  }

  @Override
  public void readSyncData(SlotContext slotContext, CompoundTag compound) {
    int state = compound.getInt(ANIMATION_TAG);

    switch (state) {
      case 0 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
      case 1 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
      case 2 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSING;
      case 3 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
    }
    this.progress = compound.getFloat(PROGRESS_TAG);
    this.progressOld = compound.getFloat(OLD_PROGRESS_TAG);
  }
}
