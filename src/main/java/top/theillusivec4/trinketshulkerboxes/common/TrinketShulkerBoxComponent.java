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

package top.theillusivec4.trinketshulkerboxes.common;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;

public class TrinketShulkerBoxComponent extends ItemComponent {

  protected final ItemStack stack;

  private ShulkerBoxBlockEntity.AnimationStage animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
  private float animationProgress;
  private float prevAnimationProgress;

  public TrinketShulkerBoxComponent(ItemStack stack) {
    super(stack);
    this.stack = stack;
    this.stack.getOrCreateSubTag("BlockEntityTag");
  }

  public void setAnimationStage(ShulkerBoxBlockEntity.AnimationStage stage) {
    this.animationStage = stage;
  }

  public float getAnimationProgress(float delta) {
    return this.prevAnimationProgress
        + (this.animationProgress - this.prevAnimationProgress) * delta;
  }

  public void tick() {
    this.prevAnimationProgress = this.animationProgress;

    switch (this.animationStage) {
      case CLOSED -> this.animationProgress = 0.0F;
      case OPENING -> {
        this.animationProgress += 0.1F;
        if (this.animationProgress >= 1.0F) {
          this.animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENED;
          this.animationProgress = 1.0F;
        }
      }
      case CLOSING -> {
        this.animationProgress -= 0.1F;
        if (this.animationProgress <= 0.0F) {
          this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
          this.animationProgress = 0.0F;
        }
      }
      case OPENED -> this.animationProgress = 1.0F;
    }
  }
}
