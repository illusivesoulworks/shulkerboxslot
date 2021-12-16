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

package top.theillusivec4.curiousshulkerboxes.common.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkHandler;
import top.theillusivec4.curiousshulkerboxes.common.network.server.SPacketSyncAnimation;

public class CurioShulkerBoxInventory implements Container, MenuProvider {

  protected NonNullList<ItemStack> items;

  private final ItemStack shulkerBox;
  private final String identifier;
  private final int index;

  public CurioShulkerBoxInventory(ItemStack shulkerBox, String identifier, int index) {
    this.shulkerBox = shulkerBox;
    this.identifier = identifier;
    this.index = index;
    this.items = NonNullList.withSize(27, ItemStack.EMPTY);
  }

  @Override
  public int getContainerSize() {
    return this.items.size();
  }

  @Override
  public int getMaxStackSize() {
    return 64;
  }

  @Override
  public void startOpen(@Nonnull Player player) {

    if (!player.isSpectator()) {
      CompoundTag tag = shulkerBox.getTagElement("BlockEntityTag");

      if (tag != null) {

        if (tag.contains("LootTable", 8)) {
          String lootTable = tag.getString("LootTable");
          long lootSeed = tag.getLong("LootTableSeed");
          this.fillWithLoot(new ResourceLocation(lootTable), lootSeed, player);
        } else {
          this.loadFromNbt(tag);
        }
      }
      CuriosApi.getCuriosHelper().getCurio(shulkerBox).ifPresent(curio -> {

        if (curio instanceof CurioShulkerBox) {
          ((CurioShulkerBox) curio)
              .setAnimationStatus(ShulkerBoxBlockEntity.AnimationStatus.OPENING);
        }
      });

      if (player instanceof ServerPlayer) {
        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
            new SPacketSyncAnimation(player.getId(), this.identifier, this.index, false));
      }
      player.level.playSound(null, new BlockPos(player.position()),
          SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F,
          player.level.random.nextFloat() * 0.1F + 0.9F);
    }
  }

  @Override
  public void stopOpen(@Nonnull Player player) {

    if (!player.isSpectator()) {
      CompoundTag nbttagcompound = shulkerBox.getTagElement("BlockEntityTag");

      if (nbttagcompound != null) {
        nbttagcompound.remove("LootTable");
        nbttagcompound.remove("LootTableSeed");
        this.saveToNbt(nbttagcompound);
      }
      CuriosApi.getCuriosHelper().getCurio(shulkerBox).ifPresent(curio -> {

        if (curio instanceof CurioShulkerBox) {
          ((CurioShulkerBox) curio)
              .setAnimationStatus(ShulkerBoxBlockEntity.AnimationStatus.CLOSING);
        }
      });

      if (player instanceof ServerPlayer) {
        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
            new SPacketSyncAnimation(player.getId(), this.identifier, this.index, true));
      }
      player.level.playSound(null, new BlockPos(player.position()),
          SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F,
          player.level.random.nextFloat() * 0.1F + 0.9F);
    }
  }

  public void loadFromNbt(CompoundTag compound) {
    this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

    if (compound.contains("Items", 9)) {
      ContainerHelper.loadAllItems(compound, this.items);
    }
  }

  public void saveToNbt(CompoundTag compound) {
    ContainerHelper.saveAllItems(compound, this.items, true);
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

  public void fillWithLoot(ResourceLocation lootTable, long lootTableSeed, Player player) {

    if (player.level.getServer() != null) {
      LootTable loottable = player.level.getServer().getLootTables()
          .get(lootTable);
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(
          (ServerLevel) player.level)).withParameter(LootContextParams.ORIGIN,
          Vec3.atCenterOf(player.blockPosition())).withOptionalRandomSeed(lootTableSeed);
      lootcontext$builder.withLuck(player.getLuck())
          .withParameter(LootContextParams.THIS_ENTITY, player);
      loottable.fill(this, lootcontext$builder.create(LootContextParamSets.CHEST));
    }
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory,
                                          @Nonnull Player playerEntity) {
    return new ShulkerBoxMenu(i, playerInventory, this);
  }
}
