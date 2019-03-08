/*
 * Copyright (C) 2019  C4
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

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosAPI;

public class KeyRegistry {

    public static KeyBinding openShulkerBox;

    public static void register() {
        IKeyConflictContext ctx = new IKeyConflictContext() {
            @Override
            public boolean isActive() {
                EntityPlayerSP playerSP = Minecraft.getInstance().player;
                return CuriosAPI.getCurioEquipped(stack -> BlockShulkerBox.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox,
                        playerSP) != null;
            }

            @Override
            public boolean conflicts(IKeyConflictContext other) {
                return false;
            }
        };
        openShulkerBox = new KeyBinding("key.curiousshulkerboxes.open.desc", GLFW.GLFW_KEY_X, "key.curiousshulkerboxes.category");
        openShulkerBox.setKeyConflictContext(ctx);
        ClientRegistry.registerKeyBinding(openShulkerBox);
    }
}
