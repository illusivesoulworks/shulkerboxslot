package top.theillusivec4.curiousshulkerboxes.common.integration.enderite;

import net.enderitemc.enderitemod.shulker.EnderiteShulkerBoxScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBoxInventory;

public class EnderiteCurioShulkerBoxInventory extends CurioShulkerBoxInventory {

  public EnderiteCurioShulkerBoxInventory(ItemStack stack, String id, int index) {
    super(stack, id, index);
    this.items = DefaultedList.ofSize(45, ItemStack.EMPTY);
  }

  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
    return new EnderiteShulkerBoxScreenHandler(syncId, inv, this);
  }
}
