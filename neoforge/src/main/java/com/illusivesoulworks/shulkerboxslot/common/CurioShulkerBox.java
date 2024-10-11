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

package com.illusivesoulworks.shulkerboxslot.common;

import com.illusivesoulworks.shulkerboxslot.BaseShulkerBoxAccessory;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotNeoForgeMod;
import com.illusivesoulworks.shulkerboxslot.common.integration.QuickRightClickPlugin;
import javax.annotation.Nonnull;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CurioShulkerBox extends BaseShulkerBoxAccessory implements ICurio {

  private final ItemStack stack;

  public CurioShulkerBox(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public ItemStack getStack() {
    return this.stack;
  }

  @Override
  public void curioTick(SlotContext slotContext) {
    this.tick(this.stack);
  }

  @Nonnull
  @Override
  public SoundInfo getEquipSound(SlotContext slotContext) {
    return new SoundInfo(SoundEvents.SHULKER_BOX_CLOSE, 1.0F, 1.0F);
  }

  @Override
  public boolean canEquipFromUse(SlotContext slotContext) {
    return !ShulkerBoxSlotNeoForgeMod.isQuickRightClickLoaded ||
        !QuickRightClickPlugin.checkQuickRightClick();
  }
}
