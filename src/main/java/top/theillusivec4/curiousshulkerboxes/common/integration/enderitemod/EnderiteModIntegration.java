package top.theillusivec4.curiousshulkerboxes.common.integration.enderitemod;

import net.enderitemc.enderitemod.block.EnderiteShulkerBox;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class EnderiteModIntegration {

  public static boolean isEnderiteShulkerBox(Block block) {
    return block instanceof EnderiteShulkerBox;
  }

  public static ICurio getCurio(ItemStack stack) {
    return new CurioEnderiteShulkerBox(stack);
  }
}
