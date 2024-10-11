package com.illusivesoulworks.shulkerboxslot.common.network;

import com.illusivesoulworks.shulkerboxslot.AnimProgressComponent;
import com.illusivesoulworks.shulkerboxslot.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

public class ShulkerBoxClientPackets {

  public static void handle(int entityId, String identifier, int index, boolean isClosing) {
    ClientLevel world = Minecraft.getInstance().level;

    if (world == null) {
      return;
    }
    Entity entity = world.getEntity(entityId);

    if (!(entity instanceof LivingEntity livingEntity)) {
      return;
    }
    ItemStack stack = Services.INSTANCE.getShulkerBoxAccessory(livingEntity, identifier, index);

    if (!stack.isEmpty()) {
      stack.update(Services.INSTANCE.getAnimationComponent(), new AnimProgressComponent(),
          animation -> {
            animation.setStatus(
                isClosing ? ShulkerBoxBlockEntity.AnimationStatus.CLOSING :
                    ShulkerBoxBlockEntity.AnimationStatus.OPENING);
            return animation;
          });
    }
  }
}
