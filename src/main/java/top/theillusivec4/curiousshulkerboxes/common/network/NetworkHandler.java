package top.theillusivec4.curiousshulkerboxes.common.network;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBoxInventory;

public class NetworkHandler {

  public static void register() {
    ServerSidePacketRegistry.INSTANCE.register(NetworkPackets.OPEN_SHULKER_BOX,
        (((packetContext, packetByteBuf) -> packetContext.getTaskQueue().execute(() -> {
          PlayerEntity playerEntity = packetContext.getPlayer();

          if (playerEntity != null) {
            playerEntity.incrementStat(Stats.OPEN_SHULKER_BOX);
            CuriosApi.getCuriosHelper().findEquippedCurio((itemStack) -> Block
                .getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock, playerEntity)
                .ifPresent(found -> {
                  ItemStack stack = found.getRight();
                  String id = found.getLeft();
                  int index = found.getMiddle();
                  playerEntity.openHandledScreen(new CurioShulkerBoxInventory(stack, id, index));
                });
          }
        }))));
  }
}
