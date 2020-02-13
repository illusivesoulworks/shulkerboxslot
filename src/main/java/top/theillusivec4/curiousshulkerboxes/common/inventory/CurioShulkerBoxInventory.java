/*
 * Copyright (C) 2019  C4
 *
 * This file is part of Curious Shulker Boxes, a mod made for Minecraft.
 *
 * Curious Shulker Boxes is free software: you can redistribute it and/or
 * modify it
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
 * License along with Curious Shulker Boxes.  If not, see <https://www.gnu
 * .org/licenses/>.
 */

package top.theillusivec4.curiousshulkerboxes.common.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkHandler;
import top.theillusivec4.curiousshulkerboxes.common.network.server.SPacketSyncAnimation;

public class CurioShulkerBoxInventory implements IInventory, INamedContainerProvider {

  protected NonNullList<ItemStack> items;

  private ItemStack shulkerBox;
  private String identifier;
  private int index;

  public CurioShulkerBoxInventory(ItemStack shulkerBox, String identifier, int index) {
    this.shulkerBox = shulkerBox;
    this.identifier = identifier;
    this.index = index;
    this.items = NonNullList.withSize(27, ItemStack.EMPTY);
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
  public void openInventory(@Nonnull PlayerEntity player) {

    if (!player.isSpectator()) {
      CompoundNBT tag = shulkerBox.getChildTag("BlockEntityTag");

      if (tag != null) {

        if (tag.contains("LootTable", 8)) {
          String lootTable = tag.getString("LootTable");
          long lootSeed = tag.getLong("LootTableSeed");
          this.fillWithLoot(new ResourceLocation(lootTable), lootSeed, player);
        } else {
          this.loadFromNbt(tag);
        }
      }
      CuriosAPI.getCurio(shulkerBox).ifPresent(curio -> {

        if (curio instanceof CurioShulkerBox) {
          ((CurioShulkerBox) curio)
              .setAnimationStatus(ShulkerBoxTileEntity.AnimationStatus.OPENING);
        }
      });

      if (player instanceof ServerPlayerEntity) {
        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
            new SPacketSyncAnimation(player.getEntityId(), this.identifier, this.index, false));
      }
      player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_SHULKER_BOX_OPEN,
          SoundCategory.BLOCKS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }
  }

  @Override
  public void closeInventory(@Nonnull PlayerEntity player) {

    if (!player.isSpectator()) {
      CompoundNBT nbttagcompound = shulkerBox.getChildTag("BlockEntityTag");

      if (nbttagcompound != null) {
        nbttagcompound.remove("LootTable");
        nbttagcompound.remove("LootTableSeed");
        this.saveToNbt(nbttagcompound);
      }
      CuriosAPI.getCurio(shulkerBox).ifPresent(curio -> {

        if (curio instanceof CurioShulkerBox) {
          ((CurioShulkerBox) curio)
              .setAnimationStatus(ShulkerBoxTileEntity.AnimationStatus.CLOSING);
        }
      });

      if (player instanceof ServerPlayerEntity) {
        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
            new SPacketSyncAnimation(player.getEntityId(), this.identifier, this.index, true));
      }
      player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_SHULKER_BOX_CLOSE,
          SoundCategory.BLOCKS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }
  }

  public void loadFromNbt(CompoundNBT compound) {
    this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

    if (compound.contains("Items", 9)) {
      ItemStackHelper.loadAllItems(compound, this.items);
    }
  }

  public CompoundNBT saveToNbt(CompoundNBT compound) {
    ItemStackHelper.saveAllItems(compound, this.items, true);
    return compound;
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
  public void markDirty() {

  }

  @Override
  public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
    return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
  }

  @Override
  public void clear() {
    this.items.clear();
  }

  @Override
  public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
    return true;
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName() {
    return shulkerBox.getDisplayName();
  }

  public void fillWithLoot(ResourceLocation lootTable, long lootTableSeed, PlayerEntity player) {

    if (player.world.getServer() != null) {
      LootTable loottable = player.world.getServer().getLootTableManager()
          .getLootTableFromLocation(lootTable);
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(
          (ServerWorld) player.world))
          .withParameter(LootParameters.POSITION, new BlockPos(player.getPosition()))
          .withSeed(lootTableSeed);
      lootcontext$builder.withLuck(player.getLuck())
          .withParameter(LootParameters.THIS_ENTITY, player);
      loottable.fillInventory(this, lootcontext$builder.build(LootParameterSets.CHEST));
    }
  }

  @Nullable
  @Override
  public Container createMenu(int i, @Nonnull PlayerInventory playerInventory,
      @Nonnull PlayerEntity playerEntity) {
    return new ShulkerBoxContainer(i, playerInventory, this);
  }
}
