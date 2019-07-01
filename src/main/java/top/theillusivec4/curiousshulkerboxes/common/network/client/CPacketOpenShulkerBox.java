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

package top.theillusivec4.curiousshulkerboxes.common.network.client;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.Stats;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curiousshulkerboxes.common.inventory.CurioShulkerBoxHandler;

import java.util.function.Supplier;

public class CPacketOpenShulkerBox {

    public static void encode(CPacketOpenShulkerBox msg, PacketBuffer buf) {}

    public static CPacketOpenShulkerBox decode(PacketBuffer buf) {
        return new CPacketOpenShulkerBox();
    }

    public static void handle(CPacketOpenShulkerBox msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();

            if (sender != null) {
                sender.addStat(Stats.OPEN_SHULKER_BOX);
                CuriosAPI.FinderData shulkerBox = CuriosAPI.getCurioEquipped(stack -> BlockShulkerBox.
                                getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox, sender);

                if (shulkerBox != null) {
                    sender.displayGUIChest(new CurioShulkerBoxHandler(shulkerBox.getStack(), shulkerBox.getIdentifier(), shulkerBox.getIndex()));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
