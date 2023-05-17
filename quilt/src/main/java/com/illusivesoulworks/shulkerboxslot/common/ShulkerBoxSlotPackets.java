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

package com.illusivesoulworks.shulkerboxslot.common;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotConstants;
import net.minecraft.resources.ResourceLocation;

public class ShulkerBoxSlotPackets {

  public static final ResourceLocation OPEN_SHULKER_BOX =
      new ResourceLocation(ShulkerBoxSlotConstants.MOD_ID, "open_shulker_box");
  public static final ResourceLocation SYNC_SHULKER_BOX =
      new ResourceLocation(ShulkerBoxSlotConstants.MOD_ID, "sync_shulker_box");
}
