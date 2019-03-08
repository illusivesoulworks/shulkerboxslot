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

package top.theillusivec4.curiousshulkerboxes.common.network.server;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;

import java.util.function.Supplier;

public class SPacketSyncAnimation {

    private final int entityId;
    private final String identifier;
    private final int index;
    private final boolean isClosing;

    public SPacketSyncAnimation(int entityId, String identifier, int index, boolean isClosing) {
        this.entityId = entityId;
        this.identifier = identifier;
        this.index = index;
        this.isClosing = isClosing;
    }

    public static void encode(SPacketSyncAnimation msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeString(msg.identifier);
        buf.writeInt(msg.index);
        buf.writeBoolean(msg.isClosing);
    }

    public static SPacketSyncAnimation decode(PacketBuffer buf) {
        return new SPacketSyncAnimation(buf.readInt(), buf.readString(25), buf.readInt(), buf.readBoolean());
    }

    public static void handle(SPacketSyncAnimation msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().world.getEntityByID(msg.entityId);

            if (entity instanceof EntityLivingBase) {
                CuriosAPI.getCuriosHandler((EntityLivingBase)entity).ifPresent(handler -> {
                    CurioStackHandler stackHandler = handler.getStackHandler(msg.identifier);

                    if (stackHandler != null && msg.index < stackHandler.getSlots()) {
                        ItemStack stack = stackHandler.getStackInSlot(msg.index);

                        if (BlockShulkerBox.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox) {
                            CuriosAPI.getCurio(stack).ifPresent(curio -> {

                                if (curio instanceof CurioShulkerBox) {

                                    if (msg.isClosing) {
                                        ((CurioShulkerBox) curio).setAnimationStatus(TileEntityShulkerBox.AnimationStatus.CLOSING);
                                    } else {
                                        ((CurioShulkerBox) curio).setAnimationStatus(TileEntityShulkerBox.AnimationStatus.OPENING);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
