package top.theillusivec4.curiousshulkerboxes.client;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBox;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkPackets;

public class ClientNetworkHandler {

  public static void register() {
    ClientSidePacketRegistry.INSTANCE
        .register(NetworkPackets.OPENING_BOX, (((packetContext, packetByteBuf) -> {
          int entityId = packetByteBuf.readInt();
          String id = packetByteBuf.readString();
          int index = packetByteBuf.readInt();

          packetContext.getTaskQueue().execute(() -> {
            ClientWorld clientWorld = MinecraftClient.getInstance().world;

            if (clientWorld != null) {
              Entity entity = clientWorld.getEntityById(entityId);

              if (entity instanceof LivingEntity) {
                CuriosApi.getCuriosHelper().getCuriosHandler((LivingEntity) entity)
                    .flatMap(handler -> handler.getStacksHandler(id)).ifPresent(stacksHandler -> {
                  ItemStack stack = stacksHandler.getStacks().getStack(index);
                  CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> {

                    if (curio instanceof CurioShulkerBox) {
                      ((CurioShulkerBox) curio).setAnimationStage(AnimationStage.OPENING);
                    }
                  });
                });
              }
            }
          });
        })));

    ClientSidePacketRegistry.INSTANCE
        .register(NetworkPackets.CLOSING_BOX, (((packetContext, packetByteBuf) -> {
          int entityId = packetByteBuf.readInt();
          String id = packetByteBuf.readString();
          int index = packetByteBuf.readInt();

          packetContext.getTaskQueue().execute(() -> {
            ClientWorld clientWorld = MinecraftClient.getInstance().world;

            if (clientWorld != null) {
              Entity entity = clientWorld.getEntityById(entityId);

              if (entity instanceof LivingEntity) {
                CuriosApi.getCuriosHelper().getCuriosHandler((LivingEntity) entity)
                    .flatMap(handler -> handler.getStacksHandler(id)).ifPresent(stacksHandler -> {
                  ItemStack stack = stacksHandler.getStacks().getStack(index);
                  CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> {

                    if (curio instanceof CurioShulkerBox) {
                      ((CurioShulkerBox) curio).setAnimationStage(AnimationStage.CLOSING);
                    }
                  });
                });
              }
            }
          });
        })));
  }
}
