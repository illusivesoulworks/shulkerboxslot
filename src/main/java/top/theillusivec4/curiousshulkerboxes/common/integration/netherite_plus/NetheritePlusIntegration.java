package top.theillusivec4.curiousshulkerboxes.common.integration.netherite_plus;

import com.oroarmor.netherite_plus.block.NetheriteShulkerBoxBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class NetheritePlusIntegration {

  public static boolean isNetheriteShulkerBox(Block block) {
    return block instanceof NetheriteShulkerBoxBlock;
  }

  public static ICurio getCurio(ItemStack stack) {
    return new CurioNetheriteShulkerBox(stack);
  }
}
