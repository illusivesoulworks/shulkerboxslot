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

package top.theillusivec4.curiousshulkerboxes.common;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkPackets;

public class CurioShulkerBoxInventory implements Inventory, NamedScreenHandlerFactory {

  protected DefaultedList<ItemStack> items;

  private ItemStack stack;
  private String id;
  private int index;

  public CurioShulkerBoxInventory(ItemStack stack, String id, int index) {
    this.stack = stack;
    this.id = id;
    this.index = index;
    this.items = DefaultedList.ofSize(27, ItemStack.EMPTY);
  }

  @Override
  public void onOpen(PlayerEntity player) {

    if (!player.isSpectator()) {
      CompoundTag tag = this.stack.getSubTag("BlockEntityTag");

      if (tag != null) {

        if (tag.contains("LootTable", 8)) {
          Identifier lootTable = new Identifier(tag.getString("LootTable"));
          long lootSeed = tag.getLong("LootTableSeed");
          this.checkLootInteraction(player, lootTable, lootSeed);
        } else {
          this.readFromTag(tag);
        }
      }

      if (!player.world.isClient()) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getEntityId());
        buf.writeString(id);
        buf.writeInt(index);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NetworkPackets.OPENING_BOX, buf);
        PlayerStream.watching(player).forEach(player1 -> ServerSidePacketRegistry.INSTANCE
            .sendToPlayer(player1, NetworkPackets.OPENING_BOX, buf));
      }
      player.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SHULKER_BOX_OPEN,
          SoundCategory.BLOCKS, 0.5F, player.world.random.nextFloat() * 0.1F + 0.9F);
    }
  }

  public void checkLootInteraction(PlayerEntity player, Identifier lootTableId, long lootSeed) {

    if (lootTableId != null && player.world.getServer() != null) {
      LootTable lootTable = player.world.getServer().getLootManager().getTable(lootTableId);

      if (player instanceof ServerPlayerEntity) {
        Criteria.PLAYER_GENERATES_CONTAINER_LOOT.test((ServerPlayerEntity) player, lootTableId);
      }
      LootContext.Builder builder = (new LootContext.Builder((ServerWorld) player.world))
          .parameter(LootContextParameters.POSITION, player.getBlockPos()).random(lootSeed);
      builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
      lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
    }
  }

  public void writeToTag(CompoundTag tag) {
    Inventories.toTag(tag, this.items, true);
  }

  public void readFromTag(CompoundTag tag) {
    this.items = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);

    if (tag.contains("Items", 9)) {
      Inventories.fromTag(tag, this.items);
    }
  }

  @Override
  public void onClose(PlayerEntity player) {

    if (!player.isSpectator()) {
      CompoundTag tag = this.stack.getSubTag("BlockEntityTag");

      if (tag != null) {
        tag.remove("LootTable");
        tag.remove("LootTableSeed");
        this.writeToTag(tag);
      }

      if (!player.world.isClient()) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getEntityId());
        buf.writeString(id);
        buf.writeInt(index);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NetworkPackets.CLOSING_BOX, buf);
        PlayerStream.watching(player).forEach(player1 -> ServerSidePacketRegistry.INSTANCE
            .sendToPlayer(player1, NetworkPackets.CLOSING_BOX, buf));
      }
      player.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SHULKER_BOX_CLOSE,
          SoundCategory.BLOCKS, 0.5F, player.world.random.nextFloat() * 0.1F + 0.9F);
    }
  }

  @Override
  public boolean isValid(int slot, ItemStack stack) {
    return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
  }

  @Override
  public int size() {
    return this.items.size();
  }

  @Override
  public boolean isEmpty() {

    for (ItemStack itemstack : this.items) {

      if (!itemstack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getStack(int slot) {
    return this.items.get(slot);
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    return Inventories.splitStack(this.items, slot, amount);
  }

  @Override
  public ItemStack removeStack(int slot) {
    return Inventories.removeStack(this.items, slot);
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    this.items.set(slot, stack == null ? ItemStack.EMPTY : stack);

    if (stack != null && stack.getCount() > this.getMaxCountPerStack()) {
      stack.setCount(this.getMaxCountPerStack());
    }
  }

  @Override
  public void markDirty() {

  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return true;
  }

  @Override
  public Text getDisplayName() {
    return this.stack.getName();
  }

  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
    return new ShulkerBoxScreenHandler(syncId, inv, this);
  }

  @Override
  public void clear() {
    this.items.clear();
  }
}
