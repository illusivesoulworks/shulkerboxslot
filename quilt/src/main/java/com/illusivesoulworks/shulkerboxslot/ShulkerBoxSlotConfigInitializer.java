package com.illusivesoulworks.shulkerboxslot;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;
import org.quiltmc.loader.api.ModContainer;

public class ShulkerBoxSlotConfigInitializer implements SpectreLibInitializer {

  @Override
  public void onInitializeConfig(ModContainer modContainer) {
    ShulkerBoxSlotConfig.setup();
  }
}
