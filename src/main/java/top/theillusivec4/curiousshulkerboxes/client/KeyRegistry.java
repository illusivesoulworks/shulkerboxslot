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

package top.theillusivec4.curiousshulkerboxes.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyRegistry {

  public static final String CONFIG_OPEN_DESC = "key.curiousshulkerboxes.open.desc";
  public static final String CONFIG_CATEGORY = "key.curiousshulkerboxes.category";

  public static KeyBinding openShulkerBox;

  public static void registerKeys() {
    openShulkerBox = registerKeybinding(
        new KeyBinding(CONFIG_OPEN_DESC, GLFW.GLFW_KEY_X, CONFIG_CATEGORY));
  }

  private static KeyBinding registerKeybinding(KeyBinding key) {
    KeyBindingHelper.registerKeyBinding(key);
    return key;
  }
}
