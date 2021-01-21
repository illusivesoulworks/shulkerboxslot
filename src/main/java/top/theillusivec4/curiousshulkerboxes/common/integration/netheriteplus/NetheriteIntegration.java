package top.theillusivec4.curiousshulkerboxes.common.integration.netheriteplus;

import com.oroarmor.netherite_plus.block.NetheriteShulkerBoxBlock;
import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.curios.api.CuriosComponent;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBox;

public class NetheriteIntegration {

  public static void setup() {

    for (Item item : Registry.ITEM) {

      if (item instanceof BlockItem && isNetheriteShulkerBox(((BlockItem) item).getBlock())) {
        registerComponent(item);
      }
    }
    RegistryEntryAddedCallback.event(Registry.ITEM).register((rawId, id, item) -> {

      if (item instanceof BlockItem && isNetheriteShulkerBox(((BlockItem) item).getBlock())) {
        registerComponent(item);
      }
    });
  }

  private static void registerComponent(Item item) {
    ItemComponentCallbackV2.event(item).register(((item1, stack, components) -> components
        .put(CuriosComponent.ITEM, new CurioShulkerBox(stack))));
  }

  public static boolean isNetheriteShulkerBox(Block block) {
    return block instanceof NetheriteShulkerBoxBlock;
  }
}
