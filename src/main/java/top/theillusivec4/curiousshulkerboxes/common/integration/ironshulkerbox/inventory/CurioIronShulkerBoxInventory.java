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

package top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox.inventory;

import com.progwml6.ironshulkerbox.common.blocks.ShulkerBoxType;
import com.progwml6.ironshulkerbox.common.inventory.ShulkerBoxContainer;
import com.progwml6.ironshulkerbox.common.inventory.ShulkerBoxContainerType;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.curiousshulkerboxes.CuriousShulkerBoxes;
import top.theillusivec4.curiousshulkerboxes.common.inventory.CurioShulkerBoxInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CurioIronShulkerBoxInventory extends CurioShulkerBoxInventory {

  private ShulkerBoxType                     shulkerBoxType;
  private ContainerType<ShulkerBoxContainer> containerType;

  public CurioIronShulkerBoxInventory(ShulkerBoxType type, ItemStack shulkerBox,
                                      String identifier, int index) {

    super(shulkerBox, identifier, index);
    this.shulkerBoxType = type;

    switch (type) {
      case IRON:
        containerType = ShulkerBoxContainerType.IRON_SHULKER_BOX;
        break;
      case GOLD:
        containerType = ShulkerBoxContainerType.GOLD_SHULKER_BOX;
        break;
      case COPPER:
        containerType = ShulkerBoxContainerType.COPPER_SHULKER_BOX;
        break;
      case SILVER:
        containerType = ShulkerBoxContainerType.SILVER_SHULKER_BOX;
        break;
      case CRYSTAL:
        containerType = ShulkerBoxContainerType.CRYSTAL_SHULKER_BOX;
        break;
      case DIAMOND:
        containerType = ShulkerBoxContainerType.DIAMOND_SHULKER_BOX;
        break;
      case OBSIDIAN:
        containerType = ShulkerBoxContainerType.OBSIDIAN_SHULKER_BOX;
        break;
    }

    this.items = NonNullList.withSize(type.size, ItemStack.EMPTY);
  }

  @Override
  public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {

    Block block = ShulkerBoxBlock.getBlockFromItem(stack.getItem());
    return !CuriousShulkerBoxes.isShulkerBox(block);
  }

  @Nullable
  @Override
  public Container createMenu(int i, @Nonnull PlayerInventory playerInventory,
                              @Nonnull PlayerEntity playerEntity) {

    return new ShulkerBoxContainer(this.containerType, i, playerInventory, this,
                                   this.shulkerBoxType);
  }
}
