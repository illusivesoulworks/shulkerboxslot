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

package top.theillusivec4.curiousshulkerboxes.common.inventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.network.NetworkDirection;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkHandler;
import top.theillusivec4.curiousshulkerboxes.common.network.server.SPacketSyncAnimation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class CurioShulkerBoxHandler implements IInventory, IInteractionObject {

    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private ItemStack shulkerBox;
    private String identifier;
    private int index;
    protected ITextComponent customName;

    public CurioShulkerBoxHandler(ItemStack shulkerBox, String identifier, int index) {
        this.shulkerBox = shulkerBox;
        this.identifier = identifier;
        this.index = index;
    }

    @Override
    public int getSizeInventory() {
        return this.items.size();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {

        if (!player.isSpectator()) {
            NBTTagCompound nbttagcompound = shulkerBox.getOrCreateChildTag("BlockEntityTag");
            this.loadFromNbt(nbttagcompound);
            CuriosAPI.getCurio(shulkerBox).ifPresent(curio -> {

                if (curio instanceof CurioShulkerBox) {
                    ((CurioShulkerBox) curio).setAnimationStatus(TileEntityShulkerBox.AnimationStatus.OPENING);
                }
            });

            if (player instanceof EntityPlayerMP) {
                Set<? extends EntityPlayer> tracking = ((WorldServer)player.world).getEntityTracker().getTrackingPlayers(player);

                for (EntityPlayer player1 : tracking) {
                    NetworkHandler.INSTANCE.sendTo(new SPacketSyncAnimation(player.getEntityId(), this.identifier, this.index, false),
                            ((EntityPlayerMP)player1).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                }
                NetworkHandler.INSTANCE.sendTo(new SPacketSyncAnimation(player.getEntityId(), this.identifier, this.index, false),
                        ((EntityPlayerMP)player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
            player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS,
                    0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {

        if (!player.isSpectator()) {
            this.saveToNbt(shulkerBox.getOrCreateChildTag("BlockEntityTag"));
            CuriosAPI.getCurio(shulkerBox).ifPresent(curio -> {

                if (curio instanceof CurioShulkerBox) {
                    ((CurioShulkerBox) curio).setAnimationStatus(TileEntityShulkerBox.AnimationStatus.CLOSING);
                }
            });

            if (player instanceof EntityPlayerMP) {
                Set<? extends EntityPlayer> tracking = ((WorldServer)player.world).getEntityTracker().getTrackingPlayers(player);

                for (EntityPlayer player1 : tracking) {
                    NetworkHandler.INSTANCE.sendTo(new SPacketSyncAnimation(player.getEntityId(), this.identifier, this.index, true),
                            ((EntityPlayerMP)player1).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                }
                NetworkHandler.INSTANCE.sendTo(new SPacketSyncAnimation(player.getEntityId(), this.identifier, this.index, true),
                        ((EntityPlayerMP)player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            }
            player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS,
                    0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    public void loadFromNbt(NBTTagCompound compound) {
        this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (compound.contains("Items", 9)) {
            ItemStackHelper.loadAllItems(compound, this.items);
        }

        if (compound.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    public NBTTagCompound saveToNbt(NBTTagCompound compound) {
        ItemStackHelper.saveAllItems(compound, this.items, true);
        return compound;
    }

    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return new ContainerShulkerBox(playerInventory, this, playerIn);
    }

    @Nonnull
    @Override
    public String getGuiID() {
        return "minecraft:shulker_box";
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        ITextComponent itextcomponent = this.getCustomName();
        return itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.shulkerBox");
    }

    @Override
    public boolean isEmpty() {

        for(ItemStack itemstack : this.items) {

            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return this.items.get(index);
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.items, index, count);
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.items, index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        this.items.set(index, stack == null ? ItemStack.EMPTY : stack);

        if (stack != null && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return true;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    public boolean hasCustomName() {
        return this.customName != null;
    }

    public void setCustomName(@Nullable ITextComponent name) {
        this.customName = name;
    }

    @Nullable
    public ITextComponent getCustomName() {
        return this.customName;
    }
}
