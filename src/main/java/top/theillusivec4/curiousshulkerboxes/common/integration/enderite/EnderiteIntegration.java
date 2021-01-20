package top.theillusivec4.curiousshulkerboxes.common.integration.enderite;

import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.enderitemc.enderitemod.shulker.EnderiteShulkerBoxBlock;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.curios.api.CuriosComponent;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBox;

public class EnderiteIntegration {

  public static void setup() {
    Identifier identifier = new Identifier("enderitemod:enderite_shulker_box");
    Item enderite = Registry.ITEM.get(identifier);

    if (enderite == Items.AIR) {
      RegistryEntryAddedCallback.event(Registry.ITEM).register((rawId, id, item) -> {

        if (id.equals(identifier)) {
          registerComponent(item);
        }
      });
    } else {
      registerComponent(enderite);
    }
  }

  private static void registerComponent(Item item) {
    ItemComponentCallbackV2.event(item).register(((item1, stack, components) -> components
        .put(CuriosComponent.ITEM, new CurioShulkerBox(stack))));
  }

  public static boolean isEnderiteShulkerBox(Block block) {
    return block instanceof EnderiteShulkerBoxBlock;
  }

  public static void openHandledScreen(PlayerEntity player, ItemStack stack, String id, int index) {
    player.openHandledScreen(new EnderiteCurioShulkerBoxInventory(stack, id, index));
  }
}
