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

package top.theillusivec4.curiousshulkerboxes.common.network;

import net.minecraft.util.Identifier;
import top.theillusivec4.curiousshulkerboxes.common.CuriousShulkerBoxesCommon;

public class NetworkPackets {

  public static final Identifier OPEN_SHULKER_BOX = new Identifier(CuriousShulkerBoxesCommon.MODID,
      "open_shulker_box");
  public static final Identifier OPENING_BOX = new Identifier(CuriousShulkerBoxesCommon.MODID,
      "opening_box");
  public static final Identifier CLOSING_BOX = new Identifier(CuriousShulkerBoxesCommon.MODID,
      "closing_box");
}
