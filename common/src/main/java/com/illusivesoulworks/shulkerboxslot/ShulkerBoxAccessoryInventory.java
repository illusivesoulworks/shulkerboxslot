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

package com.illusivesoulworks.shulkerboxslot;

import com.illusivesoulworks.shulkerboxslot.common.network.SPayloadSyncAnimation;
import com.illusivesoulworks.shulkerboxslot.platform.Services;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class ShulkerBoxAccessoryInventory implements Container, MenuProvider {

  protected NonNullList<ItemStack> items;

  private final ItemStack shulkerBox;
  private final String identifier;
  private final int index;

  public ShulkerBoxAccessoryInventory(ItemStack shulkerBox, String identifier, int index,
                                      int size) {
    this.shulkerBox = shulkerBox;
    this.identifier = identifier;
    this.index = index;
    this.items = NonNullList.withSize(size, ItemStack.EMPTY);
  }

  public ShulkerBoxAccessoryInventory(ItemStack shulkerBox, String identifier, int index) {
    this(shulkerBox, identifier, index, 27);
  }

  @Override
  public int getContainerSize() {
    return this.items.size();
  }

  @Override
  public void startOpen(@Nonnull Player player) {

    if (!player.isSpectator()) {

      if (this.shulkerBox.has(DataComponents.CONTAINER_LOOT)) {
        SeededContainerLoot loot = this.shulkerBox.get(DataComponents.CONTAINER_LOOT);

        if (loot != null) {
          this.fillWithLoot(loot.lootTable(), loot.seed(), player);
        }
      } else {
        ItemContainerContents contents =
            this.shulkerBox.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.items);
      }
      this.shulkerBox.update(Services.INSTANCE.getAnimationComponent(), new AnimProgressComponent(),
          animation -> {
            animation.setStatus(ShulkerBoxBlockEntity.AnimationStatus.OPENING);
            return animation;
          });

      if (player instanceof ServerPlayer serverPlayer) {
        Services.INSTANCE.sendSyncPacket(
            new SPayloadSyncAnimation(player.getId(), this.identifier, this.index, false),
            serverPlayer);
      }
      player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_BOX_OPEN,
          SoundSource.BLOCKS, 0.5F, player.level().random.nextFloat() * 0.1F + 0.9F);
    }
  }

  @Override
  public void stopOpen(@Nonnull Player player) {

    if (!player.isSpectator()) {
      this.shulkerBox.remove(DataComponents.CONTAINER_LOOT);
      this.shulkerBox.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
      this.shulkerBox.update(Services.INSTANCE.getAnimationComponent(), new AnimProgressComponent(),
          animation -> {
            animation.setStatus(ShulkerBoxBlockEntity.AnimationStatus.CLOSING);
            return animation;
          });

      if (player instanceof ServerPlayer serverPlayer) {
        Services.INSTANCE.sendSyncPacket(
            new SPayloadSyncAnimation(player.getId(), this.identifier, this.index, true),
            serverPlayer);
      }
      player.level().playSound(null, player.blockPosition(), SoundEvents.SHULKER_BOX_CLOSE,
          SoundSource.BLOCKS, 0.5F, player.level().random.nextFloat() * 0.1F + 0.9F);
    }
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
  public ItemStack getItem(int index) {
    return this.items.get(index);
  }

  @Nonnull
  @Override
  public ItemStack removeItem(int index, int count) {
    return ContainerHelper.removeItem(this.items, index, count);
  }

  @Nonnull
  @Override
  public ItemStack removeItemNoUpdate(int index) {
    return ContainerHelper.takeItem(this.items, index);
  }

  @Override
  public void setItem(int index, @Nullable ItemStack stack) {
    this.items.set(index, stack == null ? ItemStack.EMPTY : stack);

    if (stack != null && stack.getCount() > this.getMaxStackSize()) {
      stack.setCount(this.getMaxStackSize());
    }
  }

  @Override
  public void setChanged() {

  }

  @Override
  public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
    return !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
  }

  @Override
  public void clearContent() {
    this.items.clear();
  }

  @Override
  public boolean stillValid(@Nonnull Player player) {
    return true;
  }

  @Nonnull
  @Override
  public Component getDisplayName() {
    return shulkerBox.getHoverName();
  }

  public void fillWithLoot(ResourceKey<LootTable> resourceKey, long lootTableSeed,
                           @Nonnull Player player) {
    Level level = player.level();
    BlockPos blockpos = player.blockPosition();

    if (resourceKey != null && level.getServer() != null) {
      LootTable loottable = level.getServer().reloadableRegistries().getLootTable(resourceKey);

      if (player instanceof ServerPlayer serverPlayer) {
        CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, resourceKey);
      }
      LootParams.Builder lootparams$builder = new LootParams.Builder((ServerLevel) level)
          .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos));
      lootparams$builder.withLuck(player.getLuck())
          .withParameter(LootContextParams.THIS_ENTITY, player);
      loottable.fill(this, lootparams$builder.create(LootContextParamSets.CHEST), lootTableSeed);
    }
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory,
                                          @Nonnull Player playerEntity) {
    return new ShulkerBoxMenu(i, playerInventory, this);
  }
}
