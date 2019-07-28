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

import com.progwml6.ironshulkerbox.common.blocks.CrystalShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.blocks.ShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.blocks.ShulkerBoxType;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox.inventory.CurioCrystalShulkerBoxInventory;
import top.theillusivec4.curiousshulkerboxes.common.integration.ironshulkerbox.inventory.CurioIronShulkerBoxInventory;

public class IronShulkerBoxIntegration {

  public static boolean isIronShulkerBox(Block block) {

    return block instanceof ShulkerBoxBlock;
  }

  public static boolean isCrystalShulkerBox(Block block) {

    return block instanceof CrystalShulkerBoxBlock;
  }

  public static INamedContainerProvider createContainer(ItemStack stack,
                                                        String identifier,
                                                        int index) {

    ShulkerBoxType type = ShulkerBoxBlock.getTypeFromItem(stack.getItem());
    INamedContainerProvider container;

    if (type == ShulkerBoxType.CRYSTAL) {
      container = new CurioCrystalShulkerBoxInventory(stack, identifier, index);
    } else {
      container =
              new CurioIronShulkerBoxInventory(type, stack, identifier, index);
    }

    return container;
  }
}
