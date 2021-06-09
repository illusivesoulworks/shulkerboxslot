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

package top.theillusivec4.trinketshulkerboxes.common.network;

import net.minecraft.util.Identifier;
import top.theillusivec4.trinketshulkerboxes.common.TrinketShulkerBoxesMod;

public class TSBPackets {

  public static final Identifier OPEN_SHULKER_BOX = new Identifier(TrinketShulkerBoxesMod.MOD_ID,
      "open_shulker_box");
  public static final Identifier OPENING_BOX = new Identifier(TrinketShulkerBoxesMod.MOD_ID,
      "opening_box");
  public static final Identifier CLOSING_BOX = new Identifier(TrinketShulkerBoxesMod.MOD_ID,
      "closing_box");
}
