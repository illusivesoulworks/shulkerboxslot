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

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxSlotCommonMod {

  public static boolean isShulkerBox(Item item) {
    Block block = Block.byItem(item);
    return block instanceof ShulkerBoxBlock;
  }

  public static Item[] getShulkerBoxes() {
    return new Item[] {
        Items.SHULKER_BOX,
        Items.BLACK_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX,
        Items.BROWN_SHULKER_BOX,
        Items.CYAN_SHULKER_BOX,
        Items.GRAY_SHULKER_BOX,
        Items.GREEN_SHULKER_BOX,
        Items.LIGHT_BLUE_SHULKER_BOX,
        Items.LIGHT_GRAY_SHULKER_BOX,
        Items.LIME_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX,
        Items.ORANGE_SHULKER_BOX,
        Items.PINK_SHULKER_BOX,
        Items.PURPLE_SHULKER_BOX,
        Items.RED_SHULKER_BOX,
        Items.WHITE_SHULKER_BOX,
        Items.YELLOW_SHULKER_BOX
    };
  }
}