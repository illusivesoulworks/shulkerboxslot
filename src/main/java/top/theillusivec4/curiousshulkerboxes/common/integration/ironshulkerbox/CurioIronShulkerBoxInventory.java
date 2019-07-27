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

package top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox;

import com.progwml6.ironshulkerbox.common.blocks.ShulkerBoxType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.curiousshulkerboxes.common.inventory.CurioShulkerBoxInventory;

public class CurioIronShulkerBoxInventory extends CurioShulkerBoxInventory {

  private ShulkerBoxType shulkerBoxType;

  public CurioIronShulkerBoxInventory(ShulkerBoxType type, ItemStack shulkerBox,
                                      String identifier, int index) {

    super(shulkerBox, identifier, index);
    this.shulkerBoxType = type;
    this.items = NonNullList.withSize(type.size, ItemStack.EMPTY);
  }
}
