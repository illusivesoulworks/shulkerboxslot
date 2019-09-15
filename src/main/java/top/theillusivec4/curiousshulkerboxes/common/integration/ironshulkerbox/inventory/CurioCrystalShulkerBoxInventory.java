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
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox.capability.CurioCrystalShulkerBox;

public class CurioCrystalShulkerBoxInventory
        extends CurioIronShulkerBoxInventory {

  private CurioCrystalShulkerBox crystalShulkerBox;

  public CurioCrystalShulkerBoxInventory(ItemStack shulkerBox, String identifier, int index) {

    super(ShulkerBoxType.CRYSTAL, shulkerBox, identifier, index);
    CuriosAPI.getCurio(shulkerBox).ifPresent(curio -> {

      if (curio instanceof CurioCrystalShulkerBox) {
        this.crystalShulkerBox = (CurioCrystalShulkerBox) curio;
      }
    });
  }

  @Override
  public void markDirty() {

    if (this.crystalShulkerBox != null) {
      this.crystalShulkerBox.setTopStacks(getTopStacks(this.items));
    }
  }

  /*
  * Code is derived from CrystalShulkerBoxTileEntity#sortTopStacks in the
  * com.progwml6.ironshulkerbox.common.tileentity package in the Iron Shulker Boxes mod.
  * License: GNU GPLv3
  */
  public static NonNullList<ItemStack> getTopStacks(NonNullList<ItemStack> stacks) {

    NonNullList<ItemStack> topStacks = NonNullList.withSize(8, ItemStack.EMPTY);
    NonNullList<ItemStack> copy = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
    int compressedIndex = 0;

    mainLoop:
    for (ItemStack stack : stacks) {

      if (stack.isEmpty()) {
        continue;
      }

      for (int j = 0; j < compressedIndex; j++) {
        ItemStack copyStack = copy.get(j);

        if (ItemStack.areItemsEqualIgnoreDurability(copyStack, stack)) {

          if (stack.getCount() != copyStack.getCount()) {
            copyStack.grow(stack.getCount());
          }

          continue mainLoop;
        }
      }

      copy.set(compressedIndex, stack.copy());
      compressedIndex++;
    }

    copy.sort((stack1, stack2) -> {

      if (stack1.isEmpty()) {
        return 1;
      } else if (stack2.isEmpty()) {
        return -1;
      } else {
        return stack2.getCount() - stack1.getCount();
      }
    });

    int index = 0;

    for (ItemStack stack : copy) {

      if (stack.isEmpty() || stack.getCount() <= 0) {
        continue;
      }

      if (index == topStacks.size()) {
        break;
      }

      topStacks.set(index, stack);
      index++;
    }

    for (int i = index; i < topStacks.size(); i++) {
      topStacks.set(i, ItemStack.EMPTY);
    }

    return topStacks;
  }
}
