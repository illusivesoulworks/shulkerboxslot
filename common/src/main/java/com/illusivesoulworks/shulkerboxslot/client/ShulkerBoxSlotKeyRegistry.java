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

package com.illusivesoulworks.shulkerboxslot.client;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotConstants;
import com.illusivesoulworks.shulkerboxslot.platform.ClientServices;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ShulkerBoxSlotKeyRegistry {

  public static final String CONFIG_OPEN_DESC = "key." + ShulkerBoxSlotConstants.MOD_ID + ".open.desc";
  public static final String CONFIG_CATEGORY = "key." + ShulkerBoxSlotConstants.MOD_ID + ".category";

  public static KeyMapping openShulkerBox;

  public static void setup() {
    openShulkerBox =
        ClientServices.INSTANCE.createKeyMapping(InputConstants.KEY_X, CONFIG_OPEN_DESC,
            CONFIG_CATEGORY);
  }
}
