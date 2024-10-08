package com.illusivesoulworks.shulkerboxslot.common.integration;

import com.illusivesoulworks.elytraslot.platform.Services;
import net.minecraft.world.entity.LivingEntity;

public class ElytraSlotPlugin {

  public static boolean isElytraEquipped(LivingEntity livingEntity) {
    return Services.ELYTRA.isEquipped(livingEntity);
  }
}
