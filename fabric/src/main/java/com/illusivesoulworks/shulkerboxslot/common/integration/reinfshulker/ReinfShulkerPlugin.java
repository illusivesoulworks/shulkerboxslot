package com.illusivesoulworks.shulkerboxslot.common.integration.reinfshulker;

import atonkish.reinfcore.screen.ReinforcedStorageScreenHandler;
import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.ReinforcedShulkerBoxBlock;
import atonkish.reinfshulker.stat.ModStats;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxAccessoryInventory;
import com.illusivesoulworks.shulkerboxslot.common.TrinketShulkerBox;
import com.illusivesoulworks.shulkerboxslot.common.network.CPayloadOpenShulkerBox;
import com.illusivesoulworks.shulkerboxslot.platform.Services;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

public class ReinfShulkerPlugin {

  static final ConcurrentLinkedQueue<Item> REINF_SHULKERS = new ConcurrentLinkedQueue<>();

  public static void onInitialize() {

    for (Item item : BuiltInRegistries.ITEM) {

      if (item instanceof BlockItem blockItem &&
          blockItem.getBlock() instanceof ReinforcedShulkerBoxBlock) {
        REINF_SHULKERS.add(blockItem);
        TrinketsApi.registerTrinket(blockItem, new TrinketShulkerBox());
      }
    }
    RegistryEntryAddedCallback.event(BuiltInRegistries.ITEM).register((rawId, id, object) -> {

      if (object instanceof BlockItem blockItem &&
          blockItem.getBlock() instanceof ReinforcedShulkerBoxBlock) {
        REINF_SHULKERS.add(blockItem);
        TrinketsApi.registerTrinket(blockItem, new TrinketShulkerBox());
      }
    });
  }

  public static void handleOpenPacket(ServerPlayer player) {
    Triple<ItemStack, String, Integer> accessory =
        Services.INSTANCE.findShulkerBoxAccessory(player);
    ItemStack stack = accessory.getLeft();

    if (!stack.isEmpty()) {
      String identifier = accessory.getMiddle();
      int index = accessory.getRight();
      int size;

      if (stack.getItem() instanceof BlockItem blockItem &&
          blockItem.getBlock() instanceof ReinforcedShulkerBoxBlock block) {
        ReinforcingMaterial material = block.getMaterial();
        size = material.getSize();
        player.awardStat(ModStats.OPEN_REINFORCED_SHULKER_BOX_MAP.get(material));
        MenuProvider container =
            new ReinfShulkerBoxAccessoryInventory(stack, identifier, index, size);
        player.openMenu(container);
      } else {
        CPayloadOpenShulkerBox.handle(player);
      }
    }
  }

  public static class ReinfShulkerBoxAccessoryInventory extends ShulkerBoxAccessoryInventory {

    private ReinforcingMaterial material = null;

    public ReinfShulkerBoxAccessoryInventory(ItemStack shulkerBox, String identifier, int index,
                                             int size) {
      super(shulkerBox, identifier, index, size);

      if (shulkerBox.getItem() instanceof BlockItem blockItem &&
          blockItem.getBlock() instanceof ReinforcedShulkerBoxBlock reinforcedShulkerBoxBlock) {
        this.material = reinforcedShulkerBoxBlock.getMaterial();
      }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory,
                                            @Nonnull Player playerEntity) {
      return ReinforcedStorageScreenHandler.createShulkerBoxScreen(this.material, i,
          playerInventory, this);
    }
  }
}
