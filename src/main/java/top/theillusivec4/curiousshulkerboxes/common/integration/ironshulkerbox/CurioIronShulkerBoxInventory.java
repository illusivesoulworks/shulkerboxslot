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

import com.progwml6.ironshulkerbox.common.block.IronShulkerBoxesTypes;
import com.progwml6.ironshulkerbox.common.inventory.IronShulkerBoxContainer;
import com.progwml6.ironshulkerbox.common.inventory.IronShulkerBoxesContainerTypes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

public class CurioIronShulkerBoxInventory extends CurioShulkerBoxInventory {

  private IronShulkerBoxesTypes shulkerBoxType;
  private ContainerType<IronShulkerBoxContainer> containerType;

  public CurioIronShulkerBoxInventory(IronShulkerBoxesTypes type, ItemStack shulkerBox,
                                      String identifier, int index) {
    super(shulkerBox, identifier, index);
    this.shulkerBoxType = type;

    switch (type) {
      case GOLD:
        containerType = IronShulkerBoxesContainerTypes.GOLD_SHULKER_BOX.get();
        break;
      case COPPER:
        containerType = IronShulkerBoxesContainerTypes.COPPER_SHULKER_BOX.get();
        break;
      case SILVER:
        containerType = IronShulkerBoxesContainerTypes.SILVER_SHULKER_BOX.get();
        break;
      case CRYSTAL:
        containerType = IronShulkerBoxesContainerTypes.CRYSTAL_SHULKER_BOX.get();
        break;
      case DIAMOND:
        containerType = IronShulkerBoxesContainerTypes.DIAMOND_SHULKER_BOX.get();
        break;
      case OBSIDIAN:
        containerType = IronShulkerBoxesContainerTypes.OBSIDIAN_SHULKER_BOX.get();
        break;
      default:
        containerType = IronShulkerBoxesContainerTypes.IRON_SHULKER_BOX.get();
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
    return new IronShulkerBoxContainer(this.containerType, i, playerInventory, this,
        this.shulkerBoxType);
  }
}