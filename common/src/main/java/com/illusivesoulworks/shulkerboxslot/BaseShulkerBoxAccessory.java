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

package com.illusivesoulworks.shulkerboxslot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

public class BaseShulkerBoxAccessory {

  private static final String ANIMATION_TAG = "Animation";
  private static final String PROGRESS_TAG = "Progress";
  private static final String OLD_PROGRESS_TAG = "OldProgress";

  protected ItemStack stack;

  private ShulkerBoxBlockEntity.AnimationStatus animationStatus =
      ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
  private float progress;
  private float progressOld;

  public BaseShulkerBoxAccessory(ItemStack stack) {
    this.stack = stack;
  }

  public void setAnimationStatus(ShulkerBoxBlockEntity.AnimationStatus status) {
    this.animationStatus = status;
  }

  public float getProgress(float partialTicks) {
    return this.progressOld + (this.progress - this.progressOld) * partialTicks;
  }

  public void tick() {
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

  protected CompoundTag write() {
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

  protected void read(CompoundTag tag) {
    int state = tag.getInt(ANIMATION_TAG);

    switch (state) {
      case 0 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
      case 1 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
      case 2 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSING;
      case 3 -> this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
    }
    this.progress = tag.getFloat(PROGRESS_TAG);
    this.progressOld = tag.getFloat(OLD_PROGRESS_TAG);
  }
}
